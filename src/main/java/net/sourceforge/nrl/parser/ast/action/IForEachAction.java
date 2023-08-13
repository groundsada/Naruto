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

/**
 * An iterative action that executes child actions over each member of a
 * collection.
 * <p>
 * The context of each child action is the type being iterated over.
 * 
 * @author Christian Nentwich
 */
public interface IForEachAction extends ISimpleAction {

	/**
	 * Return the collection to iterate over. This is never null.
	 * 
	 * @return the collection, an attribute or variable reference
	 */
	public IModelReference getCollection();

	/**
	 * If the for-each action introduces an iteration variable, this is returned
	 * here. The iteration variable will be bound to each object being iterated
	 * over.
	 * <p>
	 * If the action uses a short form without a variable, for example
	 * <code>for each trade, ...</code>, then this returns null.
	 * 
	 * @return the variable or null
	 */
	public IVariable getVariable();

	/**
	 * Return the action to execute on each member of the collection. Is never
	 * null. This usually returns a {@link ICompoundAction} or a
	 * {@link IConditionalAction}.
	 * 
	 * @return the action
	 */
	public IAction getAction();

}
