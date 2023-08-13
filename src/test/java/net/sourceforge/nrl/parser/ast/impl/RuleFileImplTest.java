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
package net.sourceforge.nrl.parser.ast.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.constraints.IDecimalNumber;
import net.sourceforge.nrl.parser.ast.constraints.IIntegerNumber;
import net.sourceforge.nrl.parser.ast.constraints.ILiteralString;
import net.sourceforge.nrl.parser.ast.constraints.IVariableDeclaration;

import org.junit.Test;

public class RuleFileImplTest extends NRLParserTestSupport {

	@Test
	public void testParse() throws Exception {
		RuleFileImpl ruleFile = (RuleFileImpl) getRuleFile("src/test/resources/parsing/model-constraints.nrl");

		assertEquals(2, ruleFile.getGlobalVariableDeclarations().size());
		for (IVariableDeclaration var : ruleFile.getGlobalVariableDeclarations()) {
			assertNotNull(var.getVariableName());
			assertTrue(!var.getVariableName().equals(""));
			assertNotNull(var.getExpression());
			assertTrue(var.getExpression() instanceof IDecimalNumber
					|| var.getExpression() instanceof IIntegerNumber
					|| var.getExpression() instanceof ILiteralString);
		}

		assertEquals(2, ruleFile.getModelFileReferences().length);
		assertFalse(ruleFile.getModelFileReferences()[0].isAbsolute());
		assertEquals("../basicmodel.uml2", ruleFile.getModelFileReferences()[0].getFileName());
		assertFalse(ruleFile.getModelFileReferences()[1].isAbsolute());
		assertEquals("../FpML.uml2", ruleFile.getModelFileReferences()[1].getFileName());

		assertEquals(2, ruleFile.getOperatorFileReferences().length);
		assertEquals("test1", ruleFile.getOperatorFileReferences()[0].getFileName());
		assertEquals(false, ruleFile.getOperatorFileReferences()[0].isAbsolute());
		assertEquals("C:/test2", ruleFile.getOperatorFileReferences()[1].getFileName());
		assertEquals(true, ruleFile.getOperatorFileReferences()[1].isAbsolute());
	}
}
