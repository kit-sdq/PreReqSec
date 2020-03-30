package de.fzi.decision.security.ui.analysis.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.entity.Entity;

import security.securityPatterns.Role;
import security.securityPatterns.SecurityPattern;
import security.securityPrerequisites.Prerequisite;
import security.securityThreats.Attack;

/**
 * A customized CTabItem.
 * 
 * @author Robert Hochweiss
 *
 */
public class CCTabItem extends CTabItem {

	public CheckboxTableViewer viewer;
	public Group grpStructuralAnalysis;
	public Group grpVulnerableElements;
	public Group grpSecureElements;

	public Map<SecurityPattern, List<Role>> incorrectlyAppliedPatterns;
	public Map<Entity, List<Attack>> vulnElements;
	public List<Entity> secureElements;
	private String sysName;
	private ListViewer viewerStrucAnalysis;
	private ListViewer viewer21;
	private ListViewer viewer22;
	private ListViewer viewer3;

	public CCTabItem(CTabFolder parent, EObject root) {
		super(parent, SWT.CLOSE);
		sysName = "";
		if (root instanceof Entity) {
			sysName = ((Entity) root).getEntityName();
		} else {
			sysName = "New System";
		}
		this.setText(sysName);
		{
			Composite composite = new Composite(parent, SWT.NONE);
			this.setControl(composite);
			FillLayout fl_composite = new FillLayout(SWT.VERTICAL);
			fl_composite.spacing = 30;
			fl_composite.marginWidth = 10;
			fl_composite.marginHeight = 10;
			composite.setLayout(fl_composite);
			Composite subComposite = new Composite(composite, SWT.NONE);
			subComposite.setLayout(new RowLayout());
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

			viewer = CheckboxTableViewer.newCheckList(subComposite,
					SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
//			viewer.setContentProvider(ArrayContentProvider.getInstance());
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
//			colName.getColumn().setText("Firstname");
//			colName.setLabelProvider(new ColumnLabelProvider() {
//			    @Override
//			    public String getText(Object element) {
//			        Entity e = (Entity) element;
//			        return e.getEntityName();
//			    }
//			});
			viewer.setLabelProvider(new ITableLabelProvider() {
				public String getColumnText(Object element, int index) {
					Entity e = (Entity) element;
					return e.getEntityName();
//			          switch(index)
//			          {
//			          //return appropriate attribute for column
//			          }
				}
				// ... additional interface methods

				@Override
				public void addListener(ILabelProviderListener listener) {
					// TODO Auto-generated method stub

				}

				@Override
				public void dispose() {
					// TODO Auto-generated method stub

				}

				@Override
				public boolean isLabelProperty(Object element, String property) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void removeListener(ILabelProviderListener listener) {
					// TODO Auto-generated method stub

				}

				@Override
				public Image getColumnImage(Object element, int columnIndex) {
					// TODO Auto-generated method stub
					return null;
				}
			});

			Composite composite_1 = new Composite(composite, SWT.NONE);
			FillLayout fl_composite_1 = new FillLayout(SWT.HORIZONTAL);
			fl_composite_1.spacing = 20;
			composite_1.setLayout(fl_composite_1);

			grpStructuralAnalysis = new Group(composite_1, SWT.V_SCROLL | SWT.H_SCROLL);
			FillLayout fl_composite_11 = new FillLayout(SWT.VERTICAL);
			fl_composite_11.spacing = 10;
			grpStructuralAnalysis.setLayout(fl_composite_11);
			grpStructuralAnalysis.setText("Incorrectly Applied Security Patterns");

			incorrectlyAppliedPatterns = new HashMap<SecurityPattern, List<Role>>();
			vulnElements = new HashMap<Entity, List<Attack>>();
			secureElements = new ArrayList<Entity>();

			viewerStrucAnalysis = new ListViewer(grpStructuralAnalysis,
					SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
			viewerStrucAnalysis.setContentProvider(ArrayContentProvider.getInstance());
			viewerStrucAnalysis.setInput(incorrectlyAppliedPatterns.keySet());

			viewerStrucAnalysis.setLabelProvider(new LabelProvider() {
				public String getText(Object element) {
					return ((SecurityPattern) element).getName();
				}
			});
			viewerStrucAnalysis.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					String name = ((SecurityPattern) selection.getFirstElement()).getName();
					System.out.println(name);
					String uRolles = "";
					for (SecurityPattern sp : incorrectlyAppliedPatterns.keySet()) {
						if (sp.getName().equals(name)) {
							for (Role r : sp.getRoles()) {
								uRolles = uRolles + "\n" + "Role " + r.getName() + " is not applied!";
							}
						}

					}

					viewerStrucAnalysis.getList().setToolTipText(uRolles);
				}

			});

			grpVulnerableElements = new Group(composite_1, SWT.V_SCROLL | SWT.H_SCROLL);
			FillLayout fl_composite_22 = new FillLayout(SWT.VERTICAL | SWT.V_SCROLL | SWT.H_SCROLL);
			fl_composite_11.spacing = 10;
			grpVulnerableElements.setLayout(fl_composite_22);
			grpVulnerableElements.setText("Vulnerable Elements");

			viewer21 = new ListViewer(grpVulnerableElements, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
			viewer21.setContentProvider(ArrayContentProvider.getInstance());

			viewer21.setInput(vulnElements.keySet());

			viewer21.setLabelProvider(new LabelProvider() {
				public String getText(Object element) {
					return ((Entity) element).getEntityName();
				}
			});

			viewer22 = new ListViewer(grpVulnerableElements, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
			viewer22.setContentProvider(ArrayContentProvider.getInstance());

			viewer22.setInput(new ArrayList<Attack>());

			viewer22.setLabelProvider(new LabelProvider() {
				public String getText(Object element) {
					return ((Attack) element).getName();
				}
			});

			viewer21.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					String name = ((Entity) selection.getFirstElement()).getEntityName();
					System.out.println(name);
					List<Attack> attacks = null;
					for (Entity e : vulnElements.keySet()) {
						if (e.getEntityName().equals(name)) {
							attacks = vulnElements.get(e);
						}
					}
					viewer22.setInput(attacks);
					viewer22.refresh();
				}
			});

