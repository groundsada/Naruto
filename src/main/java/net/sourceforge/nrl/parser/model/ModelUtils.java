package net.sourceforge.nrl.parser.model;

import java.util.List;

import net.sourceforge.nrl.parser.model.xsd.IXSDUserData;
import net.sourceforge.nrl.parser.model.xsd.XSDClassifier;
import net.sourceforge.nrl.parser.model.xsd.XSDDataType;
import net.sourceforge.nrl.parser.model.xsd.XSDPackage;

/**
 * Utility methods for working with model objects.
 */
public class ModelUtils {

	/**
	 * Finds an attribute within the given {@link IClassifier} whose originalName matches
	 * that specified.
	 * 
	 * @param container The IClassifier to search for attributes.
	 * @param originalName The name of the attribute as specified in the original e.g. XSD
	 * @param includeInherited
	 * @return the attribute or null if none found
	 */
	public static IAttribute getAttributeByOriginalName(IClassifier container, String originalName,
			boolean includeInherited) {
		if (originalName == null) {
			throw new IllegalArgumentException("originalName cannot be null");
		}
		if (container == null) {
			throw new IllegalArgumentException("container cannot be null");
		}
		List<IAttribute> attributes = container.getAttributes(includeInherited);
		for (IAttribute attribute : attributes) {
			if (originalName.equals(attribute.getOriginalName())) {
				return attribute;
			}
		}
		return null;
	}

	/**
	 * Return the file location (a URL as a String) that a package was loaded from.
	 * 
	 * @param pkg the package
	 * @return the location, may be null for internal packages
	 */
	public static String getLocation(IPackage pkg) {
		return (String) pkg.getUserData(IXSDUserData.MODEL_FILE_LOCATION);
	}

	/**
	 * Return the namespace an attribute is in. This only works on attributes retrieved
	 * from XML schema, and will return null unless the attribute is in a target
	 * namespace.
	 * 
	 * @param attr the attribute
	 * @return a namespace URI or null
	 */
	public static String getXMLNamespaceURI(IAttribute attr) {
		return (String) attr.getUserData(IXSDUserData.NAMESPACE);
	}

	/**
	 * Return the namespace an model element (i.e. type) is in. This only works on
	 * attributes retrieved from XML schema, and will return null unless the element is in
	 * a target namespace.
	 * 
	 * @param element the model element
	 * @return a namespace URI or null
	 */
	public static String getXMLNamespaceURI(IModelElement element) {
		return (String) element.getUserData(IXSDUserData.NAMESPACE);
	}

	/**
	 * Return an XPath to an attribute inside a schema. This is attached if
	 * {@link #isRenamedXSDAttribute(IAttribute)} returns true and can be used by
	 * applications that require JAXB bindings.
	 * 
	 * @param attr the attribute
	 * @return the path
	 */
	public static String getXSDPath(IAttribute attr) {
		return (String) attr.getUserData(IXSDUserData.XSD_PATH);
	}

	/**
	 * Return true if an attribute represents an XML element (rather than an XML
	 * attribute). If this returns false this does <b>not</b> mean that the attribute is
	 * an XML attribute (it may be a UML attribute, for example). Use
	 * {@link #isAttributeAnXMLAttribute(IAttribute)} instead.
	 * 
	 * @param attr the attribute
	 * @return true if the attribute is an XML element
	 */
	public static boolean isAttributeAnXMLElement(IAttribute attr) {
		String kind = (String) attr.getUserData(IXSDUserData.ATTRIBUTE_KIND);
		return IXSDUserData.XSD_ELEMENT_KIND.equals(kind);
	}

	/**
	 * Return true if an attribute represents an XML attribute (as opposed to an XML
	 * element). Use {@link #isAttributeAnXMLElement(IAttribute)} to find out if it's an
	 * XML element.
	 * 
	 * @param attr the attribute
	 * @return true if the attribute is an XML attribute
	 */
	public static boolean isAttributeAnXMLAttribute(IAttribute attr) {
		String kind = (String) attr.getUserData(IXSDUserData.ATTRIBUTE_KIND);
		return IXSDUserData.XSD_ATTRIBUTE_KIND.equals(kind);
	}

	/**
	 * Returnt true if an attribute is an XML attribute or element that had to be renamed
	 * by the model loader due to a naming conflict.
	 * 
	 * @param attr the attribute
	 * @return true if the attribute was renamed
	 */
	public static boolean isRenamedXSDAttribute(IAttribute attr) {
		Boolean renamed = (Boolean) attr.getUserData(IXSDUserData.RENAMED_AMBIGUOUS_ATTRIBUTE);
		if (renamed != null) {
			return renamed;
		}
		return false;
	}

	/**
	 * Detects if an {@link IModelElement} originates from an XSD model i.e. is it an
	 * {@link XSDPackage}, {@link XSDClassifier} or {@link XSDDataType}.
	 * 
	 * @param modelElement the {@link IModelElement} to inspect
	 * @return true if the supplied modelElement originates from an XSD model, false
	 * otherwise.
	 */
	public static boolean isXSDModelElement(IModelElement modelElement) {
		if (modelElement == null) {
			throw new IllegalArgumentException("Cannot check null is part of an XSD Model.");
		}
		return modelElement instanceof XSDPackage || modelElement instanceof XSDClassifier
				|| modelElement instanceof XSDDataType;
	}

}
