package de.fzi.decision.security.ui.models;

import org.eclipse.emf.common.util.URI;

import security.securityPatterns.PatternCatalog;
import security.securityPrerequisites.PrerequisiteCatalog;
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
}
