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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.nrl.parser.ast.IRuleFile;
import net.sourceforge.nrl.parser.ast.impl.Antlr3NRLTreeAdaptor;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;
import net.sourceforge.nrl.parser.ast.impl.NRLJFlexer;
import net.sourceforge.nrl.parser.model.IPackage;
import net.sourceforge.nrl.parser.model.ModelCollection;
import net.sourceforge.nrl.parser.model.uml2.UML2ModelLoader;
import net.sourceforge.nrl.parser.operators.IOperators;
import net.sourceforge.nrl.parser.operators.XmlOperatorPersistence;
import net.sourceforge.nrl.parser.operators.XmlOperatorPersistence.LoadedVersion;

import org.antlr.runtime.TokenRewriteStream;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.resource.impl.URIHandlerImpl;
import org.eclipse.emf.mapping.ecore2xml.Ecore2XMLPackage;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UML212UMLResource;
import org.eclipse.uml2.uml.resource.UML22UMLResource;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.junit.After;

/**
 * An extension of a JUnit test case that adds various convenience methods.
 * 
 * @author Christian Nentwich
 */
@SuppressWarnings("deprecation")
public abstract class NRLParserTestSupport {

	private static IPackage basicModel = null, basicModel2 = null;

	private static IPackage simpleModel = null;

	private static IPackage typesModel = null;

	private ResourceSet resourceSet;

	private Resource resource;

	/**
	 * Return the "basic" model in <code>src/test/resources/basicmodel.uml2</code> that is used by
	 * many test cases
	 * 
	 * @return the basic model
	 * @throws Exception
	 */
	public IPackage getBasicModel() throws Exception {
		if (basicModel != null)
			return basicModel;

		// Load the model
		UML2ModelLoader loader = new UML2ModelLoader();

		File file = new File("src/test/resources/uml/basicmodel.uml");
		basicModel = loader.load(getResourceForFile(file));
		assertNotNull(basicModel);

		return basicModel;
	}

	/**
	 * Return the model in <code>src/test/resources/basicmodel2.uml2</code> that is used by many
	 * test cases
	 * 
	 * @return the basic model
	 * @throws Exception
	 */
	public IPackage getBasicModel2() throws Exception {
		if (basicModel2 != null)
			return basicModel2;

		// Load the model
		UML2ModelLoader loader = new UML2ModelLoader();

		File file = new File("src/test/resources/uml/basicmodel2.uml");
		basicModel2 = loader.load(getResourceForFile(file));
		assertNotNull(basicModel2);

		return basicModel2;
	}

	/**
	 * Return the model in <code>src/test/resources/simplemodel.uml2</code> that is used by test
	 * cases
	 * 
	 * @return the FpML model
	 * @throws Exception
	 */
	public IPackage getSimpleModel() throws Exception {
		if (simpleModel != null)
			return simpleModel;

		// Load the model
		UML2ModelLoader loader = new UML2ModelLoader();

		File file = new File("src/test/resources/uml/simplemodel.uml");
		simpleModel = loader.load(getResourceForFile(file));
		assertNotNull(simpleModel);

		return simpleModel;
	}

	/**
	 * Get a parser to parse an NRL rule from a string
	 * 
	 * @param content the NRL content
	 * @return the parser
	 */
	public NRLActionParser getParserFor(String content) {
		NRLJFlexer lexer = new NRLJFlexer(new StringReader(content));
		TokenRewriteStream rewrite = new TokenRewriteStream(lexer);
		NRLActionParser parser = new NRLActionParser(rewrite);
		parser.setTreeAdaptor(new Antlr3NRLTreeAdaptor());
		return parser;
	}

