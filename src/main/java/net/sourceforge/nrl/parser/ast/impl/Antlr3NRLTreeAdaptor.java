package net.sourceforge.nrl.parser.ast.impl;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.nrl.parser.ast.action.impl.ActionFragmentApplicationActionImpl;
import net.sourceforge.nrl.parser.ast.action.impl.ActionFragmentDeclarationImpl;
import net.sourceforge.nrl.parser.ast.action.impl.ActionRuleDeclarationImpl;
import net.sourceforge.nrl.parser.ast.action.impl.AddActionImpl;
import net.sourceforge.nrl.parser.ast.action.impl.CompoundActionImpl;
import net.sourceforge.nrl.parser.ast.action.impl.ConditionalActionImpl;
import net.sourceforge.nrl.parser.ast.action.impl.CreateActionImpl;
import net.sourceforge.nrl.parser.ast.action.impl.ForEachActionImpl;
import net.sourceforge.nrl.parser.ast.action.impl.OperatorActionImpl;
import net.sourceforge.nrl.parser.ast.action.impl.RemoveActionImpl;
import net.sourceforge.nrl.parser.ast.action.impl.RemoveFromCollectionActionImpl;
import net.sourceforge.nrl.parser.ast.action.impl.SetActionImpl;
import net.sourceforge.nrl.parser.ast.action.impl.VariableDeclarationActionImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.ArithmeticExpressionImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.BinaryOperatorStatementImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.BinaryPredicateImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.BooleanLiteralImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.CardinalityConstraintImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.CastExpressionImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.CollectionIndexImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.CompoundReportImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.ConcatenatedReportImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.ConditionalReportImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.ConstraintRuleDeclarationImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.DecimalNumberImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.ExistsStatementImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.ForallStatementImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.FunctionalExpressionImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.GlobalExistsStatementImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.IfThenStatementImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.IntegerNumberImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.IsInPredicateImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.IsNotInPredicateImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.IsSubtypePredicateImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.LiteralStringImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.ModelReferenceImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.ModelReferenceStep;
import net.sourceforge.nrl.parser.ast.constraints.impl.MultipleExistsStatementImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.MultipleNotExistsStatementImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.NotExistsStatementImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.OperatorInvocationImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.SelectionExpressionImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.ValidationFragmentApplicationImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.ValidationFragmentDeclarationImpl;
import net.sourceforge.nrl.parser.ast.constraints.impl.VariableDeclarationImpl;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTreeAdaptor;

/**
 * The new tree adaptor class for ANTLR 3: this maps token types that are
 * represented as tree nodes to proper classes in the
 * <code>net.sourceforge.nrl.parser.ast.impl</code> package.
 * <p>
 * This is much neater than in ANTLR 2: if the token names get out of line with
 * the contents of this class, you will find a compiler error in here.
 * 
 * @author Christian Nentwich
 */
public class Antlr3NRLTreeAdaptor extends CommonTreeAdaptor {

	protected Map<Integer, Class<?>> tokenTypeToClass = new HashMap<Integer, Class<?>>();

