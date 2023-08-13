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
 * Abstract implementation of a package. Provides basic content management.
 * 
 * @author Christian Nentwich
 */
public abstract class AbstractPackage extends AbstractModelElement implements IPackage {

	private List<IModelElement> contents = new ArrayList<IModelElement>();

	private Map<String, IModelElement> nameToModelElement = new HashMap<String, IModelElement>();

	/**
	 * Create a new package with a name and an optional containing package (can be null)
	 * 
	 * @param name the name
	 * @param container the containing package
	 */
	public AbstractPackage(String name, IPackage container) {
		super(name, container);
	}

	public void addElement(IModelElement element) {
		contents.add(element);
		nameToModelElement.put(element.getName(), element);
	}

	public List<IModelElement> getContents(boolean deep) {
		List<IModelElement> result = new ArrayList<IModelElement>();
		result.addAll(contents);

		if (deep) {
			for (IModelElement e : contents) {
				if (e instanceof IPackage)
					result.addAll(((IPackage) e).getContents(true));
			}
		}

		return result;
	}

	public IModelElement getElementByName(String name, boolean deep) {
		IModelElement result = nameToModelElement.get(name);
		if (!deep || result != null) {
			return result;
		}

		// Null and looking deep? Look for subpackages
		if (result == null) {
			for (IModelElement e : contents) {
				if (e instanceof IPackage) {
					result = ((IPackage) e).getElementByName(name, true);
					if (result != null)
						return result;
				}
			}
		}

		return null;
	}

	public ElementType getElementType() {
		return IModelElement.ElementType.Package;
	}

	public int getSize() {
		return contents.size();
	}

	public boolean isAmbiguous(String name) {
		return getNumberOfElements(name) > 1;
	}

	/**
	 * Return the number of elements with a given name in this package or subpackages. Helper
	 * method.
	 * 
	 * @param name the name to look for
	 * @return the number, can be zero
	 */
	protected int getNumberOfElements(String name) {
		int count = 0;
		for (IModelElement e : contents) {

			if (e.getName().equals(name)) {
				count++;
			}
			if (e instanceof AbstractPackage) {
				count += ((AbstractPackage) e).getNumberOfElements(name);
			}
		}

		return count;
	}

	public void removeElement(IModelElement element) {
		contents.remove(element);
		nameToModelElement.remove(element.getName());
	}
}
