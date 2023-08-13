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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import net.sourceforge.nrl.parser.IStatusCode;
import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.SemanticError;
import net.sourceforge.nrl.parser.ast.IRuleSetDeclaration;
import net.sourceforge.nrl.parser.ast.action.IActionFragmentApplicationAction;
import net.sourceforge.nrl.parser.ast.action.IActionRuleDeclaration;
import net.sourceforge.nrl.parser.ast.action.ICompoundAction;
import net.sourceforge.nrl.parser.ast.action.impl.ActionAstResolver;
import net.sourceforge.nrl.parser.ast.action.impl.ActionFragmentApplicationActionImpl;
import net.sourceforge.nrl.parser.ast.constraints.IBinaryOperatorStatement;
import net.sourceforge.nrl.parser.ast.constraints.IConstraintRuleDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.IExistsStatement;
import net.sourceforge.nrl.parser.ast.constraints.IIfThenStatement;
import net.sourceforge.nrl.parser.ast.constraints.IValidationFragmentApplication;
import net.sourceforge.nrl.parser.ast.constraints.impl.ModelReferenceImpl;

import org.junit.Test;

public class AntlrAstResolverTest extends NRLParserTestSupport {

	/**
	 * Test if duplicate rule ids are detected
	 */
	@Test
	public void testResolve_duplicateRuleId() throws Exception {
		RuleFileImpl ruleFile = (RuleFileImpl) getRuleFile("src/test/resources/parsing/duplicate-rule.nrl");

		ConstraintAstResolver resolver = new ConstraintAstResolver();
		List<NRLError> errors = resolver.resolve(ruleFile);

		assertEquals(1, errors.size());
		assertTrue(errors.iterator().next() instanceof SemanticError);
		assertEquals(17, ((SemanticError) errors.iterator().next()).getLine());
		assertEquals(IStatusCode.DUPLICATE_RULE, ((SemanticError) errors.iterator().next())
				.getStatusCode());
	}

	/**
	 * Test if duplicate rule parameter names are detected.
	 */
	@Test
	public void testResolve_duplicateParameterName() throws Exception {
		RuleFileImpl ruleFile = (RuleFileImpl) getRuleFile("src/test/resources/parsing/duplicate-parametername.nrl");

		ConstraintAstResolver resolver = new ConstraintAstResolver();
		List<NRLError> errors = resolver.resolve(ruleFile);

		assertEquals(1, errors.size());
		assertTrue(errors.iterator().next() instanceof SemanticError);
		assertEquals(8, ((SemanticError) errors.iterator().next()).getLine());
		assertEquals(IStatusCode.DUPLICATE_RULE_PARAMETER, ((SemanticError) errors.iterator()
				.next()).getStatusCode());
	}

	/**
	 * Test if duplicate property ids are detected
	 */
	@Test
	public void testResolve_duplicatePropertyId() throws Exception {
		RuleFileImpl ruleFile = (RuleFileImpl) getRuleFile("src/test/resources/parsing/duplicate-prop.nrl");

		ConstraintAstResolver resolver = new ConstraintAstResolver();
		List<NRLError> errors = resolver.resolve(ruleFile);

		assertEquals(1, errors.size());
		assertTrue(errors.iterator().next() instanceof SemanticError);
		assertEquals(17, ((SemanticError) errors.iterator().next()).getLine());
		assertEquals(IStatusCode.DUPLICATE_FRAGMENT, ((SemanticError) errors.iterator().next())
				.getStatusCode());
	}

	/**
	 * Test if duplicate macro ids are detected
	 */
	@Test
	public void testResolve_duplicateMacroId() throws Exception {
		RuleFileImpl ruleFile = (RuleFileImpl) getRuleFile("src/test/resources/parsing/duplicate-macro.nrl");

		ActionAstResolver resolver = new ActionAstResolver();
		List<NRLError> errors = resolver.resolve(ruleFile);

		assertEquals(1, errors.size());
		assertTrue(errors.iterator().next() instanceof SemanticError);
		assertEquals(17, ((SemanticError) errors.iterator().next()).getLine());
		assertEquals(IStatusCode.DUPLICATE_ACTION_FRAGMENT, ((SemanticError) errors.iterator()
				.next()).getStatusCode());
	}

