package net.sourceforge.nrl.parser.resolver;

import java.io.InputStream;
import java.net.URI;

/**
 * A resolver that can open streams and resolve references for URIs.
 * <p>
 * Implementations will provide support for different URI protocols.
 * 
 * @since 1.4.9
 */
public interface IURIResolver {

	/**
	 * Open a stream to read a resource from a URI.
	 * 
	 * @param uri the URI
	 * @return the stream
	 */
	public InputStream openStream(URI uri) throws ResolverException;

	/**
	 * Open a stream to read a resource from a URI referenced relative to a base URI.
	 * 
	 * @param baseURI the URI
	 * @param resourceURI a URI to resolve relative to baseURI
	 * @return the stream
	 */
	public InputStream openStream(URI baseURI, URI resourceURI) throws ResolverException;

	/**
	 * Convenience method alternative to {@link #openStream(URI, URI)}.
	 */
	public InputStream openStream(URI baseURI, String resourceURI) throws ResolverException;

	/**
	 * Determine if the given URI is resolvable i.e. it is possible to read from the
	 * resource.
	 * 
	 * @param uri the URI
	 * @return <code>true</code> if the URI is resolvable, <code>false</code> otherwise
	 */
	public boolean isURIResolvable(URI uri);
	
	/**
	 * Determine if the given resourceURI is resolvable relative to the base URI 
	 * i.e. it is possible to read from the resource.
	 * 
	 * @param baseURI the base URI
	 * @param resourceURI a URI to resolve relative to baseURI
	 * @return <code>true</code> if the URI is resolvable, <code>false</code> otherwise
	 */
	public boolean isURIResolvable(URI baseURI, URI resourceURI);
	
	/**
	 * Convenience method alternative to {@link #isURIResolvable(URI, String)}.
	 * 
	 * @param baseURI the base URI
	 * @param resourceURI a URI to resolve relative to baseURI
	 * @return <code>true</code> if the URI is resolvable, <code>false</code> otherwise
	 */
	public boolean isURIResolvable(URI baseURI, String resourceURI);
	
}
