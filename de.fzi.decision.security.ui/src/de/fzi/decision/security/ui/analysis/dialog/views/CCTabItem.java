package de.fzi.decision.security.ui.analysis.dialog.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.entity.Entity;

import de.fzi.decision.security.ui.analysis.dialog.models.AnalysisResult;
import security.securityPatterns.Role;
import security.securityPatterns.SecurityPattern;
import security.securityPrerequisites.Prerequisite;
import security.securityThreats.Attack;

/**
 * A customized CTabItem. A TabItem per system which has to be analyzed.
 * 
 * @author Robert Hochweiss
 *
 */
public class CCTabItem extends CTabItem {

	private CheckboxTableViewer viewer;
	private Group grpStructuralAnalysis;
	private Group grpVulnerableElements;
	private Group grpSecureElements;

	private String sysName;
	private ListViewer viewerStrucAnalysis;
	private ListViewer viewerVulnElements;
	private ListViewer viewerAttacks;
	private ListViewer viewerSecureElements;
	private EObject root;
	private AnalysisResult analysisResult;

	public CCTabItem(CTabFolder superComp, EObject root) {
		super(superComp, SWT.CLOSE);
		sysName = "";
		if (root instanceof Entity) {
			sysName = ((Entity) root).getEntityName();
		} else {
			sysName = "New System";
		}
		this.root = root;
		this.setText(sysName);
		analysisResult = new AnalysisResult(this);
		createContent(superComp);
	}

