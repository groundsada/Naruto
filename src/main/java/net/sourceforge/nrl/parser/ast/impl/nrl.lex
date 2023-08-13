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

import org.antlr.runtime.*;
import net.sourceforge.nrl.parser.IStatusCode;

%%
%public
%class NRLJFlexer

%line
%column
%char

%implements TokenSource
%type Token
%ignorecase
%unicode

%eofval{
	return token(Token.EOF, false);
%eofval}

%{
	// The amount to subtract from the character positions of tokens
	// This is needed to compensate for introduced { } characters, which
	// do not show in the text.
	// See the match on PROPERTYNAME for more.
	int posCompensation = 0;
	
	// Amount of columns to subtract in the current line
	int columnCompensation = 0;
	
	public String getSourceName() {
		return "";
	}
	
	private org.antlr.runtime.Token token(int type) {
		return token(type, false);
	}
	
	private org.antlr.runtime.Token token(int type, boolean stripStartAndEnd) {
		org.antlr.runtime.Token result = new org.antlr.runtime.CommonToken(type);
		result.setType(type);
		result.setCharPositionInLine(yycolumn - columnCompensation);
		result.setLine(yyline + 1);
				
		if (stripStartAndEnd) {
			String str = yytext();
			if (str.length() > 2)
				str = str.substring(1,str.length()-1);
			else
				str = "";
			result.setText(str);
		} else
			result.setText(yytext());
			
		return result;
	}
	
	public org.antlr.runtime.Token nextToken() {
		try {
			return yylex();
		} catch (java.io.IOException e) {
			return token(Token.EOF, false);
		} 
	}
%}

EndOfLine = \r|\r\n|\n
WhiteSpace = [ \t\f]
Comment = "--" [^\r\n]* {EndOfLine}

Alpha = [a-zA-Z]
Digit = [0-9]
AlphaNumeric = {Alpha}|{Digit}
 
DoubleQuotedString = \"[^\"\n\r]*\"
SingleQuotedString = (\'[^\'\n\r]*\')+

PropertyName = \{[^\}\n\r]*\}

First = "first"
Ordinal = "second" | "third" | ({Digit}+("st"|"nd"|"rd"|"th"))

