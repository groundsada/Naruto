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

import junit.framework.Assert;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.constraints.IBinaryPredicate;
import net.sourceforge.nrl.parser.ast.constraints.ICompoundReport;
import net.sourceforge.nrl.parser.ast.constraints.IConstraintRuleDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.ILiteralString;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

/**
 * Unit tests for constraint rule declaration statement.
 * 
 * @author Christian Nentwich
 */
public class ConstraintRuleDeclarationImplTest extends NRLParserTestSupport {

	@Test
	public void testParse() throws Exception {
		NRLActionParser parser = getParserFor("Context: x Validation Rule \"r1\" 1 = 1");

		IConstraintRuleDeclaration decl = (IConstraintRuleDeclaration) parser
				.validationRuleDeclaration().getTree();
		Assert.assertTrue(decl.getConstraint() instanceof IBinaryPredicate);
		Assert.assertEquals("r1", decl.getId());
		Assert.assertEquals("x", ((ConstraintRuleDeclarationImpl) decl).getModelReference()
				.getOriginalString());
		Assert.assertEquals(0, decl.getVariableDeclarations().size());
		Assert.assertNull(decl.getReport());
		Assert.assertTrue("Should not have additional parameters", decl
				.getAdditionalParameterNames().isEmpty());
	}

	@Test
	public void testParseWithReport() throws Exception {
		NRLActionParser parser = getParserFor("Context: x Validation Rule \"r1\" 1 = 1 report 5");

		IConstraintRuleDeclaration decl = (IConstraintRuleDeclaration) parser
				.validationRuleDeclaration().getTree();
		Assert.assertTrue(decl.getConstraint() instanceof IBinaryPredicate);
		Assert.assertEquals("r1", decl.getId());
		Assert.assertEquals("x", ((ConstraintRuleDeclarationImpl) decl).getModelReference()
				.getOriginalString());
		Assert.assertTrue(decl.getReport() instanceof ICompoundReport);
		Assert.assertEquals(0, decl.getVariableDeclarations().size());
		Assert.assertNotNull(decl.getReport());
		Assert.assertTrue("Should not have additional parameters", decl
				.getAdditionalParameterNames().isEmpty());
	}

	@Test
	public void testParseWithVariable() throws Exception {
		NRLActionParser parser = getParserFor("Context: x Validation Rule \"r1\" \"foo\" represents 'a' \"bar\" represents x, 1 = 1");

		IConstraintRuleDeclaration decl = (IConstraintRuleDeclaration) parser
				.validationRuleDeclaration().getTree();
		Assert.assertTrue(decl.getConstraint() instanceof IBinaryPredicate);
		Assert.assertEquals("r1", decl.getId());
		Assert.assertEquals("x", ((ConstraintRuleDeclarationImpl) decl).getModelReference()
				.getOriginalString());
		Assert.assertEquals(2, decl.getVariableDeclarations().size());
		Assert.assertEquals("foo", decl.getVariableDeclarations().get(0).getVariableName());
		Assert
				.assertTrue(decl.getVariableDeclarations().get(0).getExpression() instanceof ILiteralString);
		Assert.assertEquals("bar", decl.getVariableDeclarations().get(1).getVariableName());
		Assert
				.assertTrue(decl.getVariableDeclarations().get(1).getExpression() instanceof IModelReference);
		Assert.assertNull(decl.getReport());
		Assert.assertTrue("Should not have additional parameters", decl
				.getAdditionalParameterNames().isEmpty());
	}

	@Test
	public void testParseWithVariableAndReport() throws Exception {
		NRLActionParser parser = getParserFor("Context: x Validation Rule \"r1\" \"foo\" represents 'a' \"bar\" represents x, 1 = 1 report 5");

		IConstraintRuleDeclaration decl = (IConstraintRuleDeclaration) parser
				.validationRuleDeclaration().getTree();
		Assert.assertTrue(decl.getConstraint() instanceof IBinaryPredicate);
		Assert.assertEquals("r1", decl.getId());
		Assert.assertEquals("x", ((ConstraintRuleDeclarationImpl) decl).getModelReference()
				.getOriginalString());
		Assert.assertEquals(2, decl.getVariableDeclarations().size());
		Assert.assertEquals("foo", decl.getVariableDeclarations().get(0).getVariableName());
		Assert
				.assertTrue(decl.getVariableDeclarations().get(0).getExpression() instanceof ILiteralString);
		Assert.assertEquals("bar", decl.getVariableDeclarations().get(1).getVariableName());
		Assert
				.assertTrue(decl.getVariableDeclarations().get(1).getExpression() instanceof IModelReference);
		Assert.assertTrue(decl.getReport() instanceof ICompoundReport);
		Assert.assertNotNull(decl.getReport());
		Assert.assertTrue("Should not have additional parameters", decl
				.getAdditionalParameterNames().isEmpty());
	}

