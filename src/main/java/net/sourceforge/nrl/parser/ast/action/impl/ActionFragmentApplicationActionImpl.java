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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.action.IActionFragmentApplicationAction;
import net.sourceforge.nrl.parser.ast.action.IActionFragmentDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.IExpression;

import org.antlr.runtime.Token;

public class ActionFragmentApplicationActionImpl extends ActionImpl implements
		IActionFragmentApplicationAction {

	// The resolved fragment. Needs to be looked up by a resolver
	private IActionFragmentDeclaration fragment;

	private List<IExpression> children;

	public ActionFragmentApplicationActionImpl() {
	}

	public ActionFragmentApplicationActionImpl(Token token) {
		super(token);
	}

	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			for (IExpression expr : getParameters()) {
				expr.accept(visitor);
			}
		}
		visitor.visitAfter(this);
	}

	public String getActionFragmentId() {
		return getChild(0).getText();
	}

	public IActionFragmentDeclaration getFragment() {
		return this.fragment;
	}

	public List<IExpression> getParameters() {
		if (children == null) {
			children = new ArrayList<IExpression>();

			for (int i = 1; i < getChildCount(); i++) {
				children.add((IExpression) getChild(i));
			}
		}

		return children;
	}

	public void setFragment(IActionFragmentDeclaration decl) {
		this.fragment = decl;
	}
}
