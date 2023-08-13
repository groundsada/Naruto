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
import net.sourceforge.nrl.parser.ast.action.IRemoveAction;
import net.sourceforge.nrl.parser.ast.constraints.IConstraint;

import org.antlr.runtime.Token;

public class RemoveActionImpl extends ActionImpl implements IRemoveAction {

	private IVariable variable;
	
	public RemoveActionImpl() {
	}

	public RemoveActionImpl(Token token) {
		super(token);
	}

	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			getTarget().accept(visitor);
			if (getWhere() != null)
				getWhere().accept(visitor);
		}
		visitor.visitAfter(this);
	}

	@Deprecated
	public String dump(int indent) {
		String result = doIndent(indent) + "Remove " + getTarget().dump(0);
		if (getVariableName() != null)
			result = result + doIndent(indent) + getVariableName() + NEWLINE;

		if (getWhere() != null)
			result = result + doIndent(indent) + "where" + NEWLINE
					+ getWhere().dump(indent + 1);
		return result;
	}

	public IModelReference getTarget() {
		return (IModelReference) getChild(0);
	}

	public String getVariableName() {
		if (getChildCount() > 1)
			return getChild(1).getText();
		return null;
	}

	public IVariable getVariable() {
		return variable;
	}
	
	public IConstraint getWhere() {
		if (getChildCount() > 1)
			return (IConstraint) getChild(2);
		return null;
	}
	
	public void setVariable(IVariable variable) {
		this.variable = variable;
	}
}
