package net.sourceforge.nrl.parser.model.xsd;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A helper class that is used to create XPaths pointing to entries in schemas.
 * 
 * @since 1.4.7
 */
class SchemaPathCreator {

	// Namespace prefix to use for XML schema steps in the path
	private final static String XSD_PREFIX = "xs";

	/**
	 * Return a path to an attribute in a schema. This path is then used for tasks like
	 * declaring JAXB bindings.
	 * 
	 * @param attr the attribute
	 * @return the path or <b>null</b> if the path could not be computed
	 */
	public static String getPath(XSDAttribute attr) {
		Element element = attr.getSchemaElement();
		if (element == null) {
			return null;
		}

		StringBuilder result = new StringBuilder();

		while (element.getParentNode() != null) {
			String step = String.format("/%s:%s", XSD_PREFIX, element.getLocalName());

			if (element.hasAttribute("name")) {
				String name = element.getAttribute("name");
				step = step + String.format("[@name = '%s']", name);

				if (countTotalOccurrencesOfNamedElement(element, name) > 1) {
					step = step + String.format("[%d]", getIndexOfNamedElement(element, name));
				}
			} else if (countTotalOccurrencesOfElement(element) > 1) {
				step = step + String.format("[%d]", getIndexOfElement(element));
			}

			result.insert(0, step);

			if (!(element.getParentNode() instanceof Element)) {
				break;
			}
			element = (Element) element.getParentNode();
		}

		return result.toString();
	}

	private static int countTotalOccurrencesOfNamedElement(Element element, String name) {
		String localName = element.getLocalName();
		if (!(element.getParentNode() instanceof Element)) {
			return 1;
		}

		Element parent = (Element) element.getParentNode();
		NodeList children = parent.getChildNodes();
		int count = 0;
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode instanceof Element) {
				Element childElement = (Element) childNode;
				if (localName.equals(childElement.getLocalName())
						&& name.equals(childElement.getAttribute("name"))) {
					count++;
				}
			}
		}

		return count;
	}

	private static int countTotalOccurrencesOfElement(Element element) {
		String localName = element.getLocalName();
		if (!(element.getParentNode() instanceof Element)) {
			return 1;
		}

		Element parent = (Element) element.getParentNode();
		NodeList children = parent.getChildNodes();
		int count = 0;
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode instanceof Element) {
				Element childElement = (Element) childNode;
				if (localName.equals(childElement.getLocalName())) {
					count++;
				}
			}
		}

		return count;
	}

	private static int getIndexOfNamedElement(Element element, String name) {
		String localName = element.getLocalName();
		if (!(element.getParentNode() instanceof Element)) {
			return 1;
		}

		Element parent = (Element) element.getParentNode();
		NodeList children = parent.getChildNodes();
		int count = 0;
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode instanceof Element) {
				Element childElement = (Element) childNode;
				if (childElement == element) {
					return count + 1;
				}
				if (localName.equals(childElement.getLocalName())
						&& name.equals(childElement.getAttribute("name"))) {
					count++;
				}
			}
		}

		return count + 1;
	}

	private static int getIndexOfElement(Element element) {
		String localName = element.getLocalName();
		if (!(element.getParentNode() instanceof Element)) {
			return 1;
		}

		Element parent = (Element) element.getParentNode();
		NodeList children = parent.getChildNodes();
		int count = 0;
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode instanceof Element) {
				Element childElement = (Element) childNode;
				if (childElement == element) {
					return count + 1;
				}
				if (localName.equals(childElement.getLocalName())) {
					count++;
				}
			}
		}

		return count + 1;
	}
}
