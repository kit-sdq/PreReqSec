package de.fzi.decision.security.ui.analysis.window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
 * 
 * @author Robert Hochweiss
 *
 */
public class CCTabItem extends CTabItem{
	
	public CheckboxTableViewer viewer;
	public Group grpStructuralAnalysis;
	public Group grpVulnerableElements;
	public Group grpSecureElements;
	public Label lblNewLabel2;
	public Label lblNewLabel;
	public Label lblNewLabel3;
	
	public Map<SecurityPattern, List<Role>> resultStrucAnalysis;
	public Map<Entity, List<Attack>> vulnElements;
	public List<Entity> secureElements;
	
	private ListViewer viewerStrucAnalysis;
	private ListViewer viewer21;
	private ListViewer viewer22;
	private ListViewer viewer3;

	public CCTabItem (CTabFolder parent, EObject root) {
		super(parent, SWT.CLOSE);
		String sysName = "";
		if (root instanceof Entity) {
			sysName = ((Entity)root).getEntityName();
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

//			ScrolledComposite scrolledComposite = new ScrolledComposite(composite,
//					SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
//			scrolledComposite.setExpandHorizontal(true);
//			scrolledComposite.setExpandVertical(true);

//    		TableViewer viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
//    	            | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK);

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
			          Entity e = (Entity)element;
			          return e.getEntityName();
//			          switch(index)
//			          {
//			          //return appropriate attribute for column
//			          }
			      }
			      //... additional interface methods

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
//			table = viewer.getTable();
//			scrolledComposite.setContent(table);
//			scrolledComposite.setMinSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));

			Composite composite_1 = new Composite(composite, SWT.NONE);
			FillLayout fl_composite_1 = new FillLayout(SWT.HORIZONTAL);
			fl_composite_1.spacing = 20;
			composite_1.setLayout(fl_composite_1);
			
			grpStructuralAnalysis = new Group(composite_1, SWT.V_SCROLL |SWT.H_SCROLL);
			FillLayout fl_composite_11 = new FillLayout(SWT.VERTICAL);
			fl_composite_11.spacing = 10;
			grpStructuralAnalysis.setLayout(fl_composite_11);
			grpStructuralAnalysis.setText("Incorrectly Applied Security Patterns");
			
			resultStrucAnalysis = new HashMap<SecurityPattern, List<Role>>();
			vulnElements = new HashMap<Entity, List<Attack>>();
			secureElements = new ArrayList<Entity>();
			
			viewerStrucAnalysis = new ListViewer(grpStructuralAnalysis, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
			viewerStrucAnalysis.setContentProvider(ArrayContentProvider.getInstance());
//			{
//			      public Object[] getElements(Object inputElement) {
//			    	List<SecurityPattern> result;
//			    	if (inputElement == null) {
//			    		result = new ArrayList();
//			    	} else {
//			    		result =  (List<SecurityPattern>) inputElement;
//			    	}
//			        return result.toArray();
//			      }
//			});
			
			viewerStrucAnalysis.setInput(resultStrucAnalysis.keySet());
			
			
			viewerStrucAnalysis.setLabelProvider(new LabelProvider() {
			      public String getText(Object element) {
			        return ((SecurityPattern)element).getName();
			      }
			    });
			viewerStrucAnalysis.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
			      public void selectionChanged(SelectionChangedEvent event) {
//					viewerStrucAnalysis.getList().g
			        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
			        String name = ((SecurityPattern)selection.getFirstElement()).getName();
			        System.out.println(name);
			        String uRolles = "";
			        for (SecurityPattern sp : resultStrucAnalysis.keySet()) {
			        	if (sp.getName().equals(name)) {
			        		for(Role r : sp.getRoles()) {
			        			uRolles = uRolles + "\n" + "Role " + r.getName() + " is not applied!";
			        		}
			        }
			        	
			        }
			        
			        viewerStrucAnalysis.getList().setToolTipText(uRolles);
//			        System.out.println(selection.getFirstElement().toString());
//			        System.out.println(selection.getFirstElement().getClass());
//			        StringBuffer sb = new StringBuffer("Selection - ");
//			        sb.append("tatal " + selection.size() + " items selected: ");
//			        for(Iterator iterator = selection.iterator(); iterator.hasNext(); ) {
//			        	iterator.
//			          sb.append(iterator.next() + ", ");
//			        }
//			        System.out.println(sb);
			      }

				
			    });
		
//			lblNewLabel = new Label(grpStructuralAnalysis, SWT.NONE);
////			lblNewLabel.setBounds(0, 0, 56, 16);
//			lblNewLabel.setText("");
//			lblNewLabel.setLocation(20, 20);
//			lblNewLabel.pack();
			

