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
package net.sourceforge.nrl.parser.type;

import net.sourceforge.nrl.parser.ast.NRLDataType;

/**
 * An entry for the {@link net.sourceforge.nrl.parser.type.TypeMapping} class.
 * A triplet that maps a package name (can be <code>*</code> for any) and a
 * model element name to an internal type. See the mapping class for more
 * details.
 * 
 * @author Christian Nentwich
 */
public class TypeMappingEntry {

	private String packageName;

	private String modelElementName;

	private NRLDataType type;

	public TypeMappingEntry(String packageName, String modelElementName, NRLDataType type) {
		this.packageName = packageName;
		this.modelElementName = modelElementName;
		this.type = type;
	}

	/**
	 * Return the package name. Can be <code>*</code> for "any" package.
	 * 
	 * @return the package name
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * Return the internal data type this maps to. One of the constants from
	 * {@link net.sourceforge.nrl.parser.ast.NRLDataType}.
	 * 
	 * @return the internal type
	 */
	public NRLDataType getType() {
		return type;
	}

	/**
	 * Return the model element name we're mapping.
	 * 
	 * @return the model element name
	 */
	public String getModelElementName() {
		return modelElementName;
	}

	/**
	 * Set the model element name
	 * 
	 * @param modelElementName the new name
	 */
	public void setModelElementName(String modelElementName) {
		this.modelElementName = modelElementName;
	}

	/**
	 * Set the package name
	 * 
	 * @param packageName the new name
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * Set the internal type
	 * 
	 * @param type the type
	 */
	public void setType(NRLDataType type) {
		this.type = type;
	}

	public int hashCode() {
		return modelElementName.hashCode() ^ packageName.hashCode();
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof TypeMappingEntry))
			return false;

		TypeMappingEntry other = (TypeMappingEntry) obj;
		return other.packageName.equals(this.packageName)
				&& other.modelElementName.equals(this.modelElementName);
	}
}
