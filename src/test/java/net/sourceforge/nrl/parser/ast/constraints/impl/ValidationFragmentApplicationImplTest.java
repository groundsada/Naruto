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
import net.sourceforge.nrl.parser.ast.constraints.IIntegerNumber;
import net.sourceforge.nrl.parser.ast.constraints.IValidationFragmentApplication;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

public class ValidationFragmentApplicationImplTest extends NRLParserTestSupport {

	@SuppressWarnings("deprecation")
	@Test
	public void testParse() throws Exception {
		NRLActionParser parser = getParserFor("{do something}");
		IValidationFragmentApplication app = (IValidationFragmentApplication) parser.constraint()
				.getTree();
		Assert.assertEquals("do something", app.getFragmentName());
		Assert.assertEquals(0, app.getNumParameters());

		// Infix
		parser = getParserFor("x {do something} 5");
		app = (IValidationFragmentApplication) parser.constraint().getTree();
		Assert.assertEquals("do something", app.getFragmentName());
		Assert.assertEquals(2, app.getNumParameters());
		Assert.assertEquals("x", ((IModelReference) app.getParameter(0)).getOriginalString());
		Assert.assertEquals(5, ((IIntegerNumber) app.getParameter(1)).getNumber());

		// postfix
		parser = getParserFor("{do something} x and 5");
		app = (IValidationFragmentApplication) parser.constraint().getTree();
		Assert.assertEquals("do something", app.getFragmentName());
		Assert.assertEquals(2, app.getNumParameters());
		Assert.assertEquals("x", ((IModelReference) app.getParameter(0)).getOriginalString());
		Assert.assertEquals(5, ((IIntegerNumber) app.getParameter(1)).getNumber());
	}
}
