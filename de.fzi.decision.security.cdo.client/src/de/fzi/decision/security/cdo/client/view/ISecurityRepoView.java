package de.fzi.decision.security.cdo.client.view;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.PartInitException;

import de.fzi.decision.security.cdo.client.util.SecurityContainerTableViewerModel;
import de.fzi.decision.security.cdo.client.util.SecurityEditorInput;

public interface ISecurityRepoView {
	
	public abstract void openResourceInEditor(SecurityEditorInput editorInput);
	
	public abstract URI startModelSelection();

	//public abstract boolean showCDOConflictDialog();
	
	public abstract String showContainerChooserDialogAndGetResult(List<String> containers);

	public abstract void onSessionClosed();
	
	public abstract void setTableInput(SecurityContainerTableViewerModel[] model);
	

}
