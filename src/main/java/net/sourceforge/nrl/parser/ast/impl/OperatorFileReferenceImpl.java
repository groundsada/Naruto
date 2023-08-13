package net.sourceforge.nrl.parser.ast.impl;

import net.sourceforge.nrl.parser.ast.IOperatorFileReference;
import net.sourceforge.nrl.parser.operators.IOperators;

import org.antlr.runtime.Token;

public class OperatorFileReferenceImpl extends Antlr3NRLBaseAst implements
		IOperatorFileReference {

	public IOperators operators;
	
	public OperatorFileReferenceImpl(Token token) {
		super(token);
	}

	public String getFileName() {
		return getChild(0).getText();
	}

	public boolean isAbsolute() {
		String name = getFileName();

		return name.startsWith("/") || (name.length() > 2 && name.charAt(1) == ':');
	}

	public String dump(int indent) {
		StringBuffer result = new StringBuffer();
		result.append(doIndent(indent) + "Operator file " + getFileName() + NEWLINE);

		return result.toString();
	}

	public IOperators getOperators() {
		return operators;
	}

	public boolean isOperatorsResolved() {
		return operators != null;
	}
	
	public void resolveOperators(IOperators operators){
		if(operators == null){
			throw new IllegalStateException("Operator reference already resolved.");
		}
		this.operators = operators;
	}
}
