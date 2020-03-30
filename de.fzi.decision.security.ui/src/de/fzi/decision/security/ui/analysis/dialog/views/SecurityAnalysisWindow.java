package de.fzi.decision.security.ui.analysis.dialog.views;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import de.fzi.decision.security.ui.analysis.dialog.controller.ContAnalysisAction;
import de.fzi.decision.security.ui.analysis.dialog.controller.FileOpenAction;
import de.fzi.decision.security.ui.analysis.dialog.controller.FileSaveAction;
import de.fzi.decision.security.ui.analysis.dialog.controller.StructAnalysisAction;

/**
 * 
 * Main window of the PreReqSec-Dialog. Contains everything.
 * 
 * @author Robert Hochweiss
 *
 */
public class SecurityAnalysisWindow extends ApplicationWindow {

	protected final String workspacePath;
	private CTabItem curTabItem;
	protected Composite container;
	private CTabFolder tabFolder;

	/**
	 * Create the application window.
	 */
	public SecurityAnalysisWindow() {
		super(null);
		workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		addMenuBar();
	}

	/**
	 * Create contents of the application window.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		container.setLayout(new FillLayout(SWT.HORIZONTAL));

		tabFolder = new CTabFolder(container, SWT.BORDER);
		tabFolder.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		tabFolder.setSelectionBackground(
				Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
				curTabItem = tabFolder.getSelection();
			}
		});
		return container;
	}

	/**
	 * Create the menu manager.
	 * 
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuManager = new MenuManager("Menu");
		menuManager.add(createFileMenu());
		menuManager.add(createAnalysisMenu());
		return menuManager;
	}

	private MenuManager createFileMenu() {
		MenuManager fileMenu = new MenuManager("&File", "Id01");
		fileMenu.add(new FileOpenAction(this, workspacePath));
		fileMenu.add(new FileSaveAction(this, workspacePath));
		return fileMenu;
	}

	private MenuManager createAnalysisMenu() {
		MenuManager analysisMenu = new MenuManager("&Security Analysis", "Id02");
		analysisMenu.add(new StructAnalysisAction(this));
		analysisMenu.add(new ContAnalysisAction(this));
		return analysisMenu;
	}

	/**
	 * Configure the shell.
	 * 
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Security Analysis");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(601, 453);
	}

	/**
	 * 
	 * @return the currently selected system-tab/tab-item
	 */
	public CTabItem getCurTabItem() {
		return curTabItem;
	}

	/**
	 * 
	 * @param item the new current Tab to be set
	 */
	public void setCurTabItem(CTabItem item) {
		this.curTabItem = item;
	}

	/**
	 * 
	 * @return the TabFolder which holds all Tabs (1 Tab per system)
	 */
	public CTabFolder getTabFolder() {
		return tabFolder;
	}

}
