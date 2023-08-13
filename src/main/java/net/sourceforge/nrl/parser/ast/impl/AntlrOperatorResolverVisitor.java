package net.sourceforge.nrl.parser.ast.impl;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.nrl.parser.IStatusCode;
import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.SemanticError;
import net.sourceforge.nrl.parser.ast.INRLAstNode;
import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.action.impl.OperatorActionImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.OperatorInvocationImpl;
import net.sourceforge.nrl.parser.operators.IOperator;
import net.sourceforge.nrl.parser.operators.IOperators;

/**
 * An implementation of an operator resolver.
 * <p>
 * This class traverses an AST, and resolves references to operators in all
 * {@link net.sourceforge.nrl.parser.ast.constraints.IOperatorInvocation} nodes it finds.
 * To use it, pass it to the <code>accept</code> method on a rule file.
 * <p>
 * It produces a list of errors that can be retrieved using {@link #getErrors()}. This
 * will contain {@link net.sourceforge.nrl.parser.SemanticError} objects for missing
 * operators or parameter mismatches.
 * 
 * @author Christian Nentwich
 */
public class AntlrOperatorResolverVisitor implements INRLAstVisitor {

	protected List<NRLError> errors = new ArrayList<NRLError>();

	protected IOperators[] operators;

	/**
	 * Create a new operator visitor with the collection to resolve against.
	 * 
	 * @param operators the operator collection
	 */
	public AntlrOperatorResolverVisitor(IOperators[] operators) {
		this.operators = operators;
	}

	/**
	 * Return the list of errors, after traversal.
	 * 
	 * @return a list of {@link net.sourceforge.nrl.parser.SemanticError} objects.
	 */
	public List<NRLError> getErrors() {
		return errors;
	}

	public boolean visitBefore(INRLAstNode node) {
		if (node instanceof OperatorInvocationImpl) {
			OperatorInvocationImpl invocation = (OperatorInvocationImpl) node;
			String name = invocation.getOperatorName();

			IOperator op = null;
			for (int i = 0; i < operators.length; i++) {
				op = operators[i].getOperator(name);
				if (op != null)
					break;
			}

			if (op == null) {
				errors.add(new SemanticError(IStatusCode.OPERATOR_UNKNOWN, node.getLine(), node
						.getColumn(), name.length() + 2, "Reference operator (word) not defined: "
						+ name));
				return true;
			}

			invocation.setOperator(op);

			if (invocation.getNumParameters() != op.getParameters().size()) {
				errors.add(new SemanticError(IStatusCode.OPERATOR_PARAMETER_MISMATCH, node
						.getLine(), node.getColumn(), name.length() + 2, "Operator expects "
						+ op.getParameters().size() + " parameters, but found "
						+ invocation.getNumParameters() + " in this rule."));
			}
		} else if (node instanceof OperatorActionImpl) {
			OperatorActionImpl action = (OperatorActionImpl) node;

			String name = action.getOperatorName();

			IOperator op = null;
			for (int i = 0; i < operators.length; i++) {
				op = operators[i].getOperator(name);
				if (op != null)
					break;
			}

			if (op == null) {
				errors.add(new SemanticError(IStatusCode.OPERATOR_UNKNOWN, node.getLine(), node
						.getColumn(), name.length() + 2, "Reference operator (word) not defined: "
						+ name));
				return true;
			}

			action.setOperator(op);

			if (action.getParameters().size() != op.getParameters().size()) {
				errors
						.add(new SemanticError(IStatusCode.OPERATOR_PARAMETER_MISMATCH, node
								.getLine(), node.getColumn(), name.length() + 2,
								"Operator implementation expects " + op.getParameters().size()
										+ ", but found " + action.getParameters().size()
										+ " in this rule."));
			}

			return true;
		}

		return true;
	}

	public void visitAfter(INRLAstNode node) {
	}

}
