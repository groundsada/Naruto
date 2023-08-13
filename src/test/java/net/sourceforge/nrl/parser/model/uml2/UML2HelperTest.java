package net.sourceforge.nrl.parser.model.uml2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UML2HelperTest {

	@Test
	public void cleanUpNamePreservesLegalNRLIdentifiers() throws Exception {
		assertEquals("validName", UML2Helper.cleanUpName("validName"));
		assertEquals("valid_name", UML2Helper.cleanUpName("valid_name"));
		assertEquals("valid", UML2Helper.cleanUpName("valid"));
		assertEquals("valid1name", UML2Helper.cleanUpName("valid1name"));
	}

	@Test
	public void cleanUpNameStripsDotsAndCapitalisesNextCharacter() {
		assertEquals("cleanUpName", UML2Helper.cleanUpName("clean.upName"));
		assertEquals("cleanUpName", UML2Helper.cleanUpName("clean.upName."));
		assertEquals("cleanUpName", UML2Helper.cleanUpName(".clean.upName"));
		assertEquals("a", UML2Helper.cleanUpName(".a"));
	}

	@Test
	public void cleanUpNameReplacesSpacesWithUnderscores() {
		assertEquals("clean_upName", UML2Helper.cleanUpName("clean upName"));
		assertEquals("clean_upName_", UML2Helper.cleanUpName("clean upName "));
	}
}
