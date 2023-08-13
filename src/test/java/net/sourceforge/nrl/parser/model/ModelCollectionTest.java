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
package net.sourceforge.nrl.parser.model;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sourceforge.nrl.parser.NRLParserTestSupport;

/**
 * Test the ModelCollection class.
 * 
 * @author Christian Nentwich
 */
public class ModelCollectionTest extends NRLParserTestSupport {

	@Test
	public void testIsAmbigous() throws Exception {
		ModelCollection models = new ModelCollection();
		models.addModelPackage(getBasicModel());
		models.addModelPackage(getSimpleModel());

		assertFalse(models.isAmbiguous("IRSwap"));
		assertFalse(models.isAmbiguous("doesnotexist"));

		// Ambiguous within one model
		assertTrue(models.isAmbiguous("Ambiguous"));

		// Ambiguous across models
		assertTrue(models.isAmbiguous("Ambiguous2"));
	}

	@Test
	public void testGetElementByName() throws Exception {
		ModelCollection models = new ModelCollection();
		models.addModelPackage(getBasicModel());
		models.addModelPackage(getSimpleModel());

		assertNull(models.getElementByName("doesnotexist"));

		assertNotNull(models.getElementByName("IRSwap"));
		assertNotNull(models.getElementByName("Ambiguous"));
		assertNotNull(models.getElementByName("Ambiguous2"));
	}

	@Test
	public void testGetModelPackageByName() throws Exception {
		ModelCollection models = new ModelCollection();
		models.addModelPackage(getBasicModel());
		models.addModelPackage(getSimpleModel());

		assertNull(models.getModelPackageByName("doesnotexist"));

		assertNotNull(models.getModelPackageByName("BasicModel"));
		assertNotNull(models.getModelPackageByName("SimpleModel"));
	}

	@Test
	public void testGetElementByQualifiedName() throws Exception {
		ModelCollection models = new ModelCollection();
		models.addModelPackage(getBasicModel());

		assertNull(models.getElementByQualifiedName("doesnotexist"));
		assertNull(models.getElementByQualifiedName("BasicModel::foo::Trade"));
		assertNull(models.getElementByQualifiedName("BasicMode::Main::Trade"));

		assertNotNull(models.getElementByQualifiedName("BasicModel::Main::Trade"));
		assertEquals("Trade", models.getElementByQualifiedName("BasicModel::Main::Trade").getName());
		assertNotNull(models.getElementByQualifiedName("BasicModel::Main::IR"));
		assertEquals("IR", models.getElementByQualifiedName("BasicModel::Main::IR").getName());
		assertNotNull(models.getElementByQualifiedName("BasicModel::Main"));
		assertEquals("Main", models.getElementByQualifiedName("BasicModel::Main").getName());
	}

}
