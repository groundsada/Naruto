package net.sourceforge.nrl.parser.resolver;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

public class FileAndClassPathURIResolverTest {

	private FileAndClasspathURIResolver resolver;

	@Before
	public void before() {
		resolver = new FileAndClasspathURIResolver(FileAndClasspathURIResolver.class
				.getClassLoader());
	}

	@Test(expected = ResolverException.class)
	public void assertAbsoluteFileURIOrClasspathNoRelativeAllowed() throws URISyntaxException,
			ResolverException {
		resolver.assertAbsoluteFileURIOrClasspath(new URI("file.txt"));
	}
	
	@Test(expected = ResolverException.class)
	public void assertAbsoluteFileURIOrClasspathNoRelativeFileAllowed() throws URISyntaxException,
			ResolverException {
		resolver.assertAbsoluteFileURIOrClasspath(new URI("file:file.txt"));
	}

	@Test(expected = ResolverException.class)
	public void assertAbsoluteFileURIOrClasspathIllegalProtocol() throws URISyntaxException,
			ResolverException {
		resolver.assertAbsoluteFileURIOrClasspath(new URI("http://file.txt"));
	}

	public void assertAbsoluteFileURIOrClasspathSingleSlashOk() throws URISyntaxException,
			ResolverException {
		resolver.assertAbsoluteFileURIOrClasspath(new URI("file:/file.txt"));
	}

	@Test
	public void assertAbsoluteFileURIOrClasspathDoubleSlashOk() throws URISyntaxException,
			ResolverException {
		resolver.assertAbsoluteFileURIOrClasspath(new URI("file://file.txt"));
	}

	@Test
	public void assertAbsoluteFileURIOrClasspathTripleSlashOk() throws URISyntaxException,
			ResolverException {
		resolver.assertAbsoluteFileURIOrClasspath(new URI("file:///file.txt"));
	}

	@Test
	public void assertAbsoluteFileURIOrClasspathDriveLetterOk() throws URISyntaxException,
			ResolverException {
		resolver.assertAbsoluteFileURIOrClasspath(new URI("file:/C:/file.txt"));
	}

	@Test
	public void isURIResolvableAbsoluteClasspathURI() throws URISyntaxException {
		assertTrue(resolver.isURIResolvable(new URI("classpath:/nrlparser.xsd")));
	}
	
	@Test
	public void isURIResolvableUnResolvableAbsoluteClasspathURI() throws URISyntaxException {
		assertFalse(resolver.isURIResolvable(new URI("classpath:/i/do/not/really/exist.xsd")));
	}
	
	@Test
	public void openStreamFromClasspathBaseResourceAndAbsoluteFileURI() throws ResolverException,
			URISyntaxException, IOException {
		InputStream stream = resolver.openStream(new URI("classpath:/nrlparser.xsd"), new File(
				"pom.xml").getAbsoluteFile().toURI());
		assertNotNull("Stream not opened", stream);
		stream.close();
	}

	@Test
	public void isURIResolvableRelativeURI() throws URISyntaxException {
		assertTrue(resolver.isURIResolvable(new URI("classpath:/nrlparser.xsd"), new URI(
		"operators-1.5.xsd")));
	}
	
	@Test
	public void isURIResolvableUnresolveableRelativeURI() throws URISyntaxException {
		assertFalse(resolver.isURIResolvable(new URI("classpath:/nrlparser.xsd"), new URI(
		"../../../../../../../../operators-1.5.xsd")));
	}
	
	@Test
	public void openStreamFromClasspathBaseResourceAndRelativeURI() throws ResolverException,
			URISyntaxException, IOException {
		InputStream stream = resolver.openStream(new URI("classpath:/nrlparser.xsd"), new URI(
				"operators-1.5.xsd"));
		assertNotNull("Stream not opened", stream);
		stream.close();
	}

	@Test
	public void openStreamFromClasspathBaseResourceAndRelativeString() throws ResolverException,
			URISyntaxException, IOException {
		InputStream stream = resolver.openStream(new URI("classpath:/nrlparser.xsd"),
				"operators-1.5.xsd");
		assertNotNull("Stream not opened", stream);
		stream.close();
	}
	
	@Test
	public void isURIResolvableRelativeURIAsString() throws URISyntaxException {
		assertTrue(resolver.isURIResolvable(new URI("classpath:/nrlparser.xsd"), 
		"operators-1.5.xsd"));
	}
	
