/*
 * The contents of this file are subject to the Mozilla Public License Version 1.1 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF
 * ANY KIND, either express or implied. See the License for the specific language governing rights
 * and limitations under the License.
 * 
 * The Original Code is the NRL Parser, released 28 April 2006, Copyright (c) Christian Nentwich.
 * The Initial Developer of the Original Code is Christian Nentwich. Portions created by
 * contributors identified in the NOTICES file are Copyright (c) the contributors. All Rights
 * Reserved.
 */
package net.sourceforge.nrl.parser.type;

import static java.lang.String.format;
import static net.sourceforge.nrl.parser.ast.NRLDataType.Type.Boolean;
import static net.sourceforge.nrl.parser.ast.NRLDataType.Type.Date;
import static net.sourceforge.nrl.parser.ast.NRLDataType.Type.Decimal;
import static net.sourceforge.nrl.parser.ast.NRLDataType.Type.Element;
import static net.sourceforge.nrl.parser.ast.NRLDataType.Type.Integer;
import static net.sourceforge.nrl.parser.ast.NRLDataType.Type.String;
import static net.sourceforge.nrl.parser.ast.NRLDataType.Type.Unknown;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.nrl.parser.IStatusCode;
import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.SemanticError;
import net.sourceforge.nrl.parser.ast.ConstraintVisitorDispatcher;
import net.sourceforge.nrl.parser.ast.IDeclaration;
import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.INRLAstNode;
import net.sourceforge.nrl.parser.ast.IRuleFile;
import net.sourceforge.nrl.parser.ast.IRuleSetDeclaration;
import net.sourceforge.nrl.parser.ast.IVariable;
import net.sourceforge.nrl.parser.ast.NRLDataType;
import net.sourceforge.nrl.parser.ast.NRLDataType.Type;
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
import net.sourceforge.nrl.parser.ast.constraints.IExpression;
import net.sourceforge.nrl.parser.ast.constraints.IForallStatement;
import net.sourceforge.nrl.parser.ast.constraints.IFunctionalExpression;
import net.sourceforge.nrl.parser.ast.constraints.IGlobalExistsStatement;
import net.sourceforge.nrl.parser.ast.constraints.IIdentifier;
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
import net.sourceforge.nrl.parser.ast.constraints.impl.ValidationFragmentDeclarationImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.ValidationFragmentDependencyProcessor;
import net.sourceforge.nrl.parser.model.AbstractModelElement;
import net.sourceforge.nrl.parser.model.IAttribute;
import net.sourceforge.nrl.parser.model.IClassifier;
import net.sourceforge.nrl.parser.model.IDataType;
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.operators.IOperator;
import net.sourceforge.nrl.parser.operators.IOperators;
import net.sourceforge.nrl.parser.operators.IParameter;

/**
 * Default type checker implementation. This uses the built-in visitor pattern to type-check every
 * node in the AST.
 * <p>
 * The type checker uses a depth-first strategy. Types are assigned to children and then to their
 * parents. In addition, the following strategy is used to deal with elements or operators where the
 * NRL type is unknown:
 * <ul>
 * <li>The <i>UNKNOWN</i> type is assigned by the model reference or operator invocation visit
 * <li>The operator or model reference is flagged as an error
 * <li>No further errors are raised throughout the type checker for expressions where the type is
 * UNKNOWN
 * </ul>
 * 
 * @author Christian Nentwich
 * @see net.sourceforge.nrl.parser.type.ITypeChecker
 */
public class ConstraintTypeChecker implements INRLConstraintDetailVisitor, ITypeChecker {

	// Each row in this array works as follows: the first entry is a data type
	// being assigned to; the remainder are the permissible types that can be
	// assigned to it
	private static Type[][] ASSIGNMENT_COMPATIBILITY = new Type[][] {
			new Type[] { Unknown, Boolean, Date, Decimal, Element, Integer, String },
			new Type[] { Boolean, Boolean, Unknown }, new Type[] { Date, Date, String, Unknown },
			new Type[] { Decimal, Decimal, Integer, String, Unknown },
			new Type[] { Element, Element, Unknown },
			new Type[] { Integer, Decimal, Integer, String, Unknown },
			new Type[] { String, Boolean, Date, Decimal, Integer, String, Unknown } };

	protected List<NRLError> errors = new ArrayList<NRLError>();

	protected List<ITypeMapping> typeMappings = new ArrayList<ITypeMapping>();

	// Flag that is flipped for constructs where implicit iteration model
	// references are allowed
	private boolean isImplicitIterationAllowed = false;

	public void addTypeMapping(ITypeMapping mapping) {
		typeMappings.add(mapping);
	}

	public List<NRLError> getErrors() {
		return errors;
	}

	/**
	 * Use the type mappings to look up the internal type for a model element. This tries them all
	 * in turn, until it finds once that returns a known type
	 * 
	 * @param element the element
	 * @return the type
	 */
	protected NRLDataType getType(IModelElement element) {
		for (int i = 0; i < typeMappings.size(); i++) {
			ITypeMapping mapping = typeMappings.get(i);

			// Try resolve this and all parents
			IModelElement run = element;
			while (run != null && run != AbstractModelElement.OBJECT) {
				NRLDataType type = mapping.getType(run);
				if (type.getType() != Unknown)
					return new NRLDataType(type);
				run = run.getParent();
			}
		}

		return NRLDataType.UNKNOWN;
	}

	public List<NRLError> check(IOperators operators) {
		errors = new ArrayList<NRLError>();

		for (IOperator op : operators.getOperators()) {
			if (op.getNRLReturnType().getType() == Unknown && op.getReturnType() != null) {
				op.setNRLReturnType(getType(op.getReturnType()));
				if (op.getNRLReturnType().getType() == Unknown) {
					op.setNRLReturnType(NRLDataType.ELEMENT);
				}
			}

			for (IParameter param : op.getParameters()) {
				if (param.getNRLDataType().getType() == Unknown && param.getType() != null) {
					param.setNRLDataType(getType(param.getType()));
					if (param.getNRLDataType().getType() == Unknown) {
						param.setNRLDataType(NRLDataType.ELEMENT);
					}
				}
			}
		}

		return errors;
	}

