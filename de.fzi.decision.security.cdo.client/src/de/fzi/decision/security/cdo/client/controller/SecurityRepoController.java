package de.fzi.decision.security.cdo.client.controller;

import java.io.IOException;
import java.util.List;

import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fzi.decision.security.cdo.client.connection.ServerConnection;
import de.fzi.decision.security.cdo.client.util.SecurityContainerTableViewerModel;
import de.fzi.decision.security.cdo.client.util.SecurityEditorInput;
import de.fzi.decision.security.cdo.client.util.SecurityFileHandler;
import de.fzi.decision.security.cdo.client.view.SecurityRepoView;
import security.Container;

public class SecurityRepoController {

	private final Logger logger = LoggerFactory.getLogger(SecurityRepoController.class);
	private SecurityRepoView view;
	private ServerConnection connection;
	
	public SecurityRepoController(SecurityRepoView view) {
		this.view = view;
	}
	
	public void connectToCDOServer(String host, String repoName) throws LifecycleException {
		connection = ServerConnection.getInstance(host, repoName, createPassiveUpdateListener());
		refreshTableInput();
	}
	
	private IListener createPassiveUpdateListener() {
		return new IListener() {
					
			@Override
			public void notifyEvent(IEvent event) {
				if (event instanceof CDOSessionInvalidationEvent) {
					CDOSessionInvalidationEvent invalidationEvent = (CDOSessionInvalidationEvent) event;
					long timestamp = invalidationEvent.getTimeStamp();
					logger.info("CDOSessionInvalidationEvent occured at: " + timestamp);
					//TODO: respond properly to that event (check for conflicts...)
					refreshTableInput();
				}
			}
		};
	}

	private void refreshTableInput() {
		List<String> containerNames = connection.getAllSecurityContainerNames();
		SecurityContainerTableViewerModel[] model = new SecurityContainerTableViewerModel[containerNames.size()];
		for (int i = 0; i < containerNames.size(); i++) {
			model[i] = new SecurityContainerTableViewerModel(containerNames.get(i));
		}
		view.setTableInput(model);
	}

	public void doLoadModel() throws IllegalArgumentException, CommitException {
		URI modelURI = view.startModelSelection();
		if (modelURI != null) {
			Container rootContainer = SecurityFileHandler.getModelFromFile(modelURI);
			String name = modelURI.lastSegment().substring(0, modelURI.lastSegment().indexOf('.'));
			if (!checkIfNameAlreadyExists(name)) {
				connection.storeNewResource(rootContainer, name);
				refreshTableInput();
			} else {
				throw new IllegalArgumentException();
			}
		}
	}
	
	private boolean checkIfNameAlreadyExists(String name) {
		for (String existingName : connection.getAllSecurityContainerNames()) {
			if (existingName.equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public void openResourceInEditor(String resPath) {
		SecurityEditorInput editorInput = new SecurityEditorInput(resPath, connection.getHost(), connection.getRepoName());
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
			view.onSessionClosed();
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
		if (connection != null) {
			connection.closeSession();
			connection = null;
		}
	}

	public void deleteResource(String name) throws IOException {
		connection.deleteResource(name);
		refreshTableInput();
	}

}
