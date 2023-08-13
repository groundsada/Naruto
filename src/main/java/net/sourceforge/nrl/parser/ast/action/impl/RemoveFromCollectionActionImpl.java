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

import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.action.IRemoveFromCollectionAction;

import org.antlr.runtime.Token;

public class RemoveFromCollectionActionImpl extends ActionImpl implements IRemoveFromCollectionAction {

	public RemoveFromCollectionActionImpl() {
	}

	public RemoveFromCollectionActionImpl(Token token) {
		super(token);
	}

	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			getElement().accept(visitor);
			getFrom().accept(visitor);
		}
		visitor.visitAfter(this);
	}

	@Deprecated
	public String dump(int indent) {
		String result = doIndent(indent) + "Remove " + getElement().dump(0) + " from "
				+ getFrom().dump(0);
		return result;
	}

	public IModelReference getElement() {
		return (IModelReference)getChild(0);
	}

	public IModelReference getFrom() {
		return (IModelReference)getChild(1);
	}
}
