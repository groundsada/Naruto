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
parser grammar NRLActionParser;

options {
	output = AST;
	ASTLabelType = CommonTree;
}

import NRLConstraintRules;

tokens {
	// Definition of "virtual" tokens, i.e. all that are not returned by the lexer, but used
	// for tree construction only. As a convention, to make them easier to distinguish from lexical
	// values, they all start with VT_.
	VT_CONDITIONAL_ACTION;
	VT_COMPOUND_ACTION;
	VT_FOREACH_ACTION;
	VT_MACRO_APPLICATION;
	VT_OPERATOR_ACTION;
	VT_REMOVE_FROM_COLLECTION;
	VT_VARIABLE_DECLARATION_ACTION;
}

@header {
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
package net.sourceforge.nrl.parser.ast.impl;
	
import net.sourceforge.nrl.parser.ast.constraints.IArithmeticExpression;
import net.sourceforge.nrl.parser.ast.constraints.IBinaryPredicate;
import net.sourceforge.nrl.parser.ast.constraints.IExpression;
import net.sourceforge.nrl.parser.IStatusCode;
import net.sourceforge.nrl.parser.SyntaxError;
import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.ast.constraints.impl.VariableDeclarationImpl;	
import net.sourceforge.nrl.parser.ast.constraints.impl.ModelReferenceImpl;
}

@members {
	public java.util.List<NRLError> getSyntaxErrors() {
		return gNRLConstraintRules.getSyntaxErrors();
	}
	
	public boolean hasErrors() {
		return gNRLConstraintRules.hasErrors();
	}
	
	
	public void resetErrors() {
		gNRLConstraintRules.resetErrors();
	}
	
	public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
		gNRLConstraintRules.displayRecognitionError(tokenNames, e);
	}	

	public void initialisePositionFromChild(Object obj, int index) {
		if (obj instanceof Antlr3NRLBaseAst)
			((Antlr3NRLBaseAst) obj).initialisePositionFromChild(index);
	}
}

// -------------------------------- TOP LEVEL (SHARED) ----------------------------------------

fileBody
		:	modelFileReferences operatorFileReferences declarations EOF
			-> ^(VT_RULEFILE modelFileReferences operatorFileReferences? declarations?)
		;
		
modelFileReferences
		:	(modelFileReference)+
		;

modelFileReference
		:	MODEL^ DOUBLE_QUOTED_STRING
		;
		
operatorFileReferences
		:	(operatorFileReference)*
		;

operatorFileReference
		:	OPERATORS^ DOUBLE_QUOTED_STRING
		;		
		
declarations
		:	(declaration)*	
		;

declaration
		:	(context VALIDATION_RULE) => validationRuleDeclaration
		|	(VALIDATION_RULE) => validationRuleDeclaration
		|	(actionContext ACTION_RULE) => actionRuleDeclaration
		|	(ACTION_RULE) => actionRuleDeclaration
		|	(multipleContext ACTION_FRAGMENT) => actionFragmentDeclaration
		|	globalVariableDeclaration
		|	ruleSetDeclaration
		|	validationFragmentDeclaration
		;
		
// -------------------------------- ACTION LANGUAGE ----------------------------------------		

actionRuleDeclaration
		:	actionContext ACTION_RULE^ DOUBLE_QUOTED_STRING action
		|	ACTION_RULE^ DOUBLE_QUOTED_STRING APPLIES_TO! modelReference ( AND! USES! additionalContextList )? action
		;
		
actionFragmentDeclaration
		:	multipleContext ACTION_FRAGMENT^ DOUBLE_QUOTED_STRING action
		;

actionContext
		:	CONTEXT! modelReference
		|	CONTEXT! NO
		;
	
singleActionAndEOF
		:	action EOF!
		;
		
action
		:	compoundAction
		;
		
conditionalAction
@after { initialisePositionFromChild(retval.tree, 0); }
		:	IF cif=statement THEN cthen=action
			(
				options {
					greedy = true;
				} :
				ELSE celse=action
			)?
			SEMI
			-> ^(VT_CONDITIONAL_ACTION $cif $cthen $celse?)
		;

compoundAction
@after { initialisePositionFromChild(retval.tree, 0); }
		:	simpleActionList
			-> ^(VT_COMPOUND_ACTION simpleActionList)
		;
		
simpleActionList
		:	simpleAction ( (COMMA!)? (THEN!)? simpleAction )*
		;
		
simpleAction
		:	createAction
		|	(REMOVE modelReference FROM) => removeFromCollectionAction
		|	removeAction
		|	setAction
		|	addAction
		|	operatorAction
		|	macroApplicationAction
		|	forEachAction
		|	variableDeclarationAction
		|	conditionalAction	
		;
		
createAction
		:	CREATE^ NEW! modelReference (LPAREN! var=DOUBLE_QUOTED_STRING RPAREN!)?
		;
		
removeAction
		:	REMOVE^ (EACH!)? (OF!)? modelReference (LPAREN! var=DOUBLE_QUOTED_STRING RPAREN! WHERE! constraint)?
		;
		
removeFromCollectionAction
@after { initialisePositionFromChild(retval.tree, 0); }
		:	REMOVE source=modelReference FROM target=modelReference
			-> ^(VT_REMOVE_FROM_COLLECTION $source $target)
		;
		
setAction
		:	SET^ modelReference TO! expression
		;
		
addAction
		:	ADD_VERB^ expression TO! modelReference
		;
		
macroApplicationAction
@after { initialisePositionFromChild(retval.tree, 0); }
		:	id=PROPERTYNAME params=macroApplicationParams
			-> ^(VT_MACRO_APPLICATION $id $params)
		;
		
macroApplicationParams
		:	expression ( (AND!|FROM!|TO!|WITH!|USING!) expression )*
		;
		
operatorAction
@after { initialisePositionFromChild(retval.tree, 0); }
		:	OPERATOR operatorActionParams?
			-> ^(VT_OPERATOR_ACTION OPERATOR operatorActionParams?)
		;
		
operatorActionParams:
			expression ( (AND!|FROM!|TO!|WITH!|USING!) expression )*
		;
		
forEachAction!
@after { initialisePositionFromChild(retval.tree, 0); }
		:	EACH OF? modelReference COMMA? action SEMI 
			-> ^(VT_FOREACH_ACTION modelReference action)
		|	EACH var=DOUBLE_QUOTED_STRING IN_COLLECTION? OF? modelReference COMMA? action SEMI
			-> ^(VT_FOREACH_ACTION $var modelReference action)
		;
		
variableDeclarationAction
@after { initialisePositionFromChild(retval.tree, 0); }
		:	simpleVariableDeclaration
			-> ^(VT_VARIABLE_DECLARATION_ACTION simpleVariableDeclaration)
		;
		