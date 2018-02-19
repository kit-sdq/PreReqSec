package parser;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import data.Triple;
import modelLoader.LoadingException;
import modelLoader.ModelLoader;
import modelLoader.ModelLoaderEngine;

/**
 * This class take a tupel of a target and set a of attributes to load models.
 *  
 * @author Benjamin Plach
 *
 */
public class QueryEvaluator {
	
	private final String SECURITYPATTERN = "securitypattern";
	private final String PREREQUISITE = "prerequisite";
	private final String ATTACK = "attack";
	private final String ATTACKERCLASS = "attackerclass";
	
	private ModelLoaderEngine modelLoaderEngine = null;

	/**
	 * Initializes a new GetQueryEvaluator
	 * 
	 * @param modelLoaderEngine
	 */
	QueryEvaluator(ModelLoaderEngine modelLoaderEngine) {
		this.modelLoaderEngine  = modelLoaderEngine;
	}

	/**
	 * Evaluates a tupel of a target and set a of attributes and returns the loaded models.
	 * 
	 * @param targetType the type of models that shall be loaded
	 * @param attributes the attributes of the models that shall be loaded and their referenced models
	 * @return the loaded models
	 * @throws InterpreterException thrown if the target or the attributes are illegal
	 * @throws LoadingException thrown if there are problems loading the results
	 */
	/*package*/ Collection<?> evaluateQuery(String targetType, Collection<Triple<String, String, String>> attributes) throws InterpreterException, LoadingException {
		ModelLoader loader = new ModelLoader(modelLoaderEngine);
		
		Iterator<Triple<String, String, String>> iterator = attributes.iterator();
		

		while (iterator.hasNext()) {
			Triple<String, String, String> attribute = iterator.next();
			
			String identifier = attribute.getFirst();
			String attributeName = attribute.getSecond();
			String attributeValue = attribute.getThird();
						
			switch (identifier) {
			case SECURITYPATTERN:
				addSecurityPatternFilter(loader, identifier, attributeName, attributeValue);				
				break;
				
			case PREREQUISITE:
				addPrerequisiteFilter(loader, identifier, attributeName, attributeValue);
				break;
				
			case ATTACK:
				addAttackFilter(loader, identifier, attributeName, attributeValue);
				break;
	
			case ATTACKERCLASS:
				addAttackerClassFilter(loader, identifier, attributeName, attributeValue);
				break;

			default:
				throw new InterpreterException(identifier + " is no valid model-type (" + SECURITYPATTERN + ", " + PREREQUISITE + ", " + ATTACK + " or " + ATTACKERCLASS + " are allowed)");
			}
		}
		
		switch (targetType) {
		case SECURITYPATTERN:
			return loader.loadSecurityPatterns();
		case PREREQUISITE:
			return loader.loadPrerequisites();
		case ATTACK:
			return loader.loadAttacks();
		case ATTACKERCLASS:
			return loader.loadAttackers();
		default:
			throw new InterpreterException(targetType + "is not a valid target (" + SECURITYPATTERN + ", " + PREREQUISITE + ", " + ATTACK + " or " + ATTACKERCLASS + " are allowed)");
		}
	}
		
	
	private void addSecurityPatternFilter(ModelLoader loader, String identifier, String attributeName, String attributeValue) throws InterpreterException {
		switch (attributeName) {
		case "name":
			loader.filterBySecurityPatternName(attributeValue);
			break;		
			
		case "description":
			loader.filterBySecurityPatternDescription(attributeValue);
			break;	
				
		default:
			throw new InterpreterException(attributeName + " is no attribute of " + identifier);
		}
		
	}
	
	private void addPrerequisiteFilter(ModelLoader loader, String identifier, String attributeName, String attributeValue) throws InterpreterException {
		switch (attributeName) {				
		case "name":
			loader.filterByPrerequisiteName(attributeValue);
			break;
			
		case "description":
			loader.filterByPrerequisiteDescription(attributeValue);
			break;	
			
		default:
			throw new InterpreterException(attributeName + " is no attribute of " + identifier);
		}
	}
	
	private void addAttackFilter(ModelLoader loader, String identifier, String attributeName, String attributeValue) throws InterpreterException {
		switch (attributeName) {
		case "name":
			loader.filterByAttackName(attributeValue);
			break;
		
		case "description":
			loader.filterByAttackDescription(attributeValue);
			break;
		
		default:
			throw new InterpreterException(attributeName + " is no attribute of " + identifier);
		}	
	}

	private void addAttackerClassFilter(ModelLoader loader, String identifier, String attributeName, String attributeValue) throws InterpreterException {
		switch (attributeName) {
		case "name":
			loader.filterByAttackerName(attributeValue);
			break;					
		
		case "description":
			loader.filterByAttackerDescription(attributeValue);
			break;				
		
		default:
			throw new InterpreterException(attributeName + " is no attribute of " + identifier);
		}
	}
	
}