	public List<NRLError> check(IRuleFile ruleFile) {
		errors = new ArrayList<NRLError>();

		// Global variables

		for (IVariableDeclaration decl : ruleFile.getGlobalVariableDeclarations()) {
			decl.accept(new ConstraintVisitorDispatcher(this));
		}

		// Visit properties, in dependency order
		ValidationFragmentDependencyProcessor dependencyProc = new ValidationFragmentDependencyProcessor();
		dependencyProc.addDeclarations(ruleFile);

		List<NRLError> dependencyErrors = dependencyProc.resolve();
		errors.addAll(dependencyErrors);

		for (IDeclaration decl : dependencyProc.getOrderedDeclarations()) {
			decl.accept(new ConstraintVisitorDispatcher(this));
		}

		// Rule sets
		for (IRuleSetDeclaration decl : ruleFile.getRuleSetDeclarations()) {
			decl.accept(new ConstraintVisitorDispatcher(this));
		}

		checkRemainingDeclarations(ruleFile);

		return errors;
	}

	/**
	 * Check everything that is not:
	 * <ul>
	 * <li>a global variable
	 * <li>a property
	 * <li>a rule set
	 * </ul>
	 * 
	 * @param ruleFile the file
	 */
	protected void checkRemainingDeclarations(IRuleFile ruleFile) {
		// Now all others
		for (IDeclaration decl : ruleFile.getDeclarations()) {
			if (!(decl instanceof IValidationFragmentDeclaration)) {
				decl.accept(new ConstraintVisitorDispatcher(this));
			}
		}
	}

	/**
	 * Raise a type checking error on a construct. Adds the error to the error list.
	 * 
	 * @param construct the construct
	 * @param message the message
	 */
	protected void error(int status, INRLAstNode construct, String message) {
		errors
				.add(new SemanticError(status, construct.getLine(), construct.getColumn(), 1,
						message));
	}

	/**
	 * Check if two NRL types are assignment compatible.
	 * 
	 * @param assignedTo the type being assigned to
	 * @param assignedFrom the type being assigned from
	 * @return true if the types are assignment compatible, else false
	 */
	public static boolean isAssignmentCompatible(NRLDataType assignedTo, NRLDataType assignedFrom) {
		for (int i = 0; i < ASSIGNMENT_COMPATIBILITY.length; i++) {
			if (ASSIGNMENT_COMPATIBILITY[i][0] == assignedTo.getType()) {
				for (int j = 1; j < ASSIGNMENT_COMPATIBILITY[i].length; j++) {
					if (ASSIGNMENT_COMPATIBILITY[i][j] == assignedFrom.getType()) {
						return true;
					}
				}
				return false;
			}
		}
		return false;
	}

	/**
	 * Helper method to idenitfy if a type is a number (integer or decimal)
	 */
	public boolean isNumber(NRLDataType type) {
		return type.getType() == Decimal || type.getType() == Integer;
	}

	/**
	 * Is implicit iteration currently allowed?
	 * 
	 * @return flag
	 */
	public boolean isImplicitIterationAllowed() {
		return isImplicitIterationAllowed;
	}

	/**
	 * Set the implicit iteration status
	 * 
	 * @param isImplicitIterationAllowed flag
	 */
	public void setImplicitIterationAllowed(boolean isImplicitIterationAllowed) {
		this.isImplicitIterationAllowed = isImplicitIterationAllowed;
	}

	public void visitArithmeticExpressionAfter(IArithmeticExpression expr) {
		if (expr.getLeft().getNRLDataType().getType() == Unknown
				|| expr.getRight().getNRLDataType().getType() == Unknown) {
			expr.setNRLDataType(NRLDataType.UNKNOWN);
		} else if (expr.getLeft().getNRLDataType().isCollection()
				|| expr.getRight().getNRLDataType().isCollection()) {
			error(IStatusCode.ARITHMETIC_EXPRESSION_ARGS_INVALID, expr,
					"Cannot perform arithmetic on collections");
		} else if (expr.getLeft().getNRLDataType().getType() == Date
				|| expr.getRight().getNRLDataType().getType() == Date) {

			if (expr.getOperator() != IArithmeticExpression.Operator.PLUS) {
				error(IStatusCode.ARITHMETIC_EXPRESSION_ARGS_INVALID, expr,
						"Only addition is supported: for dates and numbers, "
								+ "or dates and strings");
			} else {
				NRLDataType other = expr.getLeft().getNRLDataType();
				if (expr.getLeft().getNRLDataType().getType() == Date)
					other = expr.getRight().getNRLDataType();

				if (other.getType() == String) {
					expr.setNRLDataType(NRLDataType.STRING);
				} else if (other.getType() == Integer) {
					expr.setNRLDataType(NRLDataType.DATE);
				} else {
					error(IStatusCode.ARITHMETIC_EXPRESSION_ARGS_INVALID, expr,
							"Only strings or integers can be added to dates");
				}
			}
		} else if (expr.getOperator() == IArithmeticExpression.Operator.PLUS
				&& (!isNumber(expr.getLeft().getNRLDataType()) || !isNumber(expr.getRight()
						.getNRLDataType()))) {
			// TODO This does not really do a "deep" check, it just sets the
			// result as string if either operand is not a number. Expressions
			// like a+(b+(c+d)) need a recursive check to see if one operand
			// is a string
			expr.setNRLDataType(NRLDataType.STRING);
		} else if (!isNumber(expr.getLeft().getNRLDataType())
				|| !isNumber(expr.getRight().getNRLDataType())) {
			// One of them is not a number? Illegal
			error(IStatusCode.ARITHMETIC_EXPRESSION_ARGS_INVALID, expr,
					"Arithmetic can only be performed on numbers or for string concatenation");
			expr.setNRLDataType(NRLDataType.DECIMAL);
		} else if (expr.getOperator() == IArithmeticExpression.Operator.MOD) {
			// Check modulo arithmetic
			if (expr.getRight().getNRLDataType().getType() != Integer) {
				error(IStatusCode.ARITHMETIC_EXPRESSION_ARGS_INVALID, expr,
						"Right hand side of modulo division must be an integer");
			}
			expr.setNRLDataType(NRLDataType.INTEGER);
		} else if (expr.getLeft().getNRLDataType().getType() == Decimal
				|| expr.getRight().getNRLDataType().getType() == Decimal) {
			// Both are numbers are this stage. Either decimal? Result is
			// decimal
			expr.setNRLDataType(NRLDataType.DECIMAL);
		} else {
			// Else result must be integer
			expr.setNRLDataType(NRLDataType.INTEGER);
		}
	}

