package net.sourceforge.nrl.parser.model.loader;

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.resource.impl.URIHandlerImpl;

/**
 * EMF {@link URIHandler} for classpath resources.
 * 
 * @Since 1.4.9
 */
class ClasspathURIHandler extends URIHandlerImpl {

	private ClassLoader classLoader;

	public ClasspathURIHandler() {
		classLoader = getClass().getClassLoader();
	}

	public ClasspathURIHandler(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public boolean canHandle(URI uri) {
		return "classpath".equals(uri.scheme());
	}

	@Override
	public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException {
		String resourcePath;
		if (uri.hasPath()) {
			resourcePath = uri.path();
		} else {
			resourcePath = uri.opaquePart();
		}
		if(resourcePath.startsWith("/")){
			resourcePath = resourcePath.substring(1);
		}
		InputStream stream = classLoader.getResourceAsStream(resourcePath);
		
		if (stream == null) {
			throw new IOException(format("Could not open stream at %s.", resourcePath));
		}
		return stream;
	}

}