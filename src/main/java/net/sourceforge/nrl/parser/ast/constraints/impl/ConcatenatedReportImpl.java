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

import org.antlr.runtime.Token;

import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.constraints.IConcatenatedReport;
import net.sourceforge.nrl.parser.ast.constraints.IExpression;
import net.sourceforge.nrl.parser.ast.impl.Antlr3NRLBaseAst;

public class ConcatenatedReportImpl extends Antlr3NRLBaseAst implements IConcatenatedReport {

	private List<IExpression> expressions = null;

	public ConcatenatedReportImpl(Token token) {
		super(token);
	}
	
	@Override
	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			for (IExpression expr : getExpressions()) {
				expr.accept(visitor);
			}
		}
		visitor.visitAfter(this);
	}

	@Deprecated
	public String dump(int indent) {
		String result = doIndent(indent) + "concatenated report" + NEWLINE;
		for (IExpression expr : getExpressions()) {
			result = result + expr.dump(indent + 1);
		}
		return result;
	}

	public List<IExpression> getExpressions() {
		if (expressions != null) {
			return expressions;
		}

		expressions = new ArrayList<IExpression>();

		List<?> children = getChildren();
		for (Object child : children) {
			expressions.add((IExpression) child);
		}
		

		return expressions;
	}

}
