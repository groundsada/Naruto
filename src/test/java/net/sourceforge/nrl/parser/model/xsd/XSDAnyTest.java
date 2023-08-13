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
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.model.IPackage;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;

/**
 * Test loading of complex types.
 * 
 * @author Christian Nentwich
 */
public class XSDAnyTest extends TestCase {

	private static boolean isInitialised = false;

	private static IPackage model = null;

	@SuppressWarnings("deprecation")
	public void setUp() throws Exception {
		if (!isInitialised) {
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd",
					new XSDResourceFactoryImpl());
			isInitialised = true;

			XSDModelLoader loader = new XSDModelLoader();
			model = loader.load(new File("src/test/resources/schema/xsdany.xsd"));

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

	public void testAnyStrict() {
		IClassifier classifier = (IClassifier) model.getElementByName("AnyStrict", true);
		assertNotNull(classifier);

		assertEquals(2, classifier.getAttributes(false).size());
		assertNotNull(classifier.getAttributeByName("attr", false));
		assertEquals("string", classifier.getAttributeByName("attr", false).getType().getName());

		assertNotNull(classifier.getAttributeByName("any", false));
		assertTrue(classifier.getAttributeByName("any", false).getType() == IModelElement.OBJECT);
	}

	public void testAnyLax() {
		IClassifier classifier = (IClassifier) model.getElementByName("AnyLax", true);
		assertNotNull(classifier);

		assertEquals(2, classifier.getAttributes(false).size());
		assertNotNull(classifier.getAttributeByName("attr", false));
		assertEquals("string", classifier.getAttributeByName("attr", false).getType().getName());

		assertNotNull(classifier.getAttributeByName("any", false));
		assertTrue(classifier.getAttributeByName("any", false).getType() == IModelElement.OBJECT);
	}

	public void testAnySkip() {
		IClassifier classifier = (IClassifier) model.getElementByName("AnySkip", true);
		assertNotNull(classifier);

		assertEquals(1, classifier.getAttributes(false).size());
		assertNotNull(classifier.getAttributeByName("attr", false));
		assertEquals("string", classifier.getAttributeByName("attr", false).getType().getName());
	}

	public void testAnyRepeating() {
		IClassifier classifier = (IClassifier) model.getElementByName("ManyAny", true);
		assertNotNull(classifier);

		assertEquals(2, classifier.getAttributes(false).size());
		assertNotNull(classifier.getAttributeByName("attr", false));
		assertEquals("string", classifier.getAttributeByName("attr", false).getType().getName());

		assertNotNull(classifier.getAttributeByName("any", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("any", false)
				.getMaxOccurs());
		assertTrue(classifier.getAttributeByName("any", false).getType() == IModelElement.OBJECT);
	}

}
