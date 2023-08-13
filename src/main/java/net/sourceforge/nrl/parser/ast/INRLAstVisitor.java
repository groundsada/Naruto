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
package net.sourceforge.nrl.parser.ast;

import net.sourceforge.nrl.parser.ast.action.INRLActionDetailVisitor;
import net.sourceforge.nrl.parser.ast.constraints.INRLConstraintDetailVisitor;

/**
 * A very simple visitor interface for straight-forward recursive descent
 * traversals of the AST. This is not really suitable for complicated mapping,
 * but it is useful for gathering rule IDs and so on.
 * <p>
 * There are more detailed visitor classes available, with a call-back for every
 * single AST constructs:
 * <ul>
 * <li>{@link INRLConstraintDetailVisitor} for constraints and
 * <li>{@link INRLActionDetailVisitor} for actions
 * </ul>
 * 
 * @author Christian Nentwich
 */
public interface INRLAstVisitor {

	/**
	 * Visit an AST node and, if this method returns true, visit its children in
	 * order.
	 * 
	 * @param node the AST node
	 * @return true if the children should be visited
	 */
	public boolean visitBefore(INRLAstNode node);

	/**
	 * Visit an AST node after its children have been processed. This is always
	 * called <b>after</b> {@link #visitBefore(INRLAstNode)} by the AST.
	 * 
	 * @param node the node to visit
	 */
	public void visitAfter(INRLAstNode node);
}
