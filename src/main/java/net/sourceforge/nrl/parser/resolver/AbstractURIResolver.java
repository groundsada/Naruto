package net.sourceforge.nrl.parser.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public abstract class AbstractURIResolver implements IURIResolver {

	public boolean isURIResolvable(URI uri) {
		boolean isResolvable = false;
		InputStream stream = null;
		try {
			stream = openStream(uri);
			isResolvable = stream != null;
		} catch(Exception e) {
			
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
				
			}
		}
		return isResolvable;
	}

	public boolean isURIResolvable(URI baseURI, URI resourceURI) {
		boolean isResolvable = false;
		InputStream stream = null;
		try {
			stream = openStream(baseURI, resourceURI);
			isResolvable = stream != null;
		} catch(Exception e) {
			
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
				
			}
		}
		return isResolvable;
	}

	public boolean isURIResolvable(URI baseURI, String resourceURI) {
		boolean isResolvable = false;
		InputStream stream = null;
		try {
			stream = openStream(baseURI, resourceURI);
			isResolvable = stream != null;
		} catch(Exception e) {
			
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
				
			}
		}
		return isResolvable;
	}
	
}