			viewer22.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					String ur = "";
					for (Prerequisite p : ((Attack) selection.getFirstElement()).getPrerequisites()) {
						ur = ur + "\n" + "Prerequisite " + p.getName() + " is unmitigated!";
					}
					viewer22.getList().setToolTipText(ur.trim());
				}
			});

			grpSecureElements = new Group(composite_1, SWT.NONE);
			FillLayout fl_composite_33 = new FillLayout(SWT.VERTICAL);
			grpSecureElements.setLayout(fl_composite_33);
			grpSecureElements.setText("Secure Elements");

			viewer3 = new ListViewer(grpSecureElements, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
			viewer3.setContentProvider(ArrayContentProvider.getInstance());

			viewer3.setInput(secureElements);
			viewer3.setLabelProvider(new LabelProvider() {
				public String getText(Object element) {
					return ((Entity) element).getEntityName();
				}
			});
		}
	}

	public void refresh() {
		if (viewerStrucAnalysis != null) {
			viewerStrucAnalysis.setInput(incorrectlyAppliedPatterns.keySet());
			viewerStrucAnalysis.refresh();
		}
		viewer22.setInput(new ArrayList<Attack>());
		viewer22.refresh();
		viewer21.setInput(vulnElements.keySet());
		viewer21.refresh();
		viewer3.setInput(secureElements);
		viewer3.refresh();
	}

	/**
	 * Saves the current results of the security analysis for the current system.
	 * 
	 * @return the results of the analysis
	 */
	public String saveResults() {
		String results = "";
		results = results + "Results of the security analysis for the current system: " + sysName + "\n\n";
		results = results + "Results of the structural analysis: \n\n";
		if (incorrectlyAppliedPatterns.isEmpty()) {
			results = results + "\t"
					+ "All security patterns are correctly applied or there is no security pattern to apply!\n\n";
		} else {
			// Print the incorrectly applied security pattern and per pattern print the
			// specific incorrectly applied roles
			for (Entry<SecurityPattern, List<Role>> e : incorrectlyAppliedPatterns.entrySet()) {
				results = results + "\t" + "Security pattern: " + e.getKey().getName() + " is not correctly applied!\n";
				for (Role r : e.getValue()) {
					results = results + "\t\t" + "Role: " + r.getName() + " is not correctly applied!\n";
				}
				results = results + "\n";
			}
		}
		results = results + "Results of the contextuell analysis: \n\n";
		if (vulnElements.isEmpty() && secureElements.isEmpty()) {
			results = results + "\t" + "Either no element or the contextuell analysis itself is not selected.";
		}
		// Print the vulnerable elements, per element the possible attack an per attack
		// the unmitigated prerequisites
		for (Entry<Entity, List<Attack>> e : vulnElements.entrySet()) {
			results = results + "\t" + "The element: " + e.getKey().getEntityName() + " is vulnerable!\n";
			for (Attack a : e.getValue()) {
				results = results + "\t\t" + "The attack: " + a.getName() + " is possible!\n";
				for (Prerequisite p : a.getPrerequisites()) {
					results = results + "\t\t\t" + "The prerequisite: " + p.getName() + " is unmitigated!\n";
				}
			}
			results = results + "\n";
		}
		for (Entity e : secureElements) {
			results = results + "\t" + "The element: " + e.getEntityName() + " is secure!\n";
		}
		return results.trim();
	}
}
