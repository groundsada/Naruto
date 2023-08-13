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
package net.sourceforge.nrl.parser.model.xsd;

import junit.framework.TestCase;

/**
 * Test the utility class.
 * 
 * @author Christian Nentwich
 */
public class XSDHelperTest extends TestCase {

	public void testGetQualifiedName() {
		assertEquals(":foo", XSDHelper.getQualifiedName(null, "foo"));
		assertEquals(":null", XSDHelper.getQualifiedName(null, null));
		assertEquals("foo:bar", XSDHelper.getQualifiedName("foo", "bar"));
	}

	public void testGetCleanedName() {
		assertNull(XSDHelper.getCleanedName(null));
		assertEquals("", XSDHelper.getCleanedName(""));

		assertEquals("testing123", XSDHelper.getCleanedName("testing123"));

		assertEquals("fooBar", XSDHelper.getCleanedName("foo bar"));
		assertEquals("fooBarBaz", XSDHelper.getCleanedName("foo.bar.baz"));

		assertEquals("2", XSDHelper.getCleanedName("2"));
	}

	public void testGetPackageName() {
		assertEquals("test", XSDHelper.getPackageName("test"));
		assertEquals("test", XSDHelper.getPackageName("test.xsd"));
		assertEquals("test", XSDHelper.getPackageName("C:/temp/test.xsd"));
		assertEquals("test", XSDHelper.getPackageName("C:\\temp\\test.xsd"));
		assertEquals("test", XSDHelper.getPackageName("C:\\temp/test.xsd"));
	}
}
