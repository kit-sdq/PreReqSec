package analysis;

import java.io.File;

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
		this(URI.createFileURI(new File(modelPath).getAbsolutePath()));
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
		
		//register EMF profiles
		set.getPackageRegistry().put(EMFProfileApplicationPackage.eNS_URI, EMFProfileApplicationPackage.eINSTANCE);
		
		//use factory registration
		//set.getResourceFactoryRegistry().getExtensionToFactoryMap().put("emfprofile_diagram", new MdsdprofilesFactoryImpl());
		
		//use platform URI
		//URI profileURI = URI.createURI("platform:/resource/de.fzi.decision.security.profile.pcm/prereq.emfprofile_diagram");
		
		//use profile API
		//for (Profile p : ProfileAPI.getApplicableProfiles()) {
		//	set.getPackageRegistry().put(p.getNsURI(), p);
		//}
		
		//create security profile registration
		//set.getPackageRegistry().put("de.fzi.decision.security.profiles.pcm.prerequisite",
		//		URI.createURI("platform:/resource/de.fzi.decision.security.profile.pcm/prereq.emfprofile_diagram"));
		//set.getResource(profileURI, true);

		//get epackage from the profile factory
		//EPackage pac = MdsdprofilesFactory.eINSTANCE.getEPackage();
		
		//register security profiles
		//set.getPackageRegistry().put("de.fzi.decision.security.profiles.pcm.prerequisite", pac);
		//set.getPackageRegistry().put("de.fzi.decision.security.profiles.pcm.pattern", pac);

		//register xmi factory
		//set.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		
		//TODO: Register security MDSDProfiles
		
		//load model	
		set.getResourceFactoryRegistry().getExtensionToFactoryMap().put("system", new EcoreResourceFactoryImpl());
		
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