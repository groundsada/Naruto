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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.constraints.IBinaryPredicate;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

/**
 * Unit tests for global exists statements.
 * 
 * @author Christian Nentwich
 */
public class GlobalExistsStatementImplTest extends NRLParserTestSupport {

	@Test
	public void testParse() throws Exception {
		// Just model reference
		NRLActionParser parser = getParserFor("There is a Trade");

		GlobalExistsStatementImpl impl = (GlobalExistsStatementImpl) parser.constraint().getTree();
		assertEquals("Trade", ((ModelReferenceImpl) impl.getModelReference()).getOriginalString());
		assertEquals(1, impl.getCount());
		assertTrue(!impl.hasConstraint());
		assertNull(impl.getConstraint());
		assertNull(impl.getVariableName());

		// Model reference + "no"
		parser = getParserFor("There is no Trade");

		impl = (GlobalExistsStatementImpl) parser.constraint().getTree();
		assertEquals("Trade", ((ModelReferenceImpl) impl.getModelReference()).getOriginalString());
		assertEquals(0, impl.getCount());
		assertTrue(!impl.hasConstraint());
		assertNull(impl.getConstraint());
		assertNull(impl.getVariableName());

		// Model reference + variable
		parser = getParserFor("There is a Trade (the \"trade\")");
		impl = (GlobalExistsStatementImpl) parser.constraint().getTree();

		assertEquals("Trade", ((ModelReferenceImpl) impl.getModelReference()).getOriginalString());
		assertEquals(1, impl.getCount());
		assertTrue(!impl.hasConstraint());
		assertNull(impl.getConstraint());
		assertEquals("trade", impl.getVariableName());

		// Model reference + variable + constraint
		parser = getParserFor("There is a Trade (the \"trade\") where trade.date = '2005-12-30'");
		impl = (GlobalExistsStatementImpl) parser.constraint().getTree();

		assertEquals("Trade", ((ModelReferenceImpl) impl.getModelReference()).getOriginalString());
		assertEquals(1, impl.getCount());
		assertTrue(impl.hasConstraint());
		assertTrue(impl.getConstraint() instanceof IBinaryPredicate);
		assertEquals("trade", impl.getVariableName());
	}
}
