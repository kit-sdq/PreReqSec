package de.fzi.decision.security.ui.views.impl.parts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A Composite that creates a horizontal bar that displays some additional information and filter possibilities.
 */
public class Toolbar extends Composite {
	private Label analysis;
	private Text filter;
	
	/**
	 * Creates the toolbar. Expects the parent layout to be a FormLayout.
	 * 
	 * @param parent the parent composite.
	 */
	public Toolbar(Composite parent) {
		super(parent, SWT.NONE);
		
		FormData formData = new FormData();
		formData.top = new FormAttachment(0);
		formData.left = new FormAttachment(0);
		formData.right = new FormAttachment(100);
		this.setLayoutData(formData);
		
		GridLayout layout = new GridLayout(5, true);
		this.setLayout(layout);
	
		analysis = new Label(this, SWT.CENTER);
		analysis.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 3, 1));
		analysis.setText("Security Analysis: Running...");
	
		filter = new Text(this, SWT.SEARCH | SWT.ICON_CANCEL | SWT.ICON_SEARCH);
		filter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1));
		filter.setMessage("Enter Query...");
	}

	/**
	 * Changes the text that shows the analysis result.
	 * 
	 * @param string a string showing the result of the analysis
	 */
	public void setAnalysisResult(String string) {
		analysis.setText("Security Analysis: " + string);
		analysis.pack();
	}
	
	/**
	 * Adds a Key Listener to the Filter Textbox.
	 * 
	 * @param KeyAdapter the listener to add
	 */
	public void addFilterKeyListener(KeyAdapter listener) {
		filter.addKeyListener(listener);
	}
	
	/**
	 * Clears the Filter Textbox.
	 */
	public void clearFilterText() {
		filter.setText("");
	}
	
	/**
	 * @returns the current content of the Filter Textbox.
	 */
	public String getFilterText() {
		return filter.getText();
	}
}
