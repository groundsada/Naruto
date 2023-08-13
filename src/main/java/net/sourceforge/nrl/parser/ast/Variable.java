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
package net.sourceforge.nrl.parser.ast;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.nrl.parser.ast.constraints.ICastExpression;
import net.sourceforge.nrl.parser.ast.constraints.IExpression;
import net.sourceforge.nrl.parser.model.IModelElement;

/**
 * A default implementation of the variable reference interface.
 * 
 * @author Christian Nentwich
 */
public class Variable implements IVariable {

	private String name;

	private Object boundObject;

	private INRLAstNode declarationNode;

	private NRLDataType type = NRLDataType.UNKNOWN;

	// User data map (string->object)
	private Map<String, Object> userData = new HashMap<String, Object>();

	public Variable(String name, IExpression boundExpression) {
		this.name = name;
		this.boundObject = boundExpression;
	}

	public Variable(String name, IModelElement boundElement) {
		this.name = name;
		this.boundObject = boundElement;
	}

	public IModelElement getBoundElement() {
		if (boundObject instanceof IModelElement)
			return (IModelElement) boundObject;
		else if (boundObject instanceof ICastExpression) {
			ICastExpression cast = (ICastExpression) boundObject;
			return cast.getTargetType();
		}
		
		return null;
	}

	public IExpression getBoundExpression() {
		if (boundObject instanceof IExpression)
			return (IExpression) boundObject;
		return null;
	}

	/**
	 * Helper node for error reporting - where the variable was declared
	 * 
	 * @return the node
	 */
	public INRLAstNode getDeclarationNode() {
		return declarationNode;
	}

	public String getName() {
		return name;
	}

	public NRLDataType getNRLDataType() {
		return type;
	}

	public Object getUserData(String key) {
		return userData.get(key);
	}

	public boolean isBoundToElement() {
		return boundObject instanceof IModelElement || boundObject instanceof ICastExpression;
	}

	/**
	 * Helper node for error reporting - where the variable was declared
	 * 
	 * @param declarationNode the node
	 */
	public void setDeclarationNode(INRLAstNode declarationNode) {
		this.declarationNode = declarationNode;
	}
	
	public void setNRLDataType(NRLDataType type) {
		this.type = type;		
	}

	public void setUserData(String key, Object data) {
		userData.put(key, data);		
	}

}
