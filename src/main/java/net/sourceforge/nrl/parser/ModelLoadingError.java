package net.sourceforge.nrl.parser;

/**
 * Specific error for models which are referenced by something but could not
 * be loaded.
 * 
 * @since 1.4.10
 */
public class ModelLoadingError extends SemanticError {

	private String modelURI;
	
	public ModelLoadingError(String modelURI) {
		this(modelURI, String.format("Could not load model: %s",
				modelURI));
	}
	
	public ModelLoadingError(String modelURI, String message) {
		super(IStatusCode.MODEL_NOT_FOUND, 1, 0, message);
		this.modelURI = modelURI;
	}
	
	/**
	 * Returns the URI of the model which could not be loaded.
	 * 
	 * @return
	 */
	public String getModelURI() {
		return this.modelURI;
	}

}
