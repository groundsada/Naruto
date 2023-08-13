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
import net.sourceforge.nrl.parser.ast.constraints.IConcatenatedReport;
import net.sourceforge.nrl.parser.ast.constraints.IIntegerNumber;
import net.sourceforge.nrl.parser.ast.constraints.ILiteralString;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

public class ConcatenatedReportImplTest extends NRLParserTestSupport {

	@Test
	public void testParse() throws Exception {
		NRLActionParser parser = getParserFor("'a'");

		IConcatenatedReport rep = (IConcatenatedReport) parser.concatenatedReport().getTree();
		assertEquals(1, rep.getExpressions().size());
		assertTrue(rep.getExpressions().get(0) instanceof ILiteralString);

		parser = getParserFor("'a' + foobar + 23");
		rep = (IConcatenatedReport) parser.concatenatedReport().getTree();

		assertEquals(3, rep.getExpressions().size());
		assertTrue(rep.getExpressions().get(0) instanceof ILiteralString);
		assertTrue(rep.getExpressions().get(1) instanceof IModelReference);
		assertTrue(rep.getExpressions().get(2) instanceof IIntegerNumber);
	}
}
