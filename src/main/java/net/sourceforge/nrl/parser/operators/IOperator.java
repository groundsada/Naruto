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
 * An IOperator is a scripted, programmed or otherwise externally defined extension that
 * can be dynamically referred to in rules.
 * <p>
 * As a minimum, an operator needs to have a name. It does not necessarily have to have an
 * implementation associated with it.
 * 
 * @author Christian Nentwich
 */
public interface IOperator {

	/**
	 * Get the documentation string attached to this operator, if any.
	 * 
	 * @return the documentation or null
	 */
	public String getDocumentation();

	/**
	 * Get an implementation detail associated with the operator, given its label. This is
	 * used by mappings to targets like Java or rule engines to obtain supplementary
	 * mapping information.
	 * <p>
	 * See {@link IImplementationDetail} for more information.
	 * 
	 * @param label the label
	 * @return the detail value or null if not found
	 */
	public String getImplementationDetail(String label);

	/**
	 * Return all implementation details associated with the operator. This is used for
	 * mappings, see {@link IImplementationDetail} for more information.
	 * <p>
	 * Note that this does not return the implementation details associated with
	 * parameters of the operators - those have to be read off the individual parameters.
	 * 
	 * @return a collection of {@link IImplementationDetail} objects
	 */
	public Collection<IImplementationDetail> getImplementationDetails();

	/**
	 * Return the operator name.
	 * 
	 * @return the operator name
	 */
	public String getName();

	/**
	 * Return a parameter by name
	 * 
	 * @param name parameter name
	 * @return the parameter or null if not found
	 */
	public IParameter getParameter(String name);

	/**
	 * Return a list of {@link IParameter} objects.
	 * 
	 * @return the parameters expected by the operator
	 */
	public List<IParameter> getParameters();

	/**
	 * Return the purpose of this operator. The use of this is entirely
	 * implementation-dependent.
	 * 
	 * @return the purpose
	 */
	public String getPurpose();

	/**
	 * Return the name of the return type as found in the definition
	 * 
	 * @return the name
	 */
	public String getReturnTypeName();

	/**
	 * Return the return type of the operator. You must call
	 * {@link #resolveModelReferences(IModelCollection)} before this.
	 * 
	 * @return the return type or null if not specified.
	 */
	public IModelElement getReturnType();

	/**
	 * Returns the return type of the operator. If it is not defined, this returns
	 * {@link net.sourceforge.nrl.parser.ast.NRLDataType#UNKNOWN}.
	 * 
	 * @return the return type
	 */
	public NRLDataType getNRLReturnType();

	/**
	 * Resolve all parameter type and return type references against models.
	 * 
	 * @param models the models
	 * @return any errors encountered
	 */
	public List<NRLError> resolveModelReferences(IModelCollection models);

	/**
	 * Set the type this operator will return.
	 * 
	 * @param returnType the type
	 */
	public void setReturnType(IModelElement returnType);

	/**
	 * Set the raw name of the return type.
	 * 
	 * @param name the name
	 */
	public void setReturnTypeName(String name);

	/**
	 * Set the NRL type of the return type
	 * 
	 * @param type the type
	 */
	public void setNRLReturnType(NRLDataType type);
}
