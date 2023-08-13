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
import net.sourceforge.nrl.parser.ast.constraints.IBinaryPredicate;
import net.sourceforge.nrl.parser.ast.constraints.impl.ModelReferenceImpl;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

/**
 * Test the 'delete' action
 * 
 * @author Christian Nentwich
 */
public class RemoveActionImplTest extends NRLParserTestSupport {

	/**
	 * Test parsing without where clause
	 */
	@Test
	public void testParseWithoutCondition() throws Exception {
		NRLActionParser parser = getParserFor("Remove any foo");

		RemoveActionImpl delete = (RemoveActionImpl) parser.simpleAction().getTree();
		assertEquals("foo", ((ModelReferenceImpl) delete.getTarget()).getOriginalString());
		assertNull(delete.getWhere());
		assertNull(delete.getVariableName());
	}

	/**
	 * Test parsing with condition
	 */
	@Test
	public void testParseWithCondition() throws Exception {
		NRLActionParser parser = getParserFor("Remove any foo (\"x\") where x.a = 'b'");

		RemoveActionImpl delete = (RemoveActionImpl) parser.simpleAction().getTree();
		assertEquals("foo", ((ModelReferenceImpl) delete.getTarget()).getOriginalString());
		assertTrue(delete.getWhere() instanceof IBinaryPredicate);
		assertEquals("x", delete.getVariableName());
	}
}
