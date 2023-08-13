/*
 * The contents of this file are subject to the Mozilla Public License Version 1.1 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF
 * ANY KIND, either express or implied. See the License for the specific language governing rights
 * and limitations under the License.
 * 
 * The Original Code is the NRL Parser, released 28 April 2006, Copyright (c) Christian Nentwich.
 * The Initial Developer of the Original Code is Christian Nentwich. Portions created by
 * contributors identified in the NOTICES file are Copyright (c) the contributors. All Rights
 * Reserved.
 */
package net.sourceforge.nrl.parser.model.xsd;

import static java.lang.String.format;
import static net.sourceforge.nrl.parser.model.xsd.XSDAmbiguousAttributeResolver.relabelDuplicateAttributes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.nrl.parser.model.AbstractClassifier;
import net.sourceforge.nrl.parser.model.AbstractModelElement;
import net.sourceforge.nrl.parser.model.IAttribute;
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.model.IPackage;
import net.sourceforge.nrl.parser.model.loader.IModelLoader;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupContent;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDProcessContents;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDVariety;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.util.XSDResourceImpl;

/**
 * The main XML Schema model loader class. Call {@link #load(File)} to load a file and its includes
 * and imports as an NRL model or {@link #load(Resource, File)} if you already have a handle on the
 * EMF resource containing the schema.
 * 
 * @author Christian Nentwich
 */
public class XSDModelLoader implements IXSDUserData {

	private List<String> warnings = new ArrayList<String>();

	/**
	 * Resolver map. This maps a fully qualified qname, of the form namespaceURI:elementName, to the
	 * model element representing the element.
	 */
	private Map<String, IModelElement> nameToElement = new HashMap<String, IModelElement>();

	/**
	 * Resolver map from schema namespace to the set of types declared in the namespace. "" is
	 * mapped for "no namespace".
	 */
	private Map<String, Set<String>> namespaceToTypeNames = new HashMap<String, Set<String>>();

	/**
	 * The Object supertype.
	 */
	private XSDClassifier _object;

	/**
	 * Traverse the entire particle and check whether all descendant elements have a unique type.
	 * 
	 * @param particle the particle
	 * @return true only if all have a unique type
	 */
	protected boolean areDescendantTypesUnique(XSDParticle particle) {
		List<XSDElementDeclaration> elements = getDirectDescendants(particle);

		Set<XSDTypeDefinition> types = new HashSet<XSDTypeDefinition>();

		for (XSDElementDeclaration element : elements) {
			XSDTypeDefinition type = element.getType();
			if (type == null)
				continue;

			// If this derives from a simple type, find the root type
			XSDTypeDefinition base = type;
			while (base.getBaseType() != null && !"anyType".equals(base.getBaseType().getName())
					&& !"anySimpleType".equals(base.getBaseType().getName())) {
				base = base.getBaseType();
			}

			if (base != null)
				type = base;

			if (types.contains(type))
				return false;
			types.add(type);
		}

		return true;
	}

	/**
	 * Given an XML Schema element declaration, produce an NRL {@link XSDAttribute}.
	 * 
	 * @param resultPackage the resulting package any type produce locally will be placed in
	 * @param container the {@link IModelElement} that will contain the attribute
	 * @param particle the nearest particle containing the element (the element will be retrieved
	 * from this)
	 * @param ancestorOptional whether any ancestor particle of the element is optional
	 * @param ancestorUnbounded whether any ancestor particle of the element repeats
	 * @return the new attribute
	 */
	private XSDAttribute createAttributeFromElement(XSDPackage resultPackage,
			XSDClassifier container, XSDParticle particle, boolean ancestorOptional,
			boolean ancestorUnbounded) {
		// Element declaration
		XSDElementDeclaration decl = (XSDElementDeclaration) particle.getContent();

		// References a global element
		if (decl.isElementDeclarationReference()) {
			decl = decl.getResolvedElementDeclaration();

			XSDAttribute attr = createAttributeFromElementReferencingGlobalElement(container,
					particle, ancestorOptional, ancestorUnbounded, decl);

			// Process substitution groups - include any substitutable
			// attribute
			List<?> list = decl.getSubstitutionGroup();
			for (Iterator<?> iter = list.iterator(); iter.hasNext();) {
				XSDElementDeclaration subst = (XSDElementDeclaration) iter.next();

				if (subst != decl) {
					createElementFromSubstitutionGroupElement(container, particle,
							ancestorUnbounded, attr, subst);
				}
			}

			return attr;
		} else if (decl.getType() instanceof XSDSimpleTypeDefinition) {
			// Element has a simple type
			return createAttributeFromElementWithSimpleType(container, particle, ancestorOptional,
					ancestorUnbounded, decl);
		} else if (decl.getType() instanceof XSDComplexTypeDefinition) {
			// Element has a complex type
			return createAttributeFromElementWithComplexType(resultPackage, container, particle,
					ancestorOptional, ancestorUnbounded, decl);
		} else
			throw new RuntimeException("Internal error. Illegal element content.");
	}

	/**
	 * Given an XML Schema element that references a global element declaration, return a new NRL
	 * attribute corresponding to the element.
	 * 
	 * @param container the {@link IModelElement} that will contain the attribute
	 * @param particle the nearest particle containing the element
	 * @param ancestorOptional whether any ancestor particle of the element is optional
	 * @param ancestorUnbounded whether any ancestor particle of the element repeats
	 * @param elementDeclaration the element declaration in the schema
	 * @return the new attribute
	 */
	private XSDAttribute createAttributeFromElementReferencingGlobalElement(
			XSDClassifier container, XSDParticle particle, boolean ancestorOptional,
			boolean ancestorUnbounded, XSDElementDeclaration elementDeclaration) {
		// Figure out the type name
		String typeName = XSDHelper.getCleanedName(elementDeclaration.getName());
		String typeTargetNamespace = elementDeclaration.getTargetNamespace();

		if (elementDeclaration.getType() != null && elementDeclaration.getType().getName() != null) {
			typeName = XSDHelper.getCleanedName(elementDeclaration.getType().getName());
			typeTargetNamespace = elementDeclaration.getType().getTargetNamespace();
		}

		// Check if it's derived by list (as a simple type)
		boolean isListType = elementDeclaration.getTypeDefinition() instanceof XSDSimpleTypeDefinition
				&& XSDVariety.LIST_LITERAL == ((XSDSimpleTypeDefinition) elementDeclaration
						.getTypeDefinition()).getVariety();
		XSDSimpleTypeDefinition simple = null;
		if (isListType
				&& ((XSDSimpleTypeDefinition) elementDeclaration.getTypeDefinition())
						.getItemTypeDefinition() != null) {
			simple = ((XSDSimpleTypeDefinition) elementDeclaration.getTypeDefinition())
					.getItemTypeDefinition();

			while (simple.getBaseType() != null
					&& simple.getBaseType() instanceof XSDSimpleTypeDefinition
					&& !XSDBuiltInSimpleTypes.NAMESPACE.equals(simple.getTargetNamespace())
					&& !"anySimpleType".equals(simple.getBaseType().getName()))
				simple = (XSDSimpleTypeDefinition) simple.getBaseType();
		}

		XSDAttribute attr = new XSDAttribute(elementDeclaration, ancestorOptional ? 0 : particle
				.getMinOccurs(), ancestorUnbounded || isListType ? IAttribute.UNBOUNDED : particle
				.getMaxOccurs());
		attr.setSchemaElement(elementDeclaration.getElement());

		if (simple != null)
			attr.setXSDType(simple);
		else
			attr.setTypeName(XSDHelper.getQualifiedName(typeTargetNamespace, typeName));

		if (elementDeclaration.getSubstitutionGroup().size() > 1
				|| elementDeclaration.getSubstitutionGroup().size() == 1
				&& elementDeclaration.getSubstitutionGroup().get(0) != elementDeclaration) {
			attr.setUserData(SUBSTITUTABLE, true);
		}
		container.addAttribute(attr);
		attr.setOwner(container);
		return attr;
	}

