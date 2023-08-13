/*
 * The contents of this file are subject to the Mozilla Public License 
 * Version 1.1 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See 
 * the License for the specific language governing rights and limitations 
 * under the License.
 * 
 * The Original Code is the NRL Parser, released 28 April 2006, 
 * Copyright (c) Christian Nentwich. The Initial Developer of the 
 * Original Code is Christian Nentwich. Portions created by contributors 
 * identified in the NOTICES file are Copyright (c) the contributors. 
 * All Rights Reserved. 
 */
package net.sourceforge.nrl.parser.type;

import static net.sourceforge.nrl.parser.ast.NRLDataType.Type.Boolean;
import static net.sourceforge.nrl.parser.ast.NRLDataType.Type.Element;
import static net.sourceforge.nrl.parser.ast.NRLDataType.Type.Unknown;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.nrl.parser.IStatusCode;
import net.sourceforge.nrl.parser.ast.ActionVisitorDispatcher;
import net.sourceforge.nrl.parser.ast.IDeclaration;
import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.IRuleDeclaration;
import net.sourceforge.nrl.parser.ast.IRuleFile;
import net.sourceforge.nrl.parser.ast.IRuleSetDeclaration;
import net.sourceforge.nrl.parser.ast.NRLDataType;
import net.sourceforge.nrl.parser.ast.action.IActionFragmentApplicationAction;
import net.sourceforge.nrl.parser.ast.action.IActionFragmentDeclaration;
import net.sourceforge.nrl.parser.ast.action.IActionRuleDeclaration;
import net.sourceforge.nrl.parser.ast.action.IAddAction;
import net.sourceforge.nrl.parser.ast.action.ICompoundAction;
import net.sourceforge.nrl.parser.ast.action.IConditionalAction;
import net.sourceforge.nrl.parser.ast.action.ICreateAction;
import net.sourceforge.nrl.parser.ast.action.IForEachAction;
import net.sourceforge.nrl.parser.ast.action.INRLActionDetailVisitor;
import net.sourceforge.nrl.parser.ast.action.IOperatorAction;
import net.sourceforge.nrl.parser.ast.action.IRemoveAction;
import net.sourceforge.nrl.parser.ast.action.IRemoveFromCollectionAction;
import net.sourceforge.nrl.parser.ast.action.ISetAction;
import net.sourceforge.nrl.parser.ast.action.IVariableDeclarationAction;
import net.sourceforge.nrl.parser.ast.constraints.IConstraintRuleDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.IExpression;
import net.sourceforge.nrl.parser.ast.constraints.IValidationFragmentDeclaration;
import net.sourceforge.nrl.parser.model.IDataType;
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.model.PrimitiveTypeFactory;

/**
 * An extension of the type checker for the action language.
 * 
 * @author Christian Nentwich
 */
public class ActionTypeChecker extends ConstraintTypeChecker implements INRLActionDetailVisitor {

	@Override
	protected void checkRemainingDeclarations(IRuleFile ruleFile) {
		for (IDeclaration decl : ruleFile.getDeclarations()) {
			if (!(decl instanceof IValidationFragmentDeclaration)) {
				decl.accept(new ActionVisitorDispatcher(this));
			}
		}

		// Check any rules that are in no rule set - no mixing of
		// actions and constraints
		boolean constraints = false;
		boolean actions = false;

		List<IRuleDeclaration> rulesWithoutSets = getRulesWithoutSets(ruleFile);
		for (IRuleDeclaration rule : rulesWithoutSets) {
			if (rule instanceof IActionRuleDeclaration)
				actions = true;
			if (rule instanceof IConstraintRuleDeclaration)
				constraints = true;
		}

		if (actions && constraints) {
			for (IRuleDeclaration rule : rulesWithoutSets) {
				error(IStatusCode.RULESET_MIXES_RULETYPES, rule,
						"Constraint and action rules must not be mixed in the same rule set (even the default rule set)");
			}
		}
	}

