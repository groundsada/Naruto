/*
 * The contents of this file are subject to the Mozilla Public License 
 * Version 1.1 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See 
 * the License for the specific language governing rights and limitations 
 * under the License.
 * 
 * The Original Code is the NRL Parser, released 28 April 2006, 
 * Copyright (c) Christian Nentwich. The Initial Developer of the 
 * Original Code is Christian Nentwich. Portions created by contributors 
 * identified in the NOTICES file are Copyright (c) the contributors. 
 * All Rights Reserved. 
 */
package net.sourceforge.nrl.parser.operators;

/**
 * This is a simple tag-value interface for associating auxiliary mapping
 * information with operators and their parameters.
 * <p>
 * The tags (labels) of the details are mapping specific and have to be fixed by
 * implementors. Examples might include:
 * <ul>
 * <li><code>(JAVA_CLASS,"MyClass")</code>, to provide the Java class that
 * implements an operator
 * <li><code>(PARAM_TYPE,"int")</code>, to provide a concrete type for a
 * parameter
 * </ul>
 * <p>
 * Which labels are actually provided needs to be specified in the
 * implementation.
 * 
 * @author Christian Nentwich
 * 
 */
public interface IImplementationDetail {

	/**
	 * Return the label.
	 * 
	 * @return the label
	 */
	public String getLabel();

	/**
	 * Return the value.
	 * 
	 * @return the value
	 */
	public String getValue();
}
