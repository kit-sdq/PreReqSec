package de.fzi.decision.security.ui.analysis.dialog;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.system.util.SystemResourceFactoryImpl;

import analysis.PreReqSecSecurityAnalyzer;
import security.securityPatterns.Role;
import security.securityPatterns.SecurityPattern;
import security.securityThreats.Attack;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;

import swing2swt.layout.BorderLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Label;

/**
 * 
 * @author Robert Hochweiss
 *
 */
public class SecurityAnalysisWindow extends ApplicationWindow {

	protected String workspacePath;
	private CTabItem curTabItem;
	private Table table;
	protected Composite container;
	protected CTabFolder tabFolder;
	protected String result;

	/**
	 * Create the application window.
	 */
	public SecurityAnalysisWindow() {
		super(null);
		workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		createActions();
//		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
//		addStatusLine();
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
//		{
//			Group group = new Group(container, SWT.NONE);
//			
//			Label lblNewLabel = new Label(group, SWT.NONE);
//			lblNewLabel.setBounds(0, 0, 56, 16);
//			lblNewLabel.setText("New Label");
//		}
		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
				curTabItem = tabFolder.getSelection(); // This should be your TabItem/CTabItem
			}
		});
		return container;
	}
	
	protected  CTabItem createFileTab(Composite parent, EObject root) {
		curTabItem = new CCTabItem(tabFolder, root);
		return curTabItem;

	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions

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

	protected MenuManager createFileMenu() {
		MenuManager fileMenu = new MenuManager("&File", "Id01");
		fileMenu.add(new Action("&Open") {

			public void run() {
				FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
				fd.setText("Open");
				fd.setFilterPath(workspacePath);
				String[] filterExt = { "*.system" };
				fd.setFilterExtensions(filterExt);
				String selected = fd.open();
				System.out.println(selected);
				// Test ResourceSets here
				ResourceSet resourceSet = new ResourceSetImpl();
				resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
						new EcoreResourceFactoryImpl());
				resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("system",
						new SystemResourceFactoryImpl());
//				System.out.println(URI.createFileURI(selected));
//	              Resource pcmMetaModel= resourceSet.getResource(URI.createFileURI(
//	            		  "Z:/windows/profile.V6/Desktop/ise-practical/project-repo/de.fzi.decision.security.ui/TestModels/pcm.ecore"), true);
//	              EPackage pcmEPackage = (EPackage) pcmMetaModel.getContents().get(0);
//	              resourceSet.getPackageRegistry().put("http://palladiosimulator.org/PalladioComponentModel/5.2", pcmEPackage);
				Resource systemModel = resourceSet.getResource(URI.createFileURI(selected), true);
				EObject root = systemModel.getContents().get(0);
				createFileTab(tabFolder, root);
				
			}
		});
		fileMenu.add(new Action("&Save") {

			public void run() {
				FileDialog fd = new FileDialog(getShell(), SWT.SAVE);
				fd.setText("Save");
				fd.setFilterPath(workspacePath);
				String[] filterExt = { "*.txt", "*.*" };
				fd.setFilterExtensions(filterExt);
				String selected = fd.open();
				try {
				    final Path path = Paths.get(selected);
				    String[] results = ((CCTabItem)curTabItem).saveResults().split("\\r?\\n", -1);
				    Files.write(path, Arrays.asList(results), StandardCharsets.UTF_8,
				        Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
				} catch (final IOException e) {
					e.printStackTrace();
				}

			}

		});
		return fileMenu;
	}
	
	protected MenuManager createAnalysisMenu() {
		MenuManager analysisMenu = new MenuManager("&Security Analysis", "Id02");
		analysisMenu.add(new Action("Structural Analysis") {
			public void run() {
//				((CCTabItem)curTabItem).clearResults();
				PreReqSecSecurityAnalyzer analyzer = new PreReqSecSecurityAnalyzer();
				Map<SecurityPattern, List<Role>> result1 = new HashMap();
				String output = "";
				for (Object o : ((CCTabItem)curTabItem).viewer.getCheckedElements()) {
					System.out.println(((Entity)o).getEntityName());
					result1.putAll(analyzer.analyzeExtended((EObject)o).getOne());
//					spList.addAll(analyzer.analyze((EObject)o).getOne());
				}
				((CCTabItem)curTabItem).incorrectlyAppliedPatterns = result1;
				((CCTabItem)curTabItem).refresh();
				
//				for(SecurityPattern sp : spList ) {
//					output = output + "\n" + sp.getName();
//					
//				}
				System.out.println("All results cleared!");
//				((CCTabItem)curTabItem).lblNewLabel.setText(output);
//				((CCTabItem)curTabItem).lblNewLabel.getParent().layout();
//				((CCTabItem)curTabItem).lblNewLabel2.setText("");
//				((CCTabItem)curTabItem).lblNewLabel3.setText("");
				
			}
		});
		analysisMenu.add(new Action("Contextual Analysis") {
			public void run() {
				System.out.println("Hallo contextuall");
				for (Object o : ((CCTabItem)curTabItem).viewer.getCheckedElements()) {
					System.out.println(((Entity)o).getEntityName());
				}
				System.out.println();
				PreReqSecSecurityAnalyzer analyzer = new PreReqSecSecurityAnalyzer();
				
				List<Label> spLabels = new ArrayList();
				List<Label> vulLabels = new ArrayList();
				String secMsg = "No unmitigated Prerequesite";
//				Label secLabel = new Label(grpVulnerableElements, SWT.NONE);
////				lblNewLabel.setBounds(0, 0, 56, 16);
//				secLabel.setText("No unmitigated");
//				lblNewLabel2.setLocation(20, 20);
//				lblNewLabel2.pack();
				
				Map<SecurityPattern, List<Role>> result1 = new HashMap();
				for (Object o : ((CCTabItem)curTabItem).viewer.getCheckedElements()) {
					System.out.println(((Entity)o).getEntityName());
					result1.putAll(analyzer.analyzeExtended((EObject)o).getOne());
				}
				((CCTabItem)curTabItem).incorrectlyAppliedPatterns = result1;
				
				Map<Entity, List<Attack>> attackMap = new HashMap<>();
				
				

				for (Object o : ((CCTabItem)curTabItem).viewer.getCheckedElements()) {
					attackMap.put((Entity)o, analyzer.analyzeExtended((EObject)o).getTwo());
					
				}
				
				List<Entity> safeE = new ArrayList();
				String safe = "";
				String vulnerable = "";
				for (Entry<Entity, List<Attack>> entry : attackMap.entrySet()) {
					if (entry.getValue().size() == 0) {
						safe = safe + "\n" + entry.getKey().getEntityName();
						safeE.add(entry.getKey());
					} 
//					else {
//						vulnerable = vulnerable + "\n" + entry.getKey().getEntityName() + ":\n";
//						for (Attack a : entry.getValue()) {
//							vulnerable = vulnerable + "Attack " + a.getName() + " possible\n";
//						}
//					}
				}
				for (Entity e : safeE) {
					attackMap.remove(e);
				}
				((CCTabItem)curTabItem).vulnElements = attackMap;
				((CCTabItem)curTabItem).secureElements = safeE;
				
				((CCTabItem)curTabItem).refresh();

				curTabItem.getParent().requestLayout();				
			}
		});
		return analysisMenu;
	}

//	/**
//	 * Create the toolbar manager.
//	 * @return the toolbar manager
//	 */
//	@Override
//	protected ToolBarManager createToolBarManager(int style) {
//		ToolBarManager toolBarManager = new ToolBarManager(style);
//		return toolBarManager;
//	}

	/**
	 * Create the status line manager.
	 * 
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
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

	public void dispose() {
		Display.getCurrent().dispose();
	}
}
