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

import java.io.File;

import junit.framework.TestCase;
import net.sourceforge.nrl.parser.model.IAttribute;
import net.sourceforge.nrl.parser.model.IClassifier;
import net.sourceforge.nrl.parser.model.IPackage;
import net.sourceforge.nrl.parser.model.ModelUtils;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;

/**
 * Test loading of schemas with duplicate element names.
 */
public class DuplicateElementTest extends TestCase {

	private static boolean isInitialised = false;

	private static IPackage duplicates = null, groups = null;


	@SuppressWarnings("deprecation")
	public void setUp() throws Exception {
		if (!isInitialised) {
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd",
					new XSDResourceFactoryImpl());
			isInitialised = true;

			XSDModelLoader loader = new XSDModelLoader();
			duplicates = loader.load(new File("src/test/resources/schema/duplicate-elements.xsd"));

			loader = new XSDModelLoader();
			groups = loader.load(new File("src/test/resources/schema/groups.xsd"));
		}
	}

	/*
	 * Just a double-check that the model loaded
	 */
	public void testLoading() throws Exception {
		assertNotNull(duplicates);
	}

	public void testDuplicateElementInSequence() {
		IClassifier container = (IClassifier) duplicates
				.getElementByName("DuplicateElement1", true);

		assertEquals(1, container.getAttributes(false).size());

		IAttribute attr = container.getAttributeByName("element", false);
		assertNotNull(attr);
		assertEquals("element", attr.getOriginalName());
		assertEquals("string", attr.getType().getName());
		assertFalse(ModelUtils.isRenamedXSDAttribute(attr));
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertEquals("Catch-all is wrong", "content", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertEquals(1, attr.getMinOccurs());
		assertEquals(IAttribute.UNBOUNDED, attr.getMaxOccurs());
	}

	public void testDuplicateElementInMultipleParticles() {
		IClassifier container = (IClassifier) duplicates
				.getElementByName("DuplicateElement2", true);

		assertEquals(2, container.getAttributes(false).size());

		IAttribute attr = container.getAttributeByName("element", false);
		assertNotNull(attr);
		assertEquals("element", attr.getOriginalName());
		assertEquals("string", attr.getType().getName());
		assertFalse(ModelUtils.isRenamedXSDAttribute(attr));
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertEquals("Catch-all is wrong", "content", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertEquals(0, attr.getMinOccurs());
		assertEquals(IAttribute.UNBOUNDED, attr.getMaxOccurs());

		attr = container.getAttributeByName("otherElement", false);
		assertNotNull(attr);
		assertEquals("string", attr.getType().getName());
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertEquals("Catch-all is wrong", "content", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
	}

	public void testDuplicateElementInMultipleSequences() {
		IClassifier container = (IClassifier) duplicates
				.getElementByName("DuplicateElement3", true);

		assertEquals(1, container.getAttributes(false).size());

		IAttribute attr = container.getAttributeByName("element", false);
		assertNotNull(attr);
		assertEquals("element", attr.getOriginalName());
		assertEquals("string", attr.getType().getName());
		assertFalse(ModelUtils.isRenamedXSDAttribute(attr));
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertEquals("Catch-all is wrong", "content", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertEquals(1, attr.getMinOccurs());
		assertEquals(IAttribute.UNBOUNDED, attr.getMaxOccurs());
	}

	public void testMultipleDuplicateElementInSequence() {
		IClassifier container = (IClassifier) duplicates
				.getElementByName("DuplicateElement4", true);

		assertEquals(1, container.getAttributes(false).size());

		IAttribute attr = container.getAttributeByName("element", false);
		assertNotNull(attr);
		assertEquals("element", attr.getOriginalName());
		assertEquals("string", attr.getType().getName());
		assertFalse(ModelUtils.isRenamedXSDAttribute(attr));
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertEquals("Catch-all is wrong", "content", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertEquals(1, attr.getMinOccurs());
		assertEquals(IAttribute.UNBOUNDED, attr.getMaxOccurs());
	}

	public void testSameElementAndAttribute() {
		IClassifier container = (IClassifier) duplicates.getElementByName(
				"SameElementAndAttribute", true);

		IAttribute attr = container.getAttributeByName("idAttribute", false);
		assertNotNull(attr);
		assertEquals("string", attr.getType().getName());
		assertTrue(ModelUtils.isAttributeAnXMLAttribute(attr));
		assertTrue(ModelUtils.isRenamedXSDAttribute(attr));
		assertEquals(
				"Schema path wrong",
				"/xs:schema/xs:complexType[@name = 'SameElementAndAttribute']/xs:attribute[@name = 'id']",
				ModelUtils.getXSDPath(attr));
		assertNull("Catch-all is wrong", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertEquals(0, attr.getMinOccurs());
		assertEquals(1, attr.getMaxOccurs());

		attr = container.getAttributeByName("id", false);
		assertNotNull(attr);
		assertEquals("id", attr.getOriginalName());
		assertEquals("int", attr.getType().getName());
		assertTrue(ModelUtils.isAttributeAnXMLElement(attr));
		assertFalse(ModelUtils.isRenamedXSDAttribute(attr));
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertNull("Catch-all is wrong", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertEquals(1, attr.getMinOccurs());
		assertEquals(1, attr.getMaxOccurs());
	}

	public void testSameMultipleElementsAndAttribute() {
		IClassifier container = (IClassifier) duplicates.getElementByName(
				"SameMultipleElementsAndAttribute", true);

		IAttribute attr = container.getAttributeByName("idAttribute", false);
		assertNotNull(attr);
		assertEquals("string", attr.getType().getName());
		assertTrue(ModelUtils.isAttributeAnXMLAttribute(attr));
		assertTrue(ModelUtils.isRenamedXSDAttribute(attr));
		assertEquals(
				"Schema path wrong",
				"/xs:schema/xs:complexType[@name = 'SameMultipleElementsAndAttribute']/xs:attribute[@name = 'id']",
				ModelUtils.getXSDPath(attr));
		assertNull("Catch-all is wrong", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertEquals(0, attr.getMinOccurs());
		assertEquals(1, attr.getMaxOccurs());

		attr = container.getAttributeByName("id", false);
		assertNotNull(attr);
		assertEquals("id", attr.getOriginalName());
		assertEquals("int", attr.getType().getName());
		assertTrue(ModelUtils.isAttributeAnXMLElement(attr));
		assertFalse(ModelUtils.isRenamedXSDAttribute(attr));
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertEquals("Catch-all is wrong", "content", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertEquals(1, attr.getMinOccurs());
		assertEquals(IAttribute.UNBOUNDED, attr.getMaxOccurs());
	}

	public void testSubTypeWithDuplicate() {
		IClassifier container = (IClassifier) duplicates.getElementByName("SubTypeWithDuplicate",
				true);

		IAttribute attr = container.getAttributeByName("element", true);
		assertNotNull(attr);
		assertEquals("string", attr.getType().getName());
		assertFalse(ModelUtils.isRenamedXSDAttribute(attr));
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertNull("Catch-all is wrong", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertEquals(1, attr.getMinOccurs());
		assertEquals(1, attr.getMaxOccurs());

		attr = container.getAttributeByName("element2", true);
		assertNotNull(attr);
		assertEquals("element", attr.getOriginalName());
		assertEquals("string", attr.getType().getName());
		assertTrue(ModelUtils.isRenamedXSDAttribute(attr));
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertEquals("Catch-all is wrong", "rest", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertEquals(1, attr.getMinOccurs());
		assertEquals(1, attr.getMaxOccurs());
	}

	public void testSubTypeWithCollapsedDuplicateInParent() {
		IClassifier container = (IClassifier) duplicates.getElementByName(
				"SubTypeForUnboundedChoice", true);

		IAttribute attr = container.getAttributeByName("elementA", true);
		assertNotNull(attr);
		assertEquals("string", attr.getType().getName());
		assertFalse(ModelUtils.isRenamedXSDAttribute(attr));
		assertNotNull(attr.getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertNull("Catch-all is wrong", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertEquals(1, attr.getMinOccurs());
		assertEquals(IAttribute.UNBOUNDED, attr.getMaxOccurs());

		attr = container.getAttributeByName("elementB", true);
		assertNotNull(attr);
		assertEquals("elementB", attr.getOriginalName());
		assertEquals("string", attr.getType().getName());
		assertFalse(ModelUtils.isRenamedXSDAttribute(attr));
		assertNotNull(attr.getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertNull("Catch-all is wrong", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertEquals(1, attr.getMinOccurs());
		assertEquals(IAttribute.UNBOUNDED, attr.getMaxOccurs());

		attr = container.getAttributeByName("elementC", true);
		assertNotNull(attr);
		assertEquals("elementC", attr.getOriginalName());
		assertEquals("string", attr.getType().getName());
		assertFalse(ModelUtils.isRenamedXSDAttribute(attr));
		assertNull(attr.getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));
		assertNull("Catch-all is wrong", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertEquals(1, attr.getMinOccurs());
		assertEquals(1, attr.getMaxOccurs());

		attr = container.getAttributeByName("elementA2", true);
		assertNotNull(attr);
		assertEquals("elementA", attr.getOriginalName());
		assertEquals("string", attr.getType().getName());
		assertTrue(ModelUtils.isRenamedXSDAttribute(attr));
		assertNull(attr.getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));
		assertNull("Catch-all is wrong", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertEquals(1, attr.getMinOccurs());
		assertEquals(1, attr.getMaxOccurs());
	}

	public void testSubTypeWithCollisionInParent() {
		IClassifier container = (IClassifier) duplicates.getElementByName(
				"SubTypeWithCollisionInSuperType", true);

		assertEquals("Wrong number of attributes", 2, container.getAttributes(true).size());

		IAttribute attr = container.getAttributeByName("elementA", true);
		assertNotNull(attr);
		assertEquals("string", attr.getType().getName());
		assertFalse(ModelUtils.isRenamedXSDAttribute(attr));
		assertNull(attr.getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertEquals("Catch-all is wrong", "content", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertEquals(1, attr.getMinOccurs());
		assertEquals(IAttribute.UNBOUNDED, attr.getMaxOccurs());

		attr = container.getAttributeByName("elementC", true);
		assertNotNull(attr);
		assertEquals("elementC", attr.getOriginalName());
		assertEquals("string", attr.getType().getName());
		assertFalse(ModelUtils.isRenamedXSDAttribute(attr));
		assertNull(attr.getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));
		assertEquals("Catch-all is wrong", "content", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertEquals(1, attr.getMinOccurs());
		assertEquals(1, attr.getMaxOccurs());

	}
	
	public void testSubTypeWithUnboundedChoiceAndCollisionInParent() {
		IClassifier container = (IClassifier) duplicates.getElementByName(
				"SubTypeWithClashAndUnboundedChoice", true);
		
		assertEquals("Wrong number of attributes", 3, container.getAttributes(true).size());
		
		IAttribute attr = container.getAttributeByName("elementA", true);
		assertNotNull(attr);
		assertEquals("string", attr.getType().getName());
		assertFalse(ModelUtils.isRenamedXSDAttribute(attr));
		assertNull(attr.getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertEquals("Catch-all is wrong", "content", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertEquals(1, attr.getMinOccurs());
		assertEquals(IAttribute.UNBOUNDED, attr.getMaxOccurs());
		
		attr = container.getAttributeByName("elementB", true);
		assertNotNull(attr);
		assertEquals("elementB", attr.getOriginalName());
		assertEquals("string", attr.getType().getName());
		assertFalse(ModelUtils.isRenamedXSDAttribute(attr));
		assertNull(attr.getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));
		assertEquals("Catch-all is wrong", "content", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertEquals(1, attr.getMinOccurs());
		assertEquals(IAttribute.UNBOUNDED, attr.getMaxOccurs());

		attr = container.getAttributeByName("elementC", true);
		assertNotNull(attr);
		assertEquals("elementC", attr.getOriginalName());
		assertEquals("string", attr.getType().getName());
		assertFalse(ModelUtils.isRenamedXSDAttribute(attr));
		assertNull(attr.getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));
		assertEquals("Catch-all is wrong", "content", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertEquals(1, attr.getMinOccurs());
		assertEquals(IAttribute.UNBOUNDED, attr.getMaxOccurs());
	}
	
	public void testSubTypeWithUnboundedChoiceAndNoCollisionInParent() {
		IClassifier container = (IClassifier) duplicates.getElementByName(
				"SubTypeWithNoClashAndUnboundedChoice", true);
		
		assertEquals("Wrong number of attributes", 3, container.getAttributes(true).size());
		
		IAttribute attr = container.getAttributeByName("element", true);
		assertNotNull(attr);
		assertEquals("string", attr.getType().getName());
		assertFalse(ModelUtils.isRenamedXSDAttribute(attr));
		assertNull(attr.getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertNull("Catch-all is wrong", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertEquals(1, attr.getMinOccurs());
		assertEquals(1, attr.getMaxOccurs());
		
		attr = container.getAttributeByName("elementB", true);
		assertNotNull(attr);
		assertEquals("elementB", attr.getOriginalName());
		assertEquals("string", attr.getType().getName());
		assertFalse(ModelUtils.isRenamedXSDAttribute(attr));
		assertEquals("Flattened property is wrong", "elementBOrElementC", attr.getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));
		assertNull("Catch-all is wrong", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertEquals(1, attr.getMinOccurs());
		assertEquals(IAttribute.UNBOUNDED, attr.getMaxOccurs());
		
		attr = container.getAttributeByName("elementC", true);
		assertNotNull(attr);
		assertEquals("elementC", attr.getOriginalName());
		assertEquals("string", attr.getType().getName());
		assertFalse(ModelUtils.isRenamedXSDAttribute(attr));
		assertEquals("Flattened property is wrong", "elementBOrElementC", attr.getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));
		assertNull("Catch-all is wrong", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertEquals(1, attr.getMinOccurs());
		assertEquals(IAttribute.UNBOUNDED, attr.getMaxOccurs());
	}

	public void testSubstitutionGroupDuplicates() {
		IClassifier container = (IClassifier) duplicates.getElementByName("SubstitutionContainer",
				true);

		IAttribute attr = container.getAttributeByName("substitutableElement", true);
		assertNotNull(attr);
		assertEquals("SubstitutableType", attr.getType().getName());
		assertFalse(ModelUtils.isRenamedXSDAttribute(attr));
		assertNull("Catch-all is wrong", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertEquals(1, attr.getMinOccurs());
		assertEquals(1, attr.getMaxOccurs());

		attr = container.getAttributeByName("substitutableElement2", true);
		assertNotNull(attr);
		assertEquals("substitutableElement", attr.getOriginalName());
		assertEquals("SubstitutedType", attr.getType().getName());
		assertTrue(ModelUtils.isRenamedXSDAttribute(attr));
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertNull("Catch-all is wrong", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertEquals(0, attr.getMinOccurs());
		assertEquals(1, attr.getMaxOccurs());
	}

	public void testDuplicateElementWithModelGroup() {
		IClassifier container = (IClassifier) groups.getElementByName("Container1", true);

		assertEquals(1, container.getAttributes(false).size());

		IAttribute attr = container.getAttributeByName("element", false);
		assertNotNull(attr);
		assertEquals("element", attr.getOriginalName());
		assertEquals("string", attr.getType().getName());
		assertFalse(ModelUtils.isRenamedXSDAttribute(attr));
		assertNull("Schema path present", ModelUtils.getXSDPath(attr));
		assertEquals("Catch-all is wrong", "content", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertEquals(1, attr.getMinOccurs());
		assertEquals(IAttribute.UNBOUNDED, attr.getMaxOccurs());
	}

	public void testDuplicateElementsWithNonEmptyBaseType() throws Exception {
		IClassifier container = (IClassifier) duplicates.getElementByName(
				"DuplicateElementsInheritingFromNonEmptyBaseType", true);
		assertEquals(1, container.getAttributes(false).size());
		assertEquals(2, container.getAttributes(true).size());

		IAttribute attrChild = container.getAttributeByName("element2", false);
		assertNotNull(attrChild);
		assertEquals("element", attrChild.getOriginalName());
		assertEquals("Catch-all is wrong", "rest", attrChild
				.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertEquals(1, attrChild.getMinOccurs());
		assertEquals(IAttribute.UNBOUNDED, attrChild.getMaxOccurs());

		IAttribute attrParent = container.getAttributeByName("element", true);
		assertNotNull(attrParent);
		assertEquals("element", attrParent.getOriginalName());
		assertNull("Catch all erroneously detected", attrParent
				.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertEquals(1, attrParent.getMinOccurs());
		assertEquals(1, attrParent.getMaxOccurs());
	}

	public void testDuplicateElementsWithEmptyBaseType() throws Exception {
		IClassifier container = (IClassifier) duplicates.getElementByName(
				"DuplicateElementsInheritingFromEmptyBaseType", true);
		assertEquals(1, container.getAttributes(false).size());

		IAttribute attr = container.getAttributeByName("element", false);
		assertNotNull(attr);
		assertEquals("element", attr.getOriginalName());
		assertEquals("Catch-all is wrong", "rest", attr.getUserData(IXSDUserData.JAXB_CATCH_ALL));
		assertEquals(1, attr.getMinOccurs());
		assertEquals(IAttribute.UNBOUNDED, attr.getMaxOccurs());
	}

}
