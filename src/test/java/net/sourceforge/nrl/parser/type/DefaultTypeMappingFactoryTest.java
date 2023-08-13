package net.sourceforge.nrl.parser.type;

import static org.junit.Assert.assertNotNull;

import net.sourceforge.nrl.parser.ast.NRLDataType;
import net.sourceforge.nrl.parser.model.PrimitiveTypeFactory;

import org.junit.Test;

public class DefaultTypeMappingFactoryTest {

	@Test
	public void getDefaultTypeMapping() {
		ITypeMapping defaultTypeMapping = DefaultTypeMappingFactory.getDefaultTypeMapping();
		assertNotNull(defaultTypeMapping);
		assertNotNull(defaultTypeMapping.getType(PrimitiveTypeFactory.getInstance().getType(
				NRLDataType.STRING)));
	}
}
