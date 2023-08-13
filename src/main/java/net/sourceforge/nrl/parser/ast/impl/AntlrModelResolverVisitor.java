package net.sourceforge.nrl.parser.ast.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import net.sourceforge.nrl.parser.IStatusCode;
import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.SemanticError;
import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.INRLAstNode;
import net.sourceforge.nrl.parser.ast.IVariable;
import net.sourceforge.nrl.parser.ast.Variable;
import net.sourceforge.nrl.parser.ast.action.ICompoundAction;
import net.sourceforge.nrl.parser.ast.action.IVariableDeclarationAction;
import net.sourceforge.nrl.parser.ast.action.impl.CreateActionImpl;
import net.sourceforge.nrl.parser.ast.action.impl.ForEachActionImpl;
import net.sourceforge.nrl.parser.ast.action.impl.RemoveActionImpl;
import net.sourceforge.nrl.parser.ast.action.impl.VariableDeclarationActionImpl;
import net.sourceforge.nrl.parser.ast.constraints.ICollectionIndex;
import net.sourceforge.nrl.parser.ast.constraints.IFunctionalExpression;
import net.sourceforge.nrl.parser.ast.constraints.ISelectionExpression;
import net.sourceforge.nrl.parser.ast.constraints.IVariableDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.impl.CastExpressionImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.ExistsStatementImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.ForallStatementImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.FunctionalExpressionImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.GlobalExistsStatementImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.IsSubtypePredicateImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.ModelReferenceImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.OperatorInvocationImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.SelectionExpressionImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.VariableDeclarationImpl;
import net.sourceforge.nrl.parser.model.AbstractClassifier;
import net.sourceforge.nrl.parser.model.AbstractPackage;
import net.sourceforge.nrl.parser.model.IClassifier;
import net.sourceforge.nrl.parser.model.IDataType;
import net.sourceforge.nrl.parser.model.IModelCollection;
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.model.IPackage;
import net.sourceforge.nrl.parser.model.PrimitiveTypeFactory;
import net.sourceforge.nrl.parser.model.VariableContext;
import net.sourceforge.nrl.parser.operators.IOperator;

/**
 * A visitor for resolving a single rule. Given a model, and an initial rule context, this visitor
 * resolves all model references in the rule.
 * <p>
 * After this visitor is done, all {@link net.sourceforge.nrl.parser.ast.IModelReference} elements
 * within the rule will have been resolved, or the relevant errors raised.
 * <p>
 * This visitor cannot, in the same iteration, check that all references are also appropriate. For
 * example, it will not check that in the comparison <code>date = '2005-12-30'</code>, the
 * <code>date</code> reference is an attribute rather than a model element. This is done using type
 * checking later.
 * 
 * @author Christian Nentwich
 */
public class AntlrModelResolverVisitor extends AntlrAstVisitor {

	private List<NRLError> errors;

	// Overall rule/fragment context.
	private IClassifier ruleContext;

	// Stack of IModelElement objects (current context). Always has
	// at least one entry: the rule context
	private Stack<IModelElement> context = new Stack<IModelElement>();

	// Stack of IVariable objects (in scope declarations)
	private VariableContext variableContext = new VariableContext();

	// Map of variable name (string) to IVariableDeclaration for globals
	protected Map<String, IVariableDeclaration> globalVariables;

	private IModelCollection models;
	
	private PrimitiveTypeFactory primitiveTypeFactory = PrimitiveTypeFactory.getInstance();

	static IPackage nonePackage = new AbstractPackage("nrl-none", null) {
		public List<String> getDocumentation() {
			return new ArrayList<String>();
		}
	};

	static IClassifier noneClassifier = new AbstractClassifier("None", nonePackage) {
		public List<String> getDocumentation() {
			return new ArrayList<String>();
		}

		public ElementType getElementType() {
			return IModelElement.ElementType.Classifier;
		}

		public boolean isSupplementary() {
			return true;
		}
	};

	/**
	 * Initialize a constraint visitor with a rule context classifier, the model, a map of global
	 * variables and an error list to populate.
	 * 
	 * @param ruleContext
	 * @param models
	 * @param globalVariables
	 * @param errors
	 */
	public AntlrModelResolverVisitor(IClassifier ruleContext, IModelCollection models,
			Map<String, IVariableDeclaration> globalVariables, List<NRLError> errors) {
		this.ruleContext = ruleContext;
		this.models = models;
		this.errors = errors;
		this.globalVariables = globalVariables;

		// The initial context is the rule context
		context.push(ruleContext);
	}

