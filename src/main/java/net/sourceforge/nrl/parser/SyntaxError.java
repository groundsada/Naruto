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

/**
 * An error that occurred during parsing.
 * <p>
 * Lines are numbered from 1, not from 0. Columns are numbered from 0.
 * 
 * @author Christian Nentwich
 */
public class SyntaxError extends NRLError {

	public SyntaxError(int status, int line, int column, String message) {
		super(status, line, column, 1, message);
	}

	public SyntaxError(int status, int line, int column, int length, String message) {
		super(status, line, column, length, message);
	}

	public String toString() {
		return "Syntax error, Line " + getLine() + ", Column " + getColumn() + ": " + getMessage();
	}
}
