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
parser grammar NRLConstraintRules;

options {
	output = AST;
	ASTLabelType = CommonTree;
}

tokens {
	// Definition of "virtual" tokens, i.e. all that are not returned by the lexer, but used
	// for tree construction only. As a convention, to make them easier to distinguish from lexical
	// values, they all start with VT_.
	
	VT_COMPOUND_REPORT;
	VT_CONCATENATED_REPORT;
	VT_CONDITIONAL_REPORT;
	VT_NAMED_PARAMETER;
	VT_ENUMERATOR;
	VT_EXISTS;
	VT_FIRST_ORDINAL_NUMBER;
	VT_MODELREFERENCE;
	VT_MULTIPLE_EXISTS;
	VT_MULTIPLE_NOTEXISTS;
	VT_NEGATE_DECIMAL;
	VT_NEGATE_INTEGER;
	VT_RULEFILE;
	VT_SELECTION_EXPR;
	VT_SINGLE_SELECTION_EXPR;
	VT_VARIABLE_DECLARATION;
}

@members {
	private java.util.List<NRLError> syntaxErrors = new java.util.ArrayList<NRLError>();
	
	public java.util.List<NRLError> getSyntaxErrors() {
		return syntaxErrors;
	}
	
	public boolean hasErrors() {
		return !syntaxErrors.isEmpty();
	}
	
	
	public void resetErrors() {
		syntaxErrors.clear();
	}
	
	public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
		String msg;
		if(e instanceof InvalidChildException){
			msg = ((InvalidChildException)e).getErrorDescription();
		} else {
        	msg = getErrorMessage(e, tokenNames);
        }
	
		// Fix for EOF tokens: ANTLR always reports these at line -1, which causes problems.
		// Take the position of the second-to-last token instead.
		if (e.token.getType() == Token.EOF && getTokenStream().size() > 0) {
			Token last = getTokenStream().get(getTokenStream().size() - 1);
			syntaxErrors.add(new SyntaxError(IStatusCode.PARSER_ERROR, last.getLine(), last
					.getCharPositionInLine()
					+ last.getText().length(), msg));
		} else {
			syntaxErrors.add(new SyntaxError(IStatusCode.PARSER_ERROR, e.line,
					e.charPositionInLine, msg));
		}
	}
	
	public void initialisePositionFromChild(Object obj, int index) {
		if (obj instanceof Antlr3NRLBaseAst)
			((Antlr3NRLBaseAst) obj).initialisePositionFromChild(index);
	}
	
	private void validateChildType(Tree tree, Class<?> clazz, String expectedChildDescription) throws RecognitionException {
		for (int i = 0; i < tree.getChildCount(); i++) {
			Object obj = tree.getChild(i);
			if (obj != null && !clazz.isAssignableFrom(obj.getClass()))
				throw new InvalidChildException(input, expectedChildDescription);
		}		
	}
}

// -------------------------------- GRAMMAR ----------------------------------------

	
declaration
		:	(context VALIDATION_RULE) => validationRuleDeclaration
		|	(VALIDATION_RULE) => validationRuleDeclaration
		|	globalVariableDeclaration
		|	validationFragmentDeclaration
		|	ruleSetDeclaration
		;
		
validationRuleDeclaration
		:	context VALIDATION_RULE^ DOUBLE_QUOTED_STRING validationRuleVariableDeclaration* constraint ( constraintRuleReport )?
		|	VALIDATION_RULE^ DOUBLE_QUOTED_STRING APPLIES_TO! modelReference ( AND! USES! additionalContextList )? validationRuleVariableDeclaration* constraint ( constraintRuleReport )?
		;
		
additionalContextList
		:	namedParameterEntry ( ( COMMA! | AND! ) namedParameterEntry )*
		;
		
validationRuleVariableDeclaration
@after { initialisePositionFromChild(retval.tree, 0); }
		:	simpleVariableDeclaration COMMA?
			-> ^(VT_VARIABLE_DECLARATION simpleVariableDeclaration)
		;
				
validationFragmentDeclaration
		:	multipleContext VALIDATION_FRAGMENT^ DOUBLE_QUOTED_STRING constraint
		;
		
ruleSetDeclaration
		:	RULESET^ DOUBLE_QUOTED_STRING 
			(
				APPLIES_TO! modelReference WHERE! constraint
			)?
		;
		
globalVariableDeclaration
@after { initialisePositionFromChild(retval.tree, 0); }
		:	simpleVariableDeclaration
			-> ^(VT_VARIABLE_DECLARATION simpleVariableDeclaration)
		;

simpleVariableDeclaration
		:	DOUBLE_QUOTED_STRING (REPRESENT!|HASHAVE!) expression
		;		

context
		:	CONTEXT! modelReference
		;
		
multipleContext
		:	CONTEXT! namedParameterEntry ( COMMA! namedParameterEntry )*
		;
		
namedParameterEntry
@after { initialisePositionFromChild(retval.tree, 0); }
		:	modelReference LPAREN DOUBLE_QUOTED_STRING RPAREN
			-> ^(VT_NAMED_PARAMETER modelReference DOUBLE_QUOTED_STRING)
		;
		