	public void visitBinaryOperatorStatementAfter(IBinaryOperatorStatement statement) {
		Type leftType = statement.getLeft().getNRLDataType().getType();
		Type rightType = statement.getRight().getNRLDataType().getType();

		if ((leftType != Boolean && leftType != Unknown)
				|| (rightType != Boolean && rightType != Unknown)) {
			error(IStatusCode.BINARY_OPERATOR_ARGUMENTS_NOT_BOOLEAN, statement,
					"Boolean expressions need Boolean arguments");
		}

		statement.setNRLDataType(NRLDataType.BOOLEAN);
	}

	public void visitBinaryPredicateAfter(IBinaryPredicate predicate) {
		// Outcome is always boolean
		predicate.setNRLDataType(NRLDataType.BOOLEAN);

		Type ltype = predicate.getLeft().getNRLDataType().getType();
		Type rtype = predicate.getRight().getNRLDataType().getType();

		if (predicate.getLeft() instanceof IModelReference
				&& predicate.getRight() instanceof IModelReference) {
			IModelReference leftReference = (IModelReference) predicate.getLeft();
			IModelReference rightReference = (IModelReference) predicate.getRight();
			IClassifier leftTarget = (IClassifier) leftReference.getTarget();
			IClassifier rightTarget = (IClassifier) rightReference.getTarget();

			if (leftTarget != null && rightTarget != null) {
				if (leftTarget.isEnumeration() && rightTarget.isEnumeration()) {
					if (!leftTarget.isAssignableFrom(rightTarget)
							&& !rightTarget.isAssignableFrom(leftTarget)) {
						error(IStatusCode.BINARY_PREDICATE_ARGUMENT_INCOMPATIBLE, predicate,
								"Enumeration values can only be compared to the same enumeration type.");
					} else
						return;
				}
			}
		}

		if (ltype == Unknown || rtype == Unknown) {
			// Permitted - do nothing
		} else if (predicate.getLeft().getNRLDataType().isCollection()
				|| predicate.getRight().getNRLDataType().isCollection()) {
			error(IStatusCode.BINARY_PREDICATE_ARGUMENT_COLLECTION, predicate,
					"Equality comparisons must not be used for collections - is your type mapping complete?");
		} else if (ltype == Element || rtype == Element) {
			error(IStatusCode.BINARY_PREDICATE_ARGUMENT_COMPLEX, predicate,
					"Equality comparisons must not be used for complex elements - check your type mapping.");
		} else if ((ltype == Date && rtype != Date && rtype != String)
				|| (rtype == Date && ltype != Date && ltype != String)) {
			error(IStatusCode.BINARY_PREDICATE_ARGUMENT_INCOMPATIBLE, predicate,
					"Dates can only be compared to dates or strings");
		} else if ((isNumber(predicate.getLeft().getNRLDataType()) && !isNumber(predicate
				.getRight().getNRLDataType()))
				|| (isNumber(predicate.getRight().getNRLDataType()) && !isNumber(predicate
						.getLeft().getNRLDataType()))) {
			error(IStatusCode.BINARY_PREDICATE_ARGUMENT_INCOMPATIBLE, predicate,
					"Numbers can only be compared to numbers");
		} else if (predicate.getLeft().getNRLDataType().isEnumeration() != predicate.getRight()
				.getNRLDataType().isEnumeration()
				&& ltype != rtype) {
			error(IStatusCode.BINARY_PREDICATE_ARGUMENT_INCOMPATIBLE, predicate,
					"Enumeration attributes can only be compared to enumeration "
							+ "literals or values of the same type");
		}
	}

	public void visitBooleanLiteral(IBooleanLiteral bool) {
		bool.setNRLDataType(NRLDataType.BOOLEAN);
	}

	public void visitCardinalityConstraint(ICardinalityConstraint constraint) {
	}

	public void visitCollectionIndexAfter(ICollectionIndex index) {
		if (!index.getCollection().getNRLDataType().isCollection()) {
			error(IStatusCode.COLLECTION_EXPECTED, index.getCollection(),
					"Collection attribute reference expected");
			return;
		}

		NRLDataType result = new NRLDataType(index.getCollection().getNRLDataType());
		result.setCollection(false);
		index.setNRLDataType(result);
	}

	public void visitCompoundReportAfter(ICompoundReport report) {

	}

	public void visitConcatenatedReportAfter(IConcatenatedReport report) {

	}

	public void visitConditionalReportAfter(IConditionalReport report) {
		if (report.getCondition().getNRLDataType().getType() != Boolean) {
			error(IStatusCode.IF_ARGUMENTS_NOT_BOOLEAN, report.getCondition(),
					"If expression must return a Boolean expression");
		}
	}

