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
package net.sourceforge.nrl.parser.ast.action;

import java.util.List;

import net.sourceforge.nrl.parser.ast.constraints.IExpression;
import net.sourceforge.nrl.parser.operators.IOperator;

/**
 * An action that invokes an externally defined operator. This follows the
 * syntax <code>[opname]</code> or <code>[opname] parameter...</code> in the
 * rule file.
 * <P>
 * <b>TODO</b> this is currently missing operator resolution against definition
 * files.
 * 
 * @author Christian Nentwich
 */
public interface IOperatorAction extends ISimpleAction {

	/**
	 * Return the operator. This only returns a value once the AST has been
	 * resolved against an operator definition file, using the
	 * {@link net.sourceforge.nrl.parser.INRLParser#resolveOperatorReferences(net.sourceforge.nrl.parser.ast.IRuleFile, 
	 * net.sourceforge.nrl.parser.operators.IOperators[])}
	 * method.
	 * 
	 * @return the operator
	 */
	public IOperator getOperator();

	/**
	 * Return the operator name of the operator being called.
	 * 
	 * @return the name
	 */
	public String getOperatorName();

	/**
	 * Return the list of parameters, which are
	 * {@link net.sourceforge.nrl.parser.ast.constraints.IExpression} objects.
	 * 
	 * @return the parameters
	 */
	public List<IExpression> getParameters();
}
