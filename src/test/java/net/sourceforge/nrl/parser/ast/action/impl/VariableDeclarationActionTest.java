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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.action.IVariableDeclarationAction;
import net.sourceforge.nrl.parser.ast.constraints.IIntegerNumber;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

/**
 * Test the variable declaration action
 * 
 * @author Christian Nentwich
 */
public class VariableDeclarationActionTest extends NRLParserTestSupport {

	/**
	 * Test parsing
	 */
	@Test
	public void testParseWithoutVariable() throws Exception {
		NRLActionParser parser = getParserFor("\"x\" represents 0");

		IVariableDeclarationAction var = (IVariableDeclarationAction) parser.simpleAction()
				.getTree();

		assertEquals("x", var.getVariableName());
		assertTrue(var.getExpression() instanceof IIntegerNumber);
		// Must be null, not yet resolved
		assertNull(var.getVariableReference());
	}

}
