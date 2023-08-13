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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.nrl.parser.IStatusCode;
import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.SemanticError;
import net.sourceforge.nrl.parser.ast.IDeclaration;
import net.sourceforge.nrl.parser.ast.IRuleFile;
import net.sourceforge.nrl.parser.ast.IRuleSetDeclaration;
import net.sourceforge.nrl.parser.ast.ISingleContextDeclaration;
import net.sourceforge.nrl.parser.ast.Variable;
import net.sourceforge.nrl.parser.ast.action.IAction;
import net.sourceforge.nrl.parser.ast.action.IActionFragmentDeclaration;
import net.sourceforge.nrl.parser.ast.action.IActionRuleDeclaration;
import net.sourceforge.nrl.parser.ast.action.impl.ActionFragmentDeclarationImpl;
import net.sourceforge.nrl.parser.ast.action.impl.ActionRuleDeclarationImpl;
import net.sourceforge.nrl.parser.ast.constraints.IConstraint;
import net.sourceforge.nrl.parser.ast.constraints.IConstraintRuleDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.IValidationFragmentDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.IVariableDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.impl.ConstraintRuleDeclarationImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.ExistsStatementImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.ModelReferenceImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.ValidationFragmentDeclarationImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.VariableDeclarationImpl;
import net.sourceforge.nrl.parser.model.AbstractClassifier;
import net.sourceforge.nrl.parser.model.AbstractPackage;
import net.sourceforge.nrl.parser.model.IClassifier;
import net.sourceforge.nrl.parser.model.IDataType;
import net.sourceforge.nrl.parser.model.IModelCollection;
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.model.IPackage;
import net.sourceforge.nrl.parser.model.PrimitiveTypeFactory;
import net.sourceforge.nrl.parser.model.IModelElement.ElementType;

/**
 * This class resolves all model references on the AST and replaces the string values with with
 * proper references to model elements. It may generate errors in this process, if model elements
 * are not found.
 * 
 * @author Christian Nentwich
 */
public class AntlrModelResolver {

	class NoneClassifier extends AbstractClassifier {
		public NoneClassifier(String name, IPackage container) {
			super(name, container);
		}

		public ElementType getElementType() {
			return IModelElement.ElementType.Classifier;
		}

		public List<String> getDocumentation() {
			return new ArrayList<String>();
		}
	}

	protected IModelCollection models;

	public AntlrModelResolver(IModelCollection models) {
		assert models != null;
		this.models = models;
	}

	protected void eliminateExistenceWithBackreference(IConstraint constraint,
			final IModelElement context) {

		constraint.accept(new AntlrAstVisitor() {
			protected void visitAfter(Antlr3NRLBaseAst node) {
				if (node instanceof ExistsStatementImpl) {
					ExistsStatementImpl exists = (ExistsStatementImpl) node;
					if (exists.getElement() != null
							&& exists.getElement().getInitialStep() == context) {
					}
				}
			}

			protected boolean visitBefore(Antlr3NRLBaseAst node) {
				return true;
			}
		});
	}

	/**
	 * Eliminate any "exists" nodes whose element reference refers to the context element. For
	 * example, if the context is "Trade" and the constraint is "The Trade has date = '2005-12-30'"
	 * then this method will eliminate the implied existence constraint "one trade has", and replace
	 * the formula with the subconstraint. The result formula is then: "date = '2005-12-30'".
	 * 
	 * @param ruleFile the file to process
	 */
	protected void eliminateExistenceWithBackreference(IRuleFile ruleFile) {
		for (IDeclaration d : ruleFile.getDeclarations()) {
			if (!(d instanceof ISingleContextDeclaration))
				continue;

			IModelElement context = ((ISingleContextDeclaration) d).getContext();
			if (context == null)
				continue;

			if (d instanceof IConstraintRuleDeclaration)
				eliminateExistenceWithBackreference(((IConstraintRuleDeclaration) d)
						.getConstraint(), context);
			else if (d instanceof IValidationFragmentDeclaration)
				eliminateExistenceWithBackreference(((IValidationFragmentDeclaration) d)
						.getConstraint(), context);
		}
	}

