package net.sourceforge.nrl.parser.model.uml2;

import org.eclipse.uml2.uml.NamedElement;

/**
 * Additional user data keys that will be attached to the model elements, packages or
 * attributes by the UML2 loader. See the constants below for definitions.
 */
public interface IUML2UserData {

	/**
	 * The underlying UML2 element attached to the attribute, classifier, data type or
	 * package. The type will depend on what is being queried, but will always be
	 * a {@link NamedElement} and never null.
	 */
	public final static String UML2_ELEMENT = "UML2.Element";
}
