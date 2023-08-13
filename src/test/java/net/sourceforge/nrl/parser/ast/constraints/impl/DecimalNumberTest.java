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

import java.math.BigDecimal;

import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.constraints.IDecimalNumber;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

public class DecimalNumberTest extends NRLParserTestSupport {

	@SuppressWarnings("deprecation")
	@Test
	public void testParse() throws Exception {
		NRLActionParser parser = getParserFor("5.0");

		IDecimalNumber number = (IDecimalNumber) parser.constraint().getTree();
		assertEquals(5.0, number.getNumber(), 0);
		assertEquals(new BigDecimal("5.0"), number.getNumberAsBigDecimal());

		parser = getParserFor("-5.0");
		number = (IDecimalNumber) parser.constraint().getTree();

		assertEquals(-5, number.getNumber(), 0);
		assertEquals(new BigDecimal("-5.0"), number.getNumberAsBigDecimal());

		parser = getParserFor("3.14");
		number = (IDecimalNumber) parser.constraint().getTree();

		assertEquals(3.14, number.getNumber(), 0);
		assertEquals(new BigDecimal("3.14"), number.getNumberAsBigDecimal());

		parser = getParserFor("-3.14");
		number = (IDecimalNumber) parser.constraint().getTree();

		assertEquals(-3.14, number.getNumber(), 0);
		assertEquals(new BigDecimal("-3.14"), number.getNumberAsBigDecimal());

	}
}
