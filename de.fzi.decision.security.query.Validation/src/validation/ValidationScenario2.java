package validation;

import java.util.Collection;
import java.util.Iterator;

import modelLoader.InitializationException;
import modelLoader.LoadingException;
import modelLoader.ModelLoaderEngine;
import parser.InterpreterException;
import parser.QueryInterpreter;
import security.NamedDescribedEntity;

public class ValidationScenario2 {

	private static ModelLoaderEngine engine = null;
	private static QueryInterpreter interpreter = null;	
	
	public static void main(String[] args) throws InitializationException, InterpreterException, LoadingException {
		
		engine = new ModelLoaderEngine("validationCatalogs//ValidationCatalog1.security");
		interpreter = new QueryInterpreter(engine);
		
		//what attacks could a company perform?
		runQuery("get attack with attackerclass.name = company");	
		
		//what prerequisites do the attacks of a company require?
		runQuery("get prerequisite with attackerclass.name = company");
		
		//bot net prerequisite... to set or not to set? "guess not"
		//what attacks could a company perform on our system?
		runQuery("get attack with prerequisite.status = valid and attackerclass.name = company");
		
		//XSS is new -> find suitable security solution
		runQuery("get securitypattern with attack.name = crosssitescripting");
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
