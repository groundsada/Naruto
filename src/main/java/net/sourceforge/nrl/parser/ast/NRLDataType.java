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

/**
 * Representation of NRL primitive types. A type will be of a certain kind (e.g.
 * integer, string) and will also be either scalar, or a list or enumeration of
 * that type.
 * 
 * @author Christian Nentwich
 */
public class NRLDataType {

	public static NRLDataType UNKNOWN = new NRLDataType(Type.Unknown);

	/** Scalar string type */
	public static NRLDataType STRING = new NRLDataType(Type.String);

	/** Scalar date type */
	public static NRLDataType DATE = new NRLDataType(Type.Date);

	/** Scalar Integer type */
	public static NRLDataType INTEGER = new NRLDataType(Type.Integer);

	/** Scalar decimal (float, double) type */
	public static NRLDataType DECIMAL = new NRLDataType(Type.Decimal);

	/** Scalar boolean type */
	public static NRLDataType BOOLEAN = new NRLDataType(Type.Boolean);

	/** Scalar void type */
	public static NRLDataType VOID = new NRLDataType(Type.Void);

	/** Scalar element type (complex model element) */
	public static NRLDataType ELEMENT = new NRLDataType(Type.Element);

	/** Enumeration of basic type names */
	public enum Type {
		Unknown, String, Decimal, Integer, Boolean, Date, Element, Void
	}

	private boolean collection = false;

	private boolean enumeration = false;

	private Type type = Type.Unknown;

	public NRLDataType() {
	}

	public NRLDataType(Type type) {
		this.type = type;
	}

	public NRLDataType(NRLDataType other) {
		this.type = other.type;
		this.collection = other.collection;
		this.enumeration = other.enumeration;
	}
	
	public NRLDataType(Type type, boolean collection, boolean enumeration) {
		this.type = type;
		this.collection = collection;
		this.enumeration = enumeration;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NRLDataType))
			return false;
		NRLDataType other = (NRLDataType) obj;
		return this.type.equals(other.type) && this.collection == other.collection
				&& this.enumeration == other.enumeration;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + type.hashCode();
		result = 31 * result + ((Boolean)collection).hashCode();
		result = 31 * result + ((Boolean)enumeration).hashCode();
		return result;
	}

	public Type getType() {
		return type;
	}

	public boolean isCollection() {
		return collection;
	}

	public boolean isEnumeration() {
		return enumeration;
	}

	public void setCollection(boolean collection) {
		this.collection = collection;
	}

	public void setEnumeration(boolean enumeration) {
		this.enumeration = enumeration;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public String toString() {
		String str = isEnumeration() ? "Enumeration of " : "";
		str = str + (isCollection() ? "Collection of " : "");
		str = str + getType().toString();
		return str;
	}
}
