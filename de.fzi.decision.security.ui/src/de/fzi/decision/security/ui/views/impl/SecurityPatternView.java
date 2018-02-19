package de.fzi.decision.security.ui.views.impl;

import java.util.HashMap;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

import de.fzi.decision.security.ui.controllers.query.IAnalysisClickListener;
import de.fzi.decision.security.ui.main.DelegateSelectionProvider;
import de.fzi.decision.security.ui.models.ISecurityContainer;
import de.fzi.decision.security.ui.views.ISecurityPatternView;
import de.fzi.decision.security.ui.views.impl.parts.ContentWrapper;
import de.fzi.decision.security.ui.views.impl.parts.InputSection;
import de.fzi.decision.security.ui.views.impl.parts.OutputSection;
import de.fzi.decision.security.ui.views.impl.parts.Toolbar;

/**
 * Implementation of the ISecurityPatternView interface.
 */
public class SecurityPatternView implements ISecurityPatternView {
	private Toolbar toolbar;
	private InputSection inputSection;
	private OutputSection outputSection;
	private AdapterFactoryEditingDomain editingDomain;
	
	/**
	 * Creates a new SecurityPatternView, which is not a composite. To create the
	 * corresponding composite one has to call view.init(...). This was done to ensure
	 * that the controller has the responsibility when to draw the UI and not the class
	 * that creates the SecurityPatternView object.
	 * 
	 * @param editingDomain the EMF editingDomain needed for the databinding of the JFace Viewers
	 */
	public SecurityPatternView(AdapterFactoryEditingDomain editingDomain) {
		this.editingDomain = editingDomain;
	}

	@Override
	public void init(
		Composite parent,
		IAnalysisClickListener analysisClickListener,
		DelegateSelectionProvider selectionProvider,
		HashMap<EAttribute, String> patternAttributeMap,
		HashMap<EAttribute, String> prerequisiteAttributeMap,
		HashMap<EAttribute, String> threatAttributeMap,
		ISecurityContainer model
	) {
		setWindowLayout(parent);
		toolbar = new Toolbar(parent, analysisClickListener);
		ContentWrapper contentWrapper = new ContentWrapper(parent, toolbar);
		inputSection = new InputSection(contentWrapper, patternAttributeMap, prerequisiteAttributeMap, editingDomain, selectionProvider, model);
		outputSection = new OutputSection(contentWrapper, threatAttributeMap, editingDomain, selectionProvider, model);	
	}

	private static void setWindowLayout(Composite parent) {
		FormLayout layout = new FormLayout();
		layout.spacing = 10;
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		parent.setLayout(layout);	
	}
	
	@Override
	public StructuredViewer getLeftViewer() {
		return inputSection.getLeftViewer();
	}
	
	@Override
	public StructuredViewer getRightViewer() {
		return inputSection.getRightViewer();
	}
	
	@Override
	public StructuredViewer getOutputViewer() {
		return outputSection.getOutputViewer();
	}

	@Override
	public void setAnalysisResult(String string) {
		toolbar.setAnalysisResult(string);		
	}
	
	@Override
	public void setFilterKeyListener(KeyAdapter listener) {
		toolbar.addFilterKeyListener(listener);
	}
	
	@Override
	public void clearFilterText() {
		toolbar.clearFilterText();
	}
	
	@Override
	public void clearSelection() {
		inputSection.getLeftViewer().setSelection(null);
		inputSection.getRightViewer().setSelection(null);
		outputSection.getOutputViewer().setSelection(null);
	}
	
	@Override
	public String getFilterText() {
		return toolbar.getFilterText();
	}
	
	
}
