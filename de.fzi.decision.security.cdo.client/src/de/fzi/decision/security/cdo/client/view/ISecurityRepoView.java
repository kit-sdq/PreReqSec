package de.fzi.decision.security.cdo.client.view;

import org.eclipse.emf.common.util.URI;

import de.fzi.decision.security.cdo.client.util.SecurityEditorInput;
import security.NamedDescribedEntity;

/**
 * Interface for the view showing the cdo repository
 * @author matthias endlichhofer
 *
 */
public interface ISecurityRepoView {
	
	/**
	 * Opens a new SecurityEditor on the active page with the specified editor input.
	 * @param editorInput The input for the editor
	 */
	public abstract void openResourceInEditor(SecurityEditorInput editorInput);
	
	/**
	 * Opens a new FileDialog in which the user can choose a security model
	 * @return The File URI of the chosen security model or null it the selection was cancelled
	 */
	public abstract URI openModelSelectionDialog();

	/**
	 * Closes all Security Editors and resets the UI state.
	 * Has to be called when the current session gets closed.
	 */
	public abstract void onSessionClosed();
	
	/**
	 * Sets the input of the tableviewer in the UI
	 * @param model The input of the tableviewer
	 */
	public abstract void setTableInput(String[] modelNames);

	/**
	 * Opens a new Dialog in which the user can enter the name and description for a new NamedDescribedEntity.
	 * @param entity The new NamedDescribedEntity
	 */
	public abstract boolean openNamedDescribedEntityCreatorDialog(NamedDescribedEntity entity);
	
	//public abstract boolean showCDOConflictDialog();

}
