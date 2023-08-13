package net.sourceforge.nrl.parser.ast.constraints.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.constraints.IArithmeticExpression;
import net.sourceforge.nrl.parser.ast.constraints.IValidationFragmentDeclaration;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

public class ValidationFragmentDeclarationImplTest extends NRLParserTestSupport {

	/**
	 * Test parsing with a multiple parameters
	 */
	@Test
	public void testParseWithSingleParameters() throws Exception {
		NRLActionParser parser = getParserFor("Context: foo (\"f\") Validation Fragment \"m1\" 5 + 4");
		IValidationFragmentDeclaration decl = (IValidationFragmentDeclaration) parser.declaration()
				.getTree();

		assertEquals("m1", decl.getId());
		assertEquals(1, decl.getContextNames().size());
		assertEquals("foo", ((ModelReferenceImpl) ((ValidationFragmentDeclarationImpl) decl)
				.getModelReference("f")).getOriginalString());

		assertTrue(decl.getConstraint() instanceof IArithmeticExpression);
	}

	/**
	 * Test parsing with a multiple parameters
	 */
	@Test
	public void testParseWithMultiParameter() throws Exception {
		NRLActionParser parser = getParserFor("Context: foo (\"f\"), bar (\"g\"), baz (\"h\") Validation Fragment \"m1\" 5 + 4");
		IValidationFragmentDeclaration decl = (IValidationFragmentDeclaration) parser.declaration()
				.getTree();

		assertEquals("m1", decl.getId());
		assertEquals(3, decl.getContextNames().size());
		assertEquals("foo", ((ModelReferenceImpl) ((ValidationFragmentDeclarationImpl) decl)
				.getModelReference("f")).getOriginalString());
		assertEquals("bar", ((ModelReferenceImpl) ((ValidationFragmentDeclarationImpl) decl)
				.getModelReference("g")).getOriginalString());
		assertEquals("baz", ((ModelReferenceImpl) ((ValidationFragmentDeclarationImpl) decl)
				.getModelReference("h")).getOriginalString());

		assertTrue(decl.getConstraint() instanceof IArithmeticExpression);
	}

}