	@Test
	public void isURIResolvableUnResolvableRelativeURIAsString() throws URISyntaxException {
		assertFalse(resolver.isURIResolvable(new URI("classpath:/nrlparser.xsd"), 
		"../../../../.././../../../../operators-1.5.xsd"));
	}

	@Test(expected = ResolverException.class)
	public void openStreamFromClasspathBaseResourceAndRelativeStringInvalidResource()
			throws ResolverException, URISyntaxException {
		resolver.openStream(new URI("classpath:/nrlparser.xsd"), "foobar.txt");
	}

	@Test(expected = ResolverException.class)
	public void openStreamFromClasspathResourceDoesNotExist() throws ResolverException,
			URISyntaxException {
		resolver.openStream(new URI("classpath:foobar.txt"));
	}

	@Test
	public void openStreamFromClasspathNoLeadingSlash() throws ResolverException, IOException,
			URISyntaxException {
		InputStream stream = resolver.openStream(new URI("classpath:nrlparser.xsd"));
		assertNotNull("Stream not opened", stream);
		stream.close();
	}

	@Test
	public void openStreamFromClasspathWithLeadingSlash() throws ResolverException, IOException,
			URISyntaxException {
		InputStream stream = resolver.openStream(new URI("classpath:/nrlparser.xsd"));
		assertNotNull("Stream not opened", stream);
		stream.close();
	}

	@Test
	public void openStreamFromFile() throws ResolverException, IOException {
		InputStream stream = resolver.openStream(new File("pom.xml").getAbsoluteFile().toURI());
		assertNotNull("Stream not opened", stream);
		stream.close();
	}

	@Test
	public void openStreamFromBaseFileAndClasspathURI() throws ResolverException, IOException,
			URISyntaxException {
		InputStream stream = resolver.openStream(new File("pom.xml").getAbsoluteFile().toURI(),
				new URI("classpath:/nrlparser.xsd"));
		assertNotNull("Stream not opened", stream);
		stream.close();
	}

	@Test
	public void openStreamFromBaseFileAndRelativeURL() throws ResolverException, IOException,
			URISyntaxException {
		InputStream stream = resolver.openStream(new File("pom.xml").getAbsoluteFile().toURI(),
				new URI("src/test/resources/operators/operators-1.5.xml"));
		assertNotNull("Stream not opened", stream);
		stream.close();
	}

	@Test
	public void openStreamFromBaseFileAndRelativeURLString() throws ResolverException, IOException,
			URISyntaxException {
		InputStream stream = resolver.openStream(new File("pom.xml").getAbsoluteFile().toURI(),
				"src/test/resources/operators/operators-1.5.xml");
		assertNotNull("Stream not opened", stream);
		stream.close();
	}

	@Test(expected = ResolverException.class)
	public void openStreamFromFileResourceDoesNotExist() throws ResolverException, IOException {
		resolver.openStream(new File("foobar.txt").getAbsoluteFile().toURI());
	}
	
	@Test
	public void openStreamFromBaseURIAndResourceStringWithUnescapedCharacters()
			throws ResolverException{
		URI baseURI = new File("src/test/resources/schema/rulefile.nrl").getAbsoluteFile().toURI();
		
		InputStream stream;
		stream = resolver.openStream(baseURI, "global elements.xsd");
		assertNotNull(stream);
	}
	
	@Test
	public void openStreamFromBaseURIAndResourceStringWithBackSlashes()
			throws ResolverException{
		URI baseURI = new File("src/test/resources/schema/rulefile.nrl").getAbsoluteFile().toURI();
		
		InputStream stream;
		stream = resolver.openStream(baseURI, "..\\..\\resources\\schema\\groups.xsd");
		assertNotNull(stream);
	}

	@Test
	public void openStreamFromBaseURIAndRelativeURIWithEscapedSlashes()
			throws ResolverException{
		URI baseURI = new File("src/test/resources/schema/rulefile.nrl").getAbsoluteFile().toURI();
		
		InputStream stream;
		stream = resolver.openStream(baseURI, escapedURI("..\\..\\resources\\schema\\groups.xsd"));
		assertNotNull(stream);
	}

	private URI escapedURI(String unescapedURI) {
		try {
			return new URI(org.eclipse.emf.common.util.URI.encodeQuery(unescapedURI, true));
		} catch (URISyntaxException e) {
			return null;
		}
	}

	
}
