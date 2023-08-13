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

import net.sourceforge.nrl.parser.model.AbstractPackage;
import net.sourceforge.nrl.parser.model.IPackage;

/**
 * A package in the XSD model. Packages are used to hold included schemas from
 * other namespaces, and imported schemas.
 * 
 * @author Christian Nentwich
 */
public class XSDPackage extends AbstractPackage {

	public XSDPackage(String name, IPackage container) {
		super(name, container);
	}

	public List<String> getDocumentation() {
		return new ArrayList<String>();
	}

}
