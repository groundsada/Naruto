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
import net.sourceforge.nrl.parser.ast.constraints.IArithmeticExpression;
import net.sourceforge.nrl.parser.ast.constraints.IExpression;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.antlr.runtime.Token;

public class ArithmeticExpressionImpl extends ConstraintImpl implements
		IArithmeticExpression {

	// Operator attribute, initialised by initialize during parsing
	Operator operator;

	public ArithmeticExpressionImpl(Token token) {
		super(token);

		switch (token.getType()) {
		case NRLActionParser.ADD:
			operator = Operator.PLUS;
			break;
		case NRLActionParser.MINUS:
			operator = Operator.MINUS;
			break;
		case NRLActionParser.TIMES:
			operator = Operator.TIMES;
			break;
		case NRLActionParser.DIV:
			operator = Operator.DIV;
			break;
		case NRLActionParser.MOD:
			operator = Operator.MOD;
			break;
		default:
			throw new RuntimeException(
					"Internal error. Illegal token used to initialize arithmetic expression: "
							+ token.getType());
		}
	}

	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			getLeft().accept(visitor);
			getRight().accept(visitor);
		}
		visitor.visitAfter(this);
	}

	@Deprecated
	public String dump(int indent) {
		return doIndent(indent) + getOperatorAsString() + NEWLINE
				+ getLeft().dump(indent + 1) + getRight().dump(indent + 1);
	}

	public IExpression getLeft() {
		return (IExpression) getChild(0);
	}

	public IExpression getRight() {
		return (IExpression) getChild(1);
	}

	public Operator getOperator() {
		return operator;
	}

	protected String getOperatorAsString() {
		switch (getOperator()) {
		case PLUS:
			return "+";
		case MINUS:
			return "-";
		case TIMES:
			return "*";
		case DIV:
			return "/";
		case MOD:
			return "mod";
		}
		return "";
	}
}
