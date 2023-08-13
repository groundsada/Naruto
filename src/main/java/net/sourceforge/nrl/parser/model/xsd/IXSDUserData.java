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
package net.sourceforge.nrl.parser.model.xsd;

import net.sourceforge.nrl.parser.model.IModelElement;

/**
 * Additional user data keys that will be attached to the model elements, packages or
 * attributes by the XSD loader. See the constants below for definitions. See
 * {@link IModelElement#getUserData(String)} for information on user data.
 * 
 * @author Christian Nentwich
 */
public interface IXSDUserData {

	/**
	 * If a model element is directly contained in another one, this will be set. The
	 * value of the data will be an object of type {@link IModelElement}.
	 * <p>
	 * This value is set on all anonymous subtypes, which according to JAXB modeling
	 * conventions are contained within the types in which they are used.
	 */
	public final static String CONTAINING_TYPE = "XSD.ContainingType";

	/**
	 * An attribute for {@link XSDPackage} elements: contains the location (a URL) that a
	 * model was loaded from.
	 */
	public final static String MODEL_FILE_LOCATION = "Location";

	/**
	 * Will be set on elements or attributes that are in a namespace - the namespace URI.
	 * The value is a string.
	 */
	public final static String NAMESPACE = "XSD.Namespace";

	/**
	 * On elements that define a substitution group, i.e. represent the element that may
	 * be substituted, this will be Boolean(true). On the substituting elements
	 * {@link #SUBSTITUTION_FOR} will be set.
	 */
	public final static String SUBSTITUTABLE = "XSD.Substitutable";

	/**
	 * On elements that participate in a substitution group, this will point to the "root"
	 * element that this is a substitution for. The value will be an {@link XSDAttribute}.
	 */
	public final static String SUBSTITUTION_FOR = "XSD.SubstitutionFor";

	/** Will be set on elements or attributes to show of which kind they are. */
	public final static String ATTRIBUTE_KIND = "XSD.AttributeKind";

	public final static String XSD_ELEMENT_KIND = "XSD.Element";

	public final static String XSD_ATTRIBUTE_KIND = "XSD.Attribute";

	/** Will be set on elements or attributes to show of which kind they are. */
	public final static String XSD_TYPE_KIND = "XSD.TypeKind";
	
	public final static String XSD_SIMPLE_TYPE_KIND = "XSD.SimpleType";
	
	public final static String XSD_COMPLEX_TYPE_KIND = "XSD.ComplexType";

	/** If present and set to true, the type is declared as a global element. */
	public final static String XSD_GLOBAL_ELEMENT = "XSD.GlobalElement";
	
	/**
	 * An XPath to an IAttribute or IModelElement inside an XML Schema. This is attached
	 * to elements if they were renamed by the model loader, i.e.
	 * {@link #RENAMED_AMBIGUOUS_ATTRIBUTE} is also true.
	 */
	public final static String XSD_PATH = "XSD.Path";

	/**
	 * Where elements are involved in repeating sequence or choices, for example
	 * <code>(A,B,C)*</code>, JAXB generates a flatten list, e.g.
	 * <code>List&lt;Object&gt; getAAndBAndC</code>. This property is attached to
	 * attributes to identify the name of the flattened method the attribute will be
	 * stored in at runtime.
	 */
	public final static String JAXB_FLATTENED_PROPERTY = "JAXB.FlattenedProperty";

	/**
	 * Where duplicate element names are present in a type, JAXB attaches a "catch-all"
	 * list instead of actual named fields. This attribute is attached to identify the
	 * name of the catch-all list.
	 */
	public final static String JAXB_CATCH_ALL = "JAXB.CatchAll";

	/**
	 * A boolean property that is set to true for NRL attributes that had to be renamed.
	 * These cases can happen in schemas where two elements / attributes of the same name
	 * are present in a complex type. See the duplicate elements test in the schema
	 * package for more.
	 */
	public static final String RENAMED_AMBIGUOUS_ATTRIBUTE = "XSD.AmbiguousAttribute";
}
