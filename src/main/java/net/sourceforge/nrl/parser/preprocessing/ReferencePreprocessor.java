package net.sourceforge.nrl.parser.preprocessing;

import java.util.ArrayList;
import java.util.List;

/**
 * The reference processor class detects fragment references, and resolves them
 * before the main parsing stage.
 * <p>
 * It scans the rule file for fragment declarations, and then for any sentence
 * fragments that equal the fragment declaration. If it finds any, it puts the
 * curly brackets that the parser expects around them.
 * <p>
 * For example, if "this is a fragment" is declared as a fragment somewhere and
 * the text <code>this is a fragment</code> is detected in the rule file, it
 * has curly brackets placed around it.
 * 
 * @author Christian Nentwich
 */
public class ReferencePreprocessor {

	protected final static String VALIDATION_FRAGMENT_KEYWORD = "validation fragment";

	protected final static String ACTION_FRAGMENT_KEYWORD = "action fragment";

	protected final static char PROP_START = '{';

	protected final static char PROP_END = '}';

	public String process(String input) {
		if (input == null)
			return null;

		// Get fragment names
		String[] fragments = getFragmentNames(input);
		return process(input, fragments);
	}
	
	public String process(String input, String[] properties) {
		if (input == null)
			return null;
		
		StringBuffer result = new StringBuffer();
		result.append(input);

		// Now try to replace them if they occur in text
		for (int i = 0; i < properties.length; i++) {
			String fragment = properties[i];

			int pos = 0;
			while (pos != -1) {
				pos = result.indexOf(fragment, pos);
				if (pos == -1)
					break;

				// Make sure it's not the declaration itself and that it has not
				// been previously replaced
				if (pos > 0 && result.charAt(pos - 1) != '\"'
						&& result.charAt(pos - 1) != PROP_START) {
					result.insert(pos, PROP_START);
					result.insert(pos + fragment.length() + 1, PROP_END);
				}

				pos = pos + fragment.length();
				if (pos >= result.length())
					break;
			}
		}

		return result.toString();
	}

	/**
	 * Return all fragment names declared in a file
	 * 
	 * @return the fragment names
	 */
	public String[] getFragmentNames(String input) {
		String inputLowercase = input.toLowerCase();

		List<String> result = new ArrayList<String>();

		int pos = 0;
		while (pos != -1) {
			int validationPos = inputLowercase.indexOf(VALIDATION_FRAGMENT_KEYWORD, pos);
			int actionPos = inputLowercase.indexOf(ACTION_FRAGMENT_KEYWORD, pos);
			
			if (validationPos != -1) {
				if (actionPos == -1)
					pos = validationPos;
				else
					pos = validationPos < actionPos ? validationPos : actionPos;
			} else {
				if (actionPos == -1)
					break;				
				pos = actionPos;
			}

			// Do not process if this is a quoted line
			if (isQuotedLine(input, pos)) {
				pos++;
				continue;
			}
			
			// Skip spaces and find quoted name (might be unterminated
			// or not there!)
			while (pos < input.length() && input.charAt(pos) != '"')
				pos++;

			pos++;
			String fragmentName = "";
			while (pos < input.length() && input.charAt(pos) != '"') {
				fragmentName += input.charAt(pos);
				pos++;
			}

			if (pos >= input.length() || input.charAt(pos) != '"')
				continue;

			if (fragmentName.trim().length() > 0)
				result.add(fragmentName.trim());
		}

		return result.toArray(new String[0]);
	}

	/**
	 * Return true if the position is in a quoted line
	 * @param input the document
	 * @param pos position
	 * @return true if quoted, false otherwise
	 */
	protected boolean isQuotedLine(String input, int pos) {
		boolean startOfComment = false;
		
		while (pos >= 0 && pos < input.length()) {
			char c = input.charAt(pos);
			
			if (c == '-') {
				if (startOfComment) {
					return true;
				} else {
					startOfComment = true;
				}
			} else if (c == '\n' || c == '\r') {
				return false;
			} else {
				startOfComment = false;
			}
			
			pos--;
		}
		
		return false;
	}
}