	private List<IRuleDeclaration> getRulesWithoutSets(IRuleFile ruleFile) {
		// Make a list of all rules that are in sets
		Set<String> idsInSets = new HashSet<String>();

		for (IRuleSetDeclaration set : ruleFile.getRuleSetDeclarations()) {
			for (IRuleDeclaration rule : set.getRules()) {
				idsInSets.add(rule.getId());
			}
		}

		List<IRuleDeclaration> result = new ArrayList<IRuleDeclaration>();
		for (IDeclaration decl : ruleFile.getDeclarations()) {
			if (decl instanceof IRuleDeclaration) {
				IRuleDeclaration ruleDecl = (IRuleDeclaration) decl;
				if (!idsInSets.contains(ruleDecl.getId()))
					result.add((IRuleDeclaration) decl);
			}
		}

		return result;
	}

	/*
	 * Perform the following checks: <ul> <li>Context of the action fragment is the same
	 * as the declaration </ul>
	 */
	public void visitActionFragmentApplicationActionAfter(IActionFragmentApplicationAction action) {

		IActionFragmentDeclaration decl = action.getFragment();

		if (decl.getContextNames().size() != action.getParameters().size()) {
			error(IStatusCode.ACTION_FRAGMENT_PARAMETER_MISMATCH, action, "Action fragment \""
					+ decl.getId() + "\" requires " + decl.getContextNames().size()
					+ " arguments, found " + action.getParameters().size() + " here");
		} else {
			int count = 0;

			for (String param : decl.getContextNames()) {
				IModelElement declaredType = decl.getContextType(param);
				NRLDataType declaredNRLType = getType(declaredType);

				IExpression passed = action.getParameters().get(count);

				// Declared type is an element?
				if (declaredNRLType.getType() == Unknown || declaredNRLType.getType() == Element) {

					// Must be subclass if both are elements
					if (passed instanceof IModelReference) {
						if (!declaredType.isAssignableFrom(((IModelReference) passed).getTarget())) {
							error(IStatusCode.ACTION_FRAGMENT_PARAMETER_MISMATCH, passed,
									"Action Fragment argument of type "
											+ ((IModelReference) passed).getTarget().getName()
											+ " is not compatible with expected type "
											+ declaredType.getName());
						}
					}
				}

				count++;
			}
		}

		// if (action.getTarget() != null) {
		// if (action.getTarget().getTarget() != decl.getContext()) {
		// error(IStatusCode.PROPERTY_CONTEXT_MISMATCH, action, "Macro applied
		// to '"
		// + action.getTarget().getTarget().getName()
		// + "' but declared for '" + decl.getContext().getName() + "'");
		// }
		// }
	}

	public void visitActionFragmentDeclarationAfter(IActionFragmentDeclaration decl) {

	}

	public void visitActionRuleDeclarationAfter(IActionRuleDeclaration decl) {

	}

	/*
	 * Chech that the target is a collection, and that types match.
	 */
	public void visitAddActionAfter(IAddAction add) {
		if (!add.getTo().getNRLDataType().isCollection()) {
			error(IStatusCode.ADD_NEEDS_COLLECTION, add.getTo(),
					"Cannot add to non-collection attribute");
		} else if (add.getSource().getNRLDataType().isCollection()) {
			error(IStatusCode.ADD_TYPES_INCOMPATIBLE, add.getSource(),
					"Cannot add a collection to a collection");
		} else if (!isAssignmentCompatible(add.getTo().getNRLDataType(), add.getSource()
				.getNRLDataType())) {
			error(IStatusCode.ADD_TYPES_INCOMPATIBLE, add.getSource(),
					"The NRL type of the collection, '"
							+ add.getTo().getNRLDataType().getType().name()
							+ "', is not compatible with '"
							+ add.getSource().getNRLDataType().getType().name());
		} else if (add.getTo().getNRLDataType().getType() == Element) {
			if (!(add.getSource() instanceof IModelReference)) {
				error(IStatusCode.ADD_TYPES_INCOMPATIBLE, add.getTo(),
						"Cannot add an expression to a complex collection");
			} else {
				IModelElement target = ((IModelReference) add.getSource()).getTarget();
				if (!add.getTo().getTarget().isAssignableFrom(target)) {
					error(IStatusCode.ADD_TYPES_INCOMPATIBLE, add.getTo(),
							"Cannot add incompatible types: '"
									+ (target != null ? target.getName() : "") + "' and '"
									+ add.getTo().getTarget().getName() + "'");
				}
			}
		}
	}

	/*
	 * Nothing to do.
	 */
	public void visitCompoundActionAfter(ICompoundAction action) {

	}