	/**
	 * Test if duplicate rule set ids are detected
	 */
	@Test
	public void testResolve_duplicateRuleSetId() throws Exception {
		RuleFileImpl ruleFile = (RuleFileImpl) getRuleFile("src/test/resources/parsing/duplicate-ruleset.nrl");

		ConstraintAstResolver resolver = new ConstraintAstResolver();
		List<NRLError> errors = resolver.resolve(ruleFile);

		assertEquals(1, errors.size());
		assertTrue(errors.iterator().next() instanceof SemanticError);
		assertEquals(19, ((SemanticError) errors.iterator().next()).getLine());
		assertEquals(IStatusCode.DUPLICATE_RULESET, ((SemanticError) errors.iterator().next())
				.getStatusCode());
	}

	/**
	 * Test if duplicate global variables are detected
	 */
	@Test
	public void testResolve_duplicateGlobalVariables() throws Exception {
		RuleFileImpl ruleFile = (RuleFileImpl) getRuleFile("src/test/resources/parsing/duplicate-global.nrl");

		ConstraintAstResolver resolver = new ConstraintAstResolver();
		List<NRLError> errors = resolver.resolve(ruleFile);

		assertEquals(1, errors.size());
		assertTrue(errors.iterator().next() instanceof SemanticError);
		assertEquals(IStatusCode.DUPLICATE_GLOBAL_VARIABLE, ((SemanticError) errors.iterator()
				.next()).getStatusCode());
		assertEquals(9, ((SemanticError) errors.iterator().next()).getLine());
	}

	/**
	 * Test if property applications are properly resolved
	 */
	@Test
	public void testResolve_propertyApplication() throws Exception {
		RuleFileImpl ruleFile = (RuleFileImpl) getRuleFile("src/test/resources/parsing/property-application.nrl");

		ConstraintAstResolver resolver = new ConstraintAstResolver();
		List<NRLError> errors = resolver.resolve(ruleFile);

		// Should be one invalid property ref
		assertEquals(1, errors.size());
		assertTrue(errors.iterator().next() instanceof SemanticError);
		assertEquals(18, ((SemanticError) errors.iterator().next()).getLine());
		assertEquals(IStatusCode.INVALID_FRAGMENT_REF, ((SemanticError) errors.iterator().next())
				.getStatusCode());

		// Look up the rule that makes the reference
		IConstraintRuleDeclaration valid = (IConstraintRuleDeclaration) ruleFile
				.getRuleById("valid-reference");
		IValidationFragmentApplication appl = (IValidationFragmentApplication) ((IExistsStatement) valid
				.getConstraint()).getConstraint();

		// Check that the property object is populated
		assertNotNull(appl.getFragment());
		assertEquals(appl.getFragment().getId(), appl.getFragmentName());
		assertEquals("test", appl.getFragment().getId());
	}

