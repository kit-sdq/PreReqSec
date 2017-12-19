package de.fzi.decision.security.ui.controllers.viewerfilters;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import security.securityPatterns.SecurityPattern;
import security.securityPrerequisites.Prerequisite;

/**
 * A ViewerFilter that consists of a set of prerequisites that shall be show in the interface.
 */
public class PrerequisiteByPatternFilter extends ViewerFilter {
	private Set<Prerequisite> prerequisitesToShow = new HashSet<Prerequisite>();
	private boolean showAll = true;
	private Viewer viewer;
	
	/**
	 * Keeps track of the viewer this filter is used for to automatically call viewer.refresh() when a new filter is
	 * set.
	 * 
	 * @param viewer the viewer this filter shall used for
	 */
	public PrerequisiteByPatternFilter(Viewer viewer) {
		this.viewer = viewer;
	}
	
	/**
	 * Sets the filter according to a selection of SecurityPatterns. For each SecurityPattern all prerequisites are
	 * fetched and added to the filter.
	 *  
	 * @param patterns array of SecurityPatterns
	 */
	public void setFilter(Object[] securityPatterns) {
		showAll = securityPatterns.length == 0;
		
		prerequisitesToShow.clear();
		
		for (Object object: securityPatterns) {
			if (object instanceof SecurityPattern) {
				SecurityPattern pattern = (SecurityPattern) object;
				prerequisitesToShow.addAll(pattern.getMitigatedPrerequisites());
			}
		}
		
		viewer.refresh();
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (showAll) {
			return true;
		}
		
		return prerequisitesToShow.contains(element);
	}
}