	public void visitCastExpressionAfter(ICastExpression expr) {
		IModelElement source = expr.getReference().getTarget();

		if (expr.getReference().getInitialStepType() == IModelReference.STEP_VARIABLE
				&& !((IVariable) expr.getReference().getInitialStep()).isBoundToElement()) {
			error(IStatusCode.CAST_REQUIRES_SUBTYPE, expr,
					"Cannot cast variables that have been assigned a complex expression");
		}

		if (source != null && !source.isAssignableFrom(expr.getTargetType())) {
			error(IStatusCode.CAST_REQUIRES_SUBTYPE, expr, "Cannot convert from "
					+ source.getName() + " to " + expr.getTargetType().getName()
					+ ": not a sub-type.");
		}

		IModelReference ref = expr.getReference();
		if (ref.getInitialStep() instanceof IAttribute) {
			IAttribute attr = (IAttribute) ref.getInitialStep();
			if (attr.getMaxOccurs() > 1 || attr.getMaxOccurs() == IAttribute.UNBOUNDED) {
				error(IStatusCode.CANNOT_CAST_COLLECTION, expr,
						"Cannot cast a collection, has to be a single attribute");
			}
		}

		for (IAttribute attr : ref.getRemainingSteps()) {
			if (attr.getMaxOccurs() > 1 || attr.getMaxOccurs() == IAttribute.UNBOUNDED) {
				error(IStatusCode.CANNOT_CAST_COLLECTION, expr,
						"Cannot cast a collection, has to be a single attribute");
			}
		}

		expr.setNRLDataType(NRLDataType.ELEMENT);
	}

	public void visitExistsStatementAfter(IExistsStatement exists) {
		if (exists.getElement() != null) {
			if (exists.getConstraint() != null
					&& !exists.getElement().getNRLDataType().isCollection()) {
				error(IStatusCode.COLLECTION_EXPECTED, exists,
						"Reference to a collection attribute expected");
			}
		}

		if (exists.getConstraint() != null
				&& exists.getConstraint().getNRLDataType().getType() != Boolean) {

			// Update: single model references are allowed, and are cast to
			// boolean in mappings
			if (!(exists.getConstraint() instanceof IModelReference)) {
				error(IStatusCode.QUANTIFIER_ARGUMENT_NOT_BOOLEAN, exists,
						"Boolean condition expected in collection check");
			}
		}

		exists.setNRLDataType(NRLDataType.BOOLEAN);
	}

	public void visitForallStatementAfter(IForallStatement forall) {
		if (forall.getElement() != null) {
			if (!forall.getElement().getNRLDataType().isCollection()) {
				error(IStatusCode.COLLECTION_EXPECTED, forall,
						"Reference to a collection attribute expected");
			}
		}
		if (forall.getConstraint() != null
				&& forall.getConstraint().getNRLDataType().getType() != Boolean) {

			// Update: single model references are allowed, and are cast to
			// boolean in mappings
			if (!(forall.getConstraint() instanceof IModelReference)) {
				error(IStatusCode.QUANTIFIER_ARGUMENT_NOT_BOOLEAN, forall,
						"Boolean condition expected in collection check");
			}
		}

		forall.setNRLDataType(NRLDataType.BOOLEAN);
	}

	public boolean visitFunctionalExpressionBefore(IFunctionalExpression expr) {

		switch (expr.getFunction()) {
		case SUMOF:
			setImplicitIterationAllowed(true);
			break;
		}

		return true;
	}

	public void visitFunctionalExpressionAfter(IFunctionalExpression expr) {
		// TODO check 'unique' parameter

		switch (expr.getFunction()) {
		case NUMBER_OF: {
			IModelReference param = (IModelReference) expr.getParameters().iterator().next();
			if (!param.getNRLDataType().isCollection()) {
				error(IStatusCode.COLLECTION_EXPECTED, expr,
						"\"Number of\" expects a collection argument");
			}
			expr.setNRLDataType(NRLDataType.INTEGER);
			break;
		}
		case SUMOF:
			IModelReference param = (IModelReference) expr.getParameters().iterator().next();

			if (param.getRemainingSteps().size() == 0) {
				error(IStatusCode.NUMBER_EXPECTED, param,
						"\"Sum of\" to point to a number inside a collection");
			} else {
				// Check that one of the steps (except the last) is a collection
				boolean foundCollection = false;

				IAttribute firstStep = null;
				if (param.getInitialStepType() == IModelReference.STEP_ATTRIBUTE)
					firstStep = (IAttribute) param.getInitialStep();

				if (firstStep != null
						&& (firstStep.getMaxOccurs() > 1 || firstStep.getMaxOccurs() == IAttribute.UNBOUNDED)) {
					foundCollection = true;
				}

				if (!foundCollection) {
					for (int i = 0; i < param.getRemainingSteps().size() - 1; i++) {
						IAttribute attr = param.getRemainingSteps().get(i);
						if (attr.getMaxOccurs() > 1 || attr.getMaxOccurs() == IAttribute.UNBOUNDED) {
							foundCollection = true;
							break;
						}
					}
				}

				if (!foundCollection) {
					error(IStatusCode.COLLECTION_EXPECTED, param, "One of the steps in the sum of "
							+ "expression must be a collection");
				} else {
					IAttribute lastStep = param.getRemainingSteps().get(
							param.getRemainingSteps().size() - 1);
					NRLDataType type = getType(lastStep.getType());

					if (type.getType() != Decimal && type.getType() != Integer) {
						error(IStatusCode.NUMBER_EXPECTED, param, "The sum of target, '"
								+ lastStep.getName() + "', needs to be a number");
					}
				}
			}

			expr.setNRLDataType(NRLDataType.DECIMAL);
			break;
		}

		setImplicitIterationAllowed(false);
	}

	public void visitGlobalExistsStatementAfter(IGlobalExistsStatement exists) {
		if (exists.getConstraint().getNRLDataType().getType() != Boolean) {
			error(IStatusCode.QUANTIFIER_ARGUMENT_NOT_BOOLEAN, exists.getConstraint(),
					"Boolean expression expected");
		}
		exists.setNRLDataType(NRLDataType.BOOLEAN);
	}

