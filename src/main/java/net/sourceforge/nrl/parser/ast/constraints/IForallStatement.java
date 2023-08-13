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
import net.sourceforge.nrl.parser.ast.IVariable;

/**
 * A constraint that expresses that a sub-constraint holds for every member of a
 * collection identified by a model reference.
 * <p>
 * Abstract syntax reference:
 * <code>ForallStatement ::= forall element:ModelReference constraint:Constraint</code>
 * 
 * @author Christian Nentwich
 */
public interface IForallStatement extends IConstraint {

	/**
	 * Return the constraint being checked for all elements in the collection.
	 * 
	 * @return the constraint
	 */
	public IConstraint getConstraint();

	/**
	 * Return the element being iterated over.
	 * 
	 * @return the element
	 */
	public IModelReference getElement();
	
	/**
	 * If the statement introduces an iteration variable, this is returned
	 * here. The iteration variable will be bound to each object being iterated
	 * over.
	 * <p>
	 * If no variable is used, this returns null.
	 * 
	 * @return the variable or null
	 */
	public IVariable getVariable();
}
