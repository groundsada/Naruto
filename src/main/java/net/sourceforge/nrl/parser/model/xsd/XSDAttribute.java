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
package net.sourceforge.nrl.parser.model.xsd;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.nrl.parser.model.AbstractAttribute;
import net.sourceforge.nrl.parser.model.IAttribute;

import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDAttributeUseCategory;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.w3c.dom.Element;

/**
 * An NRL attribute created from a schema sub-element or attribute. Note again,
 * "attribute" refers to how NRL sees an attribute.
 * 
 * @author Christian Nentwich
 */
public class XSDAttribute extends AbstractAttribute {

	private int minOccurs = 1, maxOccurs = 1;

	private boolean _static = false;

	private List<String> documentation = new ArrayList<String>();

	// The type name of the attribute, before resolution
	private String typeName;

	// Used during loading only, then nulled out: the DOM element in the schema where this
	// came from
	private Element schemaElement;

	public XSDAttribute(String name) {
		super(XSDHelper.getCleanedName(name));
		setUserData(IXSDUserData.ATTRIBUTE_KIND, IXSDUserData.XSD_ELEMENT_KIND);
		setOriginalName(name);
	}

	/**
	 * Initialise from a schema attribute use declaration. Sets the name, occurrence and
	 * type name.
	 * 
	 * @param use the attribute use declaration
	 */
	public XSDAttribute(XSDAttributeUse use) {
		super(XSDHelper.getCleanedName(use.getAttributeDeclaration().getName()));
		setOriginalName(use.getAttributeDeclaration().getName());
		setUserData(IXSDUserData.ATTRIBUTE_KIND, IXSDUserData.XSD_ATTRIBUTE_KIND);
		setUserData(IXSDUserData.NAMESPACE, use.getAttributeDeclaration().getTargetNamespace());
		this.maxOccurs = 1;

		if (use.getUse() == null) {
			this.minOccurs = 0;
		} else {
			switch (use.getUse().getValue()) {
			case XSDAttributeUseCategory.OPTIONAL:
				this.minOccurs = 0;
				break;
			case XSDAttributeUseCategory.REQUIRED:
				this.minOccurs = 1;
				break;
			case XSDAttributeUseCategory.PROHIBITED:
				this.minOccurs = 0;
				this.maxOccurs = 0;
				break;
			}
		}

		XSDTypeDefinition typeDef = use.getAttributeDeclaration().getType();
		if (typeDef != null) {
			if (typeDef.getName() == null) {
				// Anonymous sub type? Use the base type name
				while (typeDef != null) {
					if (typeDef.getBaseType() == null
							|| XSDBuiltInSimpleTypes.NAMESPACE.equals(typeDef.getTargetNamespace()))
						break;
					typeDef = typeDef.getBaseType();
				}

				this.typeName = XSDHelper.getQualifiedName(typeDef.getTargetNamespace(), XSDHelper
						.getCleanedName(typeDef.getName()));
			} else {
				// Named type
				this.typeName = XSDHelper.getQualifiedName(typeDef.getTargetNamespace(), XSDHelper
						.getCleanedName(typeDef.getName()));
			}
		}
	}

	/**
	 * Initialise from an element declaration. This initialises the name and type name;
	 * 
	 * @param decl the declaration
	 * @param minOccurs the minOccurs value in the XSD library format
	 * @param maxOccurs the maxOccurs value in the XSD library format
	 */
	public XSDAttribute(XSDElementDeclaration decl, int minOccurs, int maxOccurs) {
		super(XSDHelper.getCleanedName(decl.getName()));
		setOriginalName(decl.getName());
		setUserData(IXSDUserData.ATTRIBUTE_KIND, IXSDUserData.XSD_ELEMENT_KIND);
		setUserData(IXSDUserData.NAMESPACE, decl.getTargetNamespace());
		XSDTypeDefinition typeDef = decl.getType();
		if (typeDef != null) {
			this.typeName = XSDHelper.getQualifiedName(typeDef.getTargetNamespace(), XSDHelper
					.getCleanedName(typeDef.getName()));
		}

		if (minOccurs == XSDParticle.UNBOUNDED)
			this.minOccurs = IAttribute.UNBOUNDED;
		else
			this.minOccurs = minOccurs;

		if (maxOccurs == XSDParticle.UNBOUNDED)
			this.maxOccurs = IAttribute.UNBOUNDED;
		else
			this.maxOccurs = maxOccurs;
	}

	public void addDocumentation(String doc) {
		documentation.add(doc);
	}

	public List<String> getDocumentation() {
		return documentation;
	}

	public int getMaxOccurs() {
		return maxOccurs;
	}

	public int getMinOccurs() {
		return minOccurs;
	}

	public boolean isStatic() {
		return _static;
	}

	public void setMaxOccurs(int maxOccurs) {
		this.maxOccurs = maxOccurs;
	}

	public void setMinOccurs(int minOccurs) {
		this.minOccurs = minOccurs;
	}

	public void setStatic(boolean _static) {
		this._static = _static;
	}

	protected Element getSchemaElement() {
		return schemaElement;
	}

	protected void setSchemaElement(Element schemaElement) {
		this.schemaElement = schemaElement;
	}
	
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public void setXSDType(XSDTypeDefinition typeDef) {
		this.typeName = XSDHelper.getQualifiedName(typeDef.getTargetNamespace(), XSDHelper
				.getCleanedName(typeDef.getName()));
	}
}
