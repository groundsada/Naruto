package net.sourceforge.nrl.parser.ast.impl;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.StringReader;

import net.sourceforge.nrl.parser.NRLParserTestSupport;

import org.antlr.runtime.Token;
import org.junit.Test;

/*
 * Test raw lexer behaviour; special cases only.
 */
public class NRLJFlexerTest extends NRLParserTestSupport {

	@Test
	public void testIllegalCharacter() {
		NRLJFlexer lexer = new NRLJFlexer(new StringReader("Set @"));
		lexer.nextToken();
		try {
			lexer.nextToken();
			fail("Expected an exception");
		} catch (SyntaxErrorException e) {
			assertEquals(1, e.getSyntaxError().getLine());
			assertEquals(4, e.getSyntaxError().getColumn());
			assertEquals(1, e.getSyntaxError().getLength());
		}
	}

	@Test
	public void testIdentifierWithUnderscoreCharacter() {
		NRLJFlexer lexer = new NRLJFlexer(new StringReader("test_identifier"));

		Token token = lexer.nextToken();
		assertEquals(89, token.getType());
		assertEquals("test_identifier", token.getText());

		token = lexer.nextToken();
		assertEquals(-1, token.getType());
	}
}
