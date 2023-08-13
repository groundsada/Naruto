package net.sourceforge.nrl.parser.model.xsd;

import static net.sourceforge.nrl.parser.model.ModelUtils.getXMLNamespaceURI;
import static net.sourceforge.nrl.parser.model.ModelUtils.isAttributeAnXMLAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.nrl.parser.model.AbstractClassifier;
import net.sourceforge.nrl.parser.model.IAttribute;
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.model.ModelUtils;

class XSDAmbiguousAttributeResolver {

	/**
	 * Return a sorted list of classifiers for the other methods to process. This needs to be sorted
	 * so that potential super-types appear before sub-types, an invariant required by the other
	 * methods.
	 * 
	 * @param pkg the package to search
	 * @return the classifiers
	 */
	private static List<AbstractClassifier> getSortedListOfElements(XSDPackage pkg) {
		List<AbstractClassifier> elementsToProcess = new ArrayList<AbstractClassifier>();
		for (IModelElement element : pkg.getContents(true)) {
			if (!(element instanceof AbstractClassifier)) {
				continue;
			}
			if (elementsToProcess.contains(element)) {
				continue;
			}

			IModelElement parent = element.getParent();
			while (parent != null && !parent.getName().equals("Object")) {
				if (!elementsToProcess.contains(parent)) {
					elementsToProcess.add(0, (AbstractClassifier) parent);
				}
				parent = parent.getParent();
			}

			elementsToProcess.add((AbstractClassifier) element);
		}
		return elementsToProcess;
	}

	/**
	 * Within one type, merge all duplicate attributes. This removes the duplicates and sets the
	 * upper bound to unbounded. It does not attach a JAXB content model override.
	 * 
	 * @param classifier the classifier to process
	 * @return true if any merging was necessary
	 */
	private static boolean mergeDuplicateAttributes(AbstractClassifier classifier) {
		boolean mergeWasNecessary = false;
		Map<String, IAttribute> nameToAttribute = new HashMap<String, IAttribute>();

		Iterator<IAttribute> iterator = classifier.getAttributes().iterator();
		while (iterator.hasNext()) {
			IAttribute attribute = iterator.next();
			String name = attribute.getName();

			boolean substitutable = attribute.getUserData(IXSDUserData.SUBSTITUTABLE) != null
					|| attribute.getUserData(IXSDUserData.SUBSTITUTION_FOR) != null;

			if (nameToAttribute.containsKey(name)) {
				if (substitutable) {
					int count = 2;
					while (nameToAttribute.containsKey(name + count)) {
						count++;
					}

					attribute.setName(name + count);
					attribute.setUserData(IXSDUserData.RENAMED_AMBIGUOUS_ATTRIBUTE, true);

					nameToAttribute.put(attribute.getName(), attribute);
				} else {
					iterator.remove();

					XSDAttribute originalAttribute = (XSDAttribute) nameToAttribute.get(name);
					originalAttribute.setMaxOccurs(XSDAttribute.UNBOUNDED);

					mergeWasNecessary = true;
				}
			} else {
				nameToAttribute.put(name, attribute);
			}
		}

		classifier.rebuildAttributeNameMap();

		return mergeWasNecessary;
	}

	/**
	 * Find all duplicate attribute names in XSD classifiers, recursively, and assign them unique
	 * names.
	 * <p>
	 * Also attaches schema paths to the attributes, in case binding annotations are required by
	 * applications.
	 * 
	 * @param result the package to traverse
	 */
	public static void relabelDuplicateAttributes(XSDPackage result) {
		// Create a list of classifiers to process, with any ancestor elements before descendants
		List<AbstractClassifier> elementsToProcess = getSortedListOfElements(result);

		for (AbstractClassifier classifier : elementsToProcess) {
			renameDistinctElementsWithClashingNames(classifier);

			if (mergeDuplicateAttributes(classifier)) {
				String catchAllName = "content";
				if (classifier.getParent() != null
						&& !"Object".equals(classifier.getParent().getName())) {
					catchAllName = "rest";
				}
				setJaxbCatchAllOnAttributes(classifier, catchAllName);
			}

			if (classifier.getParent() != null
					&& !"Object".equals(classifier.getParent().getName())) {
				AbstractClassifier parent = (AbstractClassifier) classifier.getParent();

				if (hasCollapsedContentModel(parent)) {
					setJaxbCatchAllOnAttributes(classifier, "content");
					resolveSuperTypeAttributeClashes(classifier);
				} else if (resolveSuperTypeAttributeClashes(classifier)) {
					setJaxbCatchAllOnAttributes(classifier, "rest");
				}
			}

			// Set<String> attributeNames = getAncestralAttributes(classifier);
		}
	}