constraint
		:	statement
		;
		
statement
		:	IF^ iffStatement THEN! statement
			(
				options {
					greedy = true;
				} :
				ELSE! statement
			)?
		|	iffStatement
		;
		
iffStatement
		:	impliesStatement (IFF^ iffStatement)?
		;

impliesStatement
		:	orStatement	(IMPLIES^ impliesStatement)?
		;
		
orStatement
		:	andStatement (OR^ orStatement)?
		;
		
andStatement
		:	logicalStatement (AND^ andStatement)?
		;
		
logicalStatement
		:	(modelReference (PRESENT|HASHAVE)) => existsStatement
		|	(modelReference THAT) => existsStatement
		|	(modelReference NOT_PRESENT) => notExistsStatement 
		|	(enumeratorDisambiguation) => existsStatement
		|	(forallStart) => forallStatement
		|	globalExistsStatement
		|	predicateStatement
		;

enumeratorDisambiguation
		:	enumeratorStart OF? (THAT | HASHAVE | modelReference)
		;		
		
enumeratorStart
		:	(IN)? (EXACTLY|AT_LEAST|AT_MOST)? (ONE | TWO | THREE | FOUR | INTEGER_NUMBER | NO)
		;
		
enumerator
		:	(IN)? (qual=AT_MOST | qual=AT_LEAST | qual=EXACTLY)? (num=ONE|num=TWO|num=THREE|num=FOUR|num=INTEGER_NUMBER|num=NO)
			-> ^(VT_ENUMERATOR $qual? $num)
		;
		
existsStatement
@after { initialisePositionFromChild(retval.tree, 0); }
		:	(enumeratorStart HASHAVE) => enumerator HASHAVE simpleOrComplexConstraint
			-> ^(VT_EXISTS enumerator simpleOrComplexConstraint)
		|	(enumerator OF?)? modelReference (PRESENT | (( ((THAT)? HASHAVE) | WHERE)? simpleOrComplexConstraint))
			-> ^(VT_EXISTS enumerator? modelReference simpleOrComplexConstraint?)
		;
				
globalExistsStatement
		:	THERE_IS^ (NO)? modelReference 
			(
				LPAREN! var=DOUBLE_QUOTED_STRING RPAREN!
			)?
			( 
				WHERE! simpleOrComplexConstraint 
			)?
		;
		
simpleOrComplexConstraint
		:	(LPAREN) => LPAREN! constraint RPAREN!
		|	predicateStatement
		;

notExistsStatement
		:	modelReference NOT_PRESENT^
		;
		
forallStatement
		:	(forallStart OF modelReference HASHAVE) => (IN!)? EACH^ (OF!)? modelReference HASHAVE! simpleOrComplexConstraint
		|	(forallStart modelReference HASHAVE) => (IN!)? EACH^ (OF!)? modelReference HASHAVE! simpleOrComplexConstraint
		|	(forallStart DOUBLE_QUOTED_STRING) => (IN!)? EACH^ DOUBLE_QUOTED_STRING (IN_COLLECTION!)? (OF!)? modelReference (HASHAVE! | COMMA!)? simpleOrComplexConstraint
		|	(IN!)? EACH^ (OF!)? modelReference simpleOrComplexConstraint
		;
		
forallStart
		:	(IN)? EACH
		;		
					
predicateStatement
@after { 
	if (retval.tree instanceof IBinaryPredicate && retval.tree.getChildCount() == 2) {
		validateChildType(retval.tree, IExpression.class, "expression");
	}
}
		:	(expression IS_IN) => expression IS_IN^ listDefinition
		|	(expression IS_NOT_IN) => expression IS_NOT_IN^ listDefinition
		|	(modelReference KIND_OF) => isSubtypePredicate
		|	(FOLLOWING PRESENT) => multipleExistsStatement
		|	(FOLLOWING NOT_PRESENT) => multipleNotExistsStatement
		|	expression	((
						  EQUALS^
						| NOT_EQUALS^
						| LESS^
						| LESS_EQ^
						| GREATER^
						| GREATER_EQ^
						)
			expression)?
		;
		
/*
	Ambiguity warning switched off because of actions: 
	Compound actions are also joined using comma.
 */		
listDefinition
		:	identifier (
				COMMA! 
				identifier
			)*
		;

isSubtypePredicate
		:	modelReference KIND_OF^ modelReference
		;
		
multipleExistsStatement
@after { initialisePositionFromChild(retval.tree, 0); }
		:	FOLLOWING PRESENT COLON modelReferenceList
			-> ^(VT_MULTIPLE_EXISTS modelReferenceList)
		;

multipleNotExistsStatement
@after { initialisePositionFromChild(retval.tree, 0); }
		:	FOLLOWING NOT_PRESENT COLON modelReferenceList
			-> ^(VT_MULTIPLE_NOTEXISTS modelReferenceList)
		;

expression
		:	addExpression
		;

/*
 * Making addExpression and multiplyExpression "greedy" solves a problem with
 * operatorInvocation below. operatorInovcation recurses back to expression,
 * creating ambiguity in the following:   x + [operator] y + z.
 * This could be: x + ([operator] y) + z   or   x + [operator] (y + z). The
 * greedy rule forces the second case.
 */
