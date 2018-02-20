package de.fzi.decision.security.ui.views.impl.dialogs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import de.fzi.decision.security.ui.controllers.query.IAnalysisClickListener;
import modelLoader.InitializationException;
import modelLoader.LoadingException;
import parser.InterpreterException;

/**
 * 
 * @author matthias endlichhofer
 *
 */

public class AnalysisConfigDialog extends TitleAreaDialog {
	
	private IAnalysisClickListener analysisStarter;
	
	private Text firstQueryTxt;
	private Text secondQueryTxt;

	public AnalysisConfigDialog(Shell parentShell, IAnalysisClickListener analysisStarter) {
		super(parentShell);
		this.analysisStarter = analysisStarter;
	}
	
	@Override
	public void create() {
		super.create();
		setTitle("Security Threat Analysis");
		setMessage("Enter the two queries for the Security Threat Analyis");
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		
		Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        createFirstPart(container);
        createSecondPart(container);
		
		return area;
	}
	
	private void createFirstPart(Composite container) {
		Label lbl = new Label(container, SWT.NONE);
		lbl.setText("First Query:");
		
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		
		firstQueryTxt = new Text(container, SWT.BORDER);
		firstQueryTxt.setLayoutData(gridData);
	}

	private void createSecondPart(Composite container) {
		Label lbl = new Label(container, SWT.NONE);
		lbl.setText("Second Query:");
		
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		
		secondQueryTxt = new Text(container, SWT.BORDER);
		secondQueryTxt.setLayoutData(gridData);
	}
	
	@Override
	protected boolean isResizable() {
		return false;
	}
	
	@Override
	protected void okPressed() {
		String firstQuery = firstQueryTxt.getText();
		String secondQuery = secondQueryTxt.getText();
		
		if (checkValidQuery(firstQuery) && checkValidQuery(secondQuery)) {
				startAnalysis(firstQuery, secondQuery);
				super.okPressed();
			
		} else {
			Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openError(activeShell, "Illegal query", "The query has to start with 'get prerequisite'");
		}
		
	}
	
	private boolean checkValidQuery(String query) {
		query.toLowerCase();
		if (query.startsWith("get prerequisite")) {
			return true;
		}
		return false;
	}
	
	private void startAnalysis(String firstQuery, String secondQuery) {
		try {
			analysisStarter.startAnalysis(firstQuery, secondQuery);
		} catch (InterpreterException e) {
			Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openError(activeShell, "Illegal query", e.getMessage());
		} catch (LoadingException e) {
			Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openError(activeShell, "Problems loading the result", e.getMessage());
		} catch (InitializationException e) {
			Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openError(activeShell, "Problems loading the resource", e.getMessage());
		}
	}

}