	/**
	 * Given an XML Schema element declaration with a complex type (assigned or anonymous), produce
	 * an attribute representing the element in the NRL model.
	 * 
	 * @param resultPackage the resulting package any type produce locally will be placed in
	 * @param container the {@link IModelElement} that will contain the attribute
	 * @param particle the nearest particle containing the element
	 * @param ancestorOptional whether any ancestor particle of the element is optional
	 * @param ancestorUnbounded whether any ancestor particle of the element repeats
	 * @return the new attribute
	 */
	private XSDAttribute createAttributeFromElementWithComplexType(XSDPackage resultPackage,
			XSDClassifier container, XSDParticle particle, boolean ancestorOptional,
			boolean ancestorUnbounded, XSDElementDeclaration elementDeclaration) {
		assert elementDeclaration.getType() instanceof XSDComplexTypeDefinition;
		XSDComplexTypeDefinition complex = (XSDComplexTypeDefinition) elementDeclaration.getType();

		// Complex type is an anonymous subtype
		if (complex.getName() == null) {
			// If so, introduce a new "anonymous" class named
			// after the element
			String upperName = XSDHelper.toUpperCase(elementDeclaration.getName());
			AbstractClassifier anonymousType = null;

			if (complex.getRootType() instanceof XSDSimpleTypeDefinition) {
				anonymousType = getDataType(complex, resultPackage, getSuggestedAnonymousTypeName(
						elementDeclaration.getSchema().getTargetNamespace(), upperName), false);
			} else {
				anonymousType = getComplexType(resultPackage, complex,
						getSuggestedAnonymousTypeName(elementDeclaration.getSchema()
								.getTargetNamespace(), upperName), false);
			}

			anonymousType.setOriginalName(upperName);
			anonymousType.setUserData(IXSDUserData.CONTAINING_TYPE, container);

			XSDAttribute attr = new XSDAttribute(elementDeclaration.getName());
			attr.setUserData(NAMESPACE, complex.getTargetNamespace());
			attr.setUserData(ATTRIBUTE_KIND, XSD_ELEMENT_KIND);
			attr.setMinOccurs(ancestorOptional ? 0 : particle.getMinOccurs());
			attr.setMaxOccurs(ancestorUnbounded ? IAttribute.UNBOUNDED : particle.getMaxOccurs());
			attr.setTypeName(XSDHelper.getQualifiedName(complex.getTargetNamespace(), anonymousType
					.getName()));
			attr.setSchemaElement(elementDeclaration.getElement());

			container.addAttribute(attr);
			attr.setOwner(container);
			return attr;
		} else {
			// Complex type references existing type
			XSDAttribute attr = new XSDAttribute(elementDeclaration, ancestorOptional ? 0
					: particle.getMinOccurs(), ancestorUnbounded ? IAttribute.UNBOUNDED : particle
					.getMaxOccurs());
			attr.setSchemaElement(elementDeclaration.getElement());
			attr.setUserData(NAMESPACE, elementDeclaration.getTargetNamespace());
			container.addAttribute(attr);
			attr.setOwner(container);
			return attr;
		}
	}

	/**
	 * Given an XML Schema element declaration with a simple type (assigned or anonymous), produce
	 * an attribute representing the element in the NRL model.
	 * 
	 * @param container the {@link IModelElement} that will contain the attribute
	 * @param particle the nearest particle containing the element
	 * @param ancestorOptional whether any ancestor particle of the element is optional
	 * @param ancestorUnbounded whether any ancestor particle of the element repeats
	 * @param elementDeclaration the element declaration in the schema
	 * @return the new attribute
	 */
	private XSDAttribute createAttributeFromElementWithSimpleType(XSDClassifier container,
			XSDParticle particle, boolean ancestorOptional, boolean ancestorUnbounded,
			XSDElementDeclaration elementDeclaration) {
		assert elementDeclaration.getType() instanceof XSDSimpleTypeDefinition;
		XSDSimpleTypeDefinition simple = (XSDSimpleTypeDefinition) elementDeclaration.getType();

		boolean isListType = XSDVariety.LIST_LITERAL == simple.getVariety();

		if (isListType && simple.getItemTypeDefinition() != null) {
			simple = simple.getItemTypeDefinition();
			while (simple.getBaseType() != null
					&& simple.getBaseType() instanceof XSDSimpleTypeDefinition
					&& !XSDBuiltInSimpleTypes.NAMESPACE.equals(simple.getTargetNamespace())
					&& !"anySimpleType".equals(simple.getBaseType().getName()))
				simple = (XSDSimpleTypeDefinition) simple.getBaseType();
		}

		XSDAttribute attr = null;
		if (simple.getName() != null) {

			// Explicit type
			attr = new XSDAttribute(elementDeclaration, ancestorOptional ? 0 : particle
					.getMinOccurs(), ancestorUnbounded || isListType ? IAttribute.UNBOUNDED
					: particle.getMaxOccurs());
			attr.setSchemaElement(elementDeclaration.getElement());
			attr.setXSDType(simple);
		} else {
			// Anonymous simple subtype
			while (simple.getBaseType() != null) {
				if (XSDVariety.LIST_LITERAL == simple.getVariety())
					isListType = true;

				simple = (XSDSimpleTypeDefinition) simple.getBaseType();

				if (simple.getName() != null) {
					// Certain conditions need to be present for a type
					// to be explicit; just having a restriction by
					// length is not enough, for example - then we take
					// the super-type instead
					if (!simple.getEnumerationFacets().isEmpty())
						break;
					if (simple.getTargetNamespace() != null
							&& simple.getTargetNamespace().equals(XSDBuiltInSimpleTypes.NAMESPACE))
						break;
				}
			}

			attr = new XSDAttribute(elementDeclaration.getName());
			attr.setUserData(NAMESPACE, simple.getTargetNamespace());
			attr.setUserData(ATTRIBUTE_KIND, XSD_ELEMENT_KIND);
			attr.setSchemaElement(elementDeclaration.getElement());

			attr.setMinOccurs(ancestorOptional ? 0 : particle.getMinOccurs());
			attr.setMaxOccurs(ancestorUnbounded || isListType ? IAttribute.UNBOUNDED : particle
					.getMaxOccurs());
			attr.setXSDType(simple);
		}

		container.addAttribute(attr);
		attr.setOwner(container);
		return attr;
	}