addExpression
@after { 
	if (retval.tree instanceof IArithmeticExpression && retval.tree.getChildCount() == 2) {
		validateChildType(retval.tree, IExpression.class, "expression");
	}
}
		:	multiplyExpression 
			(
				options {
					greedy = true;
				} :			
				(ADD^ | MINUS^) 
				multiplyExpression
			)*
		;
		
multiplyExpression
		:	infixOperatorExpression 
			(
				options {
					greedy = true;
				} :			
				(TIMES^|DIV^|MOD^) 
				infixOperatorExpression						
			)*
		;
	
infixOperatorExpression
		:	infixPropertyApplication (
				options {
					greedy = true;
				} :
				OPERATOR^ infixPropertyApplication
			)*
		;

infixPropertyApplication
		:	term (
				options {
					greedy = true;
				} :
				PROPERTYNAME^ term
			)*
		;
		
term
		:	(OPERATOR) => operatorInvocation
		|	(PROPERTYNAME) => propertyApplication
		|	(modelReference AS_A) => castExpression
		|	(modelReference WHERE) => selectionExpression
		|	(FIRST modelReference WHERE) => selectionExpression
		|	(FIRST OF modelReference WHERE) => selectionExpression		
		|	LPAREN! constraint RPAREN!
		|	functionalExpression
		|	identifier
		;

/* Simplified term; used mainly for reporting */
simpleTerm
		:	(OPERATOR) => operatorInvocation
		|	functionalExpression
		|	identifier
		;
		
/*
 * BUG FIX: warnWhenFollowAmbig added. Consequence:
 * [operator]-5 is interpreted as [operator] with a parameter of "-5", NOT as
 * "[operator] subtract 5"!
 */
operatorInvocation
		:	OPERATOR^ 
			(
				operatorParameterList
			)?
		;
											
/*
   Ambiguity warning switch off because of nested operators.
      [op] param1 from [op2] param2 from param3 resolves to
      [op] param1 from ([op2] param2 from param3) in this case.
 */
operatorParameterList
		:	expression ( (AND!|FROM!|TO!|WITH!|USING!) expression )*
		;
				
propertyApplication
		:	PROPERTYNAME^ operatorParameterList?
		;
				
functionalExpression
		:	SUM_OF^ modelReference
		|	(NUMBER_OF UNIQUE) => NUMBER_OF^ UNIQUE! modelReference ( LPAREN! BY! modelReference RPAREN! )?
		|	NUMBER_OF^ modelReference
		;
		
castExpression
		:	modelReference AS_A^ modelReference
		;
		
selectionExpression
		:	modelReference WHERE simpleOrComplexConstraint
			-> ^(VT_SELECTION_EXPR modelReference simpleOrComplexConstraint)
		|	FIRST OF? modelReference WHERE simpleOrComplexConstraint
			-> ^(VT_SINGLE_SELECTION_EXPR modelReference simpleOrComplexConstraint)
		;

identifier
		:	modelReference
		|	SINGLE_QUOTED_STRING
		|	BOOLEAN
		|	number
		|	collectionIndex
		;

number
		:	DECIMAL_NUMBER
		|	INTEGER_NUMBER
		|	MINUS DECIMAL_NUMBER -> ^(VT_NEGATE_DECIMAL DECIMAL_NUMBER)
		|	MINUS INTEGER_NUMBER -> ^(VT_NEGATE_INTEGER INTEGER_NUMBER)
		;
		
collectionIndex
		:	ORDINAL_NUMBER^ (OF!)? modelReference
		|	FIRST OF? modelReference
			-> ^(VT_FIRST_ORDINAL_NUMBER modelReference)
		;
		
modelReference
@after { ((ModelReferenceImpl) retval.tree).initializeSteps(); }
		:	identifierSequence 
			-> ^(VT_MODELREFERENCE identifierSequence)
		;

modelReferenceList
		:	modelReference (
				options {
					greedy = true;
				} :
				COMMA! modelReference
			)*
		;
		
identifierSequence
		:	IDENTIFIER (OF! identifierSequence)?
		;
		
/* Reporting */

constraintRuleReport
		:	REPORT! ( COLON! )? compoundReport
		;
		
compoundReport
		:	simpleReports 
			-> ^(VT_COMPOUND_REPORT simpleReports)
		;

simpleReports
		:	simpleReport ( ( COMMA! )? simpleReport )*
		;

simpleReport
		:	concatenatedReport
		|	conditionalReport
		;
		
concatenatedReport
		:	REPORT? concatenatedReportTerms
			-> ^(VT_CONCATENATED_REPORT concatenatedReportTerms)
		;

concatenatedReportTerms
		:	simpleTerm ( ADD! simpleTerm ) *
		;	
		
conditionalReport
		:	IF constraint THEN c1=compoundReport ( ELSE c2=compoundReport )? SEMI
			-> ^(VT_CONDITIONAL_REPORT constraint $c1 $c2?)
		;
		
		
