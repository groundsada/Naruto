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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.nrl.parser.IStatusCode;
import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.SemanticError;
import net.sourceforge.nrl.parser.ast.IDeclaration;
import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.IRuleDeclaration;
import net.sourceforge.nrl.parser.ast.IRuleFile;
import net.sourceforge.nrl.parser.ast.IRuleSetDeclaration;
import net.sourceforge.nrl.parser.ast.ISingleContextDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.IExistsStatement;
import net.sourceforge.nrl.parser.ast.constraints.IValidationFragmentDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.IVariableDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.impl.ExistsStatementImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.ModelReferenceImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.ValidationFragmentApplicationImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.ValidationFragmentDeclarationImpl;

/**
 * A resolver class that performs basic processing of the AST and returns semantic errors if it
 * fails.
 * <p>
 * The main method is {@link #resolve(RuleFileImpl)}.
 * 
 * @author Christian Nentwich
 */
public class ConstraintAstResolver {

	// List of string ids populated by collectRuleIds()
	protected Set<String> ruleIds = new HashSet<String>();

	// List of string ids populated by collectRuleSetIds()
	protected Set<String> ruleSetIds = new HashSet<String>();

	// Map of string ids to IValidationFragmentDeclaration populated by
	// collectFragmentIds()
	protected Map<String, IValidationFragmentDeclaration> fragmentIds = new HashMap<String, IValidationFragmentDeclaration>();

	// Map of string ids to IVariableDeclaration populated by
	// collectGlobalVariableIds()
	protected Map<String, IVariableDeclaration> globalVariableIds = new HashMap<String, IVariableDeclaration>();

	/**
	 * Resolve the rule file and return a list of {@link SemanticError} objects if errors occurred.
	 * 
	 * @param ruleFile the rule file to resolve
	 * @return the list of errors
	 */
	public List<NRLError> resolve(RuleFileImpl ruleFile) {
		List<NRLError> errors = new ArrayList<NRLError>();

		collectIdentifiers(ruleFile, errors);

		resolveReferences(ruleFile, errors);
		resolveIncompleteExistsStatements(ruleFile, errors);

		resolveRuleSets(ruleFile, errors);

		validateRuleParameters(ruleFile, errors);

		return errors;
	}

	/**
	 * Collect all identifiable parts. The default implementation collects rule ids, property ids
	 * and rule set ids.
	 * 
	 * @param ruleFile the rule file
	 * @param errors errors, if any are found during collection
	 */
	protected void collectIdentifiers(RuleFileImpl ruleFile, List<NRLError> errors) {
		collectFragmentIds(ruleFile, errors);
		collectRuleIds(ruleFile, errors);
		collectRuleSetIds(ruleFile, errors);
		collectGlobalVariableIds(ruleFile, errors);
	}

	/**
	 * Collect all fragment ids
	 * 
	 * @param ruleFile the rule file
	 * @param errors errors, if any are found during collection
	 */
	protected void collectFragmentIds(final RuleFileImpl ruleFile, final List<NRLError> errors) {
		fragmentIds.clear();

		ruleFile.accept(new AntlrAstVisitor() {
			protected boolean visitBefore(Antlr3NRLBaseAst node) {

				// Visit property declarations
				if (node instanceof ValidationFragmentDeclarationImpl) {
					ValidationFragmentDeclarationImpl decl = (ValidationFragmentDeclarationImpl) node;
					if (fragmentIds.keySet().contains(decl.getId())) {
						errors.add(new SemanticError(IStatusCode.DUPLICATE_FRAGMENT,
								decl.getLine(), decl.getColumn(), "Duplicate fragment id: "
										+ decl.getId()));
					} else {
						fragmentIds.put(((IValidationFragmentDeclaration) node).getId(),
								(IValidationFragmentDeclaration) node);
					}
				}

				return true;
			}
		});
	}