	/*
	 * Helper method that returns the global variables as a map from string to IVariableDeclaration.
	 */
	protected Map<String, IVariableDeclaration> getGlobalVariablesAsMap(IRuleFile ruleFile) {
		Map<String, IVariableDeclaration> result = new HashMap<String, IVariableDeclaration>();

		for (IVariableDeclaration decl : ruleFile.getGlobalVariableDeclarations()) {
			result.put(decl.getVariableName(), decl);
		}

		return result;
	}

	/**
	 * Main method - resolve all model references in the entire AST.
	 * 
	 * @param ruleFile the rule file AST
	 * @return errors, empty if no errors
	 */
	public List<NRLError> resolve(IRuleFile ruleFile) {
		List<NRLError> errors = new ArrayList<NRLError>();

		Map<String, IVariableDeclaration> globalVariables = getGlobalVariablesAsMap(ruleFile);

		resolveDeclarationsWithoutContext(ruleFile);
		resolveGlobalVariables(ruleFile, errors);
		resolveSingleContextDeclarations(ruleFile, errors);
		resolveMultiContextDeclarations(ruleFile, errors);
		resolveConstraints(ruleFile, globalVariables, errors);
		resolveRuleSets(ruleFile, globalVariables, errors);
		resolveActions(ruleFile, errors);

		eliminateExistenceWithBackreference(ruleFile);

		return errors;
	}

	protected void resolveAction(IAction action, IClassifier context,
			Map<String, IVariableDeclaration> globalVariables, List<Variable> variables,
			List<NRLError> errors) {
		action.accept(new AntlrModelResolverVisitor(context, models, globalVariables, variables,
				errors));
	}

	protected void resolveActionFragment(IActionFragmentDeclaration macro,
			Map<String, IVariableDeclaration> globalVariables, List<NRLError> errors) {

		List<Variable> variables = new ArrayList<Variable>();

		for (String param : macro.getContextNames()) {
			IModelElement element = macro.getContextType(param);

			Variable var = new Variable(param, element);
			var
					.setDeclarationNode(((ActionFragmentDeclarationImpl) macro)
							.getModelReference(param));
			variables.add(var);
		}

		macro.accept(new AntlrModelResolverVisitor(models, globalVariables, variables, errors));
	}

	protected void resolveActions(IRuleFile ruleFile, List<NRLError> errors) {
		Map<String, IVariableDeclaration> globals = getGlobalVariablesAsMap(ruleFile);

		for (IDeclaration decl : ruleFile.getDeclarations()) {
			// Don't resolve those that failed before

			if (decl instanceof ISingleContextDeclaration) {
				SingleContextDeclarationImpl single = (SingleContextDeclarationImpl) decl;
				if (single.getContext() == null)
					continue;
				if (!(single.getContext() instanceof IClassifier || single.getContext() instanceof IDataType))
					continue;
				if (!single.areAdditionalParametersFullyResolved())
					continue;
			} else if (decl instanceof MultipleContextDeclarationImpl) {
				MultipleContextDeclarationImpl multi = (MultipleContextDeclarationImpl) decl;
				if (!multi.isFullyResolved())
					continue;
			}

			if (decl instanceof IActionRuleDeclaration) {
				ActionRuleDeclarationImpl action = (ActionRuleDeclarationImpl) decl;

				List<Variable> variables = new ArrayList<Variable>();
				for (String paramName : action.getAdditionalParameterNames()) {
					Variable var = new Variable(paramName, action
							.getAdditionalParameterType(paramName));
					var.setDeclarationNode(action.getAdditionalParameterTypeReference(paramName));
					variables.add(var);
				}

				resolveAction(action.getAction(), (IClassifier) action.getContext(), globals,
						variables, errors);
			} else if (decl instanceof IActionFragmentDeclaration) {
				resolveActionFragment((IActionFragmentDeclaration) decl, globals, errors);
			}
		}
	}

