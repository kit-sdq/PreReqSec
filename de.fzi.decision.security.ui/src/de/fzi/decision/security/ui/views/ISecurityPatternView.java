package de.fzi.decision.security.ui.views;

import java.util.HashMap;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.widgets.Composite;

import de.fzi.decision.security.ui.controllers.query.IAnalysisClickListener;
import de.fzi.decision.security.ui.main.DelegateSelectionProvider;
import de.fzi.decision.security.ui.models.ISecurityContainer;

/**
 * Interface for the view showing the SecurityContainer model and the corresponding analysis.
 */
public interface ISecurityPatternView {
	
	/**
	 * Initializes the SecurityPatternView consisting of three viewers (Patterns, Prerequisites, Attacks)
	 * and an extra composite in which additional information like the current analysis result is displayed.
	 * 
	 * @param parent the parent composite
	 * @param patternAttributeMap the AttributeMap for the SecurityPatterns Viewer 
	 * @param prerequisiteAttributeMap the AttributeMap for the Prerequisites Viewer
	 * @param threatAttributeMap the AttributeMap for the Attacks Viewer
	 */
	public void init(
		Composite parent,
		IAnalysisClickListener analysisClickListener,
		DelegateSelectionProvider selectionProvider,
		HashMap<EAttribute, String> patternAttributeMap,
		HashMap<EAttribute, String> prerequisiteAttributeMap,
		HashMap<EAttribute, String> threatAttributeMap,
		ISecurityContainer model
	);
	
	/**
	 * @return the left viewer of the input section
	 */
	public StructuredViewer getLeftViewer();
	
	/**
	 * @return the right viewer of the input section
	 */
	public StructuredViewer getRightViewer();
	
	/**
	 * @return the viewer that shows the output
	 */
	public StructuredViewer getOutputViewer();
	
	/**
	 * @param string the text that shall be displayed as analysis result
	 */
	public void setAnalysisResult(String string);

	/**
	 * @param KeyAdapter the listener that shall be registered to the filter textbox
	 */
	void setFilterKeyListener(KeyAdapter listener);

	/**
	 * Clears the Filter Textbox.
	 */
	void clearFilterText();
	
	/**
	 * @returns the current content of the Filter Textbox.
	 */
	String getFilterText();

	/**
	 * clears the selection of all tableviewers in the view
	 */
	void clearSelection();
}