	/**
	 * Turn a particle containing an xsd:any into an attribute, if possible
	 * 
	 * @param resultPackage the result package
	 * @param container the containing classifier
	 * @param particle the nearest particle containing the element
	 * @param ancestorOptional whether any ancestor particle of the element is optional
	 * @param ancestorUnbounded whether any ancestor particle of the element repeats
	 * @param wildcard the xsd:any
	 */
	private void createAttributeFromWildcard(XSDPackage resultPackage, XSDClassifier container,
			XSDParticle particle, boolean ancestorOptional, boolean ancestorUnbounded,
			XSDWildcard wildcard) {

		if (wildcard.getProcessContents().getValue() == XSDProcessContents.SKIP) {
			warnings.add("Removing xsd:any from " + container.getName()
					+ ". processContents = skip is not supported.");
			return;
		}

		XSDAttribute attr = new XSDAttribute("any");
		attr.setType(IModelElement.OBJECT);
		attr.setMinOccurs(ancestorOptional ? 0 : particle.getMinOccurs());
		if (ancestorUnbounded) {
			attr.setMaxOccurs(IAttribute.UNBOUNDED);
		} else {
			attr.setMaxOccurs(particle.getMaxOccurs());
		}
		attr.setSchemaElement(wildcard.getElement());

		attr.setOwner(container);
		container.addAttribute(attr);
	}

	/**
	 * Create an attribute from an element that is in a substitution group list.
	 * 
	 * @param container the {@link IModelElement} that will contain the attribute
	 * @param particle the nearest particle containing the element
	 * @param ancestorOptional whether any ancestor particle of the element is optional
	 * @param ancestorUnbounded whether any ancestor particle of the element repeats
	 * @param substitutionTarget the attribute (previously created from an element) being
	 * substituted
	 * @param substitutingElement the element acting as a substitute
	 * @return the new attribute
	 */
	private XSDAttribute createElementFromSubstitutionGroupElement(XSDClassifier container,
			XSDParticle particle, boolean ancestorUnbounded, XSDAttribute substitutionTarget,
			XSDElementDeclaration substitutingElement) {

		String typeName = XSDHelper.getCleanedName(substitutingElement.getName());
		if (substitutingElement.getType() != null
				&& substitutingElement.getType().getName() != null) {
			typeName = XSDHelper.getCleanedName(substitutingElement.getType().getName());
		}

		XSDAttribute substAttr = new XSDAttribute(substitutingElement, 0,
				ancestorUnbounded ? IAttribute.UNBOUNDED : particle.getMaxOccurs());
		substAttr.setTypeName(XSDHelper.getQualifiedName(substitutingElement.getTargetNamespace(),
				typeName));
		substAttr.setUserData(NAMESPACE, substitutingElement.getTargetNamespace());
		substAttr.setUserData(SUBSTITUTION_FOR, substitutionTarget);
		substAttr.setSchemaElement(substitutingElement.getElement());

		container.addAttribute(substAttr);
		substAttr.setOwner(container);
		return substAttr;
	}

	/**
	 * Assuming that two elements are contained within the same time, return the closest enclosing
	 * particular (sequence/choice, etc) that encloses both of them.
	 * 
	 * @param elementA the first element
	 * @param elementB the second element
	 * @return the closest particle or null if they have no common parent
	 */
	protected XSDModelGroup getClosestEnclosingGroup(XSDElementDeclaration elementA,
			XSDElementDeclaration elementB) {

		XSDConcreteComponent containerA = elementA.getContainer();
		while (containerA != null) {

			XSDConcreteComponent containerB = elementB.getContainer();

			while (containerB != null) {
				if (containerA == containerB && containerA instanceof XSDModelGroup)
					return (XSDModelGroup) containerA;

				containerB = containerB.getContainer();
				if (containerB instanceof XSDTypeDefinition
						|| containerB instanceof XSDElementDeclaration)
					break;
			}

			containerA = containerA.getContainer();
			if (containerA instanceof XSDTypeDefinition
					|| containerA instanceof XSDElementDeclaration)
				break;
		}

		return null;
	}

	/**
	 * Generate a complex type from a complex type definition, registered it with the name map, add
	 * it to the result package and return it.
	 * 
	 * @param resultPackage the package to add to
	 * @param complexDef the type definition
	 * @param anonymousTypeName a name suggestion if the type is anonymous (has no name)
	 * @param declaredAsGlobalElement if true, this is a global element with an anonymous type
	 * @return the complex type classifier
	 */
	protected XSDClassifier getComplexType(XSDPackage resultPackage,
			XSDComplexTypeDefinition complexDef, String anonymousTypeName,
			boolean declaredAsGlobalElement) {

		String name = complexDef.getName();
		if (name == null) {
			name = anonymousTypeName;
		}
		name = XSDHelper.getCleanedName(name);

		if (nameToElement.get(XSDHelper.getQualifiedName(complexDef.getTargetNamespace(), name)) != null) {
			if (!(nameToElement.get(XSDHelper.getQualifiedName(complexDef.getTargetNamespace(),
					name)) instanceof XSDClassifier)) {
				return null;
			}

			return (XSDClassifier) nameToElement.get(XSDHelper.getQualifiedName(complexDef
					.getTargetNamespace(), name));
		}

		XSDClassifier result = new XSDClassifier(name, resultPackage);
		result.setUserData(XSD_TYPE_KIND, XSD_COMPLEX_TYPE_KIND);
		result.setOriginalName(complexDef.getName() != null ? complexDef.getName()
				: anonymousTypeName);
		result.setUserData(XSD_GLOBAL_ELEMENT, declaredAsGlobalElement);
		result.setUserData(NAMESPACE, complexDef.getTargetNamespace());

		nameToElement
				.put(XSDHelper.getQualifiedName(complexDef.getTargetNamespace(), name), result);

		// Add the attribute definitions
		for (Iterator<?> attrIter = complexDef.getAttributeContents().iterator(); attrIter
				.hasNext();) {
			XSDAttributeGroupContent content = (XSDAttributeGroupContent) attrIter.next();

			if (content instanceof XSDAttributeUse) {

				XSDAttribute attr = new XSDAttribute((XSDAttributeUse) content);
				attr.setSchemaElement(content.getElement());
				result.addAttribute(attr);
				attr.setOwner(result);

				XSDSimpleTypeDefinition simple = ((XSDAttributeUse) content)
						.getAttributeDeclaration().getTypeDefinition();
				boolean isListType = simple != null
						&& XSDVariety.LIST_LITERAL == simple.getVariety();

				if (isListType && simple.getItemTypeDefinition() != null) {
					simple = simple.getItemTypeDefinition();
					while (simple.getBaseType() != null
							&& simple.getBaseType() instanceof XSDSimpleTypeDefinition
							&& !XSDBuiltInSimpleTypes.NAMESPACE.equals(simple.getBaseType()
									.getTargetNamespace())
							&& !"anySimpleType".equals(simple.getBaseType().getName()))
						simple = (XSDSimpleTypeDefinition) simple.getBaseType();
					attr.setXSDType(simple);
				}
			} else if (content instanceof XSDAttributeGroupDefinition) {

				XSDAttributeGroupDefinition group = ((XSDAttributeGroupDefinition) content)
						.getResolvedAttributeGroupDefinition();

				for (Iterator<?> groupIter = group.getAttributeUses().iterator(); groupIter
						.hasNext();) {
					XSDAttributeUse use = (XSDAttributeUse) groupIter.next();

					XSDAttribute attr = new XSDAttribute(use);
					attr.setSchemaElement(use.getElement());
					result.addAttribute(attr);
					attr.setOwner(result);
				}
			}
		}

		// Now add the content model
		XSDComplexTypeContent content = complexDef.getContent();

		if (content != null && content instanceof XSDParticle) {
			traverseParticle(resultPackage, result, (XSDParticle) content, false, false, complexDef
					.getTargetNamespace());
		} else if (content != null && content instanceof XSDSimpleTypeDefinition) {
			XSDSimpleTypeDefinition simpleDef = (XSDSimpleTypeDefinition) content;
			if (simpleDef.getEnumerationFacets().isEmpty()) {
				getDataType(simpleDef, resultPackage, "", false);
			} else {
				getEnumeration(simpleDef, resultPackage, "", false);
			}
		}

		if (complexDef.getBaseType() != null
				&& !complexDef.getBaseType().getName().equals("anyType")) {

			String baseName = XSDHelper.getCleanedName(complexDef.getBaseType().getName());
			result.setParentName(XSDHelper.getQualifiedName(complexDef.getBaseType()
					.getTargetNamespace(), baseName));
		} else {
			result.setParent(_object);
		}

		resultPackage.addElement(result);
		return result;
	}

