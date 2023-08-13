package net.sourceforge.nrl.parser.type;

import java.io.IOException;
import java.io.InputStream;

/**
 * Load the default type mapping, which is shipped in the NRL parser JAR.
 * 
 * @since 1.4.12
 */
public class DefaultTypeMappingFactory {

	private static XmlTypeMapping typeMapping;

	/**
	 * Return the default type mapping.
	 * 
	 * @return the type mapping
	 * @throws RuntimeException if the type mapping could not be loaded. This would
	 * usually indicate some sort of internal error like a classpath problem.
	 */
	public static synchronized ITypeMapping getDefaultTypeMapping() {
		if (typeMapping != null) {
			return typeMapping;
		}

		InputStream stream = DefaultTypeMappingFactory.class.getClassLoader().getResourceAsStream(
				"default-type-mapping.xml");
		if (stream == null) {
			throw new RuntimeException(
					"default-type-mapping.xml could not be found on the classpath.");
		}

		typeMapping = new XmlTypeMapping();
		try {
			typeMapping.load(stream);
		} catch (Exception e) {
			throw new RuntimeException("An error occurred while loading default-type-mapping.xml",
					e);
		}

		try {
			stream.close();
		} catch (IOException e) {
		}

		return typeMapping;
	}
}
