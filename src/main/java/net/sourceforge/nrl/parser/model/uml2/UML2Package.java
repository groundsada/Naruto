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
package net.sourceforge.nrl.parser.model.uml2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.nrl.parser.model.AbstractPackage;
import net.sourceforge.nrl.parser.model.IClassifier;
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.model.IPackage;

import org.eclipse.uml2.uml.Classifier;

/**
 * A package implementation that wraps a UML2 Package objects.
 * 
 * @author Christian Nentwich
 */
public class UML2Package extends AbstractPackage {

	// The package we're wrapping
	private org.eclipse.uml2.uml.Package _package;

	public UML2Package(org.eclipse.uml2.uml.Package _package, IPackage container) {
		super(_package.getName(), container);
		this._package = _package;
	}

	/**
	 * Return any documentation associated with the UML package.
	 */
	public List<String> getDocumentation() {
		return UML2Helper.extractComments(_package);
	}

	/**
	 * Return the UML2 package wrapped by this class.
	 * 
	 * @return the package
	 */
	public org.eclipse.uml2.uml.Package getUML2() {
		return _package;
	}

	@Override
	public Object getUserData(String key) {
		if (IUML2UserData.UML2_ELEMENT.equals(key))
			return getUML2();
		return super.getUserData(key);
	}

	/**
	 * Resolve attribute and generalisation references, then discard the internal UML 2 model
	 * references to save memory.
	 * 
	 * @param warnings the list to append warnings to
	 */
	protected void resolveTypes(Map<Classifier, IClassifier> classifierToUML2Classifier, List<String> warnings) {
		// Resolve the element and its attributes
		for (IModelElement e : getContents(true)) {
			if (e instanceof UML2Classifier) {
				UML2Classifier element = (UML2Classifier) e;
				element.resolve(classifierToUML2Classifier, warnings);
			} else if (e instanceof UML2DataType) {
				UML2DataType element = (UML2DataType) e;
				element.resolve(classifierToUML2Classifier, warnings);
			}
		}
	}

	protected Map<Classifier, IClassifier> buildTypeMap() {
		Map<Classifier, IClassifier> classifierToUML2Classifier = new HashMap<Classifier, IClassifier>();

		// Build up a list of model elements
		for (IModelElement element : getContents(true)) {
			if (element instanceof UML2Classifier) {
				classifierToUML2Classifier.put(((UML2Classifier) element).getUML2(), (UML2Classifier)element);
			} else if (element instanceof UML2DataType) {
				classifierToUML2Classifier.put(((UML2DataType) element).getUML2(), (UML2DataType)element);
			}
			
			
		}
		return classifierToUML2Classifier;
	}

	/**
	 * Remove group, sequence and choice artifacts from classifiers
	 */
	protected void removeHyperModelArtifacts() {
		for (IModelElement element : getContents(false)) {
			if (element instanceof UML2Classifier) {
				((UML2Classifier) element).removeHyperModelArtifacts();
			} else if (element instanceof UML2Package) {
				((UML2Package) element).removeHyperModelArtifacts();
			}
		}
	}
}