	/**
	 * Read in all global complex type definitions and add to result package.
	 * 
	 * @param schema the schema to read from
	 * @param resultPackage package to add to
	 */
	protected void getComplexTypes(XSDSchema schema, XSDPackage resultPackage) {

		for (Iterator<?> iter = schema.getTypeDefinitions().iterator(); iter.hasNext();) {
			XSDTypeDefinition typeDef = (XSDTypeDefinition) iter.next();

			if (!(typeDef instanceof XSDComplexTypeDefinition))
				continue;

			if (schema != typeDef.getSchema())
				continue;
			XSDComplexTypeDefinition complexDef = (XSDComplexTypeDefinition) typeDef;

			if (complexDef.getContent() == null) {
				getComplexType(resultPackage, complexDef, "", false);
			} else if (complexDef.getContent() instanceof XSDSimpleTypeDefinition) {
				// Complex derived from simple is handled in getSimpleTypes()!
				continue;
			} else {
				getComplexType(resultPackage, complexDef, "", false);
			}
		}
	}

	/**
	 * Turn a sequence compositor to a string for naming purposes
	 * 
	 * @param compositor the compositor (choice/sequence/all)
	 * @return the string "Or" or "And"
	 */
	protected String getCompositorAsString(XSDCompositor compositor) {
		switch (compositor.getValue()) {
		case XSDCompositor.CHOICE:
			return "Or";
		default:
			return "And";
		}
	}

	/**
	 * Construct an XSDDataType from a complex type definition in the schema. The complex type must
	 * have ultimately inherited from a simple type to make this a valid operation.
	 * 
	 * @param def the definition
	 * @param resultPackage the package to add to
	 * @param anonymousName name to use if the type has no name
	 * @param declaredAsGlobalElement if true, this is a global element declaration with an anoymous
	 * type
	 */
	protected XSDDataType getDataType(XSDComplexTypeDefinition def, XSDPackage resultPackage,
			String anonymousName, boolean declaredAsGlobalElement) {

		String name = def.getName();
		if (name == null)
			name = anonymousName;
		name = XSDHelper.getCleanedName(name);

		XSDDataType result = new XSDDataType(name, resultPackage);
		result.setUserData(XSD_TYPE_KIND, XSD_COMPLEX_TYPE_KIND);
		result.setUserData(NAMESPACE, def.getTargetNamespace());
		result.setUserData(XSD_GLOBAL_ELEMENT, declaredAsGlobalElement);
		if (def.getName() != null) {
			result.setOriginalName(def.getName());
		}
		resultPackage.addElement(result);

		// If any supertype is an enumeration, so is this
		XSDTypeDefinition run = def;
		while (run != null && !"anyType".equals(run.getName())) {
			if (run instanceof XSDSimpleTypeDefinition) {
				XSDSimpleTypeDefinition simple = (XSDSimpleTypeDefinition) run;
				if (!simple.getEnumerationFacets().isEmpty()) {
					result.setEnumeration(true);
				}
			}

			run = run.getBaseType();
		}

		// Store base type name for later resolution
		if (def.getBaseType() != null)
			result.setParentName(XSDHelper.getQualifiedName(def.getBaseType().getTargetNamespace(),
					XSDHelper.getCleanedName(def.getBaseType().getName())));

		// Add attributes
		for (Iterator<?> iter = def.getAttributeUses().iterator(); iter.hasNext();) {
			XSDAttributeUse use = (XSDAttributeUse) iter.next();
			XSDAttributeDeclaration decl = use.getAttributeDeclaration();
			if (decl == null)
				continue;

			// Skip inherited
			if (use.getContainer() != def)
				continue;

			XSDAttribute attr = new XSDAttribute(use);
			attr.setSchemaElement(use.getElement());
			result.addAttribute(attr);
			attr.setOwner(result);

			XSDSimpleTypeDefinition simple = decl.getTypeDefinition();
			boolean isListType = simple != null && XSDVariety.LIST_LITERAL == simple.getVariety();

			if (isListType && simple.getItemTypeDefinition() != null) {
				simple = simple.getItemTypeDefinition();
				while (simple.getBaseType() != null
						&& simple.getBaseType() instanceof XSDSimpleTypeDefinition
						&& !XSDBuiltInSimpleTypes.NAMESPACE.equals(simple.getBaseType()
								.getTargetNamespace())
						&& !"anySimpleType".equals(simple.getBaseType().getName()))
					simple = (XSDSimpleTypeDefinition) simple.getBaseType();
				attr.setXSDType(simple);
				attr.setMaxOccurs(IAttribute.UNBOUNDED);
			} else if (!(decl.getTypeDefinition()).getVariety().equals(
							XSDVariety.ATOMIC_LITERAL)) {
				warnings.add("Attribute " + attr.getName()
						+ " uses a union type and will be excluded.");
				result.setAttributesStripped(true);
			}
		}

		nameToElement.put(XSDHelper.getQualifiedName(def.getTargetNamespace(), name), result);

		return result;
	}

