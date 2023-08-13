/*
 * The contents of this file are subject to the Mozilla Public License Version 1.1 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF
 * ANY KIND, either express or implied. See the License for the specific language governing rights
 * and limitations under the License.
 * 
 * The Original Code is the NRL Parser, released 28 April 2006, Copyright (c) Christian Nentwich.
 * The Initial Developer of the Original Code is Christian Nentwich. Portions created by
 * contributors identified in the NOTICES file are Copyright (c) the contributors. All Rights
 * Reserved.
 */
package net.sourceforge.nrl.parser.ast.constraints.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.INRLAstVisitor;
import net.sourceforge.nrl.parser.ast.IVariable;
import net.sourceforge.nrl.parser.ast.impl.Antlr3NRLBaseAst;
import net.sourceforge.nrl.parser.model.IAttribute;
import net.sourceforge.nrl.parser.model.IModelElement;

import org.antlr.runtime.Token;

/**
 * Implementation of a model reference. When initialised during parsing, this class contains only a
 * broken up list of strings representing the steps through the model. This list can be obtained
 * using {@link #getStepsAsStrings()}.
 * <p>
 * A model resolver needs to be used to set the actual model references in the initial step, and the
 * remaining steps. In the ANTLR implementation, the
 * {@link net.sourceforge.nrl.parser.ast.impl.AntlrModelResolver} class performs this task.
 * 
 * @author Christian Nentwich
 */
public class ModelReferenceImpl extends ConstraintImpl implements IModelReference {

	/**
	 * The character that may appear at the start of steps to escape an NRL keyword. This is
	 * stripped off by this class and will only appear in the string returned by
	 * {@link #getOriginalString()}.
	 */
	public static final char KEYWORD_ESCAPE_CHARACTER = '`';

	// The parsed steps in the reference, as strings
	private List<String> stepsAsStrings;

	// The original string read from the NRL file
	private String originalString;

	// The non-initial steps as IAttributes. Will be empty
	// until addStep is called. Is reset by resetSteps
	private List<IAttribute> steps = new ArrayList<IAttribute>();

	// Is either an IAttribute, IModelElement or IVariable
	// Reset to null by resetSteps, set by setInitialStep
	private Object initialStep;

	// The current context in which this reference was made
	private IModelElement currentContext;

	private int referenceType;

	public ModelReferenceImpl(Token token) {
		super(token);

		stepsAsStrings = new ArrayList<String>();
		originalString = null;
	}

	/**
	 * Initialise the model reference by cloning another
	 * 
	 * @param other the reference to clone
	 */
	public ModelReferenceImpl(ModelReferenceImpl other) {
		stepsAsStrings = new ArrayList<String>();
		stepsAsStrings.addAll(other.stepsAsStrings);
		originalString = other.originalString;
	}

	public void accept(INRLAstVisitor visitor) {
		if (visitor.visitBefore(this)) {
		}
		visitor.visitAfter(this);
	}

	public String dump(int indent) {
		String result = doIndent(indent);
		for (Iterator<String> iter = stepsAsStrings.iterator(); iter.hasNext();) {
			result = result + iter.next();
			if (iter.hasNext())
				result = result + ".";
		}
		result = result + NEWLINE;
		return result;
	}

	public IModelElement getCurrentContext() {
		return currentContext;
	}

	public int getInitialStepType() {
		if (initialStep instanceof IModelElement)
			return STEP_MODEL_ELEMENT;
		if (initialStep instanceof IVariable)
			return STEP_VARIABLE;
		return STEP_ATTRIBUTE;
	}

	public Object getInitialStep() {
		return initialStep;
	}

	public IAttribute getLastAttribute() {
		if (steps.isEmpty()) {
			return null;
		}
		return steps.get(steps.size() - 1);
	}
	
	public List<IAttribute> getRemainingSteps() {
		return steps;
	}

	public int getReferenceType() {
		return referenceType;
	}

	public IModelElement getTarget() {
		if (getRemainingSteps().size() == 0) {
			if (initialStep instanceof IAttribute)
				return ((IAttribute) initialStep).getType();
			if (initialStep instanceof IModelElement)
				return (IModelElement) initialStep;
			if (initialStep instanceof IVariable) {
				IVariable var = (IVariable) initialStep;
				if (var.isBoundToElement())
					return var.getBoundElement();
				return null;
			}

			throw new RuntimeException("Internal error.");
		} else
			return (steps.get(steps.size() - 1)).getType();
	}

	public List<String> getStepsAsStrings() {
		return stepsAsStrings;
	}

	public String getOriginalString() {
		return originalString;
	}

	/**
	 * AST processing method: initialise the stepsAsStrings and originalString member variables from
	 * the steps.
	 * <p>
	 * This converts steps of the form <code>a.b.c</code> and <code>c of b of a</code> into a string
	 * step list.
	 */
	public void initializeSteps() {
		stepsAsStrings = new ArrayList<String>();
		originalString = "";

		List<?> children = getChildren();
		if (children != null) {
			for (Object child : children) {
				if (!(child instanceof ModelReferenceStep))
					continue;

				ModelReferenceStep step = (ModelReferenceStep) child;
				String stepString = step.getText();

				if (originalString.length() > 0)
					originalString += " of ";
				originalString += stepString;

				List<String> subSteps = new ArrayList<String>();

				String[] stepTokens = stepString.split("\\.");
				for (String stepToken : stepTokens) {
					// Replace escapes in package steps
					stepToken = stepToken.replaceAll("::`", "::");
					if (stepToken.length() > 1 && stepToken.charAt(0) == KEYWORD_ESCAPE_CHARACTER)
						stepToken = stepToken.substring(1);
					subSteps.add(stepToken);
				}

				stepsAsStrings.addAll(0, subSteps);
			}
		}

		// Now also fix up the line and column counts from the steps
		if (getChildCount() > 0 && getChild(0) != null && getChild(0) instanceof Antlr3NRLBaseAst) {
			setLine(getChild(0).getLine());
			setColumn(getChild(0).getCharPositionInLine());
		}
	}

	public void setInitialStep(Object obj) {
		initialStep = obj;
	}

	public void addStep(IAttribute step) {
		steps.add(step);
	}

	public void resetSteps() {
		steps.clear();
		initialStep = null;
	}

	public void setCurrentContext(IModelElement currentContext) {
		this.currentContext = currentContext;
	}

	public void setReferenceType(int referenceType) {
		this.referenceType = referenceType;
	}

	/**
	 * Delete the left-most step in the model reference, and move all other steps left. If this
	 * leaves an empty model reference, which should never happen, this throws a runtime exception.
	 */
	public void shiftStepsLeft() {
		if (steps.size() == 0)
			throw new RuntimeException(
					"Internal error. Cannot eliminate reference steps to form empty reference.");

		IAttribute newInitialStep = steps.remove(0);

		setInitialStep(newInitialStep);
	}
}
