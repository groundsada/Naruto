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
package net.sourceforge.nrl.parser.type;

import java.util.List;

import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.ast.IRuleFile;
import net.sourceforge.nrl.parser.operators.IOperators;

/**
 * A type checker traverses an AST and assigns a data type to every node. After
 * the traversal is complete,
 * {@link net.sourceforge.nrl.parser.ast.constraints.IConstraint#getNRLDataType()} for a
 * constraint will return its type.
 * <p>
 * The traveral method also returns a collection
 * {@link net.sourceforge.nrl.parser.SemanticError} objects in case of any type
 * checking errors.
 * <p>
 * Use:
 * <ul>
 * <li>{@link #check(IOperators)} to check operator files - this should be done
 * before checking the rule files the operators are used in
 * <li>{@link #check(IRuleFile)} to check rule files
 * </ul>
 * <p>
 * <b>CAUTION:</b> The checker relies on the AST being in absolute order. If
 * any resolver or model resolver errors occurred previously, the type checker
 * must not be used.
 * 
 * @author Christian Nentwich
 */
public interface ITypeChecker {

	/**
	 * Add a type mapping for the checker to map model types to abstract
	 * internal types.
	 * 
	 * @param mapping the mapping
	 */
	public void addTypeMapping(ITypeMapping mapping);

	/**
	 * Type check a rule file and assign a type to all constraints.
	 * 
	 * @param ruleFile the rule file to check
	 * @return a collection of error objects, can be empty
	 */
	public List<NRLError> check(IRuleFile ruleFile);

	/**
	 * Type check an operator collection. This assigns types to all parameters.
	 * 
	 * @param operators the operators to check
	 * @return a collection of errors
	 */
	public List<NRLError> check(IOperators operators);
}
