package validation;

import java.util.Collection;
import java.util.Iterator;

import modelLoader.InitializationException;
import modelLoader.LoadingException;
import modelLoader.ModelLoaderEngine;
import parser.InterpreterException;
import parser.QueryInterpreter;
import security.NamedDescribedEntity;

public class ValidationScenario_g {
	
	private static ModelLoaderEngine engine = null;
	private static QueryInterpreter interpreter = null;	
	private static SecurityPatternAnalysis spa = null;
	
	public static void main(String[] args) throws InitializationException, InterpreterException, LoadingException {
		
		engine = new ModelLoaderEngine("validationCatalogs//ValidationCatalog01.security");
		interpreter = new QueryInterpreter(engine);
		spa = new SecurityPatternAnalysis();
		
//		//devs clueless -> show all 
		runQuery("get prerequisite");
		runQuery("get attack");
		runQuery("get securitypattern");
//		
//		//set status of all prerequisites
////		//get possible attacks and necessary security patterns
		runQuery("get attack with prerequisite.status = valid");		
		runQuery("get attack with prerequisite.status = invalid");		

//		runQuery("get securitypattern with prerequisite.status = valid");
//		runQuery("get attack with prerequisite.status = valid and prerequisite.name = botnetavailability");
//		runQuery("get attack with prerequisite.name = botnetavailability");
//		runQuery("get securitypattern with prerequisite.status = valid and prerequisite.name = botnetavailability");
		
		runQuery("get prerequisite with attack.name = crosssitescripting");
		runQuery("get prerequisite with attack.name = crosssiterequestforgery");
		runQuery("get prerequisite with securitypattern.name = htmlinputvalidation");
		runQuery("get prerequisite with prerequisite.status = valid and securitypattern.name = htmlinputvalidation");

//		runAnalysis(getPrerequisites("get prerequisite with attack.name = dos"), 
//				getPrerequisites("get prerequisite with securitypattern.name = packetfilterfirewall"));
		
//		runAnalysis(getPrerequisites("get prerequisite with attack.name = distributeddos"), 
//				getPrerequisites("get prerequisite with securitypattern.name = packetfilterfirewall"));
		
		spa.runThreatAnalysis(spa.getPrerequisites(interpreter, "get prerequisite with prerequisite.status = valid and attack.name = httpflooding"), 
				spa.getPrerequisites(interpreter, "get prerequisite with prerequisite.status = valid and securitypattern.name = packetfilterfirewall"));
		
		spa.runThreatAnalysis(spa.getPrerequisites(interpreter, "get prerequisite with prerequisite.status = valid and attack.name = tcpsynflooding"), 
				spa.getPrerequisites(interpreter, "get prerequisite with prerequisite.status = valid and securitypattern.name = packetfilterfirewall"));
	
		spa.runThreatAnalysis(spa.getPrerequisites(interpreter, "get prerequisite with prerequisite.status = valid and attack.name = crosssitescripting"), 
				spa.getPrerequisites(interpreter, "get prerequisite with prerequisite.status = valid and securitypattern.name = packetfilterfirewall"));
		
		spa.runThreatAnalysis(spa.getPrerequisites(interpreter, "get prerequisite with prerequisite.status = valid and attack.name = crosssitescripting"), 
				spa.getPrerequisites(interpreter, "get prerequisite with prerequisite.status = valid and securitypattern.name = htmlinputvalidation"));
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