	/*
	 * Check that then if part is a proper boolean statement
	 */
	public void visitConditionalActionAfter(IConditionalAction action) {
		if (action.getIf().getNRLDataType().getType() != Boolean) {
			error(IStatusCode.IF_ARGUMENTS_NOT_BOOLEAN, action.getIf(),
					"The 'if' part of an if-statement needs a Boolean condition");
		}
	}

	/*
	 * Nothing to check.
	 */
	public void visitCreateActionAfter(ICreateAction action) {
	}

	/*
	 * Check that if there is a where clause, it's a boolean statement. Check that if the
	 * target is not a collection, there is no where clause (doesn't make sense)
	 */
	public void visitRemoveActionAfter(IRemoveAction action) {
		if (action.getWhere() != null) {
			if (action.getWhere().getNRLDataType().getType() != Boolean) {
				error(IStatusCode.WHERE_NOT_BOOLEAN, action.getWhere(),
						"Deletion where clause must be a boolean condition");
			}

			if (!action.getTarget().getNRLDataType().isCollection()
					&& action.getTarget().getReferenceType() != IModelReference.REFERENCE_ELEMENT) {
				error(IStatusCode.REMOVE_NEEDS_COLLECTION, action.getTarget(),
						"Can only specify 'where' conditions with collections or classifiers");
			}
		}
	}

	/*
	 * Check that the iteration is over a collection
	 */
	public void visitForEachActionAfter(IForEachAction action) {
		if (!action.getCollection().getNRLDataType().isCollection()) {
			error(IStatusCode.ITERATION_NEEDS_COLLECTION, action.getCollection(),
					"Can only apply 'for each' to collections");
		}
	}

	public void visitOperatorActionAfter(IOperatorAction action) {
		if (action.getOperator() == null)
			return;

		// Use parameter checking from superclass
		visitOperatorParameters(action.getOperator(), action, action.getParameters());
	}

	/*
	 * Check if the target is a collection, and if the element to be removed is compatible
	 * with the collection.
	 */
	public void visitRemoveFromCollectionActionAfter(IRemoveFromCollectionAction action) {
		if (!action.getFrom().getNRLDataType().isCollection()) {
			error(IStatusCode.REMOVE_NEEDS_COLLECTION, action.getFrom(),
					"Can only apply 'remove' to collections");
		} else if (action.getElement().getNRLDataType().isCollection()) {
			error(IStatusCode.REMOVE_CANNOT_REMOVE_COLLECTION, action.getElement(),
					"Cannot remove a collection from a collection");
		} else if (!action.getFrom().getTarget().isAssignableFrom(action.getElement().getTarget())) {
			error(IStatusCode.REMOVE_TYPES_INCOMPATIBLE, action.getFrom(),
					"Cannot remove incompatible types: '"
							+ action.getElement().getTarget().getName() + "' and '"
							+ action.getFrom().getTarget().getName() + "'");

		}
	}

	@Override
	public void visitRuleSetDeclarationAfter(IRuleSetDeclaration decl) {
		super.visitRuleSetDeclarationAfter(decl);

		// No mixing of action rules and constraint rules

		boolean hasActionRules = false;
		boolean hasConstraintRules = false;
		for (IRuleDeclaration ruleDecl : decl.getRules()) {
			if (ruleDecl instanceof IActionRuleDeclaration)
				hasActionRules = true;
			else if (ruleDecl instanceof IConstraintRuleDeclaration)
				hasConstraintRules = true;
		}

		if (hasActionRules && hasConstraintRules) {
			error(IStatusCode.RULESET_MIXES_RULETYPES, decl,
					"Rule sets may not mix action and constraint rules");
		}
	}

