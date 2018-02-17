package de.fzi.decision.security.cdo.client.util;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import de.fzi.decision.security.cdo.client.connection.ServerConnection;

/**
 * This class represents the input of the security editor when opening a security model as a cdo resource.
 * @author matthias endlichhofer
 *
 */
public class SecurityEditorInput extends PlatformObject implements IEditorInput {
	
	private String resourcePath;
	private String host;
	private String repoName;
	
	public SecurityEditorInput(String resourcePath, String host, String repoName) {
		this.resourcePath = resourcePath;
		this.host = host;
		this.repoName = repoName;
	}
	
	public String getResourcePath() {
		return resourcePath;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		//No image needed
		return null;
	}

	@Override
	public String getName() {
		return new Path(resourcePath).lastSegment();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return resourcePath;
	}
	
	@Override
	public int hashCode() {
		return ObjectUtil.hashCode(resourcePath);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SecurityEditorInput) {
			SecurityEditorInput tmpObj = (SecurityEditorInput) obj;
			if (tmpObj.getResourcePath().equals(resourcePath)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Opens a new CDOTransaction in the current session on the given EMF resource set
	 * @param set An EMF resource set
	 * @return the cdo transaction
	 */
	public CDOTransaction getTransaction(ResourceSet set) {
		ServerConnection connection = ServerConnection.getInstance(host, repoName);
		return connection.getSession().openTransaction(set);
	}

}
