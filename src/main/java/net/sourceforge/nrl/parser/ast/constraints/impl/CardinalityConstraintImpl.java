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
package net.sourceforge.nrl.parser.ast.constraints.impl;

import net.sourceforge.nrl.parser.ast.constraints.ICardinalityConstraint;
import net.sourceforge.nrl.parser.ast.constraints.IIntegerNumber;
import net.sourceforge.nrl.parser.ast.impl.Antlr3NRLBaseAst;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;

public class CardinalityConstraintImpl extends Antlr3NRLBaseAst implements ICardinalityConstraint {

	public CardinalityConstraintImpl() {
		super();
	}

	public CardinalityConstraintImpl(Token token) {
		super(token);
	}

	public String dump(int indent) {
		String result = doIndent(indent) + getNumber();
		switch (getQualifier()) {
		case AT_LEAST:
			result = result + "+";
			break;
		case AT_MOST:
			result = result + "-";
			break;
		}
		result = result + NEWLINE;
		return result;
	}

	@Override
	public int getLine() {
		return getChild(0).getLine();
	}

	@Override
	public int getColumn() {
		return getChild(0).getCharPositionInLine();
	}

	public int getNumber() {
		// Find out whether to use the first or second child (depending on
		// whether exactly/as least/at most are present
		Tree node = getChild(0);
		if (getChildCount() == 2)
			node = getChild(1);

		switch (node.getType()) {
		case NRLActionParser.NO:
			return 0;
		case NRLActionParser.ONE:
			return 1;
		case NRLActionParser.TWO:
			return 2;
		case NRLActionParser.THREE:
			return 3;
		case NRLActionParser.FOUR:
			return 4;
		}

		return ((IIntegerNumber) node).getNumberAsBigInteger().intValue();
	}

	public QualifierEnum getQualifier() {
		switch (getChild(0).getType()) {
		case NRLActionParser.AT_LEAST:
			return QualifierEnum.AT_LEAST;
		case NRLActionParser.AT_MOST:
			return QualifierEnum.AT_MOST;
		default:
			return QualifierEnum.EXACTLY;
		}
	}

}
