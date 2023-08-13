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

import java.io.File;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.SemanticError;
import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.IRuleSetDeclaration;
import net.sourceforge.nrl.parser.ast.ISingleContextDeclaration;
import net.sourceforge.nrl.parser.ast.IVariable;
import net.sourceforge.nrl.parser.ast.action.IActionFragmentDeclaration;
import net.sourceforge.nrl.parser.ast.action.IActionRuleDeclaration;
import net.sourceforge.nrl.parser.ast.action.IAddAction;
import net.sourceforge.nrl.parser.ast.action.ICompoundAction;
import net.sourceforge.nrl.parser.ast.action.IConditionalAction;
import net.sourceforge.nrl.parser.ast.action.ICreateAction;
import net.sourceforge.nrl.parser.ast.action.IForEachAction;
import net.sourceforge.nrl.parser.ast.action.IRemoveAction;
import net.sourceforge.nrl.parser.ast.action.IRemoveFromCollectionAction;
import net.sourceforge.nrl.parser.ast.action.ISetAction;
import net.sourceforge.nrl.parser.ast.action.IVariableDeclarationAction;
import net.sourceforge.nrl.parser.ast.action.impl.ActionAstResolver;
import net.sourceforge.nrl.parser.ast.constraints.IBinaryOperatorStatement;
import net.sourceforge.nrl.parser.ast.constraints.IBinaryPredicate;
import net.sourceforge.nrl.parser.ast.constraints.ICastExpression;
import net.sourceforge.nrl.parser.ast.constraints.ICompoundReport;
import net.sourceforge.nrl.parser.ast.constraints.IConcatenatedReport;
import net.sourceforge.nrl.parser.ast.constraints.IConditionalReport;
import net.sourceforge.nrl.parser.ast.constraints.IConstraintRuleDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.IExistsStatement;
import net.sourceforge.nrl.parser.ast.constraints.IForallStatement;
import net.sourceforge.nrl.parser.ast.constraints.IFunctionalExpression;
import net.sourceforge.nrl.parser.ast.constraints.IGlobalExistsStatement;
import net.sourceforge.nrl.parser.ast.constraints.IIdentifier;
import net.sourceforge.nrl.parser.ast.constraints.IIfThenStatement;
import net.sourceforge.nrl.parser.ast.constraints.IIsInPredicate;
import net.sourceforge.nrl.parser.ast.constraints.IIsSubtypePredicate;
import net.sourceforge.nrl.parser.ast.constraints.ILiteralString;
import net.sourceforge.nrl.parser.ast.constraints.IOperatorInvocation;
import net.sourceforge.nrl.parser.ast.constraints.ISelectionExpression;
import net.sourceforge.nrl.parser.ast.constraints.IValidationFragmentDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.IVariableDeclaration;
import net.sourceforge.nrl.parser.model.IAttribute;
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.model.ModelCollection;
import net.sourceforge.nrl.parser.operators.IOperators;
import net.sourceforge.nrl.parser.operators.XmlOperatorPersistence;
import net.sourceforge.nrl.parser.operators.XmlOperatorPersistence.LoadedVersion;

import org.junit.Test;

public class AntlrModelResolverTest extends NRLParserTestSupport {

