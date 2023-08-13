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
package net.sourceforge.nrl.parser.ast.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sourceforge.nrl.parser.IStatusCode;
import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.SemanticError;
import net.sourceforge.nrl.parser.ast.IModelReference;
import net.sourceforge.nrl.parser.ast.IVariable;
import net.sourceforge.nrl.parser.ast.constraints.IOperatorInvocation;
import net.sourceforge.nrl.parser.ast.constraints.IVariableDeclaration;
import net.sourceforge.nrl.parser.ast.constraints.impl.ModelReferenceImpl;
import net.sourceforge.nrl.parser.model.IAttribute;
import net.sourceforge.nrl.parser.model.IClassifier;
import net.sourceforge.nrl.parser.model.IModelCollection;
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.model.IPackage;
import net.sourceforge.nrl.parser.model.PrimitiveTypeFactory;
import net.sourceforge.nrl.parser.model.VariableContext;

/**
 * A helper class for resolving model references against a model. This class is mainly used by
 * {@link AntlrModelResolver}.
 * <p>
 * The job of the class is to navigate the model and resolve name strings to attributes or model
 * elements.
 * 
 * @author Christian Nentwich
 */
public class ModelReferenceHelper {

	/**
	 * Resolve a reference by looking up the initial step and traversing a path.
	 * <p>
	 * This implements a fall-back algorithm to find the initial step of the path and then resolve
	 * all further steps.
	 * <ul>
	 * <li>Look up the initial step as an attribute of the current context
	 * <li>Look up the initial step as an attribute of the rule/fragment context
	 * <li>Look up the initial step as an model element
	 * <li>Look up the initial step as a variable in scope
	 * <li>Look up the initial step as a global variable (can only be one step)
	 * </ul>
	 * If the fallback algorithm fails or encounters ambiguity, an error is raised. If any error at
	 * all occurs, the initial step on the model reference is set to <code>null</code> and the
	 * reference remains invalid.
	 * 
	 * @param ref the reference to resolve
	 * @param models the models to look up elements in
	 * @param contextElement the "current" (nearest enclosing) context element
	 * @param initialContext the overall rule/fragment context
	 * @param variableContext a stack of {@link IVariable} objects, with variable declarations in
	 * scope
	 * @param errors a collection of semantic errors to add to if necessary
	 */
	public static void resolveReference(ModelReferenceImpl ref, IModelCollection models,
			IModelElement contextElement, IClassifier initialContext,
			VariableContext variableContext,
			Map<String, IVariableDeclaration> globalVariableContext, List<NRLError> errors) {

		List<String> steps = ref.getStepsAsStrings();
		if (steps.size() == 0)
			return;

		ref.resetSteps();

		// -------------------- INITIAL STEP ------------------------

		// Resolve the initial step - look it up as
		// 1. an attribute of the current context
		// 2. an attribute of the rule context
		// 3. a model element
		// 4. a local variable
		// 5. a global variable

		Iterator<String> stepIterator = steps.iterator();

		String firstStep = stepIterator.next();
		Object initialStep = null;

		// Attempt one: try as an attribute of the current context
		if (contextElement instanceof IClassifier) {
			initialStep = ((IClassifier) contextElement).getAttributeByName(firstStep, true);
			if (initialStep != null)
				ref.setCurrentContext(contextElement);
			ref.setReferenceType(IModelReference.REFERENCE_RELATIVE_ATTRIBUTE);
		}

		// Attempt two: try as attribute of rule context
		if (initialStep == null) {
			initialStep = initialContext.getAttributeByName(firstStep, true);
			ref.setReferenceType(IModelReference.REFERENCE_TOP_CONTEXT_RELATIVE_ATTRIBUTE);
		} else {
			// If an attribute is both in the rule context, and the current
			// context we need to raise an ambiguity error

			// if (contextElement != initialContext
			// && initialContext.getAttributeByName(firstStep, true) != null) {
			// initialStep = null;
			// raiseError(errors, ref, IStatusCode.ATTRIBUTE_AMBIGUOUS,
			// "Attribute reference ambiguous (occurs in "
			// + initialContext.getName() + " and "
			// + contextElement.getName() + "): " + firstStep);
			// return;
			// }
		}

		// Attempt three: try as model element
		if (initialStep == null) {
			initialStep = getModelElement(firstStep, ref, models, errors);
			ref.setReferenceType(IModelReference.REFERENCE_ELEMENT);
		}

		// Attempt four: try to look it up as a variable
		if (initialStep == null) {
			IVariable var = variableContext.lookup(firstStep);
			if (var != null) {
				initialStep = var;
				ref.setReferenceType(IModelReference.REFERENCE_VARIABLE_RELATIVE_ATTRIBUTE);
			}
		}

		// Final attempt: global variable
		if (initialStep == null) {
			IVariableDeclaration decl = (IVariableDeclaration) globalVariableContext.get(firstStep);

			if (decl != null && steps.size() > 1) {
				raiseError(errors, ref, IStatusCode.GLOBAL_VARIABLE_NAVIGATION,
						"Reference to global variable '" + firstStep
								+ "' cannot be followed by additional steps.");
			}

			if (decl != null) {
				initialStep = decl.getVariableReference();
				ref.setReferenceType(IModelReference.REFERENCE_GLOBAL_VARIABLE);
			}
		}

		// Nothing worked - produce an error
		if (initialStep == null) {
			raiseError(errors, ref, IStatusCode.UNKNOWN_ELEMENT_OR_ATTRIBUTE,
					"Reference to non-existent attribute, model element or variable: " + firstStep);
			return;
		}

		// Check if the reference starts with a classifier and is ambiguous
		ref.setInitialStep(initialStep);
		if (steps.size() == 1)
			return;

		// -------------------- REMAINING STEPS ------------------------

		// currentContext will be the context as we step through the
		// reference. Set it to the initial context for now.

		IClassifier currentContext = null;

		if (initialStep instanceof IClassifier)
			currentContext = (IClassifier) initialStep;
		else if (initialStep instanceof IAttribute) {
			IAttribute attr = (IAttribute) initialStep;

			if (attr.getType() instanceof IClassifier)
				currentContext = (IClassifier) attr.getType();
		} else if (initialStep instanceof IVariable) {
			IVariable var = (IVariable) initialStep;

			if (var.isBoundToElement()) {
				if (var.getBoundElement() instanceof IClassifier)
					currentContext = (IClassifier) var.getBoundElement();
			} else if (var.getBoundExpression() instanceof IOperatorInvocation) {
				// More explicit error for operators
				raiseError(errors, ref, IStatusCode.ILLEGAL_VARIABLE_NAVIGATION, "Variable '"
						+ var.getName()
						+ "' is bound to an operator, but used for navigation. You need to define"
						+ " a valid return type for this operator");
				ref.resetSteps();
				return;
			} else {
				raiseError(errors, ref, IStatusCode.ILLEGAL_VARIABLE_NAVIGATION, "Variable '"
						+ var.getName()
						+ "' is bound to a complex expression. Navigation not allowed in "
						+ ref.getOriginalString());
				ref.resetSteps();
				return;
			}
		}

		// If we have steps <code>a.b</code> and <code>a</code> wasn't
		// bound to a classifier, we have to give up: nothing else can
		// contain <code>b</code>

		if (currentContext == null) {
			raiseError(errors, ref, IStatusCode.DATATYPE_NAVIGATION,
					"Model reference has multiple steps but does not start with a classifier or enumeration: "
							+ ref.getOriginalString());
			ref.resetSteps();
			return;
		}

		// Keep looking up the referenced attribute name in the current
		// context until we get to the end of the steps
		while (stepIterator.hasNext()) {

			// Look up the next step attribute
			String step = stepIterator.next();

			IAttribute attr = currentContext.getAttributeByName(step, true);
			if (attr == null) {
				raiseError(errors, ref, IStatusCode.UNKNOWN_ATTRIBUTE, "Attribute " + step
						+ " does not exist in " + currentContext.getName() + ", in reference: "
						+ ref.getOriginalString());
				ref.resetSteps();
				return;
			}
			ref.addStep(attr);

			// Now, double-check that we are inside a classifier if there
			// are still more steps to be looked up
			if (stepIterator.hasNext()) {
				if (!(attr.getType() instanceof IClassifier)) {
					String typeName = attr.getType() != null ? attr.getType().getName() : "";
					typeName += " ";
					raiseError(errors, ref, IStatusCode.DATATYPE_NAVIGATION, "Attribute " + step
							+ " cannot occur in the middle of a reference because its type "
							+ typeName + "has no attributes.");
					ref.resetSteps();
					return;
				}

				currentContext = (IClassifier) attr.getType();
			} else {
				// If there are no more steps then in the case where the
				// initial step was a direct classifier reference, the
				// attribute has to be static, or the classifier has to
				// be the rule context

				if (initialStep instanceof IClassifier) {
					if (initialStep != initialContext && !attr.isStatic()) {
						raiseError(errors, ref,
								IStatusCode.STATIC_REFERENCE_TO_NONSTATIC_ATTRIBUTE,
								"Attribute reference " + step
										+ " not allowed because the attribute is not static.");
						ref.resetSteps();
						return;
					}

					if (attr.isStatic())
						ref.setReferenceType(IModelReference.REFERENCE_STATIC_ATTRIBUTE);
					else
						ref
								.setReferenceType(IModelReference.REFERENCE_TOP_CONTEXT_RELATIVE_ATTRIBUTE);
				}
			}
		}

		//Check that references to enum literals are rooted at the enum type

		IAttribute lastAttribute = ref.getLastAttribute();
		if(lastAttribute != null){
			IClassifier lastAttributeOwner = lastAttribute.getOwner();
			if(lastAttributeOwner.isEnumeration() && (ref.getInitialStepType() != IModelReference.STEP_MODEL_ELEMENT || ref.getRemainingSteps().size() > 2)){
				raiseError(errors, ref,
						IStatusCode.ACCESS_TO_ENUM_LITERAL_NOT_THROUGH_CLASSIFIER,
						"Enum reference " + lastAttribute.getName()
								+ " not allowed because the parent is not the enum classifier.");
				ref.resetSteps();
				return;				
			}
		}
		
		// In cases where we have a context-relative attribute, like
		// Trade.date (where Trade is the rule context), we now want to
		// remove the context step - because the reference type clarifies
		// that anyway

		if (ref.getReferenceType() == IModelReference.REFERENCE_TOP_CONTEXT_RELATIVE_ATTRIBUTE
				&& ref.getInitialStepType() == IModelReference.STEP_MODEL_ELEMENT) {
			ref.shiftStepsLeft();
		}
	}

