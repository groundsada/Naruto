package net.sourceforge.nrl.parser.ast.constraints;

import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.IRuleFile;
import net.sourceforge.nrl.parser.ast.IRuleSetDeclaration;
import net.sourceforge.nrl.parser.ast.action.INRLActionDetailVisitor;

/**
 * A more complete visitor class with callbacks for each type of node that can
 * be found in the AST for constraint files. {@link INRLActionDetailVisitor}
 * extends this to cover the action language.
 * <p>
 * The class has two methods for each construct:
 * <ul>
 * <li>The <code>before</code> method is called before the <b>children</b>
 * of the node are processed. You must return a boolean true if you want the
 * children to be processed, or false otherwise.
 * <li>The <code>after</code> method is always visited (even if the before
 * method returned false), after the children have been processed.
 * </ul>
 * <p>
 * <b>How to use:</b> To use this interface, do the following:
 * <ul>
 * <li>Implement this interface, or extend {@link Stub}.
 * <li>Instantiate {@link net.sourceforge.nrl.parser.ast.ConstraintVisitorDispatcher}
 * and pass your instance to it.
 * <li>Call
 * {@link net.sourceforge.nrl.parser.ast.INRLAstNode#accept(INRLAstVisitor)},
 * passing the dispatcher to it.
 * </ul>
 * 
 * @author Christian Nentwich
 */
public interface INRLConstraintDetailVisitor {

	public void visitArithmeticExpressionAfter(IArithmeticExpression expr);

	public boolean visitArithmeticExpressionBefore(IArithmeticExpression expr);

	public void visitBinaryOperatorStatementAfter(IBinaryOperatorStatement statement);

	public boolean visitBinaryOperatorStatementBefore(IBinaryOperatorStatement statement);

	public void visitBinaryPredicateAfter(IBinaryPredicate predicate);

	public boolean visitBinaryPredicateBefore(IBinaryPredicate predicate);

	public void visitBooleanLiteral(IBooleanLiteral bool);

	public void visitCardinalityConstraint(ICardinalityConstraint constraint);

	public void visitCastExpressionAfter(ICastExpression expr);

	public boolean visitCastExpressionBefore(ICastExpression expr);

	public void visitCollectionIndexAfter(ICollectionIndex index);

	public boolean visitCollectionIndexBefore(ICollectionIndex index);

	public void visitCompoundReportAfter(ICompoundReport report);

	public boolean visitCompoundReportBefore(ICompoundReport report);

	public void visitConcatenatedReportAfter(IConcatenatedReport report);

	public boolean visitConcatenatedReportBefore(IConcatenatedReport report);

	public void visitConditionalReportAfter(IConditionalReport report);

	public boolean visitConditionalReportBefore(IConditionalReport report);

	public void visitConstraintRuleDeclarationAfter(IConstraintRuleDeclaration decl);

	public boolean visitConstraintRuleDeclarationBefore(IConstraintRuleDeclaration decl);

	public void visitDecimalNumber(IDecimalNumber number);

	public void visitExistsStatementAfter(IExistsStatement exists);

	public boolean visitExistsStatementBefore(IExistsStatement exists);

	public void visitForallStatementAfter(IForallStatement forall);

	public boolean visitForallStatementBefore(IForallStatement forall);

	public void visitFunctionalExpressionAfter(IFunctionalExpression expr);

	public boolean visitFunctionalExpressionBefore(IFunctionalExpression expr);

	public void visitGlobalExistsStatementAfter(IGlobalExistsStatement exists);

	public boolean visitGlobalExistsStatementBefore(IGlobalExistsStatement exists);

	public void visitIfThenStatementAfter(IIfThenStatement ifThen);

	public boolean visitIfThenStatementBefore(IIfThenStatement ifThen);

	public void visitIntegerNumber(IIntegerNumber number);

	public void visitIsInPredicateAfter(IIsInPredicate isIn);

	public boolean visitIsInPredicateBefore(IIsInPredicate isIn);

	public void visitIsNotInPredicateAfter(IIsNotInPredicate isNotIn);

