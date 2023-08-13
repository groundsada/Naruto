/*
 * The contents of this file are subject to the Mozilla Public License Version 1.1 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF
 * ANY KIND, either express or implied. See the License for the specific language governing rights
 * and limitations under the License.
 * 
 * The Original Code is the NRL Parser, released 28 April 2006, Copyright (c) Christian Nentwich.
 * The Initial Developer of the Original Code is Christian Nentwich. Portions created by
 * contributors identified in the NOTICES file are Copyright (c) the contributors. All Rights
 * Reserved.
 */
package net.sourceforge.nrl.parser.model;

import java.util.List;

/**
 * An attribute has a name, a type and a multiplicity. The type is a model element.
 * 
 * @author Christian Nentwich
 */
public interface IAttribute {

	/** Constant for unbounded upper and lower occurrence */
	public final static int UNBOUNDED = -1;

	/**
	 * Return any documentation associated with the attribute.
	 * 
	 * @return the documentation or an empty list
	 */
	public List<String> getDocumentation();

	/**
	 * Return the attribute's name.
	 * 
	 * @return the name
	 */
	public String getName();

	/**
	 * Return the minimum number of occurrences of the attribute (0 or more).
	 * 
	 * @return the occurrences
	 */
	public int getMinOccurs();

	/**
	 * Return the maximum number of occurrences of the attribute (0 or more).
	 * 
	 * @return the occurrences
	 */
	public int getMaxOccurs();

	/**
	 * Some attributes have to be renamed on loading. For example, XML schema elements or UML
	 * classes that have a dot in them have their dots stripped out by the NRL model loader,
	 * otherwise they cannot be used. In these cases, this method will return the original name
	 * found in the model.
	 * <p>
	 * In all other cases, the method returns the same as {@link #getName()}.
	 * 
	 * @return the original name
	 */
	public String getOriginalName();

	/**
	 * Return the classifier that owns the attribute.
	 * 
	 * @return the classifier
	 */
	public IClassifier getOwner();

	/**
	 * Return user data stored on this attribute. This can be used to attach and retrieve processing
	 * information.
	 * 
	 * @param key the key under which the data is stored
	 * @return the data or null if not present
	 */
	public Object getUserData(String key);

	/**
	 * Return the type.
	 * 
	 * @return the type
	 */
	public IModelElement getType();

	/**
	 * Returns true if the attribute can occur more than once, i.e. max occurs is greater than 1 or
	 * unbounded.
	 * 
	 * @return true if the attribute is repeating
	 */
	public boolean isRepeating();

	/**
	 * Returns true if the attribute is static, or if it is an enum value.
	 * 
	 * @return true if static
	 */
	public boolean isStatic();

	/**
	 * Set the attribute name
	 * 
	 * @param name the new name
	 */
	public void setName(String name);

	/**
	 * Set user data to be stored on this attribute. This can be used to attach and retrieve
	 * processing information.
	 * 
	 * @param key the key under which the data is stored
	 * @param data the data
	 */
	public void setUserData(String key, Object data);
}
