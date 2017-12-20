package de.fzi.decision.security.cdo.client.listeners;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

import de.fzi.decision.security.cdo.client.model.ResourceManager;
import de.fzi.decision.security.cdo.client.util.SecurityFileHandler;

public class ResourceChangeListenerImpl implements IResourceChangeListener {
	
	private ResourceManager resourceManager;
	private boolean hasChanges;

	
	public ResourceChangeListenerImpl(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
		hasChanges = false;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		try {
			delta.accept(initResourceDeltaVisitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private IResourceDeltaVisitor initResourceDeltaVisitor() {
		return new IResourceDeltaVisitor() {
			
			@Override
			public boolean visit(IResourceDelta delta) throws CoreException {
				boolean isFile = delta.getResource().getType() == IResource.FILE;
				
				if (isFile) {
					boolean fileChanged = delta.getKind() == IResourceDelta.CHANGED;
					if (fileChanged) {
						String resourceName = delta.getResource().getName();
						if (resourceName.equals(SecurityFileHandler.file_name)) {
							hasChanges = true;
							resourceManager.setResourceModified(true);
						}
					}
					
				}
				
				return true;
			}
		};
	}

	public boolean hasChanges() {
		return hasChanges;
	}
	
	public void resetChangesState() {
		hasChanges = false;
	}

}
