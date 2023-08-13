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
import static org.junit.Assert.assertTrue;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.constraints.ICompoundReport;
import net.sourceforge.nrl.parser.ast.constraints.IConcatenatedReport;
import net.sourceforge.nrl.parser.ast.constraints.IConditionalReport;
import net.sourceforge.nrl.parser.ast.constraints.IConstraintRuleDeclaration;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

public class CompoundReportImplTest extends NRLParserTestSupport {

	@Test
	public void testParse() throws Exception {
		NRLActionParser parser = getParserFor("'a'");

		ICompoundReport rep = (ICompoundReport) parser.compoundReport().getTree();
		assertEquals(1, rep.getReports().size());
		assertTrue(rep.getReports().get(0) instanceof IConcatenatedReport);

		parser = getParserFor("'a'  foobar  23");
		rep = (ICompoundReport) parser.compoundReport().getTree();

		assertEquals(3, rep.getReports().size());
		assertTrue(rep.getReports().get(0) instanceof IConcatenatedReport);
		assertTrue(rep.getReports().get(1) instanceof IConcatenatedReport);
		assertTrue(rep.getReports().get(2) instanceof IConcatenatedReport);

		parser = getParserFor("report 'a', if foo = bar then report 'b';");
		rep = (ICompoundReport) parser.compoundReport().getTree();
		assertEquals(0, parser.getSyntaxErrors().size());

		assertEquals(2, rep.getReports().size());
		assertTrue(rep.getReports().get(0) instanceof IConcatenatedReport);
		assertTrue(rep.getReports().get(1) instanceof IConditionalReport);

		parser = getParserFor("context: x Validation Rule \"y\" 1 = 1 report: 'x'");
		IConstraintRuleDeclaration rule = (IConstraintRuleDeclaration) parser
				.validationRuleDeclaration().getTree();
		assertNotNull(rule.getReport());
		assertEquals("y", rule.getId());
	}
}
