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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.nrl.parser.ast.NRLDataType;
import net.sourceforge.nrl.parser.ast.NRLDataType.Type;

/**
 * A factory singleton that returns data types corresponding to the internal NRL
 * data types.
 * <p>
 * Call {@link #getInstance()} to get a handle on the singleton.
 * 
 * @author Christian Nentwich
 */
public class PrimitiveTypeFactory {

	private static PrimitiveTypeFactory instance;

	private IDataType tBoolean = new SimpleTypeImplementation(Type.Boolean, "Boolean");

	private IDataType tString = new SimpleTypeImplementation(Type.String, "String");

	private IDataType tDate = new SimpleTypeImplementation(Type.Date, "Date");

	private IDataType tDecimalNumber = new SimpleTypeImplementation(Type.Decimal, "Decimal");

	private IDataType tFloat = new SimpleTypeImplementation(Type.Decimal, "Float");
	
	private IDataType tDouble = new SimpleTypeImplementation(Type.Decimal, "Double");

	private IDataType tIntegerNumber = new SimpleTypeImplementation(Type.Integer, "Integer");
	
	private IDataType tInt= new SimpleTypeImplementation(Type.Integer, "Int");
	
	private IDataType tLong = new SimpleTypeImplementation(Type.Integer, "Long");

	private IDataType tVoid = new SimpleTypeImplementation(Type.Void, "Void");

	private Map<String, IDataType> nameToType = new HashMap<String, IDataType>();

	private Map<String, NRLDataType> nameToNrlType = new HashMap<String, NRLDataType>();

	private PrimitiveTypeFactory() {
		nameToType.put("string", tString);
		nameToType.put("date", tDate);
		nameToType.put("integer", tIntegerNumber);
		nameToType.put("int", tInt);
		nameToType.put("long", tLong);
		nameToType.put("decimal", tDecimalNumber);
		nameToType.put("float", tFloat);
		nameToType.put("double", tDouble);
		nameToType.put("boolean", tBoolean);
		nameToType.put("void", tVoid);

		nameToNrlType.put("string", NRLDataType.STRING);
		nameToNrlType.put("date", NRLDataType.DATE);
		nameToNrlType.put("integer", NRLDataType.INTEGER);
		nameToNrlType.put("int", NRLDataType.INTEGER);
		nameToNrlType.put("long", NRLDataType.INTEGER);
		nameToNrlType.put("decimal", NRLDataType.DECIMAL);
		nameToNrlType.put("float", NRLDataType.DECIMAL);
		nameToNrlType.put("double", NRLDataType.DECIMAL);
		nameToNrlType.put("boolean", NRLDataType.BOOLEAN);
		nameToNrlType.put("void", NRLDataType.VOID);
	}

	public static PrimitiveTypeFactory getInstance() {
		if (instance == null)
			instance = new PrimitiveTypeFactory();
		return instance;
	}

	/**
	 * Return a list of all built-in type names
	 * @return the type names
	 */
	public List<String> getAllTypeNames() {
		List<String> result = new ArrayList<String>();
		for (IDataType type : nameToType.values()) {
			result.add(type.getQualifiedName());
		}
		return result;
	}
	
	public Collection<IDataType> getAllTypes() {
		return nameToType.values();
	}
	
	/**
	 * Return a data type object corresponding to the given NRL type. The type
	 * must be one of:
	 * <ul>
	 * <li>{@link NRLDataType#BOOLEAN}
	 * <li>{@link NRLDataType#STRING}
	 * <li>{@link NRLDataType#DECIMAL}
	 * <li>{@link NRLDataType#INTEGER}
	 * <li>{@link NRLDataType#DATE}
	 * <li>{@link NRLDataType#BOOLEAN}
	 * <li>{@link NRLDataType#VOID}
	 * </ul>
	 * If it is not one of those, null is returned.
	 * 
	 * @param nrlType
	 * @return the type or null
	 */
	public IDataType getType(NRLDataType nrlType) {
		String name = null;
		for (String s : nameToNrlType.keySet()) {
			if (nameToNrlType.get(s).equals(nrlType)) {
				name = s;
				break;
			}
		}
		if (name == null)
			return null;
		return nameToType.get(name);
	}

	/**
	 * Return the NRL data type of a built-in type, given its name
	 * 
	 * @param name the name
	 * @return the data type or {@link NRLDataType#UNKNOWN} if not possible
	 */
	public NRLDataType getNrlType(String name) {
		if (name == null)
			return NRLDataType.UNKNOWN;
		name = name.toLowerCase();

		if (!nameToNrlType.containsKey(name))
			return NRLDataType.UNKNOWN;
		return nameToNrlType.get(name);
	}

	/**
	 * Return a data type by name. This will return a non-null value if the name
	 * is one of:
	 * <ul>
	 * <li>string
	 * <li>date
	 * <li>int, integer
	 * <li>double,number
	 * <li>float
	 * <li>boolean
	 * <li>void
	 * </ul>
	 * 
	 * @param name the type name
	 * @return the type or null
	 */
	public IDataType getType(String name) {
		if (name == null)
			return null;
		name = name.toLowerCase();

		return nameToType.get(name);
	}

	class SimpleTypeImplementation extends AbstractClassifier implements IDataType {

		public SimpleTypeImplementation(NRLDataType.Type type, String name) {
			super("", null);
			setName(name);
		}
		
		public ElementType getElementType() {
			return IModelElement.ElementType.DataType;
		}

		public List<String> getDocumentation() {
			return new ArrayList<String>();
		}
		
		public boolean isBuiltIn() {
			return true;
		}
	}
}
