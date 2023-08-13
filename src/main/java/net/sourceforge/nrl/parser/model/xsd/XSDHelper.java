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
 * Static helper methods for schema import.
 * 
 * @author Christian Nentwich
 */
class XSDHelper {

	/**
	 * Concatenate a namespace and local name using colon separator. If the
	 * namespace is null, it is left off
	 * 
	 * @param namespace the name space
	 * @param name the local name
	 * @return qualified name
	 */
	public static String getQualifiedName(String namespace, String name) {
		if (namespace == null)
			return ":" + name;
		return namespace + ":" + name;
	}

	/**
	 * Clean up an element/attribute name by removing NRL-illegal characters
	 * like dots and inserting camel case instead.
	 * 
	 * @param name the name to clean up
	 * @return the cleaned name
	 */
	public static String getCleanedName(String name) {
		if (name == null || name.trim().length() == 0)
			return name;

		name = name.replace("%20", "");
		
		StringBuffer result = new StringBuffer();

		boolean nextCharUppercase = false;
		int pos = 0;
		while (pos < name.length()) {
			char c = name.charAt(pos);

			// if (Character.isDigit(c) && pos == 0) {
			// result.append("_");
			// }

			if (c == '.' || Character.isWhitespace(c)) {
				nextCharUppercase = true;
			} else {
				if (nextCharUppercase) {
					c = Character.toUpperCase(c);
					nextCharUppercase = false;
				}

				result.append(c);
			}

			pos++;
		}

		return result.toString();
	}

	/**
	 * In a qualified name of the form namespace:name, strip the namespace
	 * leaving only :name.
	 * 
	 * @param qualifiedName the qualified name
	 * @return the stripped name
	 */
	public static String stripNamespace(String qualifiedName) {
		if (qualifiedName == null)
			return qualifiedName;

		if (qualifiedName.indexOf(':') == -1)
			return qualifiedName;

		return qualifiedName.substring(qualifiedName.lastIndexOf(':'));
	}

	/**
	 * Derive a package name from a schema location.
	 * 
	 * @param schemaLocation the location
	 * @return the package name
	 */
	public static String getPackageName(String schemaLocation) {
		int lastSlash = schemaLocation.lastIndexOf('/');
		int lastBackSlash = schemaLocation.lastIndexOf('\\');

		int last = Math.max(lastSlash, lastBackSlash);
		if (last != -1) {
			schemaLocation = schemaLocation.substring(last + 1);
		}

		if (schemaLocation.indexOf(".xsd") != -1) {
			schemaLocation = schemaLocation.substring(0, schemaLocation.indexOf(".xsd"));
		}

		return getCleanedName(schemaLocation);
	}

	/**
	 * Lowercase the first char in a string.
	 * 
	 * @param s the string
	 * @return the string with first char in lower case
	 */
	public static String toLowerCase(String s) {
		if (s == null || s.length() < 1)
			return s;

		return Character.toLowerCase(s.charAt(0)) + s.substring(1);
	}

	/**
	 * Uppercase the first char in a string.
	 * 
	 * @param s the string
	 * @return the string with first char in upper case
	 */
	public static String toUpperCase(String s) {
		if (s == null || s.length() < 1)
			return s;

		if (s.length() == 1)
			return Character.toUpperCase(s.charAt(0)) + "";
		else
			return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}
}