	@Test
	public void testResolveConstraints() throws Exception {
		RuleFileImpl ruleFile = (RuleFileImpl) getRuleFile("src/test/resources/parsing/model-constraints.nrl");

		// Run the normal resolver first
		ConstraintAstResolver resolver = new ConstraintAstResolver();
		List<NRLError> errors = resolver.resolve(ruleFile);
		for (int i = 0; i < errors.size(); i++)
			System.err.println(errors.get(i));
		assertEquals(0, errors.size());

		// Now load a model and run the model resolver
		ModelCollection models = new ModelCollection();
		models.addModelPackage(getBasicModel());
		models.addModelPackage(getSimpleModel());

		// Run the model resolver
		AntlrModelResolver modelRes = new AntlrModelResolver(models);
		errors = modelRes.resolve(ruleFile);
		for (NRLError error : errors) {
			System.out.println("ERROR: " + error);
		}

		int firstErrorLine = ruleFile.getDeclarationById("invalid-1").getLine();
		for (NRLError error : errors) {
			assertTrue("Rule that should be valid failed, line: " + error.getLine(),
					error.getLine() + 2 >= firstErrorLine);
		}

		assertEquals(32, errors.size());

		// Check the context references
		assertEquals("Trade", ruleFile.getRuleById("expr-1").getContext().getName());
		assertEquals("IRSwap", ruleFile.getRuleById("expr-2").getContext().getName());
		assertNull(ruleFile.getRuleById("invalid-1").getContext());

		// Now check that all refs are ok - expr-1
		IConstraintRuleDeclaration rule = (IConstraintRuleDeclaration) ruleFile
				.getRuleById("expr-1");
		IModelReference ref = (IModelReference) ((IBinaryPredicate) rule.getConstraint()).getLeft();
		assertElement(ref, IAttribute.class, 0, "tradeDate", "Date",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);
		assertEquals("Trade", ref.getCurrentContext().getName());

		// Rule expr-2 (exists plus expression)
		rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("expr-2");
		ref = ((IExistsStatement) ((IIfThenStatement) rule.getConstraint()).getIf()).getElement();
		assertElement(ref, IAttribute.class, 0, "tradeheader", "TradeHeader",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);
		assertEquals("IRSwap", ref.getCurrentContext().getName());

		ref = (IModelReference) ((IBinaryPredicate) ((IIfThenStatement) rule.getConstraint())
				.getThen()).getLeft();
		assertElement(ref, IAttribute.class, 0, "tradeDate", "Date",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		// Rule iteration (exists and nested)
		rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("iteration");
		ref = ((IExistsStatement) rule.getConstraint()).getElement();
		assertElement(ref, IAttribute.class, 0, "legs", "IRLeg",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		ref = (IModelReference) ((IBinaryPredicate) ((IExistsStatement) rule.getConstraint())
				.getConstraint()).getLeft();
		assertElement(ref, IAttribute.class, 0, "fixFloat", "FixFloatEnum",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);
		assertEquals("IRLeg", ref.getCurrentContext().getName());

		ref = (IModelReference) ((IBinaryPredicate) ((IExistsStatement) rule.getConstraint())
				.getConstraint()).getRight();
		assertElement(ref, IModelElement.class, 1, "FixFloatEnum", "FixFloatEnum",
				IModelReference.REFERENCE_STATIC_ATTRIBUTE);
		assertEquals(null, ref.getCurrentContext());

		// Rule iteration-2 (forall)
		rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("iteration-2");
		ref = ((IForallStatement) rule.getConstraint()).getElement();
		assertElement(ref, IAttribute.class, 0, "legs", "IRLeg",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		// Rule iteration-2 (forall with variable)
		rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("iteration-3");
		ref = ((IForallStatement) rule.getConstraint()).getElement();
		assertElement(ref, IAttribute.class, 0, "legs", "IRLeg",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		IVariable var = ((IForallStatement) rule.getConstraint()).getVariable();
		assertEquals("x", var.getName());
		assertEquals("IRLeg", var.getBoundElement().getName());

		// Rule self-reference
		// rule = ruleFile.getRuleById("self-reference");
		// ref = (IModelReference)((IBinaryPredicate)
		// rule.getConstraint()).getLeft();
		// assertElement(ref, IAttribute.class, 0, "tradeDate", "Date");

		// Rule context-reference
		rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("context-reference");
		ref = (IModelReference) ((IBinaryPredicate) ((IForallStatement) rule.getConstraint())
				.getConstraint()).getLeft();
		assertElement(ref, IAttribute.class, 0, "tradeDate", "Date",
				IModelReference.REFERENCE_TOP_CONTEXT_RELATIVE_ATTRIBUTE);

		// Rule var-3
		rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("var-3");
		ref = (IModelReference) rule.getVariableDeclarations().get(0).getExpression();
		assertElement(ref, IModelElement.class, 0, "TradeHeader", "TradeHeader",
				IModelReference.REFERENCE_ELEMENT);

		// Rule steps
		rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("steps");
		ref = (IModelReference) ((IBinaryPredicate) rule.getConstraint()).getLeft();
		assertElement(ref, IAttribute.class, 1, "tradeheader", "String",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		// Rule qualified-1
		rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("qualified-1");
		assertEquals("Trade", rule.getContext().getName());

		// Rule context-1
		rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("context-1");
		ref = (IModelReference) ((IBinaryPredicate) ((IExistsStatement) rule.getConstraint())
				.getConstraint()).getLeft();
		assertElement(ref, IAttribute.class, 0, "tradeDate", "Date",
				IModelReference.REFERENCE_TOP_CONTEXT_RELATIVE_ATTRIBUTE);

		// Rule uniqueby-1
		rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("uniqueby-1");
		IFunctionalExpression func = ((IFunctionalExpression) ((IBinaryPredicate) rule
				.getConstraint()).getLeft());

		ref = (IModelReference) func.getParameters().iterator().next();
		assertElement(ref, IAttribute.class, 0, "legs", "IRLeg",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		Iterator<IIdentifier> iter = func.getParameters().iterator();
		iter.next();
		ref = (IModelReference) iter.next();
		assertElement(ref, IAttribute.class, 0, "fixFloat", "FixFloatEnum",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		// Rule global-var
		rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("global-var");
		ref = (IModelReference) ((IBinaryPredicate) rule.getConstraint()).getRight();
		assertElement(ref, IVariable.class, 0, "mydate", null,
				IModelReference.REFERENCE_GLOBAL_VARIABLE);

		// Rule second-model
		rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("second-model");
		ref = (IModelReference) ((IBinaryPredicate) rule.getConstraint()).getLeft();
		assertElement(ref, IAttribute.class, 0, "attr", "String",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		// Rule global-exists-1
		rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("global-exists-1");
		IModelElement element = ((IGlobalExistsStatement) rule.getConstraint()).getElement();
		assertEquals("Trade", element.getName());

		// Rule global-exists-2
		rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("global-exists-2");
		element = ((IGlobalExistsStatement) rule.getConstraint()).getElement();
		assertEquals("Trade", element.getName());

		ref = (IModelReference) ((IBinaryPredicate) ((IGlobalExistsStatement) rule.getConstraint())
				.getConstraint()).getLeft();
		assertElement(ref, IVariable.class, 1, "trade", "Date",
				IModelReference.REFERENCE_VARIABLE_RELATIVE_ATTRIBUTE);

		// Rule global-exists-3
		rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("global-exists-3");
		element = ((IGlobalExistsStatement) rule.getConstraint()).getElement();
		assertEquals("TradeHeader", element.getName());

		ref = (IModelReference) ((IBinaryPredicate) ((IGlobalExistsStatement) rule.getConstraint())
				.getConstraint()).getLeft();
		assertElement(ref, IAttribute.class, 0, "masterAgreement", "String",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		// Rule is-one-of-1
		rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("is-one-of-1");
		List<IIdentifier> list = ((IIsInPredicate) rule.getConstraint()).getList();
		assertEquals(3, list.size());

		ref = (IModelReference) list.get(0);
		assertElement(ref, IModelElement.class, 1, "FixFloatEnum", "FixFloatEnum",
				IModelReference.REFERENCE_STATIC_ATTRIBUTE);
		ref = (IModelReference) list.get(2);
		assertElement(ref, IAttribute.class, 0, "tradeDate", "Date",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		// Property property-1
		IValidationFragmentDeclaration decl = (IValidationFragmentDeclaration) ruleFile
				.getDeclarationById("property-1");
		assertEquals("IRSwap", decl.getContextType("swap").getName());

		ref = (IModelReference) ((IBinaryPredicate) decl.getConstraint()).getLeft();
		assertElement(ref, IVariable.class, 1, "swap", "Date",
				IModelReference.REFERENCE_VARIABLE_RELATIVE_ATTRIBUTE);

		// Rule cast-1
		rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("cast-1");
		ICastExpression cast = (ICastExpression) (rule.getVariableDeclarations().get(0)
				.getExpression());
		assertElement(cast.getReference(), IModelElement.class, 0, "Trade", "Trade",
				IModelReference.REFERENCE_ELEMENT);
		assertEquals("IRSwap", cast.getTargetType().getName());

		// Rule is-subtype-1
		rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("is-subtype-1");
		IIsSubtypePredicate subType = (IIsSubtypePredicate) ((IIfThenStatement) rule
				.getConstraint()).getIf();
		assertElement(subType.getReference(), IModelElement.class, 0, "Trade", "Trade",
				IModelReference.REFERENCE_ELEMENT);
		assertEquals("IRSwap", subType.getTargetType().getName());

		// Rule selection-1
		rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("selection-1");
		ISelectionExpression select = (ISelectionExpression) rule.getVariableDeclarations().get(0)
				.getExpression();
		assertElement(select.getModelReference(), IAttribute.class, 0, "legs", "IRLeg",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		// Rule report-1
		rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("report-1");
		ICompoundReport report = rule.getReport();
		ref = (IModelReference) ((IConcatenatedReport) report.getReports().get(0)).getExpressions()
				.get(1);
		assertElement(ref, IAttribute.class, 1, "tradeheader", "String",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		ref = (IModelReference) ((IBinaryPredicate) ((IConditionalReport) report.getReports()
				.get(1)).getCondition()).getLeft();
		assertElement(ref, IAttribute.class, 1, "tradeheader", "String",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		ref = (IModelReference) ((IConcatenatedReport) (((IConditionalReport) report
				.getReports().get(1)).getThen()).getReports().get(0)).getExpressions().get(0);
		assertElement(ref, IAttribute.class, 0, "tradeDate", "Date",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		ref = (IModelReference) ((IConcatenatedReport) (((IConditionalReport) report
				.getReports().get(1)).getElse()).getReports().get(0)).getExpressions().get(0);
		assertElement(ref, IAttribute.class, 1, "tradeheader", "String",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		// Rule additionalparams-1
		rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("additionalparams-1");
		assertEquals("String", rule.getAdditionalParameterType("foo").getName());
		ref = (IModelReference) ((IBinaryPredicate) rule.getConstraint()).getRight();
		assertElement(ref, IVariable.class, 0, "foo", "String",
				IModelReference.REFERENCE_VARIABLE_RELATIVE_ATTRIBUTE);

		// Rule additionalparams-2
		rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("additionalparams-2");
		assertEquals("TradeHeader", rule.getAdditionalParameterType("header").getName());
		assertEquals("IRSwap", rule.getAdditionalParameterType("swap").getName());

		ref = ((IExistsStatement) ((IBinaryOperatorStatement) rule.getConstraint()).getLeft())
				.getElement();
		assertElement(ref, IVariable.class, 0, "header", "TradeHeader",
				IModelReference.REFERENCE_VARIABLE_RELATIVE_ATTRIBUTE);
		ref = ((IExistsStatement) ((IBinaryOperatorStatement) rule.getConstraint()).getRight())
				.getElement();
		assertElement(ref, IVariable.class, 1, "swap", "IRLeg",
				IModelReference.REFERENCE_VARIABLE_RELATIVE_ATTRIBUTE);
	}

	/**
	 * Test if duplicate rule ids are detected
	 */
	@Test
	public void testResolveActions() throws Exception {
		RuleFileImpl ruleFile = (RuleFileImpl) getRuleFile("src/test/resources/parsing/model-actions.nrl");

		// Run the normal resolver first
		ActionAstResolver resolver = new ActionAstResolver();
		List<NRLError> errors = resolver.resolve(ruleFile);
		for (NRLError error : errors)
			System.err.println(error);
		assertEquals(0, errors.size());

		// System.out.println(ruleFile.dump(0));

		// Now load a model and run the model resolver
		ModelCollection models = new ModelCollection();
		models.addModelPackage(getBasicModel());

		// Run the model resolver
		AntlrModelResolver modelRes = new AntlrModelResolver(models);
		errors = modelRes.resolve(ruleFile);
		for (NRLError error : errors)
			System.err.println(error);

		int firstInvalidLine = ruleFile.getDeclarationById("invalid-1").getLine();
		for (NRLError error : errors)
			assertTrue("Rule that should be valid failed, line: " + error.getLine(),
					error.getLine() >= firstInvalidLine);

		assertEquals(20, errors.size());

		// Check the context references
		assertEquals("Trade", ((ISingleContextDeclaration) ruleFile.getDeclarationById("set-1"))
				.getContext().getName());

		// Now check that all refs are ok - set-1
		IActionRuleDeclaration rule = (IActionRuleDeclaration) ruleFile.getDeclarationById("set-1");
		IModelReference ref = ((ISetAction) ((ICompoundAction) rule.getAction())
				.getSimpleActions().get(0)).getTarget();
		assertElement(ref, IAttribute.class, 0, "tradeDate", "Date",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		// Deletion of an attribute of the context - delete-1
		rule = (IActionRuleDeclaration) ruleFile.getDeclarationById("delete-1");
		ref = ((IRemoveAction) ((ICompoundAction) rule.getAction())
				.getSimpleActions().get(0)).getTarget();
		assertElement(ref, IAttribute.class, 0, "tradeDate", "Date",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		// Deletion of a type given a query - delete-2
		rule = (IActionRuleDeclaration) ruleFile.getDeclarationById("delete-2");
		ref = ((IRemoveAction) ((ICompoundAction) rule.getAction())
				.getSimpleActions().get(0)).getTarget();
		assertElement(ref, IModelElement.class, 0, "Trade", "Trade",
				IModelReference.REFERENCE_ELEMENT);

		ref = (IModelReference) ((IBinaryPredicate) ((IBinaryOperatorStatement) ((IRemoveAction) ((ICompoundAction) rule
				.getAction()).getSimpleActions().get(0)).getWhere()).getLeft()).getLeft();
		assertElement(ref, IVariable.class, 1, "tradeDate", "Date",
				IModelReference.REFERENCE_VARIABLE_RELATIVE_ATTRIBUTE);

		// Deletion of a member of a collection - delete-3
		rule = (IActionRuleDeclaration) ruleFile.getDeclarationById("delete-3");
		ref = ((IRemoveAction) ((ICompoundAction) rule.getAction())
				.getSimpleActions().get(0)).getTarget();
		assertElement(ref, IAttribute.class, 0, "legs", "IRLeg",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		// Conditional - if-1
		rule = (IActionRuleDeclaration) ruleFile.getDeclarationById("if-1");
		IConditionalAction cond = (IConditionalAction) ((ICompoundAction) rule.getAction())
				.getSimpleActions().get(0);

		ref = (IModelReference) ((IBinaryPredicate) cond.getIf()).getLeft();
		assertElement(ref, IAttribute.class, 0, "tradeDate", "Date",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		ref = ((ISetAction) (cond.getThen()).getSimpleActions().get(0))
				.getTarget();
		assertElement(ref, IAttribute.class, 0, "tradeDate", "Date",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		ref = ((ISetAction) (cond.getElse()).getSimpleActions().get(0))
				.getTarget();
		assertElement(ref, IAttribute.class, 0, "tradeDate", "Date",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		// Creation - create-1
		rule = (IActionRuleDeclaration) ruleFile.getDeclarationById("create-1");
		ICreateAction create = (ICreateAction) ((ICompoundAction) rule.getAction())
				.getSimpleActions().get(0);
		IModelElement element = create.getElement();
		assertEquals("Trade", element.getName());

		// Compound action with creation - create-compound-1. Tests variables
		rule = (IActionRuleDeclaration) ruleFile.getDeclarationById("create-compound-1");
		create = (ICreateAction) ((ICompoundAction) rule.getAction()).getSimpleActions().get(0);
		ISetAction set = (ISetAction) ((ICompoundAction) rule.getAction()).getSimpleActions()
				.get(1);
		assertEquals("Trade", create.getElement().getName());
		assertEquals("trade", create.getVariable().getName());
		assertTrue(create.getElement() == create.getVariable().getBoundElement());

		ref = set.getTarget();
		assertElement(ref, IVariable.class, 1, "t", "Date",
				IModelReference.REFERENCE_VARIABLE_RELATIVE_ATTRIBUTE);

		// Add - add-1
		rule = (IActionRuleDeclaration) ruleFile.getDeclarationById("add-1");
		IAddAction add = (IAddAction) ((ICompoundAction) rule.getAction()).getSimpleActions()
				.get(1);
		assertElement((IModelReference) add.getSource(), IVariable.class, 0, "leg", "IRLeg",
				IModelReference.REFERENCE_VARIABLE_RELATIVE_ATTRIBUTE);
		assertElement(add.getTo(), IAttribute.class, 0, "legs", "IRLeg",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		// Remove - remove-1
		rule = (IActionRuleDeclaration) ruleFile.getDeclarationById("remove-1");
		IRemoveFromCollectionAction remove = (IRemoveFromCollectionAction) ((ICompoundAction) rule
				.getAction()).getSimpleActions().get(2);
		assertElement(remove.getElement(), IVariable.class, 0, "leg", "IRLeg",
				IModelReference.REFERENCE_VARIABLE_RELATIVE_ATTRIBUTE);
		assertElement(remove.getFrom(), IAttribute.class, 0, "legs", "IRLeg",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		// Action macro declaration - macro-1
		IActionFragmentDeclaration macro = (IActionFragmentDeclaration) ruleFile
				.getDeclarationById("macro-1");
		assertEquals("IRSwap", macro.getContextType("swap").getName());
		set = (ISetAction) ((ICompoundAction) macro.getAction()).getSimpleActions().get(0);
		assertElement(set.getTarget(), IVariable.class, 1, "swap", "Date",
				IModelReference.REFERENCE_VARIABLE_RELATIVE_ATTRIBUTE);

		// TODO implement test for macros

		// Iteration - iterate-1
		rule = (IActionRuleDeclaration) ruleFile.getDeclarationById("iterate-1");
		IForEachAction forEach = (IForEachAction) ((ICompoundAction) rule.getAction())
				.getSimpleActions().get(0);
		assertElement(forEach.getCollection(), IAttribute.class, 0, "legs", "IRLeg",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);
		ref = ((ISetAction) ((ICompoundAction) forEach.getAction()).getSimpleActions().get(0))
				.getTarget();
		assertElement(ref, IAttribute.class, 0, "fixFloat", "FixFloatEnum",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		// Iteration with variable - iterate-2
		rule = (IActionRuleDeclaration) ruleFile.getDeclarationById("iterate-2");
		forEach = (IForEachAction) ((ICompoundAction) rule.getAction()).getSimpleActions().get(0);
		assertElement(forEach.getCollection(), IAttribute.class, 0, "legs", "IRLeg",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);

		assertEquals("leg", forEach.getVariable().getName());
		assertTrue(forEach.getVariable().isBoundToElement());
		assertEquals("IRLeg", forEach.getVariable().getBoundElement().getName());

		ref = ((ISetAction) ((ICompoundAction) forEach.getAction()).getSimpleActions().get(0))
				.getTarget();
		assertElement(ref, IVariable.class, 1, "leg", "FixFloatEnum",
				IModelReference.REFERENCE_VARIABLE_RELATIVE_ATTRIBUTE);

		// variable-1 (variable assigned to a model element)
		rule = (IActionRuleDeclaration) ruleFile.getDeclarationById("variable-1");
		IVariableDeclarationAction decl = (IVariableDeclarationAction) ((ICompoundAction) rule
				.getAction()).getSimpleActions().get(0);
		assertEquals("x", decl.getVariableName());
		assertEquals("Date", decl.getVariableReference().getBoundElement().getName());

		set = (ISetAction) ((ICompoundAction) rule.getAction()).getSimpleActions().get(1);
		ref = (IModelReference) set.getExpression();
		assertElement(ref, IVariable.class, 0, "x", "Date",
				IModelReference.REFERENCE_VARIABLE_RELATIVE_ATTRIBUTE);

		// variable-2 (variable assigned to expression)
		rule = (IActionRuleDeclaration) ruleFile.getDeclarationById("variable-2");
		decl = (IVariableDeclarationAction) ((ICompoundAction) rule.getAction()).getSimpleActions()
				.get(0);
		assertEquals("x", decl.getVariableName());
		assertEquals("2005-12-30", ((ILiteralString) decl.getVariableReference()
				.getBoundExpression()).getString());

		set = (ISetAction) ((ICompoundAction) rule.getAction()).getSimpleActions().get(1);
		ref = (IModelReference) set.getExpression();
		assertElement(ref, IVariable.class, 0, "x", null,
				IModelReference.REFERENCE_VARIABLE_RELATIVE_ATTRIBUTE);

		// variable-4 (variable assigned to global element)
		rule = (IActionRuleDeclaration) ruleFile.getDeclarationById("variable-4");
		decl = (IVariableDeclarationAction) ((ICompoundAction) rule.getAction()).getSimpleActions()
				.get(0);
		assertEquals("x", decl.getVariableName());

		assertTrue(decl.getVariableReference().isBoundToElement());
		assertEquals("TradeHeader", decl.getVariableReference().getBoundElement().getName());
		ref = (IModelReference) decl.getExpression();
		assertElement(ref, IModelElement.class, 0, "TradeHeader", "TradeHeader",
				IModelReference.REFERENCE_ELEMENT);

		// global-var (global variable reference)
		rule = (IActionRuleDeclaration) ruleFile.getDeclarationById("global-var");
		set = (ISetAction) ((ICompoundAction) rule.getAction()).getSimpleActions().get(0);
		assertElement((IModelReference) set.getExpression(), IVariable.class, 0, "mydate", null,
				IModelReference.REFERENCE_GLOBAL_VARIABLE);

		// no-context (rule without a context)
		rule = (IActionRuleDeclaration) ruleFile.getDeclarationById("no-context");
		assertEquals("None", rule.getContext().getName());
		assertTrue(!rule.hasContext());
		create = (ICreateAction) ((ICompoundAction) rule.getAction()).getSimpleActions().get(0);
		assertEquals("Trade", create.getElement().getName());

		// additional-parameters-1
		rule = (IActionRuleDeclaration) ruleFile.getDeclarationById("additional-parameters-1");
		assertEquals("String", rule.getAdditionalParameterType("foo").getName());
	}

	/**
	 * Test whether operator parameters and return types are resolved properly
	 * 
	 * @throws Exception
	 */
	@Test
	public void testResolve_operators() throws Exception {
		RuleFileImpl ruleFile = (RuleFileImpl) getRuleFile("src/test/resources/parsing/operators.nrl");

		// Run the normal resolver first
		ConstraintAstResolver resolver = new ConstraintAstResolver();
		List<NRLError> errors = resolver.resolve(ruleFile);
		assertEquals(0, errors.size());

		// Now load a model and run the model resolver
		ModelCollection models = new ModelCollection();
		models.addModelPackage(getBasicModel());
		models.addModelPackage(getSimpleModel());

		XmlOperatorPersistence loader = new XmlOperatorPersistence();
		LoadedVersion version = loader.load(new File("src/test/resources/parsing/operators.xml"));
		assertEquals(LoadedVersion.Version14, version);

		IOperators operators = loader.getOperators();
		assertNotNull(operators);

		assertEquals(0, operators.resolveModelReferences(models).size());

		AntlrOperatorResolverVisitor vis = new AntlrOperatorResolverVisitor(
				new IOperators[] { operators });
		ruleFile.accept(vis);

		// Run the model resolver
		AntlrModelResolver modelRes = new AntlrModelResolver(models);
		errors = modelRes.resolve(ruleFile);
		for (Iterator<NRLError> iter = errors.iterator(); iter.hasNext();)
			System.out.println("ERROR: " + iter.next());
		assertEquals(3, errors.size());

		assertEquals(12, ((SemanticError) errors.get(0)).getLine());

		IConstraintRuleDeclaration rule = (IConstraintRuleDeclaration) ruleFile.getRuleById("r1");
		IVariableDeclaration var = rule.getVariableDeclarations().get(0);
		assertTrue(var.getExpression() instanceof IOperatorInvocation);
		assertNotNull(var.getVariableReference().getBoundElement());
		assertTrue(rule.getConstraint() instanceof IBinaryPredicate);

		IModelReference ref = (IModelReference) ((IBinaryPredicate) rule.getConstraint()).getLeft();
		assertTrue(ref.getInitialStep() instanceof IVariable);
		assertNotNull(((IVariable) ref.getInitialStep()).getBoundElement());
	}

	@Test
	public void testResolve_ruleSet() throws Exception {
		RuleFileImpl ruleFile = (RuleFileImpl) getRuleFile("src/test/resources/parsing/model-ruleset.nrl");

		// Run the normal resolver first
		ConstraintAstResolver resolver = new ConstraintAstResolver();
		List<NRLError> errors = resolver.resolve(ruleFile);
		assertEquals(0, errors.size());

		// Now load a model and run the model resolver
		ModelCollection models = new ModelCollection();
		models.addModelPackage(getBasicModel());
		models.addModelPackage(getSimpleModel());

		// Run the model resolver
		AntlrModelResolver modelRes = new AntlrModelResolver(models);
		errors = modelRes.resolve(ruleFile);
		assertEquals(0, errors.size());

		// Check that the context of the rule set was resolved ok
		IRuleSetDeclaration decl = ruleFile.getRuleSetDeclarations().get(0);
		assertEquals("Trade", decl.getPreconditionContext().getName());

		// Check that attributes referred to in the precondition are resolved
		IBinaryPredicate pred = (IBinaryPredicate) decl.getPreconditionConstraint();
		IModelReference ref = (IModelReference) pred.getLeft();
		assertElement(ref, IAttribute.class, 0, "tradeDate", "Date",
				IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);
	}

	/*
	 * Make assertions over a reference - the type of initial steps, how many additional steps, the
	 * attribute of the first step and the target element. Also checks that the reference type is
	 * set to the expected value.
	 */
	private void assertElement(IModelReference ref, Class<?> initialStepType, int additionalSteps,
			String firstStep, String target, int referenceType) {
		assertNotNull(ref.getInitialStep());
		assertTrue(initialStepType.isAssignableFrom(ref.getInitialStep().getClass()));
		assertEquals(additionalSteps, ref.getRemainingSteps().size());

		if (initialStepType == IAttribute.class)
			assertEquals(firstStep, ((IAttribute) ref.getInitialStep()).getName());
		else if (initialStepType == IModelElement.class)
			assertEquals(firstStep, ((IModelElement) ref.getInitialStep()).getName());

		if (target == null)
			assertTrue(ref.getTarget() == null);
		else
			assertEquals(target, ref.getTarget().getName());

		assertEquals(referenceType, ref.getReferenceType());
	}
}
