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

import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.junit.Before;
import org.junit.Test;

/**
 * Package loading test.
 * 
 * @author Christian Nentwich
 */
public class UML2PackageTest extends UMLTestCase {

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
		Package pkg =  (Package) model.getMember("Main");
		assertNotNull(pkg);

		UML2Package umlPkg = new UML2Package(pkg, null);

		List<String> documentation = umlPkg.getDocumentation();
		assertEquals(2, documentation.size());
		assertEquals("Test", documentation.get(0));
		assertEquals("OtherTest", documentation.get(1));
	}

	@Test
	public void testGetName() {
		Package pkg =  (Package) model.getMember("Main");
		assertNotNull(pkg);

		UML2Package umlPkg = new UML2Package(pkg, null);

		assertEquals("Main", umlPkg.getName());
		assertEquals("Main", umlPkg.getOriginalName());
	}

	@Test
	public void testGetNameWithUnderscores() {
		Package pkg =  (Package) model.getMember("package_with_underscores");
		assertNotNull(pkg);

		UML2Package umlPkg = new UML2Package(pkg, null);

		assertEquals("package_with_underscores", umlPkg.getName());
		assertEquals("package_with_underscores", umlPkg.getOriginalName());
	}

}
