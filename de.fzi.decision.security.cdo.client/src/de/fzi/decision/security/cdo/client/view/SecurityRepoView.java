package de.fzi.decision.security.cdo.client.view;

import java.util.List;

import org.eclipse.emf.common.util.URI;

public interface SecurityRepoView {
	
	public abstract void openFileInEditor(String filePath);
	
	//public abstract void openResourceInEditor(String resPath);
	
	public abstract URI startModelSelection();

	public abstract void enableCommit();

	public abstract boolean showCDOConflictDialog();
	
	public abstract String showContainerChooserDialogAndGetResult(List<String> containers);
	

}
