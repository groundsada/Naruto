/*
 * The contents of this file are subject to the Mozilla Public License 
 * Version 1.1 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See 
 * the License for the specific language governing rights and limitations 
 * under the License.
 * 
 * The Original Code is the NRL Parser, released 28 April 2006, 
 * Copyright (c) Christian Nentwich. The Initial Developer of the 
 * Original Code is Christian Nentwich. Portions created by contributors 
 * identified in the NOTICES file are Copyright (c) the contributors. 
 * All Rights Reserved. 
 */
package net.sourceforge.nrl.parser.operators;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
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

import org.xml.sax.SAXException;

/**
 * Persistence support for the operator classes. To use this for loading:
 * <ul>
 * <li>Call {@link #load(File)} or {@link #load(Reader)}. This will load the file, and if
 * there are no errors, return the file version.
 * <li>If there were no errors, call {@link #getOperators()} to get the loaded operators.
 * </ul>
 * <p>
 * For saving, just call {@link #save(IOperators, Writer)}.
 * 
 * @deprecated Use {@link XmlOperatorLoader}.
 * @author Christian Nentwich
 * @since 1.5.0
 */
@Deprecated
public class XmlOperatorPersistence {

	/**
	 * Returned by {@link XmlOperatorPersistence#load(Reader)}, the version of the
	 * operator file.
	 */
	public enum LoadedVersion {
		Version14, Version15
	}

	/**
	 * Version 1.5 operator file namespace: <code>urn:net:sourceforge:nrl:1.5</code>
	 */
	private static final String NAMESPACE_VERSION_1_5 = "urn:net:sourceforge:nrl:1.5";

	/**
	 * File version of this persistence mechanism: currently 1.5.0
	 */
	private static final String CURRENT_FILE_VERSION = "1.5.0";

	private static JAXBContext version14Context, version15Context;

	private IOperators loadedOperators = null;

	/**
	 * Initialise the persistence class. The caller must provide the JAXB context for the
	 * version 1.4 schema and version 1.5 schema. This is so that the caller can make its
	 * own decision about caching / creating the context.
	 * 
	 * @deprecated Use {@link XmlOperatorPersistence#XmlOperatorPersistence()} instead
	 * @param version14Context context for version 1.4 schema (package is
	 * net.sourceforge.nrl.parser.jaxb14)
	 * @param version15Context context for version 1.5 schema (package is
	 * net.sourceforge.nrl.parser.jaxb15)
	 */
	@Deprecated
	public XmlOperatorPersistence(JAXBContext version14Context, JAXBContext version15Context) {
		this();
	}

	public XmlOperatorPersistence() {
		initialiseJAXBContexts();
	}

	public IOperators getOperators() {
		return loadedOperators;
	}

	private static synchronized void initialiseJAXBContexts() {
		if (version14Context == null) {
			try {
				version14Context = JAXBContext.newInstance("net.sourceforge.nrl.parser.jaxb14");
			} catch (JAXBException e) {
				throw new RuntimeException(
						"Internal error: could not initialise operator JAXB context", e
								.getLinkedException() != null ? e.getLinkedException() : e);
			}
		}

		if (version15Context == null) {
			try {
				version15Context = JAXBContext.newInstance("net.sourceforge.nrl.parser.jaxb15");
			} catch (JAXBException e) {
				throw new RuntimeException(
						"Internal error: could not initialise operator JAXB context", e
								.getLinkedException() != null ? e.getLinkedException() : e);
			}
		}
	}

	/**
	 * Load from a file - see {@link #load(Reader)}.
	 * 
	 * @param file the file to load from
	 * @return the loaded file version
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws IOException
	 */
	public LoadedVersion load(File file) throws JAXBException, SAXException, IOException {
		return load(new FileReader(file));
	}

	/**
	 * Load an operator XML file using a reader. If this was successful, returns the
	 * version of the file.
	 * <p>
	 * If the call was successful, calling {@link #getOperators()} next returns the loaded
	 * operators.
	 * 
	 * @param reader the reader
	 * @return the version
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws IOException
	 */
	public LoadedVersion load(Reader reader) throws JAXBException, SAXException, IOException {
		// Read into a string first
		StringBuilder intermediate = new StringBuilder();
		char[] buffer = new char[5000];

		while (reader.ready()) {
			int count = reader.read(buffer);
			intermediate.append(new String(buffer, 0, count));
		}

		if (intermediate.indexOf(NAMESPACE_VERSION_1_5) != -1) {
			loadedOperators = Version15OperatorLoader.load(intermediate.toString(),
					version15Context);
			return LoadedVersion.Version15;
		} else {
			loadedOperators = Version14OperatorLoader.load(intermediate.toString(),
					version14Context);
			return LoadedVersion.Version14;
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

		jaxbOps.setVersion(CURRENT_FILE_VERSION);
		JAXBElement<JaxbOperators> root = new ObjectFactory().createOperators(jaxbOps);

		Marshaller marshaller = version15Context.createMarshaller();
		marshaller.setProperty("jaxb.formatted.output", true);
		marshaller.setProperty("jaxb.schemaLocation",
				"urn:net:sourceforge:nrl:1.5 operators-1.5.xsd");
		marshaller.marshal(root, writer);
		writer.flush();
	}
}
