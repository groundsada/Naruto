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
package net.sourceforge.nrl.parser.operators;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.sourceforge.nrl.parser.ModelLoadingError;
import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.NRLDataType;
import net.sourceforge.nrl.parser.model.loader.IModelLoader;
import net.sourceforge.nrl.parser.operators.IOperators.LoadedVersion;
import net.sourceforge.nrl.parser.resolver.IURIResolver;
import net.sourceforge.nrl.parser.resolver.StandaloneResolverFactory;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * Test loading and saving operators
 * 
 */
public class XmlOperatorLoaderTest extends NRLParserTestSupport {

	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

	static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

	private IModelLoader modelLoader;
	private IURIResolver uriResolver;
	private List<NRLError> nrlErrors;

	@Before
	public void setUp() throws Exception {
		XMLUnit.setIgnoreWhitespace(true);
		StandaloneResolverFactory standaloneResolverFactory = new StandaloneResolverFactory(
				getClass().getClassLoader());
		modelLoader = standaloneResolverFactory.createModelLoader();
		uriResolver = standaloneResolverFactory.createURIResolver();

		nrlErrors = new ArrayList<NRLError>();

	}

	@Test
	public void version14ModelCanBeLoaded() throws Exception {
		XmlOperatorLoader loader = new XmlOperatorLoader(
				modelLoader, uriResolver);
		IOperators operators = loader.load(new File("src/test/resources/operators/operators.xml"),
				nrlErrors);
		assertEquals(LoadedVersion.Version14, operators.getLoadedVersion());

		loader.getOperators();
		assertNotNull(operators);
		assertNull(operators.getDocumentation());

		assertEquals(2, operators.getOperators().size());

		IOperator opA = operators.getOperator("Operator one");
		assertEquals("Operator one", opA.getName());
		assertEquals("Docs!", opA.getDocumentation());
		assertNull(opA.getPurpose());

		assertEquals(2, opA.getImplementationDetails().size());
		assertEquals("OpClass", opA.getImplementationDetail("JAVA_CLASS"));
		assertEquals("operatorOne", opA.getImplementationDetail("JAVA_METHOD"));
		assertNull(opA.getImplementationDetail("JAVA_FOO"));

		IParameter paramA = opA.getParameters().get(0);
		assertEquals("paramA", paramA.getName());
		assertEquals(NRLDataType.DECIMAL, paramA.getNRLDataType());
		assertEquals(1, paramA.getImplementationDetails().size());
		assertEquals("(int)", paramA.getImplementationDetail("JAVA_CAST"));
		assertNull(paramA.getImplementationDetail("JAVA_FOO"));

		IParameter paramB = opA.getParameters().get(1);
		assertEquals("paramB", paramB.getName());
		assertEquals(NRLDataType.STRING, paramB.getNRLDataType());

		IOperator opB = operators.getOperator("Operator two");
		assertEquals("Operator two", opB.getName());
		assertEquals(NRLDataType.UNKNOWN, opB.getNRLReturnType());
		assertNull(opB.getDocumentation());
		assertNull(opB.getPurpose());
		assertEquals(0, opB.getParameters().size());
	}

