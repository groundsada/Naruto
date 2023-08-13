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
package net.sourceforge.nrl.parser;

/**
 * An interface of status code constants for the syntax and semantic error
 * classes.
 * <p>
 * These status codes give a rough indication of the type of error that occurred
 * and can be referred to in manuals.
 * 
 * @author Christian Nentwich
 */
public interface IStatusCode {

	// -------------------------------------
	// Syntax errors: 2000 forward
	// -------------------------------------

	/** Lexer error - invalid character */
	public final static int LEXER_ERROR = 2001;

	/** Unexpected token */
	public final static int PARSER_ERROR = 2002;

	/** Invalid variable name (e.g. spaces in it) */
	public final static int INVALID_VARIABLE_NAME = 2003;

	// -------------------------------------
	// Basic semantic errors: 3000 forward
	// -------------------------------------

	/** Duplicate rule id */
	public final static int DUPLICATE_RULE = 3001;

	/** Duplicate fragment id */
	public final static int DUPLICATE_FRAGMENT = 3002;

	/** Invalid property reference */
	public final static int INVALID_FRAGMENT_REF = 3003;

	/** Ambiguous implied existence reference */
	public final static int AMBIGUOUS_EXISTENCE = 3004;

	/** Duplicate rule set identifier */
	public final static int DUPLICATE_RULESET = 3005;

	/** Duplicate global variable */
	public final static int DUPLICATE_GLOBAL_VARIABLE = 3006;

	/** Property reference cycle */
	public final static int FRAGMENT_CYCLICAL_REFERENCE = 3007;

	/** Additional rule parameter is a duplicate */
	public final static int DUPLICATE_RULE_PARAMETER = 3008;
	
	/** Unknown operator reference */
	public final static int OPERATOR_UNKNOWN = 3100;

	/** Mismatch between parameter definition and operator invocation */
	public final static int OPERATOR_PARAMETER_MISMATCH = 3101;

	// -------------------------------------
	// Basic action semantic errors: 3501 forward
	// -------------------------------------

	/** Duplicate fragment identifier */
	public final static int DUPLICATE_ACTION_FRAGMENT = 3501;

	/** Reference to non-existent fragment */
	public final static int INVALID_ACTION_FRAGMENT_REF = 3502;

	// -------------------------------------
	// Model resolver errors: 4000 forward
	// -------------------------------------

	/** The model file was not found */
	public final static int MODEL_NOT_FOUND = 4000;

	/** Context element not found */
	public final static int CONTEXT_UNKNOWN = 4001;

	/** Context element refers to a simple type */
	public final static int CONTEXT_SIMPLE_TYPE = 4002;

	/** Global element reference ambiguous */
	public final static int ELEMENT_AMBIGUOUS = 4003;

	/** Reference to an unknown element or attribute */
	public final static int UNKNOWN_ELEMENT_OR_ATTRIBUTE = 4004;

	/** Reference to an unknown attribute */
	public final static int UNKNOWN_ATTRIBUTE = 4005;

	/** Reference to an element where only an attribute should be allowed */
	public final static int ILLEGAL_ELEMENT_REFERENCE = 4006;

	/** Tried to navigate attributes of a simple data type (impossible) */
	public final static int DATATYPE_NAVIGATION = 4007;

	/** Tried to make a static reference to a non-static attribute */
	public final static int STATIC_REFERENCE_TO_NONSTATIC_ATTRIBUTE = 4008;

	/** A name referenced is not a package (but should be) */
	public final static int INVALID_PACKAGE_REFERENCE = 4009;

	/** Context must refer to a single model element, without navigation */
	public final static int CONTEXT_NAVIGATION = 4010;

	/** An attribute reference is ambiguous (occurs in multiple contexts). */
	public final static int ATTRIBUTE_AMBIGUOUS = 4011;

	/** Tried to navigate from a variable bound to an expression */
	public final static int ILLEGAL_VARIABLE_NAVIGATION = 4012;

	/** Duplicate variable declaration */
	public final static int DUPLICATE_VARIABLE = 4013;

	/** Tried to use global variable as a non-constant */
	public final static int GLOBAL_VARIABLE_NAVIGATION = 4014;

	/** Reference to static variable where none are allowed */
	public final static int ILLEGAL_STATIC_REFERENCE = 4015;

	/** Rule parameter name clashes with an global element or an attribute of the rule context */
	public final static int RULE_PARAMETER_NAME_CLASH = 4016;

	/** Rule parameter name clashes with an global element or an attribute of the rule context */
	public final static int VARIABLE_NAME_SHADOWS_MODEL_ELEMENT = 4017;
	
	/** Attempted access of an enum literal via an instance of the enum, not the enum type */
	public final static int ACCESS_TO_ENUM_LITERAL_NOT_THROUGH_CLASSIFIER = 4018;
	
	// -------------------------------------
	// Action model resolver errors: 4500 forward
	// -------------------------------------

	/** Incompatible type being created (e.g. not classifier) */
	public final static int CREATE_REFERENCE_INVALID = 4501;

	/** Incompatible type being selected from (e.g. not classifier) */
	public final static int RETRIEVE_REFERENCE_INVALID = 4502;

	// -------------------------------------
	// Type checker errors: 5000 forward
	// -------------------------------------

	/** A rule has been declared with a non-boolean constraint */
	public final static int RULE_NOT_BOOLEAN = 5001;

