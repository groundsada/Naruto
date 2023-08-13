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

import net.sourceforge.nrl.parser.ast.IRuleDeclaration;

/**
 * Declaration of an action type rule. Has an id, a context and an action.
 * 
 * @author Christian Nentwich
 */
public interface IActionRuleDeclaration extends IRuleDeclaration {

	/**
	 * Get the rule action. Never returns null. The action is either a
	 * {@link ICompoundAction} or a {@link IConditionalAction} - see the grammar
	 * for more details.
	 * 
	 * @return the action
	 */
	public IAction getAction();

	/**
	 * Indicate whether this action rule has a context, or only uses actions
	 * that do not require a context.
	 * <p>
	 * This will be true for most rules. For some, for example those that only
	 * use "create" or "select", a context is unnecessary and this will return
	 * false.
	 * <p>
	 * Calling {@link net.sourceforge.nrl.parser.ast.IRuleDeclaration#getContext()}
	 * on such a rule will return a reserved model object with no attriutes.
	 * 
	 * @return true if there is a context else false
	 */
	public boolean hasContext();
}
