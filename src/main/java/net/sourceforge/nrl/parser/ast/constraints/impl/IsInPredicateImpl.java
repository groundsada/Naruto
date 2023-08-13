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
import net.sourceforge.nrl.parser.ast.constraints.IExpression;
import net.sourceforge.nrl.parser.ast.constraints.IIdentifier;
import net.sourceforge.nrl.parser.ast.constraints.IIsInPredicate;

import org.antlr.runtime.Token;

public class IsInPredicateImpl extends ConstraintImpl implements IIsInPredicate {

	private List<IIdentifier> list = new ArrayList<IIdentifier>();

	public IsInPredicateImpl(Token token) {
		super(token);
	}

	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			getExpression().accept(visitor);

			for (IIdentifier item : getList()) {
				item.accept(visitor);
			}
		}
		visitor.visitAfter(this);
	}
	
	@Deprecated
	public String dump(int indent) {
		String listStr = "";
		for (int i = 0; i < list.size(); i++)
			listStr = listStr + list.get(i).toString();

		return doIndent(indent) + "Is one of" + NEWLINE
				+ getExpression().dump(indent + 1) + NEWLINE
				+ doIndent(indent + 1) + listStr;

	}

	public IExpression getExpression() {
		return (IExpression) getChild(0);
	}

	public List<IIdentifier> getList() {
		if (list.size() == 0) {
			List<?> children = getChildren();

			for (int i = 1; i < children.size(); i++) {
				list.add((IIdentifier) children.get(i));
			}
		}
		return list;
	}
}
