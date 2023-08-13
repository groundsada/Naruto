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

import junit.framework.Assert;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.constraints.IIsNotInPredicate;
import net.sourceforge.nrl.parser.ast.constraints.ILiteralString;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

public class IsNotInPredicateImplTest extends NRLParserTestSupport {

	@Test
	public void testParse() throws Exception {
		NRLActionParser parser = getParserFor("x.y is not one of 'a',foo.bar,'c'");

		IIsNotInPredicate notIn = (IIsNotInPredicate) parser.constraint().getTree();
		Assert.assertEquals("x.y", ((IModelReference) notIn.getExpression()).getOriginalString());
		Assert.assertEquals(3, notIn.getList().size());
		Assert.assertEquals("a", ((ILiteralString) notIn.getList().get(0)).getString());
		Assert.assertEquals("foo.bar", ((IModelReference) notIn.getList().get(1))
				.getOriginalString());
	}
}
