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

import java.io.File;
import java.util.List;

import net.sourceforge.nrl.parser.IStatusCode;
import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.IRuleFile;
import net.sourceforge.nrl.parser.ast.NRLDataType;
import net.sourceforge.nrl.parser.ast.NRLDataType.Type;
import net.sourceforge.nrl.parser.ast.action.IActionRuleDeclaration;
import net.sourceforge.nrl.parser.ast.action.ICompoundAction;
import net.sourceforge.nrl.parser.ast.action.ISetAction;
import net.sourceforge.nrl.parser.ast.action.IVariableDeclarationAction;
import net.sourceforge.nrl.parser.ast.constraints.IBinaryPredicate;
import net.sourceforge.nrl.parser.ast.constraints.IConstraintRuleDeclaration;
import net.sourceforge.nrl.parser.model.ModelCollection;
import net.sourceforge.nrl.parser.operators.IOperators;
import net.sourceforge.nrl.parser.operators.XmlOperatorPersistence;
import net.sourceforge.nrl.parser.operators.XmlOperatorPersistence.LoadedVersion;

import org.junit.Test;

/**
 * Tests the {@link com.modeltwozero.nrl.parser.type.ActionTypeChecker} class construct by
 * construct.
 * 
 * @author Christian Nentwich
 */
public class ActionTypeCheckerTest extends NRLParserTestSupport {

