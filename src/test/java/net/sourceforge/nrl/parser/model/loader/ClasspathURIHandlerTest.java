package net.sourceforge.nrl.parser.model.loader;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.emf.common.util.URI;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ClasspathURIHandlerTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private ClasspathURIHandler handler = new ClasspathURIHandler();
	private InputStream is;

	@Before
	public void nullifyStreamVariable() {
		is = null;
	}

	@After
	public void closeStream() throws IOException {
		if (is != null) {
			is.close();
		}
	}

	@Test
	public void ioExceptionThrownWhenNoLeadingSlashResourceDoesNotExist() throws Exception {
		exception.expect(IOException.class);
		handler.createInputStream(URI.createURI("classpath:dave"), null);
	}

	@Test
	public void ioExceptionThrownWhenLeadingSlashResourceDoesNotExist() throws Exception {
		exception.expect(IOException.class);
		is = handler.createInputStream(URI.createURI("classpath:/dave"), null);
	}

	@Test
	public void loadResourceWithNoLeadingSlash() throws Exception {
		handler.createInputStream(URI.createURI("classpath:xsd/duplicate-elements.xsd"), null);

	}

	@Test
	public void loadResourceWithLeadingSlash() throws Exception {
		handler.createInputStream(URI.createURI("classpath:/xsd/duplicate-elements.xsd"), null);
	}

}
