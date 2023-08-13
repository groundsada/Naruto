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
package net.sourceforge.nrl.parser.model.xsd;

/**
 * A factory for the XSD built in simple types. This returns type objects for
 * <code>xsd:string</code>, <code>xsd:int</code>, etc. in a package.
 * 
 * @author Christian Nentwich
 */
public class XSDBuiltInSimpleTypes {

	/**
	 * The package name.
	 */
	public final static String PACKAGE = "xsd";

	/**
	 * The schema namespace
	 */
	public final static String NAMESPACE = "http://www.w3.org/2001/XMLSchema";

	/**
	 * List of all primitve types defined by XML Schema
	 */
	private final static String[] TYPES = new String[] { "string", "normalizedString", "token",
			"base64Binary", "hexBinary", "integer", "positiveInteger", "negativeInteger",
			"nonNegativeInteger", "nonPositiveInteger", "long", "unsignedLong", "int",
			"unsignedInt", "short", "unsignedShort", "byte", "unsignedByte", "decimal", "float",
			"double", "boolean", "duration", "dateTime", "date", "time", "gYear", "gYearMonth",
			"gMonth", "gMonthDay", "gDay", "Name", "QName", "NCName", "anyURI", "language", "ID",
			"IDREF", "ENTITY", "NMTOKEN" };

	// Unsupported: "IDREFS", "ENTITIES", "NOTATION", "NMTOKENS"
	
	/**
	 * Return the package containing the simple types
	 * 
	 * @return the package
	 */
	public XSDPackage getSimpleTypePackage() {
		XSDPackage result = new XSDPackage(PACKAGE, null);

		for (int i = 0; i < TYPES.length; i++) {
			addType(result, TYPES[i]);
		}

		return result;
	}

	protected void addType(XSDPackage target, String name) {
		XSDDataType type = new XSDDataType(name, target);
		target.addElement(type);
	}
}
