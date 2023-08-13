package net.sourceforge.nrl.parser.ast.impl;

import org.antlr.runtime.Token;

import net.sourceforge.nrl.parser.ast.IModelReference;

/*
 * Helper class for {@link MultipleContextDeclarationImpl}
 */
public class NamedParameterImpl extends Antlr3NRLBaseAst {

	public NamedParameterImpl(Token token) {
		super(token);
	}
	
	public IModelReference getReference() {
		return (IModelReference) getChild(0);
	}

	public String getName() {
		return getChild(1).getText();
	}

}
