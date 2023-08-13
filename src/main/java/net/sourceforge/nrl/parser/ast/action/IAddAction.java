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
import net.sourceforge.nrl.parser.ast.constraints.IExpression;

/**
 * An action that adds an element to a collection. The element to be added is either an
 * attribute, or a complex object held in a variable. The target is always a collection -
 * the type checker makes sure of that.
 * 
 * @author Christian Nentwich
 */
public interface IAddAction extends ISimpleAction {

	/**
	 * Return the element to add. Never returns null after model resolultion. This model
	 * reference will typically point to an attribute.
	 * 
	 * @return the element
	 * @deprecated As of version 1.4.10, this method is deprecated. It will continue to
	 * work with old rule files, however since add expressions can now return any
	 * expression, not just model references, you should really call {@link #getSource()}.
	 */
	@Deprecated
	public IModelReference getElement();

	/**
	 * Return the expression to add to the list. This can either be a simple model
	 * reference or any other expression.
	 * <p>
	 * Replaces {@link #getElement()}.
	 * 
	 * @return the expression
	 * @since 1.4.10
	 */
	public IExpression getSource();

	/**
	 * Return the collection to add to. This always point to an attribute and is never
	 * null.
	 * 
	 * @return the collection attribute reference
	 */
	public IModelReference getTo();

}
