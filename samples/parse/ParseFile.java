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
package parse;

import java.io.FileNotFoundException;
import java.io.FileReader;

import net.sourceforge.nrl.parser.INRLParser;
import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.NRLParser;
import net.sourceforge.nrl.parser.ast.IDeclaration;
import net.sourceforge.nrl.parser.ast.IRuleFile;
import net.sourceforge.nrl.parser.ast.action.IActionRuleDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.IConstraintRuleDeclaration;

/*
 * A simple example showing how to use the parser to parse a rule file and dump the list of
 * declarations on the console.
 * 
 * This does not load the models or perform any type checking.
 */
public class ParseFile {

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: ParseFile nrlfile.nrl");
			System.exit(1);
		}

		INRLParser parser = new NRLParser();

		IRuleFile ruleFile = null;
		try {
			ruleFile = parser.parse(new FileReader(args[0]));
		} catch (FileNotFoundException e) {
			System.err.println("File not found.");
			System.exit(1);
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}

		if (!parser.getErrors().isEmpty()) {
			for (NRLError error : parser.getErrors()) {
				System.err.println("Error at line " + error.getLine() + ", column "
						+ error.getColumn() + ": " + error.getMessage());
			}
			System.exit(1);
		}
		
		for (IDeclaration decl : ruleFile.getDeclarations()) {
			if (decl instanceof IActionRuleDeclaration) {
				System.out.println("Action rule " + decl.getId());
			} else if (decl instanceof IConstraintRuleDeclaration) {
				System.out.println("Constraint rule " + decl.getId());
			}
		}
		
		System.exit(0);
	}
}
