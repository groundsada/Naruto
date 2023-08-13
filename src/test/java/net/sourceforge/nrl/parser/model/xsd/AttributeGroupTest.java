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
 * Test loading of attribute groups.
 * 
 * @author Christian Nentwich
 */
public class AttributeGroupTest extends TestCase {

	private static boolean isInitialised = false;

	private static IPackage model = null;

	@SuppressWarnings("deprecation")
	public void setUp() throws Exception {
		if (!isInitialised) {
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd",
					new XSDResourceFactoryImpl());
			isInitialised = true;

			XSDModelLoader loader = new XSDModelLoader();
			model = loader.load(new File("src/test/resources/schema/attributegroup.xsd"));

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

	public void testAttributeGroup() throws Exception {
		IClassifier usage = (IClassifier) model.getElementByName("Usage", true);

		assertNotNull(usage.getAttributeByName("elemB", false));
		assertEquals(0, usage.getAttributeByName("elemB", false).getMinOccurs());

		assertNotNull(usage.getAttributeByName("elemA", false));
		assertEquals(1, usage.getAttributeByName("elemA", false).getMinOccurs());

		assertNotNull(usage.getAttributeByName("attrA", false));
		assertEquals(1, usage.getAttributeByName("attrA", false).getMinOccurs());

		assertNotNull(usage.getAttributeByName("attrB", false));
		assertEquals(1, usage.getAttributeByName("attrB", false).getMinOccurs());
	}

	public void testAttributeGroupWithInheritance() throws Exception {
		IClassifier usage = (IClassifier) model.getElementByName("Extended", true);

		assertEquals(5, usage.getAttributes(true).size());

		assertNull(usage.getAttributeByName("elemB", false));
		assertNotNull(usage.getAttributeByName("elemB", true));
		assertEquals(0, usage.getAttributeByName("elemB", true).getMinOccurs());

		assertNull(usage.getAttributeByName("elemA", false));
		assertNotNull(usage.getAttributeByName("elemA", true));
		assertEquals(1, usage.getAttributeByName("elemA", true).getMinOccurs());

		assertNull(usage.getAttributeByName("attrA", false));
		assertNotNull(usage.getAttributeByName("attrA", true));
		assertEquals(1, usage.getAttributeByName("attrA", true).getMinOccurs());

		assertNull(usage.getAttributeByName("attrB", false));
		assertNotNull(usage.getAttributeByName("attrB", true));
		assertEquals(1, usage.getAttributeByName("attrB", true).getMinOccurs());

		assertNotNull(usage.getAttributeByName("extA", false));
		assertEquals(0, usage.getAttributeByName("extA", false).getMinOccurs());
	}
}
