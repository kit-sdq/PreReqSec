package de.fzi.decision.security.cdo.client.util;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import security.Container;

public class SecurityFileHandler {
	
	public static final String tmp_project_name = "Temporary_Security_Model";
	public static final String file_name = "tmp.security";
	
	/*public static String saveResourceIntoTemporaryProject(Resource resource) {
		try {
			String folderPath = createProjectAndFolder(tmp_project_name);
			URI fileURI = URI.createURI(folderPath + "/" + file_name);
			saveResourceAsXMI(resource, fileURI);
			String absoluteFilePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + fileURI.toString();
			return absoluteFilePath;
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}*/
	
	/*public static void deleteTemporaryProject() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(tmp_project_name);
		if (project.exists()) {
			try {
				project.delete(true, true, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
			System.out.println("Temporary project deleted.");
		}
	}*/
	
	public static Container getModelFromFile(URI modelURI) {
	    Resource resource = getResourceFromFile(modelURI);
	    Container root = (Container) resource.getContents().get(0);
	    return root;
	}
	
	public static Resource getResourceFromFile(URI modelURI) {
		ResourceSet resSet = new ResourceSetImpl();
	    Resource resource = resSet.getResource(modelURI, true);
	    return resource;
	}
	
	/*private static String createProjectAndFolder(String name) throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(name);
		if (!project.exists()) {
			project.create(null);
		}
		project.open(null);
		IFolder modelFolder = project.getFolder("Model");
		if (!modelFolder.exists()) {
			modelFolder.create(false, true, null);
		}
		return modelFolder.getFullPath().toString();
	}*/
	
	/*private static void saveResourceAsXMI(Resource resource, URI fileURI) {
		try {
			Map<String, String> saveOptions = new HashMap<String, String>();
			Resource xmiResource = new XMIResourceImpl(fileURI);
			xmiResource.getContents().add(resource.getContents().get(0));
			saveOptions.put(org.eclipse.emf.ecore.xmi.XMLResource.OPTION_ENCODING,"UTF-8");
			xmiResource.save(saveOptions);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	/*public static URI getTmpFileURI() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(tmp_project_name);
		IFolder modelFolder = project.getFolder("Model");
		IFile file = modelFolder.getFile(file_name);
		if (file != null) {
			return URI.createURI(file.getLocationURI().toString());	
		}
		return null;		
	}*/

}
