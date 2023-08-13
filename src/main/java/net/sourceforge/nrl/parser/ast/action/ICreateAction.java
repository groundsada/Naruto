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

import net.sourceforge.nrl.parser.ast.IVariable;
import net.sourceforge.nrl.parser.model.IModelElement;

/**
 * An action that creates a new object and assigns it to a variable.
 * 
 * @author Christian Nentwich
 */
public interface ICreateAction extends ISimpleAction {

	/**
	 * A reference to the element to create. This is never null.
	 * 
	 * @return the element
	 */
	public IModelElement getElement();

	/**
	 * A reference to the variable that the object is held in after creation.
	 * This is never null.
	 * 
	 * @return the variable
	 */
	public IVariable getVariable();
}
