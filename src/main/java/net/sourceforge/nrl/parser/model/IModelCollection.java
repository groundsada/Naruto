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
package net.sourceforge.nrl.parser.model;

/**
 * A collection of model packages. This contains one or more top level
 * {@link net.sourceforge.nrl.parser.model.IPackage} objects.
 * 
 * @author Christian Nentwich
 */
public interface IModelCollection {

	/**
	 * Return all top-level packages in the collection. The array can be empty
	 * but is never null.
	 * 
	 * @return the packages
	 */
	public IPackage[] getModelPackages();

	/**
	 * Do a deep search of model elements by name in all packages of all models.
	 * 
	 * @param name the name
	 * @return the element if found or null
	 */
	public IModelElement getElementByName(String name);

	/**
	 * Get an element by its exact, qualified name (e.g. package::element).
	 * 
	 * @param qualifiedName the qualified name
	 * @return the elemtn or null
	 */
	public IModelElement getElementByQualifiedName(String qualifiedName);

	/**
	 * Return a model package by name.
	 * 
	 * @param name the name
	 * @return the package or null if not found
	 */
	public IPackage getModelPackageByName(String name);

	/**
	 * Returns true if a model element name is ambiguous because it occurs in
	 * multiple packages within one model, or within multiple models.
	 * 
	 * @param name the name to check
	 * @return true if ambiguous
	 */
	public boolean isAmbiguous(String name);

	/**
	 * Returns the number of models in the collection. This is a short-hand
	 * method.
	 * 
	 * @return the number of models
	 */
	public int size();
}
