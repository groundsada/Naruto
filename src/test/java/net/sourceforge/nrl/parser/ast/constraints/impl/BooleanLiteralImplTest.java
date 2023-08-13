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

import static org.junit.Assert.assertTrue;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.constraints.IBooleanLiteral;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

/**
 * Unit tests for true/false literal
 * 
 * @author Christian Nentwich
 */
public class BooleanLiteralImplTest extends NRLParserTestSupport {

	@Test
	public void testParse() throws Exception {
		NRLActionParser parser = getParserFor("true");
		IBooleanLiteral pred = (IBooleanLiteral) parser.constraint().getTree();

		assertTrue(pred.getBoolean());

		parser = getParserFor("false");
		pred = (IBooleanLiteral) parser.constraint().getTree();
		assertTrue(!pred.getBoolean());
	}
}