	public AntlrModelResolverVisitor(IClassifier ruleContext, IModelCollection models,
			Map<String, IVariableDeclaration> globalVariables, List<Variable> contextVariables,
			List<NRLError> errors) {
		this(ruleContext, models, globalVariables, errors);

		if (contextVariables.size() > 0) {
			variableContext.pushFrame();
			for (Variable var : contextVariables) {
				bindVariable(var, var.getDeclarationNode(), true);
			}
		}
	}

	public AntlrModelResolverVisitor(IModelCollection models,
			Map<String, IVariableDeclaration> globalVariables, List<Variable> contextVariables,
			List<NRLError> errors) {
		this(noneClassifier, models, globalVariables, contextVariables, errors);
	}

	public void bindVariable(IVariable var, INRLAstNode declarationNode, boolean checkDuplicates) {

		// Check that the variable name is ok
		if (var == null || var.getName().length() == 0) {
			raiseError(declarationNode, IStatusCode.INVALID_VARIABLE_NAME,
					"Invalid variable name: " + var.getName());
		}

		if (!Character.isLetter(var.getName().charAt(0))) {
			raiseError(declarationNode, IStatusCode.INVALID_VARIABLE_NAME,
					"Invalid variable name: " + var.getName());
		} else {
			for (int i = 1; i < var.getName().length(); i++) {
				char c = var.getName().charAt(i);

				if (!Character.isJavaIdentifierPart(c)) {
					raiseError(declarationNode, IStatusCode.INVALID_VARIABLE_NAME,
							"Invalid character in variable name: " + c);
					break;
				}
			}
		}

		// Check if it's already bound
		if (checkDuplicates && variableContext.isAlreadyDeclared(var.getName())) {
			raiseError(declarationNode, IStatusCode.DUPLICATE_VARIABLE, "Duplicate variable: "
					+ var.getName());
		}

		// Check if it shadows a model element name
		// But only do this if it is a local variable definition - there is separate checking for
		// named parameters
		if (declarationNode instanceof IVariableDeclaration || declarationNode instanceof IVariableDeclarationAction) {
			if(models.getElementByName(var.getName()) != null){
				raiseError(declarationNode, IStatusCode.VARIABLE_NAME_SHADOWS_MODEL_ELEMENT,
						"Variable name is the same as a model element: " + var.getName());
			} else if (primitiveTypeFactory.getType(var.getName()) != null){
				raiseError(declarationNode, IStatusCode.VARIABLE_NAME_SHADOWS_MODEL_ELEMENT,
				"Variable name is the same as a model element: " + var.getName());
			}
		}

		// Bind it regardless, so processing can continue
		variableContext.bindToCurrentFrame(var);
	}

	public List<NRLError> getErrors() {
		return errors;
	}

	protected IModelCollection getModels() {
		return models;
	}

	protected IModelElement getCurrentContext() {
		return context.peek();
	}

	protected Stack<IModelElement> getCurrentContextStack() {
		return context;
	}

	protected IClassifier getRuleContext() {
		return ruleContext;
	}

	public VariableContext getVariableContext() {
		return variableContext;
	}

	/**
	 * Explicitly push a model element on the current context stack.
	 * 
	 * @param context the new context element
	 */
	public void pushCurrentContext(IModelElement context) {
		this.context.push(context);
	}

