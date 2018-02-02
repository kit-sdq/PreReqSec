package de.fzi.decision.security.ui.controllers.viewerfilters;

import org.eclipse.jface.viewers.Viewer;

import security.securityPatterns.SecurityPattern;
import security.securityPrerequisites.Prerequisite;
import security.securityThreats.Attack;

/**
 * A ViewerFilter that consists of a set of prerequisites that shall be show in the interface.
 */
public class PrerequisiteFilter extends CatalogViewerFilter {
	
	public PrerequisiteFilter(Viewer viewer) {
		super(viewer);
	}

	@Override
	public void setFilterByPattern(Object[] elements) {
		initFilter(elements.length);
		
		for (Object obj : elements) {
			if (obj instanceof SecurityPattern) {
				SecurityPattern pattern = (SecurityPattern) obj;
				elementsToShow.addAll(pattern.getMitigatedPrerequisites());
			}
		}
		viewer.refresh();
	}

	@Override
	public void setFilterByPrerequisite(Object[] elements) {
		initFilter(elements.length);
		for (Object obj : elements) {
			if (obj instanceof Prerequisite) {
				Prerequisite prerequisite = (Prerequisite) obj;
				elementsToShow.add(prerequisite);
			}
		}
		viewer.refresh();
	}

	@Override
	public void setFilterByAttacks(Object[] elements) {
		initFilter(elements.length);
		
		for (Object obj : elements) {
			if (obj instanceof Attack) {
				Attack attack = (Attack) obj;
				elementsToShow.addAll(attack.getPrerequisites());
			}
		}
		viewer.refresh();
	}
}