	public boolean visitIsNotInPredicateBefore(IIsNotInPredicate isNotIn);

	public void visitIsSubtypePredicateAfter(IIsSubtypePredicate subType);

	public boolean visitIsSubtypePredicateBefore(IIsSubtypePredicate subType);

	public void visitLiteralString(ILiteralString literal);

	public void visitModelReferenceAfter(IModelReference ref);

	public boolean visitModelReferenceBefore(IModelReference ref);

	public void visitMultipleExistsStatementAfter(IMultipleExistsStatement statement);

	public boolean visitMultipleExistsStatementBefore(IMultipleExistsStatement statement);

	public void visitMultipleNotExistsStatementAfter(IMultipleNotExistsStatement statement);

	public boolean visitMultipleNotExistsStatementBefore(
			IMultipleNotExistsStatement statement);

	public void visitNotExistsStatementAfter(INotExistsStatement exists);

	public boolean visitNotExistsStatementBefore(INotExistsStatement exists);

	public void visitOperatorInvocationAfter(IOperatorInvocation op);

	public boolean visitOperatorInvocationBefore(IOperatorInvocation op);

	public void visitRuleFileAfter(IRuleFile file);

	public boolean visitRuleFileBefore(IRuleFile file);

	public void visitRuleSetDeclarationAfter(IRuleSetDeclaration decl);

	public boolean visitRuleSetDeclarationBefore(IRuleSetDeclaration decl);

	public void visitSelectionExpressionAfter(ISelectionExpression expr);
	
	public boolean visitSelectionExpressionBefore(ISelectionExpression expr);
	
	public void visitValidationFragmentApplicationAfter(IValidationFragmentApplication app);

	public boolean visitValidationFragmentApplicationBefore(
			IValidationFragmentApplication app);

	public void visitValidationFragmentDeclarationAfter(
			IValidationFragmentDeclaration decl);

	public boolean visitValidationFragmentDeclarationBefore(
			IValidationFragmentDeclaration decl);

	public void visitVariableDeclarationAfter(IVariableDeclaration decl);

	public boolean visitVariableDeclarationBefore(IVariableDeclaration decl);

	/**
	 * Extend from this if you don't want to implement the full interface.
	 */
	public static class Stub implements INRLConstraintDetailVisitor {

		public void visitArithmeticExpressionAfter(IArithmeticExpression expr) {
		}

		public boolean visitArithmeticExpressionBefore(IArithmeticExpression expr) {
			return true;
		}

		public void visitBinaryOperatorStatementAfter(IBinaryOperatorStatement statement) {
		}

		public boolean visitBinaryOperatorStatementBefore(IBinaryOperatorStatement statement) {
			return true;
		}

		public void visitBinaryPredicateAfter(IBinaryPredicate predicate) {
		}

		public boolean visitBinaryPredicateBefore(IBinaryPredicate predicate) {
			return true;
		}

		public void visitBooleanLiteral(IBooleanLiteral bool) {
		}

		public void visitCardinalityConstraint(ICardinalityConstraint constraint) {
		}

		public void visitCastExpressionAfter(ICastExpression expr) {
		}

		public boolean visitCastExpressionBefore(ICastExpression expr) {
			return true;
		}

		public void visitCollectionIndexAfter(ICollectionIndex index) {
		}

		public boolean visitCollectionIndexBefore(ICollectionIndex index) {
			return true;
		}

		public void visitCompoundReportAfter(ICompoundReport report) {
		}

		public boolean visitCompoundReportBefore(ICompoundReport report) {
			return true;
		}

		public void visitConcatenatedReportAfter(IConcatenatedReport report) {
		}

		public boolean visitConcatenatedReportBefore(IConcatenatedReport report) {
			return true;
		}

		public void visitConditionalReportAfter(IConditionalReport report) {
		}

		public boolean visitConditionalReportBefore(IConditionalReport report) {
			return true;
		}

		public void visitConstraintRuleDeclarationAfter(IConstraintRuleDeclaration decl) {
		}