	/**
	 * Visit the current node and keep the "current context" up to date. The current context only
	 * changes with a quantifier or a where clause, which introduce a sub context.
	 * <p>
	 * The method also keeps the variable context up to date.
	 */
	protected boolean visitBefore(Antlr3NRLBaseAst node) {
		// Visit exists and forall nodes, and any model references
		// not yet set (they could have set by previous visits to exists
		// or forall).

		if (node instanceof ExistsStatementImpl) {
			ExistsStatementImpl exists = (ExistsStatementImpl) node;
			ModelReferenceImpl ref = (ModelReferenceImpl) exists.getElement();

			ModelReferenceHelper.resolveReference(ref, models, context.peek(), ruleContext,
					variableContext, globalVariables, errors);
			if (ref.getInitialStep() == null)
				return false;

			context.push(ref.getTarget());
		} else if (node instanceof GlobalExistsStatementImpl) {
			GlobalExistsStatementImpl exists = (GlobalExistsStatementImpl) node;
			ModelReferenceImpl ref = (ModelReferenceImpl) exists.getModelReference();

			IModelElement element = ModelReferenceHelper.getModelElement(ref.getOriginalString(),
					ref, models, errors);
			if (element == null) {
				raiseError(ref, IStatusCode.UNKNOWN_ELEMENT_OR_ATTRIBUTE, "Unknown model element: "
						+ ref.getOriginalString() + ". Did you try to use an attribute here?");
				return false;
			}

			exists.setModelElement(element);

			if (exists.getVariableName() == null) {
				context.push(element);
			} else {
				Variable newVariable = new Variable(exists.getVariableName(), element);
				newVariable.setDeclarationNode(exists);
				bindVariable(newVariable, exists, true);
				exists.setVariable(newVariable);
			}
		} else if (node instanceof ForallStatementImpl) {
			ForallStatementImpl forall = (ForallStatementImpl) node;
			ModelReferenceImpl ref = (ModelReferenceImpl) forall.getElement();

			ModelReferenceHelper.resolveReference(ref, models, context.peek(),
					ruleContext, variableContext, globalVariables, errors);

			if (ref.getInitialStep() == null)
				return false;

			getVariableContext().pushFrame();

			if (forall.getVariableName() != null) {
				Variable newVar = new Variable(forall.getVariableName(), ref.getTarget());
				newVar.setDeclarationNode(forall);
				forall.setVariable(newVar);

				bindVariable(newVar, forall, true);
			} else {
				getCurrentContextStack().push(ref.getTarget());
			}
		} else if (node instanceof FunctionalExpressionImpl) {
			FunctionalExpressionImpl func = (FunctionalExpressionImpl) node;

			if (func.getFunction() == IFunctionalExpression.Function.NUMBER_OF) {
				ModelReferenceImpl ref = (ModelReferenceImpl) func.getParameters().iterator()
						.next();

				ModelReferenceHelper.resolveReference(ref, models, context.peek(),
						ruleContext, variableContext, globalVariables, errors);
				if (ref.getInitialStep() == null)
					return false;

				context.push(ref.getTarget());
			}
		} else if (node instanceof VariableDeclarationImpl) {
			VariableDeclarationImpl var = (VariableDeclarationImpl) node;

			// Initialize this variable declaration - either with an
			// expression or a model reference (which we have to resolve).
			// It can also be resolved with operator references, if the operator
			// returns a complex type.

			Variable newVar = null;
			if (var.getExpression() instanceof ModelReferenceImpl
					|| var.getExpression() instanceof ICollectionIndex
					|| var.getExpression() instanceof ISelectionExpression) {

				ModelReferenceImpl ref = null;
				if (var.getExpression() instanceof ModelReferenceImpl) {
					ref = (ModelReferenceImpl) var.getExpression();
				} else if (var.getExpression() instanceof ICollectionIndex) {
					ref = (ModelReferenceImpl) ((ICollectionIndex) var.getExpression())
							.getCollection();
				} else {
					ref = (ModelReferenceImpl) ((ISelectionExpression) var.getExpression())
							.getModelReference();
				}

				ModelReferenceHelper.resolveReference(ref, models, context.peek(),
						ruleContext, variableContext, globalVariables, errors);

				if (ref.getInitialStep() != null)
					newVar = new Variable(var.getVariableName(), ref.getTarget());
				else {
					// BUG FIX: bind variable even if no target found
					newVar = new Variable(var.getVariableName(), (IModelElement) null);
				}
			} else if (var.getExpression() instanceof OperatorInvocationImpl) {
				OperatorInvocationImpl op = (OperatorInvocationImpl) var.getExpression();

				if (op.getOperator() != null) {
					IOperator opDef = op.getOperator();

					if (opDef.getReturnType() != null) {
						newVar = new Variable(var.getVariableName(), opDef.getReturnType());
					} else {
						newVar = new Variable(var.getVariableName(), var.getExpression());
					}
				} else {
					newVar = new Variable(var.getVariableName(), var.getExpression());
				}
			} else {
				newVar = new Variable(var.getVariableName(), var.getExpression());
			}

			if (newVar != null) {
				newVar.setDeclarationNode(var);
				var.setVariableReference(newVar);
				bindVariable(newVar, var, true);
			}
		}
		if (node instanceof SelectionExpressionImpl) {
			SelectionExpressionImpl selection = (SelectionExpressionImpl) node;
			ModelReferenceImpl ref = (ModelReferenceImpl) selection.getModelReference();

			ModelReferenceHelper.resolveReference(ref, models, context.peek(), ruleContext,
					variableContext, globalVariables, errors);
			if (ref.getInitialStep() == null)
				return false;

			context.push(ref.getTarget());
		} else if (node instanceof ModelReferenceImpl) {
			ModelReferenceImpl ref = (ModelReferenceImpl) node;

			// Check if the initial step is null - because a quantifier
			// may have resolve it already
			if (ref.getInitialStep() == null) {
				ModelReferenceHelper.resolveReference(ref, models, context.peek(),
						ruleContext, variableContext, globalVariables, errors);

				if (ref.getInitialStep() == null)
					return false;
			}
		} else // Delete action? Resolve its model reference before hand,
		// also bind to variable if necessary.
		if (node instanceof RemoveActionImpl) {
			RemoveActionImpl remove = (RemoveActionImpl) node;

			// Resolve it. Bail out if it fails
			ModelReferenceImpl ref = (ModelReferenceImpl) remove.getTarget();
			ModelReferenceHelper.resolveReference(ref, getModels(), getCurrentContext(),
					getRuleContext(), getVariableContext(), globalVariables, getErrors());
			if (ref.getInitialStep() == null)
				return false;

			// Bind it to the variable, if necessary
			if (remove.getVariableName() != null) {
				Variable newVar = new Variable(remove.getVariableName(), ref.getTarget());
				remove.setVariable(newVar);

				getVariableContext().pushFrame();
				bindVariable(newVar, remove, true);
			}

			return true;
		}
		// For each - update current context
		else if (node instanceof ForEachActionImpl) {
			ForEachActionImpl forEach = (ForEachActionImpl) node;

			// Resolve it. Bail out if it fails
			ModelReferenceImpl ref = (ModelReferenceImpl) forEach.getCollection();
			ModelReferenceHelper.resolveReference(ref, getModels(), getCurrentContext(),
					getRuleContext(), getVariableContext(), globalVariables, getErrors());
			if (ref.getInitialStep() == null)
				return false;

			getVariableContext().pushFrame();

			if (forEach.getVariableName() != null) {
				Variable newVar = new Variable(forEach.getVariableName(), ref.getTarget());
				forEach.setVariable(newVar);

				bindVariable(newVar, forEach, true);
			} else {
				getCurrentContextStack().push(ref.getTarget());
			}

			return true;
		} else if (node instanceof ICompoundAction) {
			getVariableContext().pushFrame();
		} else if (node instanceof VariableDeclarationActionImpl) {
			VariableDeclarationActionImpl var = (VariableDeclarationActionImpl) node;

			// Initialize this variable declaration - either with an
			// expression or a model reference (which we have to resolve).
			// It can also be resolved with operator references, if the operator
			// returns a complex type.
			Variable newVar = null;
			if (var.getExpression() instanceof ModelReferenceImpl
					|| var.getExpression() instanceof ICollectionIndex
					|| var.getExpression() instanceof ISelectionExpression) {

				ModelReferenceImpl ref = null;
				if (var.getExpression() instanceof ModelReferenceImpl)
					ref = (ModelReferenceImpl) var.getExpression();
				else if (var.getExpression() instanceof ICollectionIndex)
					ref = (ModelReferenceImpl) ((ICollectionIndex) var.getExpression())
							.getCollection();
				else
					ref = (ModelReferenceImpl) ((ISelectionExpression) var.getExpression())
							.getModelReference();

				ModelReferenceHelper.resolveReference(ref, getModels(), getCurrentContext(),
						getRuleContext(), getVariableContext(), globalVariables, getErrors());

				if (ref.getInitialStep() != null)
					newVar = new Variable(var.getVariableName(), ref.getTarget());
				else {
					// BUG FIX: bind variable even if no target found
					newVar = new Variable(var.getVariableName(), (IModelElement) null);
				}
			} else if (var.getExpression() instanceof OperatorInvocationImpl) {
				OperatorInvocationImpl op = (OperatorInvocationImpl) var.getExpression();

				if (op.getOperator() != null) {
					IOperator opDef = op.getOperator();

					if (opDef.getReturnType() != null) {
						newVar = new Variable(var.getVariableName(), opDef.getReturnType());
					} else {
						newVar = new Variable(var.getVariableName(), var.getExpression());
					}
				} else {
					newVar = new Variable(var.getVariableName(), var.getExpression());
				}
			} else {
				newVar = new Variable(var.getVariableName(), var.getExpression());
			}

			if (newVar != null) {
				newVar.setDeclarationNode(var);
				var.setVariableReference(newVar);
				bindVariable(newVar, var, true);
			}
		}

		return true;
	}

