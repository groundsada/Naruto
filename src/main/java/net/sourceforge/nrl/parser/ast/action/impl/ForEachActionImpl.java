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
package net.sourceforge.nrl.parser.ast.action.impl;

import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.IVariable;
import net.sourceforge.nrl.parser.ast.action.IAction;
import net.sourceforge.nrl.parser.ast.action.IForEachAction;

import org.antlr.runtime.Token;

public class ForEachActionImpl extends ActionImpl implements IForEachAction {

	private IVariable variable;

	public ForEachActionImpl() {
	}

	public ForEachActionImpl(Token token) {
		super(token);
	}

	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			getCollection().accept(visitor);
			getAction().accept(visitor);
		}
		visitor.visitAfter(this);
	}

	@Deprecated
	public String dump(int indent) {
		String result = doIndent(indent) + "For each "
				+ getCollection().dump(0) + NEWLINE
				+ getAction().dump(indent + 1);
		return result;
	}

	public IModelReference getCollection() {
		if (getChild(0) instanceof IModelReference)
			return (IModelReference) getChild(0);
		return (IModelReference) getChild(1);
	}

	public IAction getAction() {
		if (getChild(1) instanceof IAction)
			return (IAction) getChild(1);
		return (IAction) getChild(2);
	}

	public String getVariableName() {
		if (!(getChild(0) instanceof IModelReference))
			return getChild(0).getText();
		return null;
	}

	public IVariable getVariable() {
		return variable;
	}

	public void setVariable(IVariable variable) {
		this.variable = variable;
	}
}
