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
package net.sourceforge.nrl.parser.ast.constraints.impl;

import java.math.BigDecimal;

import net.sourceforge.nrl.parser.ast.constraints.IDecimalNumber;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.antlr.runtime.Token;

public class DecimalNumberImpl extends ConstraintImpl implements IDecimalNumber {

	private BigDecimal number = null;
	
	public DecimalNumberImpl(Token token) {
		super(token);
	}

	public String dump(int indent) {
		return doIndent(indent) + getNumber() + NEWLINE;
	}

	public double getNumber() {
		if (number == null) {
			number = parseNumber();
		}
		return number.doubleValue();
	}
	
	public BigDecimal getNumberAsBigDecimal() {
		if (number == null) {
			number = parseNumber();
		}
		return number;
	}
	
	/*
	 * Initialise the number, which will be stored here or as a child node,
	 * depending on whether it was negated.
	 */
	private BigDecimal parseNumber() {
		BigDecimal number = null;
		if (getToken().getType() == NRLActionParser.VT_NEGATE_DECIMAL) {
			number = new BigDecimal(getChild(0).getText());
			number = number.negate();
		} else {
			number = new BigDecimal(getText());
		}
		return number;
	}
}
