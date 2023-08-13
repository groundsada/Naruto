package net.sourceforge.nrl.parser.ast.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.ISingleContextDeclaration;
import net.sourceforge.nrl.parser.model.IModelElement;

import org.antlr.runtime.Token;

public abstract class SingleContextDeclarationImpl extends DeclarationImpl
		implements ISingleContextDeclaration {

	// The context - must be set by setContext by a resolver
	private IModelElement context;
	
	private List<String> additionalParameterNames;

	private Map<String, IModelReference> additionalParameterReferences;
	
	private Map<String, IModelElement> additionalParameterTypes = new HashMap<String, IModelElement>();

	public SingleContextDeclarationImpl(Token token) {
		super(token);
	}

	public boolean areAdditionalParametersFullyResolved() {
		if (additionalParameterNames == null) {
			initialiseAdditionalParametersFromAst();
		}
		
		if (additionalParameterTypes.size() != additionalParameterNames.size())
			return false;
		for (IModelElement element : additionalParameterTypes.values()) {
			if (element == null)
				return false;
		}
		return true;
	}
	
	/*
	 * Initialise the name and model reference lists from the AST
	 */
	protected void initialiseAdditionalParametersFromAst() {
		additionalParameterNames = new ArrayList<String>();
		additionalParameterReferences = new HashMap<String, IModelReference>();

		List<?> children = getChildren();
		for (Object childObject : children) {
			if (!(childObject instanceof NamedParameterImpl))
				continue;
			
			NamedParameterImpl child = (NamedParameterImpl) childObject;
			additionalParameterNames.add(child.getName());
			additionalParameterReferences.put(child.getName(), child.getReference());
		}
	}

	public List<String> getAdditionalParameterNames() {
		if (additionalParameterNames == null) {
			initialiseAdditionalParametersFromAst();
		}
		return additionalParameterNames;
	}

	public IModelElement getAdditionalParameterType(String parameterName) {		
		return additionalParameterTypes.get(parameterName);
	}

	public IModelReference getAdditionalParameterTypeReference(String parameterName) {
		if (additionalParameterNames == null) {
			initialiseAdditionalParametersFromAst();
		}
		return additionalParameterReferences.get(parameterName);
	}	
	
	public IModelElement getContext() {
		return context;
	}

	public IModelReference getModelReference() {
		if (isOldContextFormat())
			return (IModelReference) getChild(0);
		return (IModelReference) getChild(1);
	}

	/*
	 * Return true if the rule starts with the old format, "Context: ctx Validation Rule ...".
	 * Return false if it starts with the new format, "Validation Rule "x" applies to ..."
	 */
	protected boolean isOldContextFormat() {
		return getChild(0) instanceof IModelReference;
	}

	public void setAdditionalParameterType(String parameterName, IModelElement type) {
		additionalParameterTypes.put(parameterName, type);
	}
	
	public void setContext(IModelElement context) {
		this.context = context;
	}
}
