package de.fzi.decision.security.ui.models;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

import security.securityPatterns.PatternCatalog;
import security.securityPatterns.SecurityPattern;
import security.securityPrerequisites.Prerequisite;
import security.securityPrerequisites.PrerequisiteCatalog;
import security.securityThreats.Attack;
import security.securityThreats.ThreatCatalog;

/**
 * Interface to load EMF SecurityContainer resources.
 */
public interface ISecurityContainer {
	
	/**
	 * Loads a EMF SecurityContainer resource 
	 * 
	 * @param uri The uri to the EMF SecurityContainer resource
	 */
	void load(URI uri);
	
	/**
	 * @return the PrerequisitesCatalog of the loaded SecurityContainer resource
	 */
	PrerequisiteCatalog getPrerequisiteCatalog();
	
	/**
	 * @return the PatternCatalog of the loaded SecurityContainer resource
	 */
	PatternCatalog getPatternCatalog();
	
	/**
	 * @return the AttackCatalog of the loaded SecurityContainer resource
	 */
	ThreatCatalog getAttackCatalog();
	
	/**
	 ** @return the URI of the loaded SecurityContainer resource
	 */
	URI getResourceURI();
	
	ResourceSet getResourceSet();
	
	void addSecurityPattern(SecurityPattern pattern);
	
	void deleteSecurityPattern(String id);

	void addPrerequisite(Prerequisite prerequisite);
	
	void deletePrerequisite(String id);
	
	void addAttack(Attack attack);
	
	void deleteAttack(String id);
	
}
