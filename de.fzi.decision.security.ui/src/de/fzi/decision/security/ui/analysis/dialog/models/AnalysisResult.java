package de.fzi.decision.security.ui.analysis.dialog.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.core.entity.Entity;

import analysis.PreReqSecSecurityAnalyzer;
import de.fzi.decision.security.ui.analysis.dialog.views.CCTabItem;
import security.securityPatterns.Role;
import security.securityPatterns.SecurityPattern;
import security.securityPrerequisites.Prerequisite;
import security.securityThreats.Attack;

/**
 * A model class for storing the results of the analysis. One instance of this
 * class per system (and per TabItem).
 * 
 * @author Robert Hochweiss
 *
 */
public class AnalysisResult {

	private String sysName;
	private Map<SecurityPattern, List<Role>> incorrectlyAppliedPatterns;
	private Map<Entity, List<Attack>> vulnElements;
	private List<Entity> secureElements;
	private CCTabItem tabItem;
	private PreReqSecSecurityAnalyzer analyzer;

	/**
	 * 
	 * @param tabItem the custom TabItem that shows the system which is to be
	 *                analyzed and the results for the security analysis
	 */
	public AnalysisResult(CCTabItem tabItem) {
		this.sysName = tabItem.getSysName();
		this.tabItem = tabItem;
		incorrectlyAppliedPatterns = new HashMap<SecurityPattern, List<Role>>();
		vulnElements = new HashMap<Entity, List<Attack>>();
		secureElements = new ArrayList<Entity>();
		analyzer = new PreReqSecSecurityAnalyzer();
	}

	/**
	 * Executes the structural analysis for the selected elements of this system.
	 */
	public void analyzeStructural() {
		resetAnalysis();
		for (Object o : tabItem.getCheckedElements()) {
			incorrectlyAppliedPatterns.putAll(analyzer.analyzeExtended((EObject) o).getOne());
		}
	}

	/**
	 * Executes the contextual analysis for the selected elements of this system.
	 */
	public void analyzeContextual() {
		analyzeStructural();
		for (Object o : tabItem.getCheckedElements()) {
			vulnElements.put((Entity) o, analyzer.analyzeExtended((EObject) o).getTwo());

		}
		for (Entry<Entity, List<Attack>> entry : vulnElements.entrySet()) {
			if (entry.getValue().size() == 0) {
				secureElements.add(entry.getKey());
			}
		}
		for (Entity e : secureElements) {
			vulnElements.remove(e);
		}
	}

	/**
	 * Saves the current results of the security analysis for the current system.
	 * 
	 * @return the results of the analysis
	 */
	public String saveResults() {
		String results = "";
		results = results + "Results of the security analysis for the current system: " + sysName + "\n\n";
		results = results + "Results of the structural analysis: \n\n";
		if (incorrectlyAppliedPatterns.isEmpty()) {
			results = results + "\t"
					+ "All security patterns are correctly applied or there is no security pattern to apply!\n\n";
		} else {
			// Print the incorrectly applied security pattern and per pattern print the
			// specific incorrectly applied roles
			for (Entry<SecurityPattern, List<Role>> e : incorrectlyAppliedPatterns.entrySet()) {
				results = results + "\t" + "Security pattern: " + e.getKey().getName() + " is not correctly applied!\n";
				for (Role r : e.getValue()) {
					results = results + "\t\t" + "Role: " + r.getName() + " is not correctly applied!\n";
				}
				results = results + "\n";
			}
		}
		results = results + "Results of the contextuell analysis: \n\n";
		if (vulnElements.isEmpty() && secureElements.isEmpty()) {
			results = results + "\t" + "Either no element or the contextual analysis itself is not selected.";
		}
		// Print the vulnerable elements, per element the possible attack an per attack
		// the unmitigated prerequisites
		for (Entry<Entity, List<Attack>> e : vulnElements.entrySet()) {
			results = results + "\t" + "The element: " + e.getKey().getEntityName() + " is vulnerable!\n";
			for (Attack a : e.getValue()) {
				results = results + "\t\t" + "The attack: " + a.getName() + " is possible!\n";
				for (Prerequisite p : a.getPrerequisites()) {
					results = results + "\t\t\t" + "The prerequisite: " + p.getName() + " is unmitigated!\n";
				}
			}
			results = results + "\n";
		}
		for (Entity e : secureElements) {
			results = results + "\t" + "The element: " + e.getEntityName() + " is secure!\n";
		}
		return results.trim();
	}

	/**
	 * 
	 * @return
	 */
	public Map<SecurityPattern, List<Role>> getIncorrectlyAppliedPatterns() {
		return incorrectlyAppliedPatterns;
	}

	/**
	 * 
	 * @return
	 */
	public Map<Entity, List<Attack>> getVulnElements() {
		return vulnElements;
	}

	/**
	 * 
	 * @return
	 */
	public List<Entity> getSecureElements() {
		return secureElements;
	}
	
	private void resetAnalysis() {
		incorrectlyAppliedPatterns = new HashMap<SecurityPattern, List<Role>>();
		vulnElements = new HashMap<Entity, List<Attack>>();
		secureElements = new ArrayList<Entity>();
	}

}
