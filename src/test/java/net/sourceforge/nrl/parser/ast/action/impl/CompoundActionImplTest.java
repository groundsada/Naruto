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
import net.sourceforge.nrl.parser.ast.action.ICompoundAction;
import net.sourceforge.nrl.parser.ast.action.ISetAction;
import net.sourceforge.nrl.parser.ast.constraints.ILiteralString;
import net.sourceforge.nrl.parser.ast.constraints.impl.ModelReferenceImpl;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

/**
 * Test compound actions (lists of actions)
 * 
 * @author Christian Nentwich
 */
public class CompoundActionImplTest extends NRLParserTestSupport {

	@Test
	public void testParse() throws Exception {
		NRLActionParser parser = getParserFor("Set foo to 'abc', Set bar to 'cde';");

		ICompoundAction comp = (ICompoundAction) parser.compoundAction().getTree();
		assertEquals(2, comp.getSimpleActions().size());

		ISetAction set = (ISetAction) comp.getSimpleActions().get(0);

		assertEquals("foo", ((ModelReferenceImpl) set.getTarget()).getOriginalString());
		assertTrue(set.getExpression() instanceof ILiteralString);

		set = (ISetAction) comp.getSimpleActions().get(1);

		assertEquals("bar", ((ModelReferenceImpl) set.getTarget()).getOriginalString());
		assertTrue(set.getExpression() instanceof ILiteralString);
	}

	/**
	 * Test how actions separated by semi-colons parse.
	 * 
	 * This tests a case associated with issue NRL-14.
	 */
	@Test
	public void testParseWithSemicolons() throws Exception {
		NRLActionParser parser = getParserFor("Set foo to 'abc'; Set bar to 'cde';");
		parser.singleActionAndEOF();
		assertTrue(parser.getSyntaxErrors().size() > 0);
		assertEquals(1, parser.getSyntaxErrors().get(0).getLine());
		assertEquals(16, parser.getSyntaxErrors().get(0).getColumn());
	}
}
