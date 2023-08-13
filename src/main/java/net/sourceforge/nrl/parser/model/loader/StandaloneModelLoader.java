package net.sourceforge.nrl.parser.model.loader;

import static java.lang.String.format;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.nrl.parser.model.IPackage;

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.mapping.ecore2xml.Ecore2XMLPackage;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UML212UMLResource;
import org.eclipse.uml2.uml.resource.UML22UMLResource;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;

public class StandaloneModelLoader extends AbstractModelLoader {

	public static final Set<String> PERMITTED_URI_SCHEMES;

	static {
		PERMITTED_URI_SCHEMES = new HashSet<String>();
		PERMITTED_URI_SCHEMES.add("classpath");
		PERMITTED_URI_SCHEMES.add("file");
	}

	private final ClassLoader classLoader;

	private Map<URI, IPackage> cachedModels = new HashMap<URI, IPackage>();
	
	private Map<org.eclipse.emf.common.util.URI, org.eclipse.emf.common.util.URI>  customURIMap;
	
	public StandaloneModelLoader() {
		this(StandaloneModelLoader.class.getClassLoader());
	}

	public StandaloneModelLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
		this.customURIMap = new HashMap<org.eclipse.emf.common.util.URI, org.eclipse.emf.common.util.URI>();
	}
	
	protected Map<org.eclipse.emf.common.util.URI, org.eclipse.emf.common.util.URI> getCustomURIMap() {
		return this.customURIMap;
	}
	
	@Override
	public synchronized IPackage loadModel(URI uri) throws ModelLoadingException {
		if (cachedModels.containsKey(uri)) {
			return cachedModels.get(uri);
		}

		IPackage model = super.loadModel(uri);
		cachedModels.put(uri, model);
		return model;
	}

	@Override
	protected Resource createUMLResource(URI resolvedModelURI) {
		org.eclipse.emf.common.util.URI emfURI = createEMFURI(resolvedModelURI);

		ResourceSet set = new ResourceSetImpl();

		set.getURIConverter().getURIHandlers().add(0, new ClasspathURIHandler(classLoader));

		Registry packageRegistry = set.getPackageRegistry();
		packageRegistry.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		packageRegistry.put(Ecore2XMLPackage.eNS_URI, Ecore2XMLPackage.eINSTANCE);
		packageRegistry.put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
		packageRegistry.put("http://www.eclipse.org/uml2/2.0.0/UML", UMLPackage.eINSTANCE);
		packageRegistry.put(UML212UMLResource.UML_METAMODEL_NS_URI, UMLPackage.eINSTANCE);
		packageRegistry.put(UML22UMLResource.UML2_METAMODEL_NS_URI, UMLPackage.eINSTANCE);
		packageRegistry.put("http://www.eclipse.org/uml2/3.0.0/UML", UMLPackage.eINSTANCE);

		Map<String, Object> extensionToFactoryMap = set.getResourceFactoryRegistry()
				.getExtensionToFactoryMap();
		extensionToFactoryMap.put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		extensionToFactoryMap.put("emx", UMLResource.Factory.INSTANCE);
		extensionToFactoryMap.put("uml2", UMLResource.Factory.INSTANCE);

		Map<org.eclipse.emf.common.util.URI, org.eclipse.emf.common.util.URI> uriMap = set
				.getURIConverter().getURIMap();
		org.eclipse.emf.common.util.URI uri = org.eclipse.emf.common.util.URI
				.createURI("classpath:/");
		uriMap.put(org.eclipse.emf.common.util.URI.createURI(UMLResource.LIBRARIES_PATHMAP), uri
				.appendSegment("libraries").appendSegment(""));
		uriMap.put(org.eclipse.emf.common.util.URI.createURI(UMLResource.METAMODELS_PATHMAP), uri
				.appendSegment("metamodels").appendSegment(""));
		uriMap.put(org.eclipse.emf.common.util.URI.createURI(UMLResource.PROFILES_PATHMAP), uri
				.appendSegment("profiles").appendSegment(""));
		
		uriMap.putAll(this.customURIMap);
		
		return set.createResource(emfURI);
	}

	private org.eclipse.emf.common.util.URI createEMFURI(URI resolvedModelURI) {
		String uri = org.eclipse.emf.common.util.URI.decode(resolvedModelURI.toString());
		org.eclipse.emf.common.util.URI emfURI = org.eclipse.emf.common.util.URI.createURI(uri);
		return emfURI;
	}

	@Override
	protected Resource createXSDResource(URI resolvedModelURI) {
		org.eclipse.emf.common.util.URI emfURI = createEMFURI(resolvedModelURI);

		ResourceSetImpl resourceSet = new ResourceSetImpl();
		resourceSet.getURIConverter().getURIHandlers().add(0, new ClasspathURIHandler(classLoader));

		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
				Resource.Factory.Registry.DEFAULT_EXTENSION, new XSDResourceFactoryImpl());

		Resource resource = resourceSet.createResource(emfURI);
		return resource;
	}

	@Override
	protected void validateBaseURI(URI baseURI) throws ModelLoadingException {
		String baseURIScheme = baseURI.getScheme();

		if (baseURIScheme == null) {
			throw new ModelLoadingException(format(
					"The base URI must specify a URI scheme. Relative URIs are not permitted, "
							+ "found: %s", baseURI.toString()));
		}

		if (!PERMITTED_URI_SCHEMES.contains(baseURIScheme)) {
			throw new ModelLoadingException(format(
					"The URI scheme %s is not supported for base URIs, in %s.", baseURIScheme,
					baseURI.toString()));
		}
		if ("file".equals(baseURIScheme) && !baseURI.getSchemeSpecificPart().startsWith("/")) {
			throw new ModelLoadingException(format(
					"The base URI must not be a relative URI, found: %s", baseURI.toString()));
		}

		validateClasspathURI(baseURI, baseURIScheme);
	}

	private void validateClasspathURI(URI uri, String uriScheme) throws ModelLoadingException {
		if ("classpath".equals(uriScheme) && !uri.getSchemeSpecificPart().startsWith("/")) {
			throw new ModelLoadingException(format(
					"Classpath URIs must start with a '/', found: %s", uri.toString()));

		}
	}

	protected void validateModelURI(URI modelURI) throws ModelLoadingException {
		String modelURIScheme = modelURI.getScheme();
		if (modelURIScheme != null && !PERMITTED_URI_SCHEMES.contains(modelURIScheme)) {
			throw new ModelLoadingException(format(
					"The URI scheme %s is not supported for model URIs, in %s.", modelURIScheme,
					modelURI.toString()));
		}
		validateClasspathURI(modelURI, modelURIScheme);
	}
}
