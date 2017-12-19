package parser;

import java.util.Collection;
import data.Triple;

import data.Tupel;

import modelLoader.LoadingException;
import modelLoader.ModelLoaderEngine;

/**
 * This class parses queries and returns the results.
 * 
 * @author Benjamin Plach
 *
 */
public class QueryInterpreter {
	
	private ModelLoaderEngine engine = null;

	/**
	 * Initializes a new GetQueryParser
	 * 
	 * @param engine the engine with the model the queries are meant for
	 */
	public QueryInterpreter(ModelLoaderEngine engine) {
		this.engine = engine;
	}
	
	/**
	 * Parses a query of the following grammar and returns the results
	 * 
	 * START -> get_TARGET_WITH
	 * TARGET -> securitypattern 
	 * 			| prerequisite
	 * 			| attack with
	 * 			| attackerclass
	 * 
	 * WITH -> $ | with_ATTRIBUTE_MOREATTIRBUTES
	 * 
	 * MOREATTIRBUTES -> $ | and_ATTRIBUTE_MOREATTIRBUTES 
	 * 
	 * ATTRIBUTE -> IDENTIFIER.ATTRIBUTENAME_=_ATTRIBUTEVALUE
	 * 
	 * IDENTIFIER -> CHAR*
	 * ATTRIBUTENAME -> CHAR*
	 * ATTRIBUTEVALUE -> CHAR* 
	 * 				| "(CHAR|_)*" 
	 * 				| [CHAR*(_,CHAR*)*]
	 * 
	 * CHAR-> (A-Z|a-z|0-9)
	 * 
	 * @param query the query
	 * @return the results
	 * @throws InterpreterException thrown if the query is illegal
	 * @throws LoadingException thrown if there are problems loading the results
	 */
	public Collection<?> interpretQuery(String query) throws InterpreterException, LoadingException {
		QueryParser parser = new QueryParser();
		Tupel<String, Collection<Triple<String, String, String>>> results = parser.parseQuery(query);
		
		QueryEvaluator evaluator = new QueryEvaluator(engine);
		return evaluator.evaluateQuery(results.getFirst(), results.getSecond());
	}
}
