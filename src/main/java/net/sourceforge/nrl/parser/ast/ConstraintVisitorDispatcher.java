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

import net.sourceforge.nrl.parser.ast.constraints.IArithmeticExpression;
import net.sourceforge.nrl.parser.ast.constraints.IBinaryOperatorStatement;
import net.sourceforge.nrl.parser.ast.constraints.IBinaryPredicate;
import net.sourceforge.nrl.parser.ast.constraints.IBooleanLiteral;
import net.sourceforge.nrl.parser.ast.constraints.ICardinalityConstraint;
import net.sourceforge.nrl.parser.ast.constraints.ICastExpression;
import net.sourceforge.nrl.parser.ast.constraints.ICollectionIndex;
import net.sourceforge.nrl.parser.ast.constraints.ICompoundReport;
import net.sourceforge.nrl.parser.ast.constraints.IConcatenatedReport;
import net.sourceforge.nrl.parser.ast.constraints.IConditionalReport;
import net.sourceforge.nrl.parser.ast.constraints.IConstraintRuleDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.IDecimalNumber;
import net.sourceforge.nrl.parser.ast.constraints.IExistsStatement;
import net.sourceforge.nrl.parser.ast.constraints.IForallStatement;
import net.sourceforge.nrl.parser.ast.constraints.IFunctionalExpression;
import net.sourceforge.nrl.parser.ast.constraints.IGlobalExistsStatement;
import net.sourceforge.nrl.parser.ast.constraints.IIfThenStatement;
import net.sourceforge.nrl.parser.ast.constraints.IIntegerNumber;
import net.sourceforge.nrl.parser.ast.constraints.IIsInPredicate;
import net.sourceforge.nrl.parser.ast.constraints.IIsNotInPredicate;
import net.sourceforge.nrl.parser.ast.constraints.IIsSubtypePredicate;
import net.sourceforge.nrl.parser.ast.constraints.ILiteralString;
import net.sourceforge.nrl.parser.ast.constraints.IMultipleExistsStatement;
import net.sourceforge.nrl.parser.ast.constraints.IMultipleNotExistsStatement;
import net.sourceforge.nrl.parser.ast.constraints.INRLConstraintDetailVisitor;
import net.sourceforge.nrl.parser.ast.constraints.INotExistsStatement;
import net.sourceforge.nrl.parser.ast.constraints.IOperatorInvocation;
import net.sourceforge.nrl.parser.ast.constraints.ISelectionExpression;
import net.sourceforge.nrl.parser.ast.constraints.IValidationFragmentApplication;
import net.sourceforge.nrl.parser.ast.constraints.IValidationFragmentDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.IVariableDeclaration;

/**
 * A default implementation of the INRLAstVisitor interface that dispatches
 * calls to the visitor method to a client-supplied
 * {@link net.sourceforge.nrl.parser.ast.constraints.INRLConstraintDetailVisitor}.
 * 
 * @author Christian Nentwich
 */
public class ConstraintVisitorDispatcher implements INRLAstVisitor {

	protected INRLConstraintDetailVisitor visitor;

	/**
	 * Initialise the dispatcher with a client-supplied visitor.
	 * 
	 * @param visitor the visitor
	 */
	public ConstraintVisitorDispatcher(INRLConstraintDetailVisitor visitor) {
		this.visitor = visitor;
	}