	/*
	 * Check the following properties: 1. If the target and the expression are both c
	 */
	public void visitSetActionAfter(ISetAction action) {
		IModelReference target = action.getTarget();

		if (target.getInitialStepType() == IModelReference.STEP_MODEL_ELEMENT) {
			error(IStatusCode.ILLEGAL_ASSIGNMENT_TARGET, target,
					"Set actions may not assign to a model element or element relative attribute");
			return;
		}

		/*
		 * if (target.getInitialStepType() == IModelReference.STEP_VARIABLE &&
		 * target.getRemainingSteps().size() == 0) {
		 * error(IStatusCode.ILLEGAL_ASSIGNMENT_TARGET, target, "Variables are immutable
		 * and may not be assigned to"); return; }
		 */
		if (action.getExpression() instanceof IModelReference) {
			IModelReference value = (IModelReference) action.getExpression();

			if (target.getNRLDataType().isCollection() && !value.getNRLDataType().isCollection()) {
				error(IStatusCode.ASSIGNMENT_TYPE_INCOMPATIBLE, value,
						"You cannot assign a value to a collection attribute");
				return;
			} else if (value.getReferenceType() == IModelReference.REFERENCE_ELEMENT) {
				error(IStatusCode.ASSIGNMENT_TYPE_INCOMPATIBLE, value,
						"You cannot assign a model element to an attribute");
				return;
			} else if (!isAssignmentCompatible(target.getNRLDataType(), value.getNRLDataType())) {
				String targetName = target.getTarget() != null ? target.getTarget().getName()
						: target.getNRLDataType().toString();
				String valueName = value.getTarget() != null ? value.getTarget().getName() : value
						.getNRLDataType().toString();
				error(IStatusCode.ASSIGNMENT_TYPE_INCOMPATIBLE, value, "'" + targetName
						+ "' is not compatible with '" + valueName + "', whose NRL type is: "
						+ value.getNRLDataType() + ". Check your type mapping.");
			} else if (value.getNRLDataType().getType() == Element
					&& !target.getTarget().isAssignableFrom(value.getTarget())) {
				String targetName = target.getTarget() != null ? target.getTarget().getName()
						: target.getNRLDataType().toString();
				String valueName = value.getTarget() != null ? value.getTarget().getName() : value
						.getNRLDataType().toString();
				error(IStatusCode.ASSIGNMENT_TYPE_INCOMPATIBLE, value, "Cannot set '" + targetName
						+ "' to '" + valueName + "': types incompatible");
			}
		} else {
			if (target.getNRLDataType().isCollection()
					&& !action.getExpression().getNRLDataType().isCollection()) {
				error(IStatusCode.ASSIGNMENT_TYPE_INCOMPATIBLE, action.getExpression(),
						"You cannot assign a value to a collection attribute");
				return;
			}

			if (!isAssignmentCompatible(target.getNRLDataType(), action.getExpression()
					.getNRLDataType())) {
				IDataType valueType = PrimitiveTypeFactory.getInstance().getType(
						action.getExpression().getNRLDataType());

				String typeName = valueType == null ? "Unknown" : valueType.getName();
				String targetName = target.getTarget() != null ? target.getTarget().getName()
						: target.getNRLDataType().toString();

				error(IStatusCode.ASSIGNMENT_TYPE_INCOMPATIBLE, action.getExpression(), "'"
						+ targetName + "' is not compatible with an NRL type of '" + typeName + "'");
			}
		}
	}

	public void visitVariableDeclarationActionAfter(IVariableDeclarationAction decl) {
	}

	public boolean visitVariableDeclarationActionBefore(IVariableDeclarationAction action) {
		action.getExpression().accept(new ActionVisitorDispatcher(this));
		action.getVariableReference().setNRLDataType(action.getExpression().getNRLDataType());
		return true;
	}

	// ---------------------------------------------------------------------
	// The BEFORE methods all return true and don't do anything, just
	// forward processing to children
	// ---------------------------------------------------------------------

	public boolean visitActionFragmentApplicationActionBefore(
			IActionFragmentApplicationAction action) {
		return true;
	}

	public boolean visitActionFragmentDeclarationBefore(IActionFragmentDeclaration decl) {
		return true;
	}

	public boolean visitActionRuleDeclarationBefore(IActionRuleDeclaration decl) {
		return true;
	}

	public boolean visitAddActionBefore(IAddAction add) {
		return true;
	}

	public boolean visitCompoundActionBefore(ICompoundAction action) {
		return true;
	}

	public boolean visitConditionalActionBefore(IConditionalAction action) {
		return true;
	}

	public boolean visitCreateActionBefore(ICreateAction action) {
		return true;
	}

	public boolean visitRemoveActionBefore(IRemoveAction action) {
		return true;
	}

	public boolean visitForEachActionBefore(IForEachAction action) {
		return true;
	}

	public boolean visitOperatorActionBefore(IOperatorAction action) {
		return true;
	}

	public boolean visitRemoveFromCollectionActionBefore(IRemoveFromCollectionAction action) {
		return true;
	}

	public boolean visitSetActionBefore(ISetAction action) {
		return true;
	}
}
