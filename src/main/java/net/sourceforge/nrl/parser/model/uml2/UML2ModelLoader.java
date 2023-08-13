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
package net.sourceforge.nrl.parser.model.uml2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.nrl.parser.model.IAttribute;
import net.sourceforge.nrl.parser.model.IClassifier;
import net.sourceforge.nrl.parser.model.IPackage;
import net.sourceforge.nrl.parser.model.loader.IModelLoader;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.ClassNotFoundException;
import org.eclipse.emf.ecore.xmi.PackageNotFoundException;
import org.eclipse.emf.ecore.xmi.UnresolvedReferenceException;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.AssociationClass;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.resource.UMLResource;

/**
 * A model loader that uses the UML2 and EMF classes to populate an NRL model.
 * 
 * @author Christian Nentwich
 */
public class UML2ModelLoader {

	private List<String> warnings = new ArrayList<String>();

	// A list of attributes that represent schema facets - no warnings
	// will be raised for those
	private static Set<String> MODEL_ARTIFACT_ATTRIBUTES = new HashSet<String>();

	static {
		MODEL_ARTIFACT_ATTRIBUTES.add("length");
		MODEL_ARTIFACT_ATTRIBUTES.add("minLength");
		MODEL_ARTIFACT_ATTRIBUTES.add("maxLength");
		MODEL_ARTIFACT_ATTRIBUTES.add("pattern");
		MODEL_ARTIFACT_ATTRIBUTES.add("pattern1");
		MODEL_ARTIFACT_ATTRIBUTES.add("pattern2");
		MODEL_ARTIFACT_ATTRIBUTES.add("pattern3");
		MODEL_ARTIFACT_ATTRIBUTES.add("whiteSpace");
		MODEL_ARTIFACT_ATTRIBUTES.add("totalDigits");
		MODEL_ARTIFACT_ATTRIBUTES.add("fractionDigits");
		MODEL_ARTIFACT_ATTRIBUTES.add("wildcard");
		MODEL_ARTIFACT_ATTRIBUTES.add("minInclusive");
		MODEL_ARTIFACT_ATTRIBUTES.add("maxInclusive");
		MODEL_ARTIFACT_ATTRIBUTES.add("schemaLocation");
		MODEL_ARTIFACT_ATTRIBUTES.add("resolveProxies");
	}

	/**
	 * Return the list of warning strings created during the last load operation.
	 * 
	 * @return the warnings
	 */
	public List<String> getWarnings() {
		return warnings;
	}

	/**
	 * Load a model contained in the specified file. This will break up the model into an NRL model.
	 * <p>
	 * The method currently also ignores all ClassNotFoundException and PackageNotFoundException
	 * errors, to ignore problems where a profile is not found.
	 * 
	 * @deprecated Use the {@link IModelLoader} interface to load models.
	 * 
	 * @param file the file to load
	 * @return the model
	 * @throws Exception
	 */
	@Deprecated
	public IPackage load(File file) throws Exception {
		Resource res = new ResourceSetImpl().createResource(URI.createFileURI(file
				.getAbsolutePath()));
		return load(res, file);
	}

	/**
	 * Internal function to load using an ECore resource. Used for testing.]#
	 * 
	 * @deprecated Use the {@link IModelLoader} interface to load models.
	 */
	@Deprecated
	public IPackage load(Resource res, File file) throws Exception {
		warnings.clear();

		try {
			res.load(null);
		} catch (Resource.IOWrappedException e) {
			// These we will ignore for the time being
		} catch (IOException e) {
			throw e;
		}

		// Scan the errors. If they are all ClassNotFoundException or
		// PackageNotFoundException, we will log a warning, but won't do
		// anything
		for (int i = 0; i < res.getErrors().size(); i++) {
			Object obj = res.getErrors().get(i);

			if (obj instanceof PackageNotFoundException || obj instanceof ClassNotFoundException
					|| obj instanceof UnresolvedReferenceException) {

				// HOT FIX for EMX loading!
				// Do not raise warnings for Diagram class or classes in IBM
				// packages. This should be relatively harmless for other types
				// of models.

				if (((Exception) obj).getMessage().indexOf("http://www.ibm.com/xtools/1.5.0") != -1)
					continue;
				if (((Exception) obj).getMessage().indexOf("Class 'Diagram'") != -1)
					continue;

				warnings.add("Missing profile? " + ((Exception) obj).getMessage());
			} else if (obj instanceof Exception)
				throw (Exception) obj;
		}

		return loadFromInitialisedResource(res, file.getName());
	}

