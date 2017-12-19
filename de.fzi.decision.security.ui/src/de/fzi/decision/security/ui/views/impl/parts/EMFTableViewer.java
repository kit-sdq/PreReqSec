package de.fzi.decision.security.ui.views.impl.parts;

import java.util.HashMap;

import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * A generic EMF TableViewer that builds the table according to an attribute map that is input.
 * Used for faster development, but restricts customization of a single table. Should be phased
 * out for an explicit construction of the TableViewers.
 */
public class EMFTableViewer extends TableViewer {
	
	/**
	 * Creates a TableViewer using the attribute map. The keys of attribute map are the EMF attributes to observe
	 * and the values of the map are the names for the TableViewer columns.
	 *  
	 * @param parent the parent composite
	 * @param attributeMap the AttributeMap for the TableViewer
	 * @param editingDomain the EMF editingDomain needed for the databinding of the JFace Viewers
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public EMFTableViewer(
		Composite parent, 
		HashMap<EAttribute, String> attributeMap, 
		AdapterFactoryEditingDomain editingDomain
	) {
		super(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);

		this.getTable().setHeaderVisible(true);
		
		ObservableListContentProvider cp = new ObservableListContentProvider();
			
		for (EAttribute attribute : attributeMap.keySet()) {
			TableViewerColumn column = new TableViewerColumn(this, SWT.LEFT);
			IObservableMap map = EMFEditProperties.value(editingDomain, attribute).observeDetail(cp.getKnownElements());
			column.setLabelProvider(new ObservableMapCellLabelProvider(map));
			column.getColumn().setText(attributeMap.get(attribute));
			column.getColumn().setWidth(150);
		}
				
		this.setContentProvider(cp);
	}

}
