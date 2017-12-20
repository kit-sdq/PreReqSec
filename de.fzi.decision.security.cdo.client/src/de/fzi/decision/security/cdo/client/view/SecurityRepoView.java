package de.fzi.decision.security.cdo.client.view;

import org.eclipse.emf.common.util.URI;

public interface SecurityRepoView {
	
	public abstract void openFileInEditor(String filePath);
	
	public abstract URI startModelSelection();

	public abstract void enableCommit();

	public abstract boolean showCDOConflictDialog();

}
