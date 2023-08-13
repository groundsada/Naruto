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

import org.antlr.runtime.Token;

import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.constraints.ICompoundReport;
import net.sourceforge.nrl.parser.ast.constraints.IConditionalReport;
import net.sourceforge.nrl.parser.ast.constraints.IConstraint;
import net.sourceforge.nrl.parser.ast.impl.Antlr3NRLBaseAst;

public class ConditionalReportImpl extends Antlr3NRLBaseAst implements IConditionalReport {

	public ConditionalReportImpl(Token token) {
		super(token);
	}

	@Override
	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			getCondition().accept(visitor);
			getThen().accept(visitor);

			if (getElse() != null) {
				getElse().accept(visitor);
			}
		}
		visitor.visitAfter(this);
	}

	@Deprecated
	public String dump(int indent) {
		String result = doIndent(indent) + "if" + NEWLINE;
		result = result + getCondition().dump(indent + 1) + NEWLINE;
		result = result + doIndent(indent) + "then" + NEWLINE;
		result = result + getThen().dump(indent + 1) + NEWLINE;
		if (getElse() != null) {
			result = result + doIndent(indent) + "else" + NEWLINE;
			result = result + getThen().dump(indent + 1) + NEWLINE;
		}
		return result;
	}

	public IConstraint getCondition() {
		return (IConstraint) getChild(0);
	}

	public ICompoundReport getElse() {
		if (getChildCount() > 2)
			return (ICompoundReport) getChild(2);
		return null;
	}

	public ICompoundReport getThen() {
		return (ICompoundReport) getChild(1);
	}
}
