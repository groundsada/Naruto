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

/**
 * An action that removes an element from a collection. This is the inverse of
 * {@link com.modeltwozero.nrl.parser.ast.action.IAddAction}.
 * 
 * @author Christian Nentwich
 */
public interface IRemoveFromCollectionAction extends ISimpleAction {

	/**
	 * Return the element to remove. Never returns null after model resolution.
	 * This model reference will typically point to an attribute or variable.
	 * 
	 * @return the element
	 */
	public IModelReference getElement();

	/**
	 * Return the collection to remove from. This always point to an attribute
	 * and is never null. The type checker ensures that this is a collection and
	 * that it is compatible with the element being removed.
	 * 
	 * @return the collection attribute reference
	 */
	public IModelReference getFrom();

}
