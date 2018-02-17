package de.fzi.decision.security.cdo.client.util;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import security.Container;

/**
 * Responsible for loading the security model from a .security file.
 * @author matthias endlichhofer
 *
 */
public class SecurityContainerLoader {
	
	public static Container loadModelFromFile(URI modelURI) throws Exception {
		ResourceSet resSet = new ResourceSetImpl();
	    Resource resource = resSet.getResource(modelURI, true);
	    Container root = (Container) resource.getContents().get(0);
	    return root;
	}
}
