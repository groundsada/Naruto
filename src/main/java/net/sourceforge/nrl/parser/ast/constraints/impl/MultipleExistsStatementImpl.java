package net.sourceforge.nrl.parser.ast.constraints.impl;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.constraints.IMultipleExistsStatement;

import org.antlr.runtime.Token;

public class MultipleExistsStatementImpl extends ConstraintImpl implements
		IMultipleExistsStatement {

	private List<IModelReference> children;

	public MultipleExistsStatementImpl() {
	}
	
	public MultipleExistsStatementImpl(Token token) {
		super(token);
	}

	@Override
	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			for (IModelReference ref : getModelReferences()) {
				ref.accept(visitor);
			}
		}
		visitor.visitAfter(this);
	}

	@Deprecated
	public String dump(int indent) {
		String result = doIndent(indent) + "the following exist" + NEWLINE;
		for (IModelReference ref : getModelReferences()) {
			result = result + ref.dump(indent + 1);
		}
		return result;
	}

	public List<IModelReference> getModelReferences() {
		if (children == null) {
			children = new ArrayList<IModelReference>();

			for (Object child : getChildren()) {
				children.add((IModelReference) child);
			}
		}

		return children;
	}
}
