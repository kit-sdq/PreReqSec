package de.fzi.decision.security.ui.analysis.dialog.controller;

import org.eclipse.jface.action.Action;

import de.fzi.decision.security.ui.analysis.dialog.views.CCTabItem;
import de.fzi.decision.security.ui.analysis.dialog.views.SecurityAnalysisWindow;

/**
 * Action class that is responsible for controlling the structural analysis.
 * 
 * @author Robert Hochweiss
 */
public class StructAnalysisAction extends Action {

	private SecurityAnalysisWindow source;

	/**
	 * 
	 * @param source The source UI-Window, from which the Action is called and which
	 *               is to be modified.
	 */
	public StructAnalysisAction(SecurityAnalysisWindow source) {
		super("Structural Analysis");
		this.source = source;
		this.setToolTipText("Check whether all annotated security pattern are correctly applied \n"
				+ "(whether their roles correctly applied)");
	}

	@Override
	public void run() {
		((CCTabItem) source.getCurTabItem()).getAnalysisResult().analyzeStructural();
		((CCTabItem) source.getCurTabItem()).refresh();
		source.getCurTabItem().getParent().requestLayout();
	}

}