	/**
	 * Load a UML2 model out of an already initialised resource. The resource must have already been
	 * loaded without errors, and an NRL model is constructed from it.
	 * 
	 * @deprecated Use the {@link IModelLoader} interface to load models, or
	 * {@link #load(Resource, Map)} if you must.
	 * @param res the resource
	 * @param fileName the file name, for error reporting purposes
	 * @return the model
	 * @throws Exception
	 */
	@Deprecated
	public IPackage loadFromInitialisedResource(Resource res, String fileName) throws Exception {
		return load(res);
	}

	/**
	 * Load a UML2 model out of an already initialised resource. The resource must have already been
	 * loaded without errors, and an NRL model is constructed from it.
	 * 
	 * @param res the resource
	 * @return the model
	 * @throws Exception
	 */
	public IPackage load(Resource res) throws Exception {
		if (res.getContents().isEmpty()) {
			try {
				res.load(null);
			} catch (Resource.IOWrappedException e) {
				// These we will ignore for the time being
			} catch (IOException e) {
				throw e;
			}
		}

		// Check that it really is a UML model
		if (res.getContents().isEmpty()) {
			throw new Exception("Model file " + res.getURI().toString()
					+ " contained no model after parsing.");
		}

		if (!(res.getContents().get(0) instanceof org.eclipse.uml2.uml.Package)) {
			throw new Exception("Model file " + res.getURI().toString()
					+ " did not contain an UML 2 Model as expected. Found instead: "
					+ res.getContents().get(0).getClass().getName());
		}
		org.eclipse.uml2.uml.Package umlModel = (org.eclipse.uml2.uml.Package) res.getContents()
				.get(0);
		UML2Package result = new UML2Package(umlModel, null);

		EcoreUtil.resolveAll(res.getResourceSet());

		// Traverse the UML model and put it into our "result" model
		List<Association> associations = new ArrayList<Association>();
		traverse(umlModel, result, false, associations);

		// The traversal may have triggered the lazy loading of further models
		// e.g. type libraries - add them all into this model
		for (int i = 0; i < res.getResourceSet().getResources().size(); i++) {
			Resource otherResource = res.getResourceSet().getResources().get(i);
			if (otherResource instanceof UMLResource && otherResource != res
					&& !otherResource.getContents().isEmpty()) {
				EObject resourceObject = otherResource.getContents().get(0);
				if (resourceObject instanceof org.eclipse.uml2.uml.Package) {
					boolean isSupplementary = resourceObject instanceof Profile
							|| (otherResource.getURI() != null && "pathmap".equals(otherResource
									.getURI().scheme()));
					traverse((Package) resourceObject, result, isSupplementary, associations);
				}
			}
		}

		Map<Classifier, IClassifier> classifierToUML2Classifier = result.buildTypeMap();
		resolveAssociations(associations, classifierToUML2Classifier, warnings);
		result.resolveTypes(classifierToUML2Classifier, warnings);

		// Remove groups, sequences and choices introduced by hypermodel
		result.removeHyperModelArtifacts();

		return result;
	}

	/**
	 * Search the UML model for Associations and populate referenced classes with the attributes
	 * defined within the association's owned navigable end.
	 * 
	 * @param warnings the list to append warnings to
	 */
	private void resolveAssociations(List<Association> associations,
			Map<Classifier, IClassifier> classifierToUML2Classifier, List<String> warnings) {
		for (Association association : associations) {
			EList<Property> memberEnds = association.getMemberEnds();
			if (memberEnds.size() != 2) {
				warnings.add(String.format(
						"Skipping association '%s', must have exactly two ends.", association
								.getName()));
				continue;
			}
			if (!(memberEnds.get(0).getType() instanceof Classifier)
					|| !(memberEnds.get(1).getType() instanceof Classifier)) {
				warnings.add(String.format(
						"Skipping association '%s', ends must point to classes.", association
								.getName()));
				continue;
			}

			Map<Property, Property> otherEnd = new HashMap<Property, Property>();
			otherEnd.put(memberEnds.get(0), memberEnds.get(1));
			otherEnd.put(memberEnds.get(1), memberEnds.get(0));

			for (Property targetEnd : association.getNavigableOwnedEnds()) {
				Type targetType = targetEnd.getType();
				if (targetType == null || !(targetType instanceof Classifier)
						|| !classifierToUML2Classifier.containsKey(targetType)) {
					warnings.add(String.format("Skipping association '%s', unknown end type.",
							association.getName()));
					break;
				}

				Property sourceProperty = otherEnd.get(targetEnd);
				Type sourceType = sourceProperty.getType();
				UML2Classifier sourceNrlType = (UML2Classifier) classifierToUML2Classifier
						.get(sourceType);

				if (targetEnd.getName() != null && !targetEnd.getName().equals("")) {
					sourceNrlType.addAttribute(new UML2Attribute(targetEnd));
				} else {
					sourceNrlType.addAttribute(new UML2Attribute(targetEnd, targetEnd.getType()
							.getName()));
				}
				if (association instanceof AssociationClass) {
					sourceNrlType.addAttribute(new UML2Attribute(association, sourceProperty
							.getLower(), sourceProperty.getUpper()));
				}
			}
		}
	}

