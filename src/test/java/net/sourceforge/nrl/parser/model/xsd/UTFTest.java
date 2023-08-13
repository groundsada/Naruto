package net.sourceforge.nrl.parser.model.xsd;

import java.io.File;

import net.sourceforge.nrl.parser.model.IClassifier;
import net.sourceforge.nrl.parser.model.IPackage;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;

import junit.framework.TestCase;

/*
 * Cross-cutting test cases for UTF support.
 */
public class UTFTest extends TestCase {

	private static boolean isInitialised = false;

	private static IPackage model = null;

	@SuppressWarnings("deprecation")
	public void setUp() throws Exception {
		if (!isInitialised) {
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd",
					new XSDResourceFactoryImpl());
			isInitialised = true;

			XSDModelLoader loader = new XSDModelLoader();
			model = loader.load(new File("src/test/resources/schema/utf.xsd"));
		}
	}

	/*
	 * Just a double-check that the model loaded
	 */
	public void testLoading() throws Exception {
		assertNotNull(model);
	}

	public void testAttributeName() {
		IClassifier classifier = (IClassifier) model.getElementByName("Languages", true);
		assertNotNull(classifier.getAttributeByName("çã", false));
		assertNotNull(classifier.getAttributeByName("日本語", false));
	}

	public void testElementName() {
		IClassifier classifier = (IClassifier) model.getElementByName("Languages", true);
		assertNotNull(classifier.getAttributeByName("çãüäö", false));
	}

	public void testComplexTypeName() {
		assertNotNull(model.getElementByName("日本語", true));
		assertNotNull(model.getElementByName("çãüäö2", true));
	}

	public void testAnonymousType() {
		IClassifier classifier = (IClassifier) model.getElementByName("Çã2", true);
		assertNotNull(classifier);

		classifier = (IClassifier) model.getElementByName("日本語2", true);
		assertNotNull(classifier);
	}
}
