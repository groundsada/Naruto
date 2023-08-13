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
package net.sourceforge.nrl.parser.operators;

import java.util.Collection;
import java.util.List;

import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.ast.NRLDataType;
import net.sourceforge.nrl.parser.model.IModelCollection;
import net.sourceforge.nrl.parser.model.IModelElement;

/**
 * A definition parameter for an operator.
 * 
 * @author Christian Nentwich
 */
public interface IParameter {

	/**
	 * Return the parameter name. This never returns null.
	 * 
	 * @return the name
	 */
	public String getName();

	/**
	 * Return the parameter type. This can be null if the type was not
	 * specified. It will also return null before
	 * {@link #resolveModelReferences(IModelCollection)} is called.
	 * 
	 * @return the type
	 */
	public IModelElement getType();

	/**
	 * Return the NRL type of the parameter. If it is not defined, this returns
	 * {@link net.sourceforge.nrl.parser.ast.NRLDataType#UNKNOWN}.
	 * 
	 * @return the type
	 */
	public NRLDataType getNRLDataType();

	/**
	 * Get an implementation detail associated with the parameter, given its
	 * label. This is used by mappings to targets like Java or rule engines to
	 * obtain supplementary mapping information.
	 * <p>
	 * See {@link IImplementationDetail} for more information.
	 * 
	 * @param label the label
	 * @return the detail value or null if not found
	 */
	public String getImplementationDetail(String label);

	/**
	 * Return all implementation details associated with the parameter. This is
	 * used for mappings, see {@link IImplementationDetail} for more
	 * information.
	 * 
	 * @return a collection of {@link IImplementationDetail} objects
	 */
	public Collection<IImplementationDetail> getImplementationDetails();

	/**
	 * Return the type name as shown in the definition
	 * 
	 * @return the name
	 */
	public String getTypeName();

	/**
	 * Return true if the parameter type is a collection. <b>Note:</b>
	 * Currently always returns false, included for future use.
	 * 
	 * @return collection indicator
	 */
	public boolean isTypeCollection();

	/**
	 * Set the NRL type of the parameter. Mainly used by the type checker.
	 * 
	 * @param type the new type
	 */
	public void setNRLDataType(NRLDataType type);

	/**
	 * Set the model type of the parameter.
	 * 
	 * @param type the new type
	 */
	public void setType(IModelElement type);

	/**
	 * Set the type name
	 * 
	 * @param name the name
	 */
	public void setTypeName(String name);

	/**
	 * Set a flag indicating whether the type is a collection type. <b>Note:</b>
	 * Currently ignored, included for future use.
	 * 
	 * @param isCollection true if the type is a collection
	 */
	public void setTypeIsCollection(boolean isCollection);

	/**
	 * Resolve all parameter type and return type references against models.
	 * 
	 * @param models the models
	 * @return any errors encountered
	 */
	public List<NRLError> resolveModelReferences(IModelCollection models);

}
