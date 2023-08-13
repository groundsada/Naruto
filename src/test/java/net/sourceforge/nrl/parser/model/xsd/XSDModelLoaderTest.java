package net.sourceforge.nrl.parser.model.xsd;

import java.io.File;

import junit.framework.TestCase;
import net.sourceforge.nrl.parser.model.IClassifier;
import net.sourceforge.nrl.parser.model.IPackage;
import net.sourceforge.nrl.parser.model.ModelUtils;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;

/*
 * Additional end-to-end tests that are not covered by the other tests in this package.
 */
public class XSDModelLoaderTest extends TestCase {

	private static boolean isInitialised = false;

	public void setUp() throws Exception {
		if (!isInitialised) {
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd",
					new XSDResourceFactoryImpl());
			isInitialised = true;
		}
	}

	@SuppressWarnings("deprecation")
	public void testLoadModelWithSpaces() throws Exception {
		XSDModelLoader loader = new XSDModelLoader();
		IPackage model = loader.load(new File("src/test/resources/schema/global elements.xsd"));
		assertEquals("Wrong package name", "globalelements", model.getName());
		assertNotNull("Wrong sub-package name", model.getElementByName("globalinclude", false));
		assertNotNull(ModelUtils.getLocation(model));
	}

	@SuppressWarnings("deprecation")
	public void testElementsOfImportedSchemaAreLoadedIfThereIsAReferenceInParent() throws Exception {
		XSDModelLoader loader = new XSDModelLoader();
		IPackage model = loader.load(new File("src/test/resources/schema/global elements.xsd"));
		assertNotNull("Imported elements not present.", model
				.getElementByName("globalimport", true));
	}

	@SuppressWarnings("deprecation")
	public void testElementsOfIncludedSchemaAreLoaded() throws Exception {
		XSDModelLoader loader = new XSDModelLoader();
		IPackage model = loader.load(new File("src/test/resources/schema/global elements.xsd"));
		assertNotNull("Included elements not present.", model.getElementByName("included", true));
	}

	@SuppressWarnings("deprecation")
	public void testOverloadedTypes() throws Exception {
		XSDModelLoader loader = new XSDModelLoader();
		loader.load(new File(
				"src/test/resources/schema/multipleRootElementsWithOverloadedTypeNames.xsd"));
	}

	@SuppressWarnings("deprecation")
	public void testTypesWithDots() throws Exception {
		XSDModelLoader loader = new XSDModelLoader();
		IPackage model = loader
				.load(new File("src/test/resources/schema/typesWithDotsInNames.xsd"));

		IClassifier basic = (IClassifier) model.getElementByName("Basic", true);
		assertNotNull(basic.getAttributeByName("mySimple", false));

		IClassifier simpleWithDots = (IClassifier) model.getElementByName("MySimpleType", true);
		assertEquals("My.Simple.Type", simpleWithDots.getOriginalName());

		IClassifier complexWithDots = (IClassifier) model.getElementByName("MyComplexType", true);
		assertEquals("My.ComplexType", complexWithDots.getOriginalName());

		IClassifier complexExtended = (IClassifier) model.getElementByName("MyExtendedType", true);
		assertEquals("My.ExtendedType", complexExtended.getOriginalName());
		assertEquals("My.ComplexType", complexExtended.getParent().getOriginalName());
	}

	@SuppressWarnings("deprecation")
	public void testRedefinedType() throws Exception {
		XSDModelLoader loader = new XSDModelLoader();
		try {
			loader.load(new File("src/test/resources/schema/redefine.xsd"));
			fail("Expected UnprocessableSchemaException to be thrown.");
		} catch (UnprocessableSchemaException e) {
		} catch (Exception e) {
			fail("Expected UnprocessableSchemaException to be thrown.");
		}

	}
}
