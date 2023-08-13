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

import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.constraints.ICollectionIndex;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;

public class CollectionIndexImpl extends ConstraintImpl implements ICollectionIndex {

	private int position = 0;

	public CollectionIndexImpl(Token token) throws RecognitionException {
		super(token);

		String text = token.getText();

		if (token.getType() == NRLActionParser.VT_FIRST_ORDINAL_NUMBER) {
			position = 1;
		} else if (text.compareToIgnoreCase("second") == 0) {
			position = 2;
		} else if (text.compareToIgnoreCase("third") == 0) {
			position = 3;
		} else {
			if (text.length() < 3) {
				// Should not really happen, but being defensive
				throw new RecognitionException();
			}
			try {
				position = Integer.parseInt(text.substring(0, text.length() - 2));
			} catch (NumberFormatException e) {
				throw new RecognitionException();
			}
		}

	}

	@Override
	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			getCollection().accept(visitor);
		}
		visitor.visitAfter(this);
	}

	public String dump(int indent) {
		return doIndent(indent) + getCollection().getOriginalString() + "["
				+ getPosition() + "]" + NEWLINE;
	}

	public IModelReference getCollection() {
		return (IModelReference) getChild(0);
	}

	public int getPosition() {
		return position;
	}

}
