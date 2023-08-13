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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.nrl.parser.model.AbstractClassifier;
import net.sourceforge.nrl.parser.model.AbstractModelElement;
import net.sourceforge.nrl.parser.model.IAttribute;
import net.sourceforge.nrl.parser.model.IClassifier;
import net.sourceforge.nrl.parser.model.IDataType;
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.model.IPackage;

import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.DataType;

/**
 * A data type that wraps a UML2 DataType object.
 * 
 * @author Christian Nentwich
 */
public class UML2DataType extends AbstractClassifier implements IDataType {

	private DataType dataType;

	private Classifier umlSuperClassifier;

	public UML2DataType(DataType dataType, IPackage container) {
		super(dataType.getName(), container);
		this.dataType = dataType;
	}

	/**
	 * Produces the documentation from the enclosed comments on the UML data type.
	 */
	public List<String> getDocumentation() {
		return UML2Helper.extractComments(dataType);
	}

	public ElementType getElementType() {
		if (hasNonStaticAttributes())
			return IModelElement.ElementType.DataTypeWithAttributes;
		if (isEnumeration())
			return IModelElement.ElementType.Enumeration;

		return IModelElement.ElementType.DataType;
	}

	public DataType getUML2() {
		return dataType;
	}

	public Classifier getUmlSuperClassifier() {
		return umlSuperClassifier;
	}

	@Override
	public Object getUserData(String key) {
		if (IUML2UserData.UML2_ELEMENT.equals(key))
			return getUML2();
		return super.getUserData(key);
	}

	public boolean isBuiltIn() {
		return false;
	}

	/**
	 * Set the superclass of this one in the UML model
	 * 
	 * @param umlSuperClass the super class
	 */
	public void setUMLSuperClass(Classifier umlSuperClass) {
		this.umlSuperClassifier = umlSuperClass;
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
			if (umlClassToModelElement.get(umlSuperClassifier) instanceof UML2DataType) {
				UML2DataType superType = (UML2DataType) umlClassToModelElement
						.get(umlSuperClassifier);
				if (superType != null) {
					setParent(superType);
					((AbstractModelElement) superType).addChild(this);
				} else {
					warnings.add("Cannot find supertype of " + getName()
							+ ". Generalization discarded.");
				}
			}
		}

		// Resolve the attributes
		for (Iterator<IAttribute> iter = getAttributes().iterator(); iter.hasNext();) {
			UML2Attribute attr = (UML2Attribute) iter.next();

			// Is this an enumeration? Then set all literals to be of this type
			if (isEnumeration())
				attr.setType(this);
			else
				attr.resolve(umlClassToModelElement);

			if (attr.getName() == null || attr.getName().trim().equals("")) {
				iter.remove();
				warnings.add("Removed empty attribute from " + getName());
			}

			if (attr.getType() == null) {
				warnings.add("Removed attribute " + attr.getName()
						+ " with unresolvable type from " + getName() + " (UML type "
						+ attr.getUMLType().getName() + ")");
				iter.remove();
			}
		}
	}
}
