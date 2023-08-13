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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.SemanticError;
import net.sourceforge.nrl.parser.ast.NRLDataType;
import net.sourceforge.nrl.parser.ast.impl.ModelReferenceHelper;
import net.sourceforge.nrl.parser.model.IDataType;
import net.sourceforge.nrl.parser.model.IModelCollection;
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.model.PrimitiveTypeFactory;

/**
 * A basic implementation of an operator. This supports listener notification if any
 * fields are set.
 * 
 * @author Christian Nentwich
 */
public class Operator extends PropertyAwareObject implements IOperator {

	private static final long serialVersionUID = 5232299559789647958L;

	/**
	 * Event - documentation changed.
	 */
	public final static String DOCUMENTATION_CHANGED = "OperatorDocumentationChanged";

	/**
	 * Event - name changed.
	 */
	public final static String NAME_CHANGED = "OperatorNameChanged";

	/**
	 * Event - implementation detail list changed.
	 */
	public final static String IMPLEMENTATION_DETAILS_CHANGED = "OperatorImplementationDetailsChanged";

	/**
	 * Event - parameter list changed.
	 */
	public final static String PARAMETERS_CHANGED = "OperatorParametersChanged";

	/**
	 * Event - purpose attribute changed.
	 */
	public final static String PURPOSE_CHANGED = "OperatorPurposeChanged";

	/**
	 * Event - return type changed.
	 */
	public final static String RETURN_TYPE_CHANGED = "OperatorReturnTypeChanged";

	/**
	 * Event - return type name changed.
	 */
	public final static String RETURN_TYPE_NAME_CHANGED = "OperatorReturnTypeNameChanged";

	private NRLDataType nrlReturnType = NRLDataType.UNKNOWN;

	private String name;

	private List<IParameter> parameters = new ArrayList<IParameter>();

	private String documentation;

	private Map<String, IImplementationDetail> implementationDetails = new HashMap<String, IImplementationDetail>();

	private IModelElement returnType = null;

	private String returnTypeName = null;

	private String purpose = null;

	public void addImplementationDetail(IImplementationDetail detail) {
		implementationDetails.put(detail.getLabel(), detail);
		firePropertyChange(IMPLEMENTATION_DETAILS_CHANGED, null, detail);
	}

	/**
	 * Add a parameter to the end of the list.
	 * 
	 * @param param the parameter to add
	 */
	public void addParameter(IParameter param) {
		parameters.add(param);
		firePropertyChange(PARAMETERS_CHANGED, null, param);
	}

	/**
	 * Add a parameter before another one.
	 * 
	 * @param param the parameter to add
	 * @param before the parameter before which to insert. If this is null, or the before
	 * parameter is not in the list, "param" is inserted at the front.
	 */
	public void addParameter(IParameter param, IParameter before) {
		if (before == null) {
			parameters.add(0, param);
		} else {
			int index = parameters.indexOf(before);
			if (index != -1)
				parameters.add(index, param);
			else
				parameters.add(0, param);
		}
		firePropertyChange(PARAMETERS_CHANGED, null, param);
	}

	public void clearImplementationDetail(String label) {
		IImplementationDetail removed = implementationDetails.remove(label);
		firePropertyChange(IMPLEMENTATION_DETAILS_CHANGED, removed, null);
	}

	public void clearParameters() {
		parameters.clear();
		firePropertyChange(PARAMETERS_CHANGED, parameters, null);
	}

	public String getDocumentation() {
		return documentation;
	}

	public String getImplementationDetail(String label) {
		IImplementationDetail detail = (IImplementationDetail) implementationDetails.get(label);
		if (detail != null)
			return detail.getValue();
		return null;
	}

	public Collection<IImplementationDetail> getImplementationDetails() {
		return implementationDetails.values();
	}

	public String getName() {
		return name;
	}

	public NRLDataType getNRLReturnType() {
		return nrlReturnType;
	}

	public IParameter getParameter(String name) {
		for (IParameter param : getParameters()) {
			if (param.getName().equals(name))
				return param;
		}
		return null;
	}

	public List<IParameter> getParameters() {
		return parameters;
	}

	public String getPurpose() {
		return purpose;
	}

	public IModelElement getReturnType() {
		return returnType;
	}

	public String getReturnTypeName() {
		return returnTypeName;
	}

	public void removeParameter(IParameter param) {
		parameters.remove(param);
		firePropertyChange(PARAMETERS_CHANGED, param, null);
	}

	public List<NRLError> resolveModelReferences(IModelCollection models) {
		List<NRLError> errors = new ArrayList<NRLError>();

		if (getReturnTypeName() != null) {
			PrimitiveTypeFactory factory = PrimitiveTypeFactory.getInstance();
			IDataType type = factory.getType(getReturnTypeName());
			if (type != null) {
				setReturnType(type);
				setNRLReturnType(factory.getNrlType(getReturnTypeName()));
			} else {
				IModelElement resolvedType = ModelReferenceHelper.getModelElement(
						getReturnTypeName(), null, models, new ArrayList<NRLError>());

				if (resolvedType == null) {
					errors.add(new SemanticError("Cannot find return type of \"" + getName()
							+ "\" in models: " + getReturnTypeName()
							+ ". Did you reference the right models?"));
				} else {
					setReturnType(resolvedType);
				}
			}
		}

		for (IParameter param : getParameters()) {
			errors.addAll(param.resolveModelReferences(models));
		}

		return errors;
	}

	public void setDocumentation(String documentation) {
		String old = this.documentation;
		this.documentation = documentation;
		firePropertyChange(DOCUMENTATION_CHANGED, old, documentation);
	}

	public void setName(String name) {
		String old = this.name;
		this.name = name;
		firePropertyChange(NAME_CHANGED, old, name);
	}

	public void setNRLReturnType(NRLDataType returnType) {
		this.nrlReturnType = returnType;
	}

	public void setPurpose(String purpose) {
		String old = this.purpose;
		this.purpose = purpose;
		firePropertyChange(PURPOSE_CHANGED, old, purpose);
	}

	public void setReturnType(IModelElement returnType) {
		IModelElement old = this.returnType;
		this.returnType = returnType;
		firePropertyChange(RETURN_TYPE_CHANGED, old, returnType);
	}

	public void setReturnTypeName(String returnTypeName) {
		String old = this.returnTypeName;
		this.returnTypeName = returnTypeName;
		firePropertyChange(RETURN_TYPE_NAME_CHANGED, old, returnTypeName);
	}
}