	public void visitIfThenStatementAfter(IIfThenStatement ifThen) {
		// Check if, then and else (if present). They all must be boolean!
		if (ifThen.getIf().getNRLDataType().getType() != Boolean) {
			error(IStatusCode.IF_ARGUMENTS_NOT_BOOLEAN, ifThen,
					"The 'if' part of an if-statement needs a Boolean condition");
		}

		if (ifThen.getElse() != null) {
			if (!ifThen.getThen().getNRLDataType().equals(ifThen.getElse().getNRLDataType())) {
				error(IStatusCode.IF_STATEMENT_MULTIPLE_TYPES, ifThen,
						"The 'then' and 'else' part of this statement must return the same type of result");
			}
		}

		ifThen.setNRLDataType(ifThen.getThen().getNRLDataType());
	}

	/*
	 * Helper method used by "is in" and "is not in"
	 */
	protected void visitIsInList(List<IIdentifier> list, IExpression expression,
			NRLDataType exprType) {
		for (IIdentifier id : list) {
			NRLDataType idType = id.getNRLDataType();
			if (idType.getType() == Unknown)
				continue;

			// There is some repetition here between the binary predicate check
			// and this, but the error messages and status codes differ. Could
			// probably be refactored
			if (id instanceof IModelReference && expression instanceof IModelReference) {
				IModelReference idReference = (IModelReference) id;
				IModelReference exprReference = (IModelReference) expression;

				if (!exprReference.getTarget().isAssignableFrom(idReference.getTarget())) {
					error(IStatusCode.IS_IN_EXPRESSION_INCOMPATIBLE, expression,
							"Enumeration values can only be compared to the same enumeration type.");
				} else
					continue;
			}

			if (idType.isCollection()) {
				error(IStatusCode.IS_IN_LIST_ENTRY_INVALID, id,
						"Collections not allowed in the list of values");
			} else if (idType.getType() == Element && !idType.isEnumeration()) {
				error(IStatusCode.IS_IN_LIST_ENTRY_INVALID, id,
						"Complex elements not allowed in the list of values");
			} else if ((exprType.getType() == Date && idType.getType() != Date && idType.getType() != String)
					|| (idType.getType() == Date && exprType.getType() != Date && exprType
							.getType() != String)) {
				error(IStatusCode.IS_IN_EXPRESSION_TYPE_MISMATCH, id,
						"Dates can only be compared to dates or strings");
			} else if ((isNumber(idType) && !isNumber(exprType))
					|| (isNumber(exprType) && !isNumber(idType))) {
				error(IStatusCode.IS_IN_EXPRESSION_TYPE_MISMATCH, id,
						"Numbers can only be compared to numbers");
			} else if (idType.isEnumeration() != exprType.isEnumeration()
					&& exprType.getType() != idType.getType()) {
				error(IStatusCode.BINARY_PREDICATE_ARGUMENT_INCOMPATIBLE, id,
						"Enumeration attributes can only be compared to enumeration "
								+ "literals or values of the same type");
			}

		}
	}

	public void visitIsInPredicateAfter(IIsInPredicate isIn) {
		NRLDataType exprType = isIn.getExpression().getNRLDataType();

		if (exprType.isCollection() || exprType.getType() == Element && !exprType.isEnumeration()) {
			error(IStatusCode.IS_IN_EXPRESSION_INCOMPATIBLE, isIn,
					"'is one of' requires an expression that has a value as its first parameter");
		}
		if (exprType.getType() != Unknown) {
			visitIsInList(isIn.getList(), isIn.getExpression(), exprType);
		}

		isIn.setNRLDataType(NRLDataType.BOOLEAN);
	}

	public void visitIsNotInPredicateAfter(IIsNotInPredicate isNotIn) {
		NRLDataType exprType = isNotIn.getExpression().getNRLDataType();

		if (exprType.isCollection() || exprType.getType() == Element && !exprType.isEnumeration()) {
			error(IStatusCode.IS_IN_EXPRESSION_INCOMPATIBLE, isNotIn,
					"'is not one of' requires an expression that has a value as its first parameter");
		}
		if (exprType.getType() != Unknown) {
			visitIsInList(isNotIn.getList(), isNotIn.getExpression(), exprType);
		}

		isNotIn.setNRLDataType(NRLDataType.BOOLEAN);
	}

	public void visitIsSubtypePredicateAfter(IIsSubtypePredicate subType) {
		IModelElement check = subType.getReference().getTarget();

		if (subType.getReference().getInitialStepType() == IModelReference.STEP_VARIABLE
				&& !((IVariable) subType.getReference().getInitialStep()).isBoundToElement()) {
			error(IStatusCode.CAST_REQUIRES_SUBTYPE, subType,
					"Cannot cast variables that have been assigned a complex expression");
		}

		if (subType.getReference().getNRLDataType().isCollection()) {
			error(IStatusCode.CAST_COLLECTION, subType.getReference(),
					"Cannot check collection types");
		}

		if (check != null && !check.isAssignableFrom(subType.getTargetType())) {
			error(IStatusCode.CAST_REQUIRES_SUBTYPE, subType, "Cannot convert from "
					+ check.getName() + " to " + subType.getTargetType().getName()
					+ ": not a sub-type.");
		}

		subType.setNRLDataType(NRLDataType.BOOLEAN);
	}

	public void visitLiteralString(ILiteralString literal) {
		literal.setNRLDataType(NRLDataType.STRING);
	}