	/**
	 * Collect all rule ids
	 * 
	 * @param ruleFile the rule file
	 * @param errors errors, if any are found during collection
	 */
	protected void collectRuleIds(final RuleFileImpl ruleFile, final List<NRLError> errors) {
		ruleIds.clear();

		ruleFile.accept(new AntlrAstVisitor() {
			protected boolean visitBefore(Antlr3NRLBaseAst node) {

				// Visit rule declarations
				if (node instanceof IRuleDeclaration) {
					IRuleDeclaration decl = (IRuleDeclaration) node;
					if (ruleIds.contains(decl.getId())) {
						errors.add(new SemanticError(IStatusCode.DUPLICATE_RULE, decl.getLine(),
								decl.getColumn(), "Duplicate rule id: " + decl.getId()));
					} else
						ruleIds.add(((IRuleDeclaration) node).getId());
				}

				return true;
			}
		});
	}

	/**
	 * Collect all rule set ids
	 * 
	 * @param ruleFile the rule file
	 * @param errors errors to add to , if any
	 */
	protected void collectRuleSetIds(final RuleFileImpl ruleFile, final List<NRLError> errors) {
		ruleSetIds.clear();

		for (IRuleSetDeclaration decl : ruleFile.getRuleSetDeclarations()) {
			if (ruleSetIds.contains(decl.getId())) {
				errors.add(new SemanticError(IStatusCode.DUPLICATE_RULESET, decl.getLine(), decl
						.getColumn(), "Duplicate rule set: " + decl.getId()));
			} else
				ruleSetIds.add(decl.getId());
		}
	}

	/**
	 * Collect global variables.
	 * 
	 * @param ruleFile the rule file
	 * @param errors error list
	 */
	protected void collectGlobalVariableIds(final RuleFileImpl ruleFile, final List<NRLError> errors) {
		globalVariableIds.clear();

		for (IVariableDeclaration decl : ruleFile.getGlobalVariableDeclarations()) {
			if (globalVariableIds.keySet().contains(decl.getVariableName())) {
				errors.add(new SemanticError(IStatusCode.DUPLICATE_GLOBAL_VARIABLE, decl.getLine(),
						decl.getColumn(), "Duplicate global variable: " + decl.getVariableName()));
			} else
				globalVariableIds.put(decl.getVariableName(), decl);
		}
	}

	/**
	 * Resolve references within the AST. This:
	 * <ul>
	 * <li>Visits all {@link com.modeltwozero.nrl.parser.ast.IValidationFragmentApplication} nodes
	 * and looks up the property being applied, setting a cross-reference.
	 * </ul>
	 * 
	 * @param ruleFile the rule file to process
	 * @param errors the error list to add to if errors occur
	 */
	protected void resolveReferences(final RuleFileImpl ruleFile, final List<NRLError> errors) {
		ruleFile.accept(new AntlrAstVisitor() {
			protected boolean visitBefore(Antlr3NRLBaseAst node) {

				// Visit property applications
				if (node instanceof ValidationFragmentApplicationImpl) {
					ValidationFragmentApplicationImpl prop = (ValidationFragmentApplicationImpl) node;

					ValidationFragmentDeclarationImpl decl = (ValidationFragmentDeclarationImpl) fragmentIds
							.get(prop.getFragmentName());
					if (decl == null) {
						errors.add(new SemanticError(IStatusCode.INVALID_FRAGMENT_REF, prop
								.getLine(), prop.getColumn(),
								"Rule references undeclared fragment: " + prop.getFragmentName()));
					} else
						prop.setFragment(decl);
				}

				return true;
			}
		});
	}

