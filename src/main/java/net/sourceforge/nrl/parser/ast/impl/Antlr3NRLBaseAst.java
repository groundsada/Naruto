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
package net.sourceforge.nrl.parser.ast.impl;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.nrl.parser.ast.INRLAstNode;
import net.sourceforge.nrl.parser.ast.INRLAstVisitor;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonErrorNode;
import org.antlr.runtime.tree.CommonTree;

/**
 * The basic AST node, extends the ANTLR 3 AST.
 * 
 * @author Christian Nentwich
 */
public class Antlr3NRLBaseAst extends CommonTree implements INRLAstNode {

	// token line
	private int line = -1;

	// token column
	private int column = -1;

	// User data map (string->object)
	private Map<String, Object> userData = new HashMap<String, Object>();
	
	public Antlr3NRLBaseAst() {
	}

	public Antlr3NRLBaseAst(Token token) {
		super(token);

		line = token.getLine();
		column = token.getCharPositionInLine();
	}

	public void accept(INRLAstVisitor visitor) {
		visitor.visitBefore(this);
		visitor.visitAfter(this);
	}

	/**
	 * Return an indentation of the given size.
	 * 
	 * @param indent the indentation
	 * @return a string of spaces
	 */
	protected String doIndent(int indent) {
		String result = "";
		for (int i = 0; i < indent * 4; i++)
			result += " ";
		return result;
	}

	public String dump(int indent) {
		return doIndent(indent) + "unknown";
	}

	public int getColumn() {
		return column;
	}

	public int getLine() {
		return line;
	}

	public Object getUserData(String key) {
		return userData.get(key);
	}

	public void initialisePositionFromChild(int childPos) {
		if (getChild(0) instanceof CommonErrorNode)
			return;
		setLine(getChild(0).getLine());
		setColumn(getChild(0).getCharPositionInLine());
	}
	
	public void setLine(int line) {
		this.line = line;
	}
	
	public void setColumn(int column) {
		this.column = column;
	}
	
	public void setUserData(String key, Object data) {
		userData.put(key, data);		
	}
}
