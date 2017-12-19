package validation;

import java.util.Collection;
import java.util.Iterator;

import modelLoader.InitializationException;
import modelLoader.LoadingException;
import modelLoader.ModelLoaderEngine;
import parser.InterpreterException;
import parser.QueryInterpreter;
import security.NamedDescribedEntity;

public class ValidationScenario3 {

	private static ModelLoaderEngine engine = null;
	private static QueryInterpreter interpreter = null;
	
	public static void main(String[] args) throws InitializationException, InterpreterException, LoadingException {
		engine = new ModelLoaderEngine("validationCatalogs//ValidationCatalog3.security");
		interpreter = new QueryInterpreter(engine);
		
		//"Well, guess covering attacks that are based on a bot net wouldn't be such a bad idea"
		//What attacks are based on a bot net?
		runQuery("get attack with prerequisite.status = valid and prerequisite.name = botnetavailability");
		
		//find suitable security solution(s)
		runQuery("get securitypattern with prerequisite.name = botnetavailability");
	}	
	
	private static void runQuery(String query) throws InterpreterException, LoadingException {
		//run query
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
	}
}
