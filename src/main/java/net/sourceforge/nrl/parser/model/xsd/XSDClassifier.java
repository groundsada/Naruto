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
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.model.IPackage;

/**
 * An XSD classifier, usually created from a complex type or element.
 * 
 * @author Christian Nentwich
 */
public class XSDClassifier extends AbstractClassifier {

	// The qualified name of the parent type - before resolution
	private String parentName;

	public XSDClassifier(String name, IPackage container) {
		super(name, container);

	}

	public List<String> getDocumentation() {
		return new ArrayList<String>();
	}

	public ElementType getElementType() {
		if (isEnumeration())
			return IModelElement.ElementType.Enumeration;
		return IModelElement.ElementType.Classifier;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
}
