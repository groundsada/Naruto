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

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.nrl.parser.ast.IModelFileReference;
import net.sourceforge.nrl.parser.ast.IOperatorFileReference;
import net.sourceforge.nrl.parser.ast.IRuleFile;
import net.sourceforge.nrl.parser.ast.action.impl.ActionAstResolver;
import net.sourceforge.nrl.parser.ast.impl.Antlr3NRLTreeAdaptor;
import net.sourceforge.nrl.parser.ast.impl.AntlrModelResolver;
import net.sourceforge.nrl.parser.ast.impl.AntlrOperatorResolverVisitor;
import net.sourceforge.nrl.parser.ast.impl.NRLActionParser;
import net.sourceforge.nrl.parser.ast.impl.NRLJFlexer;
import net.sourceforge.nrl.parser.ast.impl.RuleFileImpl;
import net.sourceforge.nrl.parser.model.IModelCollection;
import net.sourceforge.nrl.parser.model.IPackage;
import net.sourceforge.nrl.parser.model.ModelCollection;
import net.sourceforge.nrl.parser.model.loader.IModelLoader;
import net.sourceforge.nrl.parser.model.loader.ModelLoadingException;
import net.sourceforge.nrl.parser.model.loader.OperatorLoadingException;
import net.sourceforge.nrl.parser.operators.IOperators;
import net.sourceforge.nrl.parser.operators.XmlOperatorLoader;
import net.sourceforge.nrl.parser.preprocessing.ReferencePreprocessor;
import net.sourceforge.nrl.parser.resolver.IResolverFactory;
import net.sourceforge.nrl.parser.resolver.IURIResolver;
import net.sourceforge.nrl.parser.resolver.ResolverException;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

/**
 * The main parser interface for creating an NRL AST. This supports both the action and constraint
 * language, and is based on ANTLR.
 * 
 * @see net.sourceforge.nrl.parser.INRLParser
 */
public class NRLParser implements INRLParser {

	private List<NRLError> errors = new ArrayList<NRLError>();

	/**
	 * After any call to the parse or resolve methods, this method returns a list of errors, if any.
	 */
	public List<NRLError> getErrors() {
		return errors;
	}

	/**
	 * Uses a reader to read a stream and returns the content as a string.
	 * 
	 * @param reader the reader
	 * @return the content
	 * @throws IOException
	 */
	protected String getStreamAsString(Reader reader) throws IOException {
		StringBuffer result = new StringBuffer();

		char[] buffer = new char[10000];
		while (reader.ready()) {
			int size = reader.read(buffer);
			result.append(new String(buffer, 0, size));
			if (size != 10000)
				break;
		}

		return result.toString();
	}

	/**
	 * @deprecated Use {@link #parse(URI, IResolverFactory)}
	 */
	@Deprecated
	public IRuleFile parse(InputStream stream) throws Exception {
		return parse(new InputStreamReader(stream));
	}

	/**
	 * @deprecated Use {@link #parse(URI, IResolverFactory)}
	 */
	@Deprecated
	public IRuleFile parse(Reader reader) throws Exception {
		errors = new ArrayList<NRLError>();

		String content = getStreamAsString(reader);

		// Pre-process
		ReferencePreprocessor processor = new ReferencePreprocessor();
		content = processor.process(content);

		// Parse
		NRLJFlexer lexer = new NRLJFlexer(new StringReader(content));
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);

		NRLActionParser parser = new NRLActionParser(tokenStream);
		parser.setTreeAdaptor(new Antlr3NRLTreeAdaptor());
		RuleFileImpl ruleFile = (RuleFileImpl) parser.fileBody().getTree();

		// Syntax errors? Get out now
		if (parser.getSyntaxErrors().size() > 0) {
			errors = parser.getSyntaxErrors();
			return null;
		}

