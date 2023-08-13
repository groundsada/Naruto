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
import net.sourceforge.nrl.parser.model.IModelElement;

/**
 * Checks if a model reference refers to a sub-type of a specified type.
 * <p>
 * Abstract syntax reference:
 * <code>IsSubtypePredicateImpl ::= ModelReference ISA ModelElement</code>
 * 
 * @author Christian Nentwich
 */
public interface IIsSubtypePredicate extends IPredicate {

	/**
	 * Return the model reference to check
	 * 
	 * @return the reference
	 */
	public IModelReference getReference();

	/**
	 * Return the type to check.
	 * 
	 * @return the type
	 */
	public IModelElement getTargetType();
}