	private void createTableViewer(Composite parent) {
		// The table viewer that shows the elements of the system which can be selected
		// for the analysis
		viewer = CheckboxTableViewer.newCheckList(parent,
				SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object input) {
				List<Object> results = new ArrayList<>();
				EObject eo = (EObject) input;
				for (EObject o : eo.eContents()) {
					if (o != null && o instanceof AssemblyContext) {
						results.add(o);
					}
				}
				return (results.toArray());
			}
		});
		viewer.setInput(root);
		TableViewerColumn colName = new TableViewerColumn(viewer, SWT.NONE);
		colName.getColumn().setWidth(600);
		colName.getColumn().setResizable(true);
		viewer.setLabelProvider(new ITableLabelProvider() {
			public String getColumnText(Object element, int index) {
				Entity e = (Entity) element;
				return e.getEntityName();
			}

			@Override
			public void addListener(ILabelProviderListener listener) {
			}

			@Override
			public void dispose() {
			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			@Override
			public void removeListener(ILabelProviderListener listener) {
			}

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
		});
	}

	private void createContent(CTabFolder parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		this.setControl(composite);
		FillLayout fl_composite = new FillLayout(SWT.VERTICAL);
		fl_composite.spacing = 30;
		fl_composite.marginWidth = 10;
		fl_composite.marginHeight = 10;
		composite.setLayout(fl_composite);
		Composite subComposite = new Composite(composite, SWT.NONE);
		subComposite.setLayout(new RowLayout());

		// Button to select all possible elements for comfort
		Button btAll = new Button(subComposite, SWT.CHECK);
		btAll.setText("All Elements");
		btAll.pack();
		btAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Button btn = (Button) event.getSource();
				if (btn.getSelection()) {
					viewer.setAllChecked(true);
				} else {
					viewer.setAllChecked(false);
				}
			}
		});
		// Element selection
		createTableViewer(subComposite);

		Composite composite_1 = new Composite(composite, SWT.NONE);
		FillLayout fl_composite_1 = new FillLayout(SWT.HORIZONTAL);
		fl_composite_1.spacing = 20;
		composite_1.setLayout(fl_composite_1);

		// Groups for showing the analysis results for the selected elements
		createGroupStrucAnalysis(composite_1);
		createGroupVulnElements(composite_1);
		createGroupSecureElements(composite_1);
	}

	private void createGroupStrucAnalysis(Composite parent) {
		grpStructuralAnalysis = new Group(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		FillLayout fl_composite_11 = new FillLayout(SWT.VERTICAL);
		fl_composite_11.spacing = 10;
		grpStructuralAnalysis.setLayout(fl_composite_11);
		grpStructuralAnalysis.setText("Incorrectly Applied Security Patterns");

		viewerStrucAnalysis = new ListViewer(grpStructuralAnalysis,
				SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewerStrucAnalysis.setContentProvider(ArrayContentProvider.getInstance());
		viewerStrucAnalysis.setInput(analysisResult.getIncorrectlyAppliedPatterns().keySet());

		viewerStrucAnalysis.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return ((SecurityPattern) element).getName();
			}
		});
		viewerStrucAnalysis.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					if (selection.getFirstElement() != null) {
						String name = ((SecurityPattern) selection.getFirstElement()).getName();
						String uRolles = "";
						for (SecurityPattern sp : analysisResult.getIncorrectlyAppliedPatterns().keySet()) {
							if (sp.getName().equals(name)) {
								for (Role r : sp.getRoles()) {
									uRolles = uRolles + "\n" + "Role " + r.getName() + " is not applied!";
								}
							}
						}
						viewerStrucAnalysis.getList().setToolTipText(uRolles.trim());
					}
				}
			}
		});
	}

	private void createGroupVulnElements(Composite parent) {
		grpVulnerableElements = new Group(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		FillLayout fl_composite_22 = new FillLayout(SWT.VERTICAL | SWT.V_SCROLL | SWT.H_SCROLL);
		fl_composite_22.spacing = 10;
		grpVulnerableElements.setLayout(fl_composite_22);
		grpVulnerableElements.setText("Vulnerable Elements");

		viewerVulnElements = new ListViewer(grpVulnerableElements,
				SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewerVulnElements.setContentProvider(ArrayContentProvider.getInstance());
		viewerVulnElements.setInput(analysisResult.getVulnElements().keySet());
		viewerVulnElements.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return ((Entity) element).getEntityName();
			}
		});

		viewerAttacks = new ListViewer(grpVulnerableElements, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewerAttacks.setContentProvider(ArrayContentProvider.getInstance());
		viewerAttacks.setInput(new ArrayList<Attack>());
		viewerAttacks.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return ((Attack) element).getName();
			}
		});

		// Update the viewer for attack only if a vulnerable element was selected (only
		// then possible)
		viewerVulnElements.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					if (selection.getFirstElement() != null) {
						String name = ((Entity) selection.getFirstElement()).getEntityName();
						List<Attack> attacks = null;
						for (Entity e : analysisResult.getVulnElements().keySet()) {
							if (e.getEntityName().equals(name)) {
								attacks = analysisResult.getVulnElements().get(e);
							}
						}
						viewerAttacks.setInput(attacks);
						viewerAttacks.refresh();
					}
				}
			}
		});

		viewerAttacks.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					String ur = "";
					if (selection.getFirstElement() != null) {
						for (Prerequisite p : ((Attack) selection.getFirstElement()).getPrerequisites()) {
							ur = ur + "\n" + "Prerequisite " + p.getName() + " is unmitigated!";
						}
						viewerAttacks.getList().setToolTipText(ur.trim());
					}
				}
			}
		});
	}

	private void createGroupSecureElements(Composite parent) {
		grpSecureElements = new Group(parent, SWT.NONE);
		FillLayout fl_composite_33 = new FillLayout(SWT.VERTICAL);
		grpSecureElements.setLayout(fl_composite_33);
		grpSecureElements.setText("Secure Elements");

		viewerSecureElements = new ListViewer(grpSecureElements, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewerSecureElements.setContentProvider(ArrayContentProvider.getInstance());
		viewerSecureElements.setInput(analysisResult.getSecureElements());
		viewerSecureElements.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return ((Entity) element).getEntityName();
			}
		});
		// The same tool tip with every secure element.
		viewerSecureElements.getList().setToolTipText("Every prerequisite is mitigated!");
	}

	/**
	 * Updates the result viewers of the TabItem after a security analysis.
	 */
	public void refresh() {
		viewerStrucAnalysis.setInput(analysisResult.getIncorrectlyAppliedPatterns().keySet());
		viewerStrucAnalysis.refresh();
		viewerAttacks.setInput(new ArrayList<Attack>());
		viewerAttacks.refresh();
		viewerVulnElements.setInput(analysisResult.getVulnElements().keySet());
		viewerVulnElements.refresh();
		viewerSecureElements.setInput(analysisResult.getSecureElements());
		viewerSecureElements.refresh();
	}

	/**
	 * 
	 * @return the name of the system which is to be analyzed
	 */
	public String getSysName() {
		return this.sysName;
	}

	/**
	 * 
	 * @return
	 */
	public AnalysisResult getAnalysisResult() {
		return this.analysisResult;
	}

	/**
	 * 
	 * @return the checked/selected elements of the system of in this TabItem
	 */
	public Object[] getCheckedElements() {
		return viewer.getCheckedElements();
	}
}
