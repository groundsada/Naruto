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

import java.util.List;

/**
 * A package contains model elements and other packages.
 * 
 * @author Christian Nentwich
 */
public interface IPackage extends IModelElement {

	/**
	 * Return the contents of this package, a collection of IModelElement
	 * objects. Can optionally return all contents recursively, including the
	 * contents of subpackages.
	 * 
	 * @param deep if true, return the contents of subpackages
	 * @return the contents, a list of IModelElement objects
	 */
	public List<IModelElement> getContents(boolean deep);

	/**
	 * Scan a package and return the model element with a given name.
	 * Optionally, also scans all subpackages.
	 * <p>
	 * Note: This returns the <b>first</b> element matching the name if a deep
	 * search is used. Use {@link #isAmbiguous(String)} to determine if multiple
	 * elements with the name exist - and if so, look them up in the right
	 * package instead.
	 * 
	 * @param name the name to look for
	 * @param deep scan subpackages if true
	 * @return the element or null if not found
	 */
	public IModelElement getElementByName(String name, boolean deep);

	/**
	 * Return the number of elements and packages in the package
	 * 
	 * @return the number of elements and packages
	 */
	public int getSize();

	/**
	 * Returns true if a model element name occurs in multiple sub-packages, and
	 * hence a global reference to the name would be ambiguous.
	 * 
	 * @param name the element name
	 * @return true if multiple elements with this name exist in the package or
	 *         subpackages
	 */
	public boolean isAmbiguous(String name);
}
