package net.sourceforge.nrl.parser.model.loader;

/**
 * Exception thrown by {@link IModelLoader}s.
 * @since 1.4.9
 */
public class ModelLoadingException extends Exception {

	private static final long serialVersionUID = 5343672343418779719L;

	public ModelLoadingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ModelLoadingException(String message) {
		super(message);
	}

}
