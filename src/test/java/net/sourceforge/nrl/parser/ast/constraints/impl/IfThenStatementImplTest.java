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
import net.sourceforge.nrl.parser.ast.constraints.IIfThenStatement;
import net.sourceforge.nrl.parser.ast.constraints.IOperatorInvocation;
import net.sourceforge.nrl.parser.ast.constraints.IPredicate;
import net.sourceforge.nrl.parser.ast.constraints.IValidationFragmentApplication;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

/**
 * Unit tests for if-then-else statement
 * 
 * @author Christian Nentwich
 */
public class IfThenStatementImplTest extends NRLParserTestSupport {

	@Test
	public void testParse() throws Exception {
		// Parse if-then
		NRLActionParser parser = getParserFor("if foo = 'A' then {is valid}");

		IIfThenStatement statement = (IIfThenStatement) parser.statement().getTree();
		assertTrue(statement.getIf() instanceof IPredicate);
		assertTrue(statement.getThen() instanceof IValidationFragmentApplication);
		assertNull(statement.getElse());

		// Parse if-then-else
		parser = getParserFor("if foo = 'A' then {is valid} else [not]");
		statement = (IIfThenStatement) parser.statement().getTree();
		assertTrue(statement.getIf() instanceof IPredicate);
		assertTrue(statement.getThen() instanceof IValidationFragmentApplication);
		assertTrue(statement.getElse() instanceof IOperatorInvocation);
	}
}
