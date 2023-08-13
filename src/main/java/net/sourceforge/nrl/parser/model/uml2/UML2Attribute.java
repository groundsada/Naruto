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

import java.util.List;
import java.util.Map;

import net.sourceforge.nrl.parser.model.AbstractAttribute;
import net.sourceforge.nrl.parser.model.IClassifier;
import net.sourceforge.nrl.parser.model.IDataType;
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.model.PrimitiveTypeFactory;

import org.eclipse.uml2.uml.AssociationClass;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.MultiplicityElement;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.TypedElement;

/**
 * Extension of a standard attribute that holds a UML2 typed element reference.
 * 
 * @author Christian Nentwich
 */
public class UML2Attribute extends AbstractAttribute {

	// The UML named element we're wrapping
	private NamedElement umlNamedElement;

	private Integer minOccurs;
	private Integer maxOccurs;

	/**
	 * Initialise the attribute using a UML2 named element object. The type of the attribute will
	 * remain unresolved until {@link #resolve(Map)} is called.
	 * 
	 * @param namedElement the element
	 */
	public UML2Attribute(NamedElement namedElement) {
		super(cleanUpName(namedElement.getName()));

		setOriginalName(namedElement.getName());
		this.umlNamedElement = namedElement;
	}

	public UML2Attribute(NamedElement namedElement, String nameOverride) {
		super(cleanUpName(nameOverride));
		setOriginalName(namedElement.getName());
		this.umlNamedElement = namedElement;
	}

	public UML2Attribute(NamedElement namedElement, int minOccurs, int maxOccurs) {
		this(namedElement);
		this.minOccurs = minOccurs;
		this.maxOccurs = maxOccurs;
	}

	public UML2Attribute(NamedElement namedElement, String nameOverride, int minOccurs,
			int maxOccurs) {
		this(namedElement, nameOverride);
		this.minOccurs = minOccurs;
		this.maxOccurs = maxOccurs;
	}

	/**
	 * Extract the documentation from the UML attribute.
	 */
	public List<String> getDocumentation() {
		return UML2Helper.extractComments(umlNamedElement);
	}

	public int getMinOccurs() {
		if (minOccurs != null) {
			return minOccurs;
		}
		if (umlNamedElement instanceof MultiplicityElement) {
			int lower = ((MultiplicityElement) umlNamedElement).getLower();
			if (lower == -1) {
				return UNBOUNDED;
			} else {
				return lower;
			}
		}
		return 1;
	}

	public int getMaxOccurs() {
		if (maxOccurs != null) {
			return maxOccurs;
		}
		if (umlNamedElement instanceof MultiplicityElement) {
			int upper = ((MultiplicityElement) umlNamedElement).getUpper();
			if (upper == -1) {
				return UNBOUNDED;
			} else {
				return upper;
			}
		}
		return 1;
	}

	public boolean isStatic() {
		if (umlNamedElement instanceof EnumerationLiteral)
			return true;
		if ((umlNamedElement instanceof Property))
			return ((Property) umlNamedElement).isStatic();
		return false;
	}

	public Type getUMLType() {
		if (umlNamedElement instanceof AssociationClass) {
			return (AssociationClass) umlNamedElement;
		} else if (umlNamedElement instanceof TypedElement) {
			return ((TypedElement) umlNamedElement).getType();
		} else {
			return null;
		}
	}

	@Override
	public Object getUserData(String key) {
		if (IUML2UserData.UML2_ELEMENT.equals(key))
			return umlNamedElement;
		return super.getUserData(key);
	}

	/**
	 * Resolve the types of the attribute
	 */
	protected void resolve(Map<Classifier, IClassifier> umlClassToModelElement) {
		if ((umlNamedElement instanceof TypedElement)
				&& ((TypedElement) umlNamedElement).getType() != null) {

			TypedElement umlTypedElement = (TypedElement) umlNamedElement;

			Type type = umlTypedElement.getType();

			// FIX: for anonymous data types, use the supertype
			if (type != null && type.getName() != null && type.getName().startsWith("anonymous")
					&& type instanceof DataType) {

				if (((DataType) type).getGeneralizations().size() > 0) {
					type = ((DataType) type).getGeneralizations().get(0).getGeneral();
				}
			}

			// Check if the type is a basic UML2 data type
			if (type != null && type instanceof PrimitiveType && type.eContainer() instanceof Model
					&& ((Model) type.eContainer()).getName().toLowerCase().equals("uml2")) {

				String name = umlTypedElement.getType().getName().toLowerCase();
				IDataType typeToSet = PrimitiveTypeFactory.getInstance().getType(name);
				if (typeToSet != null) {
					setType(typeToSet);
				}
			}

			IModelElement resultType = umlClassToModelElement.get(type);
			if (resultType != null) {
				setType(resultType);
			}
		} else if (umlNamedElement instanceof AssociationClass) {
			IModelElement resultType = umlClassToModelElement.get(umlNamedElement);
			if (resultType != null) {
				setType(resultType);
			}
		}
	}

}
