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
import net.sourceforge.nrl.parser.ast.constraints.IBinaryPredicate;
import net.sourceforge.nrl.parser.ast.constraints.IExpression;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.antlr.runtime.Token;

public class BinaryPredicateImpl extends ConstraintImpl implements IBinaryPredicate {

	// Predicate attribute, initialised by constructor
	Predicate predicate;

	public BinaryPredicateImpl() {
	}
	
	public BinaryPredicateImpl(Token token) {
		super(token);

		switch (token.getType()) {
		case NRLActionParser.EQUALS:
			predicate = Predicate.EQUAL;
			break;
		case NRLActionParser.NOT_EQUALS:
			predicate = Predicate.NOT_EQUAL;
			break;
		case NRLActionParser.LESS:
			predicate = Predicate.LESS;
			break;
		case NRLActionParser.LESS_EQ:
			predicate = Predicate.LESS_OR_EQUAL;
			break;
		case NRLActionParser.GREATER:
			predicate = Predicate.GREATER;
			break;
		case NRLActionParser.GREATER_EQ:
			predicate = Predicate.GREATER_OR_EQUAL;
			break;
		default:
			throw new RuntimeException(
					"Internal error. Illegal token used to initialize predicate: "
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
		return doIndent(indent) + getPredicateAsString() + NEWLINE
				+ getLeft().dump(indent + 1) + getRight().dump(indent + 1);

	}

	public IExpression getLeft() {
		return (IExpression) getChild(0);
	}

	public IExpression getRight() {
		return (IExpression) getChild(1);
	}

	public Predicate getPredicate() {
		return predicate;
	}

	protected String getPredicateAsString() {
		switch (getPredicate()) {
		case EQUAL:
			return "=";
		case NOT_EQUAL:
			return "<>";
		case LESS:
			return "<";
		case LESS_OR_EQUAL:
			return "<=";
		case GREATER:
			return ">";
		case GREATER_OR_EQUAL:
			return ">=";
		}
		return "UNKNOWN PREDICATE";
	}

}