	/**
	 * Default implementation - dispatches calls to the registered visitor's
	 * methods.
	 */
	public void visitAfter(INRLAstNode node) {
		if (node instanceof IArithmeticExpression)
			visitor.visitArithmeticExpressionAfter((IArithmeticExpression) node);
		else if (node instanceof IBinaryOperatorStatement)
			visitor.visitBinaryOperatorStatementAfter((IBinaryOperatorStatement) node);
		else if (node instanceof IBinaryPredicate)
			visitor.visitBinaryPredicateAfter((IBinaryPredicate) node);
		else if (node instanceof IBooleanLiteral)
			visitor.visitBooleanLiteral((IBooleanLiteral) node);
		else if (node instanceof ICastExpression)
			visitor.visitCastExpressionAfter((ICastExpression) node);
		else if (node instanceof ICollectionIndex)
			visitor.visitCollectionIndexAfter((ICollectionIndex) node);
		else if (node instanceof ICompoundReport)
			visitor.visitCompoundReportAfter((ICompoundReport) node);
		else if (node instanceof IConcatenatedReport)
			visitor.visitConcatenatedReportAfter((IConcatenatedReport) node);
		else if (node instanceof IConditionalReport)
			visitor.visitConditionalReportAfter((IConditionalReport) node);
		else if (node instanceof IExistsStatement)
			visitor.visitExistsStatementAfter((IExistsStatement) node);
		else if (node instanceof IForallStatement)
			visitor.visitForallStatementAfter((IForallStatement) node);
		else if (node instanceof IFunctionalExpression)
			visitor.visitFunctionalExpressionAfter((IFunctionalExpression) node);
		else if (node instanceof IGlobalExistsStatement)
			visitor.visitGlobalExistsStatementAfter((IGlobalExistsStatement) node);
		else if (node instanceof IIfThenStatement)
			visitor.visitIfThenStatementAfter((IIfThenStatement) node);
		else if (node instanceof IIsInPredicate)
			visitor.visitIsInPredicateAfter((IIsInPredicate) node);
		else if (node instanceof IIsNotInPredicate)
			visitor.visitIsNotInPredicateAfter((IIsNotInPredicate) node);
		else if (node instanceof IIsSubtypePredicate)
			visitor.visitIsSubtypePredicateAfter((IIsSubtypePredicate) node);
		else if (node instanceof IModelReference)
			visitor.visitModelReferenceAfter((IModelReference) node);
		else if (node instanceof IMultipleExistsStatement)
			visitor.visitMultipleExistsStatementAfter((IMultipleExistsStatement) node);
		else if (node instanceof IMultipleNotExistsStatement)
			visitor
					.visitMultipleNotExistsStatementAfter((IMultipleNotExistsStatement) node);
		else if (node instanceof INotExistsStatement)
			visitor.visitNotExistsStatementAfter((INotExistsStatement) node);
		else if (node instanceof IOperatorInvocation)
			visitor.visitOperatorInvocationAfter((IOperatorInvocation) node);
		else if (node instanceof IValidationFragmentApplication)
			visitor.visitValidationFragmentApplicationAfter((IValidationFragmentApplication) node);
		else if (node instanceof IValidationFragmentDeclaration)
			visitor.visitValidationFragmentDeclarationAfter((IValidationFragmentDeclaration) node);
		else if (node instanceof IConstraintRuleDeclaration)
			visitor
					.visitConstraintRuleDeclarationAfter((IConstraintRuleDeclaration) node);
		else if (node instanceof IRuleFile)
			visitor.visitRuleFileAfter((IRuleFile) node);
		else if (node instanceof ISelectionExpression)
			visitor.visitSelectionExpressionAfter((ISelectionExpression) node);
		else if (node instanceof IVariableDeclaration)
			visitor.visitVariableDeclarationAfter((IVariableDeclaration) node);
		else if (node instanceof IRuleSetDeclaration)
			visitor.visitRuleSetDeclarationAfter((IRuleSetDeclaration) node);
	}

