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
import net.sourceforge.nrl.parser.model.IDataType;
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.model.IPackage;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;

/**
 * Test loading of simple types, derived simple types, and so on. Also tests by comparing the loader
 * against the results provided by the UML loader.
 * 
 * @author Christian Nentwich
 */
public class SimpleTypesTest extends TestCase {

	private static boolean isInitialised = false;

	private static IPackage model = null;

	@SuppressWarnings("deprecation")
	public void setUp() throws Exception {
		if (!isInitialised) {
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd",
					new XSDResourceFactoryImpl());
			isInitialised = true;

			XSDModelLoader loader = new XSDModelLoader();
			model = loader.load(new File("src/test/resources/schema/simpletypes.xsd"));

			for (String warning : loader.getWarnings()) {
				System.err.println(warning);
			}
		}
	}

	/*
	 * Just a double-check that the model loaded
	 */
	public void testLoading() throws Exception {
		assertNotNull(model);
	}

	/*
	 * Load a schema containing all built-in simple types and check that they're all loaded.
	 */
	@SuppressWarnings("deprecation")
	public void testAllBuiltInTypes() throws Exception {
		XSDModelLoader loader = new XSDModelLoader();
		IPackage model = loader.load(new File("src/test/resources/schema/builtin-types.xsd"));
		IClassifier container = (IClassifier) model.getElementByName("SimpleTypes", true);
		assertNotNull(container);

		assertEquals(43, container.getAttributes(false).size());

		for (IAttribute attr : container.getAttributes(false)) {
			assertNotNull(attr.getType());
		}
	}

	/*
	 * Test a simple type derived from a simple type (string)
	 */
	public void testSimpleTypeDerivation() throws Exception {
		IDataType dataType = (IDataType) model.getElementByName("DerivedString", true);

		assertNotNull(dataType);
		assertEquals(IModelElement.ElementType.DataType, dataType.getElementType());
		assertEquals("DerivedString", dataType.getName());
		assertNotNull(dataType.getParent());
		assertTrue(dataType.getParent() instanceof IDataType);
		assertEquals("string", dataType.getParent().getName());
		assertTrue(dataType.getParent().getDescendants(true).contains(dataType));
	}

	/*
	 * Test a simple type derived transitively from a simple type (string)
	 */
	public void testSimpleTypeDerivationTransitive() throws Exception {
		IDataType dataType = (IDataType) model.getElementByName("DoubleDerivationSimple", true);
		assertEquals(IModelElement.ElementType.DataType, dataType.getElementType());

		assertNotNull(dataType);
		assertEquals("DoubleDerivationSimple", dataType.getName());
		assertNotNull(dataType.getParent());
		assertTrue(dataType.getParent() instanceof IDataType);
		assertEquals("DerivedString", dataType.getParent().getName());
		assertTrue(dataType.getParent() == model.getElementByName("DerivedString", true));
		assertTrue(dataType.getParent().getParent().getDescendants(true).contains(dataType));
	}

	/*
	 * Test an enumeration, as a simple type, derived from a string
	 */
	public void testEnumerationString() throws Exception {
		IClassifier _enum = (IClassifier) model.getElementByName("DerivedEnumeration", true);

		assertNotNull(_enum);
		assertEquals(IModelElement.ElementType.Enumeration, _enum.getElementType());
		assertEquals("DerivedEnumeration", _enum.getName());
		assertEquals(2, _enum.getAttributes(true).size());
		assertTrue(_enum.isEnumeration());
		assertNotNull(_enum.getAttributeByName("VALUEA", false));
		assertNotNull(_enum.getAttributeByName("VALUEB", false));
		assertTrue(_enum.getAttributeByName("VALUEA", false).getType() == _enum);
		assertTrue(_enum.getAttributeByName("VALUEB", false).getType() == _enum);
		assertTrue(_enum.getAttributeByName("VALUEA", false).isStatic());
		assertTrue(_enum.getAttributeByName("VALUEB", false).isStatic());
	}

	/*
	 * Test an enumeration, as a simple type, derived from an integer
	 */
	public void testEnumerationInteger() throws Exception {
		IClassifier _enum = (IClassifier) model.getElementByName("EnumeratedInt", true);

		assertNotNull(_enum);
		assertEquals(IModelElement.ElementType.Enumeration, _enum.getElementType());
		assertEquals("EnumeratedInt", _enum.getName());
		assertEquals(2, _enum.getAttributes(true).size());
		assertTrue(_enum.isEnumeration());
		assertNotNull(_enum.getAttributeByName("1", false));
		assertNotNull(_enum.getAttributeByName("2", false));
		assertTrue(_enum.getAttributeByName("1", false).getType() == _enum);
		assertTrue(_enum.getAttributeByName("2", false).getType() == _enum);
	}

	/*
	 * Test restrictions of enumerations
	 */
	public void testEnumerationRestriction() throws Exception {
		IClassifier _enum = (IClassifier) model.getElementByName("RestrictedEnumeration", true);

		assertNotNull(_enum);
		assertEquals(IModelElement.ElementType.Enumeration, _enum.getElementType());
		assertEquals("RestrictedEnumeration", _enum.getName());
		assertEquals(2, _enum.getAttributes(true).size());
		assertEquals(0, _enum.getAttributes(false).size());
		assertTrue(_enum.isEnumeration());
		assertNotNull(_enum.getAttributeByName("VALUEA", true));
		assertNotNull(_enum.getAttributeByName("VALUEB", true));
	}

	/*
	 * Test an enumeration that also has an attribute
	 */
	public void testEnumerationComplex() throws Exception {
		IClassifier _enum = (IClassifier) model.getElementByName("EnumerationWithAttr", true);

		assertNotNull(_enum);
		assertEquals(IModelElement.ElementType.DataTypeWithAttributes, _enum.getElementType());
		assertEquals("EnumerationWithAttr", _enum.getName());
		assertEquals(3, _enum.getAttributes(true).size());
		assertTrue(_enum.isEnumeration());
		assertTrue(_enum.getAttributeByName("VALUEA", true).isStatic());
		assertTrue(_enum.getAttributeByName("VALUEB", true).isStatic());
		assertTrue(!_enum.getAttributeByName("id", false).isStatic());
		assertNotNull(_enum.getAttributeByName("id", false).getType());
	}

	/*
	 * Check whether data types are created from complex types that inherit from simple
	 */
	public void testDataTypeFromComplex() throws Exception {
		IDataType dataType = (IDataType) model.getElementByName("DerivedAsComplex", true);

		assertNotNull(dataType);
		assertEquals(IModelElement.ElementType.DataTypeWithAttributes, dataType.getElementType());
		assertEquals("DerivedAsComplex", dataType.getName());
		assertNotNull(dataType.getParent());
		assertTrue(dataType.getParent() instanceof IDataType);
		assertEquals("string", dataType.getParent().getName());
		assertTrue(dataType.getParent().getDescendants(true).contains(dataType));

		assertNotNull(dataType.getAttributeByName("id", false));
		assertEquals("id", dataType.getAttributeByName("id", false).getName());
		assertEquals(1, dataType.getAttributeByName("id", false).getMinOccurs());
		assertEquals(1, dataType.getAttributeByName("id", false).getMaxOccurs());
		assertTrue(!dataType.getAttributeByName("id", false).isStatic());

		IClassifier usage = (IClassifier) model.getElementByName("Usage", true);
		assertNotNull(usage);
		assertNotNull(usage.getAttributeByName("derivedComplex", false));
		assertEquals("DerivedAsComplex", usage.getAttributeByName("derivedComplex", false)
				.getType().getName());
	}

	/*
	 * Check whether data types are created from complex types that inherit from simple
	 */
	public void testDataTypeFromComplexAnonymous() throws Exception {
		IClassifier classifier = (IClassifier) model.getElementByName("AnonymousComplexDerivUsage",
				true);
		assertNotNull(classifier);

		IAttribute attr = classifier.getAttributeByName("derivedSimpleAnonymous", false);
		assertNotNull(attr);
		assertNotNull(attr.getType().getName());
		assertEquals(IModelElement.ElementType.DataTypeWithAttributes, attr.getType()
				.getElementType());

		IDataType dataType = (IDataType) attr.getType().getParent();
		assertEquals("anyURI", dataType.getName());

		assertNotNull(attr.getType().getUserData(IXSDUserData.CONTAINING_TYPE));
		assertTrue(attr.getType().getUserData(IXSDUserData.CONTAINING_TYPE) == classifier);

		attr = classifier.getAttributeByName("derivedSimpleAnonymous2", false);
		assertNotNull(attr);
		assertNotNull(attr.getType().getName());
		assertTrue(attr.getType() instanceof XSDDataType);
		assertEquals(IModelElement.ElementType.DataTypeWithAttributes, attr.getType()
				.getElementType());

		dataType = (IDataType) attr.getType();
		assertEquals("anyURI", dataType.getParent().getName());

		assertNotNull(attr.getType().getUserData(IXSDUserData.CONTAINING_TYPE));
		assertTrue(attr.getType().getUserData(IXSDUserData.CONTAINING_TYPE) == classifier);

		assertNotNull(dataType.getAttributeByName("listAttr", true));
		assertEquals("string", dataType.getAttributeByName("listAttr", true).getType().getName());
	}

	/*
	 * Check whether data types are created from complex types that inherit from other complex
	 * types, with the root of the hierarchy being a simple type.
	 */
	public void testDataTypeFromComplexHierarchy() throws Exception {
		IDataType dataType = (IDataType) model.getElementByName("DoubleDerivation", true);

		assertNotNull(dataType);
		assertEquals(IModelElement.ElementType.DataTypeWithAttributes, dataType.getElementType());
		assertEquals("DoubleDerivation", dataType.getName());
		assertNotNull(dataType.getParent());
		assertTrue(dataType.getParent() instanceof IDataType);
		assertNotNull(dataType.getParent().getParent());
		assertEquals("DerivedAsComplex", dataType.getParent().getName());
		assertEquals("string", dataType.getParent().getParent().getName());
		assertTrue(dataType.getParent().getDescendants(true).contains(dataType));

		// 'id' is not directly in this, but is inherited
		assertNull(dataType.getAttributeByName("id", false));
		assertNotNull(dataType.getAttributeByName("id", true));

		assertEquals("id", dataType.getAttributeByName("id", true).getName());
		assertEquals(1, dataType.getAttributeByName("id", true).getMinOccurs());
		assertEquals(1, dataType.getAttributeByName("id", true).getMaxOccurs());
		assertTrue(!dataType.getAttributeByName("id", true).isStatic());

		// 'id2' is directly in this
		assertNotNull(dataType.getAttributeByName("id2", false));

		assertEquals("id2", dataType.getAttributeByName("id2", false).getName());
		assertEquals(0, dataType.getAttributeByName("id2", false).getMinOccurs());
		assertEquals(1, dataType.getAttributeByName("id2", false).getMaxOccurs());
		assertTrue(!dataType.getAttributeByName("id2", false).isStatic());
	}

	/*
	 * Test anonymous type extension on an element
	 */
	public void testAnonymousTypeOnElement() throws Exception {
		IClassifier usage = (IClassifier) model.getElementByName("Usage", true);
		assertNotNull(usage);

		IAttribute attr = usage.getAttributeByName("anonymousB", false);
		assertNotNull(attr);
		assertEquals("string", attr.getType().getName());

		attr = usage.getAttributeByName("anonymousC", false);
		assertNotNull(attr);
		assertTrue(((IClassifier) attr.getType()).isEnumeration());
		assertEquals("DerivedEnumeration", attr.getType().getName());
	}

	/*
	 * Test anonymous type extension on an attribute
	 */
	public void testAnonymousTypeOnAttribute() throws Exception {
		IClassifier usage = (IClassifier) model.getElementByName("Usage", true);
		assertNotNull(usage);

		IAttribute attr = usage.getAttributeByName("anonymousA", false);
		assertNotNull(attr);
		assertEquals("string", attr.getType().getName());
	}

	/*
	 * Type derived as list (list of strings)
	 */
	public void testListType() throws Exception {
		// The types themselves don't actually get created ...
		assertNull(model.getElementByName("StringList", true));
		assertNull(model.getElementByName("DerivedStringList", true));

		// ... because it will always be primitive types on the attributes anyway. The
		// attributes have to be set to be collections.

		IClassifier usage = (IClassifier) model.getElementByName("Usage", true);
		assertNotNull(usage);

		IAttribute attr = usage.getAttributeByName("stringList", false);
		assertNotNull(attr);
		assertEquals("string", attr.getType().getName());
		assertTrue(attr.isRepeating());

		attr = usage.getAttributeByName("derivedStringList", false);
		assertNotNull(attr);
		assertEquals("string", attr.getType().getName());
		assertTrue(attr.isRepeating());

		attr = usage.getAttributeByName("simpleDerivedStringList", false);
		assertNotNull(attr);
		assertEquals("string", attr.getType().getName());
		assertTrue(attr.isRepeating());

		attr = usage.getAttributeByName("anonymousList", false);
		assertNotNull(attr);
		assertEquals("int", attr.getType().getName());
		assertTrue(attr.isRepeating());

		attr = usage.getAttributeByName("anonymousDerivedList", false);
		assertNotNull(attr);
		assertEquals("string", attr.getType().getName());
		assertTrue(attr.isRepeating());

		attr = usage.getAttributeByName("globalStringList", false);
		assertNotNull(attr);
		assertEquals("string", attr.getType().getName());
		assertTrue(attr.isRepeating());

		attr = usage.getAttributeByName("globalDerivedStringList", false);
		assertNotNull(attr);
		assertEquals("string", attr.getType().getName());
		assertTrue(attr.isRepeating());

		attr = usage.getAttributeByName("derivedAsComplexList", false);
		assertNotNull(attr);
		attr = ((IDataType) attr.getType()).getAttributeByName("list", false);
		assertNotNull(attr);
		assertEquals("anyURI", attr.getType().getName());
		assertTrue(attr.isRepeating());
	}

	/**
	 * Test whether enum values are joined in a union
	 * 
	 * @throws Exception
	 */
	public void testUnionEnum() throws Exception {
		// Not supported
	}

}
