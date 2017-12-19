package de.fzi.decision.security.ui.controllers.viewerfilters;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import security.securityPrerequisites.Prerequisite;
import security.securityThreats.Attack;

/**
 * A ViewerFilter that consists of a set of attacks that shall be show in the interface.
 */
public class AttackByPrerequisiteFilter extends ViewerFilter {
	private Set<Attack> attacksToShow = new HashSet<Attack>();
	private boolean showAll = true;
	private Viewer viewer;
	
	/**
	 * Keeps track of the viewer this filter is used for to automatically call viewer.refresh() when a new filter is
	 * set.
	 * 
	 * @param viewer the viewer this filter shall used for
	 */
	public AttackByPrerequisiteFilter(Viewer viewer) {
		this.viewer = viewer;
	}
	
	/**
	 * Sets the filter according to a selection of Prerequisites. For each Prerequisites all attacks are
	 * fetched and added to the filter.
	 * 
	 * @param patterns array of Prerequisites
	 */
	public void setFilter(Object[] prerequisites) {
		showAll = prerequisites.length == 0;
		
		attacksToShow.clear();
		
		for (Object object: prerequisites) {
			if (object instanceof Prerequisite) {
				Prerequisite prerequisite = (Prerequisite) object;
				attacksToShow.addAll(prerequisite.getAttacks());
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
