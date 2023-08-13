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
package net.sourceforge.nrl.parser.ast.action;

import java.util.List;

import net.sourceforge.nrl.parser.ast.constraints.IExpression;

/**
 * An action fragment application. This is an action that refers to a fragment,
 * applying it to a list of parameters.
 * 
 * @author Christian Nentwich
 */
public interface IActionFragmentApplicationAction extends ISimpleAction {

	/**
	 * Return the fragment that this application refers to. This will return null
	 * until after a resolver has been applied - and applied without errors.
	 * 
	 * @return the fragment
	 */
	public IActionFragmentDeclaration getFragment();

	/**
	 * Return the parameters being passed to the macro.
	 * 
	 * @return the parameters
	 */
	public List<IExpression> getParameters();
}
