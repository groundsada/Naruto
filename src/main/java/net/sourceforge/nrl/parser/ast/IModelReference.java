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
package net.sourceforge.nrl.parser.ast;

import java.util.List;

import net.sourceforge.nrl.parser.ast.constraints.IIdentifier;
import net.sourceforge.nrl.parser.model.IAttribute;
import net.sourceforge.nrl.parser.model.IModelElement;

/**
 * A reference to a model element, consisting of several steps along element and attribute names.
 * Includes an optional "where" clause. The class effectively represents a simple query over a
 * model.
 * 
 * @author Christian Nentwich
 */
public interface IModelReference extends IIdentifier {

	/**
	 * Reference type: references a model element. This happens, for example, in a rule context.
	 */
	public final static int REFERENCE_ELEMENT = 0;

	/**
	 * Reference type: a reference to a static attribute, or enumeration value. For example,
	 * <code>TradeType.FX</code>. Such a reference typically starts with a model element and points
	 * to a static attribute.
	 */
	public final static int REFERENCE_STATIC_ATTRIBUTE = 1;

	/**
	 * Reference type: a reference to an attribute, relative to the current evaluation context. For
	 * example <code>tradeHeader.tradeDate</code>.
	 */
	public final static int REFERENCE_RELATIVE_ATTRIBUTE = 2;

	/**
	 * Reference type: a reference to an attribute relative to the rule or fragment context. For
	 * example, in a rule <code>each leg has date = Trade.date</code>, the reference
	 * <code>Trade.date</code> refers to the date of the overall rule context, not the leg. The
	 * element <code>Trade</code> <b>must</b> be the rule context, otherwise an error is raised
	 * during resolution.
	 */
	public final static int REFERENCE_TOP_CONTEXT_RELATIVE_ATTRIBUTE = 3;

	/**
	 * Reference type: an attribute relative to a variable, for example <code>x.date</code>, where x
	 * is some bound variable.
	 */
	public final static int REFERENCE_VARIABLE_RELATIVE_ATTRIBUTE = 4;

	/**
	 * Reference type: global variable reference. The first step is a variable with a string or
	 * number bound to it.
	 */
	public final static int REFERENCE_GLOBAL_VARIABLE = 5;

	/** The initial step is an attribute of a model element */
	public final static int STEP_ATTRIBUTE = 0;

	/** The initial step is a model element */
	public final static int STEP_MODEL_ELEMENT = 1;

	/** The initial step is a variable reference */
	public final static int STEP_VARIABLE = 2;

	/** The step separator constant */
	public final static String SEPARATOR = ".";

	/**
	 * Return the "current context" in which this reference was made. This is <b>only</b> valid if
	 * {@link #getReferenceType()} returns {@link #REFERENCE_RELATIVE_ATTRIBUTE}.
	 * <p>
	 * For an attribute reference, this returns the model element in which the attribute occurred.
	 * For example, in the rule <code>in each of the trades,
	 * the tradeDate is equal to '2005-12-12'</code>, calling this method on the <code>tradeDate</code> reference would
	 * return <code>Trade</code> or whatever the type of 'trades' was.
	 * 
	 * @return the element or null if wrong reference type
	 */
	public IModelElement getCurrentContext();

	/**
	 * Return the type of model reference this is: whether it starts with a variable, with a model
	 * element, or with an attribute.
	 * 
	 * @return the initial step type, one of the STEP constants defined in this interface
	 */
	public int getInitialStepType();

	/**
	 * Return the initial step. Depending on the result of {@link #getInitialStepType()} this will
	 * return:
	 * <ul>
	 * <li>STEP_ATTRIBUTE - an {@link net.sourceforge.nrl.parser.model.IAttribute} object
	 * <li>STEP_MODEL_ELEMENT - an {@link net.sourceforge.nrl.parser.model.IModelElement} object
	 * <li>STEP_VARIABLE - an {@link IVariable} object
	 * </ul>
	 * <p>
	 * <b>IMPORTANT:</b> This can only be called after a model has been loaded, because information
	 * from the model is needed to determine this.
	 * 
	 * @return the initial step, never null
	 */
	public Object getInitialStep();

	/**
	 * Return the original model reference string found in the NRL text.
	 * 
	 * @return the string
	 */
	public String getOriginalString();

	/**
	 * Get the last attribute in a model reference. For example:
	 * <ul>
	 * <li>If the reference is <code>attr1.attr2.attr3</code>, returns <code>attr3</code>
	 * <li>If the reference is <code>attr1</code>, returns <code>attr1</code>
	 * <li>If the reference is a pure variable or type reference, returns <b>null</b>
	 * 
	 * @return the last attribute or null if this was not possible
	 */
	public IAttribute getLastAttribute();

	/**
	 * Return the remaining steps after the initial step has been traversed. This is always a
	 * collection of {@link net.sourceforge.nrl.parser.model.IAttribute} objects.
	 * 
	 * @return the collection, can be empty
	 */
	public List<IAttribute> getRemainingSteps();

	/**
	 * Indicate what type of reference this is: to an attribute, to a static attribute or
	 * enumeration, etc. This returns one of {@link #REFERENCE_ELEMENT},
	 * {@link #REFERENCE_RELATIVE_ATTRIBUTE}, {@link #REFERENCE_STATIC_ATTRIBUTE},
	 * {@link #REFERENCE_TOP_CONTEXT_RELATIVE_ATTRIBUTE} or
	 * {@link #REFERENCE_VARIABLE_RELATIVE_ATTRIBUTE}.
	 * 
	 * @return the constant
	 */
	public int getReferenceType();

	/**
	 * Return the final target of a reference, which is always a model element. This is used to
	 * determine what the last step in a sequence of steps pointed to.
	 * <p>
	 * There is only one case where this can return null: If the initial step is a variable that
	 * points to a complex expression rather than a model element. In that case, retrieve the
	 * expression from the variable.
	 * 
	 * @return the target attribute
	 */
	public IModelElement getTarget();
}
