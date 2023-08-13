/*
 * The contents of this file are subject to the Mozilla Public License Version 1.1 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF
 * ANY KIND, either express or implied. See the License for the specific language governing rights
 * and limitations under the License.
 * 
 * The Original Code is the NRL Parser, released 28 April 2006, Copyright (c) Christian Nentwich.
 * The Initial Developer of the Original Code is Christian Nentwich. Portions created by
 * contributors identified in the NOTICES file are Copyright (c) the contributors. All Rights
 * Reserved.
 */
package net.sourceforge.nrl.parser.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.NRLDataType;
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.model.IPackage;

import org.junit.Test;

/**
 * Test the type mapping class.
 * 
 * @author Christian Nentwich
 */
public class TypeMappingTest extends NRLParserTestSupport {

	@Test
	public void testAddMapping() {
		TypeMapping mapping = new TypeMapping();

		// Add a mapping, then change the type
		mapping.addMapping("a::b::c", "A", NRLDataType.DATE);
		assertEquals(1, mapping.getMapping().size());
		mapping.addMapping("a::b::c", "A", NRLDataType.DECIMAL);
		assertEquals(1, mapping.getMapping().size());

		mapping.clear();
		assertEquals(0, mapping.getMapping().size());
	}

	@Test
	public void testGetType() throws Exception {
		IPackage model = getBasicModel();

		TypeMapping mapping = new TypeMapping();

		// Add a mapping with a qualified, and one with "any" package name.
		// (Note, the mapping doesn't make sense in terms of the model)
		mapping.addMapping("BasicModel::Main", "Date", NRLDataType.DATE);
		mapping.addMapping("*", "IRSwap", NRLDataType.DECIMAL);

		IModelElement swap = model.getElementByName("IRSwap", true);
		IModelElement date = model.getElementByName("Date", true);
		IModelElement header = model.getElementByName("TradeHeader", true);
		assertNotNull(swap);
		assertNotNull(date);
		assertNotNull(header);

		// Do the lookups
		assertEquals(NRLDataType.DATE, mapping.getType(date));
		assertEquals(NRLDataType.DECIMAL, mapping.getType(swap));
		assertEquals(NRLDataType.UNKNOWN, mapping.getType(header));
	}
}
