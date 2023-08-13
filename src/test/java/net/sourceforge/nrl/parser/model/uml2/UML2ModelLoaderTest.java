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
package net.sourceforge.nrl.parser.model.uml2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Iterator;

import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.model.AbstractModelElement;
import net.sourceforge.nrl.parser.model.IAttribute;
import net.sourceforge.nrl.parser.model.IClassifier;
import net.sourceforge.nrl.parser.model.IDataType;
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.model.IPackage;

import org.junit.Test;

/**
 * Test the entire model loader. This tests all the model classes again, and also tests if
 * resolution works correctly.
 * 
 * @author Christian Nentwich
 */
public class UML2ModelLoaderTest extends NRLParserTestSupport {

	/**
	 * Load a sample model (src/test/resources/basicmodel.uml2) and check if all resolution worked.
	 */
	@Test
	public void testLoad() throws Exception {
		UML2ModelLoader loader = new UML2ModelLoader();

		File file = new File("src/test/resources/uml/basicmodel.uml");
		IPackage model = loader.load(getResourceForFile(file));

		for (Iterator<String> iter = loader.getWarnings().iterator(); iter.hasNext();)
			System.out.println(iter.next());

			// Check the top package and model lookup functions
		assertEquals("BasicModel", model.getName());
		assertNotNull(model.getElementByName("Main", false));
		assertEquals("Object", model.getParent().getName());
		assertNotNull(model.getElementByName("Trade", true));
		assertNull(model.getElementByName("Trade", false));
		assertNotNull(model.getElementByName("IRSwap", true));

		// Check the "Main" package
		IPackage main = (IPackage) model.getElementByName("Main", false);
		assertEquals(7, main.getSize());
		assertTrue(main.getElementByName("Trade", false) instanceof IClassifier);
		assertTrue(main.getElementByName("TradeHeader", false) instanceof IClassifier);
		assertTrue(main.getElementByName("Date", false) instanceof IDataType);
		assertNotNull(main.getUserData(IUML2UserData.UML2_ELEMENT));

		IClassifier trade = (IClassifier) model.getElementByName("Trade", true);
		IClassifier irSwap = (IClassifier) model.getElementByName("IRSwap", true);
		assertTrue(irSwap.getParent() == trade);
		assertTrue(trade.getDescendants(true).get(0) == irSwap);
		assertTrue(!trade.isSupplementary());
		assertNotNull(trade.getUserData(IUML2UserData.UML2_ELEMENT));

		assertTrue(AbstractModelElement.OBJECT.isAssignableFrom(trade));

		assertEquals(IModelElement.ElementType.Classifier, trade.getElementType());
		assertEquals(IModelElement.ElementType.Classifier, irSwap.getElementType());

		// Check that trade has all attributes
		assertNotNull(trade.getAttributeByName("tradeDate", false));
		assertNotNull(trade.getAttributeByName("tradeheader", false));
		assertNotNull(trade.getAttributeByName("terminationDate", false));
		assertNotNull(trade.getAttributeByName("description", false));

		assertEquals(0, trade.getAttributeByName("description", false).getMinOccurs());
		assertEquals(IAttribute.UNBOUNDED, trade.getAttributeByName("description", false)
				.getMaxOccurs());

		assertNotNull(trade.getAttributeByName("description", false).getUserData(
				IUML2UserData.UML2_ELEMENT));

		// Check that the attribute types are resolved
		assertEquals("Date", trade.getAttributeByName("tradeDate", false).getType().getName());
		assertEquals("TradeHeader", trade.getAttributeByName("tradeheader", false).getType()
				.getName());
		assertEquals("String", trade.getAttributeByName("id", false).getType().getName());
		assertEquals("String", trade.getAttributeByName("description", false).getType().getName());

		// Check that IRSwap has inherited attributes
		assertNotNull(trade.getAttributeByName("tradeDate", false));
		assertNull(irSwap.getAttributeByName("tradeDate", false));
		assertNotNull(irSwap.getAttributeByName("tradeDate", true));
		assertNull(irSwap.getAttributeByName("tradeheader", false));
		assertNotNull(irSwap.getAttributeByName("tradeheader", true));

		// Check that the enumeration is there
		IClassifier _enum = (IClassifier) model.getElementByName("FixFloatEnum", true);
		assertNotNull(_enum);
		assertEquals(2, _enum.getAttributes(false).size());

		assertTrue(_enum == _enum.getAttributeByName("FIXED", true).getType());
		assertTrue(_enum == _enum.getAttributeByName("FLOAT", true).getType());
		assertEquals(1, _enum.getAttributeByName("FIXED", false).getMinOccurs());
		assertEquals(1, _enum.getAttributeByName("FIXED", false).getMaxOccurs());
		assertEquals(IModelElement.ElementType.Enumeration, _enum.getElementType());

		// Check that supplementary info is identified
		IModelElement str = model.getElementByName("String", true);
		assertNotNull(str);
		assertTrue(str.isSupplementary());

		// Test assignable from
		assertTrue(model.getElementByName("Trade", true).isAssignableFrom(
				model.getElementByName("Trade", true)));
		assertTrue(model.getElementByName("Trade", true).isAssignableFrom(
				model.getElementByName("IRSwap", true)));
		assertTrue(!model.getElementByName("IRSwap", true).isAssignableFrom(
				model.getElementByName("Trade", true)));

		// Test anonymous type
		IClassifier leg = (IClassifier) model.getElementByName("IRLeg", true);
		IAttribute attr = leg.getAttributeByName("ids", false);
		// assertNotNull(attr);
		// assertNotNull(attr.getType());
		//		
		// attr = ((IClassifier)attr.getType()).getAttributeByName("id", false);
		// assertNotNull(attr);
		// assertNotNull(attr.getType());

		// Test anonymous simple type
		leg = (IClassifier) model.getElementByName("IRLeg", true);
		attr = leg.getAttributeByName("name", false);
		assertNotNull(attr);
		assertNotNull(attr.getType());

		// Test attribute character processing (dotted.attribute in the model)
		attr = leg.getAttributeByName("dottedAttribute", false);
		assertNotNull(attr);
		assertNotNull(attr.getType());
	}
	
