package de.fzi.decision.security.ui.views.impl.parts;

import java.util.HashMap;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;

import de.fzi.decision.security.ui.main.DelegateSelectionProvider;
import de.fzi.decision.security.ui.models.ISecurityContainer;
import de.fzi.decision.security.ui.models.impl.PrerequisiteModificationListener;
import de.fzi.decision.security.ui.models.impl.SecurityPatternModificationListener;

/**
 * A composite that consists of two TableViewers that show either
 * pattern and prerequisites or attacks and prerequisites.
 */
public class InputSection extends SashForm {
	private TableViewer leftViewer;
	private TableViewer rightViewer;

	/**
	 * Creates the inputSection composite.
	 * 
	 * @param parent the parent composite
	 * @param leftAttributeMap the AttributeMap for the left Viewer 
	 * @param rightAttributeMap the AttributeMap for the right Viewer
	 * @param editingDomain the EMF editingDomain needed for the databinding of the JFace Viewers
	 */
	public InputSection(
		Composite parent, 
		HashMap<EAttribute, String> leftAttributeMap, 
		HashMap<EAttribute, String> rightAttributeMap,
		AdapterFactoryEditingDomain editingDomain,
		DelegateSelectionProvider selectionProvider,
		ISecurityContainer model
	) {
		super(parent, SWT.HORIZONTAL);
		
		leftViewer = new EMFTableViewer(this, leftAttributeMap, editingDomain, selectionProvider, 
				new SecurityPatternModificationListener(model));
		rightViewer = new EMFTableViewer(this, rightAttributeMap, editingDomain, selectionProvider, 
				new PrerequisiteModificationListener(model));
	}
	
	
	/**
	 * @return the left TableViewer
	 */
	public TableViewer getLeftViewer() {
		return leftViewer;
	}
	
	/**
	 * @return the right TableViewer
	 */
	public TableViewer getRightViewer() {
		return rightViewer;
	}
}
