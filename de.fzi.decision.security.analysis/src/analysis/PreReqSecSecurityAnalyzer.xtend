package analysis

import java.util.ArrayList
import java.util.Collection
import java.util.HashSet
import java.util.List
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.palladiosimulator.mdsdprofiles.api.StereotypeAPI
import security.securityPatterns.Role
import security.securityPatterns.SecurityPattern
import security.securityPrerequisites.Prerequisite
import security.securityThreats.Attack
import org.eclipse.emf.common.util.EList

/**
 * PreReqSecSecurityAnalyzer provides security analysis capabilities 
 * following the PreReqSec methodology. 
 * 
 * @author Fabian Scheytt, Robert Hochweiss
 */
class PreReqSecSecurityAnalyzer {

	private static String SECURITY_PATTERN_NAME = "SecurityPatternRole"
	private static String SECURITY_PATTERN_VALUE_NAME = "roles"
	private static String PREREQ_NAME = "ComponentPrerequisite"
	private static String PREREQ_VALUE_NAME = "prerequisites"

	private ModelQueryEngine engine

	/**
	 * Analyzes the attack surface of given element according to the PreReqSec methodology
	 * and returns a List of possible Attacks
	 *  
	 * @param component The element on which the security analysis should be executed
	 * @return A list of possible attacks on the given element
	 */
	def List<Attack> analyze(EObject component) {
		engine = new ModelQueryEngine(new EMFScope(component.eResource))

		var unmitigatedPreq = getAnnotatedPrerequisites(component)

		val mitigatedPrerequisites = new ArrayList<Prerequisite>()
		getAnnotatedSecurityPatterns(component).forEach [
			// check if all roles of the pattern are applied anywhere in the model
			if (patternCorrectlyApplied(it)) 
				mitigatedPrerequisites.addAll(this.getMitigatedPrerequisites(it))
			else {
				println("!Warning: Pattern " + it.name + " is not correctly applied")
			}
		]
		unmitigatedPreq.removeAll(mitigatedPrerequisites)

		if (!unmitigatedPreq.empty) {
			// Add security catalog to scope
			val scope = new HashSet<ResourceSet>(
				#[unmitigatedPreq.get(0).eResource.resourceSet, component.eResource.resourceSet]
			)
			engine = new ModelQueryEngine(new EMFScope(scope));
		}
		getPossibleAttacks(unmitigatedPreq)
	}

	/**
	 * Finds all Prerequisites that are annotated to an EObject by StereotypeApplications
	 * @param object The Object to query
	 * @returns List of annotated Prerequisites
	 */
	private def List<Prerequisite> getAnnotatedPrerequisites(EObject object) {
		val stereotypeValues = getAnnotatedStereotypeValue(object, PREREQ_NAME, PREREQ_VALUE_NAME)
		val prerequisites = new ArrayList<Prerequisite>()
		if (stereotypeValues instanceof EList<?>) {
			stereotypeValues.forEach [
				if (it instanceof Prerequisite) {
					prerequisites.add(it)
				}
			]
		}
		prerequisites
	}

	/**
	 * Queries an EObject for Stereotypes and returns all SecurityPatterns that are indirectly annotated to the object by applied security pattern roles.
	 * @param object The Object to query
	 * @returns A list of all indirectly annotated SecurityPatterns
	 */
	private def List<SecurityPattern> getAnnotatedSecurityPatterns(EObject object) {
		val stereotypeValues = getAnnotatedStereotypeValue(object, SECURITY_PATTERN_NAME, SECURITY_PATTERN_VALUE_NAME)
		val patterns = new ArrayList<SecurityPattern>()
		if (stereotypeValues instanceof EList<?>) {
			stereotypeValues.forEach [
				if (it instanceof Role)
					patterns.add(it.securityPattern)
			]
		}
		// Ensure that no Pattern can appear more than once
		patterns.toSet.toList
	}

	/**
	 * Gets the value of a stereotype application on an EObject
	 * @param target The Eobject to query for stereotypes
	 * @param stereotypeName Name of the StereotypeApplication pattern
	 * @param stereotypeValueName Name of the StereotypeApplication value
	 * @returns The value of the stereotype application
	 */
	private def Object getAnnotatedStereotypeValue(EObject target, String stereotypeName, String stereotypeValueName) {
		val queryResult = engine.getStereotypesOnElement(target)
		for (value : queryResult) {
			if (value !== null && value.stereotype !== null && value.stereotype.name == stereotypeName) {
				return StereotypeAPI.getTaggedValue(target, stereotypeValueName, stereotypeName)
			}
		}
		null
	}

	/**
	 * Gets all Prerequisites that are mitigated by a SecurityPattern
	 * @param pattern The SecurityPattern to get the Prerequisites from
	 * @returns A list of Prerequisites or an empty list
	 */
	private def getMitigatedPrerequisites(SecurityPattern pattern) {
		if (pattern !== null && pattern.mitigatedPrerequisites !== null)
			pattern.mitigatedPrerequisites
		else
			#[]
	}
	
	/**
	 * Finds all Attacks whose required Prerequisites are a subset of the unmitigated Prerequisites.
	 * @param unmitigatedPrerequisites All Prerequisites that are satisfied in the current context
	 * @returns possible Attacks derived of the unmitigated prerequisites
	 */
	private def getPossibleAttacks(Collection<Prerequisite> unmitigatedPrerequisites) {
		engine.getAttacksByPrerequisites(unmitigatedPrerequisites).toList // Check for each Attack if all prerequisites are unmitigated
		.filter [
			unmitigatedPrerequisites.containsAll(it.prerequisites)
		].toList
	}
	
	/**
	 * Evaluates if the given pattern is correctly applied by querying all applied PatternRole stereotypes of the instance.
	 * A pattern is deemed correctly applied if each role is assigned at least once!
	 * @param pattern The security pattern to check appliance for
	 * @returns true if pattern is correctly applied
	 */
	private def boolean patternCorrectlyApplied(SecurityPattern pattern) {
		engine.allStereotypeApplications.map[
			if (it !== null && it.stereotype !== null && it.stereotype.name == SECURITY_PATTERN_NAME)
				return StereotypeAPI.getTaggedValue(it.appliedTo, SECURITY_PATTERN_VALUE_NAME, SECURITY_PATTERN_NAME)
			else
				null
		].filter[
			it !== null
		].flatten.toList.containsAll(pattern.roles)
	}
}