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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.constraints.IBinaryPredicate;
import net.sourceforge.nrl.parser.ast.constraints.IForallStatement;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser_NRLConstraintRules.constraint_return;

import org.junit.Test;

/**
 * Unit tests for universal quantifier
 * 
 * @author Christian Nentwich
 */
public class ForallStatementImplTest extends NRLParserTestSupport {

	@Test
	public void testParse() throws Exception {
		NRLActionParser parser = getParserFor("each foo has abc = cde");

		IForallStatement stmt = (IForallStatement) parser.constraint().getTree();
		assertNotNull(stmt.getConstraint());
		assertNotNull(stmt.getElement());
		assertNull(stmt.getVariable());

		parser = getParserFor("in each foo abc = cde");

		constraint_return constraint = parser.constraint();
		stmt = (IForallStatement) constraint.getTree();
		assertNotNull(stmt.getConstraint());
		assertNotNull(stmt.getElement());
		assertNull(stmt.getVariable());

		parser = getParserFor("for each \"x\" in the collection of foo abc = cde");
		stmt = (IForallStatement) parser.constraint().getTree();
		assertNotNull(stmt.getConstraint());
		assertTrue(stmt.getConstraint() instanceof IBinaryPredicate);
		assertNotNull(stmt.getElement());
		assertEquals("x", ((ForallStatementImpl) stmt).getVariableName());
	}

	@Test
	public void testParseError() throws Exception {
		NRLActionParser parser = getParserFor("for each foo (abc = cde");
		parser.forallStatement();
		assertTrue(parser.getSyntaxErrors().size() > 0);
		assertEquals(1, parser.getSyntaxErrors().get(0).getLine());
		assertEquals(23, parser.getSyntaxErrors().get(0).getColumn());
	}
}
