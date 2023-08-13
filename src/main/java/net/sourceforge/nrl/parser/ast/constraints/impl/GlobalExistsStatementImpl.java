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

import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.IVariable;
import net.sourceforge.nrl.parser.ast.constraints.IConstraint;
import net.sourceforge.nrl.parser.ast.constraints.IGlobalExistsStatement;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;
import net.sourceforge.nrl.parser.model.IModelElement;

import org.antlr.runtime.Token;

public class GlobalExistsStatementImpl extends ConstraintImpl implements
		IGlobalExistsStatement {

	// Assigned by resolver
	private IVariable variable;

	// Assigned by resolver
	private IModelElement element;
	
	public GlobalExistsStatementImpl() {
	}

	public GlobalExistsStatementImpl(Token token) {
		super(token);
	}

	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			if (hasConstraint())
				getConstraint().accept(visitor);
		}
		visitor.visitAfter(this);
	}

	@Deprecated
	public String dump(int indent) {
		String result = doIndent(indent) + "globalExists" + NEWLINE;
		if (getConstraint() != null)
			result += getConstraint().dump(indent + 1);
		return result;
	}

	public IConstraint getConstraint() {
		// NO? ModelReference DOUBLE_QUOTED? Constraint?
		switch (getChildCount()) {
		case 1:
			return null;
		case 2:
			if (getChild(0).getType() == NRLActionParser.NO)
				return null;
			if (getChild(1).getType() == NRLActionParser.DOUBLE_QUOTED_STRING)
				return null;
			return (IConstraint) getChild(1);
		case 3:
			if (getChild(2) instanceof IConstraint)
				return (IConstraint) getChild(2);
			return null;
		default:
			// 4
			return (IConstraint) getChild(3);
		}
	}

	public int getCount() {
		if (getChild(0).getType() == NRLActionParser.NO)
			return 0;
		return 1;
	}

	public IModelElement getElement() {
		return element;
	}

	public IVariable getVariable() {
		return variable;
	}

	public String getVariableName() {
		if (getChildCount() >= 2
				&& getChild(1).getType() == NRLActionParser.DOUBLE_QUOTED_STRING)
			return getChild(1).getText();
		if (getChildCount() >= 3
				&& getChild(2).getType() == NRLActionParser.DOUBLE_QUOTED_STRING)
			return getChild(2).getText();
		return null;
	}

	public boolean hasConstraint() {
		// NO? ModelReference DOUBLE_QUOTED? Constraint?
		switch (getChildCount()) {
		case 1:
			return false;
		case 2:
			if (getChild(0).getType() == NRLActionParser.NO)
				return false;
			if (getChild(1).getType() == NRLActionParser.DOUBLE_QUOTED_STRING)
				return false;
			return true;
		case 3:
			return getChild(2) instanceof IConstraint;
		default:
			// 4+
			return true;
		}
	}

	public IModelReference getModelReference() {
		if (getChild(0) instanceof IModelReference)
			return (IModelReference) getChild(0);
		return (IModelReference) getChild(1);
	}

	public String getElementName() {
		return ((ModelReferenceImpl) getModelReference()).getOriginalString();
	}

	public void setModelElement(IModelElement element) {
		this.element = element;
	}
	
	public void setVariable(IVariable variable) {
		this.variable = variable;
	}
}
