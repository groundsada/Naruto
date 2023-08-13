package net.sourceforge.nrl.parser.ast.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.IMultipleContextDeclaration;
import net.sourceforge.nrl.parser.model.IModelElement;

import org.antlr.runtime.Token;

public class MultipleContextDeclarationImpl extends DeclarationImpl implements
		IMultipleContextDeclaration {

	private List<String> names;

	private Map<String, IModelReference> references;

	private Map<String, IModelElement> elements = new HashMap<String, IModelElement>();

	public MultipleContextDeclarationImpl(Token token) {
		super(token);
	}

	public List<String> getContextNames() {
		// Cached access
		if (names == null) {
			initialiseFromAst();
		}

		return names;
	}

	public IModelElement getContextType(String name) {
		return elements.get(name);
	}

	/*
	 * Initialise the name and model reference list from AST
	 */
	protected void initialiseFromAst() {
		names = new ArrayList<String>();
		references = new HashMap<String, IModelReference>();

		List<?> children = getChildren();
		for (Object childObject : children) {
			if (!(childObject instanceof NamedParameterImpl))
				break;
			
			NamedParameterImpl child = (NamedParameterImpl) childObject;
			names.add(child.getName());
			references.put(child.getName(), child.getReference());
		}
	}

	/*
	 * Return the raw model reference for any given parameter. This is used by
	 * the resolver.
	 */
	public IModelReference getModelReference(String name) {
		if (references == null) {
			initialiseFromAst();
		}
		return references.get(name);
	}

	/*
	 * Set the context type for a parameter - used by the resolver.
	 */
	public void setContextType(String name, IModelElement element) {
		elements.put(name, element);
	}

	/*
	 * Return true if all parameters have been resolved
	 */
	public boolean isFullyResolved() {
		if (names == null) {
			initialiseFromAst();
		}

		for (String name : names) {
			if (elements.get(name) == null)
				return false;
		}
		return true;
	}
}
