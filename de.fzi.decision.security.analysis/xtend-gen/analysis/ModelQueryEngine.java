package analysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.modelversioning.emfprofileapplication.StereotypeApplication;
import patterns.AttackByPrerequisiteMatcher;
import patterns.StereotypesOnElementMatcher;
import security.securityPrerequisites.Prerequisite;
import security.securityThreats.Attack;

/**
 * PreReqSec relevant query operations for model instances using EMF-Profile
 * stereotypes and PreReqSec security catalogs
 * 
 * @autor Fabian Scheytt, Robert Hochweiss
 */
@SuppressWarnings("all")
public class ModelQueryEngine {
  private final ViatraQueryEngine engine;
  
  /**
   * Initializes the ModelQueryEngine
   * @param scope A VIATRA EMF scope
   */
  public ModelQueryEngine(final EMFScope scope) {
    this.engine = ViatraQueryEngine.on(scope);
  }
  
  /**
   * Queries all EMF-Profile StereotypeApplications on the given element
   * @param element The EObject to query for StereotypeApplications
   * @returns Set of applied StereotypeApplications
   */
  public Set<StereotypeApplication> getStereotypesOnElement(final EObject element) {
    Set<StereotypeApplication> _xblockexpression = null;
    {
      final StereotypesOnElementMatcher matcher = StereotypesOnElementMatcher.on(this.engine);
      _xblockexpression = matcher.getAllValuesOfstereotypeApplication(element);
    }
    return _xblockexpression;
  }
  
  /**
   * Queries all Attacks that require <b>at least one</b> of the given prerequisites
   * @param prerequisite Prerequisites to consider for query
   * @returns Set of relevant Attacks
   */
  public Set<Attack> getAttacksByPrerequisites(final Collection<Prerequisite> prerequisites) {
    Set<Attack> _xblockexpression = null;
    {
      final AttackByPrerequisiteMatcher matcher = AttackByPrerequisiteMatcher.on(this.engine);
      final HashSet<Attack> result = new HashSet<Attack>();
      final Consumer<Prerequisite> _function = (Prerequisite it) -> {
        final Set<Attack> matches = matcher.getAllValuesOfattack(it);
        result.addAll(matches);
      };
      prerequisites.forEach(_function);
      _xblockexpression = IterableExtensions.<Attack>toSet(result);
    }
    return _xblockexpression;
  }
  
  public Set<StereotypeApplication> getAllStereotypeApplications() {
    Set<StereotypeApplication> _xblockexpression = null;
    {
      final StereotypesOnElementMatcher matcher = StereotypesOnElementMatcher.on(this.engine);
      _xblockexpression = matcher.getAllValuesOfstereotypeApplication();
    }
    return _xblockexpression;
  }
}
