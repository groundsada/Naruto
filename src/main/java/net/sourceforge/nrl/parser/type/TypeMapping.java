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
package net.sourceforge.nrl.parser.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.nrl.parser.ast.NRLDataType;
import net.sourceforge.nrl.parser.ast.NRLDataType.Type;
import net.sourceforge.nrl.parser.model.IAttribute;
import net.sourceforge.nrl.parser.model.IClassifier;
import net.sourceforge.nrl.parser.model.IModelElement;

/**
 * A default type mapping implementation. The type mapping is completely empty and can be populated
 * using the <code>add</code> methods before use.
 * <p>
 * Alternatively, a subclass with a serializer/deserializer method can be used to load and save the
 * mapping to a file, for example {@link net.sourceforge.nrl.parser.type.XmlTypeMapping}.
 * 
 * @author Christian Nentwich
 */
public class TypeMapping implements ITypeMapping {

	// A linear list of all TypeMappingEntry objects. Maintained for cases
	// where an ordered view is needed
	private List<TypeMappingEntry> allMappings = new ArrayList<TypeMappingEntry>();

	// A mapping from String qualified name to TypeMappingEntry object.
	private Map<String, TypeMappingEntry> qualifiedNameToMapping = new HashMap<String, TypeMappingEntry>();

	/**
	 * Add all entries from another mapping to this one.
	 * 
	 * @param other the mapping to add
	 */
	public void addAll(TypeMapping other) {
		for (TypeMappingEntry entry : other.getMapping()) {
			addMapping(entry.getPackageName(), entry.getModelElementName(), entry.getType());
		}
	}

	/**
	 * Add a mapping from a type name, in a particular package, to an internal type. The package can
	 * be passed as "*" to indicate that the type is always mapped, no matter where it occurs.
	 * 
	 * @param packageName the package name or "*"
	 * @param typeName the type (class/data type) name
	 * @param type the target type
	 */
	public TypeMappingEntry addMapping(String packageName, String typeName, NRLDataType type) {
		String lookupKey = typeName;
		if (!packageName.equals("*"))
			lookupKey = packageName + "::" + typeName;

		TypeMappingEntry entry = qualifiedNameToMapping.get(lookupKey);
		if (entry == null) {
			entry = new TypeMappingEntry(packageName, typeName, type);
			allMappings.add(entry);
		} else
			entry.setType(type);

		qualifiedNameToMapping.put(lookupKey, entry);
		return entry;
	}

	/**
	 * Empty the mapping.
	 */
	public void clear() {
		allMappings.clear();
		qualifiedNameToMapping.clear();
	}

	/**
	 * Return true if this contains a mapping for a particular package and type name.
	 * 
	 * @param packageName the package
	 * @param typeName the type
	 * @return true if found
	 */
	public boolean contains(String packageName, String typeName) {
		if (packageName.equals("*"))
			return qualifiedNameToMapping.containsKey(typeName);
		return qualifiedNameToMapping.containsKey(packageName + "::" + typeName);
	}

	/**
	 * Return the type mapping entries, in order.
	 * 
	 * @return the entries
	 */
	public List<TypeMappingEntry> getMapping() {
		return allMappings;
	}

	/**
	 * Return a serializable string for the integer constant from {@link NRLDataType}. If it is not
	 * one of the constants, returns "Unknown".
	 * 
	 * @param type the type value
	 * @return a string value
	 */
	public static String getStringForType(NRLDataType type) {
		switch (type.getType()) {
		case Boolean:
			return "Boolean";
		case Date:
			return "Date";
		case Element:
			return "Element";
		case Decimal:
			return "DecimalNumber";
		case Integer:
			return "IntegerNumber";
		case String:
			return "String";
		case Void:
			return "Void";
		}
		return "Unknown";
	}

