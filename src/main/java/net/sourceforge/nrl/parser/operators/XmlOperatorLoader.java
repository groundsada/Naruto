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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.sourceforge.nrl.parser.ModelLoadingError;
import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.jaxb15.JaxbImplementationDetail;
import net.sourceforge.nrl.parser.jaxb15.JaxbOperator;
import net.sourceforge.nrl.parser.jaxb15.JaxbOperators;
import net.sourceforge.nrl.parser.jaxb15.JaxbParameter;
import net.sourceforge.nrl.parser.jaxb15.JaxbParameters;
import net.sourceforge.nrl.parser.jaxb15.ObjectFactory;
import net.sourceforge.nrl.parser.model.IDataType;
import net.sourceforge.nrl.parser.model.IPackage;
import net.sourceforge.nrl.parser.model.ModelCollection;
import net.sourceforge.nrl.parser.model.loader.IModelLoader;
import net.sourceforge.nrl.parser.model.loader.ModelLoadingException;
import net.sourceforge.nrl.parser.resolver.IResolverFactory;
import net.sourceforge.nrl.parser.resolver.IURIResolver;
import net.sourceforge.nrl.parser.resolver.ResolverException;

import org.xml.sax.SAXException;

/**
 * Loading support for the operator classes.
 * 
 * Call {@link #load(File, List)} or {@link #load(URI, URI, List)}. This will load the
 * file and resolve model references. Any errors will be added to the supplied list.
 * Returns the version of the operator file loaded.
 * If there were no errors, call {@link #getOperators()} to get the loaded operators.
 * 
 * @since 1.4.10
 */
public class XmlOperatorLoader {

	/**
	 * Version 1.5 operator file namespace: <code>urn:net:sourceforge:nrl:1.5</code>
	 */
	private static final String NAMESPACE_VERSION_1_5 = "urn:net:sourceforge:nrl:1.5";

	/**
	 * File version of this persistence mechanism: currently 1.5.0
	 */
	protected static final String CURRENT_FILE_VERSION = "1.5.0";

	private static JAXBContext version14Context, version15Context;

	private IOperators loadedOperators = null;

	private final IModelLoader modelLoader;

	private final IURIResolver uriResolver;
	
	/**
	 * Initialise the persistence class using a factory.
	 * 
	 * @param factory the factory that will provide a model loader and URI resolver
	 */
	public XmlOperatorLoader(IResolverFactory factory) {
		this(factory.createModelLoader(), factory.createURIResolver());
	}
	
	/**
	 * Initialise the persistence class.
	 * 
	 * @param modelLoader an {@link IModelLoader} that will be used to resolve models
	 * referenced by the operators.
	 */
	public XmlOperatorLoader(IModelLoader modelLoader, IURIResolver uriResolver) {
		this.modelLoader = modelLoader;
		this.uriResolver = uriResolver;
		initialiseJAXBContexts();
	}

	public IOperators getOperators() {
		return loadedOperators;
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
	 * Load from a file - see {@link #load(Reader)}.
	 * 
	 * @param file the file to load from
	 * @return the loaded operators
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ResolverException
	 */
	public IOperators load(File file, List<NRLError> errors) throws JAXBException, SAXException,
			IOException, ResolverException {
		return load(file.getAbsoluteFile().toURI(), file.getAbsoluteFile().toURI(), errors);
	}

	/**
	 * Load an operator file from a URI (relative or absolute) resolved against an absolute base URI.
	 * <p>
	 * If the call was successful, calling {@link #getOperators()} next returns the loaded
	 * operators. The {@link IOperators} returned will have fully resolved and type
	 * checked models.
	 * 
	 * @param operatorFileUri an absolute uri pointing to the operator file
	 * @return the loaded operators
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ResolverException
	 */
	public IOperators load(URI baseURI, URI operatorFileUri, List<NRLError> errors)
			throws JAXBException, SAXException, IOException, ResolverException {
		InputStream operatorStream = uriResolver.openStream(baseURI, operatorFileUri);

		Reader operatorReader = new InputStreamReader(operatorStream);

		// Read into a string first
		StringBuilder intermediate = new StringBuilder();
		char[] buffer = new char[5000];

		while (operatorReader.ready()) {
			int count = operatorReader.read(buffer);
			intermediate.append(new String(buffer, 0, count));
		}

		if (intermediate.indexOf(NAMESPACE_VERSION_1_5) != -1) {
			loadedOperators = Version15OperatorLoader.load(intermediate.toString(),
					version15Context);
		} else {
			loadedOperators = Version14OperatorLoader.load(intermediate.toString(),
					version14Context);
		}
		URI resolvedOperatorFileURI = baseURI.resolve(operatorFileUri);
		resolveModelReferences(resolvedOperatorFileURI, loadedOperators, errors);
		return loadedOperators;
	}

	private void resolveModelReferences(URI operatorFileUri, IOperators operators,
			List<NRLError> errors) {
		List<String> modelFileNames = operators.getModelFileNames();

		ModelCollection modelCollection = new ModelCollection();

		for (String modelFileName : modelFileNames) {
			try {
				IPackage model = modelLoader.loadModel(operatorFileUri, modelFileName);
				if (model != null) {
					modelCollection.addModelPackage(model);
				} else {
					errors.add(new ModelLoadingError(modelFileName));
				}
			} catch (ModelLoadingException e) {
				errors.add(new ModelLoadingError(modelFileName));
			}
		}

		List<NRLError> resolutionErrors = operators.resolveModelReferences(modelCollection);
		errors.addAll(resolutionErrors);
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