	/**
	 * Return true if the classifier has a collapsed content model (e.g. 'content' or 'rest' for all
	 * attributes
	 * 
	 * @param classifier the classifier
	 * @return true if the content model is collapsed
	 */
	private static boolean hasCollapsedContentModel(AbstractClassifier classifier) {
		return !classifier.getAttributes().isEmpty()
				&& classifier.getAttributes().get(0).getUserData(IXSDUserData.JAXB_CATCH_ALL) != null;
	}

	private static boolean resolveSuperTypeAttributeClashes(AbstractClassifier classifier) {
		boolean catchAllNecessary = false;
		boolean attributesRenamed = false;
		Map<String, IAttribute> nameToAttribute = new HashMap<String, IAttribute>();

		AbstractClassifier parent = (AbstractClassifier) classifier.getParent();
		for (IAttribute attr : parent.getAttributes(true)) {
			nameToAttribute.put(attr.getName(), attr);
		}

		Iterator<IAttribute> iterator = classifier.getAttributes().iterator();
		while (iterator.hasNext()) {
			IAttribute attribute = iterator.next();
			String name = attribute.getName();

			if (nameToAttribute.containsKey(name)) {
				XSDAttribute originalAttribute = (XSDAttribute) nameToAttribute.get(name);

				if (!hasCollapsedContentModel(parent)) {
					int count = 2;
					while (nameToAttribute.containsKey(name + count)) {
						count++;
					}
					attribute.setName(name + count);
					attribute.setUserData(IXSDUserData.RENAMED_AMBIGUOUS_ATTRIBUTE, true);
				} else {
					originalAttribute.setMaxOccurs(IAttribute.UNBOUNDED);
					iterator.remove();
				}

				attributesRenamed = true;
				if (originalAttribute.getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY) == null
						&& originalAttribute.getUserData(IXSDUserData.JAXB_CATCH_ALL) == null) {
					catchAllNecessary = true;
				}
			}

			nameToAttribute.put(attribute.getName(), attribute);
		}

		if (attributesRenamed) {
			classifier.rebuildAttributeNameMap();
		}
		return catchAllNecessary;
	}

	private static void setJaxbCatchAllOnAttributes(AbstractClassifier classifier,
			String catchAllName) {
		for (IAttribute attr : classifier.getAttributes(false)) {
			if (ModelUtils.isAttributeAnXMLElement(attr)) {
				attr.setUserData(IXSDUserData.JAXB_CATCH_ALL, catchAllName);
				attr.setUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY, null);
			}
		}

	}

	/**
	 * Performs two disambiguation tasks relative to the <code>classifier</code> argument:
	 * <ul>
	 * <li>XML attributes with the same name as an XML element are renamed by appending "Attribtue"</li>
	 * <li>XML elements with the same name but different namespaces are disambiguated with appended
	 * integer indices</li>
	 * </ul>
	 * 
	 * @param classifier The classifier to check for ambiguous sibling elements
	 */
	private static void renameDistinctElementsWithClashingNames(AbstractClassifier classifier) {
		// Rename XML attributes that shadow XML elements
		for (IAttribute attr : classifier.getAttributes()) {
			XSDAttribute xsdAttr = (XSDAttribute) attr;
			int disambiguationIndex = 2;
			for (IAttribute otherAttr : classifier.getAttributes()) {
				if (otherAttr != attr && otherAttr.getName().equals(attr.getName())) {
					String path = SchemaPathCreator.getPath(xsdAttr);
					if (isAttributeAnXMLAttribute(xsdAttr)) {
						// Exclusion for this release: don't handle cases involving
						// model groups
						xsdAttr.setName(xsdAttr.getName() + "Attribute");
						xsdAttr.setUserData(IXSDUserData.RENAMED_AMBIGUOUS_ATTRIBUTE, true);
						classifier.rebuildAttributeNameMap();

						if (path != null && !path.contains("xs:group")
								&& !path.contains("xs:attributeGroup")) {
							xsdAttr.setUserData(IXSDUserData.XSD_PATH, path);
						}
						break;
					} else if (xsdAttr.getUserData(IXSDUserData.SUBSTITUTABLE) == null
							&& xsdAttr.getUserData(IXSDUserData.SUBSTITUTION_FOR) == null) {
						String attrNamespace = getXMLNamespaceURI(xsdAttr);
						String otherAttrNamespace = getXMLNamespaceURI(otherAttr);
						if ((attrNamespace != null && !attrNamespace.equals(otherAttrNamespace))
								|| (attrNamespace == null && otherAttrNamespace != null)) {

							// Exclusion for this release: don't handle cases involving
							// model groups
							xsdAttr.setName(xsdAttr.getName() + disambiguationIndex++);
							xsdAttr.setUserData(IXSDUserData.RENAMED_AMBIGUOUS_ATTRIBUTE, true);
							classifier.rebuildAttributeNameMap();

							if (path != null && !path.contains("xs:group")) {
								xsdAttr.setUserData(IXSDUserData.XSD_PATH, path);
							}
						}

					}
				}
			}
		}
	}

}
