package net.sourceforge.nrl.parser.operators;

import java.io.InputStream;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import net.sourceforge.nrl.parser.jaxb14.JaxbImplementationDetail;
import net.sourceforge.nrl.parser.jaxb14.JaxbOperator;
import net.sourceforge.nrl.parser.jaxb14.JaxbOperators;
import net.sourceforge.nrl.parser.jaxb14.JaxbParameter;
import net.sourceforge.nrl.parser.operators.IOperators.LoadedVersion;

import org.xml.sax.SAXException;

/**
 * Internal class: loads a version 1.4 operator file. Use {@link XmlOperatorPersistence},
 * which uses this class, instead of calling this directly.
 * 
 * @author Christian Nentwich
 */
final class Version14OperatorLoader {

	public static Operators load(String xmlContent, JAXBContext context) throws JAXBException,
			SAXException {
		// Loading using JAXB
		Unmarshaller unmarshaller = context.createUnmarshaller();

		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		InputStream schemaStream = Version14OperatorLoader.class
				.getResourceAsStream("/nrlparser.xsd");
		Schema schema = schemaFactory.newSchema(new StreamSource(schemaStream));

		unmarshaller.setSchema(schema);

		JAXBElement<?> root = (JAXBElement<?>) unmarshaller.unmarshal(new StringReader(xmlContent));
		JaxbOperators operators = (JaxbOperators) root.getValue();

		Operators result = new Operators();
		result.setLoadedVersion(LoadedVersion.Version14);
		
		for (String model : operators.getModel()) {
			result.addModelFileName(model);
		}

		for (JaxbOperator jaxbOp : operators.getOperator()) {

			Operator op = new Operator();

			op.setName(jaxbOp.getName().trim());
			op.setDocumentation(jaxbOp.getDocumentation());
		
			for (JaxbImplementationDetail jaxbDetail : jaxbOp.getImplementationDetail()) {
				IImplementationDetail detail = parseImplementationDetail(jaxbDetail);
				op.addImplementationDetail(detail);
			}

			if (jaxbOp.getParameters() != null) {
				for (JaxbParameter jaxbParam : jaxbOp.getParameters().getParameter()) {
					Parameter param = new Parameter();

					param.setName(jaxbParam.getName().trim());
					if (jaxbParam.getType() != null) {
						param.setTypeName(jaxbParam.getType());
					}

					if (jaxbParam.isIsCollection() != null) {
						param.setTypeIsCollection(jaxbParam.isIsCollection().booleanValue());
					}

					for (JaxbImplementationDetail jaxbDetail : jaxbParam.getImplementationDetail()) {
						IImplementationDetail detail = parseImplementationDetail(jaxbDetail);
						if (detail != null)
							param.addImplementationDetail(detail);
					}

					op.addParameter(param);
				}
			}
			if (jaxbOp.getReturnType() != null) {
				op.setReturnTypeName(jaxbOp.getReturnType().getType().trim());
			}

			result.addOperator(op);
		}

		return result;
	}

	/**
	 * Parse an implementationDetail element and return it as an object.
	 * 
	 * @param impl the element to parse
	 * @return the object or null if not parsable
	 */
	private static IImplementationDetail parseImplementationDetail(JaxbImplementationDetail impl) {
		return new ImplementationDetail(impl.getLabel().trim(), impl.getValue().trim());
	}
}
