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
 * A semantic error that occurred during AST creation or AST resolution. Always has a message and a
 * line number. Can optionally return a column number and length in the input text related to the
 * error (or line 1 column 0 if this information is not available).
 * <p>
 * Lines are numbered from 1, not from 0. Columns are numbered from 0.
 * 
 * @author Christian Nentwich
 */
public class SemanticError extends NRLError {

	public SemanticError(int statusCode, int line, int column, String message) {
		super(statusCode, line, column, 1, message);
	}

	public SemanticError(int statusCode, int line, int column, int length, String message) {
		super(statusCode, line, column, length, message);
	}

	public SemanticError(String message) {
		super(0, 1, 0, 1, message);
	}

	public String toString() {
		return "Semantic error " + getStatusCode() + ": Line " + getLine() + ", Column "
				+ getColumn() + ": " + getMessage();
	}
}
