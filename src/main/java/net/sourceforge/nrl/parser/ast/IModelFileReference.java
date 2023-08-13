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
package net.sourceforge.nrl.parser.ast;

import net.sourceforge.nrl.parser.model.IPackage;

public interface IModelFileReference extends INRLAstNode {

	/**
	 * Return the file name. Can be absolute or relative.
	 * 
	 * @return the file name
	 */
	public String getFileName();

	/**
	 * Returns true if the file name is absolute, i.e. starts from a root
	 * directory. If false, it is relative to the rule file.
	 * 
	 * @return true if the file name is absolute, false otherwise
	 */
	public boolean isAbsolute();
	
	/**
	 * Returns <code>true</code> if the model has been resolved in which case it can be accessed
	 * via {@link IModelFileReference.getModel}, <code>false</code> otherwise.
	 * @return <code>true</code> if the model has been resolved, <code>false</code> otherwise.
	 * @since 1.4.10
	 */
	public boolean isModelResolved();
	
	/**
	 * Returns the model referred to by this reference, it has been resolved.
	 * @return The model referred to by this reference, if it has been resolved, null otherwise.
	 * @since 1.4.10
	 */
	public IPackage getModel();

	/**
	 * Attaches a model resolved from this model file reference.
	 * @param model The resolved model for this model file reference
	 */
	public void resolveModel(IPackage model);
}
