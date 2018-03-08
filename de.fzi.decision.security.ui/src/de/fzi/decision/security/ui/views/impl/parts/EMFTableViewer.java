package de.fzi.decision.security.ui.views.impl.parts;


import java.util.List;

import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.fzi.decision.security.ui.main.DelegateSelectionProvider;
import de.fzi.decision.security.ui.models.ModelModificationListener;
import security.NamedDescribedEntity;

/**
 * A generic EMF TableViewer that builds the table according to an attribute map that is input.
 * Used for faster development, but restricts customization of a single table. Should be phased
 * out for an explicit construction of the TableViewers.
 */
public class EMFTableViewer extends TableViewer {
	
	private DelegateSelectionProvider selectionProvider;
	private ModelModificationListener modModListener;
	
	/**
	 * Creates a TableViewer using the attribute map. The keys of attribute map are the EMF attributes to observe
	 * and the values of the map are the names for the TableViewer columns.
	 *  
	 * @param parent the parent composite
	 * @param attributeList the AttributeMap for the TableViewer
	 * @param editingDomain the EMF editingDomain needed for the databinding of the JFace Viewers
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public EMFTableViewer(
		Composite parent,
		List<Pair<EAttribute, String>> attributeList, 
		AdapterFactoryEditingDomain editingDomain,
		DelegateSelectionProvider selectionProvider,
		ModelModificationListener modModListener
	) {
		super(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);

		this.getTable().setHeaderVisible(true);
		
		ObservableListContentProvider cp = new ObservableListContentProvider();
			
		for (Pair<EAttribute, String> pair : attributeList) {
			TableViewerColumn column = new TableViewerColumn(this, SWT.LEFT);
			IObservableMap map = EMFEditProperties.value(editingDomain, pair.getElement1()).observeDetail(cp.getKnownElements());
			column.setLabelProvider(new ObservableMapCellLabelProvider(map));
			column.getColumn().setText(pair.getElement2());
			column.getColumn().setWidth(200);
		}
				
		this.setContentProvider(cp);
		this.selectionProvider = selectionProvider;
		this.modModListener = modModListener;
		setContextMenu();
	}
	
	private void setContextMenu() {
		MenuManager menuManager = new MenuManager();
		Table table = this.getTable();
		Menu contextMenu = menuManager.createContextMenu(table);
		table.setMenu(contextMenu);
		contextMenu.addMenuListener(createMenuAdapter(contextMenu));
	}
	
	private MenuAdapter createMenuAdapter(Menu contextMenu) {
		return new MenuAdapter()
	    {
			@Override
	        public void menuShown(MenuEvent e)
	        {
				MenuItem[] items = contextMenu.getItems();
	            for (int i = 0; i < items.length; i++)
	            {
	                items[i].dispose();
	            }
	            createAddItem(contextMenu);
				if (doGetSelectionIndices().length == 1) {
					createPropertiesViewItem(contextMenu);
					createDeleteItem(contextMenu);
				}				
	        }
	    };
	}
	
	private void createPropertiesViewItem(Menu contextMenu) {
		MenuItem item = new MenuItem(contextMenu, SWT.NONE);
		item.setText("Open in Properties View");
		item.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		setSelectionProviderDelegate();
        		openPropertiesView();
        	}
		});
	}
	
	private void setSelectionProviderDelegate() {
		selectionProvider.setSelectionProviderDelegate(this);				
	}
	
	private void openPropertiesView() {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IPageLayout.ID_PROP_SHEET);
		} catch (PartInitException e) {
			e.printStackTrace();
		} 
	}
	
	private void createAddItem(Menu contextMenu) {
		MenuItem item = new MenuItem(contextMenu, SWT.NONE);
		item.setText("Add New");
		item.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		modModListener.addEntity();
        	}
		});
	}
	
	private void createDeleteItem(Menu contextMenu) {
		MenuItem item = new MenuItem(contextMenu, SWT.NONE);
		item.setText("Delete");
		item.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		String id = ((NamedDescribedEntity)getTable().getSelection()[0].getData()).getId();
        		modModListener.deleteEntity(id);
        	}
		});
	}

}
