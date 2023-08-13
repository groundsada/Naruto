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

import java.util.List;


/**
 * "is not in" returns true if a value is NOT in a
 * comma-separated list of identifiers.
 * <p>
 * Abstract syntax reference:
 * <code>IsInPredicate ::= Expression ISIN ListDefinition</code>
 * 
 * @author Christian Nentwich
 */
public interface IIsNotInPredicate extends IPredicate {

	/**
	 * Return the expression whose computed value must not be in the list. This
	 * never returns null.
	 * 
	 * @return the expression
	 */
	public IExpression getExpression();

	/**
	 * Return the list of IIdentifier in which the value must not be contained.
	 * 
	 * @return the list of identifiers, contains at least one entry.
	 */
	public List<IIdentifier> getList();
}
