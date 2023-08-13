package net.sourceforge.nrl.parser.operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.nrl.parser.NRLError;
import net.sourceforge.nrl.parser.SemanticError;
import net.sourceforge.nrl.parser.ast.NRLDataType;
import net.sourceforge.nrl.parser.ast.impl.ModelReferenceHelper;
import net.sourceforge.nrl.parser.model.IDataType;
import net.sourceforge.nrl.parser.model.IModelCollection;
import net.sourceforge.nrl.parser.model.IModelElement;
import net.sourceforge.nrl.parser.model.PrimitiveTypeFactory;

/**
 * A default implementation of an operator parameter.
 * 
 * @author Christian Nentwich
 */
public class Parameter extends PropertyAwareObject implements IParameter {

	private static final long serialVersionUID = 8001204176434082829L;

	/**
	 * Event - name changed.
	 */
	public final static String NAMED_CHANGED = "ParameterNameChanged";

	/**
	 * Event - implementation details changed.
	 */
	public final static String IMPLEMENTATION_DETAILS_CHANGED = "ParameterImplementationDetailsChanged";

	/**
	 * Event - type changed.
	 */
	public final static String TYPE_CHANGED = "ParameterTypeChanged";

	/**
	 * Event - type collection indicator changed.
	 */
	public final static String TYPE_IS_COLLECTION_CHANGED = "ParameterTypeIsCollectionChanged";

	/**
	 * Event - type name changed.
	 */
	public final static String TYPE_NAME_CHANGED = "ParameterTypeNameChanged";

	private String name;

	private NRLDataType nrlType = NRLDataType.UNKNOWN;

	private IModelElement type;

	// Unresolved type name
	private String typeName;

	private Map<String, IImplementationDetail> implementationDetails = new HashMap<String, IImplementationDetail>();

	private boolean typeIsCollection = false;

	public Parameter() {
	}

	public Parameter(Parameter other) {
		this.name = other.name;
		this.nrlType = other.nrlType;
		this.type = other.type;
		this.typeName = other.typeName;
		this.typeIsCollection = other.typeIsCollection;

		for (IImplementationDetail detail : other.implementationDetails.values()) {
			addImplementationDetail(new ImplementationDetail(detail));
		}
	}

	public void addImplementationDetail(IImplementationDetail detail) {
		implementationDetails.put(detail.getLabel(), detail);
		firePropertyChange(IMPLEMENTATION_DETAILS_CHANGED, null, detail);
	}

	public String getImplementationDetail(String label) {
		IImplementationDetail detail = (IImplementationDetail) implementationDetails.get(label);
		if (detail != null)
			return detail.getValue();
		return null;
	}

	public Collection<IImplementationDetail> getImplementationDetails() {
		return implementationDetails.values();
	}

	public String getName() {
		return name;
	}

	public NRLDataType getNRLDataType() {
		return nrlType;
	}

	public IModelElement getType() {
		return type;
	}

	public String getTypeName() {
		return typeName;
	}

	public boolean isTypeCollection() {
		return typeIsCollection;
	}

	public List<NRLError> resolveModelReferences(IModelCollection models) {
		List<NRLError> errors = new ArrayList<NRLError>();
		if (typeName == null)
			return errors;

		if (getTypeName() != null) {
			PrimitiveTypeFactory factory = PrimitiveTypeFactory.getInstance();
			IDataType type = factory.getType(getTypeName());
			if (type != null) {
				setType(type);
				setNRLDataType(factory.getNrlType(getTypeName()));
			} else {
				IModelElement resolvedType = ModelReferenceHelper.getModelElement(getTypeName(),
						null, models, new ArrayList<NRLError>());

				if (resolvedType == null) {
					errors.add(new SemanticError("Cannot find type of parameter \"" + getName()
							+ "\" in models: " + getTypeName()
							+ ". Did you reference the right models?"));
				} else {
					setType(resolvedType);
				}
			}
		}

		return errors;

	}

	public void removeImplementationDetail(IImplementationDetail detail) {
		implementationDetails.remove(detail.getLabel());
		firePropertyChange(IMPLEMENTATION_DETAILS_CHANGED, detail, null);
	}

	public void setName(String name) {
		String old = this.name;
		this.name = name;
		firePropertyChange(NAMED_CHANGED, old, name);
	}

	public void setNRLDataType(NRLDataType type) {
		this.nrlType = type;
	}

	public void setType(IModelElement type) {
		IModelElement old = this.type;
		this.type = type;
		firePropertyChange(TYPE_CHANGED, old, type);
	}

	public void setTypeIsCollection(boolean isCollection) {
		this.typeIsCollection = isCollection;
		firePropertyChange(TYPE_IS_COLLECTION_CHANGED, !isCollection, isCollection);
	}

	public void setTypeName(String typeName) {
		String old = this.typeName;
		this.typeName = typeName;
		firePropertyChange(TYPE_NAME_CHANGED, old, typeName);
	}
}
