package de.fzi.decision.security.cdo.client.view.dialogs;

import org.eclipse.swt.widgets.Shell;

import security.NamedDescribedEntity;

public class CreateNamedDescribedEntityDialog extends NamedDescribedEntityDialog {

	public CreateNamedDescribedEntityDialog(Shell parentShell, NamedDescribedEntity entity) {
		super(parentShell, entity);
	}
	
	@Override
	public void create() {
		super.create();
		String typeName = entity.getClass().getSimpleName().replace("Impl", "");
		setTitle("Create New " + typeName);
		setMessage("Please specify a name and a description for the new " + typeName);
	}

}
