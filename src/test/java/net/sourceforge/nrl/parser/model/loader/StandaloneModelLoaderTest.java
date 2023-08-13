package net.sourceforge.nrl.parser.model.loader;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import net.sourceforge.nrl.parser.model.IPackage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class StandaloneModelLoaderTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public static File TEST_DIR = new File(".");

	private AbstractModelLoader modelLoader;

	@BeforeClass
	public static void setupLocations() {
		assertTrue(
				"Cannot find the src/test/resources directory. Is this running in the right place?",
				new File(TEST_DIR, "src/test/resources").exists());
	}

	@Before
	public void setup() {
		modelLoader = new StandaloneModelLoader();
	}

	@Test
	public void unknownBaseURIProtocolCausesError() throws ModelLoadingException,
			URISyntaxException {
		exception.expect(ModelLoadingException.class);
		modelLoader.loadModel(new URI("http://foo"), new URI("bar.xsd"));
	}

	@Test
	public void loadNonExistentXSDCausesError() throws Exception {
		File baseFile = new File(TEST_DIR.getAbsolutePath(),
				"src/test/resources/schema/rulefile.nrl");

		exception.expect(ModelLoadingException.class);
		modelLoader.loadModel(getAbsoluteURIForFile(baseFile), new URI("notsimpletypes.xsd"));
	}

	@Test
	public void loadXSDUsingRelativeFileReference() throws Exception {
		File baseFile = new File(TEST_DIR.getAbsolutePath(),
				"src/test/resources/schema/rulefile.nrl");
		IPackage model = modelLoader.loadModel(getAbsoluteURIForFile(baseFile), new URI(
				"simpletypes.xsd"));
		assertNotNull(model);
	}

	@Test
	public void loadXSDUsingRelativeFileReferenceIncludingParentReferences() throws Exception {
		File baseFile = new File(TEST_DIR.getAbsolutePath(),
				"src/test/resources/schema/rulefile.nrl");
		IPackage model = modelLoader.loadModel(getAbsoluteURIForFile(baseFile), new URI(
				"../../resources/schema/simpletypes.xsd"));
		assertNotNull(model);
	}

	@Test
	public void loadXSDWithEscapedSpacesInModelURI() throws Exception {
		File baseFile = new File(TEST_DIR.getAbsolutePath(),
				"src/test/resources/schema/rulefile.nrl");
		IPackage model = modelLoader.loadModel(getAbsoluteURIForFile(baseFile), new URI(
				"global%20elements.xsd"));
		assertNotNull(model);
	}

	@Test
	public void loadXSDWithUnescapedSpacesInModelURIStringWithBaseURI() throws Exception {
		File baseFile = new File(TEST_DIR.getAbsolutePath(),
				"src/test/resources/schema/rulefile.nrl");
		IPackage model = modelLoader.loadModel(getAbsoluteURIForFile(baseFile),
				"global elements.xsd");
		assertNotNull(model);
	}

	@Test
	public void loadXSDWithUnescapedSpacesInModelURIStringWithBaseFile() throws Exception {
		File baseFile = new File(TEST_DIR.getAbsolutePath(),
				"src/test/resources/schema/rulefile.nrl");
		IPackage model = modelLoader.loadModel(baseFile, "global elements.xsd");
		assertNotNull(model);
	}

	@Test
	public void loadUMLModel() throws Exception {
		File baseFile = new File(TEST_DIR.getAbsolutePath(), "src/test/resources/uml/rulefile.nrl");
		IPackage model = modelLoader.loadModel(getAbsoluteURIForFile(baseFile), "basicmodel.uml");
		assertNotNull(model);
	}

	@Test
	public void loadEMXModel() throws Exception {
		File baseFile = new File(TEST_DIR.getAbsolutePath(), "src/test/resources/uml/rulefile.nrl");
		IPackage model = modelLoader.loadModel(getAbsoluteURIForFile(baseFile), "trades.emx");
		assertNotNull(model);
	}

	@Test
	public void loadUML2Model() throws Exception {
		File baseFile = new File(TEST_DIR.getAbsolutePath(), "src/test/resources/uml/rulefile.nrl");
		IPackage model = modelLoader.loadModel(getAbsoluteURIForFile(baseFile), "basicmodel.uml2");
		assertNotNull(model);
	}

	@Test
	public void loadUMLModelWithUnescapedSpacesInModelURIString() throws Exception {
		File baseFile = new File(TEST_DIR.getAbsolutePath(), "src/test/resources/uml/rulefile.nrl");
		IPackage model = modelLoader.loadModel(getAbsoluteURIForFile(baseFile), "basic model.uml");
		assertNotNull(model);
	}

	@Test
	public void loadUMLWithEscapedSpacesInModelURI() throws Exception {
		File baseFile = new File(TEST_DIR.getAbsolutePath(), "src/test/resources/uml/rulefile.nrl");
		IPackage model = modelLoader.loadModel(getAbsoluteURIForFile(baseFile), new URI(
				"basic%20model.uml"));
		assertNotNull(model);
	}

	@Test
	public void loadXSDFromClasspath() throws Exception {
		File baseFile = new File(TEST_DIR.getAbsolutePath(),
				"src/test/resources/schema/rulefile.nrl");
		IPackage model = modelLoader.loadModel(getAbsoluteURIForFile(baseFile), new URI(
				"classpath:/xsd/duplicate-elements.xsd"));
		assertNotNull(model);
		assertNotNull(model.getElementByName("Dave", true));
	}

	@Test
	public void loadXSDWithIncludesFromFileSystem() throws Exception {
		File baseFile = new File(TEST_DIR.getAbsolutePath(), "src/test/resources/rulefile.nrl");
		IPackage model = modelLoader.loadModel(getAbsoluteURIForFile(baseFile), new URI(
				"xsd/duplicate-elements.xsd"));
		assertNotNull(model);
		assertNotNull(model.getElementByName("Dave", true));
	}

	@Test
	public void multipleLoadsOfSameModelReturnSameCachedObject() throws Exception {
		File baseFile = new File(TEST_DIR.getAbsolutePath(),
				"src/test/resources/schema/rulefile.nrl");
		IPackage model1 = modelLoader.loadModel(getAbsoluteURIForFile(baseFile), new URI(
				"classpath:/xsd/duplicate-elements.xsd"));
		assertNotNull(model1);

		IPackage model2 = modelLoader.loadModel(getAbsoluteURIForFile(baseFile), new URI(
				"classpath:/xsd/duplicate-elements.xsd"));
		assertNotNull(model2);

		assertEquals(model1, model2);
	}

	@Test
	public void loadingModelFromEquivalentUriReturnsCachedVersion() throws Exception {
		File baseFile1 = new File(TEST_DIR.getAbsolutePath(),
				"src/test/resources/schema/rulefile.nrl");

		IPackage model1 = modelLoader.loadModel(getAbsoluteURIForFile(baseFile1),
				"../uml/basicmodel.uml");
		assertNotNull(model1);

		File baseFile2 = new File(TEST_DIR.getAbsolutePath(), "src/test/resources/uml/rulefile.nrl");
		IPackage model2 = modelLoader.loadModel(getAbsoluteURIForFile(baseFile2), "basicmodel.uml");
		assertNotNull(model2);

		assertEquals(model1, model2);
	}

	private URI getAbsoluteURIForFile(File baseFile) throws URISyntaxException {
		return new URI(String.format("file:/%s", baseFile.getAbsolutePath().replace('\\', '/')));
	}

	@Test
	public void loadXSDUsingBackslashRelativePath() throws Exception {
		File baseFile = new File(TEST_DIR.getAbsolutePath(),
				"src/test/resources/schema/rulefile.nrl");
		IPackage model = modelLoader.loadModel(getAbsoluteURIForFile(baseFile), new URI(
				org.eclipse.emf.common.util.URI.encodeQuery("..\\..\\resources\\schema\\simpletypes.xsd", false)));
		assertNotNull(model);
		assertEquals("simpletypes", model.getName());
	}

	@Test
	public void loadUMLUsingBackslashRelativePath() throws Exception {
		File baseFile = new File(TEST_DIR.getAbsolutePath(),
				"src/test/resources/uml/rulefile.nrl");
		IPackage model = modelLoader.loadModel(getAbsoluteURIForFile(baseFile), new URI(
				org.eclipse.emf.common.util.URI.encodeQuery("..\\..\\resources\\uml\\basicmodel.uml", false)));
		assertNotNull(model);
		assertEquals("BasicModel", model.getName());
	}

}
