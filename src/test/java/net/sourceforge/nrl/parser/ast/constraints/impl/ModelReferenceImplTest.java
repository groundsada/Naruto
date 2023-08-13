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
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser_NRLConstraintRules;

import org.junit.Test;

/**
 * Test parsing model references
 * 
 * @author Christian Nentwich
 */
public class ModelReferenceImplTest extends NRLParserTestSupport {

	@Test
	public void testParse() throws Exception {
		// Simple reference
		ModelReferenceImpl ref = getReference("test");
		assertEquals("test", ref.getOriginalString());
		assertEquals(1, ref.getStepsAsStrings().size());

		ref = getReference("test-test2");
		assertEquals("test-test2", ref.getOriginalString());
		assertEquals(1, ref.getStepsAsStrings().size());

		// Some steps
		ref = getReference("a.b.c");
		assertEquals("a.b.c", ref.getOriginalString());
		assertEquals(3, ref.getStepsAsStrings().size());
		assertEquals("a", ref.getStepsAsStrings().get(0));
		assertEquals("b", ref.getStepsAsStrings().get(1));
		assertEquals("c", ref.getStepsAsStrings().get(2));

		// Using "of" instead of dot notation
		ref = getReference("c of b of aa");
		assertEquals("c of b of aa", ref.getOriginalString());
		assertEquals(3, ref.getStepsAsStrings().size());
		assertEquals("aa", ref.getStepsAsStrings().get(0));
		assertEquals("b", ref.getStepsAsStrings().get(1));
		assertEquals("c", ref.getStepsAsStrings().get(2));

		// Using "of" and dot notation
		ref = getReference("c.d of b of aa");
		assertEquals("c.d of b of aa", ref.getOriginalString());
		assertEquals(4, ref.getStepsAsStrings().size());
		assertEquals("aa", ref.getStepsAsStrings().get(0));
		assertEquals("b", ref.getStepsAsStrings().get(1));
		assertEquals("c", ref.getStepsAsStrings().get(2));
		assertEquals("d", ref.getStepsAsStrings().get(3));
	}

	@Test
	public void testParseKeywordEscape() throws Exception {
		// Simple reference
		ModelReferenceImpl ref = getReference("`test");
		assertEquals("`test", ref.getOriginalString());
		assertEquals(1, ref.getStepsAsStrings().size());
		assertEquals("test", ref.getStepsAsStrings().get(0));

		ref = getReference("`test-test2");
		assertEquals("`test-test2", ref.getOriginalString());
		assertEquals(1, ref.getStepsAsStrings().size());
		assertEquals("test-test2", ref.getStepsAsStrings().get(0));

		// Some steps
		ref = getReference("`a.b.c");
		assertEquals("`a.b.c", ref.getOriginalString());
		assertEquals(3, ref.getStepsAsStrings().size());
		assertEquals("a", ref.getStepsAsStrings().get(0));
		assertEquals("b", ref.getStepsAsStrings().get(1));
		assertEquals("c", ref.getStepsAsStrings().get(2));

		ref = getReference("a.`b.c");
		assertEquals("a.`b.c", ref.getOriginalString());
		assertEquals(3, ref.getStepsAsStrings().size());
		assertEquals("a", ref.getStepsAsStrings().get(0));
		assertEquals("b", ref.getStepsAsStrings().get(1));
		assertEquals("c", ref.getStepsAsStrings().get(2));

		ref = getReference("`a.`b.`c");
		assertEquals("`a.`b.`c", ref.getOriginalString());
		assertEquals(3, ref.getStepsAsStrings().size());
		assertEquals("a", ref.getStepsAsStrings().get(0));
		assertEquals("b", ref.getStepsAsStrings().get(1));
		assertEquals("c", ref.getStepsAsStrings().get(2));

		// Using "of" instead of dot notation
		ref = getReference("c of b of `aa");
		assertEquals("c of b of `aa", ref.getOriginalString());
		assertEquals(3, ref.getStepsAsStrings().size());
		assertEquals("aa", ref.getStepsAsStrings().get(0));
		assertEquals("b", ref.getStepsAsStrings().get(1));
		assertEquals("c", ref.getStepsAsStrings().get(2));

		// Using "of" and dot notation
		ref = getReference("c.`d of `b of aa");
		assertEquals("c.`d of `b of aa", ref.getOriginalString());
		assertEquals(4, ref.getStepsAsStrings().size());
		assertEquals("aa", ref.getStepsAsStrings().get(0));
		assertEquals("b", ref.getStepsAsStrings().get(1));
		assertEquals("c", ref.getStepsAsStrings().get(2));
		assertEquals("d", ref.getStepsAsStrings().get(3));

		// Package
		ref = getReference("`foo::bar");
		assertEquals("`foo::bar", ref.getOriginalString());
		assertEquals(1, ref.getStepsAsStrings().size());
		assertEquals("foo::bar", ref.getStepsAsStrings().get(0));

		ref = getReference("`foo::bar::`baz");
		assertEquals("`foo::bar::`baz", ref.getOriginalString());
		assertEquals(1, ref.getStepsAsStrings().size());
		assertEquals("foo::bar::baz", ref.getStepsAsStrings().get(0));
	}

	@Test
	public void testParseUTF() throws Exception {
		// European chars
		ModelReferenceImpl ref = getReference("çãüäö");
		assertEquals("çãüäö", ref.getOriginalString());
		assertEquals("çãüäö", ref.getStepsAsStrings().get(0));

		ref = getReference("üäö");
		assertEquals("üäö", ref.getOriginalString());
		assertEquals("üäö", ref.getStepsAsStrings().get(0));

		ref = getReference("日本語.日本語");
		assertEquals("日本語.日本語", ref.getOriginalString());
		assertEquals("日本語", ref.getStepsAsStrings().get(0));
		assertEquals("日本語", ref.getStepsAsStrings().get(1));
	}

	/*
	 * Parse a string into a model reference
	 */
	private ModelReferenceImpl getReference(String str) throws Exception {
		NRLActionParser parser = getParserFor(str);
		NRLActionParser_NRLConstraintRules.modelReference_return result = parser.modelReference();
		assertEquals(0, parser.getSyntaxErrors().size());
		return (ModelReferenceImpl) result.getTree();
	}
}
