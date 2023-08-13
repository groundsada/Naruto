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
package net.sourceforge.nrl.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.util.List;

import net.sourceforge.nrl.parser.ast.IRuleFile;
import net.sourceforge.nrl.parser.model.IModelCollection;
import net.sourceforge.nrl.parser.model.loader.ModelLoadingException;
import net.sourceforge.nrl.parser.model.loader.OperatorLoadingException;
import net.sourceforge.nrl.parser.operators.IOperators;
import net.sourceforge.nrl.parser.resolver.IResolverFactory;
import net.sourceforge.nrl.parser.resolver.ResolverException;

import org.antlr.runtime.RecognitionException;

/**
 * An INRLParser is a parser that can return a fully-resolved NRL abstract syntax tree.
 * <p>
 * The way this is normally used is as follows:
 * <ol>
 * <li>Call {@link #parse(InputStream)} to read in an AST
 * <li>If an exception is thrown, process it - the result will be null
 * <li>Call {@link #getErrors()}, which may return a mixture of syntax and semantic errors
 * if any occurred. If so, the AST is unsafe and the errors need to be displayed to a user
 * (or some other processing).
 * <li>Call {@link #resolveModelReferences(IRuleFile, IModelCollection)} to resolve model
 * references in the AST. Without this, no model information can be accessed through the
 * AST.
 * <li>Call {@link #getErrors()} again to check if any errors occurred during model
 * processing. If so, the AST is unsafe and needs to be discarded.
 * </ol>
 * <p>
 * The reason why parsing and resolving, and on the other hand resolving against a model
 * are kept separate is that the basic AST can be validated even before a model is
 * available, for example in an editor. For any sort of processing, a full resolution
 * against the model should be performed.
 */
public interface INRLParser {

	/**
	 * @deprecated Use {@link #parse(URI, IResolverFactory)}
	 */
	@Deprecated
	public IRuleFile parse(InputStream stream) throws Exception;

	/**
	 * @deprecated Use {@link #parse(URI, IResolverFactory)}
	 */
	@Deprecated
	public IRuleFile parse(Reader reader) throws Exception;

	/**
	 * Parse an input stream and construct an AST. Then resolve model references. After a
	 * call to this method, clients must:
	 * <ul>
	 * <li>Handle any exception and about processing
	 * <li>If no exception occurred, call {@link #getErrors()} to get a list of parsing
	 * errors
	 * </ul>
	 * <p>
	 * If no check on errors is made after parsing, the AST is unstable and will cause
	 * null pointer and other exceptions.
	 * 
	 * @param uri the NRL file URI
	 * @param resolverFactory the factory to use to load additional resources
	 * @return the rule file, which may contain errors
	 * 
	 * @since 1.4.9
	 */
	public IRuleFile parse(URI uri, IResolverFactory resolverFactory) throws IOException,
			RecognitionException, ResolverException, ModelLoadingException, OperatorLoadingException;

	/**
	 * Resolve model reference in the AST by looking up model elements. This can be called
	 * after {@link #parse(InputStream)}, if no errors occurred.
	 * <p>
	 * After a call to this method, {@link #getErrors()} may contain a number of semantic
	 * errors where model references could not be looked up.
	 * 
	 * @param ruleFile the rule file to resolve
	 * @param models the models to look at
	 * @deprecated Use {@link #parse(URI, IResolverFactory)}
	 */
	@Deprecated
	public void resolveModelReferences(IRuleFile ruleFile, IModelCollection models);

	/**
	 * Resolve operator references in the AST by looking up operator definitions in a
	 * collection of operators. This can only be called after {@link #parse(InputStream)},
	 * and after {@link #resolveModelReferences(IRuleFile, IModelCollection)}, if no
	 * errors occurred.
	 * <p>
	 * This call is kept separate to enable callers to check operator references
	 * separately - because missing definitions may or may not be treated as errors
	 * depending on the state of the rule file.
	 * <p>
	 * After a call to this, {@link #getErrors()} will contain semantic error objects if
	 * any operators could not be resolved properly.
	 * 
	 * @param ruleFile the file to resolve
	 * @param operators the collection of operator files
	 * @deprecated Use {@link #parse(URI, IResolverFactory)}
	 */
	@Deprecated
	public void resolveOperatorReferences(IRuleFile ruleFile, IOperators[] operators);

	/**
	 * Returns a collection of {@link net.sourceforge.nrl.parser.SyntaxError} and
	 * {@link net.sourceforge.nrl.parser.SemanticError} objects. This is intended for use
	 * after {@link #parse(InputStream)}, and returns a different result after every call
	 * to parse.
	 * 
	 * @return the collection of errors, empty if no errors occurred
	 */
	public List<NRLError> getErrors();
}
