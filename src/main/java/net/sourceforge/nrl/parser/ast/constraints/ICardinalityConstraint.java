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

/**
 * A cardinality constraint: one, one or more, and so on. Consists of a number
 * and a qualifier (exactly, at least and at most).
 * <p>
 * Abstract syntax reference:
 * <code>CardinalityConstraint ::= Number (at least | at most | exactly)</code>
 * 
 * @author Christian Nentwich
 */
public interface ICardinalityConstraint extends INRLAstNode {

	public enum QualifierEnum {
		/**
		 * The number must match exactly.
		 */
		EXACTLY,

		/**
		 * The target must be at least as big as the number
		 */
		AT_LEAST,

		/**
		 * The target must be at most equal to the number
		 */
		AT_MOST
	};

	/**
	 * Return the cardinality number. The number will be greater than or equal
	 * to zero.
	 * 
	 * @return the number
	 */
	public int getNumber();

	/**
	 * Return one of the qualifier constants defined in this class, that bound
	 * the number.
	 * 
	 * @return the constant
	 */
	public QualifierEnum getQualifier();
}
