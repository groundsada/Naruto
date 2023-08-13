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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.constraints.IBinaryPredicate;
import net.sourceforge.nrl.parser.ast.constraints.ICompoundReport;
import net.sourceforge.nrl.parser.ast.constraints.IConditionalReport;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

public class ConditionalReportImplTest extends NRLParserTestSupport {

	@Test
	public void testParse() throws Exception {
		NRLActionParser parser = getParserFor("if foo = bar then 'a';");

		IConditionalReport rep = (IConditionalReport) parser.conditionalReport().getTree();
		assertNull(rep.getElse());
		assertTrue(rep.getCondition() instanceof IBinaryPredicate);
		assertTrue(rep.getThen() instanceof ICompoundReport);

		parser = getParserFor("if foo = bar then 'a' else 'b';");
		rep = (IConditionalReport) parser.conditionalReport().getTree();

		assertTrue(rep.getCondition() instanceof IBinaryPredicate);
		assertTrue(rep.getThen() instanceof ICompoundReport);
		assertTrue(rep.getElse() instanceof ICompoundReport);
	}
}
