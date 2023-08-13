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
package net.sourceforge.nrl.parser.ast.action.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.action.IActionRuleDeclaration;
import net.sourceforge.nrl.parser.ast.action.ICompoundAction;
import net.sourceforge.nrl.parser.ast.constraints.impl.ModelReferenceImpl;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;

import org.junit.Test;

/**
 * Test action rule declarations
 * 
 * @author Christian Nentwich
 */
public class ActionRuleDeclarationImplTest extends NRLParserTestSupport {

	/**
	 * Test parsing without where clause
	 */
	@Test
	public void testParse() throws Exception {
		NRLActionParser parser = getParserFor("Context: foo Action Rule \"m1\" Set x to 'a'");

		IActionRuleDeclaration rule = (IActionRuleDeclaration) parser.declaration().getTree();
		assertEquals("m1", rule.getId());
		assertEquals("foo", ((ModelReferenceImpl) ((ActionRuleDeclarationImpl) rule)
				.getModelReference()).getOriginalString());
		assertTrue(rule.hasContext());
		assertTrue(rule.getAction() instanceof ICompoundAction);
		assertTrue("Should not have additional parameters", rule.getAdditionalParameterNames()
				.isEmpty());
	}

	@Test
	public void testParseWithNoneContext() throws Exception {
		NRLActionParser parser = getParserFor("Context: None Action Rule \"m1\" Set x to 'a'");

		IActionRuleDeclaration rule = (IActionRuleDeclaration) parser.declaration().getTree();
		assertTrue(!rule.hasContext());
		assertEquals("Wrong rule id", "m1", rule.getId());
		assertTrue("Should not have additional parameters", rule.getAdditionalParameterNames()
				.isEmpty());
	}

	@Test
	public void testParseAppliesToSyntax() throws Exception {
		NRLActionParser parser = getParserFor("Action Rule \"m1\" applies to foo Set x to 'a'");

		IActionRuleDeclaration rule = (IActionRuleDeclaration) parser.declaration().getTree();
		assertEquals("m1", rule.getId());
		assertEquals("foo", ((ModelReferenceImpl) ((ActionRuleDeclarationImpl) rule)
				.getModelReference()).getOriginalString());
		assertTrue(rule.hasContext());
		assertTrue(rule.getAction() instanceof ICompoundAction);
		assertTrue("Should not have additional parameters", rule.getAdditionalParameterNames()
				.isEmpty());
	}

	@Test
	public void testParseAppliesToSyntaxWithAdditionalParameter() throws Exception {
		NRLActionParser parser = getParserFor("Action Rule \"m1\" applies to foo and uses y (\"y1\") Set x to 'a'");

		IActionRuleDeclaration rule = (IActionRuleDeclaration) parser.declaration().getTree();
		assertEquals("m1", rule.getId());
		assertEquals("foo", ((ModelReferenceImpl) ((ActionRuleDeclarationImpl) rule)
				.getModelReference()).getOriginalString());
		assertTrue(rule.hasContext());
		assertTrue(rule.getAction() instanceof ICompoundAction);
		assertEquals("Should have one additional parameter", 1, rule.getAdditionalParameterNames()
				.size());
		assertEquals("Parameter name wrong", "y1", rule.getAdditionalParameterNames().get(0));
		assertEquals("Parameter type ref wrong", "y", ((ActionRuleDeclarationImpl) rule)
				.getAdditionalParameterTypeReference("y1").getOriginalString());
	}
}
