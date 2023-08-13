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
package net.sourceforge.nrl.parser.ast.action.impl;

import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.IRuleSetDeclaration;
import net.sourceforge.nrl.parser.ast.action.IAction;
import net.sourceforge.nrl.parser.ast.action.IActionRuleDeclaration;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;
import net.sourceforge.nrl.parser.ast.impl.SingleContextDeclarationImpl;

import org.antlr.runtime.Token;

public class ActionRuleDeclarationImpl extends SingleContextDeclarationImpl implements
		IActionRuleDeclaration {

	/** The rule set this rule is in */
	private IRuleSetDeclaration ruleSet;

	public ActionRuleDeclarationImpl(Token token) {
		super(token);
	}

	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this))
			getAction().accept(visitor);
		visitor.visitAfter(this);
	}

	@Deprecated
	public String dump(int indent) {
		return doIndent(indent) + "ActionRule " + getId() + NEWLINE
				+ ((IModelReference) getChild(0)).dump(indent + 1) + getAction().dump(indent + 1);
	}

	public IAction getAction() {
		return (IAction) getChild(getChildCount() - 1);
	}

	@Override
	public String getId() {		
		if (!(getChild(0) instanceof IModelReference) && getChild(0).getType() != NRLActionParser.NO)
			return getChild(0).getText();
		return getChild(1).getText();
	}

	public IRuleSetDeclaration getRuleSet() {
		return ruleSet;
	}

	public boolean hasContext() {
		return getChild(0) instanceof IModelReference || getChild(1) instanceof IModelReference;
	}

	public void setRuleSet(IRuleSetDeclaration ruleSet) {
		this.ruleSet = ruleSet;
	}
}
