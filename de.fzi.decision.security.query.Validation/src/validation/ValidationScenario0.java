package validation;

import java.util.Collection;
import java.util.Iterator;

import modelLoader.InitializationException;
import modelLoader.LoadingException;
import modelLoader.ModelLoaderEngine;
import parser.InterpreterException;
import parser.QueryInterpreter;
import security.NamedDescribedEntity;

public class ValidationScenario0 {
	
	private static ModelLoaderEngine engine = null;
	private static QueryInterpreter interpreter = null;	
	
	public static void main(String[] args) throws InitializationException, InterpreterException, LoadingException {
		engine = new ModelLoaderEngine("validationCatalogs//ValidationCatalog0.security");
		interpreter = new QueryInterpreter(engine);
		
		//devs clueless -> show all prerequisites
		runQuery("get prerequisite");
		
		//set status of all prerequisites
		//get possible attacks and necessary security patterns
		runQuery("get attack with prerequisite.status = valid");		
		runQuery("get securitypattern with prerequisite.status = valid");
		//none - great!
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
