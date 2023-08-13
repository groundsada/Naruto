package net.sourceforge.nrl.parser.resolver;

import net.sourceforge.nrl.parser.model.loader.IModelLoader;

/**
 * A factory responsible for creating model and generic URI resolvers. Behaviour differs
 * when loading files standalone or inside eclipse, so different implementations are
 * necessary.
 * <p>
 * Use {@link StandaloneResolverFactory} for a simple implementation that works outside
 * eclipse.
 * 
 * @since 1.4.9
 */
public interface IResolverFactory {

	/**
	 * Create a model loader that implement's the factory's resolver strategy. Calling
	 * classes should try to reuse the model loader after calling this, as it may be
	 * caching models.
	 * 
	 * @return the loader
	 */
	public IModelLoader createModelLoader();

	/**
	 * Create a generic URI resolver for loading resources from URIs. Do not use this to
	 * load models, the {@link #createModelLoader()} method provides additional
	 * configuration functionality for models.
	 * 
	 * @return the resolver
	 */
	public IURIResolver createURIResolver();
}
