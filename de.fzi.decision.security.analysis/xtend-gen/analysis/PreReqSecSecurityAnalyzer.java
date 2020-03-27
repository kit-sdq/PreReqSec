package analysis;

import analysis.ModelQueryEngine;
import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.modelversioning.emfprofileapplication.StereotypeApplication;
import org.palladiosimulator.mdsdprofiles.api.StereotypeAPI;
import security.securityPatterns.Role;
import security.securityPatterns.SecurityPattern;
import security.securityPrerequisites.Prerequisite;
import security.securityThreats.Attack;

/**
 * PreReqSecSecurityAnalyzer provides security analysis capabilities
 * following the PreReqSec methodology.
 * 
 * @author Fabian Scheytt, Robert Hochweiss
 */
@SuppressWarnings("all")
public class PreReqSecSecurityAnalyzer {
  private static String SECURITY_PATTERN_NAME = "SecurityPatternRole";
  
  private static String SECURITY_PATTERN_VALUE_NAME = "roles";
  
  private static String PREREQ_NAME = "ComponentPrerequisite";
  
  private static String PREREQ_VALUE_NAME = "prerequisites";
  
  private ModelQueryEngine engine;
  
  /**
   * Analyzes the attack surface of given element according to the PreReqSec methodology
   * and returns a List of possible Attacks
   * 
   * @param component The element on which the security analysis should be executed
   * @return A list of possible attacks on the given element
   */
  public Pair<ArrayList<SecurityPattern>, ArrayList<Attack>> analyze(final EObject component) {
    Pair<ArrayList<SecurityPattern>, ArrayList<Attack>> _xblockexpression = null;
    {
      Resource _eResource = component.eResource();
      EMFScope _eMFScope = new EMFScope(_eResource);
      ModelQueryEngine _modelQueryEngine = new ModelQueryEngine(_eMFScope);
      this.engine = _modelQueryEngine;
      List<Prerequisite> unmitigatedPreq = this.getAnnotatedPrerequisites(component);
      ArrayList<SecurityPattern> _arrayList = new ArrayList<SecurityPattern>();
      ArrayList<Attack> _arrayList_1 = new ArrayList<Attack>();
      final Pair<ArrayList<SecurityPattern>, ArrayList<Attack>> results = Tuples.<ArrayList<SecurityPattern>, ArrayList<Attack>>pair(_arrayList, _arrayList_1);
      final ArrayList<Prerequisite> mitigatedPrerequisites = new ArrayList<Prerequisite>();
      final Consumer<SecurityPattern> _function = (SecurityPattern it) -> {
        boolean _patternCorrectlyApplied = this.patternCorrectlyApplied(it);
        if (_patternCorrectlyApplied) {
          mitigatedPrerequisites.addAll(this.getMitigatedPrerequisites(it));
        } else {
          results.getOne().add(it);
        }
      };
      this.getAnnotatedSecurityPatterns(component).forEach(_function);
      unmitigatedPreq.removeAll(mitigatedPrerequisites);
      boolean _isEmpty = unmitigatedPreq.isEmpty();
      boolean _not = (!_isEmpty);
      if (_not) {
        ResourceSet _resourceSet = unmitigatedPreq.get(0).eResource().getResourceSet();
        ResourceSet _resourceSet_1 = component.eResource().getResourceSet();
        final HashSet<ResourceSet> scope = new HashSet<ResourceSet>(
          Collections.<ResourceSet>unmodifiableList(CollectionLiterals.<ResourceSet>newArrayList(_resourceSet, _resourceSet_1)));
        EMFScope _eMFScope_1 = new EMFScope(scope);
        ModelQueryEngine _modelQueryEngine_1 = new ModelQueryEngine(_eMFScope_1);
        this.engine = _modelQueryEngine_1;
      }
      results.getTwo().addAll(this.getPossibleAttacks(unmitigatedPreq));
      _xblockexpression = results;
    }
    return _xblockexpression;
  }
  