	/**
	 * This resolves inclompete exists statements. This is a rather tricky method. It replaces
	 * statements like "one trade has a = b and one has a = c" with a complete statement like "one
	 * trade has a = b and one trade has a = c", by determining which model reference came up in the
	 * tree before the incomplete exists statement.
	 * 
	 * @param ruleFile the rule file
	 * @param errors the error list to add to
	 */
	protected void resolveIncompleteExistsStatements(final RuleFileImpl ruleFile,
			final List<NRLError> errors) {

		// Process rule-by-rule and property-by-property
		for (IDeclaration declNode : ruleFile.getDeclarations()) {

			// Build up a depth-first list of all exists statements in the
			// declaration
			final List<IExistsStatement> allExists = new ArrayList<IExistsStatement>();
			declNode.accept(new AntlrAstVisitor() {
				protected boolean visitBefore(Antlr3NRLBaseAst node) {
					// Visit existence statements
					if (node instanceof IExistsStatement)
						allExists.add((IExistsStatement) node);
					return true;
				}
			});

			// Now visit the exists statements without element ref and
			// resolve them
			declNode.accept(new AntlrAstVisitor() {
				protected boolean visitBefore(Antlr3NRLBaseAst node) {
					if (node instanceof ExistsStatementImpl) {
						ExistsStatementImpl exists = (ExistsStatementImpl) node;

						// If no reference, search all exists declarations
						// BEFORE this one in the rule tree to find one
						if (exists.getElement() == null) {
							for (int i = 0; i < allExists.size(); i++) {
								if (allExists.get(i) == exists)
									break;
								ExistsStatementImpl other = (ExistsStatementImpl) allExists.get(i);
								if (other.getElement() != null) {
									exists.setElement(new ModelReferenceImpl(
											((ModelReferenceImpl) other.getElement())));
									return true;
								}
							}

							errors
									.add(new SemanticError(IStatusCode.AMBIGUOUS_EXISTENCE, exists
											.getLine(), exists.getColumn(),
											"Ambiguous existance statement: need to refer to an attribute"));
						}
					}
					return true;
				}
			});
		}

		// Now visit the file again and and try to resolve
	}

	/**
	 * Resolve rule set references. This ensures that
	 * <ul>
	 * <li>All rule sets reference the rules contained in them
	 * <li>All rules back-reference the rule set that contains them (or none)
	 * </ul>
	 * 
	 * @param ruleFile the rule file to process
	 * @param errors the error list to add to
	 */
	protected void resolveRuleSets(RuleFileImpl ruleFile, List<NRLError> errors) {
		// Process on the AST level. Whenever a rule set occurs, set the current
		// rule set to it and add any following rule declaration into it.

		RuleSetDeclarationImpl currentSet = null;

		for (Object child : ruleFile.getChildren()) {
			if (child instanceof RuleSetDeclarationImpl) {
				currentSet = (RuleSetDeclarationImpl) child;
			} else if (child instanceof IRuleDeclaration) {
				if (currentSet != null) {
					currentSet.addRule((IRuleDeclaration) child);
					((IRuleDeclaration) child).setRuleSet(currentSet);
				}
			}
		}
	}

	/**
	 * Ensure that the names of rule parameters are ok.
	 * 
	 * @param ruleFile the rule file
	 * @param errors the errors to add to
	 */
	protected void validateRuleParameters(IRuleFile ruleFile, List<NRLError> errors) {
		for (IDeclaration decl : ruleFile.getDeclarations()) {
			if (decl instanceof ISingleContextDeclaration) {
				SingleContextDeclarationImpl singleContextDeclaration = (SingleContextDeclarationImpl) decl;

				Set<String> names = new HashSet<String>();
				for (String name : singleContextDeclaration.getAdditionalParameterNames()) {
					if (names.contains(name)) {
						IModelReference ref = singleContextDeclaration
								.getAdditionalParameterTypeReference(name);
						errors.add(new SemanticError(IStatusCode.DUPLICATE_RULE_PARAMETER, ref
								.getLine(), ref.getColumn(), "Duplicate rule parameter: '" + name
								+ "'."));
					}
					names.add(name);
				}
			}
		}
	}
}
