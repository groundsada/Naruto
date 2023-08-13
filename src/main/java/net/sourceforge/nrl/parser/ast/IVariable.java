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

import net.sourceforge.nrl.parser.ast.constraints.IExpression;
import net.sourceforge.nrl.parser.model.IModelElement;

/**
 * A variable reference. A variable is bound either directly to an element, or
 * to a complex expression.
 * 
 * @author Christian Nentwich
 */
public interface IVariable {

	/**
	 * Return the model element bound to this variable. If
	 * {@link #isBoundToElement()} returns false for this variable, then this
	 * method will return <code>null</code>.
	 * 
	 * @return the element or null if a complex expression is bound.
	 */
	public IModelElement getBoundElement();

	/**
	 * Return the complex expression bound to this variable. If
	 * {@link #isBoundToElement()} returns true for this variable, then this
	 * will return null, else it returns the expression.
	 * 
	 * @return the expression of null if the variable is bound to an element
	 */
	public IExpression getBoundExpression();

	/**
	 * Return the variable name.
	 * 
	 * @return the variable name
	 */
	public String getName();

	/**
	 * Return a type constant from the {@link NRLDataType} class, indicating the
	 * data type of the variable.
	 * <p>
	 * This can only be called once a type inference algorithm has been applied
	 * to the variable or the entire AST.
	 * 
	 * @return the type, never null
	 */
	public NRLDataType getNRLDataType();

	/**
	 * Return a named user data object associated with this variable.
	 * 
	 * @param key the key identifying the data
	 * @return the object or null if no such data
	 */
	public Object getUserData(String key);

	/**
	 * Return true if the variable is bound to a model element, false if it is
	 * bound to a complex expression
	 * 
	 * @return true if bound to element
	 */
	public boolean isBoundToElement();
	
	/**
	 * Assign an NRL type to this variable.
	 * 
	 * @param type the type
	 */
	public void setNRLDataType(NRLDataType type);
	
	/**
	 * Associate user data with this variable node, under a given key.
	 * 
	 * @param key the key
	 * @param data the data
	 */
	public void setUserData(String key, Object data);
}
