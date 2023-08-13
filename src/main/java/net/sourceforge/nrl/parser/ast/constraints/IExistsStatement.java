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
 * One of the "exists" statements. Expresses that an element in a collection
 * exists, or that a simple attribute exists. The statement contains an optional
 * "cardinality constraint", which declares how many elements exist in a
 * collection, and an optional "constraint", which specifies a constraint that
 * has to hold for each element in the collection that is checked for existence.
 * <p>
 * Abstract syntax reference:
 * <code>exists (count:CardinalityConstraint)? element:ModelReference (constraint:Constraint)?</code>
 * 
 * @author Christian Nentwich
 */
public interface IExistsStatement extends IConstraint {

	/**
	 * Return the constraint to check relative to each collection member being
	 * iterated over. Use {@link #hasConstraint()} to check if it exists first.
	 * 
	 * @return the constraint or null
	 */
	public IConstraint getConstraint();

	/**
	 * Return the cardinality constraint (one, two, three, at least one, etc.),
	 * if there is one. Else returns null. Call {@link #hasCount()} to check
	 * first.
	 * 
	 * @return the cardinality constraint or null
	 */
	public ICardinalityConstraint getCount();

	/**
	 * Return the model reference being checked for existence. Never returns
	 * null.
	 * 
	 * @return the model reference
	 */
	public IModelReference getElement();

	/**
	 * Return true if there is a constraint attached that is to be executed
	 * relative to collection members.
	 * 
	 * @return true if a constraint is attached
	 */
	public boolean hasConstraint();

	/**
	 * Return true if the exists statement has a counter attached to it.
	 * 
	 * @return true if there is a counter
	 */
	public boolean hasCount();
}