	/**
	 * Construct an XSDDataType from a simple type definition in the schema.
	 * 
	 * @param def the definition
	 * @param resultPackage the package to add to
	 * @param anonymousName name to use if the type has no name
	 * @param declaredAsGlobalElement if true, this is a global element with an anoymous type
	 */
	protected XSDDataType getDataType(XSDSimpleTypeDefinition def, XSDPackage resultPackage,
			String anonymousName, boolean declaredAsGlobalElement) {

		String name = def.getName();
		if (name == null)
			name = anonymousName;
		name = XSDHelper.getCleanedName(name);

		XSDDataType result = new XSDDataType(name, resultPackage);
		result.setUserData(XSD_TYPE_KIND, XSD_SIMPLE_TYPE_KIND);
		result.setUserData(NAMESPACE, def.getTargetNamespace());
		result.setUserData(XSD_GLOBAL_ELEMENT, declaredAsGlobalElement);
		if (def.getName() != null) {
			result.setOriginalName(def.getName());
		}

		resultPackage.addElement(result);

		// If any supertype is an enumeration, so is this
		XSDTypeDefinition run = def;
		while (run != null && !"anyType".equals(run.getName())) {
			if (run instanceof XSDSimpleTypeDefinition) {
				XSDSimpleTypeDefinition simple = (XSDSimpleTypeDefinition) run;
				if (!simple.getEnumerationFacets().isEmpty()) {
					result.setEnumeration(true);
				}
			}

			run = run.getBaseType();
		}

		// Store base type name for later resolution
		if (def.getBaseType() != null)
			result.setParentName(XSDHelper.getQualifiedName(def.getBaseType().getTargetNamespace(),
					XSDHelper.getCleanedName(def.getBaseType().getName())));

		nameToElement.put(XSDHelper.getQualifiedName(def.getTargetNamespace(), name), result);

		return result;
	}

	/**
	 * Keep looking inside the nested particles until we find the first element declaration. The
	 * return it. Returns null if none found.
	 * 
	 * @param particle the particle to search
	 * @return the element
	 */
	protected XSDParticle getDescendantElementParticle(XSDParticle particle) {
		if (particle.getContent() == null)
			return null;

		if (particle.getContent() instanceof XSDElementDeclaration) {
			return particle;
		} else if (particle.getContent() instanceof XSDModelGroup) {
			XSDModelGroup group = (XSDModelGroup) particle.getContent();

			for (Iterator<?> iter = group.getContents().iterator(); iter.hasNext();) {
				XSDParticle next = (XSDParticle) iter.next();

				XSDParticle result = getDescendantElementParticle(next);
				if (result != null)
					return result;
			}
		}

		return null;
	}

	/**
	 * Return all element declarations in a particle, but not element declarations _within_ other
	 * element declarations, i.e. complex anonymous types.
	 * 
	 * @param particle the particle
	 * @return all elements declared in the particle
	 */
	protected List<XSDElementDeclaration> getDirectDescendants(XSDParticle particle) {
		List<XSDElementDeclaration> result = new ArrayList<XSDElementDeclaration>();

		if (particle.getContent() instanceof XSDElementDeclaration) {
			result.add((XSDElementDeclaration) particle.getContent());
		} else if (particle.getContent() instanceof XSDModelGroup) {
			XSDModelGroup group = (XSDModelGroup) particle.getContent();

			for (Iterator<?> iter = group.getContents().iterator(); iter.hasNext();) {
				XSDParticle next = (XSDParticle) iter.next();
				result.addAll(getDirectDescendants(next));
			}
		} else if (particle.getContent() instanceof XSDModelGroupDefinition) {
			XSDModelGroup group = ((XSDModelGroupDefinition) particle.getContent())
					.getResolvedModelGroupDefinition().getModelGroup();

			if (group != null) {
				for (Iterator<?> iter = group.getContents().iterator(); iter.hasNext();) {
					XSDParticle next = (XSDParticle) iter.next();
					result.addAll(getDirectDescendants(next));
				}
			}
		}

		return result;
	}

	/**
	 * Return a disambiguated type name unique within the namespace. Searches the name mapping and
	 * adds disambiguator at the end.
	 * 
	 * @param name the name
	 * @param targetNamespace the namespace
	 * @return the unique name
	 */
	protected String getDisambiguated(String name, String targetNamespace) {
		if (nameToElement.get(XSDHelper.getQualifiedName(targetNamespace, name)) != null) {

			int count = 1;

			while (true) {
				if (nameToElement.get(XSDHelper.getQualifiedName(targetNamespace, name + count)) == null)
					return name + count;
				count++;
			}
		} else {
			return name;
		}
	}

	/**
	 * Create an enumeration type from a schema simple type. This normally instatiates a classifier
	 * with static attributes.
	 * 
	 * @param def the the type
	 * @param resultPackage the package to add to
	 * @param anonymousName name to use if the type has no name
	 * @param declaredAsGlobalElement if true, this is a global element with an anonymous type
	 */
	protected XSDDataType getEnumeration(XSDSimpleTypeDefinition def, XSDPackage resultPackage,
			String anonymousName, boolean declaredAsGlobalElement) {

		String name = def.getName();
		if (name == null)
			name = anonymousName;
		name = XSDHelper.getCleanedName(name);

		XSDDataType result = new XSDDataType(name, resultPackage);
		result.setUserData(XSD_TYPE_KIND, XSD_SIMPLE_TYPE_KIND);
		result.setUserData(NAMESPACE, def.getTargetNamespace());
		result.setUserData(XSD_GLOBAL_ELEMENT, declaredAsGlobalElement);
		if (def.getName() != null) {
			result.setOriginalName(def.getName());
		}
		
		result.setEnumeration(true);

		resultPackage.addElement(result);

		// Store the base type
		if (def.getBaseType() != null)
			result.setParentName(XSDHelper.getQualifiedName(def.getBaseType().getTargetNamespace(),
					XSDHelper.getCleanedName(def.getBaseType().getName())));

		// Add enum values
		for (Iterator<?> iter = def.getEnumerationFacets().iterator(); iter.hasNext();) {
			XSDEnumerationFacet facet = (XSDEnumerationFacet) iter.next();

			XSDAttribute attribute = new XSDAttribute(facet.getLexicalValue());
			attribute.setStatic(true);
			attribute.setType(result);
			attribute.setMinOccurs(1);
			attribute.setMaxOccurs(1);
			attribute.setSchemaElement(facet.getElement());

			result.addAttribute(attribute);
			attribute.setOwner(result);
		}

		nameToElement.put(XSDHelper.getQualifiedName(def.getTargetNamespace(), name), result);

		return result;
	}

