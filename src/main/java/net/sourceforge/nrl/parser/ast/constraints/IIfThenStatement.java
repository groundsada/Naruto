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
 * An if-then statement contains a boolean if clause, a then clause and an
 * optional else clause.
 * <p>
 * Abstract syntax reference:
 * <code>IfThenStatement ::= if:BinaryOperatorStatement then:IfThenStatement (else:IfThenStatement)?</code>
 * 
 * @author Christian Nentwich
 */
public interface IIfThenStatement extends IConstraint {

	/**
	 * Return the if clause. Never returns null.
	 * 
	 * @return the if clause
	 */
	public IConstraint getIf();

	/**
	 * Return the then clause. Never returns null.
	 * 
	 * @return the then clause
	 */
	public IConstraint getThen();

	/**
	 * Return the else clause. Will return null if there is no else clause
	 * attached.
	 * 
	 * @return the else clause or null
	 */
	public IConstraint getElse();
}
