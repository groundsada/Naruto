package net.sourceforge.nrl.parser.ast.impl;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.IRuleDeclaration;
import net.sourceforge.nrl.parser.ast.IRuleSetDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.IConstraint;
import net.sourceforge.nrl.parser.model.IModelElement;

import org.antlr.runtime.Token;

public class RuleSetDeclarationImpl extends Antlr3NRLBaseAst implements IRuleSetDeclaration {

	// The context - must be set by setContext by a resolver
	private IModelElement context;

	// The rules in this set. This must be populated by an AST resolver
	// (ConstraintAstResolver
	// in this case), using the addRule method
	private List<IRuleDeclaration> rules = new ArrayList<IRuleDeclaration>();

	public RuleSetDeclarationImpl(Token token) {
		super(token);
	}

	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			if (getPreconditionConstraint() != null)
				getPreconditionConstraint().accept(visitor);
		}
		visitor.visitAfter(this);
	}

	@Deprecated
	public String dump(int indent) {
		String result = doIndent(indent) + "Rule Set " + getId() + NEWLINE
				+ getModelReference().dump(indent + 1);
		if (getPreconditionConstraint() != null)
			result += getPreconditionConstraint().dump(indent + 1);
		return result;
	}

	public String getId() {
		return getChild(0).getText();
	}

	public IConstraint getPreconditionConstraint() {
		if (getChildCount() > 2)
			return (IConstraint) getChild(2);
		return null;
	}

	public IModelElement getPreconditionContext() {
		return context;
	}

	public List<IRuleDeclaration> getRules() {
		return rules;
	}

	/**
	 * Add a rule to this set. Used by the resolver
	 * 
	 * @param decl the rule
	 */
	public void addRule(IRuleDeclaration decl) {
		rules.add(decl);
	}

	/**
	 * Helper method - return the raw precondition model reference, for use in
	 * resolution. Returns null if no precondition
	 * 
	 * @return the reference
	 */
	public IModelReference getModelReference() {
		if (getChildCount() > 1)
			return (IModelReference) getChild(1);
		return null;
	}

	/**
	 * Set the model element referenced in the precondition context. Used by a
	 * resolver only if there is a precondition.
	 * 
	 * @param context the context to set
	 */
	public void setContext(IModelElement context) {
		this.context = context;
	}

}
