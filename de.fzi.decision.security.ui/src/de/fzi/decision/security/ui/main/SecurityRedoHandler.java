package de.fzi.decision.security.ui.main;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.action.Action;

public class SecurityRedoHandler extends Action {
	
	private EditingDomain editingDomain;
	
	public SecurityRedoHandler(EditingDomain editingDomain) {
		this.editingDomain = editingDomain;
	}
	
	
	@Override
	public void run() {
		editingDomain.getCommandStack().redo();
	}
}
