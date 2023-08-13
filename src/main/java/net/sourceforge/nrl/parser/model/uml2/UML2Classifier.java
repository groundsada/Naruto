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

import static net.sourceforge.nrl.parser.model.uml2.UML2Helper.cleanUpName;
import static net.sourceforge.nrl.parser.model.uml2.UML2Helper.extractComments;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.nrl.parser.model.AbstractClassifier;
import net.sourceforge.nrl.parser.model.AbstractModelElement;
import net.sourceforge.nrl.parser.model.IAttribute;
import net.sourceforge.nrl.parser.model.IClassifier;
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.model.IPackage;

import org.eclipse.uml2.uml.Classifier;

/**
 * Extension of a standard model element that holds a reference to a UML2 class.
 * 
 * @author Christian Nentwich
 */
public class UML2Classifier extends AbstractClassifier {

	// Reference to the UML2 (EMF) class
	private Classifier umlClassifier;

	// UML2 superclass, for later resolution (can be null)
	private Classifier umlSuperClassifier;

	/**
	 * Initialise a classifier from a UML2 Classifier
	 * 
	 * @param umlClassifier the classifier
	 */
	public UML2Classifier(Classifier umlClassifier, IPackage container) {
		super(cleanUpName(umlClassifier.getName()), container);
		setOriginalName(umlClassifier.getName());
		this.umlClassifier = umlClassifier;
	}

	/**
	 * Produces the documentation from the enclosed comments on the UML classifier.
	 */
	public List<String> getDocumentation() {
		return extractComments(umlClassifier);
	}

	public ElementType getElementType() {
		if (isEnumeration())
			return IModelElement.ElementType.Enumeration;
		return IModelElement.ElementType.Classifier;
	}

	/**
	 * Return the UML class wrapped by this class.
	 * 
	 * @return the UML class
	 */
	public Classifier getUML2() {
		return umlClassifier;
	}

	@Override
	public Object getUserData(String key) {
		if (IUML2UserData.UML2_ELEMENT.equals(key))
			return getUML2();
		return super.getUserData(key);
	}

	/**
	 * Resolve the types of all attributes stored on this class.
	 * 
	 * @param a mapping from uml classes to model element instances
	 * @param warnings the list to append warnings to
	 */
	protected void resolve(Map<Classifier, IClassifier> umlClassToModelElement,
			List<String> warnings) {
		// Look up superclass
		if (umlSuperClassifier != null) {
			IModelElement superClass = umlClassToModelElement.get(umlSuperClassifier);
			if (superClass != null) {
				setParent(superClass);
				((AbstractModelElement) superClass).addChild(this);
			} else {
				warnings.add("Cannot find superclass of " + getName()
						+ ". Generalization discarded.");
			}
		}

		// Resolve the attributes
		for (Iterator<IAttribute> iter = getAttributes().iterator(); iter.hasNext();) {
			UML2Attribute attr = (UML2Attribute) iter.next();

			// Is this an enumeration? Then set all literals to be of this type
			if (isEnumeration()) {
				attr.setType(this);
			} else {
				attr.resolve(umlClassToModelElement);
			}

			if (attr.getName() == null || attr.getName().trim().equals("")) {
				iter.remove();
				warnings.add("Removed empty attribute from " + getName());
			}

			// FIX: Ignore the schema location attribute
			if (attr.getType() == null && !attr.getName().equals("schemaLocation")) {
				warnings.add("Removed attribute " + attr.getName()
						+ " with unresolvable type from " + getName() + " (UML type "
						+ attr.getUMLType().getName() + ")");
				iter.remove();
				nameToAttribute.remove(attr.getName());
			}
		}
	}

	protected void removeHyperModelArtifacts() {

		boolean done = true;

		// Keep removing attributes until no more hypermodel attributes are
		// present. This needs to be repeated like this because the replacements
		// may themselves be hypermodel attributes

		do {
			done = true;

			for (Iterator<IAttribute> iter = getAttributes().iterator(); iter.hasNext();) {
				UML2Attribute attr = (UML2Attribute) iter.next();

				// If it's a group, replace the group attribute with all
				// attributes of the target class
				if (attr.getName().startsWith("_group") || attr.getName().startsWith("_attrGroup")) {
					UML2Classifier groupClass = (UML2Classifier) attr.getType();
					iter.remove();

					if (groupClass == null)
						continue;

					for (Iterator<IAttribute> otherIter = groupClass.getAttributes().iterator(); otherIter
							.hasNext();) {
						addAttribute(otherIter.next());
					}

					done = false;
					break;
				}
			}
		} while (!done);
	}

	/**
	 * Set the superclass of this one in the UML model
	 * 
	 * @param umlSuperClass the super class
	 */
	public void setUMLSuperClass(Classifier umlSuperClass) {
		this.umlSuperClassifier = umlSuperClass;
	}
}
