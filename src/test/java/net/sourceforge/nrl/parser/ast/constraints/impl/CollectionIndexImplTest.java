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
package net.sourceforge.nrl.parser.ast.constraints.impl;

import static org.junit.Assert.assertEquals;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.constraints.ICollectionIndex;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

public class CollectionIndexImplTest extends NRLParserTestSupport {

	@Test
	public void testParse() throws Exception {
		NRLActionParser parser = getParserFor("first entry");

		ICollectionIndex coll = (ICollectionIndex) parser.constraint().getTree();
		assertEquals("entry", coll.getCollection().getOriginalString());
		assertEquals(1, coll.getPosition());

		parser = getParserFor("second entry");
		coll = (ICollectionIndex) parser.constraint().getTree();
		assertEquals(2, coll.getPosition());

		parser = getParserFor("third entry");
		coll = (ICollectionIndex) parser.constraint().getTree();
		assertEquals(3, coll.getPosition());

		parser = getParserFor("1st entry");
		coll = (ICollectionIndex) parser.constraint().getTree();
		assertEquals(1, coll.getPosition());

		parser = getParserFor("51st entry");
		coll = (ICollectionIndex) parser.constraint().getTree();
		assertEquals(51, coll.getPosition());
	}
}
