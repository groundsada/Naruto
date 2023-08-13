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
package net.sourceforge.nrl.parser;

import net.sourceforge.nrl.parser.util.StringUtilities;

/**
 * An abstract error that contains line, column and character index info, as well as a
 * status code.
 * <p>
 * This acts as a superclass for {@link net.sourceforge.nrl.parser.SyntaxError} and
 * {@link net.sourceforge.nrl.parser.SemanticError}.
 * <p>
 * Note that lines are numbered from 1 and columns from 0. If you require a full character
 * offset for anything, use {@link StringUtilities#getCharacterPosition(String, int, int)}.
 * 
 * @author Christian Nentwich
 */
public abstract class NRLError {

	private int statusCode;

	private int line = 1;

	private int column = 0;

	private int length = 0;

	private String message;

	public NRLError(int statusCode, int line, int column, int length, String message) {
		this.line = line;
		this.column = column;
		this.length = length;
		this.message = message;
		this.statusCode = statusCode;
	}

	/**
	 * Return the column where the error occurred, or -1 if not available.
	 * 
	 * @return the column or -1
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Return the length of the input text connected to the error, or 0 if not available.
	 * 
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Return the line where the error occurred. This is 1-based.
	 * 
	 * @return the line
	 */
	public int getLine() {
		return line;
	}

	/**
	 * Return the error message
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Return the status code, one of the constants in the
	 * {@link net.sourceforge.nrl.parser.IStatusCode} class.
	 * 
	 * @return the status code
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * Set the column where the error occurred. 0 based.
	 * 
	 * @param column the column
	 */
	public void setColumn(int column) {
		this.column = column;
	}

	/**
	 * Set the length of the error
	 * 
	 * @param length the length
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * Set the line where the error occurred
	 * 
	 * @param line the line
	 */
	public void setLine(int line) {
		this.line = line;
	}

	/**
	 * Set the error message
	 * 
	 * @param message the message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