	@Test
	public void associationsWithOwnedNavigableEndsGenerateAttributes() throws Exception {
		UML2ModelLoader loader = new UML2ModelLoader();

		File file = new File("src/test/resources/uml/associationModel.uml");
		IPackage model = loader.load(getResourceForFile(file));
		IPackage main = (IPackage) model.getElementByName("Main", false);

		assertEquals(2, main.getContents(false).size());
		IClassifier classA = (IClassifier) main.getContents(false).get(0);
		assertEquals("A", classA.getName());
		IClassifier classB = (IClassifier) main.getContents(false).get(1);
		assertEquals("B", classB.getName());
	
		assertEquals(1, classA.getAttributes(true).size());
		assertEquals(1, classB.getAttributes(true).size());

		IAttribute classAClassBEnd = classA.getAttributeByName("class_b_end", false);
		assertEquals("class_b_end", classAClassBEnd.getName());
		assertEquals(classB, classAClassBEnd.getType());
		IAttribute classBClassAEnd = classB.getAttributeByName("class_a_end", false);
		assertEquals("class_a_end", classBClassAEnd.getName());
		assertEquals(classA, classBClassAEnd.getType());
	}

	@Test
	public void associationsWithNoOwnedNavigableEndsGenerateNoAttributes() throws Exception {
		UML2ModelLoader loader = new UML2ModelLoader();

		File file = new File("src/test/resources/uml/associationModelNoOwnedNavigableEnds.uml");
		IPackage model = loader.load(getResourceForFile(file));
		IPackage main = (IPackage) model.getElementByName("Main", false);

		assertEquals(2, main.getContents(false).size());
		IClassifier classA = (IClassifier) main.getContents(false).get(0);
		assertEquals("A", classA.getName());
		IClassifier classB = (IClassifier) main.getContents(false).get(1);
		assertEquals("B", classB.getName());
	
		assertEquals(0, classA.getAttributes(true).size());
		assertEquals(0, classB.getAttributes(true).size());
	}

