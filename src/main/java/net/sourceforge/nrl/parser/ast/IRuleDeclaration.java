package net.sourceforge.nrl.parser.ast;

/**
 * A generic rule declaration. Extends a declaration to add a possible
 * containing rule set.
 * 
 * @author Christian Nentwich
 */
public interface IRuleDeclaration extends ISingleContextDeclaration {
	
	/**
	 * Return the rule set that the rule is contained in, if any. If the rule
	 * is declared standalone, this returns null.
	 * <p>
	 * This can only be called once the AST has been fully resolved.
	 * 
	 * @return the rule set
	 */
	public IRuleSetDeclaration getRuleSet();
	
	/**
	 * Set the rule set that this rule is contained in.
	 * 
	 * @param ruleSet the rule set
	 */
	public void setRuleSet(IRuleSetDeclaration ruleSet);

}
