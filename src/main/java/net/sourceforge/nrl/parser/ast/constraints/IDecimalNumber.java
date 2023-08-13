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
package net.sourceforge.nrl.parser.ast.constraints;

import java.math.BigDecimal;

/**
 * A decimal (floating point) number that occurred as a literal in an expression.
 */
public interface IDecimalNumber extends IIdentifier {

	/**
	 * Return the number.
	 * 
	 * @deprecated parsing a decimal out as a <code>double</code> can cause a loss of precision, the
	 * parser should maintain data integrity. Use {@link #getNumberAsBigDecimal()}.
	 * 
	 * @return the number
	 */
	@Deprecated
	public double getNumber();

	/**
	 * Return the number as a <code>{@link BigDecimal}</code>. This ensures there is no loss of
	 * precision when parsing decimal numbers.
	 * 
	 * @return the number
	 * @since 1.4.9
	 */
	public BigDecimal getNumberAsBigDecimal();

}