	@Test
	public void testCheck_AddAction() throws Exception {
		ITypeChecker typeChecker = new ActionTypeChecker();

		XmlTypeMapping mapping = new XmlTypeMapping();
		mapping.load(new File("src/test/resources/type/default-mapping.xml"));
		typeChecker.addTypeMapping(mapping);

		// Add leg to legs -> legal
		List<NRLError> errors = typeChecker
				.check(createActionTestFile("Create a new IRLeg (\"l\"), add l to legs"));
		assertEquals(0, errors.size());

		// Add legs to legs -> illegal (can't add collection to collection)
		errors = typeChecker.check(createActionTestFile("Add legs to legs"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.ADD_TYPES_INCOMPATIBLE, errors.get(0).getStatusCode());

		// Add to non-collection attribute -> illegal
		errors = typeChecker
				.check(createActionTestFile("Create a new TradeHeader (\"t\"), add t to tradeheader"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.ADD_NEEDS_COLLECTION, (errors.iterator().next()).getStatusCode());

		// String to string collection -> legal
		errors = typeChecker.check(createActionTestFile("Add id to description"));
		assertEquals(0, errors.size());

		// Date to string collection -> legal
		errors = typeChecker.check(createActionTestFile("Add tradeDate to description"));
		assertEquals(0, errors.size());

		// Number to string collection -> legal
		errors = typeChecker.check(createActionTestFile("Add age to description"));
		assertEquals(0, errors.size());

		// Complex element to string collection -> illegal
		errors = typeChecker.check(createActionTestFile("Add tradeheader to description"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.ADD_TYPES_INCOMPATIBLE, errors.get(0).getStatusCode());

		// Incompatible types -> illegal
		errors = typeChecker
				.check(createActionTestFile("Create a new TradeHeader (\"t\"), add t to legs"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.ADD_TYPES_INCOMPATIBLE, errors.get(0).getStatusCode());
	}

	@Test
	public void testCheck_CompoundAction() {
		// Nothing to test for now
	}

	@Test
	public void testCheck_ConditionalAction() throws Exception {
		ITypeChecker typeChecker = new ActionTypeChecker();

		TypeMapping mapping = new TypeMapping();
		mapping.addMapping("*", "Date", NRLDataType.DATE);
		typeChecker.addTypeMapping(mapping);

		// Boolean condition -> legal
		List<NRLError> errors = typeChecker
				.check(createActionTestFile("If tradeDate = '2005-12-12' then create a new IRLeg (\"l\");"));
		assertEquals(0, errors.size());

		// Non-boolean condition -> illegal
		errors = typeChecker
				.check(createActionTestFile("If tradeDate then create a new IRLeg (\"l\");"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.IF_ARGUMENTS_NOT_BOOLEAN, (errors.iterator().next())
				.getStatusCode());
	}

	@Test
	public void testCheck_CreateAction() {
		// Nothing to test for now
	}

	@Test
	public void testCheck_RemoveAction() throws Exception {
		ITypeChecker typeChecker = new ActionTypeChecker();

		TypeMapping mapping = new TypeMapping();
		mapping.addMapping("*", "Date", NRLDataType.DATE);
		mapping.addMapping("*", "String", NRLDataType.STRING);
		typeChecker.addTypeMapping(mapping);

		// Delete an attribute -> legal
		List<NRLError> errors = typeChecker.check(createActionTestFile("Remove the tradeheader"));
		assertEquals(0, errors.size());

		// Delete a trade with where clause -> legal
		errors = typeChecker
				.check(createActionTestFile("Remove any Trade (\"x\") where x.tradeDate > '2005-12-15'"));
		assertEquals(0, errors.size());

		// Non-boolean where clause -> illegal
		errors = typeChecker
				.check(createActionTestFile("Remove any Trade (\"x\") where x.tradeDate"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.WHERE_NOT_BOOLEAN, (errors.iterator().next()).getStatusCode());

		// Where clause with attribute (rather than element) -> legal
		errors = typeChecker
				.check(createActionTestFile("Remove any legs (\"x\") where x.fixFloat = FixFloatEnum.FLOAT"));
		assertEquals(0, errors.size());

		// Where clause with singular attribute (rather than element) -> illegal
		errors = typeChecker
				.check(createActionTestFile("Remove any tradeheader (\"x\") where x.masterAgreement = 'abc'"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.REMOVE_NEEDS_COLLECTION, (errors.iterator().next())
				.getStatusCode());
	}

	@Test
	public void testCheck_ForEachAction() throws Exception {
		ITypeChecker typeChecker = new ActionTypeChecker();

		TypeMapping mapping = new TypeMapping();
		mapping.addMapping("*", "Date", NRLDataType.DATE);
		mapping.addMapping("*", "String", NRLDataType.STRING);
		typeChecker.addTypeMapping(mapping);

		// Iteration over collection -> legal
		List<NRLError> errors = typeChecker
				.check(createActionTestFile("For each of the legs, set fixFloat to FixFloatEnum.FLOAT;"));
		assertEquals(0, errors.size());

		// Iteration over single attribute -> illegal
		errors = typeChecker
				.check(createActionTestFile("For each of the tradeheader, set masterAgreement to 'foo';"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.ITERATION_NEEDS_COLLECTION, (errors.iterator().next())
				.getStatusCode());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testCheck_OperatorAction() throws Exception {
		XmlOperatorPersistence loader = new XmlOperatorPersistence();
		LoadedVersion version = loader
				.load(new File("src/test/resources/type/operators-action.xml"));
		assertEquals(LoadedVersion.Version14, version);

		IOperators ops = loader.getOperators();
		assertNotNull(ops);

		ModelCollection models = new ModelCollection();
		models.addModelPackage(getBasicModel2());
		assertEquals(0, ops.resolveModelReferences(models).size());

		ITypeChecker typeChecker = new ActionTypeChecker();
		XmlTypeMapping mapping = new XmlTypeMapping();
		mapping.load(new File("src/test/resources/type/default-mapping.xml"));
		typeChecker.addTypeMapping(mapping);

		assertEquals(0, typeChecker.check(ops).size());

		// Operator without params -> legal
		List<NRLError> errors = typeChecker.check(createActionTestFile("[donothing]", ops));
		assertEquals(0, errors.size());

		// Operator with complex argument (passing subclass) -> legal
		errors = typeChecker.check(createActionTestFile("[pass a trade] IRSwap", ops));
		assertEquals(0, errors.size());

		// Passing incompatible type -> invalid
		errors = typeChecker.check(createActionTestFile("[pass a trade] tradeheader", ops));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.OPERATOR_TYPE_MISMATCH, errors.get(0).getStatusCode());

		// Simple type -> legal
		errors = typeChecker.check(createActionTestFile("[pass an integer] 5", ops));
		assertEquals(0, errors.size());

		// Simple type -> legal (conversion)
		errors = typeChecker.check(createActionTestFile("[pass an integer] 5.0", ops));
		assertEquals(0, errors.size());

		// Simple type -> illegal
		errors = typeChecker.check(createActionTestFile("[pass an integer] tradeDate", ops));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.OPERATOR_TYPE_MISMATCH, errors.get(0).getStatusCode());

		// Collection -> illegal
		errors = typeChecker.check(createActionTestFile("[pass a leg] legs", ops));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.OPERATOR_COLLECTION_PARAMETER, errors.get(0).getStatusCode());
	}

	@Test
	public void testCheck_RemoveFromCollectionAction() throws Exception {
		ITypeChecker typeChecker = new ActionTypeChecker();

		// Remove leg from legs -> legal
		List<NRLError> errors = typeChecker
				.check(createActionTestFile("Create a new IRLeg (\"l\"), remove l from legs"));
		assertEquals(0, errors.size());

		// Remove collection from collection -> illegal
		errors = typeChecker.check(createActionTestFile("Remove legs from legs"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.REMOVE_CANNOT_REMOVE_COLLECTION, errors.get(0).getStatusCode());

		// Remove non-collection attribute -> illegal
		errors = typeChecker
				.check(createActionTestFile("Create a new TradeHeader (\"t\"), remove t from tradeheader"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.REMOVE_NEEDS_COLLECTION, (errors.iterator().next())
				.getStatusCode());

		// Incompatible types -> illegal
		errors = typeChecker
				.check(createActionTestFile("Create a new TradeHeader (\"t\"), remove t from legs"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.REMOVE_TYPES_INCOMPATIBLE, (errors.iterator().next())
				.getStatusCode());
	}

	@Test
	public void testCheck_SetAction() throws Exception {
		ITypeChecker typeChecker = new ActionTypeChecker();

		TypeMapping mapping = new TypeMapping();
		mapping.addMapping("*", "String", NRLDataType.STRING);
		mapping.addMapping("*", "Unlimited Natural", NRLDataType.DECIMAL);
		mapping.addMapping("*", "UnlimitedNatural", NRLDataType.DECIMAL);
		mapping.addMapping("*", "Integer", NRLDataType.INTEGER);
		mapping.addMapping("*", "Date", NRLDataType.DATE);
		typeChecker.addTypeMapping(mapping);

		// Set string value -> legal
		List<NRLError> errors = typeChecker
				.check(createActionTestFile("Set tradeheader.masterAgreement to 'foo'"));
		assertEquals(0, errors.size());

		errors = typeChecker
				.check(createActionTestFile("Set tradeheader.masterAgreement to tradeheader.masterAgreement"));
		assertEquals(0, errors.size());

		// Set complex object -> legal
		errors = typeChecker
				.check(createActionTestFile("Create a new TradeHeader (\"header\"), set tradeheader to header"));
		assertEquals(0, errors.size());

		// Assign compatible value to variable, legal
		errors = typeChecker
				.check(createActionTestFile("\"x\" represents the tradeheader, set x to tradeheader"));
		assertEquals(0, errors.size());

		// Assign incompatible type to variable, illegal
		errors = typeChecker
				.check(createActionTestFile("\"x\" represents the tradeheader, set x to '52'"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.ASSIGNMENT_TYPE_INCOMPATIBLE, (errors.iterator().next())
				.getStatusCode());

		// Set collection to simple type -> illegal
		errors = typeChecker.check(createActionTestFile("Set legs to id"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.ASSIGNMENT_TYPE_INCOMPATIBLE, (errors.iterator().next())
				.getStatusCode());

		// Incompatible type -> illegal
		IRuleFile ruleFile = createActionTestFile("Set tradeDate to tradeheader");
		errors = typeChecker.check(ruleFile);
		assertEquals(1, errors.size());
		assertEquals(Type.Element,
				((ISetAction) ((ICompoundAction) ((IActionRuleDeclaration) ruleFile
						.getRuleById("test-rule")).getAction()).getSimpleActions().get(0))
						.getExpression().getNRLDataType().getType());
		assertEquals(IStatusCode.ASSIGNMENT_TYPE_INCOMPATIBLE, (errors.iterator().next())
				.getStatusCode());

		// Incompatible type -> illegal
		errors = typeChecker
				.check(createActionTestFile("Create a new IRLeg (\"l\"), set tradeheader to l"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.ASSIGNMENT_TYPE_INCOMPATIBLE, (errors.iterator().next())
				.getStatusCode());

		// Incompatible type (expression)
		errors = typeChecker
				.check(createActionTestFile("\"x\" represents 3 + 5, set tradeheader to x"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.ASSIGNMENT_TYPE_INCOMPATIBLE, (errors.iterator().next())
				.getStatusCode());

		// Subtype -> legal
		errors = typeChecker
				.check(createActionTestFile("Create a new ExtendedHeader (\"h\"), set tradeheader to h"));
		assertEquals(0, errors.size());

		// Set a model element -> illegal
		errors = typeChecker.check(createActionTestFile("Set TradeHeader to tradeheader"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.ILLEGAL_ASSIGNMENT_TARGET, (errors.iterator().next())
				.getStatusCode());

		// ENUMERATIONS
		// String enum literal to string enum literal -> valid
		errors = typeChecker
				.check(createActionTestFile("for each of the legs, set fixFloat to FixFloatEnum.FIXED;"));
		assertEquals(0, errors.size());

		// String to string enum literal -> valid
		errors = typeChecker
				.check(createActionTestFile("for each of the legs, set fixFloat to 'FIXED';"));
		assertEquals(0, errors.size());

		// number literal to string enum literal -> invalid
		// errors = typeChecker.check(createActionTestFile("for each of the legs, set
		// fixFloat to IntValue.One;"));
		// assertEquals(1, errors.size());

		// number literal to number enum literal -> valid
		errors = typeChecker.check(createActionTestFile("set counter to IntValue.One"));
		assertEquals(0, errors.size());

		// number to number enum literal -> valid
		errors = typeChecker.check(createActionTestFile("set counter to 1"));
		assertEquals(0, errors.size());

		// untyped literal to untyped literal -> valid
		errors = typeChecker.check(createActionTestFile("set untyped to UntypedEnum.LITERAL"));
		assertEquals(0, errors.size());

		// untyped literal to string -> invalid
		errors = typeChecker.check(createActionTestFile("set untyped to 'foo'"));
		assertEquals(1, errors.size());

		// derived int type to int -> valid
		errors = typeChecker.check(createActionTestFile("set age to counter"));
		assertEquals(0, errors.size());

		// Set an attribute to a model element - illegal
		errors = typeChecker.check(createActionTestFile("set age to UntypedEnum"));
		assertEquals(1, errors.size());

		// Collection of primitive types to scalar primitive type - illegal
		errors = typeChecker.check(createActionTestFile("set description to 'x'"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.ASSIGNMENT_TYPE_INCOMPATIBLE, errors.get(0).getStatusCode());
	}

	/*
	 * Supplementary tests for simple types with attributes
	 */
	@Test
	public void testCheck_ModelElement_SimpleComplexTypes() throws Exception {
		ITypeChecker typeChecker = new ConstraintTypeChecker();

		TypeMapping mapping = new TypeMapping();
		mapping.addMapping("*", "decimal", NRLDataType.DECIMAL);
		mapping.addMapping("*", "string", NRLDataType.STRING);
		typeChecker.addTypeMapping(mapping);

		// Check that simple-type inheritance will work
		IRuleFile ruleFile = createTypeTestFile("simple + simple = simple");
		List<NRLError> errors = typeChecker.check(ruleFile);
		assertEquals(0, errors.size());

		IModelReference ref = (IModelReference) ((IBinaryPredicate) ((IConstraintRuleDeclaration) ruleFile
				.getRuleById("test-rule")).getConstraint()).getRight();
		assertEquals(NRLDataType.DECIMAL, ref.getNRLDataType());

		// Check complex data type inheritance ('complex' is a decimal)
		ruleFile = createTypeTestFile("complex + complex = complex ");
		errors = typeChecker.check(ruleFile);
		assertEquals(0, errors.size());

		ref = (IModelReference) ((IBinaryPredicate) ((IConstraintRuleDeclaration) ruleFile
				.getRuleById("test-rule")).getConstraint()).getRight();
		assertEquals(NRLDataType.DECIMAL, ref.getNRLDataType());

		// Check complex data type inheritance ('complexAttr' is a decimal)
		ruleFile = createTypeTestFile("complexAttr + complexAttr = complexAttr");
		errors = typeChecker.check(ruleFile);
		assertEquals(0, errors.size());

		ref = (IModelReference) ((IBinaryPredicate) ((IConstraintRuleDeclaration) ruleFile
				.getRuleById("test-rule")).getConstraint()).getRight();
		assertEquals(NRLDataType.DECIMAL, ref.getNRLDataType());

		// Check complex type attributes
		ruleFile = createTypeTestFile("complexAttr.id = 'test'");
		errors = typeChecker.check(ruleFile);
		for (int i = 0; i < errors.size(); i++)
			System.err.println(errors.get(i));

		assertEquals(0, errors.size());

		ref = (IModelReference) ((IBinaryPredicate) ((IConstraintRuleDeclaration) ruleFile
				.getRuleById("test-rule")).getConstraint()).getLeft();
		assertEquals(NRLDataType.STRING, ref.getNRLDataType());
	}

	/*
	 * Macro applications
	 */
	@Test
	public void testCheck_ActionFragmentApplication() throws Exception {
		ITypeChecker typeChecker = new ActionTypeChecker();

		TypeMapping mapping = new TypeMapping();
		mapping.addMapping("*", "decimal", NRLDataType.DECIMAL);
		mapping.addMapping("*", "String", NRLDataType.STRING);
		mapping.addMapping("*", "Date", NRLDataType.DATE);
		typeChecker.addTypeMapping(mapping);

		// valid
		IRuleFile ruleFile = createActionTestFile("prune the tradeheader\n\n"
				+ "Context: TradeHeader (\"header\") Action Fragment \"prune\" set header.masterAgreement to 'abc'");
		List<NRLError> errors = typeChecker.check(ruleFile);
		assertEquals(0, errors.size());

		// Argument number wrong
		ruleFile = createActionTestFile("prune the tradeheader\n\n"
				+ "Context: TradeHeader (\"header\"), IRSwap (\"swap\") Action Fragment \"prune\" set header.masterAgreement to 'abc'");
		errors = typeChecker.check(ruleFile);
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.ACTION_FRAGMENT_PARAMETER_MISMATCH, (errors.get(0))
				.getStatusCode());

		// number conversion to string is ok
		ruleFile = createActionTestFile("prune the tradeheader using 2.2\n\n"
				+ "Context: TradeHeader (\"header\"), String (\"figure\") Action Fragment \"prune\" set header.masterAgreement to 'abc'");
		errors = typeChecker.check(ruleFile);
		assertEquals(0, errors.size());

		// incompatible elements
		ruleFile = createActionTestFile("prune the tradeheader\n\n"
				+ "Context: Trade (\"swap\") Action Fragment \"prune\" set swap.tradeDate to 'x'");
		errors = typeChecker.check(ruleFile);
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.ACTION_FRAGMENT_PARAMETER_MISMATCH, (errors.get(0))
				.getStatusCode());

		// compatible (subclass)
		ruleFile = createActionTestFile("prune the tradeheader.trade\n\n"
				+ "Context: Trade (\"swap\") Action Fragment \"prune\" set swap.tradeDate to 'x'");
		errors = typeChecker.check(ruleFile);
		assertEquals(0, errors.size());
	}

	/*
	 * Additional checks for rule sets
	 */
	@Test
	public void testCheck_RuleSets() throws Exception {
		ITypeChecker typeChecker = new ActionTypeChecker();

		TypeMapping mapping = new TypeMapping();
		mapping.addMapping("*", "decimal", NRLDataType.DECIMAL);
		mapping.addMapping("*", "String", NRLDataType.STRING);
		mapping.addMapping("*", "Date", NRLDataType.DATE);
		typeChecker.addTypeMapping(mapping);

		// Mixing of action and constraint rules in default rule set - illegal
		List<NRLError> errors = typeChecker
				.check(createActionTestFile("Set tradeheader.masterAgreement to 'foo' Context: Trade Validation Rule \"r2\" 1 = 1"));
		assertEquals(2, errors.size());
		assertEquals(IStatusCode.RULESET_MIXES_RULETYPES, (errors.get(0)).getStatusCode());
		assertEquals(IStatusCode.RULESET_MIXES_RULETYPES, (errors.get(1)).getStatusCode());

		// Mixing of action and constraint rules in declared rule sets - illegal
		errors = typeChecker
				.check(createActionTestFile("Set tradeheader.masterAgreement to 'foo' Rule Set \"x\" "
						+ "Context: Trade Validation Rule \"r2\" 1 = 1 Context: Trade Action Rule \"r3\" "
						+ "Remove tradeheader"));
		assertEquals(1, errors.size());
		assertEquals(IStatusCode.RULESET_MIXES_RULETYPES, (errors.get(0)).getStatusCode());
	}

	/*
	 * Variable declarations
	 */
	@Test
	public void testCheck_VariableDeclarationAction() throws Exception {
		ITypeChecker typeChecker = new ActionTypeChecker();

		// Literal -> legal
		IRuleFile ruleFile = createActionTestFile("\"x\" represents the 'foo'");
		List<NRLError> errors = typeChecker.check(ruleFile);
		assertEquals(0, errors.size());
		IVariableDeclarationAction decl = (IVariableDeclarationAction) ((ICompoundAction) ((IActionRuleDeclaration) ruleFile
				.getRuleById("test-rule")).getAction()).getSimpleActions().get(0);
		assertEquals(NRLDataType.Type.String, decl.getExpression().getNRLDataType().getType());
		assertEquals(false, decl.getExpression().getNRLDataType().isCollection());

		// Assign collection -> legal
		ruleFile = createActionTestFile("\"x\" represents the legs");
		errors = typeChecker.check(ruleFile);
		assertEquals(0, errors.size());
		decl = (IVariableDeclarationAction) ((ICompoundAction) ((IActionRuleDeclaration) ruleFile
				.getRuleById("test-rule")).getAction()).getSimpleActions().get(0);
		assertEquals(NRLDataType.Type.Element, decl.getExpression().getNRLDataType().getType());
		assertEquals(true, decl.getExpression().getNRLDataType().isCollection());

		// Propagation of type, no collection -> legal
		ruleFile = createActionTestFile("\"x\" represents the 'foo', \"y\" represents x");
		errors = typeChecker.check(ruleFile);
		assertEquals(0, errors.size());
		decl = (IVariableDeclarationAction) ((ICompoundAction) ((IActionRuleDeclaration) ruleFile
				.getRuleById("test-rule")).getAction()).getSimpleActions().get(1);
		assertEquals(NRLDataType.Type.String, decl.getExpression().getNRLDataType().getType());
		assertEquals(false, decl.getExpression().getNRLDataType().isCollection());

		// Propagation of type, collection -> legal
		ruleFile = createActionTestFile("\"x\" represents the legs, \"y\" represents x");
		errors = typeChecker.check(ruleFile);
		assertEquals(0, errors.size());
		decl = (IVariableDeclarationAction) ((ICompoundAction) ((IActionRuleDeclaration) ruleFile
				.getRuleById("test-rule")).getAction()).getSimpleActions().get(1);
		assertEquals(NRLDataType.Type.Element, decl.getExpression().getNRLDataType().getType());
		assertEquals(true, decl.getExpression().getNRLDataType().isCollection());
		assertEquals(NRLDataType.Type.Element, decl.getVariableReference().getNRLDataType()
				.getType());
		assertEquals(true, decl.getVariableReference().getNRLDataType().isCollection());
	}

}
