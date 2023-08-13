package net.sourceforge.nrl.parser.ast;

import java.util.List;

import net.sourceforge.nrl.parser.model.IModelElement;

/**
 * Any declaration that takes multiple contexts, with parameter names.
 * 
 * @author Christian Nentwich
 */
public interface IMultipleContextDeclaration extends IDeclaration {

	/**
	 * Return the assigned context names - the "parameter names". These all
	 * become variables within the declaration.
	 * 
	 * @return the names
	 */
	public List<String> getContextNames();

	/**
	 * Given a parameter name, return its type. This will never return null
	 * after the AST has been resolved.
	 * 
	 * @param name
	 *            the parameter name
	 * @return its type
	 */
	public IModelElement getContextType(String name);
}
