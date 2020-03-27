package de.fzi.decision.security.ui.analysis.dialog;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * 
 * @author Robert Hochweiss
 */
public class Main extends ActionDelegate implements IActionDelegate {

	/**
	 * Executed once the context menu item is clicked. Just calls the main ApplicationWIndow of the UI.
	 * This class is the entry point of the whole UI window.
	 */
	public void run(IAction action) {
		super.run(action);
		try {
			SecurityAnalysisWindow t = new SecurityAnalysisWindow();
			t.setBlockOnOpen(true);
			t.open();
//			t.dispose();
			 Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
		new SecurityAnalysisWindow();
	}

}
