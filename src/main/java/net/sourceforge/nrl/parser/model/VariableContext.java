package net.sourceforge.nrl.parser.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import net.sourceforge.nrl.parser.ast.IVariable;

/**
 * An implementation of a stack-based variable context that is used throughout
 * the parser for processing model references.
 * <p>
 * The variable context consists of a number of "frames":
 * <ul>
 * <li>Additional frames required for block-type statements like "if". The
 * frames can be pushed and popped as necessary.
 * </ul>
 * 
 * @author Christian Nentwich
 */
public class VariableContext {

	private Stack<List<IVariable>> variableFrames = new Stack<List<IVariable>>();

	public VariableContext() {
		pushFrame();
	}

	public void bindToCurrentFrame(IVariable var) {
		variableFrames.peek().add(var);
	}

	public boolean isAlreadyDeclared(String varName) {
		for (List<IVariable> frame : variableFrames) {
			for (IVariable var : frame) {
				if (var.getName().equals(varName))
					return true;
			}
		}
		return false;
	}

	public IVariable lookup(String name) {
		for (int i = variableFrames.size() - 1; i >= 0; i--) {
			List<IVariable> frame = variableFrames.get(i);
			for (IVariable var : frame) {
				if (var.getName().equals(name))
					return var;
			}
		}
		return null;
	}

	public void pushFrame() {
		variableFrames.push(new ArrayList<IVariable>());
	}

	public void popFrame() {
		if (variableFrames.size() > 1)
			variableFrames.pop();
	}
}