			grpVulnerableElements = new Group(composite_1, SWT.V_SCROLL |SWT.H_SCROLL);
			FillLayout fl_composite_22 = new FillLayout(SWT.VERTICAL| SWT.V_SCROLL |SWT.H_SCROLL);
			fl_composite_11.spacing = 10;
			grpVulnerableElements.setLayout(fl_composite_22);
			grpVulnerableElements.setText("Vulnerable Elements");
//			grpVulnerableElements.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN));
//			lblNewLabel2 = new Label(grpVulnerableElements, SWT.NONE);
////			lblNewLabel.setBounds(0, 0, 56, 16);
//			lblNewLabel2.setText("");
//			lblNewLabel2.setLocation(20, 20);
//			lblNewLabel2.pack();
			
			
			viewer21 = new ListViewer(grpVulnerableElements, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
			viewer21.setContentProvider(ArrayContentProvider.getInstance());
			
			viewer21.setInput(vulnElements.keySet());
			
			
			viewer21.setLabelProvider(new LabelProvider() {
			      public String getText(Object element) {
			        return ((Entity)element).getEntityName();
			      }
			    });
			
			viewer22 = new ListViewer(grpVulnerableElements, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
			viewer22.setContentProvider(ArrayContentProvider.getInstance());
			
			viewer22.setInput(new ArrayList<Attack>());
			
			
			viewer22.setLabelProvider(new LabelProvider() {
			      public String getText(Object element) {
			        return ((Attack)element).getName();
			      }
			    });
			
			viewer21.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
			      public void selectionChanged(SelectionChangedEvent event) {
			        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
			        String name = ((Entity)selection.getFirstElement()).getEntityName();
			        
			        System.out.println(name);
			        List<Attack> attacks = null;
			        for (Entity e : vulnElements.keySet()) {
			        	if (e.getEntityName().equals(name)) {
			        		System.out.println("ima here");
			        		attacks = vulnElements.get(e);
//			        		return;
			        	}
			        }
			        System.out.println("before viewer22.refresh");
			        viewer22.setInput(attacks);
			        viewer22.refresh();  
			      }

				
			    });
			
			viewer22.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
			      public void selectionChanged(SelectionChangedEvent event) {
//					viewerStrucAnalysis.getList().g
			        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
			        String name = ((Attack)selection.getFirstElement()).getName();
			        System.out.println(name);
			        String ur = "";
			        for (Prerequisite p : ((Attack)selection.getFirstElement()).getPrerequisites()) {
			        	ur = ur + "\n" + "Prerequisite " + p.getName() + " is unmitigated!";
			        	
			        }
			        
			        viewer22.getList().setToolTipText(ur);
			      }

				
			    });
		
			
			
			grpSecureElements = new Group(composite_1, SWT.NONE);
			FillLayout fl_composite_33 = new FillLayout(SWT.VERTICAL);
			grpSecureElements.setLayout(fl_composite_33);
			grpSecureElements.setText("Secure Elements");
//			grpSecureElements.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
			
			viewer3 = new ListViewer(grpSecureElements, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
			viewer3.setContentProvider(ArrayContentProvider.getInstance());
			
			viewer3.setInput(secureElements);
			
			
			viewer3.setLabelProvider(new LabelProvider() {
			      public String getText(Object element) {
			        return ((Entity)element).getEntityName();
			      }
			    });
			

			
//			lblNewLabel3 = new Label(grpSecureElements, SWT.NONE);
////			lblNewLabel.setBounds(0, 0, 56, 16);
//			lblNewLabel3.setText("");
//			lblNewLabel3.setLocation(20, 20);
//			lblNewLabel3.pack();
		}
	}
	
	public void clearResults() {
		for(Control c : grpStructuralAnalysis.getChildren()) {
			c.dispose();
		}
		grpStructuralAnalysis.requestLayout();
		for(Control c : grpVulnerableElements.getChildren()) {
			c.dispose();
		}
		grpVulnerableElements.requestLayout();
		for(Control c : grpSecureElements.getChildren()) {
			c.dispose();
		}
		grpSecureElements.requestLayout();
	}
	
	public void refresh() {
		if (viewerStrucAnalysis != null) {
			viewerStrucAnalysis.setInput(resultStrucAnalysis.keySet());
			viewerStrucAnalysis.refresh();
		}
		viewer22.setInput(new ArrayList<Attack>());
		viewer22.refresh();
		viewer21.setInput(vulnElements.keySet());
		viewer21.refresh();
		viewer3.setInput(secureElements);
		viewer3.refresh();
	}
	
}