	@Test
	public void version15ModelCanBeLoaded() throws Exception {
		XmlOperatorLoader loader = new XmlOperatorLoader(
				modelLoader, uriResolver);
		IOperators operators = loader.load(new File(
				"src/test/resources/operators/operators-1.5.xml"), nrlErrors);

		assertEquals(LoadedVersion.Version15, operators.getLoadedVersion());

		loader.getOperators();
		assertNotNull(operators);
		assertEquals("Global documentation", operators.getDocumentation());

		assertEquals(2, operators.getOperators().size());

		IOperator opA = operators.getOperator("Operator one");
		assertEquals("Operator one", opA.getName());
		assertEquals("Docs!", opA.getDocumentation());
		assertEquals("Purpose", opA.getPurpose());

		assertEquals(2, opA.getImplementationDetails().size());
		assertEquals("OpClass", opA.getImplementationDetail("JAVA_CLASS"));
		assertEquals("operatorOne", opA.getImplementationDetail("JAVA_METHOD"));
		assertNull(opA.getImplementationDetail("JAVA_FOO"));

		IParameter paramA = opA.getParameters().get(0);
		assertEquals("paramA", paramA.getName());
		assertEquals(NRLDataType.DECIMAL, paramA.getNRLDataType());
		assertEquals(1, paramA.getImplementationDetails().size());
		assertEquals("(int)", paramA.getImplementationDetail("JAVA_CAST"));
		assertNull(paramA.getImplementationDetail("JAVA_FOO"));

		IParameter paramB = opA.getParameters().get(1);
		assertEquals("paramB", paramB.getName());
		assertEquals(NRLDataType.STRING, paramB.getNRLDataType());

		IOperator opB = operators.getOperator("Operator two");
		assertEquals("Operator two", opB.getName());
		assertEquals(NRLDataType.UNKNOWN, opB.getNRLReturnType());
		assertNull(opB.getDocumentation());
		assertEquals(0, opB.getParameters().size());
		assertNull(opB.getPurpose());
	}

	@Test
	public void primitiveTypesCanBeResolved() throws Exception {
		XmlOperatorLoader loader = new XmlOperatorLoader(
				modelLoader, uriResolver);
		IOperators operators = loader.load(new File(
				"src/test/resources/operators/resolve-primitives.xml"), nrlErrors);
		assertEquals(LoadedVersion.Version14, operators.getLoadedVersion());

		loader.getOperators();
		assertNotNull(operators);

		assertEquals(0, nrlErrors.size());

		IOperator op = operators.getOperators().get(0);
		assertEquals(8, op.getParameters().size());

		assertEquals("String", op.getParameter("string").getType().getName());
		assertEquals("Int", op.getParameter("int").getType().getName());
		assertEquals("Integer", op.getParameter("integer").getType().getName());
		assertEquals("Float", op.getParameter("float").getType().getName());
		assertEquals("Double", op.getParameter("double").getType().getName());
		assertEquals("Decimal", op.getParameter("number").getType().getName());
		assertEquals("Boolean", op.getParameter("boolean").getType().getName());
		assertEquals("Date", op.getParameter("date").getType().getName());

		assertEquals("Void", op.getReturnType().getName());

		assertEquals(NRLDataType.STRING, op.getParameter("string").getNRLDataType());
		assertEquals(NRLDataType.INTEGER, op.getParameter("int").getNRLDataType());
		assertEquals(NRLDataType.INTEGER, op.getParameter("integer").getNRLDataType());
		assertEquals(NRLDataType.DECIMAL, op.getParameter("float").getNRLDataType());
		assertEquals(NRLDataType.DECIMAL, op.getParameter("double").getNRLDataType());
		assertEquals(NRLDataType.DECIMAL, op.getParameter("number").getNRLDataType());
		assertEquals(NRLDataType.BOOLEAN, op.getParameter("boolean").getNRLDataType());
		assertEquals(NRLDataType.DATE, op.getParameter("date").getNRLDataType());

		assertEquals(NRLDataType.VOID, op.getNRLReturnType());
	}

	@Test
	public void referencedModelCannotBeResolved() throws Exception {
		XmlOperatorLoader loader = new XmlOperatorLoader(
				modelLoader, uriResolver);
		IOperators operators = loader.load(new File(
				"src/test/resources/operators/model-resolution-failed.xml"), nrlErrors);
		assertEquals(LoadedVersion.Version14, operators.getLoadedVersion());

		assertNotNull(operators);

		assertTrue(nrlErrors.size() > 0);
		assertTrue(nrlErrors.get(0) instanceof ModelLoadingError);
		ModelLoadingError modelLoadingError = (ModelLoadingError) nrlErrors.get(0);
		assertEquals("basicmodel.uml", modelLoadingError.getModelURI());
			}
	
