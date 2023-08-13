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

import java.io.File;

import junit.framework.TestCase;
import net.sourceforge.nrl.parser.model.IAttribute;
import net.sourceforge.nrl.parser.model.IClassifier;
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.model.IPackage;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;

/**
 * Test loading of complex types.
 * 
 * @author Christian Nentwich
 */
public class ComplexTypesTest extends TestCase {

	private static boolean isInitialised = false;

	private static IPackage complexModel = null, anonymousModel = null;

	@SuppressWarnings("deprecation")
	public void setUp() throws Exception {
		if (!isInitialised) {
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd",
					new XSDResourceFactoryImpl());
			isInitialised = true;

			XSDModelLoader loader = new XSDModelLoader();
			complexModel = loader.load(new File("src/test/resources/schema/complextypes.xsd"));

			loader = new XSDModelLoader();
			anonymousModel = loader.load(new File(
					"src/test/resources/schema/anonymous-nameclashes.xsd"));
		}
	}

	/*
	 * Just a double-check that the model loaded
	 */
	public void testLoading() throws Exception {
		assertNotNull(complexModel);
	}

	/*
	 * Make sure an "Object" root type was introduced.
	 */
	public void testObjectType() throws Exception {
		IClassifier obj = (IClassifier) complexModel.getElementByName("Object", true);
		assertNotNull(obj);
	}

	/*
	 * Test a flat complex type with a content model based on a sequence.
	 */
	public void testComplexTypeFlat() throws Exception {
		IClassifier classifier = (IClassifier) complexModel.getElementByName("Basic", true);
		assertEquals(IModelElement.ElementType.Classifier, classifier.getElementType());

		assertNotNull(classifier);
		assertTrue(!classifier.isEnumeration());
		assertEquals("Object", classifier.getParent().getName());

		assertNotNull(classifier.getAttributeByName("id", false));
		assertEquals(0, classifier.getAttributeByName("id", false).getMinOccurs());
		assertEquals(1, classifier.getAttributeByName("id", false).getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("id", false).getType().getName());

		assertNotNull(classifier.getAttributeByName("simple", false));
		assertEquals(0, classifier.getAttributeByName("simple", false).getMinOccurs());
		assertEquals(1, classifier.getAttributeByName("simple", false).getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("simple", false).getType().getName());

		assertNotNull(classifier.getAttributeByName("mySimple", false));
		assertEquals(1, classifier.getAttributeByName("mySimple", false).getMinOccurs());
		assertEquals(1, classifier.getAttributeByName("mySimple", false).getMaxOccurs());
		assertEquals("MySimpleType", classifier.getAttributeByName("mySimple", false).getType()
				.getName());

		assertNotNull(classifier.getAttributeByName("anotherSimple", false));
		assertEquals(1, classifier.getAttributeByName("anotherSimple", false).getMinOccurs());
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("anotherSimple", false)
				.getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("anotherSimple", false).getType()
				.getName());
	}

	public void testComplexTypeNesting() throws Exception {
		IClassifier classifier = (IClassifier) complexModel.getElementByName("NestedA", true);

		assertNotNull(classifier);
		assertTrue(!classifier.isEnumeration());

		assertNotNull(classifier.getAttributeByName("childA", false));
		assertEquals(1, classifier.getAttributeByName("childA", false).getMinOccurs());
		assertEquals(1, classifier.getAttributeByName("childA", false).getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("childA", false).getType().getName());

		assertNotNull(classifier.getAttributeByName("childB", false));
		assertEquals(0, classifier.getAttributeByName("childB", false).getMinOccurs());
		assertEquals(1, classifier.getAttributeByName("childB", false).getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("childB", false).getType().getName());

		assertNotNull(classifier.getAttributeByName("childC", false));
		assertEquals(0, classifier.getAttributeByName("childC", false).getMinOccurs());
		assertEquals(1, classifier.getAttributeByName("childC", false).getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("childC", false).getType().getName());

		assertNotNull(classifier.getAttributeByName("childD", false));
		assertEquals(1, classifier.getAttributeByName("childD", false).getMinOccurs());
		assertEquals(1, classifier.getAttributeByName("childD", false).getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("childD", false).getType().getName());

		assertNotNull(classifier.getAttributeByName("childE", false));
		assertEquals(1, classifier.getAttributeByName("childE", false).getMinOccurs());
		assertEquals(1, classifier.getAttributeByName("childE", false).getMaxOccurs());
		assertEquals("double", classifier.getAttributeByName("childE", false).getType().getName());

		assertNotNull(classifier.getAttributeByName("childF", false));
		assertEquals(1, classifier.getAttributeByName("childF", false).getMinOccurs());
		assertEquals(1, classifier.getAttributeByName("childF", false).getMaxOccurs());
		assertEquals("int", classifier.getAttributeByName("childF", false).getType().getName());

		assertNotNull(classifier.getAttributeByName("childG", false));
		assertEquals(1, classifier.getAttributeByName("childG", false).getMinOccurs());
		assertEquals(1, classifier.getAttributeByName("childG", false).getMaxOccurs());
		assertEquals("long", classifier.getAttributeByName("childG", false).getType().getName());
	}

	public void testComplexAnonymousSubtype() throws Exception {
		IClassifier classifier = (IClassifier) complexModel.getElementByName(
				"WithAnonymousComplex", true);
		assertEquals(IModelElement.ElementType.Classifier, classifier.getElementType());

		assertNotNull(classifier);
		assertTrue(!classifier.isEnumeration());

		assertNotNull(classifier.getAttributeByName("simple", false));
		assertEquals(1, classifier.getAttributeByName("simple", false).getMinOccurs());
		assertEquals(1, classifier.getAttributeByName("simple", false).getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("simple", false).getType().getName());

		assertNotNull(classifier.getAttributeByName("anonymousComplex", false));
		assertEquals(1, classifier.getAttributeByName("anonymousComplex", false).getMinOccurs());
		assertEquals(1, classifier.getAttributeByName("anonymousComplex", false).getMaxOccurs());

		assertNotNull(complexModel.getElementByName("AnonymousComplex", true));
		IClassifier type = (IClassifier) classifier.getAttributeByName("anonymousComplex", false)
				.getType();
		assertEquals("AnonymousComplex", type.getName());
		assertEquals("AnonymousComplex", type.getOriginalName());
		assertEquals("Basic", type.getParent().getName());

		assertNotNull(type.getAttributeByName("simple", true));
		assertNotNull(type.getAttributeByName("mySimple", true));
		assertNotNull(type.getAttributeByName("anotherSimple", true));
		assertNotNull(type.getAttributeByName("test", true));

		assertNotNull(type.getUserData(IXSDUserData.CONTAINING_TYPE));
		assertTrue(type.getUserData(IXSDUserData.CONTAINING_TYPE) == classifier);

		assertNotNull(complexModel.getElementByName("AnonymousComplex2", true));
		type = (IClassifier) classifier.getAttributeByName("anonymousComplex2", false).getType();
		assertEquals("AnonymousComplex2", type.getName());
		assertEquals("AnonymousComplex2", type.getOriginalName());
		assertEquals("Object", type.getParent().getName());

		assertNotNull(type.getAttributeByName("childA", true));
		assertNotNull(type.getAttributeByName("childB", true));

		assertNotNull(type.getUserData(IXSDUserData.CONTAINING_TYPE));
		assertTrue(type.getUserData(IXSDUserData.CONTAINING_TYPE) == classifier);

		classifier = (IClassifier) complexModel.getElementByName("WithAnonymousComplex2", true);
		assertNotNull(classifier.getAttributeByName("anonymousComplex", false));
		type = (IClassifier) classifier.getAttributeByName("anonymousComplex", false).getType();
		assertEquals("AnonymousComplex1", type.getName());
		assertEquals("AnonymousComplex", type.getOriginalName());
		assertEquals("NestedA", type.getParent().getName());

		// Deeply nested
		classifier = (IClassifier) complexModel.getElementByName("AnonymousNested", true);
		assertNotNull(classifier);
		assertNotNull(classifier.getUserData(IXSDUserData.CONTAINING_TYPE));
		assertEquals("AnonymousComplex", ((IClassifier) classifier
				.getUserData(IXSDUserData.CONTAINING_TYPE)).getName());
		assertEquals("WithAnonymousComplex", ((IClassifier) ((IClassifier) classifier
				.getUserData(IXSDUserData.CONTAINING_TYPE))
				.getUserData(IXSDUserData.CONTAINING_TYPE)).getName());

		// anonymous complex type derived from simple
		classifier = (IClassifier) complexModel.getElementByName("WithDerivedAnonymousComplex",
				true);
		assertNotNull(classifier);
		assertNotNull(classifier.getAttributeByName("anonymousComplexElement1", false));
		assertEquals("AnonymousComplexElement1", classifier.getAttributeByName(
				"anonymousComplexElement1", false).getType().getName());

		classifier = (IClassifier) complexModel.getElementByName("AnonymousComplexElement1", true);
		assertNotNull(classifier);
		assertNotNull(classifier.getParent());
		assertEquals("normalizedString", classifier.getParent().getName());
		assertNotNull(classifier.getAttributeByName("simpleContentAttribute", false));

		classifier = (IClassifier) complexModel.getElementByName("AnonymousComplexElement2", true);
		assertNotNull(classifier);
		assertNotNull(classifier.getParent());
		assertEquals("AnotherComplexType", classifier.getParent().getName());
		assertNotNull(classifier.getAttributeByName("complexContentAttribute", false));
	}

	public void testSimpleAnonymousSubtype() throws Exception {
		IClassifier classifier = (IClassifier) complexModel.getElementByName("WithAnonymousSimple",
				true);

		assertNotNull(classifier);
		assertTrue(!classifier.isEnumeration());

		assertNotNull(classifier.getAttributeByName("facetted", false));
		assertEquals("string", classifier.getAttributeByName("facetted", false).getType().getName());

		assertNotNull(classifier.getAttributeByName("enumerated", false));
		assertEquals("string", classifier.getAttributeByName("enumerated", false).getType()
				.getName());

		assertNotNull(classifier.getAttributeByName("internal", false));
		assertEquals("string", classifier.getAttributeByName("internal", false).getType().getName());
	}

	public void testRepeatingParticlesWithReferenceToGlobalElement() {
		IClassifier classifier;
		// With reference to global element
		classifier = (IClassifier) complexModel.getElementByName("RepeatingParticle9", true);
		assertNotNull(classifier);
		assertEquals(2, classifier.getAttributes(false).size());

		assertNotNull(classifier.getAttributeByName("element", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("element", false)
				.getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("element", false).getType().getName());
		assertEquals("elementOrB", classifier.getAttributeByName("element", false).getUserData(
				IXSDUserData.JAXB_FLATTENED_PROPERTY));

		assertNotNull(classifier.getAttributeByName("b", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("b", false).getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("b", false).getType().getName());
		assertEquals("elementOrB", classifier.getAttributeByName("b", false).getUserData(
				IXSDUserData.JAXB_FLATTENED_PROPERTY));
	}

	public void testRepeatingParticlesMoreElementContentWithSpuriousSequences() {
		IClassifier classifier;
		// More element content, with spurious sequences
		classifier = (IClassifier) complexModel.getElementByName("RepeatingParticle8", true);
		assertNotNull(classifier);
		assertEquals(6, classifier.getAttributes(false).size());

		assertNotNull(classifier.getAttributeByName("A", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("A", false).getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("A", false).getType().getName());
		assertEquals("aAndBAndC", classifier.getAttributeByName("A", false).getUserData(
				IXSDUserData.JAXB_FLATTENED_PROPERTY));

		assertNotNull(classifier.getAttributeByName("B", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("B", false).getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("B", false).getType().getName());
		assertEquals("aAndBAndC", classifier.getAttributeByName("B", false).getUserData(
				IXSDUserData.JAXB_FLATTENED_PROPERTY));

		assertNotNull(classifier.getAttributeByName("C", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("C", false).getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("C", false).getType().getName());
		assertEquals("aAndBAndC", classifier.getAttributeByName("C", false).getUserData(
				IXSDUserData.JAXB_FLATTENED_PROPERTY));

		assertNotNull(classifier.getAttributeByName("elementA", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("elementA", false)
				.getMaxOccurs());
		assertEquals("Basic", classifier.getAttributeByName("elementA", false).getType().getName());
		assertEquals("aAndBAndC", classifier.getAttributeByName("elementA", false).getUserData(
				IXSDUserData.JAXB_FLATTENED_PROPERTY));

		assertNotNull(classifier.getAttributeByName("elementB", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("elementB", false)
				.getMaxOccurs());
		assertEquals("MySimpleType", classifier.getAttributeByName("elementB", false).getType()
				.getName());
		assertEquals("aAndBAndC", classifier.getAttributeByName("elementB", false).getUserData(
				IXSDUserData.JAXB_FLATTENED_PROPERTY));

		assertNotNull(classifier.getAttributeByName("elementC", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("elementC", false)
				.getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("elementC", false).getType().getName());
		assertEquals("aAndBAndC", classifier.getAttributeByName("elementC", false).getUserData(
				IXSDUserData.JAXB_FLATTENED_PROPERTY));
	}

	public void testRepeatingParticlesWithMoreThanTwoElementsWithSpuriousParticles() {
		IClassifier classifier;
		// More than two elements, with spurious particles
		classifier = (IClassifier) complexModel.getElementByName("RepeatingParticle7", true);
		assertNotNull(classifier);

		assertNotNull(classifier.getAttributeByName("elementA", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("elementA", false)
				.getMaxOccurs());
		assertEquals("Basic", classifier.getAttributeByName("elementA", false).getType().getName());
		assertEquals("elementAOrElementBAndElementC", classifier.getAttributeByName("elementA",
				false).getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));

		assertNotNull(classifier.getAttributeByName("elementB", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("elementB", false)
				.getMaxOccurs());
		assertEquals("MySimpleType", classifier.getAttributeByName("elementB", false).getType()
				.getName());
		assertEquals("elementAOrElementBAndElementC", classifier.getAttributeByName("elementB",
				false).getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));

		assertNotNull(classifier.getAttributeByName("elementC", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("elementC", false)
				.getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("elementC", false).getType().getName());
		assertEquals("elementAOrElementBAndElementC", classifier.getAttributeByName("elementC",
				false).getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));

	}

	public void testRepeatingParticlesMoreThanTwoElementsWithinARepeatingSequence() {
		IClassifier classifier;
		// More than two elements within a repeating sequence
		classifier = (IClassifier) complexModel.getElementByName("RepeatingParticle6", true);
		assertNotNull(classifier);
		assertEquals(4, classifier.getAttributes(false).size());

		assertNotNull(classifier.getAttributeByName("elementA", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("elementA", false)
				.getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("elementA", false).getType().getName());
		assertEquals("elementAAndElementBOrElementC", classifier.getAttributeByName("elementA",
				false).getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));

		assertNotNull(classifier.getAttributeByName("elementB", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("elementB", false)
				.getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("elementB", false).getType().getName());
		assertEquals("elementAAndElementBOrElementC", classifier.getAttributeByName("elementB",
				false).getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));

		assertNotNull(classifier.getAttributeByName("elementC", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("elementC", false)
				.getMaxOccurs());
		assertEquals("Basic", classifier.getAttributeByName("elementC", false).getType().getName());
		assertEquals("elementAAndElementBOrElementC", classifier.getAttributeByName("elementC",
				false).getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));

		assertNotNull(classifier.getAttributeByName("elementD", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("elementD", false)
				.getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("elementD", false).getType().getName());
		assertEquals("elementAAndElementBOrElementC", classifier.getAttributeByName("elementD",
				false).getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));
	}

	public void testRepeatingParticlesChoiceOfSameComplexType() {
		IClassifier classifier;
		// Choice of the same complex type
		classifier = (IClassifier) complexModel.getElementByName("RepeatingParticle5", true);
		assertNotNull(classifier);

		assertNotNull(classifier.getAttributeByName("elementA", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("elementA", false)
				.getMaxOccurs());
		assertEquals("Basic", classifier.getAttributeByName("elementA", false).getType().getName());
		assertEquals("elementAOrElementB", classifier.getAttributeByName("elementA", false)
				.getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));

		assertNotNull(classifier.getAttributeByName("elementB", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("elementB", false)
				.getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("elementB", false).getType().getName());
		assertEquals("elementAOrElementB", classifier.getAttributeByName("elementB", false)
				.getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));
	}

	public void testRepeatingParticlesSequenceOfSameComplexType() {
		IClassifier classifier;
		// Sequence of same complex type
		classifier = (IClassifier) complexModel.getElementByName("RepeatingParticle4", true);
		assertNotNull(classifier);

		assertNotNull(classifier.getAttributeByName("elementA", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("elementA", false)
				.getMaxOccurs());
		assertEquals("Basic", classifier.getAttributeByName("elementA", false).getType().getName());
		assertEquals("elementAAndElementB", classifier.getAttributeByName("elementA", false)
				.getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));

		assertNotNull(classifier.getAttributeByName("elementB", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("elementB", false)
				.getMaxOccurs());
		assertEquals("Basic", classifier.getAttributeByName("elementB", false).getType().getName());
		assertEquals("elementAAndElementB", classifier.getAttributeByName("elementB", false)
				.getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));
	}

	public void testRepeatingParticlesWithSequenceOfDifferentAnonymousTypes() {
		IClassifier classifier;
		// Sequence of different anonymous types
		classifier = (IClassifier) complexModel.getElementByName("RepeatingParticle3b", true);
		assertNotNull(classifier);

		assertEquals(2, classifier.getAttributes(false).size());
		assertNotNull(classifier.getAttributeByName("elementA", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("elementA", false)
				.getMaxOccurs());
		assertEquals("ElementA", classifier.getAttributeByName("elementA", false).getType()
				.getName());
		assertEquals("elementAAndElementB", classifier.getAttributeByName("elementA", false)
				.getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));

		assertNotNull(classifier.getAttributeByName("elementB", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("elementB", false)
				.getMaxOccurs());
		assertEquals("ElementB", classifier.getAttributeByName("elementB", false).getType()
				.getName());
		assertEquals("elementAAndElementB", classifier.getAttributeByName("elementB", false)
				.getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));
	}

	public void testRepeatingParticlesWithSequenceOfMixedTypeElements() {
		IClassifier classifier;
		// Sequence of mixed type elements
		classifier = (IClassifier) complexModel.getElementByName("RepeatingParticle3", true);
		assertNotNull(classifier);

		assertEquals(2, classifier.getAttributes(false).size());
		assertNotNull(classifier.getAttributeByName("elementA", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("elementA", false)
				.getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("elementA", false).getType().getName());
		assertEquals("elementAAndElementB", classifier.getAttributeByName("elementA", false)
				.getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));

		assertNotNull(classifier.getAttributeByName("elementB", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("elementB", false)
				.getMaxOccurs());
		assertEquals("Basic", classifier.getAttributeByName("elementB", false).getType().getName());
		assertEquals("elementAAndElementB", classifier.getAttributeByName("elementB", false)
				.getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY));
	}

	/*
	 * public void testRepeatingParticlesWithModelGroup() { IClassifier classifier; // Repeating
	 * choice that uses global model groups classifier = (IClassifier)
	 * model.getElementByName("RepeatingParticle10", true); assertNotNull(classifier);
	 * assertNotNull(classifier.getAttributeByName("y", false));
	 * assertNotNull(classifier.getAttributeByName("x", false));
	 * assertNotNull(classifier.getAttributeByName("elementA", false));
	 * assertNotNull(classifier.getAttributeByName("elementB", false));
	 * assertNotNull(classifier.getAttributeByName("elementC", false));
	 * assertNotNull(classifier.getAttributeByName("elementD", false));
	 * assertNotNull(classifier.getAttributeByName("test", false)); for (IAttribute attr :
	 * classifier.getAttributes(false)) { assertEquals("Attribute " + attr.getName() +
	 * " is missing JAXB property", "yOrXAndElementA",
	 * attr.getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY)); }
	 * 
	 * // Again - different nesting classifier = (IClassifier)
	 * model.getElementByName("RepeatingParticle11", true); assertNotNull(classifier);
	 * assertNotNull(classifier.getAttributeByName("x", false));
	 * assertNotNull(classifier.getAttributeByName("elementA", false));
	 * assertNotNull(classifier.getAttributeByName("elementB", false));
	 * assertNotNull(classifier.getAttributeByName("elementC", false));
	 * assertNotNull(classifier.getAttributeByName("elementD", false));
	 * assertNotNull(classifier.getAttributeByName("test", false)); for (IAttribute attr :
	 * classifier.getAttributes(false)) { assertEquals("Attribute " + attr.getName() +
	 * " is missing JAXB property", "xAndElementAAndElementB", attr
	 * .getUserData(IXSDUserData.JAXB_FLATTENED_PROPERTY)); } }
	 */

	public void testSimpleRepeatingSequenceComplexType() {
		IClassifier classifier;
		// Simple repeating sequence, complex type
		classifier = (IClassifier) complexModel.getElementByName("RepeatingParticle2", true);
		assertNotNull(classifier);

		assertEquals(1, classifier.getAttributes(false).size());
		assertNotNull(classifier.getAttributeByName("elementA", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("elementA", false)
				.getMaxOccurs());
		assertEquals("Basic", classifier.getAttributeByName("elementA", false).getType().getName());
	}

	public void testSimpleRepeatingSequenceSpuriousIntermediateParticles() {
		IClassifier classifier;
		classifier = (IClassifier) complexModel.getElementByName("RepeatingParticle1b", true);
		assertNotNull(classifier);

		assertEquals(1, classifier.getAttributes(false).size());
		assertNotNull(classifier.getAttributeByName("elementA", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("elementA", false)
				.getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("elementA", false).getType().getName());
	}

	public void testSimpleRepeatingParticles() {
		// Simple repeating sequence
		IClassifier classifier = (IClassifier) complexModel.getElementByName("RepeatingParticle1",
				true);
		assertNotNull(classifier);

		assertEquals(1, classifier.getAttributes(false).size());
		assertNotNull(classifier.getAttributeByName("elementA", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("elementA", false)
				.getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("elementA", false).getType().getName());
	}

	public void testNoIntermediateChoiceSequenceContainersCreated() throws Exception {
		assertNull("Intermediate element found", complexModel.getElementByName(
				"ElementAAndElementB", true));
		assertNull("Intermediate element found", complexModel.getElementByName(
				"ElementAAndElementBOrElementC", true));
		assertNull("Intermediate element found", complexModel.getElementByName(
				"ElementAOrElementBAndElementC", true));
		assertNull("Intermediate element found", complexModel.getElementByName("AAndBAndC", true));
	}

	public void testAnonymousTypeNameClashResolved() throws Exception {
		IClassifier classifier = (IClassifier) anonymousModel.getElementByName("TypeNameB", true);
		assertNotNull(classifier);
		assertNotNull(classifier.getAttributeByName("elementA", true));
		assertNotNull(classifier.getAttributeByName("elementB", true));

		classifier = (IClassifier) anonymousModel.getElementByName("TypeNameA", true);
		assertNotNull(classifier);

		IAttribute attr = classifier.getAttributeByName("typeNameB", true);
		assertNotNull(attr.getName());
		classifier = (IClassifier) attr.getType();
		assertNotNull(classifier.getAttributeByName("elementC", true));
		assertNotNull(classifier.getAttributeByName("elementD", true));

		classifier = (IClassifier) anonymousModel.getElementByName("ElementName", true);
		assertNotNull(classifier);

		attr = classifier.getAttributeByName("typeName", true);
		assertNotNull(attr.getName());
		classifier = (IClassifier) attr.getType();
		assertNotNull(classifier.getAttributeByName("elementE", true));
		assertNotNull(classifier.getAttributeByName("elementF", true));
	}

	public void testAnonymousTypeNameClashWithIncludeResolved() throws Exception {
		IClassifier classifier = (IClassifier) anonymousModel.getElementByName("TypeNameD", true);
		assertNotNull(classifier);

		IAttribute attr = classifier.getAttributeByName("typeNameC", true);
		assertNotNull(attr.getName());
		classifier = (IClassifier) attr.getType();
		assertNotNull(classifier.getAttributeByName("attr2", true));

		classifier = (IClassifier) anonymousModel.getElementByName("SuperType", true);
		assertNotNull(classifier);

		attr = classifier.getAttributeByName("typeNameC", true);
		assertNotNull(attr.getName());
		classifier = (IClassifier) attr.getType();
		assertNotNull(classifier.getAttributeByName("attr", true));
	}

	public void testElementsWithSameNameInDifferentNamespacesAreDisambiguated1() {
		IClassifier classifier = (IClassifier) complexModel.getElementByName(
				"DuplicateElementsDifferentNamespaces1", true);
		assertNotNull(classifier);

		assertEquals(2, classifier.getAttributes(false).size());
		assertNotNull(classifier.getAttributeByName("element1", false));
		assertEquals(1, classifier.getAttributeByName("element1", false).getMaxOccurs());
		assertEquals("element1", classifier.getAttributeByName("element1", false).getType().getName());
		assertNull(classifier.getAttributeByName("element1", false).getUserData(
				IXSDUserData.JAXB_FLATTENED_PROPERTY));

		assertNotNull(classifier.getAttributeByName("element12", false));
		assertEquals(1, classifier.getAttributeByName("element12", false).getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("element12", false).getType().getName());
		assertNull(classifier.getAttributeByName("element12", false).getUserData(
				IXSDUserData.JAXB_FLATTENED_PROPERTY));
	}

	public void testElementsWithSameNameInDifferentNamespacesAreDisambiguated2() {
		IClassifier classifier = (IClassifier) complexModel.getElementByName(
				"DuplicateElementsDifferentNamespaces2", true);
		assertNotNull(classifier);
		
		assertEquals(2, classifier.getAttributes(false).size());
		assertNotNull(classifier.getAttributeByName("element12", false));
		assertEquals(1, classifier.getAttributeByName("element12", false).getMaxOccurs());
		assertEquals("element1", classifier.getAttributeByName("element12", false).getType().getName());
		assertNull(classifier.getAttributeByName("element12", false).getUserData(
				IXSDUserData.JAXB_FLATTENED_PROPERTY));
		
		assertNotNull(classifier.getAttributeByName("element1", false));
		assertEquals(1, classifier.getAttributeByName("element1", false).getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("element1", false).getType().getName());
		assertNull(classifier.getAttributeByName("element1", false).getUserData(
				IXSDUserData.JAXB_FLATTENED_PROPERTY));
	}
	
	public void testElementsWithSameNameInDifferentNamespacesAreDisambiguated3() {
		IClassifier classifier = (IClassifier) complexModel.getElementByName(
				"DuplicateElementsDifferentNamespaces3", true);
		assertNotNull(classifier);
		
		assertEquals(2, classifier.getAttributes(false).size());
		
		assertNotNull(classifier.getAttributeByName("element1", false));
		assertEquals(1, classifier.getAttributeByName("element1", false).getMaxOccurs());
		assertEquals("element1", classifier.getAttributeByName("element1", false).getType().getName());
		assertNull(classifier.getAttributeByName("element1", false).getUserData(
				IXSDUserData.JAXB_FLATTENED_PROPERTY));
		
		assertNotNull(classifier.getAttributeByName("element12", false));
		assertEquals(1, classifier.getAttributeByName("element12", false).getMaxOccurs());
		assertEquals("element1", classifier.getAttributeByName("element12", false).getType().getName());
		assertNull(classifier.getAttributeByName("element12", false).getUserData(
				IXSDUserData.JAXB_FLATTENED_PROPERTY));
	}
	
	public void testRepeatingParticleWithModelGroupReferencesDetectedAsFlattenedProperty() {
		IClassifier classifier = (IClassifier) complexModel.getElementByName(
				"RepeatingSequenceWithModelGroupReference", true);
		assertNotNull(classifier);
		
		assertEquals(2, classifier.getAttributes(false).size());
		
		assertNotNull(classifier.getAttributeByName("element1", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("element1", false).getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("element1", false).getType().getName());
		assertEquals("element1OrElement2", classifier.getAttributeByName("element1", false).getUserData(
				IXSDUserData.JAXB_FLATTENED_PROPERTY));
		
		assertNotNull(classifier.getAttributeByName("element2", false));
		assertEquals(IAttribute.UNBOUNDED, classifier.getAttributeByName("element2", false).getMaxOccurs());
		assertEquals("string", classifier.getAttributeByName("element2", false).getType().getName());
		assertEquals("element1OrElement2", classifier.getAttributeByName("element2", false).getUserData(
				IXSDUserData.JAXB_FLATTENED_PROPERTY));
	}

}