	/**
	 * Suggest a property name based on a content model of sequences and choices. This should only
	 * be called if the full extent of the content model has at least two elements in it.
	 * <p>
	 * It will create a suggested name based on the JAXB convention, for example
	 * elementAAndElementB, if the elements are in a sequence.
	 * 
	 * @param particle the particle
	 * @return a name or null if it was not possible to determine the property name
	 */
	protected String getFlattenedJAXBPropertyName(XSDParticle particle) {
		List<XSDElementDeclaration> elements = getDirectDescendants(particle);

		if (elements.size() < 2)
			throw new RuntimeException("Internal error: particle with insufficient element content");

		StringBuffer result = new StringBuffer();

		XSDElementDeclaration elementOne = elements.get(0);
		XSDElementDeclaration elementTwo = elements.get(1);

		XSDModelGroup part = getClosestEnclosingGroup(elementOne, elementTwo);
		if (part == null)
			return null;

		result.append(XSDHelper.toLowerCase(XSDHelper.getCleanedName(elementOne
				.getResolvedElementDeclaration().getName())));
		result.append(getCompositorAsString(part.getCompositor()));
		result.append(XSDHelper.toUpperCase(XSDHelper.getCleanedName(elementTwo
				.getResolvedElementDeclaration().getName())));

		if (elements.size() == 2)
			return result.toString();

		// Append a third if necessary

		XSDElementDeclaration elementThree = elements.get(2);
		part = getClosestEnclosingGroup(elementTwo, elementThree);
		if (part == null)
			return null;

		result.append(getCompositorAsString(part.getCompositor()));
		result.append(XSDHelper.toUpperCase(XSDHelper.getCleanedName(elementThree
				.getResolvedElementDeclaration().getName())));

		return result.toString();
	}

	/**
	 * Read in all global element definitions, turning each into a classifier.
	 * 
	 * @param schema the schema to read from
	 * @param resultPackage the result package to add to
	 */
	protected void getGlobalElements(XSDSchema schema, XSDPackage resultPackage) {
		for (Iterator<?> iter = schema.getElementDeclarations().iterator(); iter.hasNext();) {
			XSDElementDeclaration decl = (XSDElementDeclaration) iter.next();

			if (schema != decl.getSchema())
				continue;

			String name = XSDHelper.getCleanedName(decl.getName());

			if (decl.getType() != null) {

				// Anonymous type
				if (decl.getType().getName() == null) {

					// Check for clash!
					if (nameToElement.get(XSDHelper.getQualifiedName(decl.getTargetNamespace(),
							name)) != null) {
						warnings.add("Global element " + name
								+ " clashes with global type of the same name. Element lost.");
					} else {
						if (decl.getType() instanceof XSDComplexTypeDefinition) {
							if (decl.getType().getRootType() instanceof XSDSimpleTypeDefinition) {
								getDataType((XSDComplexTypeDefinition) decl.getType(),
										resultPackage, name, true).setOriginalName(decl.getName());
							} else {
								getComplexType(resultPackage,
										(XSDComplexTypeDefinition) decl.getType(), name, true)
										.setOriginalName(decl.getName());
							}
						} else if (decl.getType() instanceof XSDSimpleTypeDefinition) {
							XSDSimpleTypeDefinition simple = (XSDSimpleTypeDefinition) decl
									.getType();

							// We don't create global list types
							if (XSDVariety.LIST_LITERAL.equals(simple.getVariety()))
								continue;

							if (simple.getEnumerationFacets().isEmpty()) {
								getDataType(simple, resultPackage, name, true).setOriginalName(
										decl.getName());
							} else {
								getEnumeration(simple, resultPackage, name, true).setOriginalName(
										decl.getName());
							}
						}
					}

				} else {
					// Ignore - explicit type has been assigned to the element
					// Wherever the element is reference later, use the type
					// instead.
				}
			} else {
				// Empty type
				XSDClassifier result = new XSDClassifier(name, resultPackage);
				result.setOriginalName(decl.getName());
				result.setUserData(XSD_TYPE_KIND, XSD_COMPLEX_TYPE_KIND);
				result.setUserData(NAMESPACE, decl.getTargetNamespace());
				result.setUserData(XSD_GLOBAL_ELEMENT, true);

				resultPackage.addElement(result);
				nameToElement.put(XSDHelper.getQualifiedName(decl.getTargetNamespace(), name),
						result);
			}
		}
	}

	/**
	 * Recursively search for elements within a repeating particle. Return the number found.
	 * 
	 * @param particle the particle to search
	 * @return the number of elements found
	 */
	protected int getNumberOfDescendantElements(XSDParticle particle) {
		if (particle.getContent() == null)
			return 0;

		if (particle.getContent() instanceof XSDElementDeclaration) {
			return 1;
		} else if (particle.getContent() instanceof XSDModelGroup) {
			return getNumberOfDescendedElements((XSDModelGroup) particle.getContent());
		} else if (particle.getContent() instanceof XSDModelGroupDefinition) {
			XSDModelGroupDefinition groupDefinition = (XSDModelGroupDefinition) particle
					.getContent();
			XSDModelGroup group = groupDefinition.getResolvedModelGroupDefinition().getModelGroup();
			return getNumberOfDescendedElements(group);
		}

		return 0;
	}

	private int getNumberOfDescendedElements(XSDModelGroup group) {
		int count = 0;
		for (Iterator<?> iter = group.getContents().iterator(); iter.hasNext();) {
			XSDParticle next = (XSDParticle) iter.next();
			count += getNumberOfDescendantElements(next);
		}

		return count;
	}

	/**
	 * Add anything to the result package that legitimately qualifies as a data type: simple types,
	 * derived simple types, enumerations and complex types that derive from simple types.
	 * 
	 * @param schema the schema to scan
	 * @param resultPackage the package to add to
	 */
	protected void getSimpleTypes(XSDSchema schema, XSDPackage resultPackage) {

		for (Iterator<?> iter = schema.getTypeDefinitions().iterator(); iter.hasNext();) {
			XSDTypeDefinition typeDef = (XSDTypeDefinition) iter.next();

			if (schema != typeDef.getSchema())
				continue;

			// Straight-forward simple type
			if (typeDef instanceof XSDSimpleTypeDefinition) {
				XSDSimpleTypeDefinition simpleDef = (XSDSimpleTypeDefinition) typeDef;

				if (XSDVariety.UNION_LITERAL == simpleDef.getVariety()) {
					warnings.add("Union types are not supported: found in " + typeDef.getName()
							+ ". Type removed.");
					continue;
				}

				if (XSDVariety.LIST_LITERAL == simpleDef.getVariety()) {
					// We don't need to create "global" list types, they resolve
					// to built
					// in primitive types when they get used anyway.
					continue;
				} else if (simpleDef.getEnumerationFacets().isEmpty()) {
					getDataType(simpleDef, resultPackage, "", false);
				} else {
					getEnumeration(simpleDef, resultPackage, "", false);
				}
			} else if (typeDef instanceof XSDComplexTypeDefinition) {
				XSDComplexTypeDefinition complexDef = (XSDComplexTypeDefinition) typeDef;

				// Complex type that derives from a simple type?
				if (typeDef.getRootType() instanceof XSDSimpleTypeDefinition) {
					getDataType(complexDef, resultPackage, "", false);
				}
			}
		}
	}

