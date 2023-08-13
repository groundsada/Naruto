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

import net.sourceforge.nrl.parser.ast.constraints.IConstraint;

/**
 * An if-then-else type action. If the "if" part, which is an NRL
 * {@link net.sourceforge.nrl.parser.ast.constraints.IConstraint}, holds true, the "then"
 * actions are executed, else the "else" actions are executed.
 * 
 * @author Christian Nentwich
 */
public interface IConditionalAction extends ISimpleAction {

	/**
	 * Returns the condition. This is a boolean constraint, as enforced by the
	 * type checker.
	 * 
	 * @return the condition
	 */
	public IConstraint getIf();

	/**
	 * Return the actions to execute if the condition holds. This is never
	 * null.
	 * 
	 * @return the actions
	 */
	public ICompoundAction getThen();

	/**
	 * Return the actions to execute if the condition does not hold. <b>NOTE:</b>
	 * this may be null if there is no else part.
	 * 
	 * @return the actions or null
	 */
	public ICompoundAction getElse();
}
