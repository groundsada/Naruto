package net.sourceforge.nrl.parser.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Map;

import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.model.uml2.UML2ModelLoader;
import net.sourceforge.nrl.parser.model.xsd.XSDClassifier;
import net.sourceforge.nrl.parser.model.xsd.XSDDataType;
import net.sourceforge.nrl.parser.model.xsd.XSDModelLoader;
import net.sourceforge.nrl.parser.model.xsd.XSDPackage;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;
import org.junit.Before;
import org.junit.Test;

public class ModelUtilsTest extends NRLParserTestSupport {
	private static boolean isInitialised = false;

	private static IPackage xsdModel;

	private static IPackage umlModel;

	@SuppressWarnings("deprecation")
	@Before
	public void setUp() throws Exception {
		if (!isInitialised) {
			Map<String, Object> extensionToFactoryMap = Resource.Factory.Registry.INSTANCE
					.getExtensionToFactoryMap();
			extensionToFactoryMap.put("xsd", new XSDResourceFactoryImpl());
			isInitialised = true;
		}
		XSDModelLoader xsdLoader = new XSDModelLoader();
		xsdModel = xsdLoader.load(new File("src/test/resources/schema/xsdModelUtilsSchema.xsd"));
		UML2ModelLoader umlLoader = new UML2ModelLoader();
		File umlFile = new File("src/test/resources/uml/basicmodel2.uml");
		umlModel = umlLoader.load(getResourceForFile(umlFile));
	}

	@Test
	public void testGetAtributeByNameForElementWithNonModelFriendlyName() throws Exception {
		IClassifier typeOne = (IClassifier) xsdModel.getElementByName("TypeOne", true);

		IAttribute match = ModelUtils.getAttributeByOriginalName(typeOne, "element.One", true);
		assertEquals("element.One", match.getOriginalName());
	}

	@Test
	public void testGetAttributeByNameForElementWithModelFriendlyName() throws Exception {
		IClassifier typeOne = (IClassifier) xsdModel.getElementByName("TypeOne", true);

		IAttribute match = ModelUtils.getAttributeByOriginalName(typeOne, "elementTwo", true);
		assertEquals("elementTwo", match.getOriginalName());
	}

	@Test
	public void testGetAttributeByNameForAttributeWithModelFriendlyName() throws Exception {
		IClassifier typeOne = (IClassifier) xsdModel.getElementByName("TypeOne", true);

		IAttribute match = ModelUtils.getAttributeByOriginalName(typeOne, "attributeOne", true);
		assertEquals("attributeOne", match.getOriginalName());
	}

	@Test
	public void testGetAttributeByNameForAttributeWithNonModelFriendlyName() throws Exception {
		IClassifier typeOne = (IClassifier) xsdModel.getElementByName("TypeOne", true);

		IAttribute match = ModelUtils.getAttributeByOriginalName(typeOne, "attribute.Two", true);
		assertEquals("attribute.Two", match.getOriginalName());
	}

	@Test
	public void testGetAttributeByNameReturnsNullForNonExistentAttribute() throws Exception {
		IClassifier typeOne = (IClassifier) xsdModel.getElementByName("TypeOne", true);

		IAttribute match = ModelUtils.getAttributeByOriginalName(typeOne, "meh", true);
		assertNull(match);
	}

	@Test
	public void testIsXsdModelElementNullThrowsException() {
		try {
			ModelUtils.isXSDModelElement(null);
			fail("Expected IllegalArgumentException.");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testXSDClassifierIsXsdModelElement() {
		XSDClassifier typeOne = (XSDClassifier) xsdModel.getElementByName("TypeOne", true);
		assertTrue("XSDClassifier should be identified as an XSD model element", ModelUtils
				.isXSDModelElement(typeOne));
	}

	@Test
	public void testXSDPackageIsXsdModelElement() {
		assertTrue("XSDPackage should be identified as an XSD model element", ModelUtils
				.isXSDModelElement((XSDPackage) xsdModel));
	}

	@Test
	public void testXSDDataTypeIsXsdModelElement() {
		XSDDataType string = (XSDDataType) xsdModel.getElementByName("string", true);
		assertTrue("XSDDataType should be identified as an XSD model element", ModelUtils
				.isXSDModelElement(string));
	}

	@Test
	public void testUmlClassifierIsNotXsdModelElement() {
		IModelElement trade = umlModel.getElementByName("Trade", true);
		assertFalse("UML type should not be identified as an XSD model element", ModelUtils
				.isXSDModelElement(trade));
	}

	@Test
	public void testUmlPackageIsNotModelElement() {
		assertFalse("UML package should not be identified as an XSD model element", ModelUtils
				.isXSDModelElement(umlModel));
	}

	@Test
	public void testIsAttributeAnXMLAttribute() {
		IClassifier trade = (IClassifier) umlModel.getElementByName("Trade", true);
		XSDClassifier typeOne = (XSDClassifier) xsdModel.getElementByName("TypeOne", true);
		assertFalse("UML attribute should not be an XML attribute", ModelUtils
				.isAttributeAnXMLAttribute(trade.getAttributeByName("id", true)));
		assertTrue("XML attribute not detected", ModelUtils.isAttributeAnXMLAttribute(typeOne
				.getAttributeByName("attributeOne", false)));
		assertFalse("XML element misclassified", ModelUtils.isAttributeAnXMLAttribute(typeOne
				.getAttributeByName("elementTwo", false)));
	}

	@Test
	public void testIsAttributeAnXMLElement() {
		IClassifier trade = (IClassifier) umlModel.getElementByName("Trade", true);
		XSDClassifier typeOne = (XSDClassifier) xsdModel.getElementByName("TypeOne", true);
		assertFalse("UML attribute should not be an XML element", ModelUtils
				.isAttributeAnXMLElement(trade.getAttributeByName("id", true)));
		assertFalse("XML attribute should not be an XML element", ModelUtils
				.isAttributeAnXMLElement(typeOne.getAttributeByName("attributeOne", false)));
		assertTrue("XML element not detected", ModelUtils.isAttributeAnXMLElement(typeOne
				.getAttributeByName("elementTwo", false)));
	}

	@Test
	public void testGetXMLNamespaceURI() {
		IClassifier trade = (IClassifier) umlModel.getElementByName("Trade", true);
		XSDClassifier typeOne = (XSDClassifier) xsdModel.getElementByName("TypeOne", true);
		assertNull("UML attribute should not have a namespace", ModelUtils.getXMLNamespaceURI(trade
				.getAttributeByName("id", false)));
		assertEquals("Wrong namespace", "http://www.modeltwozero.com/test/a", ModelUtils
				.getXMLNamespaceURI(xsdModel));
		assertEquals("Wrong namespace", "http://www.modeltwozero.com/test/a", ModelUtils
				.getXMLNamespaceURI(typeOne.getAttributeByName("elementTwo", false)));
		assertEquals("Wrong namespace", "http://www.modeltwozero.com/test/a", ModelUtils
				.getXMLNamespaceURI(typeOne));
	}
}
