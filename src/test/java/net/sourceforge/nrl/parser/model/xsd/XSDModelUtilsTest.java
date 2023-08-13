package net.sourceforge.nrl.parser.model.xsd;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;
import net.sourceforge.nrl.parser.model.IAttribute;
import net.sourceforge.nrl.parser.model.IClassifier;
import net.sourceforge.nrl.parser.model.IPackage;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;

public class XSDModelUtilsTest extends TestCase {

	private static boolean isInitialised = false;
	private static IPackage model;

	@SuppressWarnings("deprecation")
	public void setUp() throws Exception {
		if (!isInitialised) {
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd",
					new XSDResourceFactoryImpl());
			isInitialised = true;
		}
		XSDModelLoader loader = new XSDModelLoader();
		model = loader.load(new File("src/test/resources/schema/xsdModelUtilsSchema.xsd"));
	}

	public void testGetXsdAttributesReturnsOnlyAttributesTaggedAsXsdAttributes() throws Exception {
		IClassifier typeOne = (IClassifier) model.getElementByName("TypeOne", true);
		List<IAttribute> matches = XSDModelUtils.getXsdAttributes(typeOne, true);
		assertEquals(4, matches.size());
		for (IAttribute attribute : matches) {
			assertEquals(IXSDUserData.XSD_ATTRIBUTE_KIND, attribute
					.getUserData(IXSDUserData.ATTRIBUTE_KIND));
		}
	}

	public void testGetXsdElementsReturnsOnlyAttributesTaggedAsXsdElements() throws Exception {
		IClassifier typeOne = (IClassifier) model.getElementByName("TypeOne", true);
		List<IAttribute> matches = XSDModelUtils.getXsdElements(typeOne, true);
		assertEquals(4, matches.size());
		for (IAttribute attribute : matches) {
			assertEquals(IXSDUserData.XSD_ELEMENT_KIND, attribute
					.getUserData(IXSDUserData.ATTRIBUTE_KIND));
		}
	}

}
