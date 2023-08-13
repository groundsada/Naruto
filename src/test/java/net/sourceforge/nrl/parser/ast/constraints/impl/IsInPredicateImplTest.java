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
import static org.junit.Assert.assertTrue;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.constraints.IIsInPredicate;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

/**
 * Unit tests for "is one of" predicates.
 * 
 * @author Christian Nentwich
 */
public class IsInPredicateImplTest extends NRLParserTestSupport {

	@Test
	public void testParse() throws Exception {
		NRLActionParser parser = getParserFor("foo is one of 'A','B','C',foo.bar");

		IIsInPredicate pred = (IIsInPredicate) parser.constraint().getTree();
		assertTrue(pred.getExpression() instanceof IModelReference);
		assertEquals(4, pred.getList().size());
		assertEquals("A", pred.getList().get(0).toString());
		assertEquals("B", pred.getList().get(1).toString());
		assertEquals("C", pred.getList().get(2).toString());
		assertEquals("foo.bar", ((IModelReference) pred.getList().get(3)).getOriginalString());
	}
}