IdentifierKeywordEscape = `
IdentifierFirstStep = ([:jletter:]|{IdentifierKeywordEscape})([:jletterdigit:]|[\-])*
IdentifierStep = ({IdentifierKeywordEscape})?([:jletterdigit:]|[\-])+
Identifier = {IdentifierFirstStep}("::" {IdentifierFirstStep})*("." {IdentifierStep})*

OperatorName = \[ [^\[\n\r]+ \]

%%

<YYINITIAL> {
	{Comment}			{ /* Ignore */ }
	"a" | "an" | "the" | "its" | "their"  { /* Ignore articles */ }
	"elements"			{ /* Ignore syntactic sugar plural suffix */ }

	/* --------------- TOP-LEVEL ----------------- */
	"model"				{ return token(NRLActionParser.MODEL); }
//	"include"			{ return token(NRLActionParser.INCLUDE); }
	"validation rule"	{ return token(NRLActionParser.VALIDATION_RULE); }	
	"validation fragment"	
						{ return token(NRLActionParser.VALIDATION_FRAGMENT); }
	"action rule"		{ return token(NRLActionParser.ACTION_RULE); }
	"action fragment"	{ return token(NRLActionParser.ACTION_FRAGMENT); }
	"context:"			{ return token(NRLActionParser.CONTEXT); }
	"rule set"			{ return token(NRLActionParser.RULESET); }
	"applies to"		{ return token(NRLActionParser.APPLIES_TO); }
	"uses"				{ return token(NRLActionParser.USES); }
	"operators"			{ return token(NRLActionParser.OPERATORS); }
		
	/* --------------- BOOLEANS ----------------- */
	
	"if"				{ return token(NRLActionParser.IF); }
	"then"				{ return token(NRLActionParser.THEN); }
	"else" | "otherwise"
						{ return token(NRLActionParser.ELSE); }
	
	"and"				{ return token(NRLActionParser.AND); }
	"or"				{ return token(NRLActionParser.OR); }
	"implies"			{ return token(NRLActionParser.IMPLIES); }
	"only if"			{ return token(NRLActionParser.IFF); }

	/* --------------- QUANTIFIERS ETC ----------------- */	
	"has" | "have" | "is" | "are"
						{ return token(NRLActionParser.HASHAVE); }
	
	"in"				{ return token(NRLActionParser.IN); }
	
	("for ")? "each" (" of the")?
						{ return token(NRLActionParser.EACH); }

	"that"				{ return token(NRLActionParser.THAT); }

	("every" | "all" | "any") (" of the")?	
						{ return token(NRLActionParser.EACH); }
	
	"following"			{ return token(NRLActionParser.FOLLOWING); }
	
	("is" | "are") " present"	
						{ return token(NRLActionParser.PRESENT); }
	("is" | "are") " not present"
						{ return token(NRLActionParser.NOT_PRESENT); }
						
	"there " ("is" | "are")
						{ return token(NRLActionParser.THERE_IS); }
						
	"of"				{ return token(NRLActionParser.OF); }
						
	"no" | "none"		{ return token(NRLActionParser.NO); }
	"one"				{ return token(NRLActionParser.ONE); }
	"two"				{ return token(NRLActionParser.TWO); }
	"three"				{ return token(NRLActionParser.THREE); }
	"four"				{ return token(NRLActionParser.FOUR); }
	
	"exactly"			{ return token(NRLActionParser.EXACTLY); }
	"at least"			{ return token(NRLActionParser.AT_LEAST); }
	"at most"			{ return token(NRLActionParser.AT_MOST); }
	
	"represent"|
	"represents"		{ return token(NRLActionParser.REPRESENT); }

	"in the collection" { return token(NRLActionParser.IN_COLLECTION); }
		
	/* --------------- PREDICATES ----------------- */
	"="|
	("is ")? "equal to"	{ return token(NRLActionParser.EQUALS); }
	
	"<>"|
	("is ")? "not equal to"
						{ return token(NRLActionParser.NOT_EQUALS); }
						
	">"|
	("is ")? "greater than"|
	("is ")? "after"	{ return token(NRLActionParser.GREATER); }
	
	">="|
	("is ")? "greater than or equal to"
						{ return token(NRLActionParser.GREATER_EQ); }
						
	"<"|
	("is ")? "less than"|
	("is ")? "before"	{ return token(NRLActionParser.LESS); }
	
	"<="|
	("is ")? "less than or equal to"|
	("is ")? "before or on"
						{ return token(NRLActionParser.LESS_EQ); }

	"is one of"			{ return token(NRLActionParser.IS_IN); }
	"is not one of"		{ return token(NRLActionParser.IS_NOT_IN); }

	("is" | "are") " a kind of" 
						{ return token(NRLActionParser.KIND_OF); }
		
	"sum of"			{ return token(NRLActionParser.SUM_OF); }
	"number of"			{ return token(NRLActionParser.NUMBER_OF); }
	"unique"			{ return token(NRLActionParser.UNIQUE); }
	"by"				{ return token(NRLActionParser.BY); }
	
	/* --------------- SYMBOLS ----------------- */	
	"("					{ return token(NRLActionParser.LPAREN); }
	")"					{ return token(NRLActionParser.RPAREN); }

	"+"					{ return token(NRLActionParser.ADD); }
	"-"					{ return token(NRLActionParser.MINUS); }
	"*"					{ return token(NRLActionParser.TIMES); }
	"/"					{ return token(NRLActionParser.DIV); }
	"mod"				{ return token(NRLActionParser.MOD); }

	":"					{ return token(NRLActionParser.COLON); }		
	";"					{ return token(NRLActionParser.SEMI); }
	","					{ return token(NRLActionParser.COMMA); }
		
	/* --------------- Report ----------------- */	
	"report"			{ return token(NRLActionParser.REPORT); }

	/* ----------------- ACTIONS ----------------- */
	"create"			{ return token(NRLActionParser.CREATE); }
	"add"				{ return token(NRLActionParser.ADD_VERB); }
	"set"				{ return token(NRLActionParser.SET); }
	"remove" | "clear"	{ return token(NRLActionParser.REMOVE); }
	"new"				{ return token(NRLActionParser.NEW); }
	"to"				{ return token(NRLActionParser.TO); }
	"from"				{ return token(NRLActionParser.FROM); }
	"with"				{ return token(NRLActionParser.WITH); }
	"using"				{ return token(NRLActionParser.USING); }
		
	/* ---------------Values/Ids ----------------- */	
	"where"				{ return token(NRLActionParser.WHERE); }

	"as a" | "as an"	{ return token(NRLActionParser.AS_A); }

	"true" | "false"	{ return token(NRLActionParser.BOOLEAN); }
	
	{First}				{ return token(NRLActionParser.FIRST); }
	{Ordinal}			{ return token(NRLActionParser.ORDINAL_NUMBER); }
	
	{Identifier}		{ return token(NRLActionParser.IDENTIFIER); }
	
	{PropertyName}		{ 
						  columnCompensation++; posCompensation++;
						  Token tk = token(NRLActionParser.PROPERTYNAME, true);
						  columnCompensation++; posCompensation++;
						  return tk; 
						}
	{DoubleQuotedString}
						{ return token(NRLActionParser.DOUBLE_QUOTED_STRING, true); }
	{SingleQuotedString}
						{ return token(NRLActionParser.SINGLE_QUOTED_STRING, true); }
	{OperatorName}		{ return token(NRLActionParser.OPERATOR, true); }
	
	{Digit}+			{ return token(NRLActionParser.INTEGER_NUMBER); }
	{Digit}+"."{Digit}* 
						{ return token(NRLActionParser.DECIMAL_NUMBER); }
							
	{EndOfLine}+		{ /* Ignore */ columnCompensation = 0; }
	{WhiteSpace}+		{ /* Ignore */ }
}

.|\n { 
	throw new SyntaxErrorException(new net.sourceforge.nrl.parser.SyntaxError(
		IStatusCode.LEXER_ERROR, yyline+1, yycolumn, 1, "Unexpected character: "+yytext())
	);
}
