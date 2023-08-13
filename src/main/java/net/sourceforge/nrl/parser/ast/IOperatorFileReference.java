package net.sourceforge.nrl.parser.ast;

import net.sourceforge.nrl.parser.operators.IOperators;

/**
 * A reference to an operator file, by file name
 * 
 * @author Christian Nentwich
 */
public interface IOperatorFileReference extends INRLAstNode {

	/**
	 * Return the file name. Can be absolute or relative.
	 * 
	 * @return the file name
	 */
	public String getFileName();

	/**
	 * Returns true if the file name is absolute, i.e. starts from a root
	 * directory. If false, it is relative to the rule file.
	 * 
	 * @return true if the file name is absolute, false otherwise
	 */
	public boolean isAbsolute();
	
	/**
	 * Returns <code>true</code> if the oeprator has been resolved in which case it can be accessed
	 * via {@link IOperatorFileReference.getOperator}, <code>false</code> otherwise.
	 * @return <code>true</code> if the operator has been resolved, <code>false</code> otherwise.
	 * @since 1.4.10
	 */
	public boolean isOperatorsResolved();
	
	/**
	 * Returns the operator referred to by this reference, it has been resolved.
	 * @return The operator referred to by this reference, if it has been resolved, null otherwise.
	 * @since 1.4.10
	 */
	public IOperators getOperators();

	/**
	 * Attaches an {@link IOperators} resolved from this operator file reference.
	 * @param operators The resolved {@link IOperators} for this operator file reference
	 */
	public void resolveOperators(IOperators operators);
}