	/**
	 * Convert a string representation of a type back to an integer number. The string
	 * representation is as produced by {@link #getStringForType(NRLDataType)}. If it is not one of
	 * this, returns {@link NRLDataType#UNKNOWN}.
	 * 
	 * @param type the string
	 * @return the type
	 */
	public static NRLDataType getTypeFromString(String type) {
		if (type == null)
			return NRLDataType.UNKNOWN;

		if (type.equals("Boolean"))
			return NRLDataType.BOOLEAN;
		if (type.equals("Date"))
			return NRLDataType.DATE;
		if (type.equals("Element"))
			return NRLDataType.ELEMENT;
		if (type.equals("DecimalNumber"))
			return NRLDataType.DECIMAL;
		if (type.equals("IntegerNumber"))
			return NRLDataType.INTEGER;
		if (type.equals("String"))
			return NRLDataType.STRING;
		if (type.equals("Void"))
			return NRLDataType.VOID;

		// Backwards compatibility with earlier parser version
		if (type.equals("Number"))
			return NRLDataType.DECIMAL;

		return NRLDataType.UNKNOWN;
	}

	public NRLDataType getType(IAttribute attr) {
		if (attr == null)
			return NRLDataType.UNKNOWN;

		NRLDataType type = getType(attr.getType());
		if (type.getType() != Type.Unknown
				&& (attr.getMaxOccurs() > 1 || attr.getMaxOccurs() == IAttribute.UNBOUNDED)) {
			type.setCollection(true);
		}

		return type;
	}

	public NRLDataType getType(IModelElement element) {
		if (element == null)
			return NRLDataType.UNKNOWN;

		// Try to look up as qualified package and type name
		String lookupKey = element.getQualifiedName();

		TypeMappingEntry entry = qualifiedNameToMapping.get(lookupKey);
		if (entry == null) {
			// Try again with "*"
			entry = qualifiedNameToMapping.get(element.getName());
		}

		// Found? Return the type
		if (entry != null) {
			NRLDataType type = new NRLDataType(entry.getType());
			if ((element instanceof IClassifier) && ((IClassifier) element).isEnumeration()) {
				type.setEnumeration(true);
			}
			return type;
		}

		return NRLDataType.UNKNOWN;
	}

	/**
	 * Remove a particular mapping
	 * 
	 * @param packageName the package name
	 * @param modelElementName the type name
	 */
	public void remove(String packageName, String modelElementName) {
		String lookupKey = modelElementName;
		if (!packageName.equals("*"))
			lookupKey = packageName + "::" + modelElementName;

		if (qualifiedNameToMapping.containsKey(lookupKey)) {
			qualifiedNameToMapping.remove(lookupKey);

			for (Iterator<TypeMappingEntry> iter = allMappings.iterator(); iter.hasNext();) {
				TypeMappingEntry entry = iter.next();
				if (entry.getPackageName().equals(packageName)
						&& entry.getModelElementName().equals(modelElementName)) {
					iter.remove();
					break;
				}
			}
		}
	}

	/**
	 * Rename an entry. If the entry is not found, nothing happens.
	 * 
	 * @param oldPackageName the old package name
	 * @param oldModelElementName the old type name
	 * @param newPackageName the new package name
	 * @param newModelElementName new type name
	 */
	public void rename(String oldPackageName, String oldModelElementName, String newPackageName,
			String newModelElementName) {
		String lookupKey = oldModelElementName;
		if (!oldPackageName.equals("*"))
			lookupKey = oldPackageName + "::" + oldModelElementName;

		if (qualifiedNameToMapping.containsKey(lookupKey)) {
			TypeMappingEntry entry = qualifiedNameToMapping.get(lookupKey);
			qualifiedNameToMapping.remove(lookupKey);

			entry.setPackageName(newPackageName);
			entry.setModelElementName(newModelElementName);

			lookupKey = newModelElementName;
			if (!newPackageName.equals("*"))
				lookupKey = newPackageName + "::" + newModelElementName;
			qualifiedNameToMapping.put(lookupKey, entry);
		}
	}
}
