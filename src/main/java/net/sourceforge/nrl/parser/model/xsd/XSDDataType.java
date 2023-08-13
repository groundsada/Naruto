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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.nrl.parser.model.AbstractClassifier;
import net.sourceforge.nrl.parser.model.IDataType;
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.model.IPackage;

/**
 * An XSD data type is either a simple type, a derived simple type, or a
 * complex type derived from a simple type (with optional added attributes).
 * 
 * @author Christian Nentwich
 */
public class XSDDataType extends AbstractClassifier implements IDataType {

	// The qualified name of the parent type - before resolution
	private String parentName;
	
	// Were any attributes removed from this because they were unsupported?
	private boolean attributesStripped = false;
	
	public XSDDataType(String name, IPackage container) {
		super(name, container);
		
	}
	
	public List<String> getDocumentation() {
		return new ArrayList<String>();
	}

	public ElementType getElementType() {
		if (hasNonStaticAttributes() || hasStrippedAttributes())
			return IModelElement.ElementType.DataTypeWithAttributes;
		if (isEnumeration())
			return IModelElement.ElementType.Enumeration;
		
		return IModelElement.ElementType.DataType;
	}


	public String getParentName() {
		return parentName;
	}

	public boolean hasStrippedAttributes() {
		return attributesStripped;
	}
	
	public boolean isBuiltIn() {
		return false;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public void setAttributesStripped(boolean attributesStripped) {
		this.attributesStripped = attributesStripped;
	}
}
