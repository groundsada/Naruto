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
import net.sourceforge.nrl.parser.IStatusCode;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.constraints.IBinaryPredicate;
import net.sourceforge.nrl.parser.ast.constraints.ILiteralString;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;

/**
 * Unit tests for binary predicates.
 * 
 * @author Christian Nentwich
 */
public class BinaryPredicateImplTest extends NRLParserTestSupport {

	/**
	 * Test =
	 */
	@Test
	public void testParseEquals() throws Exception {
		NRLActionParser parser = getParserFor("foo = 'A'");
		IBinaryPredicate pred = (IBinaryPredicate) parser.constraint()
				.getTree();

		assertTrue(pred.getLeft() instanceof IModelReference);
		assertTrue(pred.getRight() instanceof ILiteralString);
		assertTrue(pred.getPredicate() == IBinaryPredicate.Predicate.EQUAL);
	}

	/**
	 * Test &lt;&gt;
	 */
	@Test
	public void testParseNotEquals() throws Exception {
		NRLActionParser parser = getParserFor("foo is not equal to 'A'");
		IBinaryPredicate pred = (IBinaryPredicate) parser.constraint()
				.getTree();

		assertTrue(pred.getLeft() instanceof IModelReference);
		assertTrue(pred.getRight() instanceof ILiteralString);
		assertTrue(pred.getPredicate() == IBinaryPredicate.Predicate.NOT_EQUAL);
	}

	/**
	 * Test &lt;
	 */
	@Test
	public void testParseLess() throws Exception {
		NRLActionParser parser = getParserFor("foo < 'A'");
		IBinaryPredicate pred = (IBinaryPredicate) parser.constraint()
				.getTree();

		assertTrue(pred.getLeft() instanceof IModelReference);
		assertTrue(pred.getRight() instanceof ILiteralString);
		assertTrue(pred.getPredicate() == IBinaryPredicate.Predicate.LESS);
	}

	/**
	 * Test &lt;=
	 */
	@Test
	public void testParseLessOrEqual() throws Exception {
		NRLActionParser parser = getParserFor("foo is less than or equal to 'A'");
		IBinaryPredicate pred = (IBinaryPredicate) parser.constraint()
				.getTree();

		assertTrue(pred.getLeft() instanceof IModelReference);
		assertTrue(pred.getRight() instanceof ILiteralString);
		assertTrue(pred.getPredicate() == IBinaryPredicate.Predicate.LESS_OR_EQUAL);
	}

	/**
	 * Test &gt;
	 */
	@Test
	public void testParseGreater() throws Exception {
		NRLActionParser parser = getParserFor("foo > 'A'");
		IBinaryPredicate pred = (IBinaryPredicate) parser.constraint()
				.getTree();

		assertTrue(pred.getLeft() instanceof IModelReference);
		assertTrue(pred.getRight() instanceof ILiteralString);
		assertTrue(pred.getPredicate() == IBinaryPredicate.Predicate.GREATER);
	}

	/**
	 * Test &gt;=
	 */
	@Test
	public void testGreaterOrEqual() throws Exception {
		NRLActionParser parser = getParserFor("foo is greater than or equal to 'A'");
		IBinaryPredicate pred = (IBinaryPredicate) parser.constraint()
				.getTree();

		assertTrue(pred.getLeft() instanceof IModelReference);
		assertTrue(pred.getRight() instanceof ILiteralString);
		assertTrue(pred.getPredicate() == IBinaryPredicate.Predicate.GREATER_OR_EQUAL);
	}

	/**
	 * Test &gt;=
	 * 
	 * @throws RecognitionException
	 */
	@Test
	public void testBinaryPredicateMustHaveExpressionParameters()
			throws RecognitionException {
		NRLActionParser parser = getParserFor("foo = ('A' or 'B')");
		parser.constraint();
		assertTrue(parser.hasErrors());
		assertEquals(1, parser.getSyntaxErrors().size());
		assertEquals(IStatusCode.PARSER_ERROR, parser.getSyntaxErrors().get(0).getStatusCode());
		
		parser = getParserFor("('A' or 'B') = foo");
		parser.constraint();
		assertTrue(parser.hasErrors());
		assertEquals(1, parser.getSyntaxErrors().size());
		assertEquals(IStatusCode.PARSER_ERROR, parser.getSyntaxErrors().get(0).getStatusCode());
	}

}
