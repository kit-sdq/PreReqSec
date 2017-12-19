package parser;

import java.util.Collection;
import java.util.HashSet;
import java.util.StringTokenizer;

import data.Triple;
import data.Tupel;

/**
 * This class is a lexer for the following grammar.
 * 
 * START -> get_TARGET_WITH
 * TARGET -> securitypattern 
 * 			| prerequisite
 * 			| attack
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
 * @author Benjamin Plach 
 *
 */
public class QueryParser {

	private final String GET = "get";
	private final String WITH = "with";
	
	private final String SECURITYPATTERN = "securitypattern";
	private final String PREREQUISITE = "prerequisite";
	private final String ATTACK = "attack";
	private final String ATTACKERCLASS = "attackerclass";
	
	private final String DOT = "\\.";	
	private final String EQUALS = "=";
	
	private final String AND = "and";
	
	private final String END = "\0";
		
	private final String splitAt = " \t\n"; //(whitespace, tab and newline )
	
	private StringTokenizer t = null;
	
	private String targetType = null;
	
	private Collection<Triple<String, String, String>> attributes = null;	
	
	/**
	 * Creates a Lexer that for the following grammar:
	 * 
	 * START -> get_TARGET_WITH
	 * TARGET -> securitypattern 
	 * 			| prerequisite
	 * 			| attack
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
	 */	
	QueryParser() {
		
	}
	
	/**
	 * Parses a query of the following grammar into a tupel of its target and its attribute values
	 * 
	 * START -> get_TARGET_WITH
	 * TARGET -> securitypattern 
	 * 			| prerequisite
	 * 			| attack
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
	 * @param query the query to be parsed
	 * @return a tupel with the target of the query and the defined attributes
	 * @throws InterpreterException thrown if the query is illegal
	 */
	/*package*/ Tupel<String, Collection<Triple<String, String, String>>> parseQuery(String query) throws InterpreterException {
		//init tokenizer (add \0 as a end-sign)
		this.t = new StringTokenizer(query.toLowerCase() + " " + END, splitAt);
		
		//init filters hashmap
		this.attributes = new HashSet<Triple<String, String, String>>();

		//start the parsing
		start();
		
		//return the parsed query
		return new Tupel<String, Collection<Triple<String, String, String>>>(targetType, attributes);
	}
	
	/* START -> get_TARGET_WITH */
	private void start() throws InterpreterException {
		switch (t.nextToken()) {
		case GET:
			target();
			with();
			break;

		default:
			throw new InterpreterException("Query has to start with \"get\"");
		}
	}
	
	/* TARGET -> securitypattern 
	 * 			| prerequisite
	 * 			| attack with
	 * 			| attackerclass */
	private void target() throws InterpreterException {
		switch (t.nextToken()) {
		case SECURITYPATTERN:
			targetType = SECURITYPATTERN;
			break;

		case PREREQUISITE:
			targetType = PREREQUISITE;
			break;
			
		case ATTACK:
			targetType = ATTACK;
			break;
			
		case ATTACKERCLASS:
			targetType = ATTACKERCLASS;
			break;
			
		default:
			throw new InterpreterException("Query has to define a target (" + SECURITYPATTERN + ", " + PREREQUISITE + ", " + ATTACK + " or " + ATTACKERCLASS + ")");
		}
	}
	
	/* WITH -> $ | with_ATTRIBUTE_MOREATTIRBUTES */
	private void with() throws InterpreterException {
		switch (t.nextToken()) {
		case WITH:
			attribute();
			moreAttributes();
			break;
			
		case END:
			break;

		default:
			throw new InterpreterException("At this point a query has to end or start defining conditions via \"with\"");
		}
	}
	
	/* MOREATTIRBUTES -> $ | and_ATTRIBUTE_MOREATTIRBUTES */
	private void moreAttributes() throws InterpreterException {
		switch (t.nextToken()) {
		case AND:
			attribute();
			moreAttributes();
			break;
				
		case END:
			break;

		default:
			throw new InterpreterException("At this point a query has to end or define more conditions via \"and\"");
		}				
	}

	/* ATTRIBUTENAME -> CHAR*
	 * ATTRIBUTEVALUE -> CHAR* 
	 * 				| "(CHAR|_)*" 
	 * 				| [CHAR*(_,CHAR*)*]
	 * 
	 * CHAR-> (A-Z|a-z|0-9) */
	private void attribute() throws InterpreterException {
		
		//split identifier.attributeName into [identifier, attributeName]
		String[] identifierDotName = t.nextToken().split(DOT);
		if (identifierDotName.length != 2) {			
			throw new InterpreterException("Attributes have to be of following form: modelType.attributeName = attributeValue");
		}		
		String identifier = identifierDotName[0];
		String attributeName = identifierDotName[1];
		
		if ((t.nextToken().equals(EQUALS)) == false) {
			throw new InterpreterException("Attributes have to be of following form: modelType.attributeName = attributeValue");
		}
		
		//case: CHAR* //for enums
		String attributeValue = t.nextToken();
		
		//case: "CHAR*" //for strings
		if (attributeValue.startsWith("\"")) { //name = "..", desc = "..."
			if (attributeValue.endsWith("\"")) {
				//all done (just "oneWord")
			} else { // "abc 
				while (true) {
					String currentToken = t.nextToken();
					if (currentToken.equals(END)) {
						throw new InterpreterException("Attribute values starting with \" have to end with \"");
					}
					attributeValue = attributeValue + " " + currentToken;
					if (currentToken.endsWith("\"")) {
						break;
					}
				}
			}
			//cut off starting- and ending-quotation marks
			attributeValue = attributeValue.substring(1, attributeValue.length() - 1);
		}
		
		//case: [CHAR*(, CHAR*)*] //for arrays
		if (attributeValue.startsWith("[")) {
			if (attributeValue.endsWith("]")) {
				// all done (just [oneValue])
			} else { //"[abc"
				while (true) {
					String currentToken = t.nextToken();
					if (currentToken.equals(END)) {
						throw new InterpreterException("Attribute values starting with \"[\" have to end with \"]\"");
					}
					attributeValue = attributeValue + currentToken;
					if (currentToken.endsWith("]")) { //end of input "array" ([value, anotherValue]) reached
						break;
					}
				}
			}
			//cut off starting- and ending-brackets
			attributeValue = attributeValue.substring(1, attributeValue.length() - 1);
		}
		
		//add the attribute to the attribute collection
		attributes.add(new Triple<String, String, String>(identifier, attributeName, attributeValue));
	}
}