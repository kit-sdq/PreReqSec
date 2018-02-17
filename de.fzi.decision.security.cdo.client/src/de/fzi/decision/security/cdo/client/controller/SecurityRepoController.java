package de.fzi.decision.security.cdo.client.controller;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.PackageNotFoundException;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fzi.decision.security.cdo.client.connection.ServerConnection;
import de.fzi.decision.security.cdo.client.util.SecurityEditorInput;
import de.fzi.decision.security.cdo.client.util.SecurityContainerLoader;
import de.fzi.decision.security.cdo.client.view.ISecurityRepoView;
import security.Container;

/**
 * Controller of the repository view
 * @author matthias endlichhofer
 *
 */
public class SecurityRepoController {

	private final Logger logger = LoggerFactory.getLogger(SecurityRepoController.class);
	private ISecurityRepoView view;
	private ServerConnection connection;
	
	public SecurityRepoController(ISecurityRepoView view) {
		this.view = view;
	}
	
	/**
	 * Establishes a ServerConnection to the given server and repo and refreshes the repo view table
	 * @param host The address of the host server to connect to
	 * @param repoName The name of the repo to connect to
	 * @throws LifecycleException Thrown if the connection to the repo or server is not possible
	 */
	public void connectToCDOServer(String host, String repoName) throws LifecycleException {
		connection = ServerConnection.getInstance(host, repoName, createPassiveUpdateListener());
		refreshTableInput();
	}
	
	/**
	 ** @return a new listener that responds to CDOSessionInvalidationEvents
	 */
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
		ArrayList<String> containerNames = connection.getAllSecurityContainerNames();
		view.setTableInput(containerNames.toArray(new String[0]));
	}

	/**
	 * Let's the user choose a security container model and stores this as a new resource in the repository
	 * @throws IllegalArgumentException Thrown if the user chooses a model which name conflicts with an existing model
	 * @throws CommitException Thrown in case of commit problems such as conflicts
	 * @throws PackageNotFoundException 
	 */
	public void doLoadModel() throws IllegalArgumentException, CommitException, Exception {
		URI modelURI = view.openModelSelectionDialog();
		if (modelURI != null) {
			Container rootContainer = SecurityContainerLoader.loadModelFromFile(modelURI);
			String name = modelURI.lastSegment().substring(0, modelURI.lastSegment().indexOf('.'));
			if (!checkIfSecurityContainerAlreadyExists(name)) {
				connection.storeNewResource(rootContainer, name);
				refreshTableInput();
			} else {
				throw new IllegalArgumentException();
			}
		}
	}
	
	/**
	 * Checks if the current opened cdo repository contains a resource with this name.
	 * @param name The name of the security container
	 * @return true if a similar named security container exists, false otherwise.
	 */
	private boolean checkIfSecurityContainerAlreadyExists(String name) {
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
		view.onSessionClosed();
		if (connection != null) {
			connection.closeSession();
			connection = null;
		}
	}

	public void deleteResource(String name) throws IOException, CommitException {
		connection.deleteResource(name);
		refreshTableInput();
	}

}
