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
import java.util.List;

import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.model.IModelCollection;

/**
 * A basic implementation of an operator collection.
 * 
 * @author Christian Nentwich
 */
public class Operators extends PropertyAwareObject implements IOperators {

	/**
	 * Event - documentation changed.
	 */
	public final static String DOCUMENTATION_CHANGED = "OperatorsDocumentationChanged";

	/**
	 * Event - model file name list changed.
	 */
	public final static String MODEL_FILE_NAMES_CHANGED = "OperatorsModelFileNamesChanged";

	/**
	 * Event - operator list changed.
	 */
	public final static String OPERATOR_LIST_CHANGED = "OperatorsListChanged";

	private static final long serialVersionUID = 4131906482398148668L;

	private List<IOperator> operators = new ArrayList<IOperator>();

	private List<String> modelFileNames = new ArrayList<String>();

	private String documentation;

	private LoadedVersion loadedVersion;
	
	public void addModelFileName(String fileName) {
		modelFileNames.add(fileName);
		firePropertyChange(MODEL_FILE_NAMES_CHANGED, null, fileName);
	}

	/**
	 * Add an operator to the end of the list.
	 * 
	 * @param operator the operator
	 */
	public void addOperator(IOperator operator) {
		operators.add(operator);
		firePropertyChange(OPERATOR_LIST_CHANGED, null, operator);
	}

	/**
	 * Add an operator before another one
	 * 
	 * @param operator operator to add
	 * @param before operator to add before. If this is null or not in the list, then
	 * "operator" is added at the front
	 */
	public void addOperator(IOperator operator, IOperator before) {
		if (before == null) {
			operators.add(0, operator);
		} else {
			int index = operators.indexOf(before);
			if (index != -1)
				operators.add(index, operator);
			else
				operators.add(0, operator);
		}
		firePropertyChange(OPERATOR_LIST_CHANGED, null, operator);
	}

	public void clear() {
		operators.clear();
		modelFileNames.clear();
	}

	public String getDocumentation() {
		return documentation;
	}

	public List<String> getModelFileNames() {
		return modelFileNames;
	}

	public IOperator getOperator(String name) {
		for (IOperator op : operators) {
			if (op.getName().toLowerCase().equals(name.toLowerCase()))
				return op;
		}
		return null;
	}

	public List<IOperator> getOperators() {
		return operators;
	}

	public void removeModelFileName(String fileName) {
		modelFileNames.remove(fileName);
		firePropertyChange(MODEL_FILE_NAMES_CHANGED, fileName, null);
	}

	public void removeOperator(IOperator operator) {
		operators.remove(operator);
		firePropertyChange(OPERATOR_LIST_CHANGED, operator, null);
	}

	public List<NRLError> resolveModelReferences(IModelCollection models) {
		List<NRLError> errors = new ArrayList<NRLError>();
		for (IOperator op : getOperators()) {
			errors.addAll(op.resolveModelReferences(models));
		}
		return errors;
	}

	public void setDocumentation(String documentation) {
		String old = this.documentation;
		this.documentation = documentation;
		firePropertyChange(DOCUMENTATION_CHANGED, old, documentation);
	}

	public LoadedVersion getLoadedVersion() {
		return loadedVersion;
	}
	
	public void setLoadedVersion(LoadedVersion loadedVersion){
		this.loadedVersion = loadedVersion;
	}
}