	@Test
	public void associationsWithOnlyOneEndGenerateNoAttributes() throws Exception {
		UML2ModelLoader loader = new UML2ModelLoader();

		File file = new File("src/test/resources/uml/associationModelOneEnd.uml");
		IPackage model = loader.load(getResourceForFile(file));
		IPackage main = (IPackage) model.getElementByName("Main", false);

		assertEquals(2, main.getContents(false).size());
		IClassifier classA = (IClassifier) main.getContents(false).get(0);
		assertEquals("A", classA.getName());
		IClassifier classB = (IClassifier) main.getContents(false).get(1);
		assertEquals("B", classB.getName());
	
		assertEquals(0, classA.getAttributes(true).size());
		assertEquals(0, classB.getAttributes(true).size());
	}
	
	@Test
	public void associationsWithMoreThanTwoEndsGenerateNoAttributes() throws Exception {
		UML2ModelLoader loader = new UML2ModelLoader();

		File file = new File("src/test/resources/uml/associationModelThreeEnds.uml");
		IPackage model = loader.load(getResourceForFile(file));
		IPackage main = (IPackage) model.getElementByName("Main", false);

		assertEquals(2, main.getContents(false).size());
		IClassifier classA = (IClassifier) main.getContents(false).get(0);
		assertEquals("A", classA.getName());
		IClassifier classB = (IClassifier) main.getContents(false).get(1);
		assertEquals("B", classB.getName());
	
		assertEquals(0, classA.getAttributes(true).size());
		assertEquals(0, classB.getAttributes(true).size());
	}

	@Test
	public void associationsWithMultipleArityGeneratesAttributesWithAppropriateArity() throws Exception {
		UML2ModelLoader loader = new UML2ModelLoader();

		File file = new File("src/test/resources/uml/associationModelWithArity.uml");
		IPackage model = loader.load(getResourceForFile(file));
		IPackage main = (IPackage) model.getElementByName("Main", false);

		assertEquals(2, main.getContents(false).size());
		IClassifier classA = (IClassifier) main.getContents(false).get(0);
		assertEquals("A", classA.getName());
		IClassifier classB = (IClassifier) main.getContents(false).get(1);
		assertEquals("B", classB.getName());
	
		assertEquals(1, classA.getAttributes(true).size());
		assertEquals(1, classB.getAttributes(true).size());

		IAttribute classAClassBEnd = classA.getAttributeByName("class_b_end", false);
		assertEquals(1, classAClassBEnd.getMinOccurs());
		assertEquals(IAttribute.UNBOUNDED, classAClassBEnd.getMaxOccurs());
		IAttribute classBClassAEnd = classB.getAttributeByName("class_a_end", false);
		assertEquals(1, classBClassAEnd.getMinOccurs());
		assertEquals(IAttribute.UNBOUNDED, classBClassAEnd.getMaxOccurs());
	}

	@Test
	public void associationAttributesAreOnlyGeneratedForNavigableEnds() throws Exception {
		UML2ModelLoader loader = new UML2ModelLoader();

		File file = new File("src/test/resources/uml/associationModelWithOneNavigableAndOneNonNavigableEnd.uml");
		IPackage model = loader.load(getResourceForFile(file));
		IPackage main = (IPackage) model.getElementByName("Main", false);

		assertEquals(2, main.getContents(false).size());
		IClassifier classA = (IClassifier) main.getContents(false).get(0);
		assertEquals("A", classA.getName());
		IClassifier classB = (IClassifier) main.getContents(false).get(1);
		assertEquals("B", classB.getName());

		assertEquals(0, classA.getAttributes(true).size());
		assertEquals(1, classB.getAttributes(true).size());
		assertEquals("class_a_end", classB.getAttributes(true).get(0).getName());
	}
	
	@Test
	public void typeNamesAreUsedForAssociationAttributesWithNoNames() throws Exception {
		UML2ModelLoader loader = new UML2ModelLoader();

		File file = new File("src/test/resources/uml/associationModelWithNavigableEndWithNoName.uml");
		IPackage model = loader.load(getResourceForFile(file));
		IPackage main = (IPackage) model.getElementByName("Main", false);

		assertEquals(2, main.getContents(false).size());
		IClassifier classA = (IClassifier) main.getContents(false).get(0);
		assertEquals("class_a", classA.getName());
		IClassifier classB = (IClassifier) main.getContents(false).get(1);
		assertEquals("B", classB.getName());

		assertEquals(1, classB.getAttributes(true).size());
		assertEquals("class_a", classB.getAttributes(true).get(0).getName());
	}
	
