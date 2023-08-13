package net.sourceforge.nrl.parser.ast.constraints.impl;

import org.antlr.runtime.Token;

import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.constraints.IIsSubtypePredicate;
import net.sourceforge.nrl.parser.model.IModelElement;

public class IsSubtypePredicateImpl extends ConstraintImpl implements IIsSubtypePredicate {

	private IModelElement superType;
	
	public IsSubtypePredicateImpl(Token token) {
		super(token);
	}

	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			getReference().accept(visitor);
		}
		visitor.visitAfter(this);
	}

	public IModelReference getReference() {
		return (IModelReference) getChild(0);
	}

	public IModelElement getTargetType() {
		return superType;
	}
	
	public IModelReference getTypeReference() {
		return (IModelReference) getChild(1);
	}

	public void setSuperType(IModelElement element) {
		this.superType = element;
	}

	@Deprecated
	public String dump(int indent) {
		String result = doIndent(indent) + "Is sub type? " + NEWLINE
				+ getReference().dump(indent + 1) + doIndent(indent + 1) + "of "
				+ getTypeReference().dump(indent + 1);
		return result;
	}
}
