package net.sourceforge.nrl.parser.ast;

import java.util.List;

import net.sourceforge.nrl.parser.model.IModelElement;

/**
 * A SingleContextDeclaration is a declaration with a unique context, for example a validation or
 * action rule. The declaration can still hold auxiliary parameters, however the rule only applies
 * if the main context is present.
 * 
 * @author Christian Nentwich
 */
public interface ISingleContextDeclaration extends IDeclaration {

	/**
	 * Get the referenced model element
	 * 
	 * @return the element
	 */
	public IModelElement getContext();
	
	/**
	 * Get the list of additional parameters available.
	 * 
	 * @return the parameter names
	 * @since 1.4.6
	 */
	public List<String> getAdditionalParameterNames();

	/**
	 * Given an additional parameter name, return its resolved type. This returns null if the
	 * parameter name is invalid.
	 * 
	 * @param name the name
	 * @return the type or null
	 * @since 1.4.6
	 */
	public IModelElement getAdditionalParameterType(String parameterName);
}
