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
import net.sourceforge.nrl.parser.model.IModelElement;

/**
 * A "global" exists statement. This tests for the existence or absence of an
 * object - rather than of an attribute or a member of a collection, like the
 * exists statement.
 * 
 * <p>
 * Abstract syntax reference:
 * <code>thereIs ("no")? element:ModelReference (Variable)? (constraint:Constraint)?</code>
 * 
 * @author Christian Nentwich
 */
public interface IGlobalExistsStatement extends IConstraint {

	/**
	 * Return the constraint to check relative to the object being iterated
	 * over. Use {@link #hasConstraint()} to check if it exists first.
	 * 
	 * @return the constraint or null
	 */
	public IConstraint getConstraint();

	/**
	 * Return the cardinality constraint if there is one. This will <b>always</b>
	 * return one or zero for the time being, zero indicating 'none'.
	 * <p>
	 * It never returns null.
	 * 
	 * @return the cardinality constraint, as a number
	 */
	public int getCount();

	/**
	 * Return the model element being checked for existence. Never returns
	 * null.
	 * 
	 * @return the model element
	 */
	public IModelElement getElement();

	/**
	 * If the statement is used to check for the existence (rather than absence)
	 * of an element, the element can be assigned to a variable if found. If
	 * a variable is assigned, it is returned here.
	 * 
	 * @return the variable or null if none was assigned
	 */
	public IVariable getVariable();
	
	/**
	 * Return true if there is a constraint attached that is to be executed
	 * relative to the model element.
	 * 
	 * @return true if a constraint is attached
	 */
	public boolean hasConstraint();
}
