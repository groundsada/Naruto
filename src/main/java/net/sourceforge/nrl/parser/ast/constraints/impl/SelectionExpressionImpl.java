package net.sourceforge.nrl.parser.ast.constraints.impl;

import org.antlr.runtime.Token;

import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.constraints.IConstraint;
import net.sourceforge.nrl.parser.ast.constraints.ISelectionExpression;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

public class SelectionExpressionImpl extends ConstraintImpl implements
		ISelectionExpression {

	private boolean single = false;

	public SelectionExpressionImpl(Token token) {
		super(token);

		if (token.getType() == NRLActionParser.VT_SINGLE_SELECTION_EXPR) {
			single = true;
		}
	}

	@Override
	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			getModelReference().accept(visitor);
			getConstraint().accept(visitor);
		}
		visitor.visitAfter(this);
	}

	public IModelReference getModelReference() {
		return (IModelReference) getChild(0);
	}

	public IConstraint getConstraint() {
		return (IConstraint) getChild(1);
	}

	public boolean isSingleElementSelection() {
		return single;
	}
}
