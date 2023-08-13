package net.sourceforge.nrl.parser.model.loader;

import java.net.URI;

import org.eclipse.emf.ecore.resource.Resource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AbstractModelLoaderTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private IModelLoader modelLoader;

	@Before
	public void setup() {
		modelLoader = new AbstractModelLoader() {

			@Override
			protected Resource createUMLResource(URI resolvedModelURI) {
				return null;
			}

			@Override
			protected Resource createXSDResource(URI resolvedModelURI) {
				return null;
			}

			@Override
			protected void validateBaseURI(URI baseURI) throws ModelLoadingException {
				
			}

			@Override
			protected void validateModelURI(URI modelURI) throws ModelLoadingException {
				// TODO Auto-generated method stub
				
			}
		};
	}

	@Test
	public void baseURIMustHaveAProtocol() throws Exception {
		exception.expect(ModelLoadingException.class);
		modelLoader.loadModel(new URI("foo"), new URI("bar.xsd"));
	}

	@Test
	public void unknownModelURIProtocolCausesError() throws Exception {
		exception.expect(ModelLoadingException.class);
		modelLoader.loadModel(new URI("file:///foo"), new URI("http://bar.xsd"));
	}

	@Test
	public void baseFileURIMustBeAbsolute() throws Exception {
		exception.expect(ModelLoadingException.class);
		modelLoader.loadModel(new URI("file:foo"), new URI("bar.xsd"));
	}

	@Test
	public void baseClasspathURIMustStartWithSlash() throws Exception {
		exception.expect(ModelLoadingException.class);
		modelLoader.loadModel(new URI("classpath:foo"), new URI("bar.xsd"));
	}

	@Test
	public void modelClasspathURIMustStartWithSlash() throws Exception {
		exception.expect(ModelLoadingException.class);
		modelLoader.loadModel(new URI("classpath:/foo"), new URI("classpath:bar.xsd"));
	}

	@Test
	public void modelURIMustHaveAFileExtension() throws Exception {
		exception.expect(ModelLoadingException.class);
		modelLoader.loadModel(new URI("file:/foo"), new URI("bar"));
	}

	@Test
	public void modelURIMustNotEndWithADot() throws Exception {
		exception.expect(ModelLoadingException.class);
		modelLoader.loadModel(new URI("file:/foo"), new URI("bar."));
	}

	@Test
	public void unknownModelFileExtensionCausesError() throws Exception {
		exception.expect(ModelLoadingException.class);
		modelLoader.loadModel(new URI("file:/foo"), new URI("bar.baz"));
	}

}
