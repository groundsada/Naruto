package net.sourceforge.nrl.parser.operators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.sourceforge.nrl.parser.NRLError;
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

public class XmlOperatorWriterTest {

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

	
}
