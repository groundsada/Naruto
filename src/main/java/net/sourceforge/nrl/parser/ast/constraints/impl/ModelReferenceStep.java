package net.sourceforge.nrl.parser.ast.constraints.impl;

import net.sourceforge.nrl.parser.ast.impl.Antlr3NRLBaseAst;

import org.antlr.runtime.Token;


/**
 * A helper AST node class that represents one step in a model reference.
 * 
 * @author Christian Nentwich
 */
public class ModelReferenceStep extends Antlr3NRLBaseAst {

	public ModelReferenceStep() {
	}

	public ModelReferenceStep(Token token) {
		super(token);
	}
}