	public void visitModelReferenceAfter(IModelReference ref) {
		// The type is determing by trying to look at the final attribute step
		// If it's a multiple, it's a collection. Otherwise, it's whatever the
		// type of the attribute maps to

		ref.setNRLDataType(NRLDataType.UNKNOWN);

		IAttribute lastAttribute = null;
		Object target = null;

		if (ref.getRemainingSteps().size() > 0) {
			lastAttribute = ref.getRemainingSteps().get(ref.getRemainingSteps().size() - 1);
		} else {
			if (ref.getInitialStep() instanceof IAttribute)
				lastAttribute = (IAttribute) ref.getInitialStep();
			else if (ref.getInitialStep() instanceof IVariable) {
				IVariable var = (IVariable) ref.getInitialStep();

				if (var.getNRLDataType().getType() != Unknown) {
					ref.setNRLDataType(var.getNRLDataType());
					return;
				}

				if (var.isBoundToElement()) {
					target = var.getBoundElement();
				} else {
					ref.setNRLDataType(new NRLDataType(var.getBoundExpression().getNRLDataType()));
					return;
				}
			}
		}

		if (target == null)
			target = ref.getTarget();

		boolean collection = false;
		if (lastAttribute != null
				&& (lastAttribute.getMaxOccurs() == IAttribute.UNBOUNDED || lastAttribute
						.getMaxOccurs() > 1)) {
			collection = true;
		}

		if (target instanceof IModelElement) {
			IModelElement targetElement = (IModelElement) target;
			NRLDataType type = new NRLDataType(getType(targetElement));

			if (type.getType() != Unknown) {
				if (targetElement instanceof IClassifier && type.getType() != Unknown
						&& ((IClassifier) targetElement).isEnumeration()) {
					type.setEnumeration(true);
				}
				type.setCollection(collection);
				ref.setNRLDataType(type);
			} else if (target instanceof IClassifier && !(target instanceof IDataType)) {
				type = new NRLDataType(Element);
				type.setCollection(collection);
				if (((IClassifier) target).isEnumeration())
					type.setEnumeration(true);
				ref.setNRLDataType(type);
			}
		}

		// Unknown data types illegal since release 1.3
		if (ref.getNRLDataType().getType() == Unknown) {
			if (target instanceof IModelElement) {
				error(IStatusCode.UNKNOWN_DATATYPE, ref, "The NRL type of '"
						+ ref.getOriginalString() + "' is unknown. Model type is '"
						+ ((IModelElement) target).getQualifiedName()
						+ "'. Please check your type mapping.");
			} else {
				error(IStatusCode.UNKNOWN_DATATYPE, ref, "The NRL type of '"
						+ ref.getOriginalString() + "' is unknown. Please check your type mapping.");
			}
		}

		// Check for implicit iteration
		if (!isImplicitIterationAllowed()) {
			// First step occurs > 1, plus additional steps?
			if (ref.getInitialStepType() == IModelReference.STEP_ATTRIBUTE) {
				IAttribute attr = (IAttribute) ref.getInitialStep();
				if ((attr.getMaxOccurs() > 1 || attr.getMaxOccurs() == IAttribute.UNBOUNDED)
						&& ref.getRemainingSteps().size() > 0) {
					error(IStatusCode.IMPLICIT_ITERATION, ref, "Model reference not unique: '"
							+ attr.getName() + "' is a collection. Use quantifier/for-each?");
				}
			}

			// Step somewhere in the middle has maxOccurs > 1
			if (ref.getRemainingSteps().size() > 1) {
				for (int i = 0; i < ref.getRemainingSteps().size() - 1; i++) {
					IAttribute attr = ref.getRemainingSteps().get(i);
					if ((attr.getMaxOccurs() > 1 || attr.getMaxOccurs() == IAttribute.UNBOUNDED)
							&& ref.getRemainingSteps().size() > 0) {
						error(IStatusCode.IMPLICIT_ITERATION, ref,
								"Model reference not unique: step '" + attr.getName()
										+ "' is a collection. Use quantifier/for-each?");
					}
				}
			}
		}
	}

	public void visitMultipleExistsStatementAfter(IMultipleExistsStatement statement) {
		for (IModelReference ref : statement.getModelReferences()) {
			if (ref.getReferenceType() == IModelReference.REFERENCE_ELEMENT) {
				error(IStatusCode.ILLEGAL_ELEMENT_REFERENCE, ref,
						"Model element reference not allowed here, only attributes");
			} else if (ref.getReferenceType() == IModelReference.REFERENCE_STATIC_ATTRIBUTE) {
				error(IStatusCode.ILLEGAL_STATIC_REFERENCE, ref,
						"Enumerations and static attributes not allowed here, only normal attributes");
			}
		}

		statement.setNRLDataType(NRLDataType.BOOLEAN);
	}

	public void visitMultipleNotExistsStatementAfter(IMultipleNotExistsStatement statement) {
		for (IModelReference ref : statement.getModelReferences()) {
			if (ref.getReferenceType() == IModelReference.REFERENCE_ELEMENT) {
				error(IStatusCode.ILLEGAL_ELEMENT_REFERENCE, ref,
						"Model element reference not allowed here, only attributes");
			} else if (ref.getReferenceType() == IModelReference.REFERENCE_STATIC_ATTRIBUTE) {
				error(IStatusCode.ILLEGAL_STATIC_REFERENCE, ref,
						"Enumerations and static attributes not allowed here, only normal attributes");
			}
		}

		statement.setNRLDataType(NRLDataType.BOOLEAN);
	}

	public void visitNotExistsStatementAfter(INotExistsStatement exists) {
		exists.setNRLDataType(NRLDataType.BOOLEAN);
	}

	public void visitDecimalNumber(IDecimalNumber number) {
		number.setNRLDataType(NRLDataType.DECIMAL);
	}

	public void visitIntegerNumber(IIntegerNumber number) {
		number.setNRLDataType(NRLDataType.INTEGER);
	}

