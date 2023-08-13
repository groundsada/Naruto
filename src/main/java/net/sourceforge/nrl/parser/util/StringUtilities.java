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
package net.sourceforge.nrl.parser.util;

import java.util.StringTokenizer;

/**
 * Generic utilities to help with string processing tasks that occur with NRL.
 */
public class StringUtilities {

	/**
	 * Convert a line and column in a document to a full character offset.
	 * 
	 * @param document the document
	 * @param line the line (1-based !)
	 * @param column the column (0-based)
	 * @return the character offset, capped at 0 or document.length()-1 to keep it in
	 * range
	 */
	public static int getCharacterPosition(String document, int line, int column) {
		if (line < 1)
			return 0;
		if (document.length() == 0)
			return 0;

		StringTokenizer tokenizer = new StringTokenizer(document, "\n");

		int currentLine = 1;
		int currentOffset = 0;

		while (tokenizer.hasMoreTokens() && currentLine < line) {
			String token = tokenizer.nextToken();
			currentOffset += token.length() + 1;
			currentLine++;
		}

		if (column < 0)
			return currentOffset;

		if (column >= 0 && currentLine == line) {
			if (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();

				if (column <= token.length())
					currentOffset += column;
				else if (column == token.length() + 1)
					currentOffset += column - 1;
				else
					currentOffset += token.length() - 1;
			}
		} else {
			if (document.length() == 0)
				return 0;
			return document.length() - 1;
		}

		if (currentOffset < document.length())
			return currentOffset;
		return document.length() - 1;
	}
}
