package net.sourceforge.nrl.parser.model.xsd;

import static org.junit.Assert.assertEquals;

import java.io.File;

import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.model.IPackage;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class IncludedSchemaTest {

	@BeforeClass
	public static void setUpOnce() {
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd",
				new XSDResourceFactoryImpl());
	}
	
	@Test
	@SuppressWarnings("deprecation")
	public void includedSchemaWithNoNamespaceInheritsNamespace() throws Exception {
		XSDModelLoader loader = new XSDModelLoader();
		IPackage model = loader.load(new File("src/test/resources/schema/namespace-includes-no-namespace.xsd"));
		IModelElement complexTypeWithIncludedType = model.getElementByName("ComplexTypeWithIncludedType", true);
		assertEquals("http://www.modeltwozero.com/test", model.getUserData(IXSDUserData.NAMESPACE));
		assertEquals("http://www.modeltwozero.com/test", complexTypeWithIncludedType.getUserData(IXSDUserData.NAMESPACE));
		IPackage included = (IPackage)model.getContents(true).get(2);
		IModelElement trivialComplexType = included.getElementByName("TrivialComplexType", true);
		assertEquals("no-namespace", included.getName());	
		assertEquals("http://www.modeltwozero.com/test", included.getUserData(IXSDUserData.NAMESPACE));
		assertEquals("http://www.modeltwozero.com/test", trivialComplexType.getUserData(IXSDUserData.NAMESPACE));
	}
	
	@Ignore
	@Test
	@SuppressWarnings("deprecation")
	public void includedSchemaWithNamespaceIncludesSchemaWithNoNamespace() throws Exception {
		XSDModelLoader loader = new XSDModelLoader();
		IPackage model = loader.load(new File("src/test/resources/schema/namespace-includes-namespace-which-includes-no-namespace.xsd"));
		IModelElement trivialComplexType = model.getElementByName("TrivialComplexType", true);
		assertEquals("http://www.modeltwozero.com/test", trivialComplexType.getUserData(IXSDUserData.NAMESPACE));		
	}
	
}
