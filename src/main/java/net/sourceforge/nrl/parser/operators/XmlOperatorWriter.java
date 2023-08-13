package net.sourceforge.nrl.parser.operators;

import java.io.IOException;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.sourceforge.nrl.parser.jaxb15.JaxbImplementationDetail;
import net.sourceforge.nrl.parser.jaxb15.JaxbOperator;
import net.sourceforge.nrl.parser.jaxb15.JaxbOperators;
import net.sourceforge.nrl.parser.jaxb15.JaxbParameter;
import net.sourceforge.nrl.parser.jaxb15.JaxbParameters;
import net.sourceforge.nrl.parser.jaxb15.ObjectFactory;
import net.sourceforge.nrl.parser.model.IDataType;

/**
 * Save support for the operator classes.
 *
 * @since 1.4.10
 */
public class XmlOperatorWriter {

	private static JAXBContext version14Context, version15Context;
	
	public XmlOperatorWriter() {
		initialiseJAXBContexts();
	}
	
	private static synchronized void initialiseJAXBContexts() {
		if (version14Context == null) {
			try {
				version14Context = JAXBContext.newInstance("net.sourceforge.nrl.parser.jaxb14",
						XmlOperatorLoader.class.getClassLoader());
			} catch (JAXBException e) {
				throw new RuntimeException(
						"Internal error: could not initialise operator JAXB context", e
								.getLinkedException() != null ? e.getLinkedException() : e);
			}
		}

		if (version15Context == null) {
			try {
				version15Context = JAXBContext.newInstance("net.sourceforge.nrl.parser.jaxb15",
						XmlOperatorLoader.class.getClassLoader());
			} catch (JAXBException e) {
				throw new RuntimeException(
						"Internal error: could not initialise operator JAXB context", e
								.getLinkedException() != null ? e.getLinkedException() : e);
			}
		}
	}
	
	/**
	 * Save the mapping using a writer. Always indents.
	 * 
	 * @param operators the operators to save
	 * @param writer the writer
	 * @throws IOException
	 */
	public void save(IOperators operators, Writer writer) throws Exception {
		JaxbOperators jaxbOps = new JaxbOperators();

		jaxbOps.setDocumentation(operators.getDocumentation());

		for (String ref : operators.getModelFileNames()) {
			jaxbOps.getModel().add(ref);
		}

		for (IOperator op : operators.getOperators()) {
			JaxbOperator jaxbOperator = new JaxbOperator();
			jaxbOps.getOperator().add(jaxbOperator);

			jaxbOperator.setName(op.getName().trim());
			jaxbOperator.setPurpose(op.getPurpose());

			if (op.getDocumentation() != null) {
				jaxbOperator.setDocumentation(op.getDocumentation());
			}

			for (IImplementationDetail detail : op.getImplementationDetails()) {
				JaxbImplementationDetail jaxbDetail = new JaxbImplementationDetail();
				jaxbDetail.setLabel(detail.getLabel());
				jaxbDetail.setValue(detail.getValue());
				jaxbOperator.getImplementationDetail().add(jaxbDetail);
			}

			// Output params if any
			if (op.getParameters().size() > 0) {
				JaxbParameters jaxbParameters = new JaxbParameters();
				jaxbOperator.setParameters(jaxbParameters);

				for (IParameter param : op.getParameters()) {
					JaxbParameter jaxbParam = new JaxbParameter();
					jaxbParameters.getParameter().add(jaxbParam);

					jaxbParam.setName(param.getName().trim());

					if (param.isTypeCollection()) {
						jaxbParam.setIsCollection(true);
					}

					if (param.getType() != null) {
						String typeName = null;
						if (param.getType() instanceof IDataType
								&& ((IDataType) param.getType()).isBuiltIn()) {
							typeName = param.getType().getName();
						}

						if (typeName != null) {
							jaxbParam.setType(typeName);
						} else {
							jaxbParam.setType(param.getType().getQualifiedName());
						}
					} else if (param.getTypeName() != null) {
						jaxbParam.setType(param.getTypeName());
					}

					for (IImplementationDetail detail : param.getImplementationDetails()) {
						JaxbImplementationDetail jaxbDetail = new JaxbImplementationDetail();
						jaxbDetail.setLabel(detail.getLabel());
						jaxbDetail.setValue(detail.getValue());
						jaxbParam.getImplementationDetail().add(jaxbDetail);
					}
				}
			}

			// Return type, if any
			if (op.getReturnType() != null) {
				JaxbOperator.JaxbReturnType jaxbReturn = new JaxbOperator.JaxbReturnType();
				jaxbOperator.setReturnType(jaxbReturn);

				String typeName = null;
				if (op.getReturnType() instanceof IDataType
						&& ((IDataType) op.getReturnType()).isBuiltIn()) {
					typeName = op.getReturnType().getName();
				}

				if (typeName != null) {
					jaxbReturn.setType(typeName);
				} else {
					jaxbReturn.setType(op.getReturnType().getQualifiedName());
				}
			} else if (op.getReturnTypeName() != null) {
				JaxbOperator.JaxbReturnType jaxbReturn = new JaxbOperator.JaxbReturnType();
				jaxbOperator.setReturnType(jaxbReturn);
				jaxbReturn.setType(op.getReturnTypeName());
			}
		}

		jaxbOps.setVersion(XmlOperatorLoader.CURRENT_FILE_VERSION);
		JAXBElement<JaxbOperators> root = new ObjectFactory().createOperators(jaxbOps);

		Marshaller marshaller = version15Context.createMarshaller();
		marshaller.setProperty("jaxb.formatted.output", true);
		marshaller.setProperty("jaxb.schemaLocation",
				"urn:net:sourceforge:nrl:1.5 operators-1.5.xsd");
		marshaller.marshal(root, writer);
		writer.flush();
	}
	
}
