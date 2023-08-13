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
import net.sourceforge.nrl.parser.ast.constraints.ICardinalityConstraint;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

/**
 * Test parsing cardinality constraints
 * 
 * @author Christian Nentwich
 */
public class CardinalityConstraintImplTest extends NRLParserTestSupport {

	@Test
	public void testParse() throws Exception {
		ICardinalityConstraint card = getCardinality("at least one");
		assertEquals(1, card.getNumber());
		assertEquals(ICardinalityConstraint.QualifierEnum.AT_LEAST, card.getQualifier());

		card = getCardinality("at most two");
		assertEquals(2, card.getNumber());
		assertEquals(ICardinalityConstraint.QualifierEnum.AT_MOST, card.getQualifier());

		card = getCardinality("exactly three");
		assertEquals(3, card.getNumber());
		assertEquals(ICardinalityConstraint.QualifierEnum.EXACTLY, card.getQualifier());

		card = getCardinality("four");
		assertEquals(4, card.getNumber());
		assertEquals(ICardinalityConstraint.QualifierEnum.EXACTLY, card.getQualifier());

		card = getCardinality("5");
		assertEquals(5, card.getNumber());
		assertEquals(ICardinalityConstraint.QualifierEnum.EXACTLY, card.getQualifier());

		card = getCardinality("at least 6");
		assertEquals(6, card.getNumber());
		assertEquals(ICardinalityConstraint.QualifierEnum.AT_LEAST, card.getQualifier());

		card = getCardinality("no");
		assertEquals(0, card.getNumber());
		assertEquals(ICardinalityConstraint.QualifierEnum.EXACTLY, card.getQualifier());
	}

	/*
	 * Parse a string into a cardinality constraint
	 */
	private ICardinalityConstraint getCardinality(String str) throws Exception {
		NRLActionParser parser = getParserFor(str);
		return (ICardinalityConstraint) parser.enumerator().getTree();
	}
}
