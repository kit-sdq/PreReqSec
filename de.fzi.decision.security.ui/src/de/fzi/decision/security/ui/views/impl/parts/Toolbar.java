package de.fzi.decision.security.ui.views.impl.parts;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.fzi.decision.security.ui.controllers.query.IAnalysisClickListener;
import de.fzi.decision.security.ui.views.impl.dialogs.AnalysisConfigDialog;

/**
 * A Composite that creates a horizontal bar that displays some additional information and filter possibilities.
 */
public class Toolbar extends Composite {
	private Label analysis;
	private Text filter;
	private Button analysisBtn;
	
	/**
	 * Creates the toolbar. Expects the parent layout to be a FormLayout.
	 * 
	 * @param parent the parent composite.
	 */
	public Toolbar(Composite parent, IAnalysisClickListener analysisClickListener) {
		super(parent, SWT.NONE);
		FormData formData = new FormData();
		formData.top = new FormAttachment(0);
		formData.left = new FormAttachment(0);
		formData.right = new FormAttachment(100);
		this.setLayoutData(formData);
		
		GridLayout layout = new GridLayout(10, true);
		this.setLayout(layout);
		
		Composite analysisComposite = new Composite(this, SWT.NONE);
		analysisComposite.setLayout(new GridLayout(3, true));
		analysisComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 6, 1));
		
		analysisBtn = new Button(analysisComposite, SWT.CENTER);
		analysisBtn.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		analysisBtn.setText("Security Threat Analysis");
		addAnalysisBtnClickListener(analysisClickListener);
	
		analysis = new Label(analysisComposite, SWT.CENTER);
		analysis.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 2, 1));
		analysis.setText("");
		
		filter = new Text(this, SWT.SEARCH | SWT.ICON_CANCEL | SWT.ICON_SEARCH);
		filter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 4, 1));
		filter.setMessage("Enter Query...");
	}
	
	private void addAnalysisBtnClickListener(IAnalysisClickListener analysisClickListener) {
		analysisBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openAnalysisConfigDialog(analysisClickListener);
			}
		});
	}
	
	private void openAnalysisConfigDialog(IAnalysisClickListener analysisClickListener) {
		AnalysisConfigDialog analysisDialog = new AnalysisConfigDialog(getShell(), analysisClickListener);
		analysisDialog.create();
		if (analysisDialog.open() == Window.CANCEL) {
			resetAnalysisResult();
		}
	}
	
	private void resetAnalysisResult() {
		analysis.setText("");
		analysis.pack();
	}

	/**
	 * Changes the text that shows the analysis result.
	 * 
	 * @param string a string showing the result of the analysis
	 */
	public void setAnalysisResult(String string) {
		analysis.setText("Result: " + string);
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
