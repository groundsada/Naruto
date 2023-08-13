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
import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.constraints.IArithmeticExpression;
import net.sourceforge.nrl.parser.ast.constraints.ILiteralString;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

/**
 * Test action macro application parsing.
 * 
 * @author Christian Nentwich
 */
public class ActionFragmentApplicationActionImplTest extends NRLParserTestSupport {

	/**
	 * Test parsing.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testParse() throws Exception {
		// Test without model reference
		NRLActionParser parser = getParserFor("{foo} 'x'");

		ActionFragmentApplicationActionImpl action = (ActionFragmentApplicationActionImpl) parser
				.simpleAction().getTree();
		assertEquals("foo", action.getActionFragmentId());
		assertNull(action.getFragment());
		assertEquals(1, action.getParameters().size());
		assertTrue(action.getParameters().get(0) instanceof ILiteralString);

		// Test with model reference and expression
		parser = getParserFor("{foo} target and 2+3");

		action = (ActionFragmentApplicationActionImpl) parser.simpleAction().getTree();
		assertEquals("foo", action.getActionFragmentId());
		assertNull(action.getFragment());
		assertTrue(action.getParameters().get(0) instanceof IModelReference);
		assertTrue(action.getParameters().get(1) instanceof IArithmeticExpression);
	}
}