  public Pair<HashMap<SecurityPattern, List<Role>>, ArrayList<Attack>> analyzeExtended(final EObject component) {
    Pair<HashMap<SecurityPattern, List<Role>>, ArrayList<Attack>> _xblockexpression = null;
    {
      Resource _eResource = component.eResource();
      EMFScope _eMFScope = new EMFScope(_eResource);
      ModelQueryEngine _modelQueryEngine = new ModelQueryEngine(_eMFScope);
      this.engine = _modelQueryEngine;
      List<Prerequisite> unmitigatedPreq = this.getAnnotatedPrerequisites(component);
      HashMap<SecurityPattern, List<Role>> _hashMap = new HashMap<SecurityPattern, List<Role>>();
      ArrayList<Attack> _arrayList = new ArrayList<Attack>();
      final Pair<HashMap<SecurityPattern, List<Role>>, ArrayList<Attack>> results = Tuples.<HashMap<SecurityPattern, List<Role>>, ArrayList<Attack>>pair(_hashMap, _arrayList);
      final ArrayList<Prerequisite> mitigatedPrerequisites = new ArrayList<Prerequisite>();
      final Consumer<SecurityPattern> _function = (SecurityPattern it) -> {
        boolean _patternCorrectlyApplied = this.patternCorrectlyApplied(it);
        if (_patternCorrectlyApplied) {
          mitigatedPrerequisites.addAll(this.getMitigatedPrerequisites(it));
        } else {
          results.getOne().put(it, this.getUnappliedRoles(it));
        }
      };
      this.getAnnotatedSecurityPatterns(component).forEach(_function);
      unmitigatedPreq.removeAll(mitigatedPrerequisites);
      boolean _isEmpty = unmitigatedPreq.isEmpty();
      boolean _not = (!_isEmpty);
      if (_not) {
        ResourceSet _resourceSet = unmitigatedPreq.get(0).eResource().getResourceSet();
        ResourceSet _resourceSet_1 = component.eResource().getResourceSet();
        final HashSet<ResourceSet> scope = new HashSet<ResourceSet>(
          Collections.<ResourceSet>unmodifiableList(CollectionLiterals.<ResourceSet>newArrayList(_resourceSet, _resourceSet_1)));
        EMFScope _eMFScope_1 = new EMFScope(scope);
        ModelQueryEngine _modelQueryEngine_1 = new ModelQueryEngine(_eMFScope_1);
        this.engine = _modelQueryEngine_1;
      }
      results.getTwo().addAll(this.getPossibleAttacks(unmitigatedPreq));
      _xblockexpression = results;
    }
    return _xblockexpression;
  }
  
  /**
   * Finds all Prerequisites that are annotated to an EObject by StereotypeApplications
   * @param object The Object to query
   * @returns List of annotated Prerequisites
   */
  private List<Prerequisite> getAnnotatedPrerequisites(final EObject object) {
    ArrayList<Prerequisite> _xblockexpression = null;
    {
      final Object stereotypeValues = this.getAnnotatedStereotypeValue(object, PreReqSecSecurityAnalyzer.PREREQ_NAME, PreReqSecSecurityAnalyzer.PREREQ_VALUE_NAME);
      final ArrayList<Prerequisite> prerequisites = new ArrayList<Prerequisite>();
      if ((stereotypeValues instanceof EList<?>)) {
        final Consumer<Object> _function = (Object it) -> {
          if ((it instanceof Prerequisite)) {
            prerequisites.add(((Prerequisite)it));
          }
        };
        ((EList<?>)stereotypeValues).forEach(_function);
      }
      _xblockexpression = prerequisites;
    }
    return _xblockexpression;
  }
  
  /**
   * Queries an EObject for Stereotypes and returns all SecurityPatterns that are indirectly annotated to the object by applied security pattern roles.
   * @param object The Object to query
   * @returns A list of all indirectly annotated SecurityPatterns
   */
  private List<SecurityPattern> getAnnotatedSecurityPatterns(final EObject object) {
    List<SecurityPattern> _xblockexpression = null;
    {
      final Object stereotypeValues = this.getAnnotatedStereotypeValue(object, PreReqSecSecurityAnalyzer.SECURITY_PATTERN_NAME, PreReqSecSecurityAnalyzer.SECURITY_PATTERN_VALUE_NAME);
      final ArrayList<SecurityPattern> patterns = new ArrayList<SecurityPattern>();
      if ((stereotypeValues instanceof EList<?>)) {
        final Consumer<Object> _function = (Object it) -> {
          if ((it instanceof Role)) {
            patterns.add(((Role)it).getSecurityPattern());
          }
        };
        ((EList<?>)stereotypeValues).forEach(_function);
      }
      _xblockexpression = IterableExtensions.<SecurityPattern>toList(IterableExtensions.<SecurityPattern>toSet(patterns));
    }
    return _xblockexpression;
  }
  
  /**
   * Gets the value of a stereotype application on an EObject
   * @param target The Eobject to query for stereotypes
   * @param stereotypeName Name of the StereotypeApplication pattern
   * @param stereotypeValueName Name of the StereotypeApplication value
   * @returns The value of the stereotype application
   */
  private Object getAnnotatedStereotypeValue(final EObject target, final String stereotypeName, final String stereotypeValueName) {
    Object _xblockexpression = null;
    {
      final Set<StereotypeApplication> queryResult = this.engine.getStereotypesOnElement(target);
      for (final StereotypeApplication value : queryResult) {
        if ((((value != null) && (value.getStereotype() != null)) && Objects.equal(value.getStereotype().getName(), stereotypeName))) {
          return StereotypeAPI.<Object>getTaggedValue(target, stereotypeValueName, stereotypeName);
        }
      }
      _xblockexpression = null;
    }
    return _xblockexpression;
  }
  
