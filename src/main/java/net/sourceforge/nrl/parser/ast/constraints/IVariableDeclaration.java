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
package net.sourceforge.nrl.parser.ast.constraints;

import net.sourceforge.nrl.parser.ast.IVariable;

/**
 * A variable declaration statement.
 * <p>
 * Abstract syntax reference:
 * <code>DeclareStatement ::= declare Variable expression</code>
 * 
 * @author Christian Nentwich
 */
public interface IVariableDeclaration extends IConstraint {


	/**
	 * Return the variable name
	 * 
	 * @return the variable name
	 */
	public String getVariableName();

	/**
	 * Get the expression that initialises the variable.
	 * 
	 * @return the expression
	 */
	public IExpression getExpression();

	/**
	 * Create a resolved variable reference. This initialises a variable as
	 * declared by this expression. This method can only be called when the AST
	 * has been resolved against a model. It can be called any number of times.
	 * 
	 * @return the variable
	 */
	public IVariable getVariableReference();
}
