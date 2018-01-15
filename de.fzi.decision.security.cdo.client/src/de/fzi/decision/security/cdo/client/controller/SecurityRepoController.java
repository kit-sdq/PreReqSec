package de.fzi.decision.security.cdo.client.controller;

import java.util.List;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.common.util.URI;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.fzi.decision.security.cdo.client.connection.ServerConnection;
import de.fzi.decision.security.cdo.client.util.Constants;
import de.fzi.decision.security.cdo.client.util.SecurityEditorInput;
import de.fzi.decision.security.cdo.client.util.SecurityFileHandler;
import de.fzi.decision.security.cdo.client.view.SecurityRepoView;
import security.Container;

public class SecurityRepoController {
	
	private SecurityRepoView view;
	private ServerConnection connection;
	
	public SecurityRepoController(SecurityRepoView view) {
		this.view = view;
	}
	
	public void connectToCDOServer(String host, String repoName) throws LifecycleException{
		openCDOSessionsView();
		connection = ServerConnection.getInstance(host, repoName);
		loadResourceAndOpenEditor();
	}
	
	private void openCDOSessionsView() {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(Constants.SESSIONS_VIEW_ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	private void closeSessionsView() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		for (IViewReference viewRef : page.getViewReferences()) {
			if (viewRef.getId().equals(Constants.SESSIONS_VIEW_ID)) {
				page.hideView(viewRef);
			}
		}
	}

	private void loadResourceAndOpenEditor() {
		CDOResource rootResource = loadRootResource();
		openResourceInEditor(rootResource);
	}
	
	private CDOResource loadRootResource() {
		String containerPath = Constants.RESOURCE_PATH;
		List<String> containerNames = connection.getAllSecurityContainerNames();
		if (!containerNames.isEmpty()) {
			containerPath = "/" + handleNotEmptyRepository(containerNames);
		} else {
			handleEmptyRepository();
		}
		CDOResource rootResource = connection.loadResourceByPath(containerPath);
		return rootResource;
	}
	
	private String handleNotEmptyRepository(List<String> containerNames) {
		if (containerNames.size() == 1) {
			return containerNames.get(0);
		} else {
			return handleMultipleContainers(containerNames);
		}
	}
	
	private String handleMultipleContainers(List<String> containerNames) {
		return view.showContainerChooserDialogAndGetResult(containerNames);
	}

	private void handleEmptyRepository() {
		URI modelURI = view.startModelSelection();
		if (modelURI != null) {
			Container rootContainer = SecurityFileHandler.getModelFromFile(modelURI);
			connection.storeInitialResource(rootContainer);
			loadRootResource();
		}
	}
	
	private void openResourceInEditor(CDOResource root) {
		SecurityEditorInput editorInput = new SecurityEditorInput(root.getURI().toString());
		view.openResourceInEditor(editorInput);
	}

	public boolean isRepoLoaded() {
		if (connection != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public void closeSession() {
		try {
			view.closeSecurityEditorIfOpen();
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
		closeSessionsView();
		
		if (connection != null) {
			connection.closeSession();
			connection = null;
		}
	}

}
