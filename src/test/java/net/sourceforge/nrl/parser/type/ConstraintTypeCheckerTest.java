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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.List;

import net.sourceforge.nrl.parser.IStatusCode;
import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.NRLParser;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.IRuleFile;
import net.sourceforge.nrl.parser.ast.NRLDataType;
import net.sourceforge.nrl.parser.ast.NRLDataType.Type;
import net.sourceforge.nrl.parser.ast.constraints.IBinaryPredicate;
import net.sourceforge.nrl.parser.ast.constraints.ICollectionIndex;
import net.sourceforge.nrl.parser.ast.constraints.IConstraintRuleDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.IFunctionalExpression;
import net.sourceforge.nrl.parser.ast.constraints.IIsInPredicate;
import net.sourceforge.nrl.parser.ast.constraints.IIsNotInPredicate;
import net.sourceforge.nrl.parser.ast.constraints.IIsSubtypePredicate;
import net.sourceforge.nrl.parser.ast.constraints.IOperatorInvocation;
import net.sourceforge.nrl.parser.ast.constraints.IValidationFragmentDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.IVariableDeclaration;
import net.sourceforge.nrl.parser.model.ModelCollection;
import net.sourceforge.nrl.parser.operators.IOperator;
import net.sourceforge.nrl.parser.operators.IOperators;
import net.sourceforge.nrl.parser.operators.XmlOperatorPersistence;
import net.sourceforge.nrl.parser.operators.XmlOperatorPersistence.LoadedVersion;

import org.junit.Test;

public class ConstraintTypeCheckerTest extends NRLParserTestSupport {

	/**
	 * Test loading a file that should have no errors
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testCheck_NoErrors() throws Exception {
		// Parse the file, assign model references
		NRLParser parser = new NRLParser();
		IRuleFile ruleFile = parser.parse(new FileInputStream(
				"src/test/resources/parsing/type-check.nrl"));
		assertEquals(0, parser.getErrors().size());

		ModelCollection models = new ModelCollection();
		models.addModelPackage(getBasicModel());

		parser.resolveModelReferences(ruleFile, models);
		assertEquals(0, parser.getErrors().size());

		// Now type check
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		TypeMapping mapping = new TypeMapping();
		mapping.addMapping("*", "Date", NRLDataType.DATE);
		typeChecker.addTypeMapping(mapping);

		List<NRLError> errors = typeChecker.check(ruleFile);
		assertEquals(0, errors.size());
	}

	/*
	 * Type assignment for operator files
	 */
	@Test
	public void testCheck_Operators() throws Exception {
		ModelCollection models = new ModelCollection();
		models.addModelPackage(getBasicModel());

		ITypeChecker typeChecker = new ConstraintTypeChecker();

		XmlTypeMapping mapping = new XmlTypeMapping();
		mapping.load(new File("src/test/resources/type/default-mapping.xml"));
		typeChecker.addTypeMapping(mapping);

		// Primitive types - should already be assigned
		XmlOperatorPersistence loader = new XmlOperatorPersistence();
		LoadedVersion version = loader.load(new File(
				"src/test/resources/operators/resolve-primitives.xml"));
		assertEquals(LoadedVersion.Version14, version);

		IOperators ops = loader.getOperators();
		assertNotNull(ops);

		assertEquals(0, ops.resolveModelReferences(models).size());

		assertEquals(0, typeChecker.check(ops).size());

		IOperator op = ops.getOperators().get(0);

		assertEquals(NRLDataType.STRING, op.getParameter("string").getNRLDataType());
		assertEquals(NRLDataType.INTEGER, op.getParameter("int").getNRLDataType());
		assertEquals(NRLDataType.INTEGER, op.getParameter("integer").getNRLDataType());
		assertEquals(NRLDataType.DECIMAL, op.getParameter("float").getNRLDataType());
		assertEquals(NRLDataType.DECIMAL, op.getParameter("double").getNRLDataType());
		assertEquals(NRLDataType.DECIMAL, op.getParameter("number").getNRLDataType());
		assertEquals(NRLDataType.BOOLEAN, op.getParameter("boolean").getNRLDataType());
		assertEquals(NRLDataType.DATE, op.getParameter("date").getNRLDataType());

		assertEquals(NRLDataType.VOID, op.getNRLReturnType());

		// Model types - these should be "ELEMENT"
		loader.load(new File("src/test/resources/operators/model-types-resolved.xml"));
		ops = loader.getOperators();
		assertEquals(0, ops.resolveModelReferences(models).size());

		assertEquals(0, typeChecker.check(ops).size());

		op = ops.getOperators().get(0);
		assertEquals(NRLDataType.ELEMENT, op.getParameters().get(0).getNRLDataType());
		assertEquals(NRLDataType.ELEMENT, op.getNRLReturnType());
	}

	@Test
	public void testCheck_ArithmeticExpression() throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		XmlTypeMapping mapping = new XmlTypeMapping();
		mapping.load(new File("src/test/resources/type/default-mapping.xml"));
		typeChecker.addTypeMapping(mapping);

		// Plus expression on strings -> legal
		List<NRLError> errors = typeChecker.check(createTestFile("'a' + 'a' = 'aa'"));
		assertEquals(0, errors.size());

		// Times expression on two numbers -> legal
		errors = typeChecker.check(createTestFile("2 * 4 = 8"));
		assertEquals(0, errors.size());

