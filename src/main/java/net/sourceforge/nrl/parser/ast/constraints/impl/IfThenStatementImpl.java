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
import net.sourceforge.nrl.parser.ast.constraints.IConstraint;
import net.sourceforge.nrl.parser.ast.constraints.IIfThenStatement;

import org.antlr.runtime.Token;

public class IfThenStatementImpl extends ConstraintImpl implements IIfThenStatement {

	public IfThenStatementImpl(Token token) {
		super(token);
	}

	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			getIf().accept(visitor);
			getThen().accept(visitor);
			if (getElse() != null)
				getElse().accept(visitor);
		}
		visitor.visitAfter(this);
	}

	@Deprecated
	public String dump(int indent) {
		String result = doIndent(indent) + "if" + NEWLINE + getIf().dump(indent + 1)
				+ doIndent(indent) + "then" + NEWLINE + getThen().dump(indent + 1);
		if (getElse() != null)
			result = doIndent(indent) + "else" + NEWLINE + getElse().dump(indent + 1);
		return result;
	}

	public IConstraint getIf() {
		return (IConstraint) getChild(0);
	}

	public IConstraint getThen() {
		return (IConstraint) getChild(1);
	}

	public IConstraint getElse() {
		if (getChildCount() < 3)
			return null;
		return (IConstraint) getChild(2);
	}
}
