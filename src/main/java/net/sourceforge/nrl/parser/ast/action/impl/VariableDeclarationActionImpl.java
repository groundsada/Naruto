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

import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.IVariable;
import net.sourceforge.nrl.parser.ast.action.IVariableDeclarationAction;
import net.sourceforge.nrl.parser.ast.constraints.IExpression;

import org.antlr.runtime.Token;

public class VariableDeclarationActionImpl extends ActionImpl implements
		IVariableDeclarationAction {

	private IVariable variable;

	public VariableDeclarationActionImpl(Token token) {
		super(token);
	}

	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			getExpression().accept(visitor);
		}

		visitor.visitAfter(this);
	}
	
	@Deprecated
	public String dump(int indent) {
		return doIndent(indent) + "Declare " + getVariableName() + NEWLINE
				+ getExpression().dump(indent + 1);
	}

	public String getVariableName() {
		return getChild(0).getText();
	}

	public IExpression getExpression() {
		return (IExpression) getChild(1);
	}

	public IVariable getVariableReference() {
		return variable;
	}
	
	public void setVariableReference(IVariable variable) {
		this.variable = variable;
	}
}