	/*
	 * Helper method to return an ecore resource for a file (for XMI parsing)
	 */
	@SuppressWarnings("unchecked")
	public Resource getResourceForFile(File file) {
		resourceSet = new ResourceSetImpl();

		resourceSet.getURIConverter().getURIHandlers().add(0, new URIHandlerImpl() {
			@Override
			public boolean canHandle(URI uri) {
				return "classpath".equals(uri.scheme());
			}

			@Override
			public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException {
				return getClass().getResourceAsStream(uri.path());
			}
		});

		Map packageRegistry = resourceSet.getPackageRegistry();
		packageRegistry.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		packageRegistry.put(Ecore2XMLPackage.eNS_URI, Ecore2XMLPackage.eINSTANCE);
		packageRegistry.put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
		packageRegistry.put("http://www.eclipse.org/uml2/2.0.0/UML", UMLPackage.eINSTANCE);
		packageRegistry.put(UML212UMLResource.UML_METAMODEL_NS_URI, UMLPackage.eINSTANCE);
		packageRegistry.put(UML22UMLResource.UML2_METAMODEL_NS_URI, UMLPackage.eINSTANCE);

		Map extensionToFactoryMap = resourceSet.getResourceFactoryRegistry()
				.getExtensionToFactoryMap();
		extensionToFactoryMap.put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);

		Map<URI, URI> uriMap = resourceSet.getURIConverter().getURIMap();
		URI uri = URI.createURI("classpath:/");
		uriMap.put(URI.createURI(UMLResource.LIBRARIES_PATHMAP), uri.appendSegment("libraries")
				.appendSegment(""));
		uriMap.put(URI.createURI(UMLResource.METAMODELS_PATHMAP), uri.appendSegment("metamodels")
				.appendSegment(""));
		uriMap.put(URI.createURI(UMLResource.PROFILES_PATHMAP), uri.appendSegment("profiles")
				.appendSegment(""));

		uri = URI.createFileURI("etc/xsd.profile");
		uriMap.put(URI.createURI("pathmap://XMLmodeling_LIBRARIES/"), uri
				.appendSegment("libraries").appendSegment(""));
		uriMap.put(URI.createURI("pathmap://XMLmodeling_PROFILES/"), uri.appendSegment("profiles")
				.appendSegment(""));

