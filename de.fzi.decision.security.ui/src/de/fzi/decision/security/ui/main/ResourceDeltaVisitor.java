package de.fzi.decision.security.ui.main;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Visitor used to visit all changed workspace resources.
 */
public class ResourceDeltaVisitor implements IResourceDeltaVisitor {
	private ResourceSet resourceSet;
	private Collection<Resource> savedResources;
	private Collection<Resource> changedResources = new ArrayList<Resource>();
	private Collection<Resource> removedResources = new ArrayList<Resource>();
	
	/**
	 * Creates the visitor.
	 * 
	 * @param resourceSet the resourceSet that shall be monitored for changes
	 * @param savedResources a collection of resources that were saved since the last change
	 */
	public ResourceDeltaVisitor(ResourceSet resourceSet, Collection<Resource> savedResources) {
		this.resourceSet = resourceSet;
		this.savedResources = savedResources;
	}

	@Override
	public boolean visit(IResourceDelta delta) {
		boolean isFile = delta.getResource().getType() == IResource.FILE;
		
		if (isFile) {
			boolean fileRemoved = delta.getKind() == IResourceDelta.REMOVED;
			boolean fileChanged = delta.getKind() == IResourceDelta.CHANGED;
			boolean notMarkers = delta.getFlags() != IResourceDelta.MARKERS;
			
			if (fileRemoved || fileChanged && notMarkers) {
				URI uri = URI.createPlatformResourceURI(delta.getFullPath().toString(), true);
				Resource resource = resourceSet.getResource(uri, false);
				boolean resourceIsInResourceSet = resource != null;
				
				if (resourceIsInResourceSet) {
					if (fileRemoved) {
						removedResources.add(resource);
					} else {
						boolean wasInSavedResources = savedResources.remove(resource);
						
						if (!wasInSavedResources) {
							changedResources.add(resource);
						}
					}
				}
			}
			
			return false;
		}

		return true;
	}

	/** 
	 * @return a collection of resources that were changed
	 */
	public Collection<Resource> getChangedResources() {
		return changedResources;
	}

	/**
	 * @return a collection of resources that were removed
	 */
	public Collection<Resource> getRemovedResources() {
		return removedResources;
	}
	
	/**
	 * @return true when there are changed resources
	 */
	public boolean hasChangedResources() {
		return !changedResources.isEmpty();
	}
	
	/**
	 * @return true when there are removed resources
	 */
	public boolean hasRemovedResources() {
		return !removedResources.isEmpty();
	}

}
