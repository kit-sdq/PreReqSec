package scenarios;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import analysis.InitializationException;
import analysis.LoadingException;
import analysis.ModelAnalyzer;
import analysis.PcmModelLoaderEngine;
import analysis.SecurityModelLoaderEngine;
import security.NamedDescribedEntity;
import security.securityPrerequisites.Prerequisite;

public class ValidationScenario1 {
	
	private static ModelAnalyzer analyzer;
	
	public static void main(String[] args) throws InitializationException {

		PcmModelLoaderEngine systemEngine = new PcmModelLoaderEngine("assets//cocome-cloud.system");
		SecurityModelLoaderEngine securityEngine = new SecurityModelLoaderEngine("assets//basicScenario.security");
		analyzer = new ModelAnalyzer(securityEngine, systemEngine);

		printAnalysisFor("_GY2HALF-EeaCwqxzEIieVA");
	}

	private static void printAnalysisFor(String id) {
		
		// Start printing
		System.out.println(" > Running Test for " + id);
		
		// Print prerequisites
		String outputString = "Prerequisites: [ ";
		try {
			for (Prerequisite prereq : analyzer.getPrerequisites(id)) {
				outputString += prereq.getName() + " ";
			}
		} catch (Exception e) {
			outputString += "EXCEPTION ";
		}
		outputString = outputString + "]";
		System.out.println(outputString);
		
		// Stop printing
		System.out.println();
	}

}
