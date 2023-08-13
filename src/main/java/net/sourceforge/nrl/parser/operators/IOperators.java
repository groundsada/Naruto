package net.sourceforge.nrl.parser.operators;

import java.util.List;

import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.model.IModelCollection;

/**
 * A collection of operators, with operations to look them up by name. This
 * needs a concrete implementation, for example to load and write definitions
 * from and to XML.
 * 
 * @author Christian Nentwich
 */
public interface IOperators {

	/**
	 * Indicates the version of the operator file format from which operators were loaded.
	 */
	public enum LoadedVersion {
		Version14, Version15
	}
	
	/**
	 * Return the operator documentation, may be null.
	 * 
	 * @return the documentation
	 */
	public String getDocumentation();
	
	/**
	 * The list of referenced models.
	 * 
	 * @return the list
	 */
	public List<String> getModelFileNames();

	/**
	 * Return all operators in this collection.
	 * 
	 * @return a list of {@link IOperator}.
	 */
	public List<IOperator> getOperators();

	/**
	 * Get an operator by name. Returns null if not found.
	 * 
	 * @param name the name
	 * @return the operator or null
	 */
	public IOperator getOperator(String name);

	/**
	 * Resolve all parameter type and return type references against models.
	 * 
	 * @param models the models
	 * @return any errors encountered
	 */
	public List<NRLError> resolveModelReferences(IModelCollection models);
	
	/**
	 * Gets the operator file format version that these operators were loaded from.
	 */
	public LoadedVersion getLoadedVersion(); 
}