	protected void resolveConstraints(IRuleFile ruleFile,
			Map<String, IVariableDeclaration> globalVariables, List<NRLError> errors) {

		for (IDeclaration d : ruleFile.getDeclarations()) {
			// Don't resolve those that failed before
			if (d instanceof ISingleContextDeclaration) {
				SingleContextDeclarationImpl single = (SingleContextDeclarationImpl) d;
				if (single.getContext() == null)
					continue;
				if (!(single.getContext() instanceof IClassifier || single.getContext() instanceof IDataType))
					continue;
				if (!single.areAdditionalParametersFullyResolved())
					continue;
			} else if (d instanceof MultipleContextDeclarationImpl) {
				MultipleContextDeclarationImpl multi = (MultipleContextDeclarationImpl) d;
				if (!multi.isFullyResolved())
					continue;
			} else
				continue;

			if (d instanceof IConstraintRuleDeclaration) {
				ConstraintRuleDeclarationImpl constraint = (ConstraintRuleDeclarationImpl) d;

				List<Variable> variables = new ArrayList<Variable>();
				for (String paramName : constraint.getAdditionalParameterNames()) {
					Variable var = new Variable(paramName, constraint
							.getAdditionalParameterType(paramName));
					var.setDeclarationNode(constraint
							.getAdditionalParameterTypeReference(paramName));
					variables.add(var);
				}

				constraint.accept(new AntlrModelResolverVisitor((IClassifier) constraint
						.getContext(), models, globalVariables, variables, errors));
			} else if (d instanceof IValidationFragmentDeclaration)
				resolveValidationFragmentDeclaration((IValidationFragmentDeclaration) d,
						globalVariables, errors);
		}
	}

	/**
	 * Resolve all action rule/macro declarations that do not have a context, i.e. a context of
	 * 'None'.
	 * 
	 * @param ruleFile the rule file
	 */
	protected void resolveDeclarationsWithoutContext(IRuleFile ruleFile) {
		// Create some representations for the "none" type that will
		// be inserted as a placeholder
		IPackage nonePackage = new AbstractPackage("nrl-none", null) {
			public List<String> getDocumentation() {
				return new ArrayList<String>();
			}
		};
		IClassifier noneClassifier = new AbstractClassifier("None", nonePackage) {
			public List<String> getDocumentation() {
				return new ArrayList<String>();
			}

			public ElementType getElementType() {
				return IModelElement.ElementType.Classifier;
			}

			public boolean isSupplementary() {
				return true;
			}
		};

		for (IDeclaration next : ruleFile.getDeclarations()) {
			if (next instanceof ActionRuleDeclarationImpl) {
				ActionRuleDeclarationImpl decl = (ActionRuleDeclarationImpl) next;
				if (!decl.hasContext()) {
					decl.setContext(noneClassifier);
				}
			}
		}
	}

	/*
	 * Set variable object on all global variable references.
	 */
	protected void resolveGlobalVariables(IRuleFile ruleFile, List<NRLError> errors) {
		for (IVariableDeclaration var : ruleFile.getGlobalVariableDeclarations()) {
			((VariableDeclarationImpl) var).setVariableReference(new Variable(
					var.getVariableName(), var.getExpression()));
		}
	}

