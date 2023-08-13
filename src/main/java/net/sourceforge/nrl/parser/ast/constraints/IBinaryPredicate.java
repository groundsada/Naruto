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
 * A binary predicate compares two expressions for the purpose of returning a
 * Boolean result.
 * 
 * @author Christian Nentwich
 */
public interface IBinaryPredicate extends IPredicate {

	/** Available predicates */
	public enum Predicate {
		EQUAL, NOT_EQUAL, LESS, LESS_OR_EQUAL, GREATER, GREATER_OR_EQUAL
	};

	/**
	 * Return the left parameter. Never returns null.
	 * 
	 * @return the left parameter
	 */
	public IExpression getLeft();

	/**
	 * Return the right parameter. Never returns null.
	 * 
	 * @return the right parameter
	 */
	public IExpression getRight();

	/**
	 * Return the predicate, one of the enums defined in this interface.
	 * 
	 * @return the predicate
	 */
	public Predicate getPredicate();
}
