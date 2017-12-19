package de.fzi.decision.security.ui.controllers.viewerfilters;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import security.securityPatterns.SecurityPattern;
import security.securityPrerequisites.Prerequisite;
import security.securityThreats.Attack;

/**
 * A ViewerFilter that consists of a set of attacks that shall be show in the interface.
 */
public class AttackByPatternFilter extends ViewerFilter {
	private Set<Attack> attacksToShow = new HashSet<Attack>();
	private boolean showAll = true;
	private Viewer viewer;
	
	/**
	 * Keeps track of the viewer this filter is used for to automatically call viewer.refresh() when a new filter is
	 * set.
	 * 
	 * @param viewer the viewer this filter shall used for
	 */
	public AttackByPatternFilter(Viewer viewer) {
		this.viewer = viewer;
	}
	
	/**
	 * Sets the filter according to a selection of SecurityPatterns. For each SecurityPattern all prerequisites are
	 * fetched and for those prerequisites all attacks corresponding to at least one of those prerequisites are added
	 * to the filter.
	 * 
	 * @param patterns array of SecurityPatterns
	 */
	public void setFilter(Object[] patterns) {
		showAll = patterns.length == 0;
		
		attacksToShow.clear();
		
		for (Object object : patterns) {
			if (object instanceof SecurityPattern) {
				SecurityPattern pattern = (SecurityPattern) object;
				Collection<Prerequisite> prerequisites = pattern.getMitigatedPrerequisites();
				
				for (Prerequisite prerequisite : prerequisites) {
					attacksToShow.addAll(prerequisite.getAttacks());
				}
			}
		}
			
		viewer.refresh();
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (showAll) {
			return true;
		}
		
		return attacksToShow.contains(element);
	}
}
