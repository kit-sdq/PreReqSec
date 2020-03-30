package de.fzi.decision.security.ui.analysis.dialog;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

import de.fzi.decision.security.ui.analysis.dialog.views.SecurityAnalysisWindow;

/**
 * The main class of the PreReqSec-Dialog. Its the central entry point.
 * 
 * @author Robert Hochweiss
 */
public class Main extends ActionDelegate implements IActionDelegate {

	/**
	 * Executed once the context menu item is clicked. Just calls the main
	 * ApplicationWindow of the UI. This class is the entry point of the whole UI
	 * window.
	 */
	public void run(IAction action) {
		super.run(action);
		try {
			SecurityAnalysisWindow t = new SecurityAnalysisWindow();
			t.setBlockOnOpen(true);
			t.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
