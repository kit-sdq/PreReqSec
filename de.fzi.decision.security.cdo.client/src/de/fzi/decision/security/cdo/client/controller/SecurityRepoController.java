package de.fzi.decision.security.cdo.client.controller;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.fzi.decision.security.cdo.client.connection.ServerConnection;
import de.fzi.decision.security.cdo.client.model.ResourceManager;
import de.fzi.decision.security.cdo.client.util.Constants;
import de.fzi.decision.security.cdo.client.util.SecurityFileHandler;
import de.fzi.decision.security.cdo.client.view.SecurityRepoView;
import security.Container;

public class SecurityRepoController {
	
	private SecurityRepoView view;
	private ServerConnection connection;
	private ResourceManager resourceManager;
	
	public SecurityRepoController(SecurityRepoView view) {
		this.view = view;
	}
	
	public void connectToCDOServer(String host, String repoName) throws LifecycleException{
		connection = ServerConnection.getInstance(host, repoName);
		applyGlobalRepoState();
		openCDOSessionsView();
	}
	
	private void openCDOSessionsView() {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(Constants.SESSIONS_VIEW_ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	private void applyGlobalRepoState() {
		CDOResource rootResource = loadRootResource();
		this.resourceManager = new ResourceManager(this, rootResource);
	}
	
	private CDOResource loadRootResource() {
		CDOResource rootResource = connection.loadRootResource();
		if (rootResource != null) {
			if (!rootResource.getContents().isEmpty()) {
				createTempSecurityFileAndOpenEditor(connection.loadRootResource());
			} else {
				rootResource.getContents().add(handleEmptyRepository());
			}
		} else {
			//repository could not be loaded
		}
		return rootResource;
	}
	
	private void createTempSecurityFileAndOpenEditor(Resource root) {
		String filePath = SecurityFileHandler.saveResourceIntoTemporaryProject(root);
		view.openFileInEditor(filePath);
	}
	
	private Container handleEmptyRepository() {
		URI modelURI = view.startModelSelection();
		if (modelURI != null) {
			Container rootContainer = SecurityFileHandler.getModelFromFile(modelURI);
			connection.storeInitialResource(rootContainer);
			createTempSecurityFileAndOpenEditor(SecurityFileHandler.getResourceFromFile(modelURI));
			return rootContainer;
		}
		return null;
	}

	public boolean isRepoLoaded() {
		if (connection != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public void closeSession() {
		//TODO: check unsaved changes
		if (connection != null) {
			connection.closeSession();
			connection = null;
		}
		SecurityFileHandler.deleteTemporaryProject();
	}

	public void notifyCommitNecessary() {
		view.enableCommit();
	}
	
	public boolean existLocalChanges() {
		return resourceManager.isResourceModified();
	}

	public void commitChanges() {
		if (resourceManager.getRootContainer().getRootResource().cdoConflict()) {
			boolean discardLocalChanges = view.showCDOConflictDialog();
			if (discardLocalChanges) {
				applyGlobalRepoState();
				return;
			}
		}
		connection.commitChanges(resourceManager.getRootContainer());
		resourceManager.setResourceModified(false);
	}

}
