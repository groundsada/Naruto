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
import net.sourceforge.nrl.parser.ast.constraints.IArithmeticExpression;
import net.sourceforge.nrl.parser.ast.constraints.IDecimalNumber;
import net.sourceforge.nrl.parser.ast.constraints.IIntegerNumber;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

/**
 * Unit tests for arithmetic expressions.
 * 
 * @author Christian Nentwich
 */
public class ArithmeticExpressionImplTest extends NRLParserTestSupport {

	/**
	 * Test +
	 */
	@Test
	public void testParsePlus() throws Exception {
		NRLActionParser parser = getParserFor("foo + 5");

		IArithmeticExpression expr = (IArithmeticExpression) parser.constraint().getTree();
		assertTrue(expr.getLeft() instanceof IModelReference);
		assertTrue(expr.getRight() instanceof IIntegerNumber);
		assertEquals(IArithmeticExpression.Operator.PLUS, expr.getOperator());
	}

	/**
	 * Test -
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testParseMinus() throws Exception {
		NRLActionParser parser = getParserFor("foo - 5.0");

		IArithmeticExpression expr = (IArithmeticExpression) parser.constraint().getTree();
		assertTrue(expr.getLeft() instanceof IModelReference);
		assertTrue(expr.getRight() instanceof IDecimalNumber);
		assertEquals(IArithmeticExpression.Operator.MINUS, expr.getOperator());

		// BUG test: Unary minus parsing
		parser = getParserFor("4-3");
		expr = (IArithmeticExpression) parser.constraint().getTree();
		assertEquals(4, ((IIntegerNumber) expr.getLeft()).getNumber());
		assertEquals(3, ((IIntegerNumber) expr.getRight()).getNumber());

		parser = getParserFor("-4-3");

		expr = (IArithmeticExpression) parser.constraint().getTree();
		assertEquals(-4, ((IIntegerNumber) expr.getLeft()).getNumber());
		assertEquals(3, ((IIntegerNumber) expr.getRight()).getNumber());

		parser = getParserFor("-4.2--3.2");
		expr = (IArithmeticExpression) parser.constraint().getTree();

		assertEquals(-4.2, ((IDecimalNumber) expr.getLeft()).getNumber(), 0);
		assertEquals(-3.2, ((IDecimalNumber) expr.getRight()).getNumber(), 0);
	}

	/**
	 * Test *
	 */
	@Test
	public void testParseTimes() throws Exception {
		NRLActionParser parser = getParserFor("foo * 5");

		IArithmeticExpression expr = (IArithmeticExpression) parser.constraint().getTree();

		assertTrue(expr.getLeft() instanceof IModelReference);
		assertTrue(expr.getRight() instanceof IIntegerNumber);
		assertEquals(IArithmeticExpression.Operator.TIMES, expr.getOperator());
	}

	/**
	 * Test /
	 */
	@Test
	public void testParseDiv() throws Exception {
		NRLActionParser parser = getParserFor("foo / 5.2");

		IArithmeticExpression expr = (IArithmeticExpression) parser.constraint().getTree();

		assertTrue(expr.getLeft() instanceof IModelReference);
		assertTrue(expr.getRight() instanceof IDecimalNumber);
		assertEquals(IArithmeticExpression.Operator.DIV, expr.getOperator());
	}

	/**
	 * Test mod
	 */
	@Test
	public void testParseMod() throws Exception {
		NRLActionParser parser = getParserFor("foo mod 5");
		IArithmeticExpression expr = (IArithmeticExpression) parser.constraint().getTree();

		assertTrue(expr.getLeft() instanceof IModelReference);
		assertTrue(expr.getRight() instanceof IIntegerNumber);
		assertEquals(IArithmeticExpression.Operator.MOD, expr.getOperator());
	}

	/**
	 * Test parentheses and nesting
	 */
	@Test
	public void testParseNested() throws Exception {
		NRLActionParser parser = getParserFor("5+4/3");
		IArithmeticExpression expr = (IArithmeticExpression) parser.constraint().getTree();

		assertTrue(expr.getLeft() instanceof IIntegerNumber);
		assertTrue(expr.getRight() instanceof IArithmeticExpression);
		assertEquals(IArithmeticExpression.Operator.PLUS, expr.getOperator());
		assertEquals(IArithmeticExpression.Operator.DIV, ((IArithmeticExpression) expr.getRight())
				.getOperator());

		// Same but with parentheses
		parser = getParserFor("(5+4)/3");
		expr = (IArithmeticExpression) parser.constraint().getTree();

		assertTrue(expr.getLeft() instanceof IArithmeticExpression);
		assertTrue(expr.getRight() instanceof IIntegerNumber);
		assertEquals(IArithmeticExpression.Operator.DIV, expr.getOperator());
		assertEquals(IArithmeticExpression.Operator.PLUS, ((IArithmeticExpression) expr.getLeft())
				.getOperator());

		// Prevent a mish-mash of constraints and expressions - should
		// be an error
		parser = getParserFor("(aa and b) + 3");
		parser.constraint();
		assertTrue(parser.getSyntaxErrors().size() > 0);
		System.err.println(parser.getSyntaxErrors().get(0));

		parser = getParserFor("5 + (4<3)");
		parser.constraint();
		assertTrue(parser.getSyntaxErrors().size() > 0);
		System.err.println(parser.getSyntaxErrors().get(0));

		parser = getParserFor("5 * (4<3)");
		parser.constraint();
		assertTrue(parser.getSyntaxErrors().size() > 0);
		System.err.println(parser.getSyntaxErrors().get(0));
	}
}
