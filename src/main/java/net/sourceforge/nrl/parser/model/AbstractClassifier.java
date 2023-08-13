/*
 * The contents of this file are subject to the Mozilla Public License Version 1.1 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF
 * ANY KIND, either express or implied. See the License for the specific language governing rights
 * and limitations under the License.
 * 
 * The Original Code is the NRL Parser, released 28 April 2006, Copyright (c) Christian Nentwich.
 * The Initial Developer of the Original Code is Christian Nentwich. Portions created by
 * contributors identified in the NOTICES file are Copyright (c) the contributors. All Rights
 * Reserved.
 */
package net.sourceforge.nrl.parser.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract basic implementation of a classifier that manages attribute collections. Subclasses need
 * to provide further detail.
 * 
 * @author Christian Nentwich
 */
public abstract class AbstractClassifier extends AbstractModelElement implements IClassifier {

	private List<IAttribute> attributes = new ArrayList<IAttribute>();

	protected Map<String, IAttribute> nameToAttribute = new HashMap<String, IAttribute>();

	// Does this classifier contain static attributes
	// -1 = not initialised, 0 = no, 1 = yes
	private int containsStaticAttributes = -1;

	// Same as above, but for non-static attributes
	private int containsNonStaticAttributes = -1;

	private boolean enumeration = false;

	public AbstractClassifier(String name, IPackage container) {
		super(name, container);
	}

	public void addAttribute(IAttribute attribute) {
		attributes.add(attribute);
		nameToAttribute.put(attribute.getName(), attribute);
	}

	public List<IAttribute> getAttributes(boolean includeInherited) {
		List<IAttribute> result = new ArrayList<IAttribute>();

		if (includeInherited && getParent() != null && getParent() instanceof IClassifier) {
			IClassifier parent = (IClassifier) getParent();
			result.addAll(parent.getAttributes(true));
		}

		result.addAll(attributes);

		return result;
	}

	/**
	 * Return the actual, unmodified attribute list for use by subclasses.
	 * 
	 * @return the list
	 */
	public List<IAttribute> getAttributes() {
		return attributes;
	}

	public IAttribute getAttributeByName(String name, boolean includeInherited) {
		IAttribute result = nameToAttribute.get(name);
		if (!includeInherited || result != null)
			return result;

		IClassifier run = this;
		while (run != null) {
			result = run.getAttributeByName(name, false);
			if (result != null)
				return result;

			run = (IClassifier) run.getParent();
		}

		return null;
	}

	public boolean hasAttribute(String name) {
		return nameToAttribute.keySet().contains(name);
	}

	public boolean hasNonStaticAttributes() {
		if (containsNonStaticAttributes == -1) {
			containsNonStaticAttributes = 0;
			for (IAttribute attr : attributes) {
				if (!attr.isStatic()) {
					containsNonStaticAttributes = 1;
					break;
				}
			}
		}
		return containsNonStaticAttributes == 1;
	}

	public boolean hasStaticAttributes() {
		if (containsStaticAttributes == -1) {
			containsStaticAttributes = 0;
			for (IAttribute attr : attributes) {
				if (attr.isStatic()) {
					containsStaticAttributes = 1;
					break;
				}
			}
		}
		return containsStaticAttributes == 1;
	}

	public boolean isEnumeration() {
		return enumeration;
	}

	public void rebuildAttributeNameMap() {
		nameToAttribute.clear();
		for (IAttribute attr : attributes) {
			nameToAttribute.put(attr.getName(), attr);
		}
	}

	public void removeAttribute(IAttribute attribute) {
		attributes.remove(attribute);
	}

	public void removeAttributeNameMapping(String name) {
		nameToAttribute.remove(name);
	}

	public void setEnumeration(boolean enumeration) {
		this.enumeration = enumeration;
	}

}
