package net.sourceforge.nrl.parser.ast.constraints;

import java.util.List;

import net.sourceforge.nrl.parser.ast.INRLAstNode;

/**
 * A list of reporting statements that should be processed in sequence, possibly
 * by concatenation.
 * 
 * @author Christian Nentwich
 */
public interface ICompoundReport extends INRLAstNode {

	/**
	 * Return the individual reports that make up this compound report.
	 * @return a list of reports
	 */
	public List<ISimpleReport> getReports();
}
