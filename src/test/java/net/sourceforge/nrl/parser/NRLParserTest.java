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
package net.sourceforge.nrl.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URI;

import net.sourceforge.nrl.parser.ast.IModelFileReference;
import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.IOperatorFileReference;
import net.sourceforge.nrl.parser.ast.IRuleFile;
import net.sourceforge.nrl.parser.ast.constraints.IBinaryPredicate;
import net.sourceforge.nrl.parser.ast.constraints.IConstraintRuleDeclaration;
import net.sourceforge.nrl.parser.ast.impl.SyntaxErrorException;
import net.sourceforge.nrl.parser.model.IPackage;
import net.sourceforge.nrl.parser.model.ModelCollection;
import net.sourceforge.nrl.parser.resolver.IResolverFactory;
import net.sourceforge.nrl.parser.resolver.ResolverException;
import net.sourceforge.nrl.parser.resolver.StandaloneResolverFactory;

import org.junit.Before;
import org.junit.Test;

/**
 * Test the main parser class.
 * 
 * @author Christian Nentwich
 */
public class NRLParserTest extends NRLParserTestSupport {

	public static File TEST_DIR = new File(".");

	private IResolverFactory resolverFactory;

	@Before
	public void setUp() throws Exception {
		resolverFactory = new StandaloneResolverFactory();
	}

	@Test
	public void testUnsupportedURISchemeCausesError() throws Exception {
		INRLParser parser = new NRLParser();
		try {
			parser.parse(new URI("http://relativeURI.nrl"), resolverFactory);
			fail();
		} catch (ResolverException e) {
		}
	}

	@Test
	public void testRelativeURIsNotPermittedForNRLFile() throws Exception {
		INRLParser parser = new NRLParser();
		try {
			parser.parse(new URI("file:relativeURI.nrl"), resolverFactory);
			fail();
		} catch (ResolverException e) {
		}
	}

	/*
	 * Test unified parsing and model/operator resolution via the URI-centric interface.
	 */
	@Test
	public void testURIParse() throws Exception {
		INRLParser parser = new NRLParser();

		URI nrlFileURI = new File(TEST_DIR.getAbsolutePath(),
				"src/test/resources/parsing/model-and-operators-correct.nrl").toURI();

		IRuleFile ruleFile = parser.parse(nrlFileURI, resolverFactory);
		IModelFileReference[] models = ruleFile.getModelFileReferences();
		IOperatorFileReference[] operators = ruleFile.getOperatorFileReferences();
		assertEquals(0, parser.getErrors().size());
		assertNotNull(ruleFile);
		assertNotNull(models);
		assertEquals(1, models.length);
		assertTrue(allModelsResolved(models));
		assertNotNull(operators);
		assertEquals(1, operators.length);
		assertTrue(allOperatorsResolved(operators));
		assertEquals("Trade", ruleFile.getRuleById("expr-1").getContext().getName());
	}

	/*
	 * Test unified parsing and model/operator resolution via the URI-centric interface. Check
	 * models can be resolved from the classpath
	 */
	@Test
	public void testURIParseWithClasspathModel() throws Exception {
		INRLParser parser = new NRLParser();

		URI nrlFileURI = new File(TEST_DIR.getAbsolutePath(),
				"src/test/resources/parsing/classpath-model-correct.nrl").toURI();

		IRuleFile ruleFile = parser.parse(nrlFileURI, resolverFactory);
		IModelFileReference[] models = ruleFile.getModelFileReferences();
		IOperatorFileReference[] operators = ruleFile.getOperatorFileReferences();
		assertEquals(0, parser.getErrors().size());
		assertNotNull(ruleFile);
		assertNotNull(models);
		assertEquals(1, models.length);
		assertTrue(allModelsResolved(models));
		assertNotNull(operators);
		assertEquals(1, operators.length);
		assertTrue(allOperatorsResolved(operators));
		assertEquals("Trade", ruleFile.getRuleById("expr-1").getContext().getName());
	}
	
	@Test
	public void testURIParseWithClasspathUMLModelWithSpacesInName() throws Exception {
		INRLParser parser = new NRLParser();

		URI nrlFileURI = new File(TEST_DIR.getAbsolutePath(),
				"src/test/resources/parsing/classpath-uml-model-with-spaces-in-name-correct.nrl").toURI();

		IRuleFile ruleFile = parser.parse(nrlFileURI, resolverFactory);
		assertEquals(0, parser.getErrors().size());
		IModelFileReference[] models = ruleFile.getModelFileReferences();
		assertNotNull(ruleFile);
		assertNotNull(models);
		assertEquals(1, models.length);
		assertTrue(allModelsResolved(models));
		assertEquals("Trade", ruleFile.getRuleById("expr-1").getContext().getName());
	}
	
	@Test
	public void testURIParseWithClasspathXSDModelWithSpacesInName() throws Exception {
		INRLParser parser = new NRLParser();

		URI nrlFileURI = new File(TEST_DIR.getAbsolutePath(),
				"src/test/resources/parsing/classpath-xsd-model-with-spaces-in-name-correct.nrl").toURI();

		IRuleFile ruleFile = parser.parse(nrlFileURI, resolverFactory);
		assertEquals(0, parser.getErrors().size());
		IModelFileReference[] models = ruleFile.getModelFileReferences();
		IOperatorFileReference[] operators = ruleFile.getOperatorFileReferences();
		assertNotNull(ruleFile);
		assertNotNull(models);
		assertEquals(1, models.length);
		assertTrue(allModelsResolved(models));
		assertNotNull(operators);
		assertEquals(0, operators.length);
		assertTrue(allOperatorsResolved(operators));
	}

