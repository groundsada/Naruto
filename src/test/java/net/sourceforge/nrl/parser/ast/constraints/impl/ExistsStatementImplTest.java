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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.constraints.IBinaryPredicate;
import net.sourceforge.nrl.parser.ast.constraints.IExistsStatement;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser_NRLConstraintRules.constraint_return;

import org.junit.Test;

/**
 * Unit tests for existence
 * 
 * @author Christian Nentwich
 */
public class ExistsStatementImplTest extends NRLParserTestSupport {

	@Test
	public void testParse() throws Exception {
		NRLActionParser parser = getParserFor("foo is present");

		IExistsStatement stmt = (IExistsStatement) parser.constraint().getTree();
		assertNull(stmt.getConstraint());
		assertNull(stmt.getCount());
		assertNotNull(stmt.getElement());

		parser = getParserFor("2 foo have (1=1)");
		stmt = (IExistsStatement) parser.constraint().getTree();
		assertNotNull(stmt.getConstraint());
		assertNotNull(stmt.getCount());
		assertNotNull(stmt.getElement());

		parser = getParserFor("two foo are present");
		stmt = (IExistsStatement) parser.constraint().getTree();
		assertNull(stmt.getConstraint());
		assertNotNull(stmt.getCount());
		assertNotNull(stmt.getElement());

		parser = getParserFor("foo has (1 = 1)");
		stmt = (IExistsStatement) parser.constraint().getTree();
		assertNotNull(stmt.getConstraint());
		assertTrue(stmt.getConstraint() instanceof IBinaryPredicate);
		assertNull(stmt.getCount());
		assertNotNull(stmt.getElement());

		parser = getParserFor("foo that has (1 = 1)");
		stmt = (IExistsStatement) parser.constraint().getTree();
		assertNotNull(stmt.getConstraint());
		assertTrue(stmt.getConstraint() instanceof IBinaryPredicate);
		assertNull(stmt.getCount());
		assertNotNull(stmt.getElement());

		parser = getParserFor("at least one foo has a bar");
		stmt = (IExistsStatement) parser.constraint().getTree();
		assertNotNull(stmt.getConstraint());
		assertTrue(stmt.getConstraint() instanceof IModelReference);
		assertNotNull(stmt.getCount());
		assertNotNull(stmt.getElement());

		parser = getParserFor("in at least one foo (a bar is present)");
		constraint_return constraint = parser.constraint();
		stmt = (IExistsStatement) constraint.getTree();
		assertNotNull(stmt.getConstraint());
		assertTrue(stmt.getConstraint() instanceof IExistsStatement);
		assertNotNull(stmt.getCount());
		assertNotNull(stmt.getElement());
		stmt = (IExistsStatement) stmt.getConstraint();
		assertNull(stmt.getConstraint());
		assertNull(stmt.getCount());
		assertNotNull(stmt.getElement());

		parser = getParserFor("at least one foo has (at least one bar where x = y)");
		stmt = (IExistsStatement) parser.constraint().getTree();
		assertNotNull(stmt.getConstraint());
		assertTrue(stmt.getConstraint() instanceof IExistsStatement);
		stmt = (IExistsStatement) stmt.getConstraint();
		assertNotNull(stmt.getConstraint());
		assertTrue(stmt.getConstraint() instanceof IBinaryPredicate);
		assertNotNull(stmt.getCount());
		assertNotNull(stmt.getElement());

		parser = getParserFor("at least one foo has (a bar that has (a baz that has x equal to y))");
		constraint = parser.constraint();
		stmt = (IExistsStatement) constraint.getTree();
		assertTrue(stmt.getConstraint() instanceof IExistsStatement);
		assertNotNull(stmt.getCount());
		assertNotNull(stmt.getElement());

		stmt = (IExistsStatement) stmt.getConstraint();
		assertNotNull(stmt.getConstraint());
		assertNull(stmt.getCount());
		assertNotNull(stmt.getElement());

		stmt = (IExistsStatement) stmt.getConstraint();
		assertTrue(stmt.getConstraint() instanceof IBinaryPredicate);
		assertNull(stmt.getCount());
		assertNotNull(stmt.getElement());
	}
}
