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

import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.IVariable;
import net.sourceforge.nrl.parser.ast.constraints.IConstraint;

/**
 * An action that deletes an attribute or deletes any object that matches a set
 * of criteria.
 * <p>
 * This action combines two quite different activities: to null out an attribute
 * in an object; and to query for any object that match the criteria supplied in
 * a where clause, and delete them.

 * 
 * @author Christian Nentwich
 * 
 */
public interface IRemoveAction extends ISimpleAction {

	/**
	 * The target of the deletion. This will either be an attribute-relative
	 * reference, or an element reference. Use
	 * {@link IModelReference#getReferenceType()} to determine which type
	 * applies.
	 * 
	 * @return the target
	 */
	public IModelReference getTarget();

	/**
	 * Return the variable that is being provided with the where clause. This
	 * will return null if there is no where clause, otherwise it will always
	 * return a variable.
	 * 
	 * @return the variable
	 */
	public IVariable getVariable();

	/**
	 * Return a where clause if there is one. This can only return a non-null
	 * value if the target is an element (rather than an attribute).
	 * 
	 * @return the where clause or null
	 */
	public IConstraint getWhere();
}
