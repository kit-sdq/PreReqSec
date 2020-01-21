package analysis

import patterns.AttackByPrerequisiteMatcher
import patterns.StereotypesOnElementMatcher
import java.util.Collection
import java.util.HashSet
import java.util.Set
import org.eclipse.emf.ecore.EObject
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.modelversioning.emfprofileapplication.StereotypeApplication
import security.securityPrerequisites.Prerequisite
import security.securityThreats.Attack

/**
 * PreReqSec relevant query operations for model instances using EMF-Profile
 * stereotypes and PreReqSec security catalogs
 * 
 * @autor Fabian Scheytt, Robert Hochweiss
 */
class ModelQueryEngine {
	
	private val ViatraQueryEngine engine;
	
	/**
	 * Initializes the ModelQueryEngine
	 * @param scope A VIATRA EMF scope
	 */
	new(EMFScope scope) {
		engine = ViatraQueryEngine.on(scope);		
	}
	
	/**
	 * Queries all EMF-Profile StereotypeApplications on the given element
	 * @param element The EObject to query for StereotypeApplications
	 * @returns Set of applied StereotypeApplications
	 */
	def Set<StereotypeApplication> getStereotypesOnElement(EObject element) {
		val matcher = StereotypesOnElementMatcher.on(engine)
		matcher.getAllValuesOfstereotypeApplication(element)
	}
	
	/**
	 * Queries all Attacks that require <b>at least one</b> of the given prerequisites
	 * @param prerequisite Prerequisites to consider for query
	 * @returns Set of relevant Attacks 
	 */
	def Set<Attack> getAttacksByPrerequisites(Collection<Prerequisite> prerequisites) {
		val matcher = AttackByPrerequisiteMatcher.on(engine)
		val result = new HashSet<Attack>()
				
		prerequisites.forEach[
			val matches = matcher.getAllValuesOfattack(it)
			result.addAll(matches)
		]		
		result.toSet	
	}
	
	def Set<StereotypeApplication> getAllStereotypeApplications() {
		val matcher = StereotypesOnElementMatcher.on(engine)
		matcher.allValuesOfstereotypeApplication
	}
}
