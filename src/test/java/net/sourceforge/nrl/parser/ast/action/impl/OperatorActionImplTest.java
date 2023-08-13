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
import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.action.ICompoundAction;
import net.sourceforge.nrl.parser.ast.action.IOperatorAction;
import net.sourceforge.nrl.parser.ast.constraints.IArithmeticExpression;
import net.sourceforge.nrl.parser.ast.constraints.ILiteralString;
import net.sourceforge.nrl.parser.ast.constraints.IOperatorInvocation;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

/**
 * Test the operator action
 * 
 * @author Christian Nentwich
 */
public class OperatorActionImplTest extends NRLParserTestSupport {

	/**
	 * Test parsing
	 */
	@Test
	public void testParseWithoutParam() throws Exception {
		NRLActionParser parser = getParserFor("[i'm your operator]");

		IOperatorAction op = (IOperatorAction) parser.simpleAction().getTree();
		assertEquals("i'm your operator", op.getOperatorName());
		assertEquals(0, op.getParameters().size());
	}

	/**
	 * Test parsing with param
	 */
	@Test
	public void testParseWithOneParam() throws Exception {
		NRLActionParser parser = getParserFor("[i'm your operator] you");

		IOperatorAction op = (IOperatorAction) parser.simpleAction().getTree();
		assertEquals("i'm your operator", op.getOperatorName());
		assertEquals(1, op.getParameters().size());
		assertTrue(op.getParameters().get(0) instanceof IModelReference);

		// try with expression argument
		parser = getParserFor("[test] 'a' + 'b'");

		op = (IOperatorAction) parser.simpleAction().getTree();
		assertEquals(1, op.getParameters().size());
		assertTrue(op.getParameters().get(0) instanceof IArithmeticExpression);
	}

	/**
	 * Test parsing with multiple parameters
	 */
	@Test
	public void testParseWithParams() throws Exception {
		NRLActionParser parser = getParserFor("[send] the Message using 'ABC'");

		IOperatorAction op = (IOperatorAction) parser.simpleAction().getTree();
		assertEquals("send", op.getOperatorName());
		assertEquals(2, op.getParameters().size());
		assertTrue(op.getParameters().get(0) instanceof IModelReference);
		assertTrue(op.getParameters().get(1) instanceof ILiteralString);

		// Try with two operator names
		parser = getParserFor("[send] [receive]");

		ICompoundAction action = (ICompoundAction) parser.action().getTree();
		assertEquals(1, action.getSimpleActions().size());
		assertTrue(action.getSimpleActions().get(0) instanceof IOperatorAction);
		assertEquals(1, ((IOperatorAction) action.getSimpleActions().get(0)).getParameters().size());
		assertTrue(((IOperatorAction) action.getSimpleActions().get(0)).getParameters().get(0) instanceof IOperatorInvocation);
	}

}
