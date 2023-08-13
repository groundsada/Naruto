package net.sourceforge.nrl.parser.operators;

/**
 * A default implementation of the implementation detail interface.
 * 
 * @author Christian Nentwich
 */
public class ImplementationDetail extends PropertyAwareObject implements IImplementationDetail {

	/**
	 * Event - label changed.
	 */
	public final static String LABEL_CHANGED = "ImplementationDetailNameChanged";

	/**
	 * Event - value changed.
	 */
	public final static String VALUE_CHANGED = "ImplementationDetailValueChanged";

	private static final long serialVersionUID = -2251621148303061200L;

	private String label;

	private String value;

	public ImplementationDetail(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public ImplementationDetail(IImplementationDetail other) {
		this.label = other.getLabel();
		this.value = other.getValue();
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof ImplementationDetail))
			return false;

		ImplementationDetail other = (ImplementationDetail) obj;
		return other.label.equals(label) && other.value.equals(value);
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}

	public void setLabel(String label) {
		String old = this.label;
		this.label = label;
		firePropertyChange(LABEL_CHANGED, old, label);
	}

	public void setValue(String value) {
		String old = this.value;
		this.value = value;
		firePropertyChange(VALUE_CHANGED, old, value);
	}

}
