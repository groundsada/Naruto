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
 * Test loading of substitution groups.
 * 
 * @author Christian Nentwich
 */
public class SubstitutionGroupTest extends TestCase {

	private static boolean isInitialised = false;

	private static IPackage model = null;

	@SuppressWarnings("deprecation")
	public void setUp() throws Exception {
		if (!isInitialised) {
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd",
					new XSDResourceFactoryImpl());
			isInitialised = true;

			XSDModelLoader loader = new XSDModelLoader();
			model = loader.load(new File("src/test/resources/schema/substitution.xsd"));
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
	public void testSubstitution() throws Exception {
		// First check the type/element setup
		assertNotNull(model.getElementByName("TheBase", true));
		assertNotNull(model.getElementByName("TheAlternativeA", true));
		assertNotNull(model.getElementByName("TheAlternativeB", true));
		assertNull(model.getElementByName("base", true));
		assertNull(model.getElementByName("alternativeA", true));
		assertNull(model.getElementByName("alternativeB", true));

		IClassifier obj = (IClassifier) model.getElementByName("Usage", true);
		assertNotNull(obj);

		assertEquals(3, obj.getAttributes(false).size());
		assertTrue(((Boolean) obj.getAttributeByName("base", false).getUserData(
				IXSDUserData.SUBSTITUTABLE)).booleanValue());
		assertEquals("TheBase", obj.getAttributeByName("base", false).getType().getName());
		assertEquals("TheAlternativeA", obj.getAttributeByName("alternativeA", false).getType()
				.getName());
		assertEquals("base", ((XSDAttribute) obj.getAttributeByName("alternativeA", false)
				.getUserData(IXSDUserData.SUBSTITUTION_FOR)).getName());
		assertEquals("TheAlternativeB", obj.getAttributeByName("alternativeB", false).getType()
				.getName());
		assertEquals("base", ((XSDAttribute) obj.getAttributeByName("alternativeB", false)
				.getUserData(IXSDUserData.SUBSTITUTION_FOR)).getName());
	}
}
