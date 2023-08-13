package net.sourceforge.nrl.parser.ast.constraints.impl;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.constraints.IMultipleNotExistsStatement;

import org.antlr.runtime.Token;

public class MultipleNotExistsStatementImpl extends ConstraintImpl implements
		IMultipleNotExistsStatement {

	private List<IModelReference> children;

	public MultipleNotExistsStatementImpl() {
	}
	
	public MultipleNotExistsStatementImpl(Token token) {
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
		String result = doIndent(indent) + "the following do not exist" + NEWLINE;
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
