package net.sourceforge.nrl.parser.ast.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import net.sourceforge.nrl.parser.IStatusCode;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.SemanticError;
import net.sourceforge.nrl.parser.ast.IRuleFile;
import net.sourceforge.nrl.parser.ast.constraints.IConstraintRuleDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.IOperatorInvocation;
import net.sourceforge.nrl.parser.model.ModelCollection;
import net.sourceforge.nrl.parser.operators.IOperators;
import net.sourceforge.nrl.parser.operators.Operators;
import net.sourceforge.nrl.parser.operators.XmlOperatorPersistence;
import net.sourceforge.nrl.parser.operators.XmlOperatorPersistence.LoadedVersion;

import org.junit.Test;

public class AntlrOperatorResolverVisitorTest extends NRLParserTestSupport {

	/**
	 * Test valid resolution.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testResolve_ValidNoParams() throws Exception {
		IRuleFile ruleFile = createTestFile("[Operator two]");

		ModelCollection models = new ModelCollection();
		models.addModelPackage(getBasicModel());

		IOperators operators = new Operators();
		assertEquals(0, operators.resolveModelReferences(models).size());

		XmlOperatorPersistence loader = new XmlOperatorPersistence();
		LoadedVersion version = loader.load(new File("src/test/resources/operators/operators.xml"));
		assertEquals(LoadedVersion.Version14, version);

		operators = loader.getOperators();
		assertNotNull(operators);

		AntlrOperatorResolverVisitor visitor = new AntlrOperatorResolverVisitor(
				new IOperators[] { operators });
		ruleFile.accept(visitor);
		assertEquals(0, visitor.getErrors().size());

		IOperatorInvocation invocation = (IOperatorInvocation) ((IConstraintRuleDeclaration) ruleFile
				.getRuleById("test-rule")).getConstraint();
		assertNotNull(invocation.getOperator());
		assertTrue(invocation.getOperator() == operators.getOperator("Operator two"));
	}

	/**
	 * Test valid resolution with no parameters.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testResolve_ValidWithParams() throws Exception {
		IRuleFile ruleFile = createTestFile("'ab' [Operator one] 'cd'");

		XmlOperatorPersistence loader = new XmlOperatorPersistence();
		LoadedVersion version = loader.load(new File("src/test/resources/operators/operators.xml"));
		assertEquals(LoadedVersion.Version14, version);

		IOperators operators = loader.getOperators();
		assertNotNull(operators);

		AntlrOperatorResolverVisitor visitor = new AntlrOperatorResolverVisitor(
				new IOperators[] { operators });
		ruleFile.accept(visitor);
		assertEquals(0, visitor.getErrors().size());

		IOperatorInvocation invocation = (IOperatorInvocation) ((IConstraintRuleDeclaration) ruleFile
				.getRuleById("test-rule")).getConstraint();
		assertNotNull(invocation.getOperator());
		assertTrue(invocation.getOperator() == operators.getOperator("Operator one"));
	}

	/**
	 * Test error detection of invalid names
	 * 
	 * @throws Exception
	 */
	@Test
	public void testResolve_InvalidName() throws Exception {
		IRuleFile ruleFile = createTestFile("'ab' [Operator one] 'cd'");

		// Empty collection
		Operators operators = new Operators();

		AntlrOperatorResolverVisitor visitor = new AntlrOperatorResolverVisitor(
				new IOperators[] { operators });
		ruleFile.accept(visitor);
		assertEquals(1, visitor.getErrors().size());

		assertEquals(IStatusCode.OPERATOR_UNKNOWN, ((SemanticError) visitor.getErrors().get(0))
				.getStatusCode());
	}

	/**
	 * Parameter mismatch. Operator requires two parameters, we pass one.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testResolve_Parameters() throws Exception {
		IRuleFile ruleFile = createTestFile("[Operator one] 'cd'");

		XmlOperatorPersistence loader = new XmlOperatorPersistence();
		LoadedVersion version = loader.load(new File("src/test/resources/operators/operators.xml"));
		assertEquals(LoadedVersion.Version14, version);

		IOperators operators = loader.getOperators();
		assertNotNull(operators);

		AntlrOperatorResolverVisitor visitor = new AntlrOperatorResolverVisitor(
				new IOperators[] { operators });
		ruleFile.accept(visitor);
		assertEquals(1, visitor.getErrors().size());

		assertEquals(IStatusCode.OPERATOR_PARAMETER_MISMATCH, ((SemanticError) visitor.getErrors()
				.get(0)).getStatusCode());
	}

}
