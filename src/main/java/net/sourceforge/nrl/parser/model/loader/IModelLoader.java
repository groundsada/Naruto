package net.sourceforge.nrl.parser.model.loader;

import java.io.File;
import java.net.URI;

import net.sourceforge.nrl.parser.model.IPackage;

/**
 * {@link URI}-based loader for NRL models. Pass instances to the NRLParser to resolve referenced
 * models.
 * 
 * @since 1.4.9
 */
public interface IModelLoader {

	/**
	 * Loads any supported model format, provided the targeted file has an appropriate extension.
	 * URI arguments may use the <code>classpath</code> or <code>file</code> schemes.
	 * 
	 * When invoked with equivalent {@link URI}s the loader will return the same {@link IPackage}.
	 * 
	 * @param uri the URI identifying the model location
	 * @return An {@link IPackage} representing the XSD or UML model referenced by
	 * <code>modelURI</code>, never null
	 * @throws ModelLoadingException If an error is encountered loading the model
	 */
	public IPackage loadModel(URI uri) throws ModelLoadingException;

	/**
	 * Loads any supported model format, provided the targeted file has an appropriate extension.
	 * URI arguments may use the <code>classpath</code> or <code>file</code> schemes.
	 * 
	 * When invoked with equivalent {@link URI}s the loader will return the same {@link IPackage}.
	 * 
	 * @param baseURI An absolute URI used to resolve <code>modelURI</code> if it is relative.
	 * @param modelURI An absolute or relative (to <code>baseURI</code>) URI referencing a model
	 * file.
	 * @return An {@link IPackage} representing the XSD or UML model referenced by
	 * <code>modelURI</code>, never null
	 * @throws ModelLoadingException If an error is encountered loading the model
	 */
	public IPackage loadModel(URI baseURI, URI modelURI) throws ModelLoadingException;

	/**
	 * Convenience method that converts <code>baseFile</code> to a {@link URI}. See
	 * {@link #loadModel(URI, URI)}.
	 */
	public IPackage loadModel(File baseFile, URI modelURI) throws ModelLoadingException;

	/**
	 * Convenience method that converts <code>modelURI</code> to a {@link URI} taking care to escape
	 * illegal characters. See {@link #loadModel(URI, URI)}.
	 */
	public IPackage loadModel(URI baseURI, String modelURI) throws ModelLoadingException;

	/**
	 * Convenience method that converts <code>baseFile</code> to a {@link URI} and
	 * <code>modelURI</code> to a {@link URI} taking care to escape illegal characters. See
	 * {@link #loadModel(URI, URI)}.
	 */
	public IPackage loadModel(File baseFile, String modelURI) throws ModelLoadingException;
}
