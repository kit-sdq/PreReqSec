package analysis;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
//import org.modelversioning.emfprofileapplication.EMFProfileApplicationPackage;

import security.SecurityPackage;

/**
 * This class loads a security model. It has to be given to a new modelLoader to enable it to load models.
 * 
 * @author Benjamin Plach, Dominik Doerner
 *
 */
public class SecurityModelLoaderEngine {

	private ViatraQueryEngine engine;
	
	/**
	 * This class loads a security model. It has to be given to a new modelLoader to enable it to load models.
	 * 
	 * @param modelPath the path of the security model file
	 * @throws InitializationException thrown if the model could not be loaded
	 */
	public SecurityModelLoaderEngine(String modelPath) throws InitializationException {
		super();

		//create a new resource set
		ResourceSet set = new ResourceSetImpl();
		
		//register security meta model
		set.getPackageRegistry().put(SecurityPackage.eNS_URI, SecurityPackage.eINSTANCE);
		
		//load model		
		set.getResourceFactoryRegistry().getExtensionToFactoryMap().put("security", new EcoreResourceFactoryImpl());
		
		URI modelUri = URI.createFileURI(modelPath);
		initEngine(set, modelUri);
	}
	
	/**
	 * This class loads a security model. It has to be given to a new modelLoader to enable it to load models.
	 * 
	 * @param modelUri the URI to the cdo resource
	 * @throws InitializationException thrown if the model could not be loaded
	 */
	public SecurityModelLoaderEngine(URI modelUri) throws InitializationException {
		super();
		ResourceSet set = new ResourceSetImpl();
		set.getPackageRegistry().put(SecurityPackage.eNS_URI, SecurityPackage.eINSTANCE);
		initEngine(set, modelUri);
	}
	
	private void initEngine(ResourceSet set, URI modelUri) throws InitializationException {
		set.createResource(modelUri);
		
		set.getResource(modelUri, true);
			
		//start engine		
		try {
			engine = ViatraQueryEngine.on(new EMFScope(set));			
		} catch (ViatraQueryException e) {
			InitializationException wrapperException = new InitializationException(e.getMessage(), e.getCause());
			wrapperException.setStackTrace(e.getStackTrace());
			throw wrapperException;
		}
	}
	
	//to hide Viatra-stuff from users
	/*package*/ ViatraQueryEngine getEngine() {
		return engine;
	}
}