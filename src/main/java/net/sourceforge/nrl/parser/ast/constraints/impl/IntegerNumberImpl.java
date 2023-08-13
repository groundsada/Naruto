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

import java.math.BigInteger;

import net.sourceforge.nrl.parser.ast.constraints.IIntegerNumber;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.antlr.runtime.Token;

public class IntegerNumberImpl extends ConstraintImpl implements IIntegerNumber {

	private BigInteger number = null;

	public IntegerNumberImpl(Token token) {
		super(token);
	}

	public String dump(int indent) {
		return doIndent(indent) + getNumber() + NEWLINE;
	}

	public int getNumber() {
		if (number == null) {
			number = parseNumber();
		}
		return number.intValue();
	}
	
	public BigInteger getNumberAsBigInteger() {
		if (number == null) {
			number = parseNumber();
		}
		return number;
	}
	
	/*
	 * Initialise the number, which will be stored here or as a child node,
	 * depending on whether it was negated.
	 */
	private BigInteger parseNumber() {
		BigInteger number = null;
		if (getToken().getType() == NRLActionParser.VT_NEGATE_INTEGER) {
			number = new BigInteger(getChild(0).getText());
			number = number.negate();
		} else {
			number = new BigInteger(getText());
		}
		return number;
	}

}