	@Test
	public void associationsWithUnTypedEndsAreSkipped() throws Exception {
		UML2ModelLoader loader = new UML2ModelLoader();

		File file = new File("src/test/resources/uml/associationModelWithUntypedEnd.uml");
		IPackage model = loader.load(getResourceForFile(file));
		IPackage main = (IPackage) model.getElementByName("Main", false);

		assertEquals(2, main.getContents(false).size());
		IClassifier classA = (IClassifier) main.getContents(false).get(0);
		assertEquals("A", classA.getName());
		IClassifier classB = (IClassifier) main.getContents(false).get(1);
		assertEquals("B", classB.getName());

		assertEquals(0, classA.getAttributes(true).size());
		assertEquals(0, classB.getAttributes(true).size());
	}

	@Test
	public void associationClassesWithOwnedNavigableEndsGenerateAttributes() throws Exception {
		UML2ModelLoader loader = new UML2ModelLoader();

		File file = new File("src/test/resources/uml/associationClassModel.uml");
		IPackage model = loader.load(getResourceForFile(file));
		IPackage main = (IPackage) model.getElementByName("Main", false);
		assertEquals(3, main.getContents(false).size());

		IClassifier classA = (IClassifier) main.getContents(false).get(0);
		IClassifier classB = (IClassifier) main.getContents(false).get(1);
		IClassifier classAssociationClass = (IClassifier) main.getContents(false).get(2);

		// Class A
		assertEquals("A", classA.getName());
		assertEquals(2, classA.getAttributes(true).size());
		IAttribute classAClassBEnd = classA.getAttributeByName("class_b_end", false);
		assertEquals("class_b_end", classAClassBEnd.getName());
		assertEquals(classB, classAClassBEnd.getType());
		
		IAttribute classAAssociationAttribute = classA.getAttributeByName("AssociationClassName", false);
		assertEquals("AssociationClassName", classAAssociationAttribute.getName());
		assertEquals(classAssociationClass, classAAssociationAttribute.getType());
		assertEquals(1, classAAssociationAttribute.getMaxOccurs());
		assertEquals(1, classAAssociationAttribute.getMinOccurs());
		
		// Class B
		assertEquals("B", classB.getName());
		assertEquals(2, classB.getAttributes(true).size());
		IAttribute classBClassAEnd = classB.getAttributeByName("class_a_end", false);
		assertEquals("class_a_end", classBClassAEnd.getName());
		assertEquals(classA, classBClassAEnd.getType());
	
		IAttribute classBAssociationAttribute = classB.getAttributeByName("AssociationClassName", false);
		assertEquals("AssociationClassName", classBAssociationAttribute.getName());
		assertEquals(classAssociationClass, classBAssociationAttribute.getType());
		assertEquals(1, classBAssociationAttribute.getMaxOccurs());
		assertEquals(1, classBAssociationAttribute.getMinOccurs());

		// Association Class
		assertEquals("AssociationClassName", classAssociationClass.getName());
		assertEquals(3, classAssociationClass.getAttributes(true).size());

		IAttribute classAssociationClassAttribute = classAssociationClass.getAttributeByName("associationClassAttribute", false);
		assertEquals("associationClassAttribute", classAssociationClassAttribute.getName());
		assertNotNull(classAssociationClassAttribute.getType());
		IAttribute classAssociationClassAAttribute = classAssociationClass.getAttributeByName("class_a_end", false);
		assertEquals("class_a_end", classAssociationClassAAttribute.getName());
		assertEquals(classA, classAssociationClassAAttribute.getType());	
		assertEquals(1, classAssociationClassAAttribute.getMinOccurs());
		assertEquals(1, classAssociationClassAAttribute.getMaxOccurs());
		IAttribute classAssociationClassBAttribute = classAssociationClass.getAttributeByName("class_b_end", false);
		assertEquals("class_b_end", classAssociationClassBAttribute.getName());
		assertEquals(classB, classAssociationClassBAttribute.getType());
		assertEquals(1, classAssociationClassBAttribute.getMinOccurs());
		assertEquals(1, classAssociationClassBAttribute.getMaxOccurs());
	}