	/**
	 * Suggest a type name for an anonymous sub type, based on the element it has been assigned to.
	 * 
	 * @param assignedToElement the element name
	 * @return the type name, which will have to be unique
	 */
	protected String getSuggestedAnonymousTypeName(String namespace, String assignedToElement) {
		// Make first char upper case
		String name = assignedToElement;

		Set<String> names = namespaceToTypeNames.get(namespace == null ? "" : namespace);
		if (names == null) {
			return name;
		}

		// Disambiguate
		if (names.contains(name)
				|| nameToElement.containsKey(XSDHelper.getQualifiedName(namespace, name))) {
			int count = 1;
			while (names.contains(name + count)
					|| nameToElement.containsKey(XSDHelper
							.getQualifiedName(namespace, name + count))) {
				count++;
			}
			name = name + count;
		}

		return name;
	}

	/**
	 * Return the list of warning strings created during the last load operation.
	 * 
	 * @return the warnings
	 */
	public List<String> getWarnings() {
		return warnings;
	}

	/**
	 * Load a model contained in the specified file. This will break up the model into an NRL model.
	 * <p>
	 * The method currently also ignores all ClassNotFoundException and PackageNotFoundException
	 * errors, to ignore problems where a profile is not found.
	 * 
	 * @deprecated Use the {@link IModelLoader} interface to load models.
	 * @param file the file to load
	 * @return the model
	 * @throws Exception
	 */
	@Deprecated
	public IPackage load(File file) throws Exception {
		Resource res = new ResourceSetImpl().createResource(URI.createFileURI(file
				.getAbsolutePath()));
		return load(res, file);
	}

	/**
	 * Load with no options.
	 * 
	 * @deprecated Use the {@link IModelLoader} interface to load models.
	 */
	@Deprecated
	public IPackage load(Resource res, File file) throws Exception {
		return load(res, file, null);
	}

	/**
	 * Load using an ECore resource and an option map. Used for testing.
	 * 
	 * @deprecated Use the {@link IModelLoader} interface to load models, or
	 * {@link #load(Resource, Map)} if you must.
	 */
	@Deprecated
	public IPackage load(Resource res, File file, Map<?, ?> options) throws Exception {
		return load(res, options);
	}

	public IPackage load(Resource res, Map<?, ?> options) throws Exception {
		warnings.clear();
		nameToElement.clear();
		namespaceToTypeNames.clear();

		try {
			res.load(options);
		} catch (Resource.IOWrappedException e) {
			// These we will ignore for the time being
		} catch (IOException e) {
			throw e;
		}

		if (res.getContents().isEmpty() || !(res.getContents().get(0) instanceof XSDSchema)) {
			throw new Exception("No schema information found in " + res.getURI().toString());
		}

		XSDSchema schema = (XSDSchema) res.getContents().get(0);
		XSDSchema rootSchema = schema;
		XSDPackage result = new XSDPackage(XSDHelper.getPackageName(schema.getSchemaLocation()),
				null);
		result.setUserData(IXSDUserData.MODEL_FILE_LOCATION, schema.getSchemaLocation());
		result.setUserData(IXSDUserData.NAMESPACE, schema.getTargetNamespace());

		// Store the XS simple types
		XSDPackage xsdTypes = new XSDBuiltInSimpleTypes().getSimpleTypePackage();
		xsdTypes.setUserData(IXSDUserData.NAMESPACE, XSDBuiltInSimpleTypes.NAMESPACE);
		for (Iterator<?> iter = xsdTypes.getContents(false).iterator(); iter.hasNext();) {
			IModelElement element = (IModelElement) iter.next();

			element.setUserData(IXSDUserData.NAMESPACE, XSDBuiltInSimpleTypes.NAMESPACE);

			nameToElement.put(XSDHelper.getQualifiedName(XSDBuiltInSimpleTypes.NAMESPACE, element
					.getName()), element);
		}

		result.addElement(xsdTypes);

		// Store the Object super type
		_object = new XSDClassifier("Object", xsdTypes);
		xsdTypes.addElement(_object);
		nameToElement.put(XSDHelper.getQualifiedName(XSDBuiltInSimpleTypes.NAMESPACE, _object
				.getName()), _object);

		// Build the namespace to name map
		for (Iterator<?> iter = res.getResourceSet().getResources().iterator(); iter.hasNext();) {
			Object obj = iter.next();
			if (!(obj instanceof XSDResourceImpl)) {
				continue;
			}
			XSDResourceImpl impl = (XSDResourceImpl) obj;

			if (impl.getSchema() != null) {
				schema = impl.getSchema();
				populatePackageTypeNameMap(schema);
			}
		}

		// Add all resources in the returned resource set (i.e. included and
		// imported schemas
		for (Iterator<?> iter = res.getResourceSet().getResources().iterator(); iter.hasNext();) {
			Object obj = iter.next();
			if (!(obj instanceof XSDResourceImpl)) {
				continue;
			}

			XSDResourceImpl impl = (XSDResourceImpl) obj;
			if (impl.getSchema() != null) {

				schema = impl.getSchema();

				checkForRedefines(schema);

				XSDPackage childPackage = new XSDPackage(XSDHelper.getPackageName(schema
						.getSchemaLocation()), result);
				childPackage.setUserData(IXSDUserData.MODEL_FILE_LOCATION, schema
						.getSchemaLocation());
				result.addElement(childPackage);

				if (schema.getTargetNamespace() != null) {
					childPackage.setUserData(NAMESPACE, schema.getTargetNamespace());
				} else {
					childPackage.setUserData(NAMESPACE, rootSchema.getTargetNamespace());
					schema.setTargetNamespace(rootSchema.getTargetNamespace());
				}

				// Read in derived simple types from the schema
				getSimpleTypes(schema, childPackage);

				// Read in global complex type definitions
				getComplexTypes(schema, childPackage);

				// Read in global element definitions
				getGlobalElements(schema, childPackage);
			} else {
				throw new Exception("Resource " + impl.getURI().toString()
						+ " did not resolve during schema loading!");
			}
		}

		resolve(result);

		relabelDuplicateAttributes(result);

		return result;
	}

	/**
	 * Examines the schema for redefines, if one is found then an exception is thrown as NRL is not
	 * compatiable with redefines.
	 * 
	 * @param schema
	 */
	private void checkForRedefines(XSDSchema schema) throws UnprocessableSchemaException {
		EList<?> contents = schema.getContents();
		for (Object element : contents) {
			if (element instanceof XSDRedefine) {
				throw new UnprocessableSchemaException(format(
						"Schema %s cannot be processed as it contains \"xs:redefine\".", schema
								.getSchemaLocation()));
			}
		}
	}

