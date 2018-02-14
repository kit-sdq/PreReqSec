package analysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.query.runtime.api.IMatchProcessor;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.modelversioning.emfprofile.Stereotype;
import org.palladiosimulator.mdsdprofiles.api.StereotypeAPI;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;

import patterns.PcmAssemblyStereotypesMatch;
import patterns.PcmAssemblyStereotypesMatcher;
import patterns.SecurityAttacksMatch;
import patterns.SecurityAttacksMatcher;
import security.securityPatterns.Role;
import security.securityPatterns.SecurityPattern;
import security.securityPrerequisites.Prerequisite;
import security.securityThreats.Attack;

/**
 * This class is used as the base class for the analysis. A base element is chosen upon which the analysis can be executed.
 * Based on the ModelLoader by Benjamin Plach.
 * 
 * Usage-example:
 *     Collection<Attack> c = new ModelLoader(engine, element)
 *         .getUnmitigatedAttacks();
 *         
 * @author Dominik Doerner
 * 
 */
public class ModelAnalyzer {
	
	private ViatraQueryEngine securityEngine;
	private ViatraQueryEngine systemEngine;
	
	/* Magic strings */
	// TODO: Move to a better place
	private static final String PREREQUISITE_STEREOTYPE_NAME = "ComponentPrerequisite";
	private static final String PREREQUISITE_REFERENCE_NAME = "prerequisites";
	
	
	/**
	 * Creates a new model analyser for a given system model and the associated security catalog
	 * 
	 * @param securityEngine a model-loader engine for the security meta model
	 * @param systemEngine a model-loader engine for the system model
	 */
	public ModelAnalyzer(SecurityModelLoaderEngine securityEngine, PcmModelLoaderEngine systemEngine) {
		this.securityEngine = securityEngine.getEngine();
		this.systemEngine = systemEngine.getEngine();
	}
	
	/* +------------------+
	 * | analysis methods |
	 * +------------------+
	 */
		
	/**
	 * Loads all security prerequisites applied to the base system element
	 * 	
	 * @return Collection of prerequisites
	 * @throws LoadingException
	 * @throws NoSuchElementException
	 */
	public Collection<Prerequisite> getPrerequisites(String elementId) throws LoadingException, NoSuchElementException {
		AssemblyContext element = this.getAssemblyContextById(elementId);
		
		return getPrerequisitesByElement(element);
	}

	/**
	 * Loads all security patterns applied to the base system element
	 * 	
	 * @return Collection of patterns
	 * @throws LoadingException
	 * @throws NoSuchElementException
	 */
	public Collection<SecurityPattern> getSecurityPatterns(String elementId) throws LoadingException, NoSuchElementException {
		AssemblyContext element = this.getAssemblyContextById(elementId);
		
		return getSecurityPatternsByElement(element);
	}
	
	/**
	 * Loads all attacks possible on the base system element that are not mitigated by applied security patterns
	 * 	
	 * @return Collection of attacks
	 * @throws LoadingException
	 * @throws NoSuchElementException
	 */
	public Collection<Attack> getUnmitigatedAttacks(String elementId) throws LoadingException, NoSuchElementException {
		AssemblyContext element = this.getAssemblyContextById(elementId);
		
		// collect all prerequisites of the element
		Collection<Prerequisite> unmitigatedPrerequisites = this.getPrerequisitesByElement(element);

		// remove mitigated prerequisites
		Collection<SecurityPattern> securityPatterns = this.getSecurityPatternsByElement(element);
		for (SecurityPattern pattern : securityPatterns) {
			for (Prerequisite prereq : pattern.getMitigatedPrerequisites()) {
				if (unmitigatedPrerequisites.contains(prereq))
					unmitigatedPrerequisites.remove(prereq);
			}
		}
		
		// check which attacks are possible
		Collection<Attack> attacks = this.getPossibleAttacksByPrerequisites(unmitigatedPrerequisites);
		return attacks;
	}

	/* +----------------+
	 * | helper methods |
	 * +----------------+
	 */

	/**
	 * Loads all prerequisites that are applied to a given system element
	 * @param element The system element to be analyzed
	 * @return Collection of prerequisites
	 * @throws LoadingException
	 */
	private Collection<Prerequisite> getPrerequisitesByElement(AssemblyContext element) throws LoadingException {
		//init return collection
		Collection<Prerequisite> returnCollection = new HashSet<Prerequisite>();
		
		//retrieve security prerequisites
		for (PcmAssemblyStereotypesMatch match : loadStereotypedMatches(element)) {
			AssemblyContext ac = match.getAssemblyContext();
			Stereotype stereo = match.getStereotypeApplication().getStereotype();
			
			if (stereo.getName() == PREREQUISITE_STEREOTYPE_NAME) {
				EStructuralFeature feature = StereotypeAPI.getTaggedValue(ac, PREREQUISITE_REFERENCE_NAME, PREREQUISITE_STEREOTYPE_NAME);
				for (org.eclipse.emf.ecore.EObject obj : feature.eContents()) {
					if (!(obj instanceof Prerequisite))
						continue;
					returnCollection.add((Prerequisite) obj);
				}
			}
		}
		
		return returnCollection;
	}
	
