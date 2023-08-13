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
package net.sourceforge.nrl.parser.ast;

import net.sourceforge.nrl.parser.util.StringUtilities;

/**
 * Basic interface for all NRL constructs. Has a line and column number. To convert to a total
 * character position, use {@link StringUtilities#getCharacterPosition(String, int, int)}.
 * 
 * @author Christian Nentwich
 */
public interface INRLAstNode {

	/** Newline constant for output purposes */
	public final static String NEWLINE = System.getProperty("line.separator");

	/**
	 * Accept a visitor to this node.
	 * 
	 * @param visitor the visitor
	 */
	public void accept(INRLAstVisitor visitor);

	/**
	 * Return the column in the input file where this node occurred.
	 * 
	 * @return the column, 1-based
	 */
	public int getColumn();

	/**
	 * Return the line in the input file where this node occurred.
	 * 
	 * @return the line, 1-based
	 */
	public int getLine();

	/**
	 * Return a named user data object associated with this AST node.
	 * 
	 * @param key the key identifying the data
	 * @return the object or null if no such data
	 */
	public Object getUserData(String key);

	/**
	 * Dump the AST and its children for debugging purposes
	 * 
	 * @param indent the indentation count, 0 on first call
	 * 
	 * @return the AST as a string
	 * @deprecated This method will be removed in a future release - it hasn't been implemented
	 * consistently.
	 */
	@Deprecated
	public String dump(int indent);

	/**
	 * Associate user data with this AST node, under a given key.
	 * 
	 * @param key the key
	 * @param data the data
	 */
	public void setUserData(String key, Object data);
}