	/**
	 * Traverse a package and store all its classifier members
	 * 
	 * @param umlPackage package
	 * @param current the current package to add to
	 */
	protected void traverse(Package umlPackage, UML2Package current, boolean setAsSupplementary,
			List<Association> associations) {
		for (Iterator<?> iter = umlPackage.getOwnedMembers().iterator(); iter.hasNext();) {
			Object obj = iter.next();

			if (obj instanceof Package) {
				UML2Package newPackage = new UML2Package((Package) obj, current);
				newPackage.setSupplementary(setAsSupplementary);
				current.addElement(newPackage);

				traverse((Package) obj, newPackage, setAsSupplementary, associations);
			} else if (obj instanceof Enumeration) {
				traverse((Enumeration) obj, current, setAsSupplementary);
			} else if (obj instanceof DataType) {
				traverse((DataType) obj, current, setAsSupplementary);
			} else if (obj instanceof Classifier && !(obj instanceof Association)) {
				traverse((Classifier) obj, current, setAsSupplementary);
			} else if (obj instanceof Classifier && obj instanceof Association) {
				associations.add((Association) obj);
				if (obj instanceof AssociationClass) {
					traverse((AssociationClass) obj, current, setAsSupplementary);
				}
			}
		}
	}

	/**
	 * Traverse a classifier, store its generalizations and attributes
	 * 
	 * @param umlClassifier the classifier
	 * @param current the package to add to
	 */
	protected void traverse(Classifier umlClassifier, UML2Package current,
			boolean setAsSupplementary) {
		// Don't process no-name classifiers
		if (umlClassifier.getName() == null || umlClassifier.getName().trim().equals("")) {
			return;
		}

		// Set up an element from the classifier
		UML2Classifier element = new UML2Classifier(umlClassifier, current);
		element.setSupplementary(setAsSupplementary);
		current.addElement(element);

		// Go through the generalizations, and store the superclass reference
		// for later resolution
		for (int i = 0; i < umlClassifier.getGeneralizations().size(); i++) {
			Generalization gen = umlClassifier.getGeneralizations().get(i);
			if (gen.getSpecific() == umlClassifier)
				element.setUMLSuperClass(gen.getGeneral());
		}

		// Add the attributes
		for (int i = 0; i < umlClassifier.getAttributes().size(); i++) {
			Property prop = umlClassifier.getAttributes().get(i);

			if (prop.getName() == null || prop.getName().equals("")) {
				continue;
			}

			if (prop.getType() != null) {
				UML2Attribute attr = new UML2Attribute(prop);

				element.addAttribute(attr);
				attr.setOwner(element);
			} else {
				if (!MODEL_ARTIFACT_ATTRIBUTES.contains(prop.getName())) {
					warnings.add("Eliminated attribute without type: " + prop.getName());
				}
			}
		}

		if (umlClassifier instanceof AssociationClass) {
			AssociationClass associationClass = (AssociationClass) umlClassifier;
			for (Property memberEnd : associationClass.getMemberEnds()) {
				if(memberEnd.getType() == null){
					warnings.add("Eliminated member end with no type: " + memberEnd.getName());
					continue;
				}
				if (memberEnd.getName() != null && !memberEnd.getName().equals("")) {
					UML2Attribute attr = new UML2Attribute(memberEnd, 1, 1);
					element.addAttribute(attr);
					attr.setOwner(element);
				} else {
					UML2Attribute attr = new UML2Attribute(memberEnd, memberEnd.getType().getName(), 1, 1);
					element.addAttribute(attr);
					attr.setOwner(element);
				}
			}
		}

		// Inline anonymous nested subclasses
		for (Iterator<?> iter = umlClassifier.getOwnedElements().iterator(); iter.hasNext();) {
			Object next = iter.next();

			if (!(next instanceof Classifier)) {
				continue;
			}
			Classifier nested = (Classifier) next;
			if (nested.getName() != null && nested.getName().startsWith("anonymous")) {

				for (int i = 0; i < nested.getAttributes().size(); i++) {
					Property prop = nested.getAttributes().get(i);

					if (prop.getName() == null || prop.getName().equals("")) {
						continue;
					}

					if (prop.getType() != null) {
						UML2Attribute attr = new UML2Attribute(prop);

						element.addAttribute(attr);
						attr.setOwner(element);
					} else {
						if (!MODEL_ARTIFACT_ATTRIBUTES.contains(prop.getName())) {
							warnings.add("Eliminated attribute without type: " + prop.getName());
						}
					}
				}
			}
		}

		// Inline hypermodel artifacts as necessary
		// Hypermodel artifacts? inline them
		boolean needsReplacements = true;
		while (needsReplacements) {
			needsReplacements = false;

			for (Iterator<IAttribute> iter = element.getAttributes().iterator(); iter.hasNext();) {
				UML2Attribute attr = (UML2Attribute) iter.next();
				Object umlElement = attr.getUserData(IUML2UserData.UML2_ELEMENT);
				if (umlElement instanceof Property) {
					Property prop = (Property) umlElement;
					if (prop.getType() instanceof Classifier
							&& (prop.getName().startsWith("_choice") || prop.getName().startsWith(
									"_sequence"))) {
						needsReplacements = true;
						iter.remove();

						Classifier other = (Classifier) prop.getType();

						for (int i = 0; i < other.getAttributes().size(); i++) {
							Property otherProp = other.getAttributes().get(i);

							if (otherProp.getName() == null || otherProp.getName().equals(""))
								continue;

							if (otherProp.getType() != null) {
								UML2Attribute otherAttr = new UML2Attribute(otherProp);
								element.addAttribute(otherAttr);
								otherAttr.setOwner(element);
							}
						}

						break;
					}
				}
			}
		}
	}

