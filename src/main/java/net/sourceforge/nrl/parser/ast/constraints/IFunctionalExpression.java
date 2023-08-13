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
package net.sourceforge.nrl.parser.ast.constraints;

import java.util.List;

/**
 * An expression that uses a function to compute a result.
 * <p>
 * Abstract syntax reference:
 * <code>FunctionalExpression ::= sumOf ModelReference | numberOf ModelReference (uniqueby ModelReference)?</code>
 * 
 * @author Christian Nentwich
 */
public interface IFunctionalExpression extends IExpression {

	/** Available functions */
	public enum Function {
		SUMOF, NUMBER_OF
	}

	/**
	 * Return the function identifier, one of the constants defined in this
	 * interface.
	 * 
	 * @return the function identifier
	 */
	public Function getFunction();

	/**
	 * Get the parameters. The number and position of the parameters depends on
	 * the function id, see the documentation of the ID for more details.
	 * 
	 * @return the parameters, all {@link IIdentifier} objects
	 */
	public List<IIdentifier> getParameters();
}