		public boolean visitConstraintRuleDeclarationBefore(IConstraintRuleDeclaration decl) {
			return true;
		}

		public void visitDecimalNumber(IDecimalNumber number) {
		}

		public void visitExistsStatementAfter(IExistsStatement exists) {
		}

		public boolean visitExistsStatementBefore(IExistsStatement exists) {
			return true;
		}

		public void visitForallStatementAfter(IForallStatement forall) {
		}

		public boolean visitForallStatementBefore(IForallStatement forall) {
			return true;
		}

		public void visitFunctionalExpressionAfter(IFunctionalExpression expr) {
		}

		public boolean visitFunctionalExpressionBefore(IFunctionalExpression expr) {
			return true;
		}

		public void visitGlobalExistsStatementAfter(IGlobalExistsStatement exists) {
		}

		public boolean visitGlobalExistsStatementBefore(IGlobalExistsStatement exists) {
			return true;
		}

		public void visitIfThenStatementAfter(IIfThenStatement ifThen) {
		}

		public boolean visitIfThenStatementBefore(IIfThenStatement ifThen) {
			return true;
		}

		public void visitIntegerNumber(IIntegerNumber number) {
		}

		public void visitIsInPredicateAfter(IIsInPredicate isIn) {
		}

		public boolean visitIsInPredicateBefore(IIsInPredicate isIn) {
			return true;
		}

		public void visitIsNotInPredicateAfter(IIsNotInPredicate isNotIn) {
		}

		public boolean visitIsNotInPredicateBefore(IIsNotInPredicate isNotIn) {
			return true;
		}

		public void visitIsSubtypePredicateAfter(IIsSubtypePredicate subType) {
		}

		public boolean visitIsSubtypePredicateBefore(IIsSubtypePredicate subType) {
			return true;
		}

		public void visitLiteralString(ILiteralString literal) {
		}

		public void visitModelReferenceAfter(IModelReference ref) {
		}

		public boolean visitModelReferenceBefore(IModelReference ref) {
			return true;
		}

		public void visitMultipleExistsStatementAfter(IMultipleExistsStatement statement) {
		}

		public boolean visitMultipleExistsStatementBefore(IMultipleExistsStatement statement) {
			return true;
		}

		public void visitMultipleNotExistsStatementAfter(IMultipleNotExistsStatement statement) {
		}

		public boolean visitMultipleNotExistsStatementBefore(IMultipleNotExistsStatement statement) {
			return true;
		}

		public void visitNotExistsStatementAfter(INotExistsStatement exists) {
		}

		public boolean visitNotExistsStatementBefore(INotExistsStatement exists) {
			return true;
		}

		public void visitOperatorInvocationAfter(IOperatorInvocation op) {
		}

		public boolean visitOperatorInvocationBefore(IOperatorInvocation op) {
			return true;
		}

		public void visitRuleFileAfter(IRuleFile file) {
		}

		public boolean visitRuleFileBefore(IRuleFile file) {
			return true;
		}

		public void visitRuleSetDeclarationAfter(IRuleSetDeclaration decl) {
			
			
		}

		public boolean visitRuleSetDeclarationBefore(IRuleSetDeclaration decl) {
			return true;
		}

		public void visitSelectionExpressionAfter(ISelectionExpression expr) {
		}

		public boolean visitSelectionExpressionBefore(ISelectionExpression expr) {
			return true;
		}

		public void visitValidationFragmentApplicationAfter(IValidationFragmentApplication app) {
		}

		public boolean visitValidationFragmentApplicationBefore(IValidationFragmentApplication app) {
			return true;
		}

		public void visitValidationFragmentDeclarationAfter(IValidationFragmentDeclaration decl) {
		}

		public boolean visitValidationFragmentDeclarationBefore(IValidationFragmentDeclaration decl) {
			return true;
		}

		public void visitVariableDeclarationAfter(IVariableDeclaration decl) {
		}

		public boolean visitVariableDeclarationBefore(IVariableDeclaration decl) {
			return true;
		}
		
	}
}
