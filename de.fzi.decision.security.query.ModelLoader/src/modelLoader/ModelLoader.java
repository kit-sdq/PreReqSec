package modelLoader;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.viatra.query.runtime.api.IMatchProcessor;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;

import patterns.SecurityCatalogMatch;
import patterns.SecurityCatalogMatcher;
import security.securityPatterns.SecurityPattern;
import security.securityPrerequisites.Prerequisite;
import security.securityThreats.Attack;
import security.securityThreats.Attacker;

/**
 * This class is used to load models. Create a new ModelLoader, apply filters and use one of the load-operations.
 * 
 * Usage-example:
 *     Collection<attacker> c = new ModelLoader(engine)
 *         .filterByattackerName("ScriptKiddie")
 *         .loadattackeres();
 *         
 * @author Benjamin Plach
 *
 */
public class ModelLoader {
	
	private String securityPatternName = null;
	private String securityPatternDescription = null;
	
	private String prerequisiteName = null;
	private String prerequisiteDescription = null;

	private String attackName = null;
	private String attackDescription = null;
	
	private String attackerName = null;
	private String attackerDescription = null;
	
	private ViatraQueryEngine engine;
	
	/**
	 * Creates a new model loader. Add filters by using loader.filterBy[...](...)-methods to specify the objects to be loaded.
	 * Load by using one of the load[...]()-methods.
	 * 
	 * @param engine a model-loader engine
	 */
	public ModelLoader(ModelLoaderEngine engine) {
		this.engine = engine.getEngine();			
	}
	
	/* +------------------+
	 * | filter - methods |
	 * +------------------+
	 */
	
	public ModelLoader filterBySecurityPatternName(String securityPatternName) {
		this.securityPatternName = securityPatternName;
		return this;
	}

	public ModelLoader filterBySecurityPatternDescription(String securityPatternDescription) {
		this.securityPatternDescription = securityPatternDescription;
		return this;
	}

	public ModelLoader filterByPrerequisiteName(String prerequisiteName) {
		this.prerequisiteName = prerequisiteName;
		return this;
	}

	public ModelLoader filterByPrerequisiteDescription(String prerequisiteDescription) {
		this.prerequisiteDescription = prerequisiteDescription;
		return this;
	}

	public ModelLoader filterByAttackName(String attackName) {
		this.attackName = attackName;
		return this;
	}

	public ModelLoader filterByAttackDescription(String attackDescription) {
		this.attackDescription = attackDescription;
		return this;
	}

	public ModelLoader filterByAttackerName(String attackerName) {
		this.attackerName = attackerName;
		return this;
	}

	public ModelLoader filterByAttackerDescription(String attackerDescription) {
		this.attackerDescription = attackerDescription;
		return this;
	}

	/* +----------------+
	 * | load - methods |
	 * +----------------+
	 */
		
	/**
	 * Loads the securityPatterns matching the defined filters
	 * 	
	 * @return Collection of securityPatterns
	 * @throws LoadingException
	 */
	public Collection<SecurityPattern> loadSecurityPatterns() throws LoadingException {
		//init return Collection
		Collection<SecurityPattern> returnCollection = new HashSet<SecurityPattern>();
		
		//retrieve security patterns
		for (SecurityCatalogMatch match : getFilteredMatches()) {
			returnCollection.add(match.getSecurityPattern());
		}
		return returnCollection;
	}
	
	/**
	 * Loads the prerequisites matching the defined filters
	 * 	
	 * @return Collection of prerequisites
	 * @throws LoadingException
	 */
	public Collection<Prerequisite> loadPrerequisites() throws LoadingException {
		//init return Collection
		Collection<Prerequisite> returnCollection = new HashSet<Prerequisite>();
		
		//retrieve prerequisites
		for (SecurityCatalogMatch match : getFilteredMatches()) {			
			returnCollection.add(match.getPrerequisite());
		}		
		return returnCollection;
	}
	
	/**
	 * Loads the attacks matching the defined filters
	 * 	
	 * @return Collection of attacks
	 * @throws LoadingException
	 */
	public Collection<Attack> loadAttacks() throws LoadingException {
		//init return Collection
		Collection<Attack> returnCollection = new HashSet<Attack>();
		
		//retrieve attacks
		for (SecurityCatalogMatch match : getFilteredMatches()) {
			returnCollection.add(match.getAttack());
		}		
		return returnCollection;
	}
	