	/**
	 * Traverse a classifier, store its generalizations and attributes
	 * 
	 * @param umlEnumeration the classifier
	 * @param current the package to add to
	 */
	protected void traverse(Enumeration umlEnumeration, UML2Package current,
			boolean setAsSupplementary) {
		// Don't process no-name enums
		if (umlEnumeration.getName() == null || umlEnumeration.getName().trim().equals("")) {
			warnings.add("Eliminated enumeration with empty name.");
			return;
		}

		// Set up an element from the classifier
		UML2Classifier element = new UML2Classifier(umlEnumeration, current);
		element.setEnumeration(true);
		element.setSupplementary(setAsSupplementary);
		current.addElement(element);

		// Go through the generalizations, and store the superclass reference
		// for later resolution
		for (int i = 0; i < umlEnumeration.getGeneralizations().size(); i++) {
			Generalization gen = umlEnumeration.getGeneralizations().get(i);
			if (gen.getSpecific() == umlEnumeration)
				element.setUMLSuperClass(gen.getGeneral());
		}

		// Add the literal strings as attributes
		for (int i = 0; i < umlEnumeration.getOwnedLiterals().size(); i++) {
			Object obj = umlEnumeration.getOwnedLiterals().get(i);
			if (obj instanceof EnumerationLiteral) {
				UML2Attribute attr = new UML2Attribute((EnumerationLiteral) obj);

				element.addAttribute(attr);
				attr.setOwner(element);
			}
		}
	}

	/**
	 * Traverse a data type
	 * 
	 * @param umlDataType the data type
	 * @param current the package to add to
	 */
	protected void traverse(DataType umlDataType, UML2Package current, boolean setAsSupplementary) {
		// Don't process no-name classifiers
		if (umlDataType.getName() == null || umlDataType.getName().trim().equals("")) {
			warnings.add("Eliminated data type with empty name.");
			return;
		}

		// Set up an element from the classifier
		UML2DataType element = new UML2DataType(umlDataType, current);
		element.setSupplementary(setAsSupplementary);
		current.addElement(element);

		// Go through the generalizations, and store the superclass reference
		// for later resolution
		for (int i = 0; i < umlDataType.getGeneralizations().size(); i++) {
			Generalization gen = umlDataType.getGeneralizations().get(i);
			if (gen.getSpecific() == umlDataType)
				element.setUMLSuperClass(gen.getGeneral());
		}

		// Add the attributes
		for (int i = 0; i < umlDataType.getAttributes().size(); i++) {
			Property prop = umlDataType.getAttributes().get(i);

			if (prop.getName() == null || prop.getName().equals(""))
				continue;

			if (prop.getType() != null) {
				UML2Attribute attr = new UML2Attribute(prop);

				element.addAttribute(attr);
				attr.setOwner(element);
			} else {
				if (!MODEL_ARTIFACT_ATTRIBUTES.contains(prop.getName())) {
					warnings.add("Eliminated attribute without type: " + prop.getName());
				}
			}
		}

	}

}
