package net.sourceforge.nrl.parser.ast.constraints;

import net.sourceforge.nrl.parser.ast.INRLAstNode;

/**
 * A "simple report" is a report that can occur in a compound report, but cannot
 * contain further compound reports.
 * 
 * @author Christian Nentwich
 * 
 */
public interface ISimpleReport extends INRLAstNode {

}