  /**
   * Gets all Prerequisites that are mitigated by a SecurityPattern
   * @param pattern The SecurityPattern to get the Prerequisites from
   * @returns A list of Prerequisites or an empty list
   */
  private List<Prerequisite> getMitigatedPrerequisites(final SecurityPattern pattern) {
    List<Prerequisite> _xifexpression = null;
    if (((pattern != null) && (pattern.getMitigatedPrerequisites() != null))) {
      _xifexpression = pattern.getMitigatedPrerequisites();
    } else {
      _xifexpression = Collections.<Prerequisite>unmodifiableList(CollectionLiterals.<Prerequisite>newArrayList());
    }
    return _xifexpression;
  }
  
  /**
   * Finds all Attacks whose required Prerequisites are a subset of the unmitigated Prerequisites.
   * @param unmitigatedPrerequisites All Prerequisites that are satisfied in the current context
   * @returns possible Attacks derived of the unmitigated prerequisites
   */
  private List<Attack> getPossibleAttacks(final Collection<Prerequisite> unmitigatedPrerequisites) {
    final Function1<Attack, Boolean> _function = (Attack it) -> {
      return Boolean.valueOf(unmitigatedPrerequisites.containsAll(it.getPrerequisites()));
    };
    return IterableExtensions.<Attack>toList(IterableExtensions.<Attack>filter(IterableExtensions.<Attack>toList(this.engine.getAttacksByPrerequisites(unmitigatedPrerequisites)), _function));
  }
  
  /**
   * Evaluates if the given pattern is correctly applied by querying all applied PatternRole stereotypes of the instance.
   * A pattern is deemed correctly applied if each role is assigned at least once!
   * @param pattern The security pattern to check appliance for
   * @returns true if pattern is correctly applied
   */
  private boolean patternCorrectlyApplied(final SecurityPattern pattern) {
    final Function1<StereotypeApplication, Iterable<?>> _function = (StereotypeApplication it) -> {
      Object _xifexpression = null;
      if ((((it != null) && (it.getStereotype() != null)) && Objects.equal(it.getStereotype().getName(), PreReqSecSecurityAnalyzer.SECURITY_PATTERN_NAME))) {
        return StereotypeAPI.<Iterable<?>>getTaggedValue(it.getAppliedTo(), PreReqSecSecurityAnalyzer.SECURITY_PATTERN_VALUE_NAME, PreReqSecSecurityAnalyzer.SECURITY_PATTERN_NAME);
      } else {
        _xifexpression = null;
      }
      return ((Iterable<?>)_xifexpression);
    };
    final Function1<Iterable<?>, Boolean> _function_1 = (Iterable<?> it) -> {
      return Boolean.valueOf((it != null));
    };
    return IterableExtensions.<Object>toList(Iterables.<Object>concat(IterableExtensions.<Iterable<?>>filter(IterableExtensions.<StereotypeApplication, Iterable<?>>map(this.engine.getAllStereotypeApplications(), _function), _function_1))).containsAll(pattern.getRoles());
  }
  
  private List<Role> getUnappliedRoles(final SecurityPattern pattern) {
    ArrayList<Role> _xblockexpression = null;
    {
      final Function1<StereotypeApplication, Iterable<?>> _function = (StereotypeApplication it) -> {
        Object _xifexpression = null;
        if ((((it != null) && (it.getStereotype() != null)) && Objects.equal(it.getStereotype().getName(), PreReqSecSecurityAnalyzer.SECURITY_PATTERN_NAME))) {
          return StereotypeAPI.<Iterable<?>>getTaggedValue(it.getAppliedTo(), PreReqSecSecurityAnalyzer.SECURITY_PATTERN_VALUE_NAME, PreReqSecSecurityAnalyzer.SECURITY_PATTERN_NAME);
        } else {
          _xifexpression = null;
        }
        return ((Iterable<?>)_xifexpression);
      };
      final Function1<Iterable<?>, Boolean> _function_1 = (Iterable<?> it) -> {
        return Boolean.valueOf((it != null));
      };
      final List<Object> allARoles = IterableExtensions.<Object>toList(Iterables.<Object>concat(IterableExtensions.<Iterable<?>>filter(IterableExtensions.<StereotypeApplication, Iterable<?>>map(this.engine.getAllStereotypeApplications(), _function), _function_1)));
      ArrayList<Role> tmp = new ArrayList<Role>();
      EList<Role> _roles = pattern.getRoles();
      for (final Role r : _roles) {
        boolean _contains = allARoles.contains(r);
        boolean _not = (!_contains);
        if (_not) {
          tmp.add(r);
          InputOutput.<String>println(r.getName());
        }
      }
      _xblockexpression = tmp;
    }
    return _xblockexpression;
  }
}
