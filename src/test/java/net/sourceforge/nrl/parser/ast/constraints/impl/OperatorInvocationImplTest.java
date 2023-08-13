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
package net.sourceforge.nrl.parser.ast.constraints.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.constraints.IIntegerNumber;
import net.sourceforge.nrl.parser.ast.constraints.ILiteralString;
import net.sourceforge.nrl.parser.ast.constraints.IOperatorInvocation;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

/**
 * Unit tests for opreator invocation
 * 
 * @author Christian Nentwich
 */
public class OperatorInvocationImplTest extends NRLParserTestSupport {

	@Test
	public void testParse() throws Exception {
		NRLActionParser parser = getParserFor("[my op]");

		IOperatorInvocation op = (IOperatorInvocation) parser.constraint().getTree();
		assertEquals("my op", op.getOperatorName());
		assertEquals(0, op.getNumParameters());

		// One parameter, post-fix
		parser = getParserFor("[my op] modelelement");

		op = (IOperatorInvocation) parser.constraint().getTree();
		assertEquals(1, op.getNumParameters());
		assertTrue(op.getParameter(0) instanceof IModelReference);

		IModelReference ref = (IModelReference) op.getParameter(0);
		assertEquals("modelelement", ref.getOriginalString());
		assertEquals(1, ref.getLine());
		assertEquals(8, ref.getColumn());

		// Two parameters, infix
		parser = getParserFor("5 [my op] 3");

		op = (IOperatorInvocation) parser.constraint().getTree();
		assertEquals(2, op.getNumParameters());
		assertTrue(op.getParameter(0) instanceof IIntegerNumber);
		assertTrue(op.getParameter(1) instanceof IIntegerNumber);

		// Several parameters, post-fix
		parser = getParserFor("[bond identified by] 'ABCDE' and 'CDEFG' using system");

		op = (IOperatorInvocation) parser.constraint().getTree();
		assertEquals(3, op.getNumParameters());
		assertTrue(op.getParameter(0) instanceof ILiteralString);
		assertTrue(op.getParameter(1) instanceof ILiteralString);
		assertTrue(op.getParameter(2) instanceof IModelReference);

		ILiteralString literal = (ILiteralString) op.getParameter(0);
		assertEquals(1, literal.getLine());
		assertEquals(21, literal.getColumn());

		literal = (ILiteralString) op.getParameter(1);
		assertEquals(1, literal.getLine());
		assertEquals(33, literal.getColumn());

		ref = (IModelReference) op.getParameter(2);
		assertEquals("system", ref.getOriginalString());
		assertEquals(1, ref.getLine());
		assertEquals(47, ref.getColumn());
	}
}
