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

import net.sourceforge.nrl.parser.ast.IRuleFile;
import net.sourceforge.nrl.parser.operators.IOperator;
import net.sourceforge.nrl.parser.operators.IOperators;

/**
 * An operator invocation is an expression that makes use of an external
 * undefined operator - mostly a scripted a programmed extension.
 * 
 * @author Christian Nentwich
 */
public interface IOperatorInvocation extends IExpression {

	/**
	 * Return the operator. This only returns a value if the rule file has been
	 * resolved against an operator collection using
	 * {@link net.sourceforge.nrl.parser.INRLParser#resolveOperatorReferences(IRuleFile, IOperators[])}.
	 * 
	 * @return the operator
	 */
	public IOperator getOperator();

	/**
	 * Return the name of the operator begin called. Never returns null.
	 * 
	 * @return the name without any square brackets
	 */
	public String getOperatorName();
	
	/**
	 * Return a parameter. The parameter index must be between 0 and
	 * {@link #getNumParameters()}-1, or a runtime exception will occur.
	 * 
	 * @param index the index
	 * @return the parameter, will not be null if the index was in bounds
	 */
	public IExpression getParameter(int index);

	/**
	 * Return the number of parameters passed in this invocation.
	 */
	public int getNumParameters();
}
