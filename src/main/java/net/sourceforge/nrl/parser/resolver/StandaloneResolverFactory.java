package net.sourceforge.nrl.parser.resolver;

import net.sourceforge.nrl.parser.model.loader.IModelLoader;
import net.sourceforge.nrl.parser.model.loader.StandaloneModelLoader;

/**
 * A resolver factory that provides model resolution and URI resolution outside eclipse.
 * 
 * @since 1.4.9
 */
public class StandaloneResolverFactory implements IResolverFactory {

	private final ClassLoader classLoader;
	private StandaloneModelLoader modelLoader;

	/**
	 * Inialise a default factory. Note: if you are planning to support classpath URIs,
	 * use {@link #StandaloneResolverFactory(ClassLoader)} instead and think about which
	 * classloader to use.
	 */
	public StandaloneResolverFactory() {
		this(StandaloneResolverFactory.class.getClassLoader());		
	}

	/**
	 * Initialise the resolver factory with the class loader to use to load any resources
	 * from the classpath.
	 * 
	 * @param classLoader the loader
	 */
	public StandaloneResolverFactory(ClassLoader classLoader) {
		this.classLoader = classLoader;
		modelLoader = new StandaloneModelLoader(classLoader);
	}

	public IModelLoader createModelLoader() {		
		return modelLoader;
	}

	public IURIResolver createURIResolver() {
		return new FileAndClasspathURIResolver(classLoader);
	}

}