	/*
	 * Fill in packageToTypeNames for a particular schema: put all globally declared type names into
	 * the map.
	 */
	private void populatePackageTypeNameMap(XSDSchema schema) {
		String targetNamespace = schema.getTargetNamespace();
		if (targetNamespace == null) {
			targetNamespace = "";
		}

		Set<String> names = namespaceToTypeNames.get(targetNamespace);
		if (names == null) {
			names = new HashSet<String>();
			namespaceToTypeNames.put(targetNamespace, names);
		}

		for (Iterator<?> iter = schema.getTypeDefinitions().iterator(); iter.hasNext();) {
			XSDTypeDefinition typeDef = (XSDTypeDefinition) iter.next();

			if (schema != typeDef.getSchema()) {
				continue;
			}

			String name = XSDHelper.getCleanedName(typeDef.getName());
			names.add(name);
		}

		for (Iterator<?> iter = schema.getElementDeclarations().iterator(); iter.hasNext();) {
			XSDElementDeclaration decl = (XSDElementDeclaration) iter.next();

			if (schema != decl.getSchema()) {
				continue;
			}

			String name = XSDHelper.getCleanedName(decl.getName());
			names.add(name);
		}
	}

	/**
	 * Resolve attribute types, parent references.
	 */
	protected void resolve(XSDPackage pkg) {
		// Go through all model elements in entire model
		List<IModelElement> contents = pkg.getContents(true);
		for (IModelElement element : contents) {

			// Resolve all attributes
			if (element instanceof AbstractClassifier) {
				AbstractClassifier classifier = (AbstractClassifier) element;

				for (Iterator<IAttribute> attrIter = classifier.getAttributes().iterator(); attrIter
						.hasNext();) {
					XSDAttribute attr = (XSDAttribute) attrIter.next();

					// Already resolved?
					if (attr.getType() != null)
						continue;

					String typeName = attr.getTypeName();

					if (nameToElement.get(typeName) == null)
						typeName = XSDHelper.stripNamespace(typeName);

					if (nameToElement.get(typeName) == null) {
						attrIter.remove();
						classifier.removeAttributeNameMapping(attr.getName());

						warnings.add("Cannot resolve type of attribute " + attr.getName() + " ("
								+ typeName + "). Attribute removed.");
					} else {
						attr.setType(nameToElement.get(typeName));
					}
				}
			}

			// Resolve super-type reference, if any
			if (element instanceof XSDDataType) {
				XSDDataType dataType = (XSDDataType) element;

				if (dataType.getParentName() != null) {
					String typeName = dataType.getParentName();
					if (nameToElement.get(typeName) == null)
						typeName = XSDHelper.stripNamespace(typeName);

					IModelElement parent = (IModelElement) nameToElement.get(typeName);
					if (parent == null) {
						warnings.add("Cannot resolve base type of " + dataType.getName() + " ("
								+ dataType.getParentName() + "). Contents may be lost.");
					} else if (parent != dataType) {
						dataType.setParent(parent);
						((AbstractModelElement) parent).addChild(dataType);
					}
				}
			}

			// Resolve super-type reference, if any
			if (element instanceof XSDClassifier) {
				XSDClassifier classifier = (XSDClassifier) element;

				if (classifier.getParentName() != null) {
					String typeName = classifier.getParentName();
					if (nameToElement.get(typeName) == null)
						typeName = XSDHelper.stripNamespace(typeName);

					IModelElement parent = (IModelElement) nameToElement.get(typeName);
					if (parent == null) {
						warnings.add("Cannot resolve base type of " + classifier.getName() + " ("
								+ classifier.getParentName() + "). Contents may be lost.");
					} else if (classifier != parent) {
						classifier.setParent(parent);
						((AbstractModelElement) parent).addChild(classifier);
					}
				}
			}
		}
	}

	/**
	 * Recursive method that traverses a schema particle content model and adds attributes to a
	 * classifier.
	 * 
	 * @param result the classifier to add to
	 * @param particle the particle
	 * @param ancestorOptional if true, an ancestor particle was optional; everything from here on
	 * will be optional
	 * @param ancestorUnbounded if true, an ancestor was unbounded; everything from here on will be
	 * unbounded
	 */
	protected void traverseParticle(XSDPackage resultPackage, XSDClassifier result,
			XSDParticle particle, boolean ancestorOptional, boolean ancestorUnbounded,
			String targetNamespace) {

		if (particle.getContent() == null)
			return;

		if (particle.getMinOccurs() == 0) {
			ancestorOptional = true;
		}

		if (particle.getContent() instanceof XSDElementDeclaration) {
			createAttributeFromElement(resultPackage, result, particle, ancestorOptional,
					ancestorUnbounded);
		} else if (particle.getContent() instanceof XSDWildcard) {
			createAttributeFromWildcard(resultPackage, result, particle, ancestorOptional,
					ancestorUnbounded, (XSDWildcard) particle.getContent());
		} else if (particle.getContent() instanceof XSDModelGroup
				|| particle.getContent() instanceof XSDModelGroupDefinition) {
			XSDModelGroup group = null;
			if (particle.getContent() instanceof XSDModelGroup) {
				group = (XSDModelGroup) particle.getContent();
			} else {
				group = ((XSDModelGroupDefinition) particle.getContent())
						.getResolvedModelGroupDefinition().getModelGroup();
			}

			// If the particle is unbounded, special processing needs to group
			// all contained elements into a list. See JAXB specification 2.1
			// final release, 6.12.7, case 2.
			if (particle.getMaxOccurs() > 1 || particle.getMaxOccurs() == XSDParticle.UNBOUNDED) {
				List<XSDElementDeclaration> directDescendants = getDirectDescendants(particle);

				String flattenedJAXBPropertyName = null;
				if (getNumberOfDescendantElements(particle) > 1) {
					flattenedJAXBPropertyName = getFlattenedJAXBPropertyName(particle);
				}

				for (XSDElementDeclaration element : directDescendants) {
					if (element.getContainer() instanceof XSDParticle) {
						XSDAttribute attr = createAttributeFromElement(resultPackage, result,
								(XSDParticle) element.getContainer(), particle.getMinOccurs() == 0
										|| ancestorOptional, true);

						if (flattenedJAXBPropertyName != null) {
							attr.setUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY,
									flattenedJAXBPropertyName);
						}
					}
				}
				return;
			}

			// Further nested content model
			for (Iterator<?> iter = group.getContents().iterator(); iter.hasNext();) {
				XSDParticle next = (XSDParticle) iter.next();
				traverseParticle(resultPackage, result, next, ancestorOptional, ancestorUnbounded,
						targetNamespace);
			}
		}
	}
}
