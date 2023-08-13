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
package net.sourceforge.nrl.parser.ast.action.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.nrl.parser.IStatusCode;
import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.SemanticError;
import net.sourceforge.nrl.parser.ast.action.IActionFragmentDeclaration;
import net.sourceforge.nrl.parser.ast.impl.Antlr3NRLBaseAst;
import net.sourceforge.nrl.parser.ast.impl.AntlrAstVisitor;
import net.sourceforge.nrl.parser.ast.impl.ConstraintAstResolver;
import net.sourceforge.nrl.parser.ast.impl.RuleFileImpl;

/**
 * This action AST resolver extends the basic resolver and adds resolving and
 * checking of fragment references.
 * 
 * @author Christian Nentwich
 */
public class ActionAstResolver extends ConstraintAstResolver {

	protected Map<String, IActionFragmentDeclaration> actionFragmentIds = new HashMap<String, IActionFragmentDeclaration>();

	/**
	 * Extends the base implementation to also collect fragment ids.
	 */
	protected void collectIdentifiers(RuleFileImpl ruleFile, List<NRLError> errors) {
		super.collectIdentifiers(ruleFile, errors);

		collectActionFragmentIds(ruleFile, errors);
	}

	/**
	 * Collect action fragment identifiers in the actionFragmentIds list.
	 * 
	 * @param ruleFile the rule file
	 * @param errors the error list
	 */
	protected void collectActionFragmentIds(final RuleFileImpl ruleFile,
			final List<NRLError> errors) {
		actionFragmentIds.clear();

		ruleFile.accept(new AntlrAstVisitor() {
			protected boolean visitBefore(Antlr3NRLBaseAst node) {
				// Visit macro declarations
				if (node instanceof IActionFragmentDeclaration) {
					IActionFragmentDeclaration decl = (IActionFragmentDeclaration) node;
					if (actionFragmentIds.keySet().contains(decl.getId())) {
						errors.add(new SemanticError(
								IStatusCode.DUPLICATE_ACTION_FRAGMENT, decl.getLine(),
								decl.getColumn(), "Duplicate action fragment id: "
										+ decl.getId()));
					} else
						actionFragmentIds.put(decl.getId(), decl);
				}

				return true;
			}
		});
	}

	/**
	 * Override the superclass method to also resolve fragment references.
	 */
	protected void resolveReferences(RuleFileImpl ruleFile, final List<NRLError> errors) {
		super.resolveReferences(ruleFile, errors);

		ruleFile.accept(new AntlrAstVisitor() {
			protected boolean visitBefore(Antlr3NRLBaseAst node) {

				// Visit fragment applications actions
				if (node instanceof ActionFragmentApplicationActionImpl) {
					ActionFragmentApplicationActionImpl appl = (ActionFragmentApplicationActionImpl) node;

					ActionFragmentDeclarationImpl decl = (ActionFragmentDeclarationImpl) actionFragmentIds
							.get(appl.getActionFragmentId());
					if (decl == null) {
						errors.add(new SemanticError(
								IStatusCode.INVALID_ACTION_FRAGMENT_REF, appl.getLine(),
								appl.getColumn(),
								"Rule references undeclared action fragment: "
										+ appl.getActionFragmentId()));
					} else
						appl.setFragment(decl);
				}

				return true;
			}
		});
	}
}
