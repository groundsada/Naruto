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

import java.util.HashMap;
import java.util.Map;

/**
 * A basic attribute implementation, to be subclassed. This provides a very
 * basic implementation that managements a name and attribute type.
 * 
 * @author Christian Nentwich
 */
public abstract class AbstractAttribute implements IAttribute {

	private String name;

	private IModelElement type;

	private IClassifier owner;
	
	private String originalName;
	
	private Map<String, Object> userData = new HashMap<String, Object>();
	
	public AbstractAttribute(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getOriginalName() {
		if (originalName == null)
			return getName();
		return originalName;
	}
	
	public IClassifier getOwner() {
		return owner;
	}
	
	public IModelElement getType() {
		return type;
	}

	public Object getUserData(String key) {
		return userData.get(key);
	}
	
	public boolean isRepeating() {
		return getMaxOccurs() > 1 || getMaxOccurs() == IAttribute.UNBOUNDED;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setOriginalName(String name) {
		this.originalName = name;
	}
	
	public void setOwner(IClassifier owner) {
		this.owner = owner;
	}
	
	public void setType(IModelElement type) {
		this.type = type;
	}
	
	public void setUserData(String key, Object data) {
		userData.put(key, data);
	}
}