	/*
	 * Resolve any declarations that support more than a single context (validation and action
	 * fragments)
	 */
	protected void resolveMultiContextDeclarations(IRuleFile ruleFile, List<NRLError> errors) {

		for (IDeclaration d : ruleFile.getDeclarations()) {
			if (d instanceof MultipleContextDeclarationImpl) {
				MultipleContextDeclarationImpl decl = (MultipleContextDeclarationImpl) d;

				// Skip if already done
				if (decl.isFullyResolved())
					continue;

				for (String paramName : decl.getContextNames()) {
					ModelReferenceImpl ref = (ModelReferenceImpl) decl.getModelReference(paramName);

					List<String> steps = ref.getStepsAsStrings();

					// More than one step not allowed
					if (steps.size() != 1) {
						errors.add(new SemanticError(IStatusCode.CONTEXT_NAVIGATION, ref.getLine(),
								ref.getColumn(),
								"Context element must refer directly to one model element"));
						continue;
					}

					String step = steps.iterator().next();

					IModelElement element = PrimitiveTypeFactory.getInstance().getType(step);
					if (element != null) {
						decl.setContextType(paramName, element);
					} else {
						element = ModelReferenceHelper.getModelElement(step, ref, models, errors);
						if (models.isAmbiguous(step)) {
							errors.add(new SemanticError(IStatusCode.ELEMENT_AMBIGUOUS, ref
									.getLine(), ref.getColumn(),

							step.length(), "Context element name '" + step
									+ "' occurs in multiple packages. Need absolute reference."));
							continue;
						}
						if (element == null) {
							errors.add(new SemanticError(IStatusCode.CONTEXT_UNKNOWN,
									ref.getLine(), ref.getColumn(), step.length(),
									"Context refers to unknown element: " + step));
							continue;
						}

						decl.setContextType(paramName, element);
					}
				}
			}
		}
	}

	protected void resolveValidationFragmentDeclaration(IValidationFragmentDeclaration decl,
			Map<String, IVariableDeclaration> globalVariables, List<NRLError> errors) {

		List<Variable> variables = new ArrayList<Variable>();

		for (String param : decl.getContextNames()) {
			IModelElement element = decl.getContextType(param);

			Variable var = new Variable(param, element);
			var.setDeclarationNode(((ValidationFragmentDeclarationImpl) decl)
					.getModelReference(param));
			variables.add(var);
		}

		decl.accept(new AntlrModelResolverVisitor(models, globalVariables, variables, errors));
	}

	/**
	 * Resolve the context and constraints of rule set declarations
	 * 
	 * @param ruleFile the rule file
	 * @param errors the error list to add to
	 */
	protected void resolveRuleSets(IRuleFile ruleFile,
			Map<String, IVariableDeclaration> globalVariables, List<NRLError> errors) {

		for (IRuleSetDeclaration d : ruleFile.getRuleSetDeclarations()) {
			RuleSetDeclarationImpl decl = (RuleSetDeclarationImpl) d;
			if (decl.getPreconditionConstraint() == null)
				continue;

			// TODO There is duplication here with the code above in
			// resolveDeclarations. This has to be sorted out at some point.
			ModelReferenceImpl ref = (ModelReferenceImpl) decl.getModelReference();
			List<String> steps = ref.getStepsAsStrings();

			// More than one step not allowed
			if (steps.size() != 1) {
				errors.add(new SemanticError(IStatusCode.CONTEXT_NAVIGATION, ref.getLine(), ref
						.getColumn(),
						"Rule set context element must refer directly to one model element"));
				continue;
			}

			String step = steps.iterator().next();
			IModelElement element = ModelReferenceHelper.getModelElement(step, ref, models, errors);
			if (element == null) {
				errors.add(new SemanticError(IStatusCode.CONTEXT_UNKNOWN, ref.getLine(), ref
						.getColumn(), step.length(), "Rule set context refers to unknown element: "
						+ step));
				continue;
			}

			if (models.isAmbiguous(step)) {
				errors.add(new SemanticError(IStatusCode.ELEMENT_AMBIGUOUS, ref.getLine(), ref
						.getColumn(), step.length(), "Rule set context element name '" + step
						+ "' occurs in multiple packages. Need absolute reference."));
				continue;
			}

			if (element.getElementType() != ElementType.Classifier
					&& element.getElementType() != ElementType.DataTypeWithAttributes) {
				errors.add(new SemanticError(IStatusCode.CONTEXT_SIMPLE_TYPE, ref.getLine(), ref
						.getColumn(), step.length(),
						"Context must not refer to a data type/simple type: " + step));
				continue;
			}

			decl.setContext(element);

			decl.getPreconditionConstraint().accept(
					new AntlrModelResolverVisitor((IClassifier) element, models, globalVariables,
							errors));
		}
	}

