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

import net.sourceforge.nrl.parser.ast.IModelReference;

/**
 * A constraint that expresses that an attribute does not exist, or a collection
 * is empty.
 * <p>
 * Abstract syntax reference:
 * <code>NotExistsStatement ::= notexists element:ModelReference</code>
 * 
 * @author Christian Nentwich
 */
public interface INotExistsStatement extends IConstraint {

	/**
	 * Return the element that has been declared as non-existant. Will never
	 * return null.
	 * 
	 * @return the element
	 */
	public IModelReference getElement();
}
