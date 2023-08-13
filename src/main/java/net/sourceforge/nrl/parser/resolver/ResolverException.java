package net.sourceforge.nrl.parser.resolver;

/**
 * An exception thrown by an {@link IURIResolver}.
 * 
 * @since 1.4.9
 */
public class ResolverException extends Exception {

	private static final long serialVersionUID = 5136712961362978107L;

	public ResolverException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResolverException(String message) {
		super(message);
	}

	public ResolverException(Throwable cause) {
		super(cause);
	}

}
