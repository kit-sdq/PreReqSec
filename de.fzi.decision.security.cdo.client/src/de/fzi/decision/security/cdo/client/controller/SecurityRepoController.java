package de.fzi.decision.security.cdo.client.controller;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.ui.PartInitException;

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
	
	public void connectToCDOServer(String host, String repoName) throws LifecycleException {
		connection = ServerConnection.getInstance(host, repoName);
		loadResourceAndOpenEditor();
	}

	private void loadResourceAndOpenEditor() {
		String resPath = getResourcePath();
		openResourceInEditor(resPath, connection.getHost(), connection.getRepoName());
	}
	
	private String getResourcePath() {
		String resourcePath = Constants.RESOURCE_PATH;
		List<String> containerNames = connection.getAllSecurityContainerNames();
		if (!containerNames.isEmpty()) {
			resourcePath = "/" + handleNotEmptyRepository(containerNames);
		} else {
			return handleEmptyRepository();
		}
		return resourcePath;
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

	private String handleEmptyRepository() {
		URI modelURI = view.startModelSelection();
		if (modelURI != null) {
			Container rootContainer = SecurityFileHandler.getModelFromFile(modelURI);
			connection.storeInitialResource(rootContainer);
			return getResourcePath();
		}
		return null;
	}
	
	private void openResourceInEditor(String resPath, String host, String repoName) {
		SecurityEditorInput editorInput = new SecurityEditorInput(resPath, host, repoName);
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
		
		if (connection != null) {
			connection.closeSession();
			connection = null;
		}
	}

}
