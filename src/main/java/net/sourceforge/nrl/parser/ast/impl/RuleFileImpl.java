/*
 * The contents of this file are subject to the Mozilla Public License 
 * Version 1.1 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See 
 * the License for the specific language governing rights and limitations 
 * under the License.
 * 
 * The Original Code is the NRL Parser, released 28 April 2006, 
 * Copyright (c) Christian Nentwich. The Initial Developer of the 
 * Original Code is Christian Nentwich. Portions created by contributors 
 * identified in the NOTICES file are Copyright (c) the contributors. 
 * All Rights Reserved. 
 */
package net.sourceforge.nrl.parser.ast.impl;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.nrl.parser.ast.IDeclaration;
import net.sourceforge.nrl.parser.ast.IModelFileReference;
import net.sourceforge.nrl.parser.ast.INRLAstNode;
import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.IOperatorFileReference;
import net.sourceforge.nrl.parser.ast.IRuleDeclaration;
import net.sourceforge.nrl.parser.ast.IRuleFile;
import net.sourceforge.nrl.parser.ast.IRuleSetDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.IVariableDeclaration;
import net.sourceforge.nrl.parser.model.ModelCollection;
import net.sourceforge.nrl.parser.operators.IOperator;

import org.antlr.runtime.Token;

/**
 * ANTLR implementation of the overall rule file AST node.
 * 
 * @author Christian Nentwich
 */
public class RuleFileImpl extends Antlr3NRLBaseAst implements IRuleFile {

	// Cached list of declarations
	private List<IDeclaration> declarations;

	// Cached list of rule set declarations
	private List<IRuleSetDeclaration> ruleSetDeclarations;

	// Cached list of global variable declarations
	private List<IVariableDeclaration> variableDeclarations;

	public RuleFileImpl() {
	}

	public RuleFileImpl(Token token) {
		super(token);
	}

	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
			for (int i = 1; i < getChildCount(); i++) {
				Object child = getChild(i);
				if (child instanceof INRLAstNode)
					((INRLAstNode) child).accept(visitor);
			}
		}
		visitor.visitAfter(this);
	}

	@Deprecated
	public String dump(int indent) {
		StringBuffer result = new StringBuffer();

		for (IDeclaration decl : getDeclarations()) {
			result.append(decl.dump(indent));
		}

		return result.toString();
	}

	public IModelFileReference[] getModelFileReferences() {
		List<IModelFileReference> result = new ArrayList<IModelFileReference>();

		for (Object child : getChildren()) {
			if (child instanceof IModelFileReference)
				result.add((IModelFileReference) child);
		}

		return (IModelFileReference[]) result
				.toArray(new IModelFileReference[0]);
	}

	public IOperatorFileReference[] getOperatorFileReferences() {
		List<IOperatorFileReference> result = new ArrayList<IOperatorFileReference>();

		for (Object child : getChildren()) {
			if (child instanceof IOperatorFileReference)
				result.add((IOperatorFileReference) child);
		}

		return (IOperatorFileReference[]) result
				.toArray(new IOperatorFileReference[0]);
	}

	/**
	 * Return all declarations (rule or fragment) by traversing the second child
	 * of the AST (after model) forward
	 */
	public List<IDeclaration> getDeclarations() {
		if (declarations == null) {
			declarations = new ArrayList<IDeclaration>();

			for (Object child : getChildren()) {
				if (child instanceof IDeclaration)
					declarations.add((IDeclaration) child);
			}
		}
		return declarations;
	}

	/**
	 * Return all rule set declarations by traversing the second child of the
	 * AST (after model) forward
	 */
	public List<IRuleSetDeclaration> getRuleSetDeclarations() {
		if (ruleSetDeclarations == null) {
			ruleSetDeclarations = new ArrayList<IRuleSetDeclaration>();

			for (Object child : getChildren()) {
				if (child instanceof IRuleSetDeclaration)
					ruleSetDeclarations.add((IRuleSetDeclaration) child);
			}
		}
		return ruleSetDeclarations;
	}

	/**
	 * Return a declaration given its id. The result can be a rule or a property
	 * declaration.
	 * 
	 * @param id
	 *            the id
	 * @return the declaration or null if not found
	 */
	public IDeclaration getDeclarationById(String id) {
		for (IDeclaration decl : getDeclarations()) {
			if (decl.getId().equals(id))
				return decl;
		}
		return null;
	}

	public IRuleDeclaration getRuleById(String id) {
		for (IDeclaration decl : getDeclarations()) {
			if (decl instanceof IRuleDeclaration) {
				if (((IRuleDeclaration) decl).getId().equals(id))
					return (IRuleDeclaration) decl;
			}
		}
		return null;
	}

	public List<IVariableDeclaration> getGlobalVariableDeclarations() {
		if (variableDeclarations == null) {
			variableDeclarations = new ArrayList<IVariableDeclaration>();

			for (Object child : getChildren()) {
				if (child instanceof IVariableDeclaration)
					variableDeclarations.add((IVariableDeclaration) child);
			}
		}
		return variableDeclarations;
	}

	public ModelCollection getModels() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<IOperator> getOperators() {
		// TODO Auto-generated method stub
		return null;
	}
}
