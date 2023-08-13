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
package net.sourceforge.nrl.parser.ast.action.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.action.IActionFragmentDeclaration;
import net.sourceforge.nrl.parser.ast.action.ICompoundAction;
import net.sourceforge.nrl.parser.ast.action.ISetAction;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

/**
 * Test action macro declarations
 * 
 * @author Christian Nentwich
 */
public class ActionFragmentDeclarationImplTest extends NRLParserTestSupport {

	/**
	 * Test parsing with a simple parameter
	 */
	@Test
	public void testParseWithSingleParameter() throws Exception {
		NRLActionParser parser = getParserFor("Context: foo (\"f\") Action Fragment \"m1\" Set x to 'a'");

		IActionFragmentDeclaration decl = (IActionFragmentDeclaration) parser.declaration()
				.getTree();
		assertEquals("m1", decl.getId());
		assertEquals("foo", ((ActionFragmentDeclarationImpl) decl).getModelReference("f")
				.getOriginalString());
		assertTrue(decl.getAction() instanceof ICompoundAction);
	}

	/**
	 * Test parsing with a simple parameter
	 */
	@Test
	public void testParseWithMultiParameter() throws Exception {
		NRLActionParser parser = getParserFor("Context: foo (\"f\"), bar (\"g\"), baz (\"h\") Action Fragment \"m1\" Set x to 'a'");

		IActionFragmentDeclaration decl = (IActionFragmentDeclaration) parser.declaration()
				.getTree();

		assertEquals("m1", decl.getId());
		assertEquals(3, decl.getContextNames().size());
		assertEquals("foo", ((ActionFragmentDeclarationImpl) decl).getModelReference("f")
				.getOriginalString());
		assertEquals("bar", ((ActionFragmentDeclarationImpl) decl).getModelReference("g")
				.getOriginalString());
		assertEquals("baz", ((ActionFragmentDeclarationImpl) decl).getModelReference("h")
				.getOriginalString());

		assertTrue(decl.getAction() instanceof ICompoundAction);
		assertTrue(((ICompoundAction) decl.getAction()).getSimpleActions().get(0) instanceof ISetAction);
	}

}