		// Other arithmetic expression on strings -> illegal
		errors = typeChecker.check(createTestFile("'a' - 'a' = 4"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.ARITHMETIC_EXPRESSION_ARGS_INVALID, ((NRLError) errors.iterator()
				.next()).getStatusCode());

		// Mixed numbers and strings -> legal, becomes string
		IRuleFile file = createTestFile("2 + 'a' = 'c'");
		errors = typeChecker.check(file);
		assertEquals(0, errors.size());
		assertEquals(NRLDataType.STRING, ((IBinaryPredicate) ((IConstraintRuleDeclaration) file
				.getRuleById("test-rule")).getConstraint()).getLeft().getNRLDataType());

		file = createTestFile("tradeheader.masterAgreement + 2 = 'c'");
		errors = typeChecker.check(file);
		assertEquals(0, errors.size());
		assertEquals(NRLDataType.STRING, ((IBinaryPredicate) ((IConstraintRuleDeclaration) file
				.getRuleById("test-rule")).getConstraint()).getLeft().getNRLDataType());

		file = createTestFile("5 + 6 + tradeheader.masterAgreement = 'c'");
		errors = typeChecker.check(file);
		assertEquals(0, errors.size());
		assertEquals(NRLDataType.STRING, ((IBinaryPredicate) ((IConstraintRuleDeclaration) file
				.getRuleById("test-rule")).getConstraint()).getLeft().getNRLDataType());

		// Mixed string and complex object -> legal, becomes string
		file = createTestFile("'a' + tradeheader + 5 = 'c'");
		errors = typeChecker.check(file);
		assertEquals(0, errors.size());
		assertEquals(NRLDataType.STRING, ((IBinaryPredicate) ((IConstraintRuleDeclaration) file
				.getRuleById("test-rule")).getConstraint()).getLeft().getNRLDataType());

		file = createTestFile("tradeheader + 'a' + 5 = 'c'");
		errors = typeChecker.check(file);
		assertEquals(0, errors.size());
		assertEquals(NRLDataType.STRING, ((IBinaryPredicate) ((IConstraintRuleDeclaration) file
				.getRuleById("test-rule")).getConstraint()).getLeft().getNRLDataType());

		// Unknown operator -> legal, becomes unknown
		file = createTestFile("5 + [unknown] = 0");
		errors = typeChecker.check(file);
		assertEquals(NRLDataType.UNKNOWN, ((IBinaryPredicate) ((IConstraintRuleDeclaration) file
				.getRuleById("test-rule")).getConstraint()).getLeft().getNRLDataType());

		// Enumerated string type -> lega
		errors = typeChecker.check(createTestFile("each of the legs (fixFloat + 'c' = 'd')"));
		assertEquals(0, errors.size());

		// String collection -> cannot use +
		errors = typeChecker.check(createTestFile("description + 'c' = 'd'"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.ARITHMETIC_EXPRESSION_ARGS_INVALID, errors.get(0).getStatusCode());

		// Date + number = date
		file = createTestFile("tradeDate + 0 = tradeDate");
		errors = typeChecker.check(file);
		assertEquals(0, errors.size());
		assertEquals(NRLDataType.DATE, ((IBinaryPredicate) ((IConstraintRuleDeclaration) file
				.getRuleById("test-rule")).getConstraint()).getLeft().getNRLDataType());

		// Illegal operation on a date
		file = createTestFile("1 * tradeDate = tradeDate");
		errors = typeChecker.check(file);
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.ARITHMETIC_EXPRESSION_ARGS_INVALID, errors.get(0).getStatusCode());
	}

	@Test
	public void testCheck_BinaryOperator() throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		// Check a valid one
		List<NRLError> errors = typeChecker.check(createTestFile("'a' = 'a' and 'b' = 'b'"));
		assertEquals(0, errors.size());

