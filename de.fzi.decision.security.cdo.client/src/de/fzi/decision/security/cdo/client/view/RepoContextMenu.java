package de.fzi.decision.security.cdo.client.view;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;

public class RepoContextMenu {
	
	private IRepoContextMenuClient client;
	private Table table;
	
	public RepoContextMenu(IRepoContextMenuClient client, Table table) {
		this.client = client;
		this.table = table;
		setContextMenu();
	}
	
	private void setContextMenu() {
		MenuManager menuManager = new MenuManager();
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
				if (table.getSelectionIndices().length == 1) {
					createOpenItem(contextMenu);
					createDeleteItem(contextMenu);
				}				
	        }
	    };
	}
	
	private void createDeleteItem(Menu contextMenu) {
		MenuItem item = new MenuItem(contextMenu, SWT.NONE);
		item.setText("Delete");
		item.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		client.deleteContainer(table.getSelectionIndex());
        	}
		});
	}
	
	private void createOpenItem(Menu contextMenu) {
		MenuItem item = new MenuItem(contextMenu, SWT.NONE);
		item.setText("Open");
		item.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		client.openContainer(table.getSelectionIndex());
        	}
		});
	}

}