	/*
	 * Basic operator and parameter checks that can be shared between this and subclasses.
	 */
	protected void visitOperatorParameters(IOperator operator, INRLAstNode operatorNode,
			List<IExpression> parameters) {
		if (operator.getReturnType() == null) {
			error(IStatusCode.OPERATOR_TYPE_UNKNOWN, operatorNode, "Operator '"
					+ operator.getName() + "' has no return type");
		}

		if (parameters.size() != operator.getParameters().size()) {
			error(IStatusCode.OPERATOR_PARAMETER_MISMATCH, operatorNode, "Operator '"
					+ operator.getName() + "' expects " + operator.getParameters().size()
					+ ", you specified " + parameters.size());
		} else {
			int count = 0;
			for (IExpression paramExpr : parameters) {
				IParameter param = operator.getParameters().get(count);
				count++;

				if (!paramExpr.getNRLDataType().isCollection() && param.isTypeCollection()) {
					error(IStatusCode.OPERATOR_COLLECTION_PARAMETER, paramExpr, "Parameter '"
							+ param.getName() + "' of operator '" + operator.getName()
							+ "' is expecting a collection.");
				} else if (paramExpr.getNRLDataType().isCollection() && !param.isTypeCollection()) {
					error(IStatusCode.OPERATOR_COLLECTION_PARAMETER, paramExpr, "Parameter '"
							+ param.getName() + "' of operator '" + operator.getName()
							+ "' cannot accept a collection.");
				} else if (param.isTypeCollection()
						&& paramExpr.getNRLDataType().getType() != Element
						&& param.getNRLDataType().getType() != Element
						&& paramExpr.getNRLDataType().getType() != param.getNRLDataType().getType()) {
					error(IStatusCode.OPERATOR_TYPE_MISMATCH, paramExpr, "Parameter '"
							+ param.getName() + "' of operator '" + operator.getName()
							+ "' expects a collection NRL type '"
							+ param.getNRLDataType().getType() + "': found '"
							+ paramExpr.getNRLDataType().getType() + "'");
				} else if (param.getType() == null) {
					error(IStatusCode.OPERATOR_TYPE_UNKNOWN, paramExpr, "Parameter '"
							+ param.getName() + "' of operator '" + operator.getName()
							+ "' has no type assigned");
				} else if (!isAssignmentCompatible(param.getNRLDataType(), paramExpr
						.getNRLDataType())) {

					if (paramExpr.getNRLDataType().getType() == Element) {
						String exprType = paramExpr.getNRLDataType().toString();
						if (paramExpr instanceof IModelReference
								&& ((IModelReference) paramExpr).getTarget() != null)
							exprType = ((IModelReference) paramExpr).getTarget().getName();
						error(IStatusCode.OPERATOR_TYPE_MISMATCH, paramExpr,
								"Type mismatch for parameter '" + param.getName()
										+ "' of operator '" + operator.getName() + "': '"
										+ exprType + "' cannot be converted to '"
										+ param.getNRLDataType().toString()
										+ "'. Please check type mapping.");
					} else {
						error(IStatusCode.OPERATOR_TYPE_MISMATCH, paramExpr,
								"Type mismatch for parameter '" + param.getName()
										+ "' of operator '" + operator.getName() + "': '"
										+ paramExpr.getNRLDataType() + "' cannot be converted to '"
										+ param.getNRLDataType() + "'. Please check type mapping.");
					}
				} else if (param.getNRLDataType().getType() == Element
						&& paramExpr.getNRLDataType().getType() == Element
						&& paramExpr instanceof IModelReference
						&& ((IModelReference) paramExpr).getTarget() != null
						&& !param.getType().isAssignableFrom(
								((IModelReference) paramExpr).getTarget())) {
					error(IStatusCode.OPERATOR_TYPE_MISMATCH, paramExpr,
							"Type mismatch for parameter '" + param.getName() + "' of operator '"
									+ operator.getName() + "': cannot convert "
									+ ((IModelReference) paramExpr).getTarget().getName() + " to "
									+ param.getType().getName());
				}
			}
		}

	}

	public void visitOperatorInvocationAfter(IOperatorInvocation op) {
		if (op.getOperator() == null) {
			op.setNRLDataType(NRLDataType.UNKNOWN);
			return;
		}

		List<IExpression> params = new ArrayList<IExpression>();
		for (int i = 0; i < op.getNumParameters(); i++) {
			params.add(op.getParameter(i));
		}
		visitOperatorParameters(op.getOperator(), op, params);

		if (op.getOperator().getNRLReturnType().getType() != Unknown) {
			op.setNRLDataType(op.getOperator().getNRLReturnType());
		} else if (op.getOperator().getReturnType() != null) {
			op.setNRLDataType(getType(op.getOperator().getReturnType()));

			if (op.getNRLDataType().getType() == Unknown) {
				op.setNRLDataType(NRLDataType.ELEMENT);
			}
		}

		if (op.getNRLDataType().getType() == Type.Void) {
			error(IStatusCode.VOID_OPERATOR_IN_EXPRESSION, op, "Operator '" + op.getOperatorName()
					+ "' has a type of 'void' and cannot be used in expressions");
		}
	}

	public void visitSelectionExpressionAfter(ISelectionExpression expr) {
		if (expr.getConstraint().getNRLDataType().getType() != Type.Boolean) {
			error(IStatusCode.SELECTION_CONSTRAINT_NOT_BOOLEAN, expr.getConstraint(),
					"The constraint of a selection expression must be Boolean");
			return;
		}

		if (!expr.getModelReference().getNRLDataType().isCollection()) {
			error(IStatusCode.COLLECTION_EXPECTED, expr.getModelReference(),
					"Selection can only be performed on collections");
			return;
		}

		if (expr.isSingleElementSelection()) {
			NRLDataType resultType = new NRLDataType(expr.getModelReference().getNRLDataType());
			resultType.setCollection(false);
			expr.setNRLDataType(resultType);
		} else {
			expr.setNRLDataType(new NRLDataType(expr.getModelReference().getNRLDataType()));
		}
	}

	public void visitValidationFragmentApplicationAfter(IValidationFragmentApplication app) {
		IValidationFragmentDeclaration decl = app.getFragment();

		if (decl.getContextNames().size() != app.getNumParameters()) {
			error(IStatusCode.FRAGMENT_PARAMETER_MISMATCH, app, "Property \"" + decl.getId()
					+ "\" requires " + decl.getContextNames().size() + " arguments, found "
					+ app.getNumParameters() + " here");
		} else {
			int count = 0;

			for (String param : decl.getContextNames()) {
				IModelElement declaredType = decl.getContextType(param);
				NRLDataType declaredNRLType = getType(declaredType);

				IExpression passed = app.getParameter(count);

				// Declared type is an element?
				if (declaredNRLType.getType() == Unknown || declaredNRLType.getType() == Element) {

					// Must be subclass if both are elements
					if (passed instanceof IModelReference) {
						if (!declaredType.isAssignableFrom(((IModelReference) passed).getTarget())) {
							error(IStatusCode.FRAGMENT_PARAMETER_MISMATCH, passed,
									"Property argument of type "
											+ ((IModelReference) passed).getTarget().getName()
											+ " is not compatible with expected type "
											+ declaredType.getName());
						}
					}
				}

				count++;
			}
		}

		app.setNRLDataType(app.getFragment().getNRLDataType());
	}

