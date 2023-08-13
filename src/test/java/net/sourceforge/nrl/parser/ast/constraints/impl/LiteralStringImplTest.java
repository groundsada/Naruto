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
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.constraints.ILiteralString;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;
import net.sourceforge.nrl.parser.ast.impl.SyntaxErrorException;

import org.junit.Test;

public class LiteralStringImplTest extends NRLParserTestSupport {

	@Test
	public void testParseEmptyString() throws Exception {
		NRLActionParser parser = getParserFor("''");

		ILiteralString str = (ILiteralString) parser.constraint().getTree();
		assertEquals("", str.getString());
	}

	@Test
	public void testParseString() throws Exception {
		NRLActionParser parser = getParserFor("'xyz'");

		ILiteralString str = (ILiteralString) parser.constraint().getTree();
		assertEquals("xyz", str.getString());
	}
	
	@Test
	public void testParseStringWithEscapedQuote() throws Exception {
		NRLActionParser parser = getParserFor("'xy''z'");

		ILiteralString str = (ILiteralString) parser.constraint().getTree();
		assertEquals("xy'z", str.getString());
	}

	@Test(expected = SyntaxErrorException.class)
	public void testParseUnterminatedString() throws Exception {
		NRLActionParser parser = getParserFor("'xyz");
		parser.constraint();
	}

	@Test(expected = SyntaxErrorException.class)
	public void testParseUnterminatedStringWithExtraQuote() throws Exception {
		NRLActionParser parser = getParserFor("'xyz''");
		parser.constraint();
	}

	@Test
	public void testParseUTF() throws Exception {
		// Test some European language chars
		NRLActionParser parser = getParserFor("'Ação'");
		ILiteralString str = (ILiteralString) parser.constraint().getTree();
		assertEquals("Ação", str.getString());

		parser = getParserFor("'üäö'");
		str = (ILiteralString) parser.constraint().getTree();
		assertEquals("üäö", str.getString());

		// Japanese (note: your eclipse font may display these as blocks, it says
		// "nihongo")
		parser = getParserFor("'日本語'");

		str = (ILiteralString) parser.constraint().getTree();
		assertEquals("日本語", str.getString());
	}
}
