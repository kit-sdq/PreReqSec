package validation;

import java.util.Collection;
import java.util.Iterator;

import modelLoader.InitializationException;
import modelLoader.LoadingException;
import modelLoader.ModelLoaderEngine;
import parser.InterpreterException;
import parser.QueryInterpreter;
import security.NamedDescribedEntity;
import security.securityPatterns.SecurityPattern;

public class SecurityPatternAnalysis {

//	private static ModelLoaderEngine engine = null;

	boolean runThreatAnalysis(Collection<NamedDescribedEntity> resultsOfAttack, Collection<NamedDescribedEntity> resultsOfSecurityPattern) throws InterpreterException, LoadingException {
		
		boolean result;
		
	    result = (resultsOfAttack.size( ) <= resultsOfSecurityPattern.size()
	    		&& resultsOfAttack.isEmpty() == false)
	    		&& (resultsOfSecurityPattern.containsAll(resultsOfAttack));

	    System.out.println("Analysis result ... " + result);
	    System.out.println();
	    return result;
	}
	
	Collection<NamedDescribedEntity> getPrerequisites (QueryInterpreter interpreter, String query) throws InterpreterException, LoadingException {
		//get prerequisites
		@SuppressWarnings("unchecked")
		Collection<NamedDescribedEntity> results = (Collection<NamedDescribedEntity>) interpreter.interpretQuery(query);

		//print query
		System.out.println(" > " + query);
		
		String outputString = "[";
		Iterator<NamedDescribedEntity> iterator = results.iterator();
		while (iterator.hasNext()) {
			outputString = outputString + iterator.next().getName() + ", "; 
		}
		if (outputString.length() > 1) {
			outputString = outputString.substring(0, outputString.length() - 2);//cut of last ", " if there is one
		}
		outputString = outputString + "]";
		
		System.out.println(outputString);
		System.out.println();
		
		return results;
	}

}
