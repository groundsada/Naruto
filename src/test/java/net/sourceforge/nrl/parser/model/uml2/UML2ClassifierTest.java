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
package net.sourceforge.nrl.parser.model.uml2;

import static org.junit.Assert.*;

import net.sourceforge.nrl.parser.model.IModelElement;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Package;
import org.junit.Before;
import org.junit.Test;

/**
 * Classifier loading test.
 * 
 * @author Christian Nentwich
 */
public class UML2ClassifierTest extends UMLTestCase {

	// The loaded XMI model (filled in by setup)
	static Model model = null;

	@Before
	public void setUp() throws Exception {

		if (model == null) {
			Resource res = loadXMI("src/test/resources/uml/basicmodel with underscores.uml");
			assertEquals(1, res.getContents().size());
			model = (Model) res.getContents().get(0);
		}
	}

	@Test
	public void testGetDocumentation() {
		Classifier classifier = (Classifier) ((Package) model.getMember("Main")).getMember("Trade");
		assertNotNull(classifier);

		UML2Classifier trade = new UML2Classifier(classifier, null);
		assertEquals(1, trade.getDocumentation().size());
		assertEquals("CTest", trade.getDocumentation().get(0));
	}

	@Test
	public void testGetElementName() {
		Classifier classifier = (Classifier) ((Package) model.getMember("Main")).getMember("Trade");
		assertNotNull(classifier);

		UML2Classifier trade = new UML2Classifier(classifier, null);
		assertEquals(IModelElement.ElementType.Classifier, trade.getElementType());
	}

	@Test
	public void testGetName() {
		// Look up the Trade class
		Classifier classifier = (Classifier) ((Package) model.getMember("Main")).getMember("Trade");
		assertNotNull(classifier);

		UML2Classifier trade = new UML2Classifier(classifier, null);
		assertEquals("Trade", trade.getName());
		assertEquals("Trade", trade.getOriginalName());
	}

	@Test
	public void testGetNameWithDots() {
		Classifier classifier = (Classifier) ((Package) model.getMember("Main")).getMember("Class.with.dots");
		assertNotNull(classifier);

		UML2Classifier classWithDots = new UML2Classifier(classifier, null);
		assertEquals("ClassWithDots", classWithDots.getName());
		assertEquals("Class.with.dots", classWithDots.getOriginalName());
	}

	@Test
	public void testGetNameWithUnderscores() {
		Classifier classifier = (Classifier) ((Package) model.getMember("Main")).getMember("Class_with_underscores");
		assertNotNull(classifier);

		UML2Classifier classWithUnderscores = new UML2Classifier(classifier, null);
		assertEquals("Class_with_underscores", classWithUnderscores.getName());
		assertEquals("Class_with_underscores", classWithUnderscores.getOriginalName());
	}

	@Test
	public void testGetAttributeByName() {
		// Look up the Trade class
		Classifier classifier = (Classifier) ((Package) model.getMember("Main")).getMember("Trade");

		UML2Classifier trade = new UML2Classifier(classifier, null);
		trade.addAttribute(new UML2Attribute(classifier.getAttribute("id", null)));
		assertNotNull(trade.getAttributeByName("id", false));
		assertEquals("id", trade.getAttributeByName("id", false).getName());
	}

	@Test
	public void testGetAttributes() {
		// Look up the Trade class
		Classifier classifier = (Classifier) ((Package) model.getMember("Main")).getMember("Trade");

		UML2Classifier trade = new UML2Classifier(classifier, null);
		trade.addAttribute(new UML2Attribute(classifier.getAttribute("id", null)));
		assertEquals(1, trade.getAttributes(false).size());
	}

	@Test
	public void testGetUML2Classifier() {
		Classifier classifier = (Classifier) ((Package) model.getMember("Main")).getMember("Trade");

		UML2Classifier trade = new UML2Classifier(classifier, null);
		assertNotNull(trade.getUserData(IUML2UserData.UML2_ELEMENT));
		assertTrue(trade.getUserData(IUML2UserData.UML2_ELEMENT) instanceof NamedElement);
	}
}