	/**
	 * Look up a model element by name, handling both absolute and qualified names.
	 * 
	 * @param elementName the element name
	 * @param ref the reference in which the name occurred
	 * @param models the models to search
	 * @param errors the error list to add to if an error occurs
	 * @return the element or null if not found
	 */
	public static IModelElement getModelElement(String elementName, ModelReferenceImpl ref,
			IModelCollection models, List<NRLError> errors) {

		// If the element name is qualified, traverse the packages, else
		// look up directly

		if (elementName.indexOf("::") != -1) {
			StringTokenizer tokenizer = new StringTokenizer(elementName, "::");

			// Traverse the first step by looking up the model package
			String step = tokenizer.nextToken();
			IPackage current = models.getModelPackageByName(step);

			if (current == null) {
				if (ref != null)
					raiseError(errors, ref, IStatusCode.INVALID_PACKAGE_REFERENCE, "Step '" + step
							+ "' referred to in reference is not a package name.");
				return null;
			}

			// Traverse the packages (overwrite 'elementName' with the
			// last step, which is the name reference)
			while (tokenizer.hasMoreTokens()) {
				step = tokenizer.nextToken();

				// Last step? This is the element name, get out
				if (!tokenizer.hasMoreTokens()) {
					elementName = step;
					break;
				}

				IModelElement resolvedStep = current.getElementByName(step, false);
				if (!(resolvedStep instanceof IPackage)) {
					if (ref != null)
						raiseError(errors, ref, IStatusCode.INVALID_PACKAGE_REFERENCE, "Step '"
								+ step + "' referred to in reference is not a package name.");
					return null;
				}
				current = (IPackage) resolvedStep;
			}

			// Now look up the element
			return current.getElementByName(elementName, false);
		} else {
			IModelElement result = models.getElementByName(elementName);

			if (result != null && models.isAmbiguous(elementName)) {
				if (ref != null)
					raiseError(errors, ref, IStatusCode.ELEMENT_AMBIGUOUS, "Element '"
							+ elementName
							+ "' occurs in multiple packages, need absolute reference.");
				return null;
			}

			// Try a primitive type
			if (result == null) {
				result = PrimitiveTypeFactory.getInstance().getType(elementName);
			}

			return result;
		}
	}

	/**
	 * Helper method to raise errors on model references more efficiently.
	 * 
	 * @param errors
	 * @param statusCode
	 * @param message
	 */
	protected static void raiseError(List<NRLError> errors, ModelReferenceImpl ref, int statusCode,
			String message) {
		errors.add(new SemanticError(statusCode, ref.getLine(), ref.getColumn(), ref
				.getOriginalString().length(), message));
	}
}
