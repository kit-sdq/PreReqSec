package de.fzi.decision.security.ui.views.impl.parts;

import java.util.HashMap;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * A composite that consists of one TableViewer that shows the output. The output are either the SecurityPatterns
 * or the Attacks.
 */
public class OutputSection extends Composite {
	private TableViewer outputViewer;

	/**
	 * Creates the outputSection composite.
	 * 
	 * @param parent the parent composite
	 * @param attributeMap the AttributeMap for the Output Viewer
	 * @param editingDomain the EMF editingDomain needed for the databinding of the JFace Viewers
	 */
	public OutputSection(
		Composite parent,
		HashMap<EAttribute, String> attributeMap,
		AdapterFactoryEditingDomain editingDomain
	) {
		super(parent, SWT.NONE);
		
		FillLayout layout = new FillLayout(SWT.HORIZONTAL);
		this.setLayout(layout);
		
		outputViewer = new EMFTableViewer(this, attributeMap, editingDomain);
	}
	
	/**
	 * @return the output TableViewer
	 */
	public TableViewer getOutputViewer() {
		return outputViewer;
	}
}
