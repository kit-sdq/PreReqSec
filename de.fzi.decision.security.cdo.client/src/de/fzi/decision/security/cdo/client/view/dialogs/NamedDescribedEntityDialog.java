package de.fzi.decision.security.cdo.client.view.dialogs;

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

import security.NamedDescribedEntity;

public class NamedDescribedEntityDialog extends TitleAreaDialog {
	
	protected NamedDescribedEntity entity;
	
	private Text name;
	private Text description;

	public NamedDescribedEntityDialog(Shell parentShell, NamedDescribedEntity entity) {
		super(parentShell);
		this.entity = entity;
	}
	
	@Override
	protected boolean isResizable() {
		return false;
	}
		
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		
		Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        createNamePart(container);
        createDescriptionPart(container);
		
		return area;
	}

	private void createNamePart(Composite container) {
		Label lbl = new Label(container, SWT.NONE);
		lbl.setText("Name:");
		
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		
		name = new Text(container, SWT.BORDER);
		name.setLayoutData(gridData);
	}
	
	private void createDescriptionPart(Composite container) {
		Label lbl = new Label(container, SWT.NONE);
		lbl.setText("Description:");
		
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		
		description = new Text(container, SWT.BORDER);
		description.setLayoutData(gridData);
	}

	@Override
	protected void okPressed() {
		if (isInputValid()) {
			entity.setName(name.getText());
			entity.setDescription(description.getText());
			super.okPressed();
		} else {
			Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openError(activeShell, "Invalid input", "You must specify a name and a description");
		}
	}

	private boolean isInputValid() {
		if (name.getText().isEmpty() || description.getText().isEmpty()) {
			return false;
		}
		return true;
	}
	
}
