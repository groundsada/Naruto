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

import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.model.IModelElement;

/**
 * An expression that converts a type to a sub-type.
 * <p>
 * Abstract syntax reference:
 * <code>CastExpression ::= modelReference AS_A modelReference</code>
 * 
 * @author Christian Nentwich
 */
public interface ICastExpression extends IExpression {

	/**
	 * Return the source model reference to be cast.
	 * 
	 * @return the reference
	 */
	public IModelReference getReference();
	
	/**
	 * Return the type being cast to.
	 * 
	 * @return the type
	 */
	public IModelElement getTargetType();
}