	public void visitValidationFragmentDeclarationAfter(IValidationFragmentDeclaration decl) {
		if (decl.getConstraint().getNRLDataType().isCollection()
				|| decl.getConstraint().getNRLDataType().getType() == Element) {
			error(IStatusCode.FRAGMENT_RESULT_COMPLEX, decl,
					"A property must have a simple type result");
		}

		((ValidationFragmentDeclarationImpl) decl).setNRLDataType(decl.getConstraint()
				.getNRLDataType());
	}

	public void visitConstraintRuleDeclarationAfter(IConstraintRuleDeclaration decl) {
		// Rule declarations must result in a boolean
		if (decl.getConstraint().getNRLDataType().getType() != Boolean) {
			error(IStatusCode.RULE_NOT_BOOLEAN, decl, "Rule " + decl.getId()
					+ " is not a Boolean assertion");
		}

		for (IVariableDeclaration variableDeclaration : decl.getVariableDeclarations()) {
			if (variableDeclaration.getExpression() instanceof IModelReference) {
				IModelReference ref = (IModelReference) variableDeclaration.getExpression();
				if (ref.getReferenceType() == IModelReference.REFERENCE_ELEMENT) {
					error(IStatusCode.ILLEGAL_ELEMENT_REFERENCE, variableDeclaration, format(
							"Variable %s is assigned to an element. This is not "
									+ "allowed in constraint rule variable declarations.",
							variableDeclaration.getVariableName()));
				}
			}
		}
	}

	public void visitRuleFileAfter(IRuleFile file) {
	}

	public void visitRuleSetDeclarationAfter(IRuleSetDeclaration decl) {
		// Rule declarations must result in a boolean
		if (decl.getPreconditionConstraint() == null)
			return;

		if (decl.getPreconditionConstraint().getNRLDataType().getType() != Boolean) {
			// Update: single model references are allowed, and are cast to
			// boolean in mappings
			if (!(decl.getPreconditionConstraint() instanceof IModelReference)) {
				error(IStatusCode.RULESET_PRECONDITION_NOT_BOOLEAN, decl, "Rule set "
						+ decl.getId() + " precondition is not a Boolean assertion");
			}
		}
	}

	public boolean visitVariableDeclarationBefore(IVariableDeclaration decl) {
		decl.getExpression().accept(new ConstraintVisitorDispatcher(this));
		decl.getVariableReference().setNRLDataType(decl.getExpression().getNRLDataType());
		return false;
	}

	public void visitVariableDeclarationAfter(IVariableDeclaration decl) {
		if (decl.getExpression() != null) {
			decl.setNRLDataType(decl.getExpression().getNRLDataType());
		}
	}

	// ---------------------------------------------------------------------
	// The BEFORE methods all return true and don't do anything, just
	// forward processing to children
	// ---------------------------------------------------------------------

	public boolean visitArithmeticExpressionBefore(IArithmeticExpression expr) {
		return true;
	}

	public boolean visitBinaryOperatorStatementBefore(IBinaryOperatorStatement statement) {
		return true;
	}

	public boolean visitBinaryPredicateBefore(IBinaryPredicate predicate) {
		return true;
	}

	public boolean visitCastExpressionBefore(ICastExpression expr) {
		return true;
	}

	public boolean visitCollectionIndexBefore(ICollectionIndex index) {
		return true;
	}

	public boolean visitCompoundReportBefore(ICompoundReport report) {
		return true;
	}

	public boolean visitConcatenatedReportBefore(IConcatenatedReport report) {
		return true;
	}

	public boolean visitConditionalReportBefore(IConditionalReport report) {
		return true;
	}

	public boolean visitExistsStatementBefore(IExistsStatement exists) {
		return true;
	}

	public boolean visitForallStatementBefore(IForallStatement forall) {
		return true;
	}

	public boolean visitGlobalExistsStatementBefore(IGlobalExistsStatement exists) {
		return true;
	}

	public boolean visitIfThenStatementBefore(IIfThenStatement ifThen) {
		return true;
	}

	public boolean visitIsInPredicateBefore(IIsInPredicate isIn) {
		return true;
	}

	public boolean visitIsNotInPredicateBefore(IIsNotInPredicate isNotIn) {
		return true;
	}

	public boolean visitIsSubtypePredicateBefore(IIsSubtypePredicate subType) {
		return true;
	}

	public boolean visitModelReferenceBefore(IModelReference ref) {
		return true;
	}

	public boolean visitMultipleExistsStatementBefore(IMultipleExistsStatement statement) {
		return true;
	}

	public boolean visitMultipleNotExistsStatementBefore(IMultipleNotExistsStatement statement) {
		return true;
	}

	public boolean visitNotExistsStatementBefore(INotExistsStatement exists) {
		return true;
	}

	public boolean visitOperatorInvocationBefore(IOperatorInvocation op) {
		return true;
	}

	public boolean visitValidationFragmentApplicationBefore(IValidationFragmentApplication app) {
		return true;
	}

	public boolean visitValidationFragmentDeclarationBefore(IValidationFragmentDeclaration decl) {
		return true;
	}

	public boolean visitConstraintRuleDeclarationBefore(IConstraintRuleDeclaration decl) {
		return true;
	}

	public boolean visitRuleFileBefore(IRuleFile file) {
		return true;
	}

	public boolean visitRuleSetDeclarationBefore(IRuleSetDeclaration decl) {
		return true;
	}

	public boolean visitSelectionExpressionBefore(ISelectionExpression expr) {
		return true;
	}
}
