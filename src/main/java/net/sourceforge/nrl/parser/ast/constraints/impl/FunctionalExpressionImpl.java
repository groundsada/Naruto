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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.constraints.IFunctionalExpression;
import net.sourceforge.nrl.parser.ast.constraints.IIdentifier;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.antlr.runtime.Token;

public class FunctionalExpressionImpl extends ConstraintImpl implements
		IFunctionalExpression {

	// Function ID, set by constructor
	private Function function;

	public FunctionalExpressionImpl(Token token) {
		super(token);

		switch (token.getType()) {
		case NRLActionParser.SUM_OF:
			function = Function.SUMOF;
			break;
		case NRLActionParser.NUMBER_OF:
			function = Function.NUMBER_OF;
			break;
		default:
			throw new RuntimeException("Internal error. Invalid functional expression "
					+ token.getText());
		}
	}

	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			for (IIdentifier param : getParameters()) {
				param.accept(visitor);
			}
		}
		visitor.visitAfter(this);
	}

	public Function getFunction() {
		return function;
	}

	public String getFunctionIdAsString() {
		switch (getFunction()) {
		case SUMOF:
			return "sumOf";
		case NUMBER_OF:
			return "numberOf";
		}
		return "UNKNOWN FUNCTION";
	}

	public List<IIdentifier> getParameters() {
		List<IIdentifier> result = new ArrayList<IIdentifier>();

		List<?> children = getChildren();
		for (Object child : children) {
			result.add((IIdentifier) child);
		}

		return result;
	}

	@Deprecated
	public String dump(int indent) {
		String result = doIndent(indent) + "Function " + getFunctionIdAsString()
				+ NEWLINE;
		for (IIdentifier param : getParameters()) {
			result = result + param.dump(indent + 1);
		}
		return result;
	}
}