	@Test
	public void associationClassesTwoArityOnOwnedEndAGenerateAttributes() throws Exception {
		UML2ModelLoader loader = new UML2ModelLoader();

		File file = new File("src/test/resources/uml/associationClassModelWithPluralityOnA.uml");
		IPackage model = loader.load(getResourceForFile(file));
		IPackage main = (IPackage) model.getElementByName("Main", false);
		assertEquals(3, main.getContents(false).size());

		IClassifier classA = (IClassifier) main.getContents(false).get(0);
		IClassifier classB = (IClassifier) main.getContents(false).get(1);
		IClassifier classAssociationClass = (IClassifier) main.getContents(false).get(2);

		// Class A
		IAttribute classAAssociationAttribute = classA.getAttributeByName("AssociationClassName", false);
		assertEquals(2, classAAssociationAttribute.getMaxOccurs());
		assertEquals(1, classAAssociationAttribute.getMinOccurs());
		
		// Class B
		IAttribute classBAssociationAttribute = classB.getAttributeByName("AssociationClassName", false);
		assertEquals(1, classBAssociationAttribute.getMaxOccurs());
		assertEquals(1, classBAssociationAttribute.getMinOccurs());

		// Association Class
		IAttribute classAssociationClassAAttribute = classAssociationClass.getAttributeByName("class_a_end", false);
		assertEquals("class_a_end", classAssociationClassAAttribute.getName());
		assertEquals(classA, classAssociationClassAAttribute.getType());	
		assertEquals(1, classAssociationClassAAttribute.getMinOccurs());
		assertEquals(1, classAssociationClassAAttribute.getMaxOccurs());
		IAttribute classAssociationClassBAttribute = classAssociationClass.getAttributeByName("class_b_end", false);
		assertEquals("class_b_end", classAssociationClassBAttribute.getName());
		assertEquals(classB, classAssociationClassBAttribute.getType());
		assertEquals(1, classAssociationClassBAttribute.getMinOccurs());
		assertEquals(1, classAssociationClassBAttribute.getMaxOccurs());
	}
	
	@Test
	public void typeNamesAreUsedForAssociationClassMemberEndAttributesWithNoNames() throws Exception {
		UML2ModelLoader loader = new UML2ModelLoader();

		File file = new File("src/test/resources/uml/associationClassModelWithMemberEndWithNoName.uml");
		IPackage model = loader.load(getResourceForFile(file));
		IPackage main = (IPackage) model.getElementByName("Main", false);

		assertEquals(3, main.getContents(false).size());
		IClassifier associationClass = (IClassifier) main.getContents(false).get(2);
		assertEquals("AssociationClassName", associationClass.getName());
		IAttribute classAssociationClassAAttribute = associationClass.getAttributes(false).get(1);
		assertEquals("class_a", classAssociationClassAAttribute.getName());
		assertEquals("class_a", classAssociationClassAAttribute.getType().getName());	
	}	

	@Test
	public void associationClassesDoNotMapUntypedEnds() throws Exception {
		UML2ModelLoader loader = new UML2ModelLoader();

		File file = new File("src/test/resources/uml/associationClassModelWithUntypedEnd.uml");
		IPackage model = loader.load(getResourceForFile(file));
		IPackage main = (IPackage) model.getElementByName("Main", false);

		assertEquals(3, main.getContents(false).size());
		IClassifier classA = (IClassifier) main.getContents(false).get(0);
		assertEquals("A", classA.getName());
		IClassifier classB = (IClassifier) main.getContents(false).get(1);
		assertEquals("B", classB.getName());
		IClassifier associationClass = (IClassifier) main.getContents(false).get(2);
		assertEquals("AssociationClassName", associationClass.getName());
		assertEquals(2, associationClass.getAttributes(false).size());
		assertEquals("associationClassAttribute", associationClass.getAttributes(false).get(0).getName());
		assertEquals("class_b_end", associationClass.getAttributes(false).get(1).getName());
	}
}

