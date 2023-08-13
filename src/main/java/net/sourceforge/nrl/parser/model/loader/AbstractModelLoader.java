package net.sourceforge.nrl.parser.model.loader;

import static java.lang.String.format;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.nrl.parser.model.IPackage;
import net.sourceforge.nrl.parser.model.uml2.UML2ModelLoader;
import net.sourceforge.nrl.parser.model.xsd.XSDModelLoader;
import net.sourceforge.nrl.parser.util.URIUtils;

import org.eclipse.emf.ecore.resource.Resource;

public abstract class AbstractModelLoader implements IModelLoader {

	public static final Set<String> PERMITTED_MODEL_FILE_EXTENSIONS;

	private static final Set<String> UML_FILE_EXTENSIONS;

	static {
		PERMITTED_MODEL_FILE_EXTENSIONS = new HashSet<String>();
		PERMITTED_MODEL_FILE_EXTENSIONS.add("xsd");
		PERMITTED_MODEL_FILE_EXTENSIONS.add("uml");
		PERMITTED_MODEL_FILE_EXTENSIONS.add("uml2");
		PERMITTED_MODEL_FILE_EXTENSIONS.add("emx");

		UML_FILE_EXTENSIONS = new HashSet<String>();
		UML_FILE_EXTENSIONS.add("uml");
		UML_FILE_EXTENSIONS.add("uml2");
		UML_FILE_EXTENSIONS.add("emx");
	}

	protected abstract Resource createUMLResource(URI resolvedModelURI)
			throws ModelLoadingException;

	protected abstract Resource createXSDResource(URI resolvedModelURI)
			throws ModelLoadingException;

	private String getExtension(URI resolvedModelURI) throws ModelLoadingException {
		String path = resolvedModelURI.getSchemeSpecificPart();
		int lastDotIndex = path.lastIndexOf('.');
		if (lastDotIndex < 0 || lastDotIndex == path.length() - 1) {
			throw new ModelLoadingException(format("Model URI contains no file extension: %s",
					resolvedModelURI.toString()));
		}
		return path.substring(lastDotIndex + 1);
	}

	public IPackage loadModel(URI uri) throws ModelLoadingException {
		String modelExtension = getExtension(uri);
		IPackage model;
		if ("xsd".equals(modelExtension)) {
			model = loadXSDModel(uri);
		} else if (UML_FILE_EXTENSIONS.contains(modelExtension)) {
			model = loadUMLModel(uri);
		} else {
			throw new ModelLoadingException(format("Unsupported file type %s found in %s",
					modelExtension, uri.toString()));
		}
		if (model == null) {
			return null;
		}
		return model;
	}

	public final IPackage loadModel(File baseFile, String modelURI) throws ModelLoadingException {
		try {
			return loadModel(baseFile, new URI(org.eclipse.emf.common.util.URI.encodeQuery(modelURI, true)));
		} catch (URISyntaxException e) {
			throw new ModelLoadingException(format("Illegal model URI: %s", modelURI.toString()));
		}
	}

	public final IPackage loadModel(File baseFile, URI modelURI) throws ModelLoadingException {
		return loadModel(baseFile.toURI(), modelURI);
	}

	public final IPackage loadModel(URI baseURI, String modelURI) throws ModelLoadingException {
		try {
			return loadModel(baseURI, new URI(org.eclipse.emf.common.util.URI.encodeQuery(modelURI, true) ));
		} catch (URISyntaxException e) {
			throw new ModelLoadingException(format("Illegal model URI: %s", modelURI.toString()));
		}
	}

	public final IPackage loadModel(URI baseURI, URI modelURI) throws ModelLoadingException {
		if (baseURI == null) {
			throw new IllegalArgumentException("baseURI must be non-null");
		}
		if (modelURI == null) {
			throw new IllegalArgumentException("modelURI must be non-null");
		}
		
		baseURI = URIUtils.standardiseSeparators(baseURI);
		modelURI = URIUtils.standardiseSeparators(modelURI);

		
		validateBaseURI(baseURI);
		validateModelURI(modelURI);

		URI resolvedModelURI = baseURI.resolve(modelURI);
		return loadModel(resolvedModelURI);
	}


	private IPackage loadUMLModel(URI resolvedModelURI) throws ModelLoadingException {
		Resource res = createUMLResource(resolvedModelURI);
		UML2ModelLoader loader = new UML2ModelLoader();
		try {
			return loader.load(res);
		} catch (Exception e) {
			throw new ModelLoadingException(format("Failed to load %s.", resolvedModelURI
					.toString()), e);
		}
	}

	private IPackage loadXSDModel(URI resolvedModelURI) throws ModelLoadingException {
		Resource res = createXSDResource(resolvedModelURI);
		XSDModelLoader loader = new XSDModelLoader();
		try {
			return loader.load(res, (Map<?, ?>) null);
		} catch (Exception e) {
			throw new ModelLoadingException(format("Failed to load %s.", resolvedModelURI
					.toString()), e);
		}
	}

	protected abstract void validateBaseURI(URI baseURI) throws ModelLoadingException;

	protected abstract void validateModelURI(URI modelURI) throws ModelLoadingException;

}
