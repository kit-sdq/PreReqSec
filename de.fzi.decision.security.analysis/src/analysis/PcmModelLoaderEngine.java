package analysis;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.modelversioning.emfprofileapplication.EMFProfileApplicationPackage;
import org.palladiosimulator.pcm.core.CorePackage;

/**
 * This class loads a security model. It has to be given to a new modelLoader to enable it to load models.
 * 
 * @author Benjamin Plach, Dominik Doerner
 *
 */
public class PcmModelLoaderEngine {

	private ViatraQueryEngine engine;
	
	/**
	 * This class loads a pcm system model. It has to be given to a new modelLoader to enable it to load models.
	 * 
	 * @param modelPath the path of the pcm system model file
	 * @throws InitializationException thrown if the model could not be loaded
	 */
	public PcmModelLoaderEngine(String modelPath) throws InitializationException {
		this(URI.createFileURI(modelPath));
	}
	
	/**
	 * This class loads a pcm system model. It has to be given to a new modelLoader to enable it to load models.
	 * 
	 * @param modelUri the URI to the cdo resource
	 * @throws InitializationException thrown if the model could not be loaded
	 */
	public PcmModelLoaderEngine(URI modelUri) throws InitializationException {
		super();
		
		//create a new resource set
		ResourceSet set = new ResourceSetImpl();
		
		//register pcm meta model
		set.getPackageRegistry().put(CorePackage.eNS_URI, CorePackage.eINSTANCE);
		
		//register profiles
		set.getPackageRegistry().put(EMFProfileApplicationPackage.eNS_URI, EMFProfileApplicationPackage.eINSTANCE);
		//set.getPackageRegistry().put("de.fzi.decision.security.profiles.pcm.prerequisite", EMFProfileApplicationPackage.eINSTANCE);
		//set.getPackageRegistry().put("de.fzi.decision.security.profiles.pcm.pattern", EMFProfileApplicationPackage.eINSTANCE);
		
		//load model		
		set.getResourceFactoryRegistry().getExtensionToFactoryMap().put("system", new EcoreResourceFactoryImpl());
		
		initEngine(set, modelUri);
	}
	
	private void initEngine(ResourceSet set, URI modelUri) throws InitializationException {
		set.createResource(modelUri);
		
		/*Resource r =*/ set.getResource(modelUri, true);
			
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