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

import net.sourceforge.nrl.parser.ast.INRLAstNode;
import net.sourceforge.nrl.parser.ast.INRLAstVisitor;

/**
 * An abstract class that narrows the AST visitor interface a bit, to pass it an
 * {@link Antlr3NRLBaseAst}.
 * <p>
 * Subclasses need to implement {@link #visitBefore(Antlr3NRLBaseAst)}, and
 * {@link #visitAfter(Antlr3NRLBaseAst)} only if necessary.
 * 
 * @author Christian Nentwich
 */
public abstract class AntlrAstVisitor implements INRLAstVisitor {

	public final boolean visitBefore(INRLAstNode node) {
		return visitBefore((Antlr3NRLBaseAst) node);
	}

	public final void visitAfter(INRLAstNode node) {
		visitAfter((Antlr3NRLBaseAst) node);
	}

	protected abstract boolean visitBefore(Antlr3NRLBaseAst node);

	protected void visitAfter(Antlr3NRLBaseAst node) {
	}
}
