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
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

public class CastExpressionImplTest extends NRLParserTestSupport {

	@Test
	public void testParse() throws Exception {
		NRLActionParser parser = getParserFor("foo.x as a bar");

		CastExpressionImpl cast = (CastExpressionImpl) parser.constraint().getTree();
		Assert.assertEquals("foo.x", cast.getReference().getOriginalString());
		Assert.assertEquals("bar", cast.getTargetReference().getOriginalString());
	}
}
