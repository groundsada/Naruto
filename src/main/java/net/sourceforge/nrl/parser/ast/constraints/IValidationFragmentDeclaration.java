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

import net.sourceforge.nrl.parser.ast.IMultipleContextDeclaration;
import net.sourceforge.nrl.parser.ast.NRLDataType;


/**
 * Declaration of a validation fragment, a sub-constraint that is attached to a model
 * element. Has an id, a context and a constraint.
 * 
 * @author Christian Nentwich
 */
public interface IValidationFragmentDeclaration extends IMultipleContextDeclaration {

	/**
	 * Get the fragment constraint. Never returns null.
	 * 
	 * @return the constraint
	 */
	public IConstraint getConstraint();
	
	/**
	 * Return the return type of the fragment.
	 * @return the NRL type
	 */
	public NRLDataType getNRLDataType();
}
