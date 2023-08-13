package net.sourceforge.nrl.parser.util;

import junit.framework.TestCase;

public class StringUtilitiesTest extends TestCase {

	public void testGetCharacterPosition() {
		// Beginning
		assertEquals(0, StringUtilities.getCharacterPosition("", 1, 0));
		assertEquals(0, StringUtilities.getCharacterPosition("abc", 1, 0));
		assertEquals(0, StringUtilities.getCharacterPosition("abc", -1, 0));
		assertEquals(0, StringUtilities.getCharacterPosition("abc", 1, -1));

		// Middle
		assertEquals(2, StringUtilities.getCharacterPosition("abc\ncde", 1, 2));
		assertEquals(6, StringUtilities.getCharacterPosition("abc\ncde", 2, 2));
		assertEquals(6, StringUtilities.getCharacterPosition("abc\ncde\n", 2, 2));

		// End or beyond
		assertEquals(2, StringUtilities.getCharacterPosition("abc", 3, 0));
		assertEquals(4, StringUtilities.getCharacterPosition("abc\na", 2, 3));
		assertEquals(3, StringUtilities.getCharacterPosition("abc\n", 2, 0));
	}
}