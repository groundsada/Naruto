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
package net.sourceforge.nrl.parser.ast.impl;

import net.sourceforge.nrl.parser.ast.IModelFileReference;
import net.sourceforge.nrl.parser.model.IPackage;

import org.antlr.runtime.Token;

public class ModelFileReferenceImpl extends Antlr3NRLBaseAst implements
		IModelFileReference {

	public IPackage model;
	
	public ModelFileReferenceImpl(Token token) {
		super(token);
	}

	public String getFileName() {
		return getChild(0).getText();
	}

	public boolean isAbsolute() {
		String name = getFileName();

		return name.startsWith("/") || (name.length() > 2 && name.charAt(1) == ':');
	}

	public String dump(int indent) {
		StringBuffer result = new StringBuffer();
		result.append(doIndent(indent) + "Model file " + getFileName() + NEWLINE);

		return result.toString();
	}

	public IPackage getModel() {
		return model;
	}

	public boolean isModelResolved() {
		return model != null;
	}
	
	public void resolveModel(IPackage model){
		if(model == null){
			throw new IllegalStateException("Model reference already resolved.");
		}
		this.model = model;
	}
}