	/**
	 * Visit the current node and pop the context stack where necessary, for example when a
	 * quantifier goes out of scope.
	 * <p>
	 * Also pop the variable stack when variables go out of scope.
	 */
	protected void visitAfter(Antlr3NRLBaseAst node) {
		if (node instanceof ExistsStatementImpl) {
			ExistsStatementImpl exists = (ExistsStatementImpl) node;

			if (exists.getElement().getInitialStep() != null)
				context.pop();
		} else if (node instanceof SelectionExpressionImpl) {
			SelectionExpressionImpl selection = (SelectionExpressionImpl) node;

			if (selection.getModelReference().getInitialStep() != null)
				context.pop();
		} else if (node instanceof GlobalExistsStatementImpl) {
			GlobalExistsStatementImpl exists = (GlobalExistsStatementImpl) node;

			if (exists.getElement() != null) {
				if (exists.getVariableName() == null) {
					context.pop();
				}
			}
		} else if (node instanceof ForallStatementImpl) {
			ForallStatementImpl forall = (ForallStatementImpl) node;

			getVariableContext().popFrame();

			if (forall.getElement().getInitialStep() != null) {
				if (forall.getVariableName() == null) {
					getCurrentContextStack().pop();
				}
			}
		} else if (node instanceof FunctionalExpressionImpl) {
			FunctionalExpressionImpl func = (FunctionalExpressionImpl) node;

			if (func.getFunction() == IFunctionalExpression.Function.NUMBER_OF
					&& ((ModelReferenceImpl) func.getParameters().iterator().next())
							.getInitialStep() != null)
				context.pop();
		} else if (node instanceof CastExpressionImpl) {
			CastExpressionImpl cast = (CastExpressionImpl) node;
			ModelReferenceImpl ref = (ModelReferenceImpl) cast.getTargetReference();

			IModelElement element = ModelReferenceHelper.getModelElement(ref.getOriginalString(),
					ref, models, errors);

			if (element == null) {
				raiseError(ref, IStatusCode.UNKNOWN_ELEMENT_OR_ATTRIBUTE, "Unknown model element: "
						+ ref.getOriginalString() + ". Did you try to use an attribute here?");
			}

			cast.setTargetType(element);
		} else if (node instanceof IsSubtypePredicateImpl) {
			IsSubtypePredicateImpl subType = (IsSubtypePredicateImpl) node;
			ModelReferenceImpl ref = (ModelReferenceImpl) subType.getTypeReference();

			IModelElement element = ModelReferenceHelper.getModelElement(ref.getOriginalString(),
					ref, models, errors);

			if (element == null) {
				raiseError(ref, IStatusCode.UNKNOWN_ELEMENT_OR_ATTRIBUTE, "Unknown model element: "
						+ ref.getOriginalString() + ". Did you try to use an attribute here?");
			}

			subType.setSuperType(element);
		} else if (node instanceof CreateActionImpl) {
			// Create action? We must ensure that the action references
			// an element, not an attribute. Then, if a variable was created
			// we need to bind it

			CreateActionImpl create = (CreateActionImpl) node;
			ModelReferenceImpl ref = create.getModelReference();
			if (ref.getInitialStep() == null)
				return;

			if (ref.getReferenceType() != IModelReference.REFERENCE_ELEMENT
					|| (!(ref.getTarget() instanceof IClassifier) && !(ref.getTarget() instanceof IDataType))) {
				raiseError(ref, IStatusCode.CREATE_REFERENCE_INVALID,
						"Only classifier elements may be created");
			}

			if (create.getVariableName() != null) {
				Variable newVar = new Variable(create.getVariableName(), ref.getTarget());
				create.setVariable(newVar);

				bindVariable(newVar, create, true);
			}
		} else if (node instanceof RemoveActionImpl) {
			// Delete action? Unbind variable if necessary.
			RemoveActionImpl delete = (RemoveActionImpl) node;

			if (delete.getTarget().getInitialStep() != null) {
				if (delete.getVariable() != null) {
					getVariableContext().popFrame();
				}
			}
		} else if (node instanceof ForEachActionImpl) {
			// Foreach action? Pop context
			ForEachActionImpl forEach = (ForEachActionImpl) node;

			getVariableContext().popFrame();

			if (forEach.getCollection().getInitialStep() != null) {
				if (forEach.getVariableName() == null) {
					getCurrentContextStack().pop();
				}
			}
		} else if (node instanceof ICompoundAction) {
			getVariableContext().popFrame();
		}
	}

	/**
	 * Convenience method to add an error to the error list, for a particular model reference
	 * 
	 * @param ref the model reference
	 * @param statusCode the error status code
	 * @param message the message
	 */
	protected void raiseError(ModelReferenceImpl ref, int statusCode, String message) {
		errors.add(new SemanticError(statusCode, ref.getLine(), ref.getColumn(), ref
				.getOriginalString().length(), message));
	}

	protected void raiseError(INRLAstNode node, int statusCode, String message) {
		errors.add(new SemanticError(statusCode, node.getLine(), node.getColumn(), 1, message));
	}
}
