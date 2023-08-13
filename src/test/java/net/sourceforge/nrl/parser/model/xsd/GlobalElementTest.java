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
import net.sourceforge.nrl.parser.model.IClassifier;
import net.sourceforge.nrl.parser.model.IPackage;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;

/**
 * Test loading of complex types.
 * 
 * @author Christian Nentwich
 */
public class GlobalElementTest extends TestCase {

	private static boolean isInitialised = false;

	private static IPackage model = null;

	@SuppressWarnings("deprecation")
	public void setUp() throws Exception {
		if (!isInitialised) {
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd",
					new XSDResourceFactoryImpl());
			isInitialised = true;

			XSDModelLoader loader = new XSDModelLoader();
			model = loader.load(new File("src/test/resources/schema/globalelements.xsd"));

			// Should be 1 warning because TypeC classes with element of same
			// name
			assertEquals(1, loader.getWarnings().size());
		}
	}

	/*
	 * Just a double-check that the model loaded
	 */
	public void testLoading() throws Exception {
		assertNotNull(model);
	}

	/*
	 * A simple global element should have been turned into a type.
	 */
	public void testSimpleElementWithAnonymousType() throws Exception {
		IClassifier obj = (IClassifier) model.getElementByName("globalElementA", true);
		assertNotNull(obj);
		assertEquals(1, obj.getAttributes(false).size());
		assertEquals("Object", obj.getParent().getName());
		assertNull(obj.getUserData(IXSDUserData.SUBSTITUTABLE));

		obj = (IClassifier) model.getElementByName("globalElementB", true);
		assertNotNull(obj);
		assertEquals("string", obj.getParent().getName());
		assertEquals(0, obj.getAttributes(false).size());
	}

	/*
	 * Test elements with explicitly assigned type
	 */
	public void testElementsWithAssignedType() throws Exception {
		// globalElementC will not exist, because it is just of TypeB anyway!
		IClassifier obj = (IClassifier) model.getElementByName("globalElementC", true);
		assertNull(obj);

		obj = (IClassifier) model.getElementByName("TypeB", true);
		assertNotNull(obj);
		assertEquals(1, obj.getAttributes(false).size());
		assertNull(obj.getUserData(IXSDUserData.SUBSTITUTABLE));

		obj = (IClassifier) model.getElementByName("Usage", true);
		assertNotNull(obj);
		assertNotNull(obj.getAttributeByName("globalElementC", false));
		assertEquals("TypeB", obj.getAttributeByName("globalElementC", false).getType().getName());
	}

	public void testElementWithImport() throws Exception {
		IClassifier obj = (IClassifier) model.getElementByName("Usage", true);
		assertNotNull(obj);
		assertNotNull(obj.getAttributeByName("includeElement", false));
		assertEquals("IncludedType", obj.getAttributeByName("includeElement", false).getType()
				.getName());

		assertNotNull(obj.getAttributeByName("includedElement2", false));
		assertEquals("IncludedType", obj.getAttributeByName("includedElement2", false).getType()
				.getName());
	}

	/*
	 * Anonymous complex type derived from simple
	 */
	public void testElementWithAnonymousDerivedComplex() throws Exception {
		IClassifier obj = (IClassifier) model.getElementByName("globalElementE", true);
		assertNotNull(obj);
		assertEquals(1, obj.getAttributes(false).size());
		assertEquals("test", obj.getAttributes(false).get(0).getName());
		assertEquals("integer", obj.getAttributes(false).get(0).getType().getName());
	}

	/*
	 * Check that references to global elements are handled ok.
	 */
	public void testReferences() throws Exception {
		IClassifier obj = (IClassifier) model.getElementByName("TypeWithReferences", true);
		assertNotNull(obj);

		assertEquals(7, obj.getAttributes(false).size());
		assertEquals("globalElementA", obj.getAttributeByName("globalElementA", true).getType()
				.getName());
		assertEquals("TypeB", obj.getAttributeByName("TypeB", true).getType().getName());
		assertEquals("TypeB", obj.getAttributeByName("globalElementC", true).getType().getName());

		assertEquals("string", obj.getAttributeByName("globalElementD", true).getType().getName());
		assertEquals(XSDBuiltInSimpleTypes.PACKAGE, obj.getAttributeByName("globalElementD", true)
				.getType().getContainingPackage().getName());

		// Check the model group!
		assertEquals("string", obj.getAttributeByName("x", true).getType().getName());
		assertEquals("string", obj.getAttributeByName("y", true).getType().getName());

		// Check element with illegal chars in name
		assertEquals("testElement_element", obj.getAttributeByName("testElement_element", false)
				.getType().getName());
		assertEquals("test.element_element", obj.getAttributeByName("testElement_element", false)
				.getType().getOriginalName());
	}

	/*
	 * Illegal chars
	 */
	public void testIllegalChars() throws Exception {
		IClassifier obj = (IClassifier) model.getElementByName("testElement_element", true);
		assertNotNull(obj);

		assertNotNull(obj.getAttributeByName("elementBCD", true));
		assertEquals("elementB.c.d", obj.getAttributeByName("elementBCD", true).getOriginalName());
	}
}