	@Test
	public void referencedModelsDoNotContainReferencedTypes() throws Exception {
		XmlOperatorLoader loader = new XmlOperatorLoader(
				modelLoader, uriResolver);
		IOperators operators = loader.load(new File(
		"src/test/resources/operators/type-resolution-failed.xml"), nrlErrors);
		assertEquals(LoadedVersion.Version14, operators.getLoadedVersion());
		
		assertNotNull(operators);
		
		assertTrue(nrlErrors.size() > 0);
		assertErrorsContainsErrorWithMessageContaining("Cannot find return type of \"Model Test\"");
	}
	
	@Test
	public void referencedModelTypesAreResolved() throws Exception {
		XmlOperatorLoader loader = new XmlOperatorLoader(
				modelLoader, uriResolver);
		IOperators operators = loader.load(new File(
		"src/test/resources/operators/model-types-resolved.xml"), nrlErrors);
		assertEquals(LoadedVersion.Version14, operators.getLoadedVersion());
		
		assertNotNull(operators);
		assertEquals(0, nrlErrors.size());

		IOperator operator = operators.getOperator("Model Test");
		assertThat(operator.getNRLReturnType(), is(equalTo(NRLDataType.UNKNOWN)));
		assertThat(operator.getReturnType().getName(), is(equalTo("TradeHeader")));
		IParameter parameter = operator.getParameter("trade");
		assertThat(parameter.getNRLDataType(), is(equalTo(NRLDataType.UNKNOWN)));
		assertThat(parameter.getType().getName(), is(equalTo("Trade")));
		
	}

	@Test
	public void operatorsWithNoDeclaredTypesHaveUnresolvedTypesInModel() throws Exception {
		XmlOperatorLoader loader = new XmlOperatorLoader(
				modelLoader, uriResolver);
		IOperators operators = loader.load(new File(
				"src/test/resources/operators/resolve-untyped.xml"), nrlErrors);
		assertEquals(LoadedVersion.Version14, operators.getLoadedVersion());

		assertNotNull(operators);

		assertEquals(0, nrlErrors.size());

		IOperator op = operators.getOperators().get(0);
		assertEquals(1, op.getParameters().size());

		assertNull(op.getParameters().get(0).getType());
		assertEquals(NRLDataType.UNKNOWN, op.getParameters().get(0).getNRLDataType());

		assertNull(op.getReturnType());
		assertEquals(NRLDataType.UNKNOWN, op.getNRLReturnType());
	}

	@Test
	public void save() throws Exception {
		// Load a file and save it right back
		XmlOperatorLoader loader = new XmlOperatorLoader(
				modelLoader, uriResolver);
		IOperators operators = loader.load(new File(
				"src/test/resources/operators/operators-1.5.xml"), nrlErrors);
		assertEquals(LoadedVersion.Version15, operators.getLoadedVersion());

		assertNotNull(operators);

		StringWriter sw = new StringWriter();
		loader.save(operators, sw);

		// Validate the output against the schema
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(true);

		SAXParser parser = factory.newSAXParser();
		parser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
		parser.setProperty(JAXP_SCHEMA_SOURCE, new File("src/main/resources/operators-1.5.xsd"));

		XMLReader reader = parser.getXMLReader();
		reader.setErrorHandler(new ErrorHandler() {
			public void error(SAXParseException exception) throws SAXException {
				fail("Error in XML file: " + exception.getMessage());
			}

			public void fatalError(SAXParseException exception) throws SAXException {
				fail("Error in XML file: " + exception.getMessage());
			}

			public void warning(SAXParseException exception) throws SAXException {
				fail("Error in XML file: " + exception.getMessage());
			}
		});
		reader.parse(new InputSource(new StringReader(sw.toString())));
	}
	

	private void assertErrorsContainsErrorWithMessageContaining(String string) {
		for (NRLError error : nrlErrors) {
			if(error.getMessage().contains(string)){
				return;
			}
		}
		fail(String.format("No error contained string \"%s\"", string));
		
	}
}
