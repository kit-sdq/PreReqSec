package de.fzi.decision.security.ui.analysis

import java.util.List
import org.eclipse.emf.ecore.EObject
import org.eclipse.jface.action.IAction
import org.eclipse.jface.viewers.ISelection
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.ui.IActionDelegate
import org.eclipse.ui.actions.ActionDelegate
import org.palladiosimulator.pcm.core.entity.Entity
import security.securityThreats.Attack
import analysis.PreReqSecSecurityAnalyzer

/**
 * Controls the security analysis in the editor
 */
class SecurityAnalysisController extends ActionDelegate implements IActionDelegate {

	protected var EObject selection;

	/**
	 * Executed once the context menu item is clicked
	 */
	override void run(IAction action) {
		super.run(action);
		if (selection !== null && selection instanceof EObject){
			val analyzer = new PreReqSecSecurityAnalyzer()
			var possibleAttacks = analyzer.analyze(selection)
			prettyPrintAttacksPossible(possibleAttacks, selection)
		}
	}

	/**
	 * Executed on selection change in the instance editor
	 */
	override void selectionChanged(IAction action, ISelection newSelection) {
		if (newSelection instanceof IStructuredSelection) {
			val object = (newSelection as IStructuredSelection).getFirstElement()
			if (object instanceof EObject) {
				selection = object as EObject
				action.setEnabled(selection !== null)
				return;
			}
		}
		selection = null
		action.setEnabled(false);
	}
	
	private def prettyPrintAttacksPossible(List<Attack> attacks, EObject component) {
		if(component === null || attacks === null) return;		
				
		val name = switch component {
//			NamedElement : component.name
			Entity : component.entityName
			default : component.toString
		}
		if(attacks.size == 0){
			println('''No attacks possible on element «name»''')
		} else {		
			println('''«attacks.size» « if(attacks.size>1) "attacks" else "attack" » possible:''')
			attacks.forEach[
				println('''Attack «it.name» possible on Element «name»!''')
			]		
		}
	}
}
