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
package net.sourceforge.nrl.parser.ast.constraints.impl;

import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.constraints.IExpression;
import net.sourceforge.nrl.parser.ast.constraints.IOperatorInvocation;
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.operators.IOperator;

import org.antlr.runtime.Token;

public class OperatorInvocationImpl extends ConstraintImpl implements IOperatorInvocation {

	private IOperator operator;

	private IModelElement returnType;
	
	public OperatorInvocationImpl() {
	}

	public OperatorInvocationImpl(Token token) {
		super(token);
	}

	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			for (int i = 0; i < getNumParameters(); i++)
				getParameter(i).accept(visitor);
		}
		visitor.visitAfter(this);
	}

	@Deprecated
	public String dump(int indent) {
		String result = doIndent(indent) + "Operator " + getText() + NEWLINE;
		for (int i = 0; i < getChildCount(); i++)
			result = result + getParameter(i).dump(indent + 1);
		return result;
	}

	public IOperator getOperator() {
		return operator;
	}

	public String getOperatorName() {
		return getText();
	}

	public IExpression getParameter(int index) {
		return (IExpression) getChild(index);
	}

	public int getNumParameters() {
		return getChildCount();
	}

	public IModelElement getReturnType() {
		return returnType;
	}

	public void setOperator(IOperator operator) {
		this.operator = operator;
	}
	
	public void setReturnType(IModelElement returnType) {
		this.returnType = returnType;
	}
}
