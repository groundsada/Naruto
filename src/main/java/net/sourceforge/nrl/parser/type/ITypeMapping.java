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
package net.sourceforge.nrl.parser.type;

import net.sourceforge.nrl.parser.ast.NRLDataType;
import net.sourceforge.nrl.parser.model.IAttribute;
import net.sourceforge.nrl.parser.model.IModelElement;

/**
 * A mapping from model elements to types. This is used for resolving model
 * references to internal types from the {@link NRLDataType} class.
 * 
 * @author Christian Nentwich
 */
public interface ITypeMapping {

	/**
	 * Map the type of an attribute to an internal abstract type.
	 * 
	 * @param attr the attribute
	 * @return an NRL type
	 */
	public NRLDataType getType(IAttribute attr);

	/**
	 * Map a model element to an internal, abstract type. If the element is a
	 * classifier and not otherwise mapped, it maps to
	 * {@link NRLDataType#ELEMENT}.
	 * 
	 * @param element the element
	 * @return an NRL type
	 */
	public NRLDataType getType(IModelElement element);
}