	public Antlr3NRLTreeAdaptor() {
		tokenTypeToClass.put(NRLActionParser.ADD, ArithmeticExpressionImpl.class);
		tokenTypeToClass.put(NRLActionParser.AND, BinaryOperatorStatementImpl.class);
		tokenTypeToClass.put(NRLActionParser.AS_A, CastExpressionImpl.class);
		tokenTypeToClass.put(NRLActionParser.BOOLEAN, BooleanLiteralImpl.class);
		tokenTypeToClass.put(NRLActionParser.DECIMAL_NUMBER, DecimalNumberImpl.class);
		tokenTypeToClass.put(NRLActionParser.DIV, ArithmeticExpressionImpl.class);
		tokenTypeToClass.put(NRLActionParser.EACH, ForallStatementImpl.class);		
		tokenTypeToClass.put(NRLActionParser.EQUALS, BinaryPredicateImpl.class);
		tokenTypeToClass.put(NRLActionParser.GREATER, BinaryPredicateImpl.class);
		tokenTypeToClass.put(NRLActionParser.GREATER_EQ, BinaryPredicateImpl.class);
		tokenTypeToClass.put(NRLActionParser.IDENTIFIER, ModelReferenceStep.class);
		tokenTypeToClass.put(NRLActionParser.IF, IfThenStatementImpl.class);
		tokenTypeToClass.put(NRLActionParser.IFF, BinaryOperatorStatementImpl.class);
		tokenTypeToClass.put(NRLActionParser.IMPLIES, BinaryOperatorStatementImpl.class);
		tokenTypeToClass.put(NRLActionParser.INTEGER_NUMBER, IntegerNumberImpl.class);
		tokenTypeToClass.put(NRLActionParser.IS_IN, IsInPredicateImpl.class);
		tokenTypeToClass.put(NRLActionParser.IS_NOT_IN, IsNotInPredicateImpl.class);
		tokenTypeToClass.put(NRLActionParser.KIND_OF, IsSubtypePredicateImpl.class);
		tokenTypeToClass.put(NRLActionParser.LESS, BinaryPredicateImpl.class);
		tokenTypeToClass.put(NRLActionParser.LESS_EQ, BinaryPredicateImpl.class);
		tokenTypeToClass.put(NRLActionParser.MODEL, ModelFileReferenceImpl.class);
		tokenTypeToClass.put(NRLActionParser.MINUS, ArithmeticExpressionImpl.class);
		tokenTypeToClass.put(NRLActionParser.MOD, ArithmeticExpressionImpl.class);
		tokenTypeToClass.put(NRLActionParser.NOT_EQUALS, BinaryPredicateImpl.class);		
		tokenTypeToClass.put(NRLActionParser.NOT_PRESENT, NotExistsStatementImpl.class);
		tokenTypeToClass.put(NRLActionParser.NUMBER_OF, FunctionalExpressionImpl.class);
		tokenTypeToClass.put(NRLActionParser.OPERATOR, OperatorInvocationImpl.class);
		tokenTypeToClass.put(NRLActionParser.OPERATORS, OperatorFileReferenceImpl.class);
		tokenTypeToClass.put(NRLActionParser.OR, BinaryOperatorStatementImpl.class);
		tokenTypeToClass.put(NRLActionParser.ORDINAL_NUMBER, CollectionIndexImpl.class);
		tokenTypeToClass.put(NRLActionParser.PROPERTYNAME, ValidationFragmentApplicationImpl.class);			
		tokenTypeToClass.put(NRLActionParser.RULESET, RuleSetDeclarationImpl.class);
		tokenTypeToClass.put(NRLActionParser.SINGLE_QUOTED_STRING, LiteralStringImpl.class);
		tokenTypeToClass.put(NRLActionParser.SUM_OF, FunctionalExpressionImpl.class);
		tokenTypeToClass.put(NRLActionParser.THERE_IS, GlobalExistsStatementImpl.class);
		tokenTypeToClass.put(NRLActionParser.TIMES, ArithmeticExpressionImpl.class);
		tokenTypeToClass.put(NRLActionParser.VALIDATION_FRAGMENT, ValidationFragmentDeclarationImpl.class);
		tokenTypeToClass.put(NRLActionParser.VALIDATION_RULE, ConstraintRuleDeclarationImpl.class);
		
		tokenTypeToClass.put(NRLActionParser.VT_COMPOUND_REPORT, CompoundReportImpl.class);
		tokenTypeToClass.put(NRLActionParser.VT_CONCATENATED_REPORT, ConcatenatedReportImpl.class);
		tokenTypeToClass.put(NRLActionParser.VT_CONDITIONAL_REPORT, ConditionalReportImpl.class);
		tokenTypeToClass.put(NRLActionParser.VT_ENUMERATOR, CardinalityConstraintImpl.class);
		tokenTypeToClass.put(NRLActionParser.VT_EXISTS, ExistsStatementImpl.class);
		tokenTypeToClass.put(NRLActionParser.VT_FIRST_ORDINAL_NUMBER, CollectionIndexImpl.class);
		tokenTypeToClass.put(NRLActionParser.VT_MODELREFERENCE, ModelReferenceImpl.class);
		tokenTypeToClass.put(NRLActionParser.VT_NAMED_PARAMETER, NamedParameterImpl.class);
		tokenTypeToClass.put(NRLActionParser.VT_MULTIPLE_EXISTS, MultipleExistsStatementImpl.class);
		tokenTypeToClass.put(NRLActionParser.VT_MULTIPLE_NOTEXISTS, MultipleNotExistsStatementImpl.class);
		tokenTypeToClass.put(NRLActionParser.VT_NEGATE_DECIMAL, DecimalNumberImpl.class);
		tokenTypeToClass.put(NRLActionParser.VT_NEGATE_INTEGER, IntegerNumberImpl.class);
		tokenTypeToClass.put(NRLActionParser.VT_RULEFILE, RuleFileImpl.class);
		tokenTypeToClass.put(NRLActionParser.VT_SELECTION_EXPR, SelectionExpressionImpl.class);
		tokenTypeToClass.put(NRLActionParser.VT_SINGLE_SELECTION_EXPR, SelectionExpressionImpl.class);
		tokenTypeToClass.put(NRLActionParser.VT_VARIABLE_DECLARATION, VariableDeclarationImpl.class);
		

		tokenTypeToClass.put(NRLActionParser.MODEL, ModelFileReferenceImpl.class);
		tokenTypeToClass.put(NRLActionParser.OPERATORS, OperatorFileReferenceImpl.class);

		// Actions
		tokenTypeToClass.put(NRLActionParser.ACTION_FRAGMENT,
				ActionFragmentDeclarationImpl.class);
		tokenTypeToClass
				.put(NRLActionParser.ACTION_RULE, ActionRuleDeclarationImpl.class);
		tokenTypeToClass.put(NRLActionParser.ADD_VERB, AddActionImpl.class);
		tokenTypeToClass.put(NRLActionParser.CREATE, CreateActionImpl.class);
		tokenTypeToClass.put(NRLActionParser.REMOVE, RemoveActionImpl.class);
		tokenTypeToClass.put(NRLActionParser.SET, SetActionImpl.class);

		tokenTypeToClass
				.put(NRLActionParser.VT_COMPOUND_ACTION, CompoundActionImpl.class);
		tokenTypeToClass.put(NRLActionParser.VT_CONDITIONAL_ACTION,
				ConditionalActionImpl.class);
		tokenTypeToClass.put(NRLActionParser.VT_FOREACH_ACTION, ForEachActionImpl.class);
		tokenTypeToClass.put(NRLActionParser.VT_MACRO_APPLICATION,
				ActionFragmentApplicationActionImpl.class);
		tokenTypeToClass
				.put(NRLActionParser.VT_OPERATOR_ACTION, OperatorActionImpl.class);
		tokenTypeToClass.put(NRLActionParser.VT_REMOVE_FROM_COLLECTION,
				RemoveFromCollectionActionImpl.class);
		tokenTypeToClass.put(NRLActionParser.VT_VARIABLE_DECLARATION_ACTION,
				VariableDeclarationActionImpl.class);
	}

	@Override
	public Object create(Token token) {
		if (token == null)
			return new Antlr3NRLBaseAst();
		
		Class<?> tokenClass = tokenTypeToClass.get(token.getType());
		
		if (tokenClass == null)
			return new Antlr3NRLBaseAst(token);
		
		try {
			Constructor<?> constr = tokenClass.getConstructor(new Class<?>[] { Token.class });
			return constr.newInstance(new Object[] { token } );
		} catch (Exception e) {
			throw new RuntimeException("Internal error: could not instantiate token type " + 
					token.getType()+". This is a development problem.", e);
		} 
	}	
}
