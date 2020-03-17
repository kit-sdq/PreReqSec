package de.fzi.decision.security.ui.analysis

import analysis.PreReqSecSecurityAnalyzer
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.jface.action.IAction
import org.palladiosimulator.pcm.core.composition.AssemblyContext
import org.palladiosimulator.pcm.core.entity.Entity

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
//			val analysisResultsPerObject = new ArrayList<Pair<List<SecurityPattern>, List<Attack>>> 
			rootContainer.eContents.forEach [
				// Analyze only AssemblyContexts, other EObjects are not relevant right now for the security analysis
				if (it !== null && it instanceof AssemblyContext) {
					prettyPrintAttacksPossible(analyzer.analyze(it), it)
				}
			]
		}
	}
	
}
