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
import net.sourceforge.nrl.parser.ast.constraints.IFunctionalExpression;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

public class FunctionalExpressionImplTest extends NRLParserTestSupport {

	@Test
	public void testParse_Sum() throws Exception {
		NRLActionParser parser = getParserFor("the sum of x.y.z");

		IFunctionalExpression func = (IFunctionalExpression) parser.constraint().getTree();
		Assert.assertEquals(IFunctionalExpression.Function.SUMOF, func.getFunction());
		Assert.assertEquals(1, func.getParameters().size());
		Assert.assertTrue(func.getParameters().get(0) instanceof IModelReference);
		Assert.assertEquals("x.y.z", ((IModelReference) func.getParameters().get(0))
				.getOriginalString());
	}

	@Test
	public void testParse_NumberOf() throws Exception {
		NRLActionParser parser = getParserFor("the number of x.y");

		IFunctionalExpression func = (IFunctionalExpression) parser.constraint().getTree();
		Assert.assertEquals(IFunctionalExpression.Function.NUMBER_OF, func.getFunction());
		Assert.assertEquals(1, func.getParameters().size());
		Assert.assertTrue(func.getParameters().get(0) instanceof IModelReference);
		Assert.assertEquals("x.y", ((IModelReference) func.getParameters().get(0))
				.getOriginalString());

		parser = getParserFor("the number of unique x.y (by z.w)");

		func = (IFunctionalExpression) parser.constraint().getTree();
		Assert.assertEquals(IFunctionalExpression.Function.NUMBER_OF, func.getFunction());
		Assert.assertEquals(2, func.getParameters().size());
		Assert.assertTrue(func.getParameters().get(0) instanceof IModelReference);
		Assert.assertEquals("x.y", ((IModelReference) func.getParameters().get(0))
				.getOriginalString());
		Assert.assertTrue(func.getParameters().get(1) instanceof IModelReference);
		Assert.assertEquals("z.w", ((IModelReference) func.getParameters().get(1))
				.getOriginalString());
	}
}