		// Invalid (left argument not boolean)
		errors = typeChecker.check(createTestFile("'a' or 'b' = 'b'"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.BINARY_OPERATOR_ARGUMENTS_NOT_BOOLEAN, ((NRLError) errors
				.iterator().next()).getStatusCode());

		// Invalid (right argument not boolean)
		errors = typeChecker.check(createTestFile("'a' = 'a' only if 'b'"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.BINARY_OPERATOR_ARGUMENTS_NOT_BOOLEAN, ((NRLError) errors
				.iterator().next()).getStatusCode());

		// Valid (either argument unknown)
		IRuleFile file = createTestFile("'a' = 'a' and [unknown]");
		errors = typeChecker.check(file);
		assertEquals(0, errors.size());
		assertEquals(NRLDataType.BOOLEAN, ((IConstraintRuleDeclaration) file
				.getRuleById("test-rule")).getConstraint().getNRLDataType());
	}

	@Test
	public void testCheck_BinaryPredicate() throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		XmlTypeMapping mapping = new XmlTypeMapping();
		mapping.load(new File("src/test/resources/type/default-mapping.xml"));
		typeChecker.addTypeMapping(mapping);

		// String to string -> valid
		List<NRLError> errors = typeChecker.check(createTestFile("'a' = 'a'"));
		assertEquals(0, errors.size());

		// String to number -> invalid
		errors = typeChecker.check(createTestFile("'a' = 2"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.BINARY_PREDICATE_ARGUMENT_INCOMPATIBLE, ((NRLError) errors
				.iterator().next()).getStatusCode());

		errors = typeChecker.check(createTestFile("2 = 'a'"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.BINARY_PREDICATE_ARGUMENT_INCOMPATIBLE, ((NRLError) errors
				.iterator().next()).getStatusCode());

		// Date to date -> valid
		errors = typeChecker.check(createTestFile("tradeDate = tradeDate"));
		assertEquals(0, errors.size());

		// Date to string -> valid
		errors = typeChecker.check(createTestFile("tradeDate = '2005-01-01'"));
		assertEquals(0, errors.size());
		errors = typeChecker.check(createTestFile("'2005-01-01' = tradeDate"));
		assertEquals(0, errors.size());

		// Date to anything else -> invalid
		errors = typeChecker.check(createTestFile("25 = tradeDate"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.BINARY_PREDICATE_ARGUMENT_INCOMPATIBLE, ((NRLError) errors
				.iterator().next()).getStatusCode());

		// complex type to string -> invalid
		errors = typeChecker.check(createTestFile("2 = tradeheader"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.BINARY_PREDICATE_ARGUMENT_COMPLEX, ((NRLError) errors.iterator()
				.next()).getStatusCode());

		errors = typeChecker.check(createTestFile("legs = 2"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.BINARY_PREDICATE_ARGUMENT_COLLECTION, ((NRLError) errors
				.iterator().next()).getStatusCode());

		// enumeration value to enumeration attribute -> valid
		errors = typeChecker
				.check(createTestFile("one of the legs has fixFloat = FixFloatEnum.FIXED"));
		assertEquals(0, errors.size());

		// enumeration value to derived enumeration attribute -> valid
		errors = typeChecker
				.check(createTestFile("one of the legs has extendedFixFloat = FixFloatEnum.FIXED"));
		assertEquals(0, errors.size());

		errors = typeChecker
				.check(createTestFile("one of the legs has FixFloatEnum.FIXED = extendedFixFloat"));
		assertEquals(0, errors.size());

		// string enumeration value to string -> valid
		errors = typeChecker.check(createTestFile("one of the legs has fixFloat = 'FIXED'"));
		assertEquals(0, errors.size());

		// string enumeration value to number -> invalid
		errors = typeChecker.check(createTestFile("one of the legs has fixFloat = 5"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.BINARY_PREDICATE_ARGUMENT_INCOMPATIBLE, errors.get(0)
				.getStatusCode());

		// number enumeration value to string -> invalid
		errors = typeChecker.check(createTestFile("counter = 'FIXED'"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.BINARY_PREDICATE_ARGUMENT_INCOMPATIBLE, errors.get(0)
				.getStatusCode());

		// untyped enum to string -> invalid
		errors = typeChecker.check(createTestFile("untyped = 'test'"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.BINARY_PREDICATE_ARGUMENT_COMPLEX, errors.get(0).getStatusCode());

		// untyped enum to untyped literal -> valid
		errors = typeChecker.check(createTestFile("untyped = UntypedEnum.LITERAL"));
		assertEquals(0, errors.size());

		// untyped enum to untyped enum -> valid
		errors = typeChecker.check(createTestFile("untyped = untyped"));
		assertEquals(0, errors.size());

		// derived type to int -> valid
		errors = typeChecker.check(createTestFile("age = 15"));
		assertEquals(0, errors.size());
	}

	@Test
	public void testCheck_CastExpression() throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		XmlTypeMapping mapping = new XmlTypeMapping();
		mapping.load(new File("src/test/resources/type/default-mapping.xml"));
		typeChecker.addTypeMapping(mapping);

		// valid
		List<NRLError> errors = typeChecker
				.check(createTestFile("\"x\" represents the Trade as a Trade, "
						+ "x.tradeDate = '2005-12-12'"));
		assertEquals(0, errors.size());

		// invalid (not sub-type)
		errors = typeChecker.check(createTestFile("\"x\" represents the tradeDate as a Trade, "
				+ "x.tradeDate = '2005-12-12'"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.CAST_REQUIRES_SUBTYPE, ((NRLError) errors.iterator().next())
				.getStatusCode());

		errors = typeChecker.check(createTestFile("\"x\" represents the legs as a IRLeg, "
				+ "x.fixFloat = FixFloatEnum.FIXED"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.CANNOT_CAST_COLLECTION, ((NRLError) errors.iterator().next())
				.getStatusCode());
	}

	@Test
	public void testCheck_ConstraintRuleVariableDeclarationInitialisedToElementIsIllegal()
			throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		XmlTypeMapping mapping = new XmlTypeMapping();
		mapping.load(new File("src/test/resources/type/default-mapping.xml"));
		typeChecker.addTypeMapping(mapping);

		// valid
		List<NRLError> errors = typeChecker
				.check(createTestFile("\"x\" is an IRLeg, " + "1 = 1"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.ILLEGAL_ELEMENT_REFERENCE, ((NRLError) errors.iterator().next())
				.getStatusCode());
	}

	@Test
	public void testCheck_CollectionIndex() throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		XmlTypeMapping mapping = new XmlTypeMapping();
		mapping.load(new File("src/test/resources/type/default-mapping.xml"));
		typeChecker.addTypeMapping(mapping);

		// Valid (complex object)
		IRuleFile ruleFile = createTestFile("the first of the legs");
		List<NRLError> errors = typeChecker.check(ruleFile);
		assertEquals(1, errors.size());
		assertEquals(
				NRLDataType.Type.Element,
				((ICollectionIndex) ((IConstraintRuleDeclaration) ruleFile.getRuleById("test-rule"))
						.getConstraint()).getCollection().getNRLDataType().getType());
		assertTrue(!((ICollectionIndex) ((IConstraintRuleDeclaration) ruleFile
				.getRuleById("test-rule")).getConstraint()).getNRLDataType().isCollection());
		assertEquals(IStatusCode.RULE_NOT_BOOLEAN, errors.get(0).getStatusCode());

		// Invalid (parameter not a collection)
		ruleFile = createTestFile("the first tradeDate");
		errors = typeChecker.check(ruleFile);
		assertEquals(2, errors.size());
		assertEquals(IStatusCode.COLLECTION_EXPECTED, errors.get(0).getStatusCode());

		// Invalid (complex comparison)
		ruleFile = createTestFile("the first legs = 3");
		errors = typeChecker.check(ruleFile);
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.BINARY_PREDICATE_ARGUMENT_COMPLEX, errors.get(0).getStatusCode());
	}

	@Test
	public void testCheck_ExistsStatement() throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		XmlTypeMapping mapping = new XmlTypeMapping();
		mapping.load(new File("src/test/resources/type/default-mapping.xml"));
		typeChecker.addTypeMapping(mapping);

		// valid
		List<NRLError> errors = typeChecker
				.check(createTestFile("one of the legs has fixFloat = FixFloatEnum.FIXED"));
		assertEquals(0, errors.size());

		// valid
		errors = typeChecker.check(createTestFile("tradeDate is present"));
		assertEquals(0, errors.size());

		// valid (implicit boolean)
		errors = typeChecker.check(createTestFile("one of the legs has a fixFloat"));
		assertEquals(0, errors.size());

		// Cannot traverse a non-collection
		errors = typeChecker.check(createTestFile("one of the tradeDate has 'a' = 'a'"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.COLLECTION_EXPECTED, ((NRLError) errors.iterator().next())
				.getStatusCode());

		errors = typeChecker
				.check(createTestFile("one of the legs has (fixFloat is FixFloatEnum.FIXED)"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.COLLECTION_EXPECTED, ((NRLError) errors.iterator().next())
				.getStatusCode());

		// Non-boolean condition
		errors = typeChecker.check(createTestFile("one of the legs has 'a'"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.QUANTIFIER_ARGUMENT_NOT_BOOLEAN, ((NRLError) errors.iterator()
				.next()).getStatusCode());
	}

	@Test
	public void testCheck_ForallStatement() throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		XmlTypeMapping mapping = new XmlTypeMapping();
		mapping.load(new File("src/test/resources/type/default-mapping.xml"));
		typeChecker.addTypeMapping(mapping);

		// valid
		List<NRLError> errors = typeChecker
				.check(createTestFile("each of the legs has fixFloat = FixFloatEnum.FIXED"));
		assertEquals(0, errors.size());

		// valid (implied boolean)
		errors = typeChecker.check(createTestFile("each of the legs has a fixFloat"));
		assertEquals(0, errors.size());

		// Cannot traverse a non-collection
		errors = typeChecker.check(createTestFile("each of the tradeDate has 'a' = 'a'"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.COLLECTION_EXPECTED, ((NRLError) errors.iterator().next())
				.getStatusCode());

		// Non-boolean condition
		errors = typeChecker.check(createTestFile("each of the legs has 'a'"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.QUANTIFIER_ARGUMENT_NOT_BOOLEAN, ((NRLError) errors.iterator()
				.next()).getStatusCode());
	}

	@Test
	public void testCheck_FunctionalExpression() throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		XmlTypeMapping mapping = new XmlTypeMapping();
		mapping.load(new File("src/test/resources/type/default-mapping.xml"));
		typeChecker.addTypeMapping(mapping);

		// number of -> valid
		List<NRLError> errors = typeChecker.check(createTestFile("the number of legs = 5"));
		assertEquals(0, errors.size());

		// number of -> not a collection
		errors = typeChecker.check(createTestFile("the number of tradeheader = 5"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.COLLECTION_EXPECTED, ((NRLError) errors.iterator().next())
				.getStatusCode());

		// sum of -> valid
		errors = typeChecker.check(createTestFile("the sum of legs.value = 5"));
		assertEquals(0, errors.size());

		// sum of -> invalid, not a number
		errors = typeChecker.check(createTestFile("the sum of legs.fixFloat = 5"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.NUMBER_EXPECTED, ((NRLError) errors.iterator().next())
				.getStatusCode());

		// sum of -> invalid, needs to be a number
		errors = typeChecker.check(createTestFile("the sum of legs = 5"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.NUMBER_EXPECTED, ((NRLError) errors.iterator().next())
				.getStatusCode());

		// sum of -> not a collection
		errors = typeChecker.check(createTestFile("the sum of tradeheader.masterAgreement = 5"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.COLLECTION_EXPECTED, ((NRLError) errors.iterator().next())
				.getStatusCode());
	}

	@Test
	public void testCheck_IfThenStatement() throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		// valid
		List<NRLError> errors = typeChecker
				.check(createTestFile("if 'a' = 'a' then 'b' = 'b' else 'c' = 'c'"));
		assertEquals(0, errors.size());

		// if argument not boolean
		errors = typeChecker.check(createTestFile("if 'a' then 'b' = 'b' else 'c' = 'c'"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.IF_ARGUMENTS_NOT_BOOLEAN, ((NRLError) errors.iterator().next())
				.getStatusCode());

		// mixed types
		errors = typeChecker.check(createTestFile("if 'a' = 'a' then 'b' else 2"));
		assertEquals(2, errors.size());
		assertEquals(IStatusCode.IF_STATEMENT_MULTIPLE_TYPES, ((NRLError) errors.iterator().next())
				.getStatusCode());
	}

	@Test
	public void testCheck_IsInPredicate() throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		XmlTypeMapping mapping = new XmlTypeMapping();
		mapping.load(new File("src/test/resources/type/default-mapping.xml"));
		typeChecker.addTypeMapping(mapping);

		// valid
		IRuleFile rules = createTestFile("'a' is one of 'a','b','c'");
		List<NRLError> errors = typeChecker.check(rules);
		assertEquals(0, errors.size());

		IIsInPredicate pred = (IIsInPredicate) ((IConstraintRuleDeclaration) rules
				.getRuleById("test-rule")).getConstraint();
		assertEquals(NRLDataType.BOOLEAN, pred.getNRLDataType());

		// valid
		errors = typeChecker.check(createTestFile("tradeDate is one of tradeDate, '2005-12-01'"));
		assertEquals(0, errors.size());

		// valid
		errors = typeChecker.check(createTestFile("counter is one of 52.0"));
		assertEquals(0, errors.size());

		// valid - enumeration
		errors = typeChecker.check(createTestFile("untyped is one of UntypedEnum.LITERAL"));
		assertEquals(0, errors.size());

		// invalid - can't compare collections
		errors = typeChecker.check(createTestFile("counter is one of legs"));
		assertEquals(2, errors.size());

		// invalid - can't compare date to number
		errors = typeChecker.check(createTestFile("tradeDate is one of tradeDate, 52"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.IS_IN_EXPRESSION_TYPE_MISMATCH, errors.get(0).getStatusCode());
	}

	@Test
	public void testCheck_IsNotInPredicate() throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		XmlTypeMapping mapping = new XmlTypeMapping();
		mapping.load(new File("src/test/resources/type/default-mapping.xml"));
		typeChecker.addTypeMapping(mapping);

		// valid
		IRuleFile rules = createTestFile("'a' is not one of 'a','b','c'");
		List<NRLError> errors = typeChecker.check(rules);
		assertEquals(0, errors.size());

		IIsNotInPredicate pred = (IIsNotInPredicate) ((IConstraintRuleDeclaration) rules
				.getRuleById("test-rule")).getConstraint();
		assertEquals(NRLDataType.BOOLEAN, pred.getNRLDataType());

		// valid
		errors = typeChecker
				.check(createTestFile("tradeDate is not one of tradeDate, '2005-12-01'"));
		assertEquals(0, errors.size());

		// valid
		errors = typeChecker.check(createTestFile("counter is not one of 52.0"));
		assertEquals(0, errors.size());

		// invalid - can't compare date to number
		errors = typeChecker.check(createTestFile("tradeDate is not one of tradeDate, 52"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.IS_IN_EXPRESSION_TYPE_MISMATCH, errors.get(0).getStatusCode());
	}

	@Test
	public void testCheck_IsSubtypePredicate() throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		XmlTypeMapping mapping = new XmlTypeMapping();
		mapping.load(new File("src/test/resources/type/default-mapping.xml"));
		typeChecker.addTypeMapping(mapping);

		// valid
		IRuleFile rules = createTestFile("the IRSwap is a kind of IRSwap");
		List<NRLError> errors = typeChecker.check(rules);
		assertEquals(0, errors.size());
		IIsSubtypePredicate pred = (IIsSubtypePredicate) ((IConstraintRuleDeclaration) rules
				.getRuleById("test-rule")).getConstraint();
		assertEquals(NRLDataType.BOOLEAN, pred.getNRLDataType());

		// invalid (collection)
		rules = createTestFile("legs is a kind of IRLeg");
		errors = typeChecker.check(rules);
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.CAST_COLLECTION, ((NRLError) errors.iterator().next())
				.getStatusCode());

		// in-valid (not subtype)
		rules = createTestFile("tradeDate is a kind of IRSwap");
		errors = typeChecker.check(rules);
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.CAST_REQUIRES_SUBTYPE, ((NRLError) errors.iterator().next())
				.getStatusCode());
	}

	@Test
	public void testCheck_ModelElement() throws Exception {
		ConstraintTypeChecker typeChecker = new ConstraintTypeChecker();

		XmlTypeMapping mapping = new XmlTypeMapping();
		mapping.load(new File("src/test/resources/type/default-mapping.xml"));
		typeChecker.addTypeMapping(mapping);

		// Collection
		IRuleFile rules = createTestFile("legs is equal to 2");
		List<NRLError> errors = typeChecker.check(rules);
		assertEquals(1, errors.size());

		IModelReference ref = (IModelReference) ((IBinaryPredicate) ((IConstraintRuleDeclaration) rules
				.getRuleById("test-rule")).getConstraint()).getLeft();
		assertTrue(ref.getNRLDataType().isCollection());
		assertEquals(Type.Element, ref.getNRLDataType().getType());

		// Collection
		rules = createTestFile("description is equal to 2");
		errors = typeChecker.check(rules);
		assertEquals(1, errors.size());
		ref = (IModelReference) ((IBinaryPredicate) ((IConstraintRuleDeclaration) rules
				.getRuleById("test-rule")).getConstraint()).getLeft();
		assertTrue(ref.getNRLDataType().isCollection());
		assertEquals(Type.String, ref.getNRLDataType().getType());

		// Date
		rules = createTestFile("tradeDate is equal to 2");
		errors = typeChecker.check(rules);
		assertEquals(1, errors.size());

		ref = (IModelReference) ((IBinaryPredicate) ((IConstraintRuleDeclaration) rules
				.getRuleById("test-rule")).getConstraint()).getLeft();
		assertEquals(NRLDataType.DATE, ref.getNRLDataType());

		// String
		rules = createTestFile("tradeheader.masterAgreement is equal to 'abc'");
		errors = typeChecker.check(rules);
		assertEquals(0, errors.size());

		ref = (IModelReference) ((IBinaryPredicate) ((IConstraintRuleDeclaration) rules
				.getRuleById("test-rule")).getConstraint()).getLeft();
		assertEquals(NRLDataType.STRING, ref.getNRLDataType());

		// Integer (inherited!)
		rules = createTestFile("age = 15");
		errors = typeChecker.check(rules);
		assertEquals(0, errors.size());

		ref = (IModelReference) ((IBinaryPredicate) ((IConstraintRuleDeclaration) rules
				.getRuleById("test-rule")).getConstraint()).getLeft();
		assertEquals(NRLDataType.INTEGER, ref.getNRLDataType());

		// Collection with simple type
		rules = createTestFile("number of description elements > 1");
		errors = typeChecker.check(rules);
		IBinaryPredicate pred = (IBinaryPredicate) ((IConstraintRuleDeclaration) rules
				.getRuleById("test-rule")).getConstraint();
		ref = (IModelReference) ((IFunctionalExpression) pred.getLeft()).getParameters().get(0);
		assertTrue(ref.getNRLDataType().isCollection());
		assertEquals(Type.String, ref.getNRLDataType().getType());

		// Invalid - implicit collection
		rules = createTestFile("legs.fixFloat = FixFloatEnum.FIXED");
		errors = typeChecker.check(rules);
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.IMPLICIT_ITERATION, ((NRLError) errors.iterator().next())
				.getStatusCode());

		// Invalid - untyped enumeration
		rules = createTestFile("untyped = 15");
		errors = typeChecker.check(rules);
		assertEquals(1, errors.size());
		ref = (IModelReference) ((IBinaryPredicate) ((IConstraintRuleDeclaration) rules
				.getRuleById("test-rule")).getConstraint()).getLeft();
		assertEquals(Type.Element, ref.getNRLDataType().getType());
	}

	@Test
	public void testCheck_MultipleExistsStatement() throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		XmlTypeMapping mapping = new XmlTypeMapping();
		mapping.load(new File("src/test/resources/type/default-mapping.xml"));
		typeChecker.addTypeMapping(mapping);

		// valid
		List<NRLError> errors = typeChecker
				.check(createTestFile("the following are present: legs, tradeDate"));
		assertEquals(0, errors.size());

		// invalid: model element
		errors = typeChecker.check(createTestFile("the following are present: legs, Trade"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.ILLEGAL_ELEMENT_REFERENCE, ((NRLError) errors.iterator().next())
				.getStatusCode());

		// invalid: enumeration/static attribute
		errors = typeChecker
				.check(createTestFile("the following are present: legs, FixFloatEnum.FIXED"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.ILLEGAL_STATIC_REFERENCE, ((NRLError) errors.iterator().next())
				.getStatusCode());
	}

	@Test
	public void testCheck_MultipleNotExistsStatement() throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		XmlTypeMapping mapping = new XmlTypeMapping();
		mapping.load(new File("src/test/resources/type/default-mapping.xml"));
		typeChecker.addTypeMapping(mapping);

		// valid
		List<NRLError> errors = typeChecker
				.check(createTestFile("the following are not present: legs, tradeDate"));
		assertEquals(0, errors.size());

		// invalid: model element
		errors = typeChecker.check(createTestFile("the following are not present: legs, Trade"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.ILLEGAL_ELEMENT_REFERENCE, ((NRLError) errors.iterator().next())
				.getStatusCode());

		// invalid: enumeration/static attribute
		errors = typeChecker
				.check(createTestFile("the following are not present: legs, FixFloatEnum.FIXED"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.ILLEGAL_STATIC_REFERENCE, ((NRLError) errors.iterator().next())
				.getStatusCode());
	}

	@Test
	public void testCheck_NotExistsStatement() throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		XmlTypeMapping mapping = new XmlTypeMapping();
		mapping.load(new File("src/test/resources/type/default-mapping.xml"));
		typeChecker.addTypeMapping(mapping);

		// valid
		List<NRLError> errors = typeChecker.check(createTestFile("legs are not present"));
		assertEquals(0, errors.size());

		// Valid
		errors = typeChecker.check(createTestFile("tradeDate is not present"));
		assertEquals(0, errors.size());
	}

	@Test
	public void testCheck_OperatorInvocation() throws Exception {
		XmlOperatorPersistence loader = new XmlOperatorPersistence();
		LoadedVersion version = loader.load(new File("src/test/resources/type/operators.xml"));
		assertEquals(LoadedVersion.Version14, version);

		IOperators ops = loader.getOperators();
		assertNotNull(ops);

		ModelCollection models = new ModelCollection();
		models.addModelPackage(getBasicModel());
		assertEquals(0, ops.resolveModelReferences(models).size());

		ITypeChecker typeChecker = new ConstraintTypeChecker();
		XmlTypeMapping mapping = new XmlTypeMapping();
		mapping.load(new File("src/test/resources/type/default-mapping.xml"));
		typeChecker.addTypeMapping(mapping);

		assertEquals(0, typeChecker.check(ops).size());

		// Element reference
		IRuleFile rules = createTestFile("[a trade]", ops);
		List<NRLError> errors = typeChecker.check(rules);
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.RULE_NOT_BOOLEAN, errors.get(0).getStatusCode());

		IOperatorInvocation opInv = (IOperatorInvocation) ((IConstraintRuleDeclaration) rules
				.getRuleById("test-rule")).getConstraint();
		assertEquals(NRLDataType.ELEMENT, opInv.getNRLDataType());

		// Primitive type reference (string)
		rules = createTestFile("[a string]", ops);
		errors = typeChecker.check(rules);
		assertEquals(1, errors.size());

		opInv = (IOperatorInvocation) ((IConstraintRuleDeclaration) rules.getRuleById("test-rule"))
				.getConstraint();
		assertEquals(NRLDataType.STRING, opInv.getNRLDataType());

		// Valid - pass a complex element
		rules = createTestFile("[pass a trade] IRSwap", ops);
		errors = typeChecker.check(rules);
		assertEquals(0, errors.size());

		opInv = (IOperatorInvocation) ((IConstraintRuleDeclaration) rules.getRuleById("test-rule"))
				.getConstraint();
		assertEquals(NRLDataType.BOOLEAN, opInv.getNRLDataType());

		// Valid - pass an integer (auto conversion)
		rules = createTestFile("[pass an integer] 2", ops);
		errors = typeChecker.check(rules);
		assertEquals(0, errors.size());

		rules = createTestFile("[pass an integer] 2.0", ops);
		errors = typeChecker.check(rules);
		assertEquals(0, errors.size());

		rules = createTestFile("[pass an integer] '2.0'", ops);
		errors = typeChecker.check(rules);
		assertEquals(0, errors.size());

		// Invalid - cannot convert to integer
		rules = createTestFile("[pass an integer] true", ops);
		errors = typeChecker.check(rules);
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.OPERATOR_TYPE_MISMATCH, errors.get(0).getStatusCode());

		rules = createTestFile("[pass an integer] tradeDate", ops);
		errors = typeChecker.check(rules);
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.OPERATOR_TYPE_MISMATCH, errors.get(0).getStatusCode());

		// Invalid - pass a complex element that is incompatible
		rules = createTestFile("[pass a trade] tradeheader", ops);
		errors = typeChecker.check(rules);
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.OPERATOR_TYPE_MISMATCH, errors.get(0).getStatusCode());

		// Invalid - void type in an expression
		rules = createTestFile("[void] = true", ops);
		errors = typeChecker.check(rules);
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.VOID_OPERATOR_IN_EXPRESSION, errors.get(0).getStatusCode());

		// List - valid type
		rules = createTestFile("[pass a list of legs] legs", ops);
		errors = typeChecker.check(rules);
		assertEquals(0, errors.size());

		// List - scalar parameter is invalid
		rules = createTestFile("[pass a list of legs] first of the legs", ops);
		errors = typeChecker.check(rules);
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.OPERATOR_COLLECTION_PARAMETER, errors.get(0).getStatusCode());

		// List - invalid type
		rules = createTestFile("[pass a list of legs] description", ops);
		errors = typeChecker.check(rules);
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.OPERATOR_TYPE_MISMATCH, errors.get(0).getStatusCode());
	}

	@Test
	public void testCheck_FragmentApplication() throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();
		TypeMapping mapping = new TypeMapping();
		mapping.addMapping("*", "decimal", NRLDataType.DECIMAL);
		mapping.addMapping("*", "String", NRLDataType.STRING);
		mapping.addMapping("*", "Date", NRLDataType.DATE);
		typeChecker.addTypeMapping(mapping);

		// valid
		List<NRLError> errors = typeChecker
				.check(createTestFile("{is valid} IRSwap Context: IRSwap (\"swap\") Validation Fragment \"is valid\" 'a' = 'a'"));
		assertEquals(0, errors.size());

		// valid
		errors = typeChecker
				.check(createTestFile("{is valid} tradeheader Context: TradeHeader (\"header\") Validation Fragment \"is valid\" 'a' = 'a'"));
		assertEquals(0, errors.size());

		// Argument number wrong
		IRuleFile ruleFile = createTestFile("{is valid} tradeheader \n\n"
				+ "Context: TradeHeader (\"header\"), IRSwap (\"swap\") Validation Fragment \"is valid\" 'a' = 'a'");
		errors = typeChecker.check(ruleFile);
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.FRAGMENT_PARAMETER_MISMATCH, ((NRLError) errors.get(0))
				.getStatusCode());

		// number conversion to string is ok
		ruleFile = createTestFile("tradeheader {is equivalent to} 2.2\n\n"
				+ "Context: TradeHeader (\"header\"), String (\"figure\") Validation Fragment \"is equivalent to\" figure = 'a'");
		errors = typeChecker.check(ruleFile);
		assertEquals(0, errors.size());

		// incompatible elements
		ruleFile = createTestFile("{is valid} tradeheader\n\n"
				+ "Context: Trade (\"swap\") Validation Fragment \"is valid\" 'a' = 'a'");
		errors = typeChecker.check(ruleFile);
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.FRAGMENT_PARAMETER_MISMATCH, ((NRLError) errors.get(0))
				.getStatusCode());

		// compare number to dates (note type inferred from property)
		ruleFile = createTestFile("{the date of} IRSwap = 5\n\n"
				+ "Context: Trade (\"swap\") Validation Fragment \"the date of\" swap.tradeDate");
		errors = typeChecker.check(ruleFile);
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.BINARY_PREDICATE_ARGUMENT_INCOMPATIBLE, ((NRLError) errors.get(0))
				.getStatusCode());

	}

	@Test
	public void testCheck_FragmentDeclaration() throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		// string fragment
		IRuleFile file = createTestFile("'a' = 'a' Context: Trade (\"trade\") Validation Fragment \"invalid\" 'a'");
		List<NRLError> errors = typeChecker.check(file);
		assertEquals(0, errors.size());
		assertEquals(NRLDataType.STRING, ((IValidationFragmentDeclaration) file.getDeclarations()
				.get(1)).getNRLDataType());

		file = createTestFile("'a' = 'a' Context: Trade (\"trade\") Validation Fragment \"invalid\" if 'a' = 'a' then 3 else 2");
		errors = typeChecker.check(file);
		assertEquals(0, errors.size());
		assertEquals(NRLDataType.INTEGER, ((IValidationFragmentDeclaration) file.getDeclarations()
				.get(1)).getNRLDataType());

		// complex result
		file = createTestFile("'a' = 'a' Context: Trade (\"trade\") Validation Fragment \"invalid\" trade");
		errors = typeChecker.check(file);
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.FRAGMENT_RESULT_COMPLEX, ((NRLError) errors.get(0))
				.getStatusCode());

		// different results in two execution paths
		file = createTestFile("'a' = 'a' Context: Trade (\"trade\") Validation Fragment \"invalid\" if 'a' = 'a' then 'a' else 2");
		errors = typeChecker.check(file);
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.IF_STATEMENT_MULTIPLE_TYPES, ((NRLError) errors.get(0))
				.getStatusCode());
	}

	@Test
	public void testCheck_RuleDeclaration() throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		// non-boolean rule
		List<NRLError> errors = typeChecker.check(createTestFile("'a'"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.RULE_NOT_BOOLEAN, ((NRLError) errors.iterator().next())
				.getStatusCode());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testCheck_RuleSetDeclaration() throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		// non-boolean rule set precondition
		String rawFile = "Model \"basicmodel.uml2\" Rule Set \"r1\" "
				+ "Applies to a IRSwap where 'a' Context: IRSwap Validation Rule \"test-rule\""
				+ "'a' = 'b'";

		NRLParser parser = new NRLParser();
		IRuleFile ruleFile = parser.parse(new StringReader(rawFile));
		assertEquals(0, parser.getErrors().size());

		ModelCollection models = new ModelCollection();
		models.addModelPackage(getBasicModel());

		parser.resolveModelReferences(ruleFile, models);
		assertEquals(0, parser.getErrors().size());

		List<NRLError> errors = typeChecker.check(ruleFile);
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.RULESET_PRECONDITION_NOT_BOOLEAN, ((NRLError) errors.iterator()
				.next()).getStatusCode());
	}

	@Test
	public void testCheck_SelectionExpression() throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		XmlTypeMapping mapping = new XmlTypeMapping();
		mapping.load(new File("src/test/resources/type/default-mapping.xml"));
		typeChecker.addTypeMapping(mapping);

		// Invalid - no collection
		List<NRLError> errors = typeChecker
				.check(createTestFile("\"header\" is the tradeheader where masterAgreement = 'x', 1 = 1"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.COLLECTION_EXPECTED, errors.get(0).getStatusCode());

		// Invalid - selection is not boolean
		errors = typeChecker
				.check(createTestFile("\"leg1\" is the legs where 2, number of legs = 1"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.SELECTION_CONSTRAINT_NOT_BOOLEAN, errors.get(0).getStatusCode());

		// Invalid - result of selection is a collection
		errors = typeChecker
				.check(createTestFile("\"leg1\" is the legs where fixFloat = FixFloatEnum.FIXED, leg1 = 1"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.BINARY_PREDICATE_ARGUMENT_COLLECTION, errors.get(0)
				.getStatusCode());

		// Invalid - result of single selection is not a collection
		errors = typeChecker
				.check(createTestFile("\"leg1\" is the first of the legs where fixFloat = FixFloatEnum.FIXED, "
						+ "the number of leg1 = 1"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.COLLECTION_EXPECTED, errors.get(0).getStatusCode());

		// Valid
		IRuleFile rules = createTestFile("\"leg1\" is the legs where fixFloat = FixFloatEnum.FIXED, "
				+ "the number of leg1 = 1");
		errors = typeChecker.check(rules);
		assertEquals(0, errors.size());

		IVariableDeclaration decl = ((IConstraintRuleDeclaration) rules.getRuleById("test-rule"))
				.getVariableDeclarations().get(0);
		assertEquals(NRLDataType.Type.Element, decl.getNRLDataType().getType());
		assertEquals(true, decl.getNRLDataType().isCollection());

		// Valid
		rules = createTestFile("\"leg1\" is the first of the legs where fixFloat = FixFloatEnum.FIXED, "
				+ "leg1.fixFloat = FixFloatEnum.FIXED");
		errors = typeChecker.check(rules);
		assertEquals(0, errors.size());

		decl = ((IConstraintRuleDeclaration) rules.getRuleById("test-rule"))
				.getVariableDeclarations().get(0);
		assertEquals(NRLDataType.Type.Element, decl.getNRLDataType().getType());
		assertEquals(false, decl.getNRLDataType().isCollection());
	}

	@Test
	public void testCheck_VariableDeclaration() throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		XmlTypeMapping mapping = new XmlTypeMapping();
		mapping.load(new File("src/test/resources/type/default-mapping.xml"));
		typeChecker.addTypeMapping(mapping);

		// Valid (variable assigned to model element)
		IRuleFile file = createTestFile("\"x\" represents tradeDate, x = '2005-12-12'");
		List<NRLError> errors = typeChecker.check(file);
		assertEquals(0, errors.size());

		IVariableDeclaration decl = (IVariableDeclaration) ((IConstraintRuleDeclaration) file
				.getRuleById("test-rule")).getVariableDeclarations().get(0);
		assertEquals(Type.Date, decl.getNRLDataType().getType());
		assertEquals(false, decl.getNRLDataType().isCollection());
		assertEquals(Type.Date, decl.getVariableReference().getNRLDataType().getType());
		assertEquals(Type.Boolean, ((IConstraintRuleDeclaration) file.getRuleById("test-rule"))
				.getConstraint().getNRLDataType().getType());

		// Valid (type propagation between two variables)
		file = createTestFile("\"x\" represents tradeDate, \"y\" represents x, y = '2005-12-12'");
		errors = typeChecker.check(file);
		assertEquals(0, errors.size());

		decl = (IVariableDeclaration) ((IConstraintRuleDeclaration) file.getRuleById("test-rule"))
				.getVariableDeclarations().get(1);
		assertEquals(Type.Date, decl.getNRLDataType().getType());
		assertEquals(false, decl.getNRLDataType().isCollection());
		assertEquals(Type.Date, decl.getVariableReference().getNRLDataType().getType());
		assertEquals(Type.Boolean, ((IConstraintRuleDeclaration) file.getRuleById("test-rule"))
				.getConstraint().getNRLDataType().getType());

		// Valid (variable assigned to collection)
		file = createTestFile("\"x\" represents the legs, the number of x = 2");
		errors = typeChecker.check(file);
		assertEquals(0, errors.size());

		decl = (IVariableDeclaration) ((IConstraintRuleDeclaration) file.getRuleById("test-rule"))
				.getVariableDeclarations().get(0);
		assertEquals(Type.Element, decl.getNRLDataType().getType());
		assertEquals(true, decl.getNRLDataType().isCollection());
		assertEquals(Type.Element, decl.getVariableReference().getNRLDataType().getType());
		assertEquals(true, decl.getVariableReference().getNRLDataType().isCollection());
		assertEquals(Type.Boolean, ((IConstraintRuleDeclaration) file.getRuleById("test-rule"))
				.getConstraint().getNRLDataType().getType());

		// Valid (type propagation with two variables, and a collection)
		file = createTestFile("\"x\" represents the legs, \"y\" represents x, the number of y = 2");
		errors = typeChecker.check(file);
		assertEquals(0, errors.size());

		decl = (IVariableDeclaration) ((IConstraintRuleDeclaration) file.getRuleById("test-rule"))
				.getVariableDeclarations().get(1);
		assertEquals(Type.Element, decl.getNRLDataType().getType());
		assertEquals(true, decl.getNRLDataType().isCollection());
		assertEquals(Type.Element, decl.getExpression().getNRLDataType().getType());
		assertEquals(true, decl.getExpression().getNRLDataType().isCollection());
		assertEquals(Type.Element, decl.getVariableReference().getNRLDataType().getType());
		assertEquals(true, decl.getVariableReference().getNRLDataType().isCollection());
		assertEquals(Type.Boolean, ((IConstraintRuleDeclaration) file.getRuleById("test-rule"))
				.getConstraint().getNRLDataType().getType());

		// Valid (variable assigned to expression)
		file = createTestFile("\"x\" represents 2+3, x = 5");
		errors = typeChecker.check(file);
		assertEquals(0, errors.size());

		// Invalid (variable assigned to expression)
		file = createTestFile("\"x\" represents 2+3, x = tradeDate");
		errors = typeChecker.check(file);
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.BINARY_PREDICATE_ARGUMENT_INCOMPATIBLE, errors.get(0)
				.getStatusCode());

		// Invalid (variable assigned to model element)
		file = createTestFile("\"x\" represents tradeDate, x = 5");
		errors = typeChecker.check(file);
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.BINARY_PREDICATE_ARGUMENT_INCOMPATIBLE, errors.get(0)
				.getStatusCode());

		// Invalid (variable assigned to expression)
		file = createTestFile("\"x\" represents 3+5, x is a kind of TradeHeader");
		errors = typeChecker.check(file);
		assertEquals(1, errors.size());
	}
}