	/**
	 * Loads all security patterns that are applied to a given system element
	 * @param element The system element to be analyzed
	 * @return Collection of patterns
	 * @throws LoadingException
	 */
	private Collection<SecurityPattern> getSecurityPatternsByElement(AssemblyContext element) throws LoadingException {
		//init return collection
		Collection<SecurityPattern> returnCollection = new HashSet<SecurityPattern>();
		
		//retrieve security prerequisites
		for (PcmAssemblyStereotypesMatch match : loadStereotypedMatches(element)) {
			AssemblyContext ac = match.getAssemblyContext();
			Stereotype stereo = match.getStereotypeApplication().getStereotype();
			
			if (stereo.getName() == PREREQUISITE_STEREOTYPE_NAME) {
				EStructuralFeature feature = StereotypeAPI.getTaggedValue(ac, PREREQUISITE_REFERENCE_NAME, PREREQUISITE_STEREOTYPE_NAME);
				for (org.eclipse.emf.ecore.EObject obj : feature.eContents()) {
					if (!(obj instanceof Role))
						continue;
					returnCollection.add(((Role) obj).getSecurityPattern());
				}
			}
		}
		
		return returnCollection;
	}

	/**
	 * Loads all attacks that are executable given a certain list of existing prerequisites
	 * @param prerequisites The existing prerequisites
	 * @return Collection of attacks
	 * @throws LoadingException
	 */
	private Collection<Attack> getPossibleAttacksByPrerequisites(Collection<Prerequisite> prerequisites) throws LoadingException {
		//init return collection
		Collection<Attack> returnCollection = new HashSet<Attack>();
		
		//retrieve attacks
		for (SecurityAttacksMatch match : loadPossibleAttacks(prerequisites)) {
			Attack attack = match.getAttack();
			
			if (!returnCollection.contains(attack)) {
				returnCollection.add(attack);
			}
		}
		
		return returnCollection;
	}

	/* +-----------------------+
	 * | viatra loader methods |
	 * +-----------------------+
	 */

	/**
	 * Fetches an AssemblyContext object by its ID
	 * @return The AssemblyContext instance
	 * @throws LoadingException
	 * @throws NoSuchElementException
	 */
	private AssemblyContext getAssemblyContextById(final String id) throws LoadingException, NoSuchElementException {
		//load matcher
		PcmAssemblyStereotypesMatcher matcher = null;
		try {
			matcher = PcmAssemblyStereotypesMatcher.on(systemEngine);
		} catch (ViatraQueryException e) {
			LoadingException wrapperException = new LoadingException(e.getMessage(), e.getCause());
			wrapperException.setStackTrace(e.getStackTrace());
			throw wrapperException;			
		}
		
		final Collection<AssemblyContext> returnObjects = new HashSet<AssemblyContext>();	
		matcher.forEachMatch(new IMatchProcessor<PcmAssemblyStereotypesMatch>() {
			@Override
			public void process(PcmAssemblyStereotypesMatch match) {
				if (match.getAssemblyContext().getId().equals(id)) {
					returnObjects.add(match.getAssemblyContext());
				}
			}
		});	
		
		if (returnObjects.isEmpty())
			throw new NoSuchElementException();
		
		return returnObjects.iterator().next();
	}
	
	/**
	 * Loads possible attacks from the viatra engine
	 * 
	 * @return filtered matches
	 * @throws LoadingException
	 */
	private Collection<SecurityAttacksMatch> loadPossibleAttacks(final Collection<Prerequisite> prerequisites) throws LoadingException {
		//load matcher
		SecurityAttacksMatcher matcher = null;
		try {
			matcher = SecurityAttacksMatcher.on(securityEngine);
		} catch (ViatraQueryException e) {
			LoadingException wrapperException = new LoadingException(e.getMessage(), e.getCause());
			wrapperException.setStackTrace(e.getStackTrace());
			throw wrapperException;			
		}
		
		final Collection<SecurityAttacksMatch> returnCollection = new HashSet<SecurityAttacksMatch>();	
		matcher.forEachMatch(new IMatchProcessor<SecurityAttacksMatch>() {
			@Override
			public void process(SecurityAttacksMatch match) {
				if (prerequisites.containsAll(match.getAttack().getPrerequisites())) {
					returnCollection.add(match);
				}
			}
		});	
		return returnCollection;
	}
	
	/**
	 * Loads the stereotypes of a given element from the viatra engine
	 * 
	 * @return filtered matches
	 * @throws LoadingException
	 */
	private Collection<PcmAssemblyStereotypesMatch> loadStereotypedMatches(final AssemblyContext element) throws LoadingException {
		//load matcher
		PcmAssemblyStereotypesMatcher matcher = null;
		try {
			matcher = PcmAssemblyStereotypesMatcher.on(systemEngine);
		} catch (ViatraQueryException e) {
			LoadingException wrapperException = new LoadingException(e.getMessage(), e.getCause());
			wrapperException.setStackTrace(e.getStackTrace());
			throw wrapperException;			
		}
		
		final Collection<PcmAssemblyStereotypesMatch> returnCollection = new HashSet<PcmAssemblyStereotypesMatch>();	
		matcher.forEachMatch(new IMatchProcessor<PcmAssemblyStereotypesMatch>() {
			@Override
			public void process(PcmAssemblyStereotypesMatch match) {
				if (match.getAssemblyContext().equals(element)) {
					returnCollection.add(match);
				}
			}
		});	
		return returnCollection;
	}
}
