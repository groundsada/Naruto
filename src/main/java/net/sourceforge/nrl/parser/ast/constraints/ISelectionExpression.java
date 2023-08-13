package net.sourceforge.nrl.parser.ast.constraints;

import net.sourceforge.nrl.parser.ast.IModelReference;

/**
 * A selection expression: "the element where <i>constraint</i>".
 * 
 * @author Christian Nentwich
 */
public interface ISelectionExpression extends IExpression {

	/**
	 * Return the model reference from which the selection is being made. This
	 * must be a collection.
	 * 
	 * @return the reference
	 */
	public IModelReference getModelReference();

	/**
	 * Get the constraint that must hold for an element to be selected.
	 * 
	 * @return the constraint
	 */
	public IConstraint getConstraint();

	/**
	 * Return true if this selection is supposed to get a single element, false
	 * if it's selecting a list.
	 * 
	 * @return true if single element
	 */
	public boolean isSingleElementSelection();
}
