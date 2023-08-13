package net.sourceforge.nrl.parser.model.uml2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Element;

/**
 * A class with static helper methods.
 * 
 * @author Christian Nentwich
 */
public class UML2Helper {

	/**
	 * Extract a list of comments from a UML2 model element.
	 * 
	 * @param modelElement the element
	 * @return the comments
	 */
	public static List<String> extractComments(Element modelElement) {
		List<String> result = new ArrayList<String>();

		for (Iterator<?> iter = modelElement.getOwnedComments().iterator(); iter
				.hasNext();) {
			Comment comment = (Comment) iter.next();
			if (comment.getBody() != null)
				result.add(comment.getBody());
		}

		return result;

	}

	/**
	 * Given the element name, clean it up by removing illegal characters like
	 * dots.
	 * 
	 * @param name the name
	 * @return the cleaned up name
	 */
	public static String cleanUpName(String name) {
		StringBuffer result = new StringBuffer();

		String sanitisedName = name.replaceAll(" ", "_");
		StringTokenizer tokenizer = new StringTokenizer(sanitisedName, ".");
		int count = 0;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();

			if (count == 0) {
				result.append(token);
			} else {
				result.append(Character.toUpperCase(token.charAt(0)));
				if (token.length() > 1)
					result.append(token.substring(1));
			}

			count++;
		}

		return result.toString();
	}

}