	/** A property has been declared with a non-boolean constraint */
	public final static int PROPERTY_NOT_BOOLEAN = 5002;

	/** The arguments to a binary operator (and, or...) were not boolean */
	public final static int BINARY_OPERATOR_ARGUMENTS_NOT_BOOLEAN = 5003;

	/** The arguments to an if were not boolean */
	public final static int IF_ARGUMENTS_NOT_BOOLEAN = 5004;

	/** The arguments to a then were not boolean */
	public final static int THEN_ARGUMENTS_NOT_BOOLEAN = 5005;

	/** The arguments to an else were not boolean */
	public final static int ELSE_ARGUMENTS_NOT_BOOLEAN = 5006;

	/** Tried to apply a property to the wrong type of object */
	public final static int PROPERTY_CONTEXT_MISMATCH = 5007;

	/** Arithmetic expression over invalid arguments */
	public final static int ARITHMETIC_EXPRESSION_ARGS_INVALID = 5008;

	/** Complex object used as argument in binary predicate */
	public final static int BINARY_PREDICATE_ARGUMENT_COMPLEX = 5009;

	/** Collection used as argument in binary predicate */
	public final static int BINARY_PREDICATE_ARGUMENT_COLLECTION = 5010;

	/** Data type or other complex element found where a collection was expected */
	public final static int COLLECTION_EXPECTED = 5011;

	/** Implicit iteration */
	public final static int IMPLICIT_ITERATION = 5012;

	/** Number type was expected */
	public final static int NUMBER_EXPECTED = 5013;

	/**
	 * Quantifiers (exists, forall, not exists) expect a boolean condition as a
	 * sub-element.
	 */
	public final static int QUANTIFIER_ARGUMENT_NOT_BOOLEAN = 5014;

	/** Binary predicate arguments are not compatible */
	public final static int BINARY_PREDICATE_ARGUMENT_INCOMPATIBLE = 5015;

	/** "is in" expression not compatible */
	public final static int IS_IN_EXPRESSION_INCOMPATIBLE = 5016;

	/** Rule set precondition is not a boolean */
	public final static int RULESET_PRECONDITION_NOT_BOOLEAN = 5017;

	/** Wrong number/type of property parameters */
	public final static int FRAGMENT_PARAMETER_MISMATCH = 5018;

	/** Complex results not allowed in property */
	public final static int FRAGMENT_RESULT_COMPLEX = 5019;

	/** If statement with multiple return types */
	public final static int IF_STATEMENT_MULTIPLE_TYPES = 5020;

	/** Cast expression needs a subtype to cast to */
	public final static int CAST_REQUIRES_SUBTYPE = 5021;

	/** Collection attributes cannot be cast */
	public final static int CANNOT_CAST_COLLECTION = 5022;

	/** Model element or operator type is unknown */
	public final static int UNKNOWN_DATATYPE = 5023;

	/** Operator parameter type mismatch */
	public final static int OPERATOR_TYPE_MISMATCH = 5024;

	/** Operator void type in expression */
	public final static int VOID_OPERATOR_IN_EXPRESSION = 5025;

	/** Operator parameter/return type is unknown */
	public final static int OPERATOR_TYPE_UNKNOWN = 5026;

	/** "is in" expression has a type mismatch with the list of values */
	public final static int IS_IN_EXPRESSION_TYPE_MISMATCH = 5027;

	/** "is in" has an invalid entry in the list */
	public final static int IS_IN_LIST_ENTRY_INVALID = 5028;

	/** Cannot cast a collection */
	public final static int CAST_COLLECTION = 5029;

	/** Cannot pass collections as parameters */
	public final static int OPERATOR_COLLECTION_PARAMETER = 5030;

	/** Selection expression constraints must be boolean. */
	public final static int SELECTION_CONSTRAINT_NOT_BOOLEAN = 5031;
	
	// -------------------------------------
	// Action type checker errors: 5500 forward
	// -------------------------------------

	/** Where clause is not boolean */
	public final static int WHERE_NOT_BOOLEAN = 5501;

	/** Illegal to delete a single attribute with a where clause */
	public final static int WHERE_CLAUSE_DISALLOWED = 5502;

	/** Iteration only allowed over collections */
	public final static int ITERATION_NEEDS_COLLECTION = 5503;

	/** Removal only allowed from collections */
	public final static int REMOVE_NEEDS_COLLECTION = 5504;

	/** Removing incompatible type from collection */
	public final static int REMOVE_TYPES_INCOMPATIBLE = 5505;

	/** Assignment between incompatible types (e.g. number = date) */
	public final static int ASSIGNMENT_TYPE_INCOMPATIBLE = 5506;

	/** Addition only allowed with collections */
	public final static int ADD_NEEDS_COLLECTION = 5507;

	/** Addition of object to incompatible collection */
	public final static int ADD_TYPES_INCOMPATIBLE = 5508;

	/** Cannot set model elements or variables */
	public final static int ILLEGAL_ASSIGNMENT_TARGET = 5509;

	/** Something wrong with macro invocation parameters */
	public final static int ACTION_FRAGMENT_PARAMETER_MISMATCH = 5510;

	/** Constraint and action rules mixed in set */
	public final static int RULESET_MIXES_RULETYPES = 5511;

	/** Cannot remove a collection */
	public final static int REMOVE_CANNOT_REMOVE_COLLECTION = 5512;
}
