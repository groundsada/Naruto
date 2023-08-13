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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import net.sourceforge.nrl.parser.model.IAttribute;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.junit.Before;
import org.junit.Test;

/**
 * Attribute loading test.
 * 
 * @author Christian Nentwich
 */
public class UML2AttributeTest extends UMLTestCase {

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
		// Look up the Trade class
		Classifier classifier = (Classifier) ((Package) model.getMember("Main")).getMember("Trade");
		assertNotNull(classifier);

		UML2Attribute attr = new UML2Attribute(classifier.getAttribute("id", null));
		assertEquals(1, attr.getDocumentation().size());
		assertEquals("ATest", attr.getDocumentation().get(0));
	}

	@Test
	public void testGetName() {
		// Look up the Trade class
		Classifier classifier = (Classifier) ((Package) model.getMember("Main")).getMember("Trade");
		assertNotNull(classifier);

		UML2Attribute attr = new UML2Attribute(classifier.getAttribute("id", null));
		assertEquals("id", attr.getName());
		assertEquals("id", attr.getOriginalName());
	}

	@Test
	public void testGetNameWithDots() {
		// Look up the Trade class
		Classifier classifier = (Classifier) ((Package) model.getMember("Main")).getMember("Trade");
		assertNotNull(classifier);

		UML2Attribute attr = new UML2Attribute(classifier.getAttribute("attribute.with.dots", null));
		assertEquals("attributeWithDots", attr.getName());
		assertEquals("attribute.with.dots", attr.getOriginalName());
	}

	@Test
	public void testGetNameWithUnderscores() {
		Classifier classifier = (Classifier) ((Package) model.getMember("Main")).getMember("Trade");
		UML2Attribute attr = new UML2Attribute(classifier.getAttribute("attribute_with_underscore", null));
		assertEquals("attribute_with_underscore", attr.getName());
		assertEquals("attribute_with_underscore", attr.getOriginalName());
	}

	@Test
	public void testGetMinOccurs() {
		// Look up the Trade class
		Classifier classifier = (Classifier) ((Package) model.getMember("Main")).getMember("Trade");
		assertNotNull(classifier);

		Property umlAttribute = classifier.getAttribute("id", null);
		UML2Attribute attr = new UML2Attribute(umlAttribute);
		assertEquals(umlAttribute.getLower(), attr.getMinOccurs());
	}

	@Test
	public void testGetMaxOccurs() {
		// Look up the Trade class
		Classifier classifier = (Classifier) ((Package) model.getMember("Main")).getMember("Trade");
		assertNotNull(classifier);

		Property attribute = classifier.getAttribute("id", null);
		UML2Attribute attr = new UML2Attribute(attribute);
		assertEquals(attribute.getUpper(), attr.getMaxOccurs());
	}

	@Test
	public void testGetUML2Attribute() {
		Classifier classifier = (Classifier) ((Package) model.getMember("Main")).getMember("Trade");
		assertNotNull(classifier);

		UML2Attribute attr = new UML2Attribute(classifier.getAttribute("id", null));
		assertNotNull(attr.getUserData(IUML2UserData.UML2_ELEMENT));
		assertTrue(attr.getUserData(IUML2UserData.UML2_ELEMENT) instanceof NamedElement);
	}
	
	@Test
	public void explicitArityInConstrutorOverridesUMLElementArity() throws Exception {
		Classifier classifier = (Classifier) ((Package) model.getMember("Main")).getMember("Trade");
		assertNotNull(classifier);

		UML2Attribute attr = new UML2Attribute(classifier.getAttribute("id", null), 4, -1);
		assertEquals(4, attr.getMinOccurs());
		assertEquals(IAttribute.UNBOUNDED, attr.getMaxOccurs());
	}
}
