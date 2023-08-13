package net.sourceforge.nrl.parser.ast.constraints.impl;

import org.antlr.runtime.Token;

import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.constraints.ICastExpression;
import net.sourceforge.nrl.parser.model.IModelElement;

public class CastExpressionImpl extends ConstraintImpl implements ICastExpression {

	private IModelElement targetType;
	
	public CastExpressionImpl(Token token) {
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

	public IModelReference getTargetReference() {
		return (IModelReference) getChild(1);
	}

	public IModelElement getTargetType() {
		return targetType;
	}

	public void setTargetType(IModelElement element) {
		this.targetType = element;
	}

	@Deprecated
	public String dump(int indent) {
		String result = doIndent(indent) + "Cast " + NEWLINE
				+ getReference().dump(indent + 1) + doIndent(indent + 1) + "to "
				+ getTargetReference().dump(indent + 1);
		return result;
	}
}
