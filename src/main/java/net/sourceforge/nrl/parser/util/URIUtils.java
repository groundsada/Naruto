package net.sourceforge.nrl.parser.util;

import java.net.URI;

public class URIUtils {
	
	/**
	 * Removes any backslahses or escaped backslashes from a URI. If, for some reason, it 
	 * was not possible to construct a new URI after removing the back slashes, then the
	 * original is returned.
	 * 
	 * @param uri
	 * @return
	 */
	
	public static URI standardiseSeparators(URI uri) {
		try {
			String unescapedURI = org.eclipse.emf.common.util.URI.decode(uri.toString());
			unescapedURI = unescapedURI.replace('\\', '/');
			return new URI(unescapedURI);
		} catch (Exception e) {
			return uri;
		}
	}
	
}
