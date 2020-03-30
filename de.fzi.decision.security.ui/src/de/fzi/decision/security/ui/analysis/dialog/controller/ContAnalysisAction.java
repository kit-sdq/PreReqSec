package de.fzi.decision.security.ui.analysis.dialog.controller;

import org.eclipse.jface.action.Action;

import de.fzi.decision.security.ui.analysis.dialog.views.CCTabItem;
import de.fzi.decision.security.ui.analysis.dialog.views.SecurityAnalysisWindow;

/**
 * Action class that is responsible for controlling the contextual analysis.
 * 
 * @author Robert Hochweiss
 */
public class ContAnalysisAction extends Action {

	private SecurityAnalysisWindow source;

	/**
	 * 
	 * @param source The source UI-Window, from which the Action is called and which
	 *               is to be modified.
	 */
	public ContAnalysisAction(SecurityAnalysisWindow source) {
		super("Contextual Analysis");
		this.source = source;
		setToolTipText("Check whether all elements of the system are secure (no unmitigated prerequisites). \n"
				+ "(Includes structural analysis)");
	}

	@Override
	public void run() {
		((CCTabItem) source.getCurTabItem()).getAnalysisResult().analyzeContextual();
		((CCTabItem)source.getCurTabItem()).refresh();
		source.getCurTabItem().getParent().requestLayout();				
	}
}



