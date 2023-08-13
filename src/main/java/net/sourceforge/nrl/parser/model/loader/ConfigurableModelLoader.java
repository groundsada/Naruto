package net.sourceforge.nrl.parser.model.loader;

/**
 * An extension of <code>StandaloneModelLoader</code> which allows custom resources to be loaded.  
 */
public class ConfigurableModelLoader extends StandaloneModelLoader {
		
	public ConfigurableModelLoader() {
		this(ConfigurableModelLoader.class.getClassLoader());
	}
	
	public ConfigurableModelLoader(ClassLoader loader) {
		super(loader);
	}
	
	/**
	 * Adds a URI map entry to the EMF resource URI map.
	 */
	public void addResourceMapEntry(org.eclipse.emf.common.util.URI key, org.eclipse.emf.common.util.URI value) {
		super.getCustomURIMap().put(key, value);
	}
	
}
