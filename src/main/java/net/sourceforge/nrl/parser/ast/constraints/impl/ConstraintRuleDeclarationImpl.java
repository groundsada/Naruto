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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.IRuleSetDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.ICompoundReport;
import net.sourceforge.nrl.parser.ast.constraints.IConstraint;
import net.sourceforge.nrl.parser.ast.constraints.IConstraintRuleDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.IVariableDeclaration;
import net.sourceforge.nrl.parser.ast.impl.SingleContextDeclarationImpl;

import org.antlr.runtime.Token;

public class ConstraintRuleDeclarationImpl extends SingleContextDeclarationImpl implements
		IConstraintRuleDeclaration {

	/** The rule set this rule is in */
	private IRuleSetDeclaration ruleSet;

	// Cached list of variable declarations
	private List<IVariableDeclaration> variables;

	public ConstraintRuleDeclarationImpl(Token token) {
		super(token);
	}

	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			for (IVariableDeclaration var : getVariableDeclarations()) {
				var.accept(visitor);
			}

			getConstraint().accept(visitor);

			if (getReport() != null)
				getReport().accept(visitor);
		}
		visitor.visitAfter(this);
	}

	@Deprecated
	public String dump(int indent) {
		String result = doIndent(indent) + "Rule " + getId() + NEWLINE
				+ getModelReference().dump(indent + 1) + getConstraint().dump(indent + 1) + NEWLINE;

		if (getReport() != null) {
			result = result + getReport().dump(indent + 1) + NEWLINE;
		}
		return result;
	}

	public IConstraint getConstraint() {
		int constraintChild = getChildCount() - 1;
		if (getChild(constraintChild) instanceof ICompoundReport)
			constraintChild--;
		return (IConstraint) getChild(constraintChild);
	}

	@Override
	public String getId() {
		if (isOldContextFormat())
			return getChild(1).getText();
		return getChild(0).getText();
	}

	public ICompoundReport getReport() {
		if (getChild(getChildCount() - 1) instanceof ICompoundReport)
			return (ICompoundReport) getChild(getChildCount() - 1);
		return null;
	}

	public IRuleSetDeclaration getRuleSet() {
		return ruleSet;
	}

	public List<IVariableDeclaration> getVariableDeclarations() {
		if (variables == null) {
			variables = new ArrayList<IVariableDeclaration>();

			for (int i = 1; i < getChildCount() - 1; i++) {
				if (getChild(i) instanceof IVariableDeclaration)
					variables.add((IVariableDeclaration) getChild(i));
			}
		}
		return variables;
	}

	public void setRuleSet(IRuleSetDeclaration ruleSet) {
		this.ruleSet = ruleSet;
	}
}
