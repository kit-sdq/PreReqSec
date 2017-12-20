package de.fzi.decision.security.cdo.client.model;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import de.fzi.decision.security.cdo.client.controller.SecurityRepoController;
import de.fzi.decision.security.cdo.client.listeners.ResourceChangeListenerImpl;
import de.fzi.decision.security.cdo.client.util.SecurityFileHandler;

public class ResourceManager {

	private SecurityRepoController controller;
	private ResourceChangeListenerImpl changeListener;
	private SecurityContainer rootContainer;
	
	public ResourceManager(SecurityRepoController controller, CDOResource rootResource) {
		this.controller = controller;
		this.rootContainer = new SecurityContainer(rootResource);
		changeListener = new ResourceChangeListenerImpl(this);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(changeListener, IResourceChangeEvent.POST_CHANGE);
	}
	
	public boolean isResourceModified() {
		return rootContainer.getRootResource().isModified();
	}	
	
	public void setResourceModified(boolean isModified) {
		rootContainer.getRootResource().setModified(isModified);
		if (isModified) {
			controller.notifyCommitNecessary();
			changeRootResource();
		} else {
			changeListener.resetChangesState();
		}
	}

	public SecurityContainer getRootContainer() {
		return rootContainer;
	}
	
	private void changeRootResource() {
		Resource changedResource = SecurityFileHandler.getResourceFromFile(SecurityFileHandler.getTmpFileURI());
		CDOObject changedObject = CDOUtil.getCDOObject(changedResource.getContents().get(0));
		CDOResource rootResource = rootContainer.getRootResource();
		rootResource.getContents().clear();
		rootResource.getContents().add(changedObject);
	}

}
