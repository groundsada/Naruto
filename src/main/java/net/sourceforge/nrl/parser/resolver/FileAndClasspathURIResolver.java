package net.sourceforge.nrl.parser.resolver;

import static java.lang.String.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import net.sourceforge.nrl.parser.util.URIUtils;

/**
 * An implementation of a URI resolver that can load from files and from the classpath.
 * 
 * @since 1.4.9
 */
public class FileAndClasspathURIResolver extends AbstractURIResolver {

	private final ClassLoader classLoader;

	public FileAndClasspathURIResolver(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	protected void assertAbsoluteFileURIOrClasspath(URI uri) throws ResolverException {
		String scheme = uri.getScheme();
		if (scheme == null || (!scheme.equals("file") && !scheme.equals("classpath"))) {
			throw new ResolverException(format("Unsupported URI scheme, found %s in %s", scheme,
					uri.toString()));
		}

		if (scheme.equals("file") && !uri.getSchemeSpecificPart().startsWith("/")) {
			throw new ResolverException(format("Only absolute file URIs are permitted, found %s",
					uri.toString()));
		}
	}

	public InputStream openStream(URI uri) throws ResolverException {
		
		uri = URIUtils.standardiseSeparators(uri);
		assertAbsoluteFileURIOrClasspath(uri);

		String scheme = uri.getScheme();
		if (scheme.equals("file")) {
			File file = new File(uri);
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException e) {
				throw new ResolverException(format("File not found: %s", file.getAbsolutePath()));
			}
		} else if (scheme.equals("classpath")) {
			String classpathResource = uri.getSchemeSpecificPart();
			if (classpathResource.startsWith("/")) {
				classpathResource = classpathResource.substring(1);
			}

			InputStream stream = classLoader.getResourceAsStream(classpathResource);
			if (stream == null) {
				throw new ResolverException(format("Could not find resource on the classpath: %s",
						uri.toString()));
			}
			return stream;
		}

		throw new IllegalArgumentException(format("Internal error: unhandled scheme %s.", scheme));
	}

	public InputStream openStream(URI baseURI, URI resourceURI) throws ResolverException {
		assertAbsoluteFileURIOrClasspath(baseURI);
		
		URI resolvedURI = baseURI.resolve(resourceURI);
		return openStream(resolvedURI);
	}

	public InputStream openStream(URI baseURI, String resource) throws ResolverException {
		assertAbsoluteFileURIOrClasspath(baseURI);
		try {
			URI resourceURI = new URI(org.eclipse.emf.common.util.URI.encodeQuery(resource, true));
			return openStream(baseURI, resourceURI);
		} catch (URISyntaxException e) {
			throw new ResolverException(format("Invalid resource URI %s: %s", resource, e
					.getMessage()));
		}
	}

}
