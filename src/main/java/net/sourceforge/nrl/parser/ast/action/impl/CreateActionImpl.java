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
import net.sourceforge.nrl.parser.ast.IVariable;
import net.sourceforge.nrl.parser.ast.action.ICreateAction;
import net.sourceforge.nrl.parser.ast.constraints.impl.ModelReferenceImpl;
import net.sourceforge.nrl.parser.model.IModelElement;

import org.antlr.runtime.Token;

public class CreateActionImpl extends ActionImpl implements ICreateAction {

	private IVariable variable;
	
	public CreateActionImpl() {
	}

	public CreateActionImpl(Token token) {
		super(token);
	}

	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			((IModelReference)getChild(0)).accept(visitor);
		}
		visitor.visitAfter(this);
	}
	
	public String dump(int indent) {
		return doIndent(indent) + "Create " + getElementName() + " " + getVariableName()
				+ NEWLINE;
	}

	public IModelElement getElement() {
		return getModelReference().getTarget();
	}

	public String getElementName() {
		return getModelReference().getOriginalString();
	}

	/**
	 * Return the raw model reference
	 */
	public ModelReferenceImpl getModelReference() {
		return (ModelReferenceImpl)getChild(0);
	}
	
	public IVariable getVariable() {
		return variable;
	}
	
	public String getVariableName() {
		if (getChildCount() > 1)
			return getChild(1).getText();
		return null;
	}
	
	public void setVariable(IVariable variable) {
		this.variable = variable;
	}
}
