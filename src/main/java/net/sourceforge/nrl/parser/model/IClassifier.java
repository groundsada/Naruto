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
 * A classifier, inspired by UML, is a model element with attributes.
 * 
 * @author Christian Nentwich
 */
public interface IClassifier extends IModelElement {

	/**
	 * Return a collection of {@link IAttribute} objects.
	 * 
	 * @param includeInherited if true, move up the inheritance hierarchy and
	 *        include all attributes declared in superclasses
	 * @return the attributes
	 */
	public List<IAttribute> getAttributes(boolean includeInherited);

	/**
	 * Return an attribute by name, or null if not found.
	 * 
	 * @param name the name to look for
	 * @param includeInherited if true, search the inherited attributes for the
	 *        name
	 * @return the attribute or null
	 */
	public IAttribute getAttributeByName(String name, boolean includeInherited);

	/**
	 * Return true if a classifier has an attribute with a given name, or false
	 * otherwise.
	 * 
	 * @param name the name
	 * @return true if the attribute is present
	 */
	public boolean hasAttribute(String name);
	
	/**
	 * Return true if the classifier contains any static attributes.
	 * 
	 * @return true or false
	 */
	public boolean hasStaticAttributes();
	
	/**
	 * Return true if the classifier is just an enumeration. An enumeration is a
	 * collection of static attributes.
	 * 
	 * @return true or false
	 */
	public boolean isEnumeration();
}
