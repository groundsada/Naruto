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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Basic abstract implementation of a model element that manages the name and parent.
 * Subclasses provide further details.
 * 
 * @author Christian Nentwich
 */
public abstract class AbstractModelElement implements IModelElement {

	private String name;

	private IModelElement parent = OBJECT;

	private List<IModelElement> children = new ArrayList<IModelElement>();

	private IPackage container;

	private boolean supplementary = false;

	private Map<String, Object> userData = new HashMap<String, Object>();

	private String originalName;

	public AbstractModelElement(String name, IPackage container) {
		if (name == null)
			this.name = "";
		else
			this.name = name;

		this.container = container;
	}

	public void addChild(IModelElement child) {
		children.add(child);
	}

	public IPackage getContainingPackage() {
		return container;
	}

	public String getName() {
		return name;
	}

	public IModelElement getParent() {
		return parent;
	}

	public List<IModelElement> getDescendants(boolean transitive) {
		if (!transitive)
			return children;

		List<IModelElement> result = new ArrayList<IModelElement>();
		for (IModelElement child : children) {
			if (!result.contains(child) && child != this) {
				result.add(child);
				result.addAll(child.getDescendants(true));
			}
		}

		return result;
	}

	public String getOriginalName() {
		if (originalName == null)
			return getName();
		return originalName;
	}

	public String getQualifiedName() {
		StringBuffer result = new StringBuffer();
		result.append(name);

		IPackage current = container;
		while (current != null) {
			result.insert(0, current.getName() + "::");
			current = current.getContainingPackage();
		}

		return result.toString();
	}

	public Object getUserData(String key) {
		return userData.get(key);
	}

	public boolean isAssignableFrom(IModelElement other) {
		if (other == this || other == null)
			return true;

		// Check if other is a subclass
		while (other.getParent() != null) {
			other = other.getParent();
			if (other == this)
				return true;
		}

		return false;
	}

	public boolean isSupplementary() {
		return supplementary;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOriginalName(String name) {
		this.originalName = name;
	}

	public void setParent(IModelElement parent) {
		this.parent = parent;
	}

	public void setSupplementary(boolean supplementary) {
		this.supplementary = supplementary;
	}

	public void setUserData(String key, Object data) {
		userData.put(key, data);
	}
}
