package de.fzi.decision.security.ui.analysis.window;

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
		addStatusLine();
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
		fileMenu.add(new Action("&Open\tCtrl+O") {

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
		fileMenu.add(new Action("&Save\tCtrl+S") {

			public void run() {
				FileDialog fd = new FileDialog(getShell(), SWT.SAVE);
				fd.setText("Save");
				fd.setFilterPath(workspacePath);
				String[] filterExt = { "*.txt", "*.*" };
				fd.setFilterExtensions(filterExt);
				String selected = fd.open();
//				try {
//					PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(selected, true)));
//				} catch (IOException e) {
//					e.printStackTrace();
//					new FileWriter()
//				}
				try {
				    final Path path = Paths.get(selected);
				    Files.write(path, Arrays.asList("New line to append"), StandardCharsets.UTF_8,
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
				PreReqSecSecurityAnalyzer analyzer = new PreReqSecSecurityAnalyzer();
				List<SecurityPattern> resultList = new ArrayList<>();
				String output = "";
				for (Object o : ((CCTabItem)curTabItem).viewer.getCheckedElements()) {
					resultList.addAll(analyzer.analyze((EObject)o).getOne());
				}
				for(SecurityPattern sp : resultList ) {
					output = output + "\n" + sp.getName();
					
				}
				((CCTabItem)curTabItem).lblNewLabel.setText(output);
				((CCTabItem)curTabItem).lblNewLabel.getParent().layout();
				((CCTabItem)curTabItem).lblNewLabel2.setText("");
				((CCTabItem)curTabItem).lblNewLabel3.setText("");
//				val rootContainer = EcoreUtil.getRootContainer(selection)
//						val sysName = switch rootContainer {
//						Entity : rootContainer.entityName
//						default : rootContainer.toString
//						}
//						println("\n" + "Analyzing the system " + sysName + " for security vulnerabilities:")
//						val analyzer = new PreReqSecSecurityAnalyzer()
////						val analysisResultsPerObject = new ArrayList<Pair<List<SecurityPattern>, List<Attack>>> 
//						rootContainer.eContents.forEach [
//							// Analyze only AssemblyContexts, other EObjects are not relevant right now for the security analysis
//							if (it !== null && it instanceof AssemblyContext) {
//								prettyPrintAttacksPossible(analyzer.analyze(it), it)
//							}
//						]
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
				Map<Entity, ArrayList<Attack>> resultMap = new HashMap<>();
				List<SecurityPattern> resultList = new ArrayList<>();
				String output = "";
				for (Object o : ((CCTabItem)curTabItem).viewer.getCheckedElements()) {
					resultList.addAll(analyzer.analyze((EObject)o).getOne());
				}
				for(SecurityPattern sp : resultList ) {
					output = output + "\n" + sp.getName();
					
				}
				for (Object o : ((CCTabItem)curTabItem).viewer.getCheckedElements()) {
					resultMap.put((Entity)o, analyzer.analyze((EObject)o).getTwo());
					
				}
				String safe = "";
				String vulnerable = "";
				for (Map.Entry<Entity, ArrayList<Attack>> entry : resultMap.entrySet()) {
					if (entry.getValue().size() == 0) {
						safe = safe + "\n" + entry.getKey().getEntityName();
					} else {
						vulnerable = vulnerable + "\n" + entry.getKey().getEntityName() + ":\n";
						for (Attack a : entry.getValue()) {
							vulnerable = vulnerable + "Attack " + a.getName() + " possible\n";
						}
					}
				}
				((CCTabItem)curTabItem).lblNewLabel.setText(output);
				((CCTabItem)curTabItem).lblNewLabel.getParent().layout();
				((CCTabItem)curTabItem).lblNewLabel2.setText(vulnerable);
				((CCTabItem)curTabItem).lblNewLabel2.getParent().layout();
				((CCTabItem)curTabItem).lblNewLabel3.setText(safe);
				((CCTabItem)curTabItem).lblNewLabel3.getParent().layout();
				curTabItem.getParent().layout();
				
				System.out.println(output + "\n" + vulnerable + "\n" + safe);
				
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

//	/**
//	 * Launch the application.
//	 * @param args
//	 */
//	public static void main(String args[]) {
//		try {
//			Test window = new Test();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

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