	/**
	 * Default implementation - dispatches calls to the registered visitor's
	 * methods.
	 */
	public boolean visitBefore(INRLAstNode node) {
		if (node instanceof IArithmeticExpression)
			return visitor.visitArithmeticExpressionBefore((IArithmeticExpression) node);
		else if (node instanceof IBinaryOperatorStatement)
			return visitor
					.visitBinaryOperatorStatementBefore((IBinaryOperatorStatement) node);
		else if (node instanceof IBinaryPredicate)
			return visitor.visitBinaryPredicateBefore((IBinaryPredicate) node);
		else if (node instanceof ICardinalityConstraint) {
			visitor.visitCardinalityConstraint((ICardinalityConstraint) node);
			return true;
		} else if (node instanceof ICastExpression)
			return visitor.visitCastExpressionBefore((ICastExpression) node);
		else if (node instanceof ICollectionIndex)
			return visitor.visitCollectionIndexBefore((ICollectionIndex) node);
		else if (node instanceof ICompoundReport)
			return visitor.visitCompoundReportBefore((ICompoundReport) node);
		else if (node instanceof IConcatenatedReport)
			return visitor.visitConcatenatedReportBefore((IConcatenatedReport) node);
		else if (node instanceof IConditionalReport)
			return visitor.visitConditionalReportBefore((IConditionalReport) node);
		else if (node instanceof IExistsStatement)
			return visitor.visitExistsStatementBefore((IExistsStatement) node);
		else if (node instanceof IForallStatement)
			return visitor.visitForallStatementBefore((IForallStatement) node);
		else if (node instanceof IFunctionalExpression)
			return visitor.visitFunctionalExpressionBefore((IFunctionalExpression) node);
		else if (node instanceof IGlobalExistsStatement)
			return visitor
					.visitGlobalExistsStatementBefore((IGlobalExistsStatement) node);
		else if (node instanceof IIfThenStatement)
			return visitor.visitIfThenStatementBefore((IIfThenStatement) node);
		else if (node instanceof IIsInPredicate)
			return visitor.visitIsInPredicateBefore((IIsInPredicate) node);
		else if (node instanceof IIsNotInPredicate)
			return visitor.visitIsNotInPredicateBefore((IIsNotInPredicate) node);
		else if (node instanceof IIsSubtypePredicate)
			return visitor.visitIsSubtypePredicateBefore((IIsSubtypePredicate) node);
		else if (node instanceof ILiteralString) {
			visitor.visitLiteralString((ILiteralString) node);
			return true;
		} else if (node instanceof IModelReference)
			return visitor.visitModelReferenceBefore((IModelReference) node);
		else if (node instanceof IMultipleExistsStatement)
			return visitor
					.visitMultipleExistsStatementBefore((IMultipleExistsStatement) node);
		else if (node instanceof IMultipleNotExistsStatement)
			return visitor
					.visitMultipleNotExistsStatementBefore((IMultipleNotExistsStatement) node);
		else if (node instanceof INotExistsStatement)
			return visitor.visitNotExistsStatementBefore((INotExistsStatement) node);
		else if (node instanceof IDecimalNumber) {
			visitor.visitDecimalNumber((IDecimalNumber) node);
			return true;
		} else if (node instanceof IIntegerNumber) {
			visitor.visitIntegerNumber((IIntegerNumber) node);
			return true;
		} else if (node instanceof IOperatorInvocation)
			return visitor.visitOperatorInvocationBefore((IOperatorInvocation) node);
		else if (node instanceof ISelectionExpression)
			return visitor.visitSelectionExpressionBefore((ISelectionExpression) node);
		else if (node instanceof IValidationFragmentApplication)
			return visitor.visitValidationFragmentApplicationBefore((IValidationFragmentApplication) node);
		else if (node instanceof IValidationFragmentDeclaration)
			return visitor.visitValidationFragmentDeclarationBefore((IValidationFragmentDeclaration) node);
		else if (node instanceof IConstraintRuleDeclaration)
			return visitor
					.visitConstraintRuleDeclarationBefore((IConstraintRuleDeclaration) node);
		else if (node instanceof IRuleFile)
			return visitor.visitRuleFileBefore((IRuleFile) node);
		else if (node instanceof IVariableDeclaration)
			return visitor.visitVariableDeclarationBefore((IVariableDeclaration) node);
		else if (node instanceof IRuleSetDeclaration)
			return visitor.visitRuleSetDeclarationBefore((IRuleSetDeclaration) node);

		return true;
	}
}
