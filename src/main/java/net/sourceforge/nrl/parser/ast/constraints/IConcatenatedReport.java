package net.sourceforge.nrl.parser.ast.constraints;

import java.util.List;


/**
 * A concatenation of expressions that should result in a report string.
 * 
 * @author Christian Nentwich
 */
public interface IConcatenatedReport extends ISimpleReport {

	/**
	 * Return the expressions that should be concatenated. They all have to be
	 * evaluated to strings.
	 * 
	 * @return the expressions
	 */
	public List<IExpression> getExpressions();
}
