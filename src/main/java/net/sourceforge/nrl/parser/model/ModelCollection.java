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
import java.util.List;

public class ModelCollection implements IModelCollection {

	// A list of IPackage objects
	private List<IPackage> modelPackages = new ArrayList<IPackage>();

	/**
	 * Add a package to the collection
	 * 
	 * @param modelPackage the package
	 */
	public void addModelPackage(IPackage modelPackage) {
		modelPackages.add(modelPackage);
	}

	/**
	 * Empty the collection.
	 */
	public void clear() {
		modelPackages.clear();
	}

	public IModelElement getElementByName(String name) {
		for (IPackage pack : modelPackages) {
			IModelElement result = pack.getElementByName(name, true);
			if (result != null)
				return result;
		}

		return null;
	}

	public IModelElement getElementByQualifiedName(String qualifiedName) {
		if (qualifiedName == null)
			return null;
		if (qualifiedName.indexOf("::") == -1)
			return getModelPackageByName(qualifiedName);

		String[] steps = qualifiedName.split("::");
		
		IPackage currentPackage = getModelPackageByName(steps[0]);
		if (currentPackage == null)
			return null;
		
		for (int i = 1; i < steps.length - 1; i++) {
			IModelElement next = currentPackage.getElementByName(steps[i], false);
			if (next == null || !(next instanceof IPackage))
				return null;
			currentPackage = (IPackage) next;
		}

		return currentPackage.getElementByName(steps[steps.length - 1], false);
	}

	public IPackage getModelPackageByName(String name) {
		for (IPackage pack : modelPackages) {
			if (pack.getName() != null && pack.getName().equals(name))
				return pack;
		}
		return null;
	}

	public IPackage[] getModelPackages() {
		return modelPackages.toArray(new IPackage[0]);
	}

	public boolean isAmbiguous(String name) {
		int found = 0;

		for (IPackage pack : modelPackages) {
			if (pack.isAmbiguous(name))
				return true;
			if (pack.getElementByName(name, true) != null)
				found++;
		}

		return found > 1;
	}

	public int size() {
		return modelPackages.size();
	}

}
