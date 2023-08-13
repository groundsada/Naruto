package net.sourceforge.nrl.parser.preprocessing;

import junit.framework.TestCase;

/**
 * Test the preprocessor class.
 * 
 * @author Christian Nentwich
 */
public class ReferencePreprocessorTest extends TestCase {

	public void testProcess() {
		ReferencePreprocessor processor = new ReferencePreprocessor();

		// Empty
		String result = processor.process("");
		assertEquals("", result);

		// Fragment not used
		result = processor.process("Validation Fragment \"this is a test\" ABC");
		assertEquals("Validation Fragment \"this is a test\" ABC", result);

		result = processor.process("Action Fragment \"this is a test\" ABC");
		assertEquals("Action Fragment \"this is a test\" ABC", result);

		// Fragment used
		result = processor
				.process("Validation Fragment \"this is a test\" If this is a test then do that");
		assertEquals("Validation Fragment \"this is a test\" If {this is a test} then do that",
				result);

		result = processor
				.process("Action Fragment \"this is a test\" If this is a test then do that");
		assertEquals("Action Fragment \"this is a test\" If {this is a test} then do that", result);

		// Used twice
		result = processor
				.process("Validation Fragment \"this is a test\" If this is a test then do that or if this is a test");
		assertEquals(
				"Validation Fragment \"this is a test\" If {this is a test} then do that or if {this is a test}",
				result);

		result = processor
				.process("Action Fragment \"this is a test\" If this is a test then do that or if this is a test");
		assertEquals(
				"Action Fragment \"this is a test\" If {this is a test} then do that or if {this is a test}",
				result);
	}

	/**
	 * Check if fragment name lookup works.
	 */
	public void testGetFragmentNames() {
		ReferencePreprocessor processor = new ReferencePreprocessor();

		// Empty cases
		String[] result = processor.getFragmentNames("");
		assertEquals(0, result.length);

		result = processor.getFragmentNames("testing 1 2 3");
		assertEquals(0, result.length);

		result = processor.getFragmentNames("Validation Fragment ");
		assertEquals(0, result.length);

		result = processor.getFragmentNames("Validation Fragment \"abc");
		assertEquals(0, result.length);

		result = processor.getFragmentNames("Validation Fragment ");
		assertEquals(0, result.length);

		result = processor.getFragmentNames("Action Fragment ");
		assertEquals(0, result.length);

		// Ok
		result = processor.getFragmentNames("Validation Fragment \"abc\"");
		assertEquals(1, result.length);
		assertEquals("abc", result[0]);

		result = processor.getFragmentNames("Action Fragment \"abc\"");
		assertEquals(1, result.length);
		assertEquals("abc", result[0]);

		result = processor
				.getFragmentNames("Validation Fragment \"this is a test\" If this is a test then do that");
		assertEquals(1, result.length);
		assertEquals("this is a test", result[0]);

		result = processor
				.getFragmentNames("Action Fragment \"this is a test\" If this is a test then do that");
		assertEquals(1, result.length);
		assertEquals("this is a test", result[0]);

		result = processor
				.getFragmentNames("Validation Fragment \" Abc \" vALiDaTiOn FrAgMENT \"X\"");
		assertEquals(2, result.length);
		assertEquals("Abc", result[0]);
		assertEquals("X", result[1]);

		result = processor.getFragmentNames("Action Fragment \" Abc \" AcTiON FRAGMENT \"X\"");
		assertEquals(2, result.length);
		assertEquals("Abc", result[0]);
		assertEquals("X", result[1]);

		result = processor.getFragmentNames("Action Fragment \" Abc \" validation fragment \"X\"");
		assertEquals(2, result.length);
		assertEquals("Abc", result[0]);
		assertEquals("X", result[1]);

	}

}
