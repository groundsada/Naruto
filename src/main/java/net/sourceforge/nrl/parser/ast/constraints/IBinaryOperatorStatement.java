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

/**
 * A binary operator statement is either an and, or, iff or implies statement.
 * The interface provides methods for determining which operator applies, and
 * for manipulating the two children.
 * 
 * @author Christian Nentwich
 */
public interface IBinaryOperatorStatement extends IConstraint {

	/**
	 * Available operators.
	 */
	public enum Operator {
		AND, OR, IFF, IMPLIES
	};

	/**
	 * Return the left parameter. Never returns null.
	 * 
	 * @return the left parameter
	 */
	public IConstraint getLeft();

	/**
	 * Return the right parameter. Never returns null.
	 * 
	 * @return the right parameter
	 */
	public IConstraint getRight();

	/**
	 * Return the constant indicating which operator applies. The constant is
	 * one of the enumeration values defined in this interface.
	 * 
	 * @return the operator constant
	 */
	public Operator getOperator();
}
