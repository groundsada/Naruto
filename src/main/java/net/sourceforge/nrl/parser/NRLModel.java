package net.sourceforge.nrl.parser;

import java.util.List;

import net.sourceforge.nrl.parser.ast.IRuleFile;
import net.sourceforge.nrl.parser.model.ModelCollection;
import net.sourceforge.nrl.parser.operators.IOperators;

public class NRLModel {

	private IRuleFile ruleFile;
	private ModelCollection models;
	private List<IOperators> operators;
	
	public NRLModel(IRuleFile ruleFile, ModelCollection models, List<IOperators> operators) {
		this.ruleFile = ruleFile;
		this.models = models;
		this.operators = operators;
	}

	public IRuleFile getRuleFile() {
		return ruleFile;
	}

	public ModelCollection getModels() {
		return models;
	}

	public List<IOperators> getOperators() {
		return operators;
	}
	
}
