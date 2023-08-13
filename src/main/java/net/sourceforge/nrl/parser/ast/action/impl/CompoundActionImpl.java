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
import net.sourceforge.nrl.parser.ast.action.ICompoundAction;
import net.sourceforge.nrl.parser.ast.action.ISimpleAction;

import org.antlr.runtime.Token;

public class CompoundActionImpl extends ActionImpl implements ICompoundAction {

	public CompoundActionImpl() {
	}

	public CompoundActionImpl(Token token) {
		super(token);
	}

	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			for (ISimpleAction action : getSimpleActions()) {
				action.accept(visitor);
			}
		}
		visitor.visitAfter(this);
	}

	@Deprecated
	public String dump(int indent) {
		String result = doIndent(indent) + "compound action" + NEWLINE;
		for (ISimpleAction action : getSimpleActions()) {
			result = result + action.dump(indent + 1);
		}
		return result;
	}

	public List<ISimpleAction> getSimpleActions() {
		List<ISimpleAction> result = new ArrayList<ISimpleAction>();

		for (int i = 0; i < getChildCount(); i++) {
			result.add((ISimpleAction) getChild(i));
		}

		return result;
	}

}
