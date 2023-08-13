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
package net.sourceforge.nrl.parser.ast;

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

/**
 * An extension of the basic dispatching AST visitor class that adds action
 * language support.
 * <p>
 * Implementors of mappings of the full action language, including the base
 * constraint language, should implement this visitor class. For every node in
 * the AST, which is visited in a pre-order traversal, they will get a call to a
 * <code>before</code> method before a node is visited and a call to an
 * <code>after</code> method once its children have been visited.
 * <p>
 * To use the class, extend and implement all abstract methods.
 * 
 * @author Christian Nentwich
 */
public class ActionVisitorDispatcher extends ConstraintVisitorDispatcher {

	private INRLActionDetailVisitor actionVisitor;

	/**
	 * Initialise the dispatcher with a actionVisitor.
	 * 
	 * @param visitor the visitor
	 */
	public ActionVisitorDispatcher(INRLActionDetailVisitor visitor) {
		super(visitor);
		this.actionVisitor = visitor;
	}

	/**
	 * Dispatching method. No need to change.
	 */
	public void visitAfter(INRLAstNode node) {
		if (node instanceof IActionFragmentApplicationAction)
			actionVisitor
					.visitActionFragmentApplicationActionAfter((IActionFragmentApplicationAction) node);
		else if (node instanceof IActionFragmentDeclaration)
			actionVisitor
					.visitActionFragmentDeclarationAfter((IActionFragmentDeclaration) node);
		else if (node instanceof IActionRuleDeclaration)
			actionVisitor.visitActionRuleDeclarationAfter((IActionRuleDeclaration) node);
		else if (node instanceof IAddAction)
			actionVisitor.visitAddActionAfter((IAddAction) node);
		else if (node instanceof ICompoundAction)
			actionVisitor.visitCompoundActionAfter((ICompoundAction) node);
		else if (node instanceof IConditionalAction)
			actionVisitor.visitConditionalActionAfter((IConditionalAction) node);
		else if (node instanceof ICreateAction)
			actionVisitor.visitCreateActionAfter((ICreateAction) node);
		else if (node instanceof IRemoveAction)
			actionVisitor.visitRemoveActionAfter((IRemoveAction) node);
		else if (node instanceof IForEachAction)
			actionVisitor.visitForEachActionAfter((IForEachAction) node);
		else if (node instanceof IOperatorAction)
			actionVisitor.visitOperatorActionAfter((IOperatorAction) node);
		else if (node instanceof IRemoveFromCollectionAction)
			actionVisitor.visitRemoveFromCollectionActionAfter((IRemoveFromCollectionAction) node);
		else if (node instanceof ISetAction)
			actionVisitor.visitSetActionAfter((ISetAction) node);
		else if (node instanceof IVariableDeclarationAction)
			actionVisitor.visitVariableDeclarationActionAfter((IVariableDeclarationAction)node);
		else
			super.visitAfter(node);
	}

	/**
	 * Dispatching method. No need to change.
	 */
	public boolean visitBefore(INRLAstNode node) {
		if (node instanceof IActionFragmentApplicationAction)
			return actionVisitor
					.visitActionFragmentApplicationActionBefore((IActionFragmentApplicationAction) node);
		else if (node instanceof IActionFragmentDeclaration)
			return actionVisitor
					.visitActionFragmentDeclarationBefore((IActionFragmentDeclaration) node);
		else if (node instanceof IActionRuleDeclaration)
			return actionVisitor
					.visitActionRuleDeclarationBefore((IActionRuleDeclaration) node);
		else if (node instanceof IAddAction)
			return actionVisitor.visitAddActionBefore((IAddAction) node);
		else if (node instanceof ICompoundAction)
			return actionVisitor.visitCompoundActionBefore((ICompoundAction) node);
		else if (node instanceof IConditionalAction)
			return actionVisitor.visitConditionalActionBefore((IConditionalAction) node);
		else if (node instanceof ICreateAction)
			return actionVisitor.visitCreateActionBefore((ICreateAction) node);
		else if (node instanceof IRemoveAction)
			return actionVisitor.visitRemoveActionBefore((IRemoveAction) node);
		else if (node instanceof IForEachAction)
			return actionVisitor.visitForEachActionBefore((IForEachAction) node);
		else if (node instanceof IOperatorAction)
			return actionVisitor.visitOperatorActionBefore((IOperatorAction) node);
		else if (node instanceof IRemoveFromCollectionAction)
			return actionVisitor.visitRemoveFromCollectionActionBefore((IRemoveFromCollectionAction) node);
		else if (node instanceof ISetAction)
			return actionVisitor.visitSetActionBefore((ISetAction) node);
		else if (node instanceof IVariableDeclarationAction)
			return actionVisitor.visitVariableDeclarationActionBefore((IVariableDeclarationAction)node);
		return super.visitBefore(node);
	}

}
