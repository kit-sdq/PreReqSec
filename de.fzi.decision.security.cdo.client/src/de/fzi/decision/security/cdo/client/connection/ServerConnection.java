package de.fzi.decision.security.cdo.client.connection;

import java.io.IOException;

import org.antlr.runtime.tree.TreeIterator;
import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleException;

import de.fzi.decision.security.cdo.client.model.SecurityContainer;
import de.fzi.decision.security.cdo.client.util.Constants;
import security.Catalog;
import security.Container;
import security.securityPatterns.PatternCatalog;

public class ServerConnection {
	
	private static ServerConnection instance;
	private static String host;
	private static String repoName;
	
	private CDONet4jSession session;
	
	private ServerConnection(String host, String repoName) throws LifecycleException {
		session = openSession(host, repoName);
	}
	
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
	 
	        return session;
	    }
	
	 public void closeSession() {
		 session.close();
		 repoName = null;
		 host = null;
	 }
	 
	 public CDOResource loadRootResource() {
		 if (session != null) {
			 CDOTransaction transaction = session.openTransaction();
			 CDOResource resource = transaction.getOrCreateResource(Constants.RESOURCE_PATH);
			 return resource;
		 } else {
			 return null;
		 }
	 }
	 
	 public void storeInitialResource(Container object) {
		try {
			CDOTransaction transaction = session.openTransaction();
			CDOResource cdoResource = transaction.getOrCreateResource(Constants.RESOURCE_PATH);
			cdoResource.getContents().clear();
			CDOObject cdoObject = CDOUtil.getCDOObject(object);
			cdoResource.getContents().add(cdoObject);
			transaction.commit();
		} catch (CommitException e) {
			e.printStackTrace();
		}
	 }

	public void commitChanges(SecurityContainer localContainer) {
		// TODO implement the commit correctly
		storeInitialResource(localContainer.getSecurityContainer());
	}
	 
	 

}
