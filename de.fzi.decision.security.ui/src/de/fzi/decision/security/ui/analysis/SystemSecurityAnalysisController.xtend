package de.fzi.decision.security.ui.analysis

import analysis.PreReqSecSecurityAnalyzer
import java.util.ArrayList
import java.util.HashMap
import java.util.List
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.jface.action.IAction
import org.palladiosimulator.pcm.core.entity.Entity
import security.securityThreats.Attack
import org.palladiosimulator.pcm.core.composition.AssemblyContext

/**
 * Controls the security analysis for a whole system in the editor.
 * 
 * @author Robert Hochweiss
 */
class SystemSecurityAnalysisController extends SecurityAnalysisController {

	/**
	 * Executed once the context menu item is clicked
	 */
	override void run(IAction action) {
		if (selection !== null && selection instanceof EObject) {
			// Use the root object of the selected element as starting point for the system analysis 
			val rootContainer = EcoreUtil.getRootContainer(selection)
			val sysName = switch rootContainer {
			Entity : rootContainer.entityName
			default : rootContainer.toString
			}
			println("\n" + "Analyzing the system " + sysName + " for security vulnerabilities:")
			val analyzer = new PreReqSecSecurityAnalyzer()
			val structAnalysisResults = new ArrayList<String>()
			val possibleAttacksPerObject = new HashMap<EObject, List<Attack>>()
			rootContainer.eContents.forEach [
				// Analyze only AssemblyContexts, other EObjects are not relevant right now for the security analysis
				if (it !== null && it instanceof AssemblyContext) {
					possibleAttacksPerObject.put(it, analyzer.analyze(it, structAnalysisResults))
				}
			]
			structAnalysisResults.forEach [
				println(it)
			]
			possibleAttacksPerObject.forEach [k,v|
				prettyPrintAttacksPossible(v,k)
			]
		}
	}
	
}