	protected void resolveSingleContextDeclarations(IRuleFile ruleFile, List<NRLError> errors) {

		for (IDeclaration d : ruleFile.getDeclarations()) {
			if (d instanceof SingleContextDeclarationImpl) {
				SingleContextDeclarationImpl decl = (SingleContextDeclarationImpl) d;

				// Skip if already done
				if (decl.getContext() != null)
					continue;

				ModelReferenceImpl ref = (ModelReferenceImpl) decl.getModelReference();
				List<String> steps = ref.getStepsAsStrings();

				// More than one step not allowed
				if (steps.size() != 1) {
					errors.add(new SemanticError(IStatusCode.CONTEXT_NAVIGATION, ref.getLine(), ref
							.getColumn(),
							"Context element must refer directly to one model element"));
					continue;
				}

				String step = steps.iterator().next();
				IModelElement element = ModelReferenceHelper.getModelElement(step, ref, models,
						errors);
				if (models.isAmbiguous(step)) {
					errors.add(new SemanticError(IStatusCode.ELEMENT_AMBIGUOUS, ref.getLine(), ref
							.getColumn(), step.length(), "Context element name '" + step
							+ "' occurs in multiple packages. Need absolute reference."));
					continue;
				}
				if (element == null) {
					errors.add(new SemanticError(IStatusCode.CONTEXT_UNKNOWN, ref.getLine(), ref
							.getColumn(), step.length(), "Context refers to unknown element: "
							+ step));
					continue;
				}
				if (element.getElementType() != ElementType.Classifier
						&& element.getElementType() != ElementType.DataTypeWithAttributes) {
					errors.add(new SemanticError(IStatusCode.CONTEXT_SIMPLE_TYPE, ref.getLine(),
							ref.getColumn(), step.length(),
							"Context must not refer to a data type/simple type: " + step));
					continue;
				}

				decl.setContext(element);

				// Resolve the additional parameters
				for (String paramName : decl.getAdditionalParameterNames()) {
					ref = (ModelReferenceImpl) decl.getAdditionalParameterTypeReference(paramName);

					steps = ref.getStepsAsStrings();

					// More than one step not allowed
					if (steps.size() != 1) {
						errors.add(new SemanticError(IStatusCode.CONTEXT_NAVIGATION, ref.getLine(),
								ref.getColumn(),
								"Parameter must refer directly to one model element"));
						continue;
					}

					step = steps.iterator().next();

					if (models.getElementByName(paramName) != null) {
						errors.add(new SemanticError(IStatusCode.RULE_PARAMETER_NAME_CLASH, ref
								.getLine(), ref.getColumn(), "Parameter name '" + paramName
								+ "' clashes with a global model element."));
					}
					if (((IClassifier) decl.getContext()).getAttributeByName(paramName, true) != null) {
						errors.add(new SemanticError(IStatusCode.RULE_PARAMETER_NAME_CLASH, ref
								.getLine(), ref.getColumn(), "Parameter name '" + paramName
								+ "' clashes with an attribute in the main rule context."));
					}

					element = PrimitiveTypeFactory.getInstance().getType(step);
					if (element != null) {
						decl.setAdditionalParameterType(paramName, element);
					} else {
						element = ModelReferenceHelper.getModelElement(step, ref, models, errors);
						if (models.isAmbiguous(step)) {
							errors.add(new SemanticError(IStatusCode.ELEMENT_AMBIGUOUS, ref
									.getLine(), ref.getColumn(),

							step.length(), "Parameter name '" + step
									+ "' occurs in multiple packages. Need absolute reference."));
							continue;
						}
						if (element == null) {
							errors.add(new SemanticError(IStatusCode.CONTEXT_UNKNOWN,
									ref.getLine(), ref.getColumn(), step.length(),
									"Parameter refers to unknown element: '" + step + "'."));
							continue;
						}

						decl.setAdditionalParameterType(paramName, element);
					}
				}
			}
		}
	}
}
