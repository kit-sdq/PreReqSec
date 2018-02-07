package de.fzi.decision.security.ui.controllers.viewerfilters;

import java.util.Collection;

import org.eclipse.jface.viewers.Viewer;

import security.securityPatterns.SecurityPattern;
import security.securityPrerequisites.Prerequisite;
import security.securityThreats.Attack;

/**
 * A ViewerFilter that consists of a set of attacks that shall be show in the interface.
 */
public class AttackFilter extends CatalogViewerFilter<Attack> {
	
	public AttackFilter(Viewer viewer) {
		super(viewer);
	}
	
	/**
	 * Sets the filter according to a selection of SecurityPatterns. For each SecurityPattern all attacks are
	 * fetched and added to the filter.
	 * 
	 * @param patterns array of SecurityPatterns
	 */
	@Override
	public void setFilterByPattern(Object[] elements) {
		initFilter(elements.length);
		
		for (Object obj : elements) {
			if (obj instanceof SecurityPattern) {
				SecurityPattern pattern = (SecurityPattern) obj;
				Collection<Prerequisite> prerequisites = pattern.getMitigatedPrerequisites();

				for (Prerequisite prerequisite : prerequisites) {
					elementsToShow.addAll(prerequisite.getAttacks());
				}
			}
		}
		viewer.refresh();
	}

	/**
	 * Sets the filter according to a selection of Prerequisites. For each Prerequisites all attacks are
	 * fetched and added to the filter.
	 * 
	 * @param patterns array of Prerequisites
	 */
	@Override
	public void setFilterByPrerequisite(Object[] elements) {
		initFilter(elements.length);
		
		for (Object obj : elements) {
			if (obj instanceof Prerequisite) {
				Prerequisite prerequisite = (Prerequisite) obj;
				elementsToShow.addAll(prerequisite.getAttacks());
			}
		}
		viewer.refresh();
	}

	/**
	 * Sets the filter according to a selection of Attacks. All given attacks are
	 * added to the filter.
	 * 
	 * @param patterns array of Attacks
	 */
	@Override
	public void setFilterByAttacks(Object[] elements) {
		initFilter(elements.length);
		
		for (Object obj : elements) {
			if (obj instanceof Attack) {
				Attack attack = (Attack) obj;
				elementsToShow.add(attack);
			}
		}
		viewer.refresh();
	}
	
	@Override
	protected boolean containsElementToShow(Object element) {
		if (showAll)
			return true;
		if (element instanceof Attack) {
			Attack attack = (Attack) element;
			for (Attack tmp : elementsToShow) {
				if (attack.getId().equals(tmp.getId())) {
					return true;
				}
			}
		}
		return false;
	}

}
