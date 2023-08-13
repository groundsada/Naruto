package net.sourceforge.nrl.parser.model.xsd;

import static net.sourceforge.nrl.parser.model.xsd.IXSDUserData.ATTRIBUTE_KIND;
import static net.sourceforge.nrl.parser.model.xsd.IXSDUserData.NAMESPACE;
import static net.sourceforge.nrl.parser.model.xsd.IXSDUserData.XSD_ATTRIBUTE_KIND;
import static net.sourceforge.nrl.parser.model.xsd.IXSDUserData.XSD_ELEMENT_KIND;
import static net.sourceforge.nrl.parser.model.xsd.IXSDUserData.XSD_GLOBAL_ELEMENT;

import java.io.File;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import net.sourceforge.nrl.parser.model.IAttribute;
import net.sourceforge.nrl.parser.model.IClassifier;
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.model.IPackage;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;

/** Test meta-data added to XSDAttributes */
public class MetaDataTest extends TestCase {

	private static boolean isInitialised = false;

	private static IPackage model = null;

	private static IPackage simpleTypeModel = null;

	private static IPackage complexTypeModel = null;

	private static IPackage globalElementModel = null;

	@SuppressWarnings("deprecation")
	public void setUp() throws Exception {
		if (!isInitialised) {
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd",
					new XSDResourceFactoryImpl());
			isInitialised = true;

			XSDModelLoader loader = new XSDModelLoader();
			model = loader.load(new File("src/test/resources/schema/metadata.xsd"));
			simpleTypeModel = loader.load(new File("src/test/resources/schema/simpletypes.xsd"));
			complexTypeModel = loader.load(new File("src/test/resources/schema/complextypes.xsd"));
			globalElementModel = loader.load(new File(
					"src/test/resources/schema/globalelements.xsd"));

			for (String warning : loader.getWarnings()) {
				System.err.println(warning);
			}
		}
	}

	public void testXSDAttributeKind() throws Exception {
		IClassifier parent = (IClassifier) model.getElementByName("aTypeOne", true);
		IAttribute attribute = parent.getAttributeByName("aTypeOneAttributeOne", false);
		assertEquals(XSD_ATTRIBUTE_KIND, attribute.getUserData(ATTRIBUTE_KIND));

		// Simple Types!
		IClassifier usage = (IClassifier) simpleTypeModel.getElementByName("Usage", true);
		assertTaggedAsAttribute(usage.getAttributeByName("anonymousA", false));

		IClassifier aCDU = (IClassifier) simpleTypeModel.getElementByName(
				"AnonymousComplexDerivUsage", true);
		IClassifier dSA = (IClassifier) aCDU.getAttributeByName("derivedSimpleAnonymous", false)
				.getType();
		assertTaggedAsAttribute(dSA.getAttributeByName("test", false));
		IClassifier dSA2 = (IClassifier) aCDU.getAttributeByName("derivedSimpleAnonymous2", false)
				.getType();
		assertTaggedAsAttribute(dSA2.getAttributeByName("listAttr", false));

		IClassifier ewa = (IClassifier) simpleTypeModel.getElementByName("EnumerationWithAttr",
				true);
		assertTaggedAsAttribute(ewa.getAttributeByName("id", false));

		// Complex Types!
		IClassifier basic = (IClassifier) complexTypeModel.getElementByName("Basic", true);
		assertTaggedAsAttribute(basic.getAttributeByName("id", false));

		IClassifier withDerivedAnonymousComplex = (IClassifier) complexTypeModel.getElementByName(
				"WithDerivedAnonymousComplex", true);
		XSDAttribute anonymousComplexElement1 = (XSDAttribute) withDerivedAnonymousComplex
				.getAttributeByName("anonymousComplexElement1", true);
		assertTaggedAsAttribute(((IClassifier) (anonymousComplexElement1.getType()))
				.getAttributeByName("simpleContentAttribute", false));
		XSDAttribute anonymousComplexElement2 = (XSDAttribute) withDerivedAnonymousComplex
				.getAttributeByName("anonymousComplexElement2", true);
		assertTaggedAsAttribute(((IClassifier) (anonymousComplexElement2.getType()))
				.getAttributeByName("complexContentAttribute", false));
	}

	public void testXSDElementKind() throws Exception {
		IClassifier parent = (IClassifier) model.getElementByName("aTypeOne", true);
		IAttribute attribute = parent.getAttributeByName("aTypeOneElementOne", false);
		assertEquals(XSD_ELEMENT_KIND, attribute.getUserData(ATTRIBUTE_KIND));

		// Simple types!

		IClassifier usage = (IClassifier) simpleTypeModel.getElementByName("Usage", true);
		assertTaggedAsElement(usage.getAttributeByName("astring", false));
		assertTaggedAsElement(usage.getAttributeByName("atoken", false));
		assertTaggedAsElement(usage.getAttributeByName("anumber", false));
		assertTaggedAsElement(usage.getAttributeByName("adate", false));
		assertTaggedAsElement(usage.getAttributeByName("derivedString", false));
		assertTaggedAsElement(usage.getAttributeByName("derivedComplex", false));
		assertTaggedAsElement(usage.getAttributeByName("anonymousB", false));
		assertTaggedAsElement(usage.getAttributeByName("anonymousC", false));
		assertTaggedAsElement(usage.getAttributeByName("stringList", false));
		assertTaggedAsElement(usage.getAttributeByName("derivedStringList", false));
		assertTaggedAsElement(usage.getAttributeByName("simpleDerivedStringList", false));
		assertTaggedAsElement(usage.getAttributeByName("anonymousList", false));
		assertTaggedAsElement(usage.getAttributeByName("anonymousDerivedList", false));
		assertTaggedAsElement(usage.getAttributeByName("derivedAsComplexList", false));
		assertTaggedAsElement(usage.getAttributeByName("globalDerivedStringList", false));
		assertTaggedAsElement(usage.getAttributeByName("globalStringList", false));

		IClassifier aCDU = (IClassifier) simpleTypeModel.getElementByName(
				"AnonymousComplexDerivUsage", true);
		assertTaggedAsElement(aCDU.getAttributeByName("derivedSimpleAnonymous", false));
		assertTaggedAsElement(aCDU.getAttributeByName("derivedSimpleAnonymous2", false));

		// Complex Types!

		IClassifier basic = (IClassifier) complexTypeModel.getElementByName("Basic", true);
		assertTaggedAsElement(basic.getAttributeByName("simple", false));
		assertTaggedAsElement(basic.getAttributeByName("mySimple", false));
		assertTaggedAsElement(basic.getAttributeByName("anotherSimple", false));

		IClassifier simpleChoice = (IClassifier) complexTypeModel.getElementByName("SimpleChoice",
				true);
		assertTaggedAsElement(simpleChoice.getAttributeByName("childA", false));
		assertTaggedAsElement(simpleChoice.getAttributeByName("childB", false));

		IClassifier nestedA = (IClassifier) complexTypeModel.getElementByName("NestedA", true);
		assertTaggedAsElement(nestedA.getAttributeByName("childA", false));
		assertTaggedAsElement(nestedA.getAttributeByName("childB", false));
		assertTaggedAsElement(nestedA.getAttributeByName("childC", false));
		assertTaggedAsElement(nestedA.getAttributeByName("childD", false));
		assertTaggedAsElement(nestedA.getAttributeByName("childE", false));
		assertTaggedAsElement(nestedA.getAttributeByName("childF", false));
		assertTaggedAsElement(nestedA.getAttributeByName("childG", false));

	}

	public void testXSDGlobalElement() throws Exception {
		assertEquals(false, complexTypeModel.getElementByName("Basic", true).getUserData(
				XSD_GLOBAL_ELEMENT));
		assertEquals(false, simpleTypeModel.getElementByName("DerivedEnumeration", true)
				.getUserData(XSD_GLOBAL_ELEMENT));
		assertEquals(false, simpleTypeModel.getElementByName("DerivedString", true).getUserData(
				XSD_GLOBAL_ELEMENT));
		assertEquals(true, globalElementModel.getElementByName("globalElementA", true).getUserData(
				XSD_GLOBAL_ELEMENT));
		assertEquals(true, globalElementModel.getElementByName("globalElementB", true).getUserData(
				XSD_GLOBAL_ELEMENT));
	}

	/*
	 * Hacky test to do a sanity check that all the attributes that we expect to have
	 * element/attribute meta data do have it. Doesn't check the value.
	 */
	public void testAllXsdAttributesHaveAttributeMetaData() throws Exception {
		assertAllAttributesHaveAttributeMetaData(model);
		assertAllAttributesHaveAttributeMetaData(simpleTypeModel);
		assertAllAttributesHaveAttributeMetaData(complexTypeModel);
	}

	private void assertAllAttributesHaveAttributeMetaData(IModelElement model) {
		if (model instanceof IPackage) {
			if (((IPackage) model).getName().equals("xsd")) {
				return;
			}
			List<IModelElement> descendants = Collections.emptyList();
			descendants = ((IPackage) model).getContents(true);
			for (IModelElement modelElement : descendants) {
				assertAllAttributesHaveAttributeMetaData(modelElement);
			}
		} else if (model instanceof IClassifier) {
			List<IAttribute> descendants = ((IClassifier) model).getAttributes(true);
			for (IAttribute attribute : descendants) {
				IClassifier type = (IClassifier) attribute.getType();
				// "Ands" and "Ors" are synthetic elements for choices/unions or whatever
				// Enums are just enum values.
				if (!type.isEnumeration() && !attribute.getName().contains("And")
						&& !attribute.getName().contains("Or")) {
					String tag = (String) attribute.getUserData(IXSDUserData.ATTRIBUTE_KIND);
					assertTrue(IXSDUserData.XSD_ATTRIBUTE_KIND.equals(tag)
							|| IXSDUserData.XSD_ELEMENT_KIND.equals(tag));
				}
			}
		}
	}

	public void testAttributeNamespaceSet() throws Exception {
		IClassifier parent = (IClassifier) model.getElementByName("aTypeOne", true);
		IAttribute attribute = parent.getAttributeByName("aTypeOneAttributeOne", false);
		assertEquals("http://www.modeltwozero.com/test/a", attribute.getUserData(NAMESPACE));
	}

	public void testElementNamespaceSet() throws Exception {
		IClassifier parent = (IClassifier) model.getElementByName("aTypeOne", true);
		IAttribute attribute1 = parent.getAttributeByName("aTypeOneElementOne", false);
		assertEquals("http://www.modeltwozero.com/test/a", attribute1.getUserData(NAMESPACE));

		IAttribute attribute2 = parent.getAttributeByName("bElementOne", false);
		assertEquals("http://www.modeltwozero.com/test/b", attribute2.getUserData(NAMESPACE));
	}

	public void testClassifierNamespaceSet() throws Exception {
		IClassifier aTypeOne = (IClassifier) model.getElementByName("aTypeOne", true);
		assertEquals("http://www.modeltwozero.com/test/a", aTypeOne.getUserData(NAMESPACE));

		IClassifier bTypeOne = (IClassifier) model.getElementByName("bTypeOne", true);
		assertEquals("http://www.modeltwozero.com/test/b", bTypeOne.getUserData(NAMESPACE));
	}

	public void testElementWithAnonymousType() throws Exception {
		IClassifier anonymousType = (IClassifier) model.getElementByName("AnonymousElement", true);
		assertEquals("http://www.modeltwozero.com/test/a", anonymousType.getUserData(NAMESPACE));

		IClassifier parent = (IClassifier) model.getElementByName("aTypeOne", true);
		IAttribute anonymousElement = parent.getAttributeByName("anonymousElement", false);
		assertEquals("http://www.modeltwozero.com/test/a", anonymousElement.getUserData(NAMESPACE));
	}

	public void testXsdTypeKindMetaData() throws Exception {
		assertSimpleType(simpleTypeModel.getElementByName("DerivedString", true));
		assertSimpleType(simpleTypeModel.getElementByName("DerivedEnumeration", true));
		assertSimpleType(simpleTypeModel.getElementByName("EnumeratedInt", true));
		assertComplexType(simpleTypeModel.getElementByName("DerivedAsComplex", true));
		assertComplexType(simpleTypeModel.getElementByName("DoubleDerivation", true));
		assertSimpleType(simpleTypeModel.getElementByName("DoubleDerivationSimple", true));
		assertComplexType(simpleTypeModel.getElementByName("Usage", true));
		assertComplexType(simpleTypeModel.getElementByName("AnonymousComplexDerivUsage", true));
		assertComplexType(simpleTypeModel.getElementByName("EnumerationWithAttr", true));
		assertSimpleType(simpleTypeModel.getElementByName("RestrictedEnumeration", true));

		assertComplexType(complexTypeModel.getElementByName("Basic", true));
		assertComplexType(complexTypeModel.getElementByName("SimpleChoice", true));
		assertComplexType(complexTypeModel.getElementByName("NestedA", true));
		assertComplexType(complexTypeModel.getElementByName("WithAnonymousComplex", true));
		assertComplexType(complexTypeModel.getElementByName("WithAnonymousComplex2", true));
		assertComplexType(complexTypeModel.getElementByName("WithAnonymousSimple", true));
		assertComplexType(complexTypeModel.getElementByName("WithDerivedAnonymousComplex", true));
		assertComplexType(complexTypeModel.getElementByName("AnotherComplexType", true));
		assertComplexType(complexTypeModel.getElementByName("RepeatingParticle1", true));
		assertComplexType(complexTypeModel.getElementByName("RepeatingParticle1b", true));
		assertComplexType(complexTypeModel.getElementByName("RepeatingParticle2", true));
		assertComplexType(complexTypeModel.getElementByName("RepeatingParticle3", true));
		assertComplexType(complexTypeModel.getElementByName("RepeatingParticle3b", true));
		assertComplexType(complexTypeModel.getElementByName("RepeatingParticle4", true));
		assertComplexType(complexTypeModel.getElementByName("RepeatingParticle5", true));
		assertComplexType(complexTypeModel.getElementByName("RepeatingParticle6", true));
		assertComplexType(complexTypeModel.getElementByName("RepeatingParticle7", true));
		assertComplexType(complexTypeModel.getElementByName("RepeatingParticle8", true));
		assertComplexType(complexTypeModel.getElementByName("RepeatingParticle9", true));
		assertComplexType(complexTypeModel.getElementByName("RepeatingParticle10", true));
		assertComplexType(complexTypeModel.getElementByName("RepeatingParticle11", true));
	}

	private void assertSimpleType(IModelElement modelElement) {
		assertNotNull(modelElement);
		assertTrue(modelElement instanceof IClassifier);
		assertEquals(IXSDUserData.XSD_SIMPLE_TYPE_KIND, ((IClassifier) modelElement)
				.getUserData(IXSDUserData.XSD_TYPE_KIND));
	}

	private void assertComplexType(IModelElement modelElement) {
		assertTrue(modelElement instanceof IClassifier);
		assertEquals(IXSDUserData.XSD_COMPLEX_TYPE_KIND, ((IClassifier) modelElement)
				.getUserData(IXSDUserData.XSD_TYPE_KIND));
	}

	private void assertTaggedAsElement(IAttribute attribute) {
		String attributeKind = (String) attribute.getUserData(IXSDUserData.ATTRIBUTE_KIND);
		assertEquals(IXSDUserData.XSD_ELEMENT_KIND, attributeKind);
	}

	private void assertTaggedAsAttribute(IAttribute attribute) {
		String attributeKind = (String) attribute.getUserData(IXSDUserData.ATTRIBUTE_KIND);
		assertEquals(IXSDUserData.XSD_ATTRIBUTE_KIND, attributeKind);
	}

}