	/*
	 * Test unified parsing and model/operator resolution via the URI-centric interface. Check
	 * operators can be resolved from the classpath
	 */
	@Test
	public void testURIParseWithClasspathOperators() throws Exception {
		INRLParser parser = new NRLParser();

		URI nrlFileURI = new File(TEST_DIR.getAbsolutePath(),
				"src/test/resources/parsing/classpath-operator-correct.nrl").toURI();

		IRuleFile ruleFile = parser.parse(nrlFileURI, resolverFactory);
		IModelFileReference[] models = ruleFile.getModelFileReferences();
		IOperatorFileReference[] operators = ruleFile.getOperatorFileReferences();
		assertEquals(0, parser.getErrors().size());
		assertNotNull(ruleFile);
		assertNotNull(models);
		assertEquals(1, models.length);
		assertTrue(allModelsResolved(models));
		assertNotNull(operators);
		assertEquals(1, operators.length);
		assertTrue(allOperatorsResolved(operators));
		assertEquals("Trade", ruleFile.getRuleById("expr-1").getContext().getName());
	}

	@Test
	public void testLoadOperatorsWithClasspathURIWithSpaces() throws Exception {
		INRLParser parser = new NRLParser();

		URI nrlFileURI = new File(TEST_DIR.getAbsolutePath(),
				"src/test/resources/parsing/classpath-operator-with-spaces-correct.nrl").toURI();

		IRuleFile ruleFile = parser.parse(nrlFileURI, resolverFactory);
		IModelFileReference[] models = ruleFile.getModelFileReferences();
		IOperatorFileReference[] operators = ruleFile.getOperatorFileReferences();
		assertEquals(0, parser.getErrors().size());
		assertNotNull(ruleFile);
		assertNotNull(models);
		assertEquals(1, models.length);
		assertTrue(allModelsResolved(models));
		assertNotNull(operators);
		assertEquals(1, operators.length);
		assertTrue(allOperatorsResolved(operators));
		assertEquals("Trade", ruleFile.getRuleById("expr-1").getContext().getName());
	}
	
	/*
	 * Test the old parsing and model resolution methods.
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testParse() throws Exception {
		INRLParser parser = new NRLParser();

		IRuleFile ruleFile = parser.parse(new FileInputStream(
				"src/test/resources/parsing/model-correct.nrl"));
		assertEquals(0, parser.getErrors().size());
		assertNotNull(ruleFile);

		IPackage model = getBasicModel();
		assertNotNull(model);

		ModelCollection models = new ModelCollection();
		models.addModelPackage(model);

		parser.resolveModelReferences(ruleFile, models);
		assertEquals(0, parser.getErrors().size());

		// Check for correct resolution
		assertEquals("Trade", ruleFile.getRuleById("expr-1").getContext().getName());
	}

	/*
	 * Check if illegal chars are reported using syntax errors
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testParse_illegalChar() throws Exception {
		INRLParser parser = new NRLParser();
		try {
			parser.parse(new FileInputStream("src/test/resources/parsing/illegal-char.nrl"));
			fail("should throw exception");
		} catch (SyntaxErrorException e) {
			assertEquals(9, e.getSyntaxError().getLine());
			assertEquals(12, e.getSyntaxError().getColumn());
		}
	}

	/*
	 * Test parsing UTF8 files
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testParse_UTF8() throws Exception {
		INRLParser parser = new NRLParser();
		IRuleFile ruleFile = null;
		try {
			ruleFile = parser.parse(new InputStreamReader(new FileInputStream(
					"src/test/resources/parsing/utf8.nrl"), "utf-8"));
		} catch (SyntaxErrorException e) {
			System.err.println(e.getSyntaxError().getLine() + " " + e);
			fail(e.getMessage());
		}
		assertEquals(0, parser.getErrors().size());
		assertNotNull(ruleFile);
		assertNotNull(ruleFile.getRuleById("çãüäö"));
		assertEquals("çãüäö",
				((IModelReference) ((IBinaryPredicate) ((IConstraintRuleDeclaration) ruleFile
						.getRuleById("çãüäö")).getConstraint()).getLeft()).getOriginalString());

		assertNotNull(ruleFile.getRuleById("日本語"));
		assertEquals("日本語",
				((IModelReference) ((IBinaryPredicate) ((IConstraintRuleDeclaration) ruleFile
						.getRuleById("日本語")).getConstraint()).getLeft()).getOriginalString());
	}

	/*
	 * Test parsing UTF16 files
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testParse_UTF16() throws Exception {
		INRLParser parser = new NRLParser();
		IRuleFile ruleFile = parser.parse(new InputStreamReader(new FileInputStream(
				"src/test/resources/parsing/utf16.nrl"), "utf-16"));
		assertEquals(0, parser.getErrors().size());
		assertNotNull(ruleFile);
		assertNotNull(ruleFile.getRuleById("çãüäö"));
		assertEquals("çãüäö",
				((IModelReference) ((IBinaryPredicate) ((IConstraintRuleDeclaration) ruleFile
						.getRuleById("çãüäö")).getConstraint()).getLeft()).getOriginalString());

		assertNotNull(ruleFile.getRuleById("日本語"));
		assertEquals("日本語",
				((IModelReference) ((IBinaryPredicate) ((IConstraintRuleDeclaration) ruleFile
						.getRuleById("日本語")).getConstraint()).getLeft()).getOriginalString());
	}

	private boolean allModelsResolved(IModelFileReference[] models) {
		for (IModelFileReference modelFileReference : models) {
			if (!modelFileReference.isModelResolved()) {
				return false;
			}
		}
		return true;
	}

	private boolean allOperatorsResolved(IOperatorFileReference[] operators) {
		for (IOperatorFileReference operatorFileReference : operators) {
			if (!operatorFileReference.isOperatorsResolved()) {
				return false;
			}
		}
		return true;
	}
}
