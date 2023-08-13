package net.sourceforge.nrl.parser.ast.constraints.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.nrl.parser.IStatusCode;
import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.SemanticError;
import net.sourceforge.nrl.parser.ast.IDeclaration;
import net.sourceforge.nrl.parser.ast.INRLAstNode;
import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.IRuleFile;
import net.sourceforge.nrl.parser.ast.constraints.IValidationFragmentApplication;
import net.sourceforge.nrl.parser.ast.constraints.IValidationFragmentDeclaration;

/**
 * A dependency processor that can be fed with a number of fragment declarations, and returns them
 * in dependency order. Properties with no dependencies will be returned first.
 * <p>
 * This class enables the type checker to determine a processing order, because fragment types
 * cannot determined until types of properties referenced within them are clear.
 * <p>
 * The class also detects circular fragment references. Here is how to use it:
 * <ul>
 * <li>Call {@link #addDeclaration(IDeclaration)} repeatedly
 * <li>Call {@link #resolve()} and abort if necessary
 * <li>Call {@link #getOrderedDeclarations()}
 * </ul>
 * 
 * @author Christian Nentwich
 */
public class ValidationFragmentDependencyProcessor {

	private List<IDeclaration> declarations = new ArrayList<IDeclaration>();

	private List<IDeclaration> orderedDeclarations = new ArrayList<IDeclaration>();

	public void addDeclaration(IValidationFragmentDeclaration decl) {
		declarations.add(decl);
	}

	public void addDeclaration(IDeclaration decl) {
		if (decl instanceof IValidationFragmentDeclaration)
			declarations.add(decl);
	}

	public void addDeclarations(IRuleFile file) {
		for (IDeclaration decl : file.getDeclarations()) {
			addDeclaration(decl);
		}
	}

	public List<NRLError> resolve() {
		orderedDeclarations.clear();
		List<NRLError> result = new ArrayList<NRLError>();

		// Nothing to do
		if (declarations.size() < 2) {
			orderedDeclarations.addAll(declarations);
			return result;
		}

		// Build a list of dependency node
		Collection<DependencyNode> nodes = buildDependencyGraph();

		// Find a list of nodes with no incoming edges (i.e. not referenced
		// by other properties)
		List<DependencyNode> independent = new ArrayList<DependencyNode>();

		for (DependencyNode node : nodes) {

			int count = 0;
			for (DependencyNode others : nodes) {
				if (others.references(node))
					count++;
			}

			if (count == 0) {
				independent.add(node);
			}
		}

		for (DependencyNode node : independent) {
			depthFirst(node, nodes);
		}

		// Independent set empty? Must be a cycle.
		if (!nodes.isEmpty()) {
			for (DependencyNode node : nodes) {
				IDeclaration decl = node.getSource();

				result.add(new SemanticError(IStatusCode.FRAGMENT_CYCLICAL_REFERENCE, decl
						.getLine(), decl.getColumn(), 1,
						"Fragment is involved in a circular reference"));

				orderedDeclarations.add(decl);
			}
		}

		return result;
	}

	/**
	 * Traverse from a node, and add all encountered nodes to the orderedDeclarations list. Also
	 * remove from the second list being provided as parameter, to indicate processing.
	 * 
	 * @param root the root node
	 * @param removalList the list to remove from
	 */
	protected void depthFirst(DependencyNode root, Collection<DependencyNode> removalList) {
		for (DependencyNode target : root.getTargets()) {
			depthFirst(target, removalList);
		}

		orderedDeclarations.add(root.getSource());
		removalList.remove(root);
	}

	/**
	 * Build the graph of DependencyNode objects. See below for the class definition.
	 * 
	 * @return the graph
	 */
	protected Collection<DependencyNode> buildDependencyGraph() {
		Map<IDeclaration, DependencyNode> deps = new HashMap<IDeclaration, DependencyNode>();
		for (IDeclaration decl : declarations) {
			DependencyNode node = new DependencyNode(decl);
			deps.put(decl, node);
		}

		for (DependencyNode node : deps.values()) {
			IDeclaration decl = node.getSource();

			for (IDeclaration ref : getReferencedDeclarations(decl)) {
				node.addTarget(deps.get(ref));
			}

			// System.out.println(node.getSource().getId());
			// for (DependencyNode d : node.getTargets()) {
			// System.out.println(" " + d.getSource().getId());
			// }
		}
		return deps.values();
	}

	/**
	 * Given a declaration, return a list of declarations it references.
	 * 
	 * @param decl the declarations
	 * @return the referenced declarations
	 */
	protected List<IDeclaration> getReferencedDeclarations(IDeclaration decl) {
		final List<IDeclaration> result = new ArrayList<IDeclaration>();

		if (decl instanceof IValidationFragmentDeclaration) {
			IValidationFragmentDeclaration propDecl = (IValidationFragmentDeclaration) decl;
			propDecl.accept(new INRLAstVisitor() {
				public void visitAfter(INRLAstNode node) {
					if (node instanceof IValidationFragmentApplication) {
						result.add(((IValidationFragmentApplication) node).getFragment());
					}
				}

				public boolean visitBefore(INRLAstNode node) {
					return true;
				}
			});
		}

		return result;
	}

	public List<IDeclaration> getOrderedDeclarations() {
		return orderedDeclarations;
	}

	class DependencyNode {
		private IDeclaration source;

		private List<DependencyNode> targets = new ArrayList<DependencyNode>();

		public DependencyNode(IDeclaration source) {
			this.source = source;
		}

		public void addTargets(ArrayList<DependencyNode> targets) {
			this.targets.addAll(targets);
		}

		public void addTarget(DependencyNode target) {
			targets.add(target);
		}

		public IDeclaration getSource() {
			return source;
		}

		public List<DependencyNode> getTargets() {
			return targets;
		}

		public boolean references(DependencyNode node) {
			return targets.contains(node);
		}
	}
}