		resource = resourceSet.createResource(URI.createFileURI(file.getAbsolutePath()));
		return resource;
	}

	@After
	public void teardown() {
		if (resourceSet != null) {

			for (Iterator<Resource> i = resourceSet.getResources().iterator(); i.hasNext();) {
				Resource current = i.next();
				current.unload();
				i.remove();
			}
		}
	}

	/*
	 * Helper method to parse a rule file
	 */
	public IRuleFile getRuleFile(String fileName) throws Exception {
		NRLJFlexer lexer = new NRLJFlexer(new FileReader(fileName));
		TokenRewriteStream rewrite = new TokenRewriteStream(lexer);
		NRLActionParser parser = new NRLActionParser(rewrite);
		parser.setTreeAdaptor(new Antlr3NRLTreeAdaptor());

		IRuleFile result = (IRuleFile) parser.fileBody().getTree();

		if (parser.getSyntaxErrors().size() > 0) {
			for (Iterator<NRLError> iter = parser.getSyntaxErrors().iterator(); iter.hasNext();)
				System.out.println(iter.next());
		}
		assertEquals(0, parser.getSyntaxErrors().size());

		return result;
	}

	/**
	 * Constructs and parses a rule file with a rule 'test-rule' that contains the given expression
	 * under the context of 'IRSwap'
	 * 
	 * @param expression the expression
	 * @return a parsed rule file with no errors and model elements assigned
	 */
	public IRuleFile createTestFile(String expression) throws Exception {
		ModelCollection coll = new ModelCollection();
		coll.addModelPackage(getBasicModel());

		XmlOperatorPersistence loader = new XmlOperatorPersistence();
		LoadedVersion version = loader.load(new File("src/test/resources/type/operators.xml"));
		assertEquals(LoadedVersion.Version14, version);

		IOperators operators = loader.getOperators();
		assertNotNull(operators);

		assertEquals(0, operators.resolveModelReferences(coll).size());

		return createTestFile(expression, operators);
	}

	public IRuleFile createTestFile(String expression, IOperators ops) throws Exception {
		String rawFile = "Model \"basicmodel.uml2\" Context: IRSwap Validation Rule \"test-rule\" "
				+ expression;

		NRLParser parser = new NRLParser();
		IRuleFile ruleFile = parser.parse(new StringReader(rawFile));
		for (int i = 0; i < parser.getErrors().size(); i++)
			System.err.println(parser.getErrors().get(i));
		assertEquals(0, parser.getErrors().size());

		ModelCollection coll = new ModelCollection();
		coll.addModelPackage(getBasicModel());

		parser.resolveOperatorReferences(ruleFile, new IOperators[] { ops });
		parser.resolveModelReferences(ruleFile, coll);

		for (int i = 0; i < parser.getErrors().size(); i++)
			System.err.println(parser.getErrors().get(i));
		assertEquals(0, parser.getErrors().size());

		return ruleFile;
	}

	/**
	 * Constructs and parses a rule file with an action rule 'test-rule' that contains the given
	 * expression under the context of 'IRSwap'
	 * 
	 * @param expression the expression
	 * @return a parsed rule file with no errors and model elements assigned
	 */
	public IRuleFile createActionTestFile(String expression) throws Exception {
		String rawFile = "Model \"basicmodel.uml2\" Context: IRSwap Action Rule \"test-rule\" "
				+ expression;

		NRLParser parser = new NRLParser();
		IRuleFile ruleFile = parser.parse(new StringReader(rawFile));
		if (parser.getErrors().size() > 0)
			System.err.println(parser.getErrors().get(0));
		assertEquals(0, parser.getErrors().size());

		ModelCollection models = new ModelCollection();
		models.addModelPackage(getBasicModel2());

		parser.resolveModelReferences(ruleFile, models);
		if (parser.getErrors().size() > 0)
			System.err.println(parser.getErrors().get(0));
		assertEquals(0, parser.getErrors().size());

		return ruleFile;
	}

	public IRuleFile createActionTestFile(String expression, IOperators ops) throws Exception {
		String rawFile = "Model \"basicmodel.uml2\" Context: IRSwap Action Rule \"test-rule\" "
				+ expression;

		NRLParser parser = new NRLParser();
		IRuleFile ruleFile = parser.parse(new StringReader(rawFile));
		assertEquals(0, parser.getErrors().size());

		ModelCollection models = new ModelCollection();
		models.addModelPackage(getBasicModel2());

		parser.resolveOperatorReferences(ruleFile, new IOperators[] { ops });
		parser.resolveModelReferences(ruleFile, models);
		if (parser.getErrors().size() > 0)
			System.err.println(parser.getErrors().get(0));
		assertEquals(0, parser.getErrors().size());

		return ruleFile;
	}

	/**
	 * Constructs and parses a rule file with a rule 'test-rule' that contains the given expression
	 * under the context of 'Structure'. Uses TypesTest model.
	 * 
	 * @param expression the expression
	 * @return a parsed rule file with no errors and model elements assigned
	 */
	public IRuleFile createTypeTestFile(String expression) throws Exception {
		String rawFile = "Model \"TypesTest.uml2\" Context: Structure Validation Rule \"test-rule\" "
				+ expression;

		NRLParser parser = new NRLParser();
		IRuleFile ruleFile = parser.parse(new StringReader(rawFile));
		for (int i = 0; i < parser.getErrors().size(); i++)
			System.err.println(parser.getErrors().get(i));
		assertEquals(0, parser.getErrors().size());

		ModelCollection coll = new ModelCollection();
		coll.addModelPackage(getTypesModel());

		parser.resolveModelReferences(ruleFile, coll);
		for (int i = 0; i < parser.getErrors().size(); i++)
			System.err.println(parser.getErrors().get(i));
		assertEquals(0, parser.getErrors().size());

		return ruleFile;
	}

	/**
	 * Return the model in <code>src/test/resources/TypesTest.uml</code> that is used by test cases
	 * 
	 * @return the FpML model
	 * @throws Exception
	 */
	public IPackage getTypesModel() throws Exception {
		if (typesModel != null)
			return typesModel;

		// Load the model
		UML2ModelLoader loader = new UML2ModelLoader();

		File file = new File("src/test/resources/uml/TypesTest.uml");
		typesModel = loader.load(getResourceForFile(file));
		assertNotNull(typesModel);

		return typesModel;
	}

}
