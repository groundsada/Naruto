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
import net.sourceforge.nrl.parser.ast.constraints.ICardinalityConstraint;
import net.sourceforge.nrl.parser.ast.constraints.IConstraint;
import net.sourceforge.nrl.parser.ast.constraints.IExistsStatement;

import org.antlr.runtime.Token;

public class ExistsStatementImpl extends ConstraintImpl implements IExistsStatement {

	// If the model element has been inferred, it is kept here
	private IModelReference modelElement;

	public ExistsStatementImpl() {
	}

	public ExistsStatementImpl(Token token) {
		super(token);
	}

	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			if (getElement() != null)
				getElement().accept(visitor);
			if (getConstraint() != null)
				getConstraint().accept(visitor);
		}
		visitor.visitAfter(this);
	}

	@Deprecated
	public String dump(int indent) {
		String result = doIndent(indent) + "exists" + NEWLINE;
		if (getCount() != null)
			result = result + getCount().dump(indent + 1);
		if (getElement() != null)
			result += getElement().dump(indent + 1);
		if (getConstraint() != null)
			result += getConstraint().dump(indent + 1);
		return result;
	}

	public IConstraint getConstraint() {
		// AST ::= MODEL_ELEMENT | enumerator constraint | MODEL_ELEMENT
		// constraint | enumerator MODEL_ELEMENT constraint

		if (getChildCount() == 1)
			return null;
		if (getChildCount() == 2 && getChild(1) instanceof IConstraint
				&& !(getChild(0) instanceof ICardinalityConstraint))
			return (IConstraint) getChild(1);
		if (getChildCount() == 3 && getChild(2) instanceof IConstraint)
			return (IConstraint) getChild(2);
		return null;
	}

	public ICardinalityConstraint getCount() {
		if (getChild(0) instanceof ICardinalityConstraint)
			return (ICardinalityConstraint) getChild(0);
		return null;
	}

	public IModelReference getElement() {
		if (modelElement == null) {
			if (getChild(0) instanceof IModelReference)
				modelElement = (IModelReference) getChild(0);
			if (getChildCount() >= 2 && getChild(1) instanceof IModelReference)
				modelElement = (IModelReference) getChild(1);
			return modelElement;
		}
		return modelElement;
	}

	@Override
	public int getLine() {
		if (getElement() != null)
			return getElement().getLine();
		return getCount().getLine();
	}

	@Override
	public int getColumn() {
		if (getElement() != null)
			return getElement().getColumn();
		return getCount().getColumn();
	}

	public boolean hasConstraint() {
		if (getChildCount() == 1)
			return false;
		if (getChildCount() == 2 && getChild(1) instanceof IConstraint)
			return true;
		if (getChildCount() == 3 && getChild(3) instanceof IConstraint)
			return true;
		return false;
	}

	public boolean hasCount() {
		return (getChild(0) instanceof ICardinalityConstraint);
	}

	public void setElement(ModelReferenceImpl element) {
		this.modelElement = element;
	}
}