	/**
	 * Loads the attackers matching the defined filters
	 * 	
	 * @return Collection of attackers
	 * @throws LoadingException
	 */
	public Collection<Attacker> loadAttackers() throws LoadingException {
		//init return Collection
		Collection<Attacker> returnCollection = new HashSet<Attacker>();
		
		//retrieve attacker classes
		for (SecurityCatalogMatch match : getFilteredMatches()) {			
			returnCollection.add(match.getAttackerClass());
		}
		return returnCollection;
	}	
	
	/**
	 * filters the matches
	 * 
	 * @return filtered matches
	 * @throws LoadingException
	 */
	private Collection<SecurityCatalogMatch> getFilteredMatches() throws LoadingException {
		//load matcher
		SecurityCatalogMatcher matcher = null;
		try {
			matcher = SecurityCatalogMatcher.on(engine);
		} catch (ViatraQueryException e) {
			LoadingException wrapperException = new LoadingException(e.getMessage(), e.getCause());
			wrapperException.setStackTrace(e.getStackTrace());
			throw wrapperException;			
		}
		
		//init return Collection
		Collection<SecurityCatalogMatch> returnCollection = new HashSet<SecurityCatalogMatch>();
				
		matcher.forEachMatch(new IMatchProcessor<SecurityCatalogMatch>() {
			@Override
			public void process(SecurityCatalogMatch match) {				
				if (// check if the attack can be repelled by the security pattern?
					match.getSecurityPattern().getMitigatedPrerequisites().containsAll(match.getAttack().getPrerequisites())  == true
					
					// check if the security pattern passes the filters
					&& checkSecurityPatternFilters(match.getSecurityPattern()) == true
					
					// check if the prerequisite passes the filters
					&& checkPrerequisiteFilters(match.getPrerequisite()) == true
					
					// check if the attack passes the filters
					&& checkAttackFilters(match.getAttack()) == true
					
					// check if the attacker class passes the filters
					&& checkAttackerFilters(match.getAttackerClass()) == true)
				{
					returnCollection.add(match);
				}				
			}
		});
				
		return returnCollection;
	}
	
	
	/* +----------------------+
	 * | comparator - methods |
	 * +----------------------+
	 */
	
	/**
	 * checks is a given securityPattern has at least one value matching the defined filters
	 * 
	 * @param securityPattern the securityPattern to be checked
	 * @return true if it is a match, false if not
	 */
	private boolean checkSecurityPatternFilters(SecurityPattern securityPattern) {
		if ((securityPatternName == null           || securityPattern.getName() == null        || securityPattern.getName().toLowerCase().equals(securityPatternName.toLowerCase() ))
		    && (securityPatternDescription == null || securityPattern.getDescription() == null || securityPattern.getDescription().toLowerCase().contains(securityPatternDescription.toLowerCase() ))) {
				return true;
		} else {
				return false;
		}
	}
	
	/**
	 * checks is a given prerequisite has at least one value matching the defined filters
	 * 
	 * @param prerequisite the prerequisite to be checked
	 * @return true if it is a match, false if not
	 */
	private boolean checkPrerequisiteFilters(Prerequisite prerequisite) {
		if ((prerequisiteName == null           || prerequisite.getName() == null        || prerequisite.getName().toLowerCase().equals(prerequisiteName.toLowerCase() ))
		    && (prerequisiteDescription == null || prerequisite.getDescription() == null || prerequisite.getDescription().toLowerCase().contains(prerequisiteDescription.toLowerCase() ))) {
				return true;
		} else {
				return false;
		}
	}
	
	/**
	 * checks is a given attack has at least one value matching the defined filters
	 * 
	 * @param attack the attack to be checked
	 * @return true if it is a match, false if not
	 */
	private boolean checkAttackFilters(Attack attack) {
		if ((attackName == null                  || attack.getName() == null               || attack.getName().toLowerCase().equals(attackName.toLowerCase()))
		    && (attackDescription == null        || attack.getDescription() == null        || attack.getDescription().toLowerCase().contains(attackDescription.toLowerCase()))){
				return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * checks is a given attacker has at least one value matching the defined filters
	 * 
	 * @param attacker the attacker to be checked
	 * @return true if it is a match, false if not
	 */
	private boolean checkAttackerFilters(Attacker attacker) {
		if ((attackerName == null               || attacker.getName() == null            || attacker.getName().toLowerCase().equals(attackerName.toLowerCase() ))
			&& (attackerDescription == null     || attacker.getDescription() == null     || attacker.getDescription().toLowerCase().contains(attackerDescription.toLowerCase() ))) {
				return true;
		} else {
				return false;
		}
	}
	
}
