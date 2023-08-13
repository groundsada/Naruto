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


/**
 * A fragment application is used inside a rule to obtain the value of a
 * fragment. It either applies to the current context, or optionally takes a
 * model reference parameter.
 * 
 * @author Christian Nentwich
 */
public interface IValidationFragmentApplication extends IExpression {

	/**
	 * Return the name of the fragment to check. Use {@link #getFragment()} to
	 * return the actual fragment.
	 * 
	 * @return the fragment name
	 */
	public String getFragmentName();

	/**
	 * Return the fragment to check. Never returns null.
	 * 
	 * @return the fragment
	 */
	public IValidationFragmentDeclaration getFragment();

	/**
	 * Return a parameter. The parameter index must be between 0 and
	 * {@link #getNumParameters()}-1, or a runtime exception will occur.
	 * 
	 * @param index the index
	 * @return the parameter, will not be null if the index was in bounds
	 */
	public IExpression getParameter(int index);

	/**
	 * Return the number of parameters passed to this property.
	 */
	public int getNumParameters();
}
