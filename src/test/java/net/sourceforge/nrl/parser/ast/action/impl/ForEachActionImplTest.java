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
import net.sourceforge.nrl.parser.ast.action.ICompoundAction;
import net.sourceforge.nrl.parser.ast.action.IForEachAction;
import net.sourceforge.nrl.parser.ast.constraints.impl.ModelReferenceImpl;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

/**
 * Test the 'for each' action
 * 
 * @author Christian Nentwich
 */
public class ForEachActionImplTest extends NRLParserTestSupport {

	/**
	 * Test parsing
	 */
	@Test
	public void testParse() throws Exception {
		NRLActionParser parser = getParserFor("For each of the trades, set the date to '2005-12-30';");

		IForEachAction foreach = (IForEachAction) parser.simpleAction().getTree();
		assertEquals("trades", ((ModelReferenceImpl) foreach.getCollection()).getOriginalString());
		assertNull(((ForEachActionImpl) foreach).getVariableName());
		assertTrue(foreach.getAction() instanceof ICompoundAction);
	}

	/**
	 * Test parsing where a variable is involved.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testParse_WithVariable() throws Exception {
		NRLActionParser parser = getParserFor("For each \"trade\" in the collection of trades, set trade.date to '2005-12-30';");

		IForEachAction foreach = (IForEachAction) parser.simpleAction().getTree();
		assertEquals("trades", ((ModelReferenceImpl) foreach.getCollection()).getOriginalString());

		assertEquals("trade", ((ForEachActionImpl) foreach).getVariableName());
		assertTrue(foreach.getAction() instanceof ICompoundAction);
	}
}
