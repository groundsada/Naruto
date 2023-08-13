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

import java.util.Collections;
import java.util.List;

/**
 * A model element has a name and an optional parent element through inheritance.
 * 
 * @author Christian Nentwich
 */
public interface IModelElement {

	/**
	 * A short summary of what sort of model element this is. Used by
	 * {@link IModelElement#getElementType()}.
	 */
	public enum ElementType {
		/**
		 * Element is a classifier ({@link IClassifier}}.
		 */
		Classifier,
		
		/**
		 * Element is a data type ({@link IDataType}).
		 */
		DataType,
		
		/**
		 * Element is a data type ({@link IDataType}), but has attributes.
		 */
		DataTypeWithAttributes,
		
		/**
		 * Element is an {@link IClassifier} representing an enumeration.
		 */
		Enumeration,
		
		/**
		 * Element is an {@link IPackage}.
		 */
		Package
	}

	/**
	 * The parent of all NRL model elements.
	 */
	public final static IModelElement OBJECT = new AbstractClassifier("Object", null) {
		public List<String> getDocumentation() {
			return Collections.emptyList();
		}

		public ElementType getElementType() {
			return ElementType.Classifier;
		}

		@Override
		public IModelElement getParent() {
			return null;
		}
		
		@Override
		public boolean isSupplementary() {
			return true;
		}
	};

	/**
	 * Return the package containing this model element
	 * 
	 * @return the package
	 */
	public IPackage getContainingPackage();

	/**
	 * Return any documentation associated with the element.
	 * 
	 * @return the documentation as a list of strings, or an empty list
	 */
	public List<String> getDocumentation();

	/**
	 * Return an enum indicating what type of element this is, to reduce the need to
	 * sub-cast prematurely.
	 * 
	 * @return the element type
	 */
	public ElementType getElementType();

	/**
	 * Return the model element's name.
	 * 
	 * @return the name
	 */
	public String getName();

	/**
	 * Return the parent, if there is an inheritance relationship, or null if none.
	 * 
	 * @return the parent or null
	 */
	public IModelElement getParent();

	/**
	 * Return a list of elements that inherit fromm this.
	 * 
	 * @param transitive if true, return children of children recursively
	 * @return the children, may be an empty list
	 */
	public List<IModelElement> getDescendants(boolean transitive);

	/**
	 * Some elements have to be renamed on loading. For example, XML schema elements or
	 * UML classes that have a dot in them have their dots stripped out by the NRL model
	 * loader, otherwise they cannot be used. In these cases, this method will return the
	 * original name found in the model.
	 * <p>
	 * In all other cases, the method returns the same as {@link #getName()}.
	 * 
	 * @return the original name
	 */
	public String getOriginalName();

	/**
	 * Return a fully qualified name for this element, listing all the package it is
	 * contained in.
	 * 
	 * @return the fully qualified name
	 */
	public String getQualifiedName();

	/**
	 * Return user data stored on this model element. This can be used to attach and
	 * retrieve processing information.
	 * 
	 * @param key the key under which the data is stored
	 * @return the data or null if not present
	 */
	public Object getUserData(String key);

	/**
	 * Return true if the model element can be assigned the value of another in an
	 * expression. In a typical object-oriented model, any superclass is assignable from
	 * subclass objects, but not the other way round.
	 * 
	 * @param other the assigned object
	 * @return true if the object can be assigned
	 */
	public boolean isAssignableFrom(IModelElement other);

	/**
	 * Returns a flag indicating whether the element is a supplementary element loaded
	 * from a type library or dependent model. This is necessary to distinguish primary
	 * business model types from those loaded from the UML2 type library, HyperModel or
	 * other sources.
	 * 
	 * @return true if the element is supplementary
	 */
	public boolean isSupplementary();

	/**
	 * Set user data to be stored on this model element. This can be used to attach and
	 * retrieve processing information.
	 * 
	 * @param key the key under which the data is stored
	 * @param data the data
	 */
	public void setUserData(String key, Object data);

}
