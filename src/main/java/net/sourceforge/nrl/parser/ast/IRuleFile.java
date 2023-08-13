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

import java.util.List;

import net.sourceforge.nrl.parser.ast.constraints.IConstraintRuleDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.IDecimalNumber;
import net.sourceforge.nrl.parser.ast.constraints.IIntegerNumber;
import net.sourceforge.nrl.parser.ast.constraints.ILiteralString;
import net.sourceforge.nrl.parser.ast.constraints.IValidationFragmentDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.IVariableDeclaration;

/**
 * IRuleFile is the top level interface - it contains the entire, fully resolved
 * AST of a rule file.
 * 
 * @author Christian Nentwich
 */
public interface IRuleFile extends INRLAstNode {

	/**
	 * Return the model file references, as they were declared in this rule file.
	 * 
	 * @return the file references
	 */
	public IModelFileReference[] getModelFileReferences();
	
	/**
	 * Return an array of operator file references - can be empty.
	 * 
	 * @return the file references
	 */
	public IOperatorFileReference[] getOperatorFileReferences();

	/**
	 * Return a collection of the declarations in this file. This is a
	 * <b>heterogeneous</b> collection. It contains both
	 * {@link IConstraintRuleDeclaration} and {@link IValidationFragmentDeclaration}
	 * objects.
	 * <p>
	 * Clients calling this method should be defensive and check each objects to
	 * ensure it is handled correctly.
	 * 
	 * @return the collection of declarations
	 */
	public List<IDeclaration> getDeclarations();

	/**
	 * Look up a rule declaration by ID.
	 * 
	 * @param id the rule id
	 * @return the rule or null if not found
	 */
	public IRuleDeclaration getRuleById(String id);

	/**
	 * Return a list of all rule set declarations. This is a list of
	 * {@link IRuleSetDeclaration} objects.
	 * 
	 * @return the collection of declarations
	 */
	public List<IRuleSetDeclaration> getRuleSetDeclarations();

	/**
	 * Get the list of global variable declarations. This list is a collection
	 * of {@link IVariableDeclaration} objects with some special properties:
	 * <ul>
	 * <li>{@link IVariableDeclaration#getExpression()} is always an
	 * {@link ILiteralString}, {@link IDecimalNumber} or {@link IIntegerNumber}, 
	 * but <b>never</b> a model reference.
	 * <li>{@link IVariableDeclaration#getConstraint()} is always null
	 * </ul>
	 */
	public List<IVariableDeclaration> getGlobalVariableDeclarations();
}
