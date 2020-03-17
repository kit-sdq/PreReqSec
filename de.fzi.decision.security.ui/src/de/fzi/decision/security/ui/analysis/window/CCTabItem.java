package de.fzi.decision.security.ui.analysis.window;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.entity.Entity;

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

//			ScrolledComposite scrolledComposite = new ScrolledComposite(composite,
//					SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
//			scrolledComposite.setExpandHorizontal(true);
//			scrolledComposite.setExpandVertical(true);

//    		TableViewer viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
//    	            | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK);

			viewer = CheckboxTableViewer.newCheckList(composite,
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
			fl_composite_1.spacing = 10;
			composite_1.setLayout(fl_composite_1);
			
			grpStructuralAnalysis = new Group(composite_1, SWT.V_SCROLL |SWT.H_SCROLL);
			FillLayout fl_composite_11 = new FillLayout(SWT.VERTICAL);
			fl_composite_11.spacing = 10;
			grpStructuralAnalysis.setLayout(fl_composite_11);
			grpStructuralAnalysis.setText("Incorrectly Applied Patterns");
		
			lblNewLabel = new Label(grpStructuralAnalysis, SWT.NONE);
//			lblNewLabel.setBounds(0, 0, 56, 16);
			lblNewLabel.setText("");
			lblNewLabel.setLocation(20, 20);
			lblNewLabel.pack();
			

			grpVulnerableElements = new Group(composite_1, SWT.V_SCROLL |SWT.H_SCROLL);
			FillLayout fl_composite_22 = new FillLayout(SWT.VERTICAL);
			fl_composite_22.spacing = 10;
			grpVulnerableElements.setLayout(fl_composite_22);
			grpVulnerableElements.setText("Vulnerable Elements");
			lblNewLabel2 = new Label(grpVulnerableElements, SWT.NONE);
//			lblNewLabel.setBounds(0, 0, 56, 16);
			lblNewLabel2.setText("");
			lblNewLabel2.setLocation(20, 20);
			lblNewLabel2.pack();

			grpSecureElements = new Group(composite_1, SWT.NONE);
			FillLayout fl_composite_33 = new FillLayout(SWT.VERTICAL);
			fl_composite_33.spacing = 10;
			grpSecureElements.setLayout(fl_composite_33);
			grpSecureElements.setText("Secure Elements");
			lblNewLabel3 = new Label(grpSecureElements, SWT.NONE);
//			lblNewLabel.setBounds(0, 0, 56, 16);
			lblNewLabel3.setText("");
			lblNewLabel3.setLocation(20, 20);
			lblNewLabel3.pack();
		}
	}
	
}
