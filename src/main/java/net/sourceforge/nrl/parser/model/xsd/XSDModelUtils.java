package net.sourceforge.nrl.parser.model.xsd;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.nrl.parser.model.IAttribute;
import net.sourceforge.nrl.parser.model.IClassifier;

/** Utility methods for working with models loaded from an XSD.
 * 
 * @author Matthew Smith
 *
 */
public class XSDModelUtils {

	/** 
	 * Return all attributes of the given {@link IClassifier} that are tagged as having 
	 * been generated from an XSD attribute.
	 * @param container The {@link IClassifier} to search for attributes.
	 * @param includeInherited Whether to include attributes inherited from super types in the search.
	 * @return A list of {@link IAttribute} that are taggged as {@link IXSDUserData}.XSD_ATTRIBUTE_KIND
	 */
	public static List<IAttribute> getXsdAttributes(IClassifier container, boolean includeInherited) {
		return getAttributesByKind(container, includeInherited, IXSDUserData.XSD_ATTRIBUTE_KIND);
	}

	/** 
	 * Return all attributes of the given {@link IClassifier} that are tagged as having 
	 * been generated from an XSD element.
	 * @param container The {@link IClassifier} to search for attributes.
	 * @param includeInherited Whether to include attributes inherited from super types in the search.
	 * @return A list of {@link IAttribute} that are taggged as {@link IXSDUserData}.XSD_ELEMENT_KIND
	 */
	public static List<IAttribute> getXsdElements(IClassifier container, boolean includeInherited) {
		return getAttributesByKind(container, includeInherited, IXSDUserData.XSD_ELEMENT_KIND);
	}

	private static List<IAttribute> getAttributesByKind(IClassifier container,
			boolean includeInherited, String xsdAttributeKind) {
		if(container == null){
			throw new IllegalArgumentException("Container cannot be null");
		}
		List<IAttribute> attributes = container.getAttributes(includeInherited);
		List<IAttribute> filteredAttributes = new ArrayList<IAttribute>();
		for (IAttribute attribute : attributes) {
			Object attributeKind = attribute.getUserData(IXSDUserData.ATTRIBUTE_KIND);
			if(xsdAttributeKind.equals(attributeKind)){
				filteredAttributes.add(attribute);
			}
		}
		return filteredAttributes;
	}
	
}