	/**
	 * Test if macro applications are properly resolved.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testResolve_macroApplication() throws Exception {
		RuleFileImpl ruleFile = (RuleFileImpl) getRuleFile("src/test/resources/parsing/macro-application.nrl");

		ActionAstResolver resolver = new ActionAstResolver();
		List<NRLError> errors = resolver.resolve(ruleFile);

		// Should be one invalid macro ref
		assertEquals(1, errors.size());
		assertTrue(errors.iterator().next() instanceof SemanticError);
		assertEquals(18, ((SemanticError) errors.iterator().next()).getLine());
		assertEquals(IStatusCode.INVALID_ACTION_FRAGMENT_REF, ((SemanticError) errors.iterator()
				.next()).getStatusCode());

		// Look up the first reference
		IActionRuleDeclaration valid = (IActionRuleDeclaration) ruleFile.getDeclarationById("r1");
		IActionFragmentApplicationAction appl = (IActionFragmentApplicationAction) ((ICompoundAction) valid
				.getAction()).getSimpleActions().get(1);

		assertNotNull(appl.getFragment());
		assertEquals(((ActionFragmentApplicationActionImpl) appl).getActionFragmentId(), appl
				.getFragment().getId());
	}

	/**
	 * Test whether existence statements without reference to model elements are resolved properly.
	 */
	@Test
	public void testResolve_existence() throws Exception {
		RuleFileImpl ruleFile = (RuleFileImpl) getRuleFile("src/test/resources/parsing/exists-resolver.nrl");

		ConstraintAstResolver resolver = new ConstraintAstResolver();
		List<NRLError> errors = resolver.resolve(ruleFile);

		assertEquals(1, errors.size());
		assertEquals(23, ((SemanticError) errors.iterator().next()).getLine());

		// Check if the first rule resolved properly
		IConstraintRuleDeclaration valid = (IConstraintRuleDeclaration) ruleFile
				.getRuleById("valid-reference");
		IBinaryOperatorStatement and = (IBinaryOperatorStatement) valid.getConstraint();

		IExistsStatement ex1 = (IExistsStatement) and.getLeft();
		IExistsStatement ex2 = (IExistsStatement) and.getRight();

		assertNotNull(ex1.getElement());
		assertNotNull(ex2.getElement());

		assertEquals("test", ((ModelReferenceImpl) ex1.getElement()).getStepsAsStrings().iterator()
				.next());
		assertEquals("test", ((ModelReferenceImpl) ex2.getElement()).getStepsAsStrings().iterator()
				.next());

		// check if the second resolved properly (deeper nesting)
		valid = (IConstraintRuleDeclaration) ruleFile.getRuleById("valid-reference-2");
		IIfThenStatement ifThen = (IIfThenStatement) valid.getConstraint();

		ex1 = (IExistsStatement) ifThen.getIf();
		ex2 = (IExistsStatement) ifThen.getThen();

		assertNotNull(ex1.getElement());
		assertNotNull(ex2.getElement());

		assertEquals("test", ((ModelReferenceImpl) ex1.getElement()).getStepsAsStrings().iterator()
				.next());
		assertEquals("test", ((ModelReferenceImpl) ex2.getElement()).getStepsAsStrings().iterator()
				.next());

		// Check if resolution of the third rule failed (correctly)
		IConstraintRuleDeclaration invalid = (IConstraintRuleDeclaration) ruleFile
				.getRuleById("invalid-reference");
		and = (IBinaryOperatorStatement) invalid.getConstraint();

		ex1 = (IExistsStatement) and.getLeft();
		ex2 = (IExistsStatement) and.getRight();

		assertNull(ex1.getElement());
		assertNotNull(ex2.getElement());
	}

	/**
	 * Test whether rule sets are resolved correctly. This tests if rules in a set reference the set
	 * properly after resolution, and if the set correctly includes all rules.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testResolve_ruleSet() throws Exception {
		RuleFileImpl ruleFile = (RuleFileImpl) getRuleFile("src/test/resources/parsing/ruleset-precondition.nrl");

		ConstraintAstResolver resolver = new ConstraintAstResolver();
		List<NRLError> errors = resolver.resolve(ruleFile);
		assertEquals(0, errors.size());

		assertEquals(4, ruleFile.getDeclarations().size());
		assertEquals(2, ruleFile.getRuleSetDeclarations().size());

		// First rule is in no set
		assertNull(ruleFile.getRuleById("r0").getRuleSet());

		// Check that r1,r2 are properly contained in the first rule set
		IRuleSetDeclaration declA = ruleFile.getRuleSetDeclarations().get(0);
		assertEquals("Old trades", declA.getId());

		assertEquals(2, declA.getRules().size());
		assertEquals("r1", ((IConstraintRuleDeclaration) declA.getRules().get(0)).getId());
		assertEquals("r2", ((IConstraintRuleDeclaration) declA.getRules().get(1)).getId());
		assertTrue(((IConstraintRuleDeclaration) declA.getRules().get(0)).getRuleSet() == declA);
		assertTrue(((IConstraintRuleDeclaration) declA.getRules().get(1)).getRuleSet() == declA);

		// Check that r3 is properly contained in the second rule set
		IRuleSetDeclaration declB = ruleFile.getRuleSetDeclarations().get(1);
		assertEquals("Other Trades", declB.getId());

		assertEquals(1, declB.getRules().size());
		assertEquals("r3", ((IConstraintRuleDeclaration) declB.getRules().get(0)).getId());
		assertTrue(((IConstraintRuleDeclaration) declB.getRules().get(0)).getRuleSet() == declB);
	}
}
