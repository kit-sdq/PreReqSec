package de.fzi.decision.security.cdo.client.util;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class SecurityEditorInput extends PlatformObject implements IEditorInput {
	
	private String resourcePath;
	
	public SecurityEditorInput(String resourcePath) {
		this.resourcePath = resourcePath;
	}
	
	public String getResourcePath() {
		return resourcePath;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
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

}
