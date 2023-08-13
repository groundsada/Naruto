package net.sourceforge.nrl.parser.ast.constraints;


/**
 * An if-then-else reporting statement.
 * 
 * @author Christian Nentwich
 */
public interface IConditionalReport extends ISimpleReport {

	/**
	 * Return the "if" condition.
	 * 
	 * @return the condition
	 */
	public IConstraint getCondition();

	/**
	 * Return the report to create if the condition was true. Never returns null
	 * 
	 * @return the report
	 */
	public ICompoundReport getThen();

	/**
	 * Return the report to create if the condition was false. This can return
	 * null if there was no "else" statement.
	 * 
	 * @return the report
	 */
	public ICompoundReport getElse();
}
