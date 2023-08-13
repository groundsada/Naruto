/*
 * The contents of this file are subject to the Mozilla Public License 
 * Version 1.1 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See 
 * the License for the specific language governing rights and limitations 
 * under the License.
 * 
 * The Original Code is the NRL Parser, released 28 April 2006, 
 * Copyright (c) Christian Nentwich. The Initial Developer of the 
 * Original Code is Christian Nentwich. Portions created by contributors 
 * identified in the NOTICES file are Copyright (c) the contributors. 
 * All Rights Reserved. 
 */
package net.sourceforge.nrl.parser.type;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import net.sourceforge.nrl.parser.jaxb14.JaxbMapping;
import net.sourceforge.nrl.parser.jaxb14.JaxbTypeMapping;

import org.xml.sax.SAXException;

/**
 * A type mapping that can be loaded from and saved to an XML file.
 * <p>
 * The mapping file format is very simple, and the loading function does not do
 * any validation - it would just produce an empty mapping if another type of
 * XML file was passed.
 * <p>
 * <code>
 * &lt;typeMapping&gt;<br>
 * &lt;mapping name="ModelElement" package="*" type="String"/&gt;<br>
 * ...<br>
 * &lt;/typeMapping&gt;
 * </code>
 * 
 * @author Christian Nentwich
 */
public class XmlTypeMapping extends TypeMapping {

	/**
	 * Load the mapping from an XML file.
	 * 
	 * @param file the file
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public void load(File file) throws Exception {
		FileInputStream stream = new FileInputStream(file);
		load(stream);
	}

	/**
	 * Load the mapping from a stream.
	 * 
	 * @param stream the stream
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public void load(InputStream stream) throws Exception {
		clear();

		JAXBContext context = JAXBContext.newInstance("net.sourceforge.nrl.parser.jaxb14",
				JaxbTypeMapping.class.getClassLoader());
		Unmarshaller unmarshaller = context.createUnmarshaller();

		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = null;
		try {
			InputStream schemaStream = this.getClass().getResourceAsStream(
					"/nrlparser.xsd");
			schema = schemaFactory.newSchema(new StreamSource(schemaStream));
		} catch (Exception e) {
			throw new Exception(
					"Internal error - nrlparser.xsd not on classpath or contains errors",
					e);
		}

		unmarshaller.setSchema(schema);

		JAXBElement<?> root = (JAXBElement<?>) unmarshaller.unmarshal(stream);
		JaxbTypeMapping typeMapping = (JaxbTypeMapping) root.getValue();

		for (JaxbMapping jaxbMap : typeMapping.getMapping()) {
			addMapping(jaxbMap.getPackage(), jaxbMap.getName(), getTypeFromString(jaxbMap
					.getType().value()));
		}
	}

}
