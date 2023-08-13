/*
 * The contents of this file are subject to the Mozilla Public License 
 * Version 1.1 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See 
 * the License for the specific language governing rights and limitations 
 * under the License.
 * 
 * The Original Code is the NRL Parser, released 28 April 2006, 
 * Copyright (c) Christian Nentwich. The Initial Developer of the 
 * Original Code is Christian Nentwich. Portions created by contributors 
 * identified in the NOTICES file are Copyright (c) the contributors. 
 * All Rights Reserved. 
 */
package net.sourceforge.nrl.parser.ast;

import java.util.List;

import net.sourceforge.nrl.parser.ast.constraints.IConstraint;
import net.sourceforge.nrl.parser.model.IModelElement;

/**
 * A rule set is a named collection of rules, with an optional precondition
 * attached to the execution of the rules.
 * <p>
 * The semantics of a rule set is such that the precondition is evaluated first.
 * If the precondition evaluates to true, the rules in the rule set are
 * executed, otherwise they are not executed. If there is no precondition, the
 * rule set always executes.
 * <p>
 * The precondition of a rule set is evaluated relative to a <b>context</b>,
 * the same way that a rule is evaluated relative to a context. For example:
 * 
 * <pre>
 *         RuleSet &quot;old trades&quot;
 *         Applies if Trade has a tradedate before '2005-02-17'
 * </pre>
 * 
 * <p>
 * In this example, all rules in the NRL file following the rule set declaration
 * (but before the next rule set declaration) are executed only if in the input
 * data, the context (Trade) fulfils the constraint.
 * 
 * @author Christian Nentwich
 */
public interface IRuleSetDeclaration extends INRLAstNode {

	/**
	 * Return the rule set identifier. This is never null.
	 * 
	 * @return the identifier
	 */
	public String getId();

	/**
	 * Return the precondition context, if there is one. Otherwise, return null.
	 * Can only be called after resolving against a model. If this returns a
	 * value, then {@link #getPreconditionConstraint()} will too.
	 * 
	 * @return the precondition context or null
	 */
	public IModelElement getPreconditionContext();

	/**
	 * Return the precondition constraint, if there is one. If this returns a
	 * constraint, then {@link #getPreconditionContext()} will return a value
	 * too.
	 * 
	 * @return the constraint or null
	 */
	public IConstraint getPreconditionConstraint();

	/**
	 * Return the rules in this rule set. This returns a set of
	 * {@link IRuleDeclaration} objects. This can only be called once the AST
	 * has been fully resolved.
	 * 
	 * @return the rules
	 */
	public List<IRuleDeclaration> getRules();
}