		// Resolve the tree and return it (and set errors)
		ActionAstResolver resolver = new ActionAstResolver();
		errors = resolver.resolve(ruleFile);
		return ruleFile;
	}

	/**
	 * Parse an NRL file using a reader, and return an AST. The AST will already be fully resolved
	 * and checked for basic semantic errors, as well as having model information attached.
	 * <p>
	 * <b>IMPORTANT:</b> Call {@link #getErrors()} after calling this method to check if any syntax
	 * or semantic errors occurred.
	 * 
	 * @param reader the reader to use
	 * @param resolverFactory the resolver factory to use to load resources
	 * @return the rule file or null
	 * @since 1.4.9
	 */
	public IRuleFile parse(URI uri, IResolverFactory resolverFactory) throws IOException,
			RecognitionException, ModelLoadingException, ResolverException,
			OperatorLoadingException {
		errors.clear();

		IURIResolver uriResolver = resolverFactory.createURIResolver();

		String content = getNRLFileAsString(uri, uriResolver);
		content = preprocess(content);

		IRuleFile ruleFile = parse(content);
		if (ruleFile == null) {
			return null;
		}

		IModelLoader modelLoader = resolverFactory.createModelLoader();
		resolveOperators(uri, uriResolver, modelLoader, ruleFile);
		resolveModels(uri, modelLoader, ruleFile);

		return ruleFile;
	}

	private void resolveOperators(URI uri, IURIResolver uriResolver, IModelLoader modelLoader,
			IRuleFile ruleFile) throws ResolverException, OperatorLoadingException {
		IOperators[] operators = loadOperators(ruleFile, uriResolver, modelLoader, uri);
		AntlrOperatorResolverVisitor visitor = new AntlrOperatorResolverVisitor(operators);
		ruleFile.accept(visitor);

		errors.addAll(visitor.getErrors());
	}

	private ModelCollection resolveModels(URI uri, IModelLoader modelLoader,
			IRuleFile ruleFile) throws ModelLoadingException {
		ModelCollection models = new ModelCollection();

		for (IModelFileReference reference : ruleFile.getModelFileReferences()) {
			IPackage model = modelLoader.loadModel(uri, reference.getFileName());
			models.addModelPackage(model);
			reference.resolveModel(model);
		}

		AntlrModelResolver modelResolver = new AntlrModelResolver(models);
		errors = modelResolver.resolve(ruleFile);
		return models;
	}

	private RuleFileImpl parse(String content) throws RecognitionException {
		// Parse
		NRLJFlexer lexer = new NRLJFlexer(new StringReader(content));
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);

		NRLActionParser parser = new NRLActionParser(tokenStream);
		parser.setTreeAdaptor(new Antlr3NRLTreeAdaptor());
		RuleFileImpl ruleFile = (RuleFileImpl) parser.fileBody().getTree();

		// Syntax errors? Get out now
		if (parser.getSyntaxErrors().size() > 0) {
			errors = parser.getSyntaxErrors();
			return null;
		}

		// Resolve the tree and return it (and set errors)
		ActionAstResolver resolver = new ActionAstResolver();
		errors = resolver.resolve(ruleFile);
		if (errors.size() > 0) {
			return null;
		}
		return ruleFile;
	}

	private String preprocess(String content) {
		ReferencePreprocessor processor = new ReferencePreprocessor();
		return processor.process(content);
	}

	private String getNRLFileAsString(URI uri, IURIResolver uriResolver) throws ResolverException,
			IOException {
		InputStream stream = null;
		String content = null;
		try {
			stream = uriResolver.openStream(uri);
			content = getStreamAsString(new InputStreamReader(stream));
			stream.close();
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		return content;
	}

	private IOperators[] loadOperators(IRuleFile ruleFile, IURIResolver uriResolver,
			IModelLoader modelLoader, URI baseURI) throws ResolverException,
			OperatorLoadingException {
		List<IOperators> operators = new ArrayList<IOperators>();
		for (IOperatorFileReference operatorFileReference : ruleFile.getOperatorFileReferences()) {
			String operatorFileName = operatorFileReference.getFileName();

			XmlOperatorLoader loader = new XmlOperatorLoader(
					modelLoader, uriResolver);
			try {
				URI operatorFileUri = new URI(org.eclipse.emf.common.util.URI.encodeQuery(operatorFileName, true));
				loader.load(baseURI, operatorFileUri, errors);
			} catch (Exception e) {
				throw new OperatorLoadingException(format("Failed to load operators from : %s",
						operatorFileName));
			}

			IOperators loadedOperatorFile = loader.getOperators();
			operatorFileReference.resolveOperators(loadedOperatorFile);

			operators.add(loadedOperatorFile);
		}
		return operators.toArray(new IOperators[] {});
	}

	/**
	 * Attach model information to a parsed rule file AST. This traverses the AST and decorates any
	 * model references with actual elements from the model.
	 * <p>
	 * After calling this, you <b>must</b> call {@link #getErrors()} to check if any errors
	 * occurred. If so, the AST may be unstable.
	 * 
	 * @param ruleFile the rule file to decorate
	 * @param models the models to use
	 * @Deprecated No need to load models separately any longer. Use
	 * {@link #parse(Reader, IResolverFactory)}.
	 */
	@Deprecated
	public void resolveModelReferences(IRuleFile ruleFile, IModelCollection models) {
		errors = new ArrayList<NRLError>();

		AntlrModelResolver resolver = new AntlrModelResolver(models);
		errors = resolver.resolve(ruleFile);
	}

	/**
	 * Resolve reference to operator invocations given an operator collection. The operator
	 * collection has to be loaded separately from an XML descriptor file, if one is available.
	 * 
	 * @see net.sourceforge.nrl.parser.operators.XmlOperatorPersistence
	 * @Deprecated No need to load operators separately any longer. Use
	 * {@link #parse(Reader, IResolverFactory)}.
	 */
	@Deprecated
	public void resolveOperatorReferences(IRuleFile ruleFile, IOperators[] operators) {
		AntlrOperatorResolverVisitor visitor = new AntlrOperatorResolverVisitor(operators);
		ruleFile.accept(visitor);

		errors = visitor.getErrors();
	}
}