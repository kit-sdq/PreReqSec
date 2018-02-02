package de.fzi.decision.security.ui.controllers.viewerfilters;

import java.util.Collection;

import org.eclipse.jface.viewers.Viewer;

import security.securityPatterns.SecurityPattern;
import security.securityPrerequisites.Prerequisite;
import security.securityThreats.Attack;

public class PatternFilter extends CatalogViewerFilter<SecurityPattern> {
	
	public PatternFilter(Viewer viewer) {
		super(viewer);
	}

	@Override
	public void setFilterByPattern(Object[] elements) {
		initFilter(elements.length);
		
		for (Object obj : elements) {
			if (obj instanceof SecurityPattern) {
				SecurityPattern pattern = (SecurityPattern) obj;
				elementsToShow.add(pattern);
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
				elementsToShow.addAll(prerequisite.getSecurityPatterns());
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
				Collection<Prerequisite> prerequisites = attack.getPrerequisites();

				for (Prerequisite prerequisite : prerequisites) {
					elementsToShow.addAll(prerequisite.getSecurityPatterns());
				}
			}
		}
		viewer.refresh();
	}

	@Override
	protected boolean containsElementToShow(Object element) {
		if (showAll)
			return true;
		if (element instanceof SecurityPattern) {
			SecurityPattern pattern = (SecurityPattern) element;
			for (SecurityPattern tmp : elementsToShow) {
				if (pattern.getId().equals(tmp.getId())) {
					return true;
				}
			}
		}
		return false;
	}

}
