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
import net.sourceforge.nrl.parser.ast.constraints.IBinaryOperatorStatement;
import net.sourceforge.nrl.parser.ast.constraints.IPredicate;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

/**
 * Unit tests for binary operators.
 * 
 * @author Christian Nentwich
 */
public class BinaryOperatorStatementImplTest extends NRLParserTestSupport {

	/**
	 * Test the and construct.
	 * 
	 */
	@Test
	public void testParseAnd() throws Exception {
		NRLActionParser parser = getParserFor("foo = 'A' and bar = 'B'");

		IBinaryOperatorStatement and = (IBinaryOperatorStatement) parser.constraint().getTree();
		assertTrue(and.getLeft() instanceof IPredicate);
		assertTrue(and.getRight() instanceof IPredicate);
		assertTrue(and.getOperator() == IBinaryOperatorStatement.Operator.AND);
	}

	/**
	 * Test the or construct.
	 */
	@Test
	public void testParseOr() throws Exception {
		NRLActionParser parser = getParserFor("foo = 'A' or bar = 'B'");

		IBinaryOperatorStatement or = (IBinaryOperatorStatement) parser.constraint().getTree();
		assertTrue(or.getLeft() instanceof IPredicate);
		assertTrue(or.getRight() instanceof IPredicate);
		assertEquals(IBinaryOperatorStatement.Operator.OR, or.getOperator());
	}

	/**
	 * Test the implies construct.
	 */
	@Test
	public void testParseImplies() throws Exception {
		NRLActionParser parser = getParserFor("foo = 'A' implies bar = 'B'");

		IBinaryOperatorStatement implies = (IBinaryOperatorStatement) parser.constraint().getTree();
		assertTrue(implies.getLeft() instanceof IPredicate);
		assertTrue(implies.getRight() instanceof IPredicate);
		assertEquals(IBinaryOperatorStatement.Operator.IMPLIES, implies.getOperator());
	}

	/**
	 * Test the iff construct.
	 */
	@Test
	public void testParseIff() throws Exception {
		NRLActionParser parser = getParserFor("foo = 'A' only if bar = 'B'");

		IBinaryOperatorStatement iff = (IBinaryOperatorStatement) parser.constraint().getTree();
		assertTrue(iff.getLeft() instanceof IPredicate);
		assertTrue(iff.getRight() instanceof IPredicate);
		assertEquals(IBinaryOperatorStatement.Operator.IFF, iff.getOperator());
	}

	/**
	 * Test parsing of nested expressions.
	 */
	@Test
	public void testParseNested() throws Exception {
		NRLActionParser parser = getParserFor("(foo = 'A' or bar = 'B') and baz = 'C'");

		IBinaryOperatorStatement and = (IBinaryOperatorStatement) parser.constraint().getTree();

		assertTrue(and.getLeft() instanceof IBinaryOperatorStatement);
		assertTrue(and.getRight() instanceof IPredicate);
		assertEquals(IBinaryOperatorStatement.Operator.AND, and.getOperator());
		assertEquals(IBinaryOperatorStatement.Operator.OR, ((IBinaryOperatorStatement) and
				.getLeft()).getOperator());

		// Same without parentheses
		parser = getParserFor("foo = 'A' or bar = 'B' and baz = 'C'");

		IBinaryOperatorStatement or = (IBinaryOperatorStatement) parser.constraint().getTree();

		assertTrue(or.getLeft() instanceof IPredicate);
		assertTrue(or.getRight() instanceof IBinaryOperatorStatement);
		assertEquals(IBinaryOperatorStatement.Operator.OR, or.getOperator());
		assertEquals(IBinaryOperatorStatement.Operator.AND, ((IBinaryOperatorStatement) or
				.getRight()).getOperator());

		parser = getParserFor("(((foo = 'A' or bar = 'B') and baz = 'C'))");
		parser.constraint();
		assertTrue(parser.getSyntaxErrors().isEmpty());

		parser = getParserFor("(foo and bar)");
		parser.constraint();
		assertTrue(parser.getSyntaxErrors().isEmpty());
	}

}
