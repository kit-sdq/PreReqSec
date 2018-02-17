package de.fzi.decision.security.cdo.client.connection;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import security.Container;

/**
 * This Singleton class is responsible for establishing and managing a connection to the cdo server.
 * @author matthias endlichhofer
 *
 */
public class ServerConnection {

	private final Logger logger = LoggerFactory.getLogger(ServerConnection.class);
	private static ServerConnection instance;
	private static String host;
	private static String repoName;
	private CDONet4jSession session;
	
	private ServerConnection(String host, String repoName) throws LifecycleException {
		session = openSession(host, repoName);
	}
	
	/**
	 * Returns an instance of the ServerConnection if it is already created or creates a new one and tries to
	 * connect to the given reponame at the given server.
	 * @param host The address of the host server
	 * @param repoName The name of the cdo repo
	 * @return the instance of ServerConnection
	 * @throws LifecycleException Thrown if the connection to the repo or server is not possible
	 */
	public static ServerConnection getInstance(String host, String repoName) throws LifecycleException {
		if (instance == null) {
			ServerConnection.host = host;
			ServerConnection.repoName = repoName;
			instance = new ServerConnection(host, repoName);
		} else if (!host.equals(ServerConnection.host) || !repoName.equals(ServerConnection.repoName)) {
			instance = new ServerConnection(host, repoName);
		}
		return instance;
	}
	
	/**
	 * Returns an instance of the ServerConnection if it is already created or creates a new one and tries to
	 * connect to the given reponame at the given server. Further it sets the update listener to the session.
	 * @param host The address of the host server
	 * @param repoName The name of the cdo repo
	 * @param listener A Listener that will be notified about update events of the CDONet4jSession
	 * @return the instance of ServerConnection
	 * @throws LifecycleException Thrown if the connection to the repo or server is not possible
	 */
	public static ServerConnection getInstance(String host, String repoName, IListener listener) throws LifecycleException {
		instance = getInstance(host, repoName);
		instance.session.addListener(listener);
		return instance;
	}
	
	public CDONet4jSession getSession() {
		return session;
	}
	
	 private CDONet4jSession openSession(String host, String repoName) throws LifecycleException {
	        final IConnector connector = (IConnector) IPluginContainer.INSTANCE
	                .getElement(
	                        "org.eclipse.net4j.connectors", // Product group
	                        "tcp", // Type
	                        host); // Description
	 
	        CDONet4jSessionConfiguration config = CDONet4jUtil
	                .createNet4jSessionConfiguration();
	        config.setConnector(connector);
	        config.setRepositoryName(repoName);
	 
	        CDONet4jSession session = config.openNet4jSession();
	 
	        session.addListener(new LifecycleEventAdapter() {
	            @Override
	            protected void onDeactivated(ILifecycle lifecycle) {
	                connector.close();
	            }
	        });
	        logger.info("Created new Session: " + session.getSessionID() + " - host = " + host + " - repoName = " + repoName);
	        return session;
	    }
	
	 public void closeSession() {
		 session.close();
		 repoName = null;
		 host = null;
		 instance = null;
		 logger.info("Closed Session: " + session.getSessionID());
	 }
	 
	 public ArrayList<String> getAllSecurityContainerNames() {
		 ArrayList<String> containerNames = new ArrayList<>();
		 if (session != null) {
			 CDOTransaction transaction = session.openTransaction();
			 CDOResource rootResource = transaction.getRootResource();
			 for (EObject object : rootResource.getContents()) {
				 CDOResourceNode node = (CDOResourceNode) object;
				 containerNames.add(node.getName());
			 }
			 transaction.close();
		 }
		 return containerNames;
	 }
	 
	 /**
	  * Creates a new CDOResource with the given name and stores the Container as a CDOObject in it.
	  * If there is already a CDOResource with this name, it will be overridden.
	  * @param object The object to store in the new resource. (Must be an EObject)
	  * @param name The name of the new resource
	  * @throws CommitException Thrown in case of commit problems such as conflicts
	  */
	 public void storeNewResource(Container object, String name) throws CommitException {
		CDOTransaction transaction = session.openTransaction();
		CDOResource cdoResource = transaction.getOrCreateResource("/" + name);
		cdoResource.getContents().clear();
		CDOObject cdoObject = CDOUtil.getCDOObject((EObject) object);
		cdoResource.getContents().add(cdoObject);
		transaction.commit();
		transaction.close();
	 }

	public String getHost() {
		return host;
	}

	public String getRepoName() {
		return repoName;
	}

	/**
	 * Deletes the matching resource from the cdo repository.
	 * @param name The name of the resource that should be deleted.
	 * @throws IOException Thrown if an I/O exception of some sort has occurred
	 * @throws CommitException Thrown in case of commit problems such as conflicts
	 */
	public void deleteResource(String name) throws CommitException, IOException {
		CDOTransaction transaction = session.openTransaction();
		CDOResource cdoResource = transaction.getResource("/" + name);
		if (cdoResource != null) {
			cdoResource.delete(null);
			transaction.commit();
		}
		transaction.close();
		
	}
	 
	 

}
