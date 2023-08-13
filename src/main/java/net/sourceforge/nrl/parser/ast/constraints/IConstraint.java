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
package net.sourceforge.nrl.parser.ast.constraints;

import net.sourceforge.nrl.parser.ast.INRLAstNode;
import net.sourceforge.nrl.parser.ast.NRLDataType;

/**
 * A constraint occurs in a rule or fragment declaration, or recursively
 * elsewhere.
 * <p>
 * Abstract syntax reference: <code>Constraint ::= IfThenStatement</code>
 * 
 * @author Christian Nentwich
 */
public interface IConstraint extends INRLAstNode {

	/**
	 * Return a type constant from the {@link NRLDataType} class,
	 * indicating the data type of the constraint.
	 * <p>
	 * This can only be called once a type inference algorithm has been applied
	 * to the constraint or the entire AST. Constraints either have type
	 * themselves, in the case of primitive constraints, or derived types
	 * inherited from expressions. Before type inference is called, the type is
	 * always {@link NRLDataType.Type#Unknown}.
	 * 
	 * @return the type, never null
	 */
	public NRLDataType getNRLDataType();

	/**
	 * Assign an NRL type to this constraint.
	 * 
	 * @param type the type
	 */
	public void setNRLDataType(NRLDataType type);
}
