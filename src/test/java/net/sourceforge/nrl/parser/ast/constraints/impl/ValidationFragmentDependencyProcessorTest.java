package net.sourceforge.nrl.parser.ast.constraints.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import net.sourceforge.nrl.parser.IStatusCode;
import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.NRLParserTestSupport;
import net.sourceforge.nrl.parser.ast.IDeclaration;
import net.sourceforge.nrl.parser.ast.IRuleFile;

import org.junit.Test;

public class ValidationFragmentDependencyProcessorTest extends NRLParserTestSupport {

	/*
	 * Resolve a collection of entirely independent properties. Should just come out in file order.
	 */
	@Test
	public void testResolve_Independent() throws Exception {
		IRuleFile file = createTestFile("'a' = 'b' "
				+ "Context: Trade (\"t\") Validation Fragment \"p1\" 'a' = 'b' "
				+ "Context: Trade (\"t\") Validation Fragment \"p2\" 'a' = 'b' "
				+ "Context: Trade (\"t\") Validation Fragment \"p3\" 'a' = 'b'");

		ValidationFragmentDependencyProcessor proc = new ValidationFragmentDependencyProcessor();
		proc.addDeclarations(file);

		List<NRLError> errors = proc.resolve();
		assertEquals(0, errors.size());

		List<IDeclaration> decls = proc.getOrderedDeclarations();
		assertEquals(3, decls.size());
		assertId(decls, "p1");
		assertId(decls, "p2");
		assertId(decls, "p3");
	}

	/*
	 * Resolve a collection of depdendent properties.
	 */
	@Test
	public void testResolve_Dedependent() throws Exception {
		IRuleFile file = createTestFile("'a' = 'b' "
				+ "Context: Trade (\"t\") Validation Fragment  \"p1\" 'a' = 'b' "
				+ "Context: Trade (\"t\") Validation Fragment \"p2\" p3 "
				+ "Context: Trade (\"t\")  Validation Fragment \"p4\" p1"
				+ "Context: Trade (\"t\") Validation Fragment \"p3\" p4 or p5"
				+ "Context: Trade (\"t\") Validation Fragment \"p5\" 'a' = 'b'");

		ValidationFragmentDependencyProcessor proc = new ValidationFragmentDependencyProcessor();
		proc.addDeclarations(file);

		List<NRLError> errors = proc.resolve();
		assertEquals(0, errors.size());

		List<IDeclaration> decls = proc.getOrderedDeclarations();
		assertEquals("p1", decls.get(0).getId());
		assertEquals("p4", decls.get(1).getId());
		assertEquals("p5", decls.get(2).getId());
		assertEquals("p3", decls.get(3).getId());
		assertEquals("p2", decls.get(4).getId());
	}

	/*
	 * Resolve a collection of properties with cycles.
	 */
	@Test
	public void testResolve_Cyclical_Plus_Other() throws Exception {
		IRuleFile file = createTestFile("'a' = 'b' "
				+ "Context: Trade (\"t\") Validation Fragment \"p1\" 'a' = 'b' "
				+ "Context: Trade (\"t\") Validation Fragment \"p2\" p3 "
				+ "Context: Trade (\"t\") Validation Fragment \"p3\" p4 "
				+ "Context: Trade (\"t\") Validation Fragment \"p4\" p2 ");

		ValidationFragmentDependencyProcessor proc = new ValidationFragmentDependencyProcessor();
		proc.addDeclarations(file);

		List<NRLError> errors = proc.resolve();
		assertEquals(3, errors.size());
		assertEquals(IStatusCode.FRAGMENT_CYCLICAL_REFERENCE, errors.get(0).getStatusCode());

		List<IDeclaration> decls = proc.getOrderedDeclarations();
		assertEquals(4, decls.size());
		assertEquals("p1", decls.get(0).getId());
		assertId(decls, "p2");
		assertId(decls, "p3");
		assertId(decls, "p4");
	}

	/*
	 * Resolve a collection of properties with cycles.
	 */
	@Test
	public void testResolve_Cyclical() throws Exception {
		IRuleFile file = createTestFile("'a' = 'b' "
				+ "Context: Trade (\"t\") Validation Fragment \"p2\" p3 "
				+ "Context: Trade (\"t\") Validation Fragment \"p3\" p2");

		ValidationFragmentDependencyProcessor proc = new ValidationFragmentDependencyProcessor();
		proc.addDeclarations(file);

		List<NRLError> errors = proc.resolve();
		assertEquals(2, errors.size());
		assertEquals(IStatusCode.FRAGMENT_CYCLICAL_REFERENCE, errors.get(0).getStatusCode());

		List<IDeclaration> decls = proc.getOrderedDeclarations();
		assertEquals(2, decls.size());
		assertId(decls, "p2");
		assertId(decls, "p3");
	}

	protected void assertId(List<IDeclaration> decls, String id) {
		for (IDeclaration decl : decls) {
			if (decl.getId().equals(id))
				return;
		}
		fail("Id " + id + " not found");
	}
}