	@Test
	public void testParseAppliesToSyntax() throws Exception {
		NRLActionParser parser = getParserFor("Validation Rule \"r1\" applies to x 1 = 1");

		IConstraintRuleDeclaration decl = (IConstraintRuleDeclaration) parser
				.validationRuleDeclaration().getTree();
		Assert.assertTrue(decl.getConstraint() instanceof IBinaryPredicate);
		Assert.assertEquals("r1", decl.getId());
		Assert.assertEquals("x", ((ConstraintRuleDeclarationImpl) decl).getModelReference()
				.getOriginalString());
		Assert.assertEquals(0, decl.getVariableDeclarations().size());
		Assert.assertNull(decl.getReport());
		Assert.assertTrue("Should not have additional parameters", decl
				.getAdditionalParameterNames().isEmpty());
	}

	@Test
	public void testParseAppliesToSyntaxWithVariables() throws Exception {
		NRLActionParser parser = getParserFor("Validation Rule \"r1\" applies to x \"foo\" represents 'a', 1 = 1");

		IConstraintRuleDeclaration decl = (IConstraintRuleDeclaration) parser
				.validationRuleDeclaration().getTree();
		Assert.assertTrue(decl.getConstraint() instanceof IBinaryPredicate);
		Assert.assertEquals("r1", decl.getId());
		Assert.assertEquals("x", ((ConstraintRuleDeclarationImpl) decl).getModelReference()
				.getOriginalString());
		Assert.assertEquals(1, decl.getVariableDeclarations().size());
		Assert.assertEquals("foo", decl.getVariableDeclarations().get(0).getVariableName());
		Assert
				.assertTrue(decl.getVariableDeclarations().get(0).getExpression() instanceof ILiteralString);
		Assert.assertNull(decl.getReport());
		Assert.assertTrue("Should not have additional parameters", decl
				.getAdditionalParameterNames().isEmpty());
	}

	@Test
	public void testParseAppliesToSyntaxWithVariablesAndReport() throws Exception {
		NRLActionParser parser = getParserFor("Validation Rule \"r1\" applies to x \"foo\" represents 'a', 1 = 1 Report 5");

		IConstraintRuleDeclaration decl = (IConstraintRuleDeclaration) parser
				.validationRuleDeclaration().getTree();
		Assert.assertTrue(decl.getConstraint() instanceof IBinaryPredicate);
		Assert.assertEquals("r1", decl.getId());
		Assert.assertEquals("x", ((ConstraintRuleDeclarationImpl) decl).getModelReference()
				.getOriginalString());
		Assert.assertEquals(1, decl.getVariableDeclarations().size());
		Assert.assertEquals("foo", decl.getVariableDeclarations().get(0).getVariableName());
		Assert
				.assertTrue(decl.getVariableDeclarations().get(0).getExpression() instanceof ILiteralString);
		Assert.assertNotNull(decl.getReport());
		Assert.assertTrue("Should not have additional parameters", decl
				.getAdditionalParameterNames().isEmpty());
	}

	@Test
	public void testParseWithAdditionalParameter() throws Exception {
		NRLActionParser parser = getParserFor("Validation Rule \"r1\" applies to x and uses y (\"y1\") 1 = 1");

		IConstraintRuleDeclaration decl = (IConstraintRuleDeclaration) parser
				.validationRuleDeclaration().getTree();
		Assert.assertTrue(decl.getConstraint() instanceof IBinaryPredicate);
		Assert.assertEquals("r1", decl.getId());
		Assert.assertEquals("x", ((ConstraintRuleDeclarationImpl) decl).getModelReference()
				.getOriginalString());
		Assert.assertEquals(0, decl.getVariableDeclarations().size());
		Assert.assertNull(decl.getReport());

		Assert.assertEquals("Wrong number of parameters", 1, decl.getAdditionalParameterNames()
				.size());
		Assert
				.assertEquals("Parameter name wrong", "y1", decl.getAdditionalParameterNames().get(
						0));
		Assert.assertEquals("y", ((ConstraintRuleDeclarationImpl) decl)
				.getAdditionalParameterTypeReference("y1").getOriginalString());
	}

	@Test
	public void testParseWithAdditionalParameters() throws Exception {
		NRLActionParser parser = getParserFor("Validation Rule \"r1\" applies to x and uses y (\"y1\"), z (\"z1\") 1 = 1");

		IConstraintRuleDeclaration decl = (IConstraintRuleDeclaration) parser
				.validationRuleDeclaration().getTree();
		Assert.assertTrue(decl.getConstraint() instanceof IBinaryPredicate);
		Assert.assertEquals("r1", decl.getId());
		Assert.assertEquals("x", ((ConstraintRuleDeclarationImpl) decl).getModelReference()
				.getOriginalString());
		Assert.assertEquals(0, decl.getVariableDeclarations().size());
		Assert.assertNull(decl.getReport());

		Assert.assertEquals("Wrong number of parameters", 2, decl.getAdditionalParameterNames()
				.size());
		Assert
				.assertEquals("Parameter name wrong", "y1", decl.getAdditionalParameterNames().get(
						0));
		Assert.assertEquals("y", ((ConstraintRuleDeclarationImpl) decl)
				.getAdditionalParameterTypeReference("y1").getOriginalString());
		Assert
				.assertEquals("Parameter name wrong", "z1", decl.getAdditionalParameterNames().get(
						1));
		Assert.assertEquals("z", ((ConstraintRuleDeclarationImpl) decl)
				.getAdditionalParameterTypeReference("z1").getOriginalString());
	}

}
