package de.fzi.decision.security.ui.controllers.viewerfilters;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * A ViewerFilter that consists of a set of elements that shall be show in the interface.
 */
public abstract class CatalogViewerFilter<T> extends ViewerFilter {
	protected Set<T> elementsToShow = new HashSet<T>();
	protected boolean showAll = true;
	protected Viewer viewer;
	
	/**
	 * Keeps track of the viewer this filter is used for to automatically call viewer.refresh() when a new filter is
	 * set.
	 * 
	 * @param viewer the viewer this filter shall used for
	 */
	public CatalogViewerFilter(Viewer viewer) {
		this.viewer = viewer;
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (showAll) {
			return true;
		}
		
		return containsElementToShow(element);
	}
	
	protected abstract boolean containsElementToShow(Object element);
	
	protected void initFilter(int length) {
		showAll = length == 0;
		elementsToShow.clear();
	}
	
	/**
	 * Sets the filter according to a selection of SecurityPatterns. For each SecurityPattern all elements are
	 * fetched and added to the filter.
	 *  
	 * @param elements array of SecurityPattern
	 */
	public abstract void setFilterByPattern(Object[] elements);
	
	/**
	 * Sets the filter according to a selection of Prerequisites. For each Prerequisites all elements are
	 * fetched and added to the filter.
	 *  
	 * @param elements array of Prerequisites
	 */
	public abstract void setFilterByPrerequisite(Object[] elements);
	/**
	 * Sets the filter according to a selection of Attacks. For each Attacks all elements are
	 * fetched and added to the filter.
	 *  
	 * @param elements array of Attacks
	 */
	public abstract void setFilterByAttacks(Object[] elements);

}