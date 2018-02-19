package de.fzi.decision.security.ui.models.impl;

import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

import de.fzi.decision.security.ui.models.ISecurityContainer;
import de.fzi.decision.security.ui.models.ModelModificationListener;
import de.fzi.decision.security.ui.views.impl.dialogs.NamedDescribedEntityCreatorDialog;
import security.securityPrerequisites.Prerequisite;
import security.securityPrerequisites.impl.SecurityPrerequisitesFactoryImpl;

public class PrerequisiteModificationListener extends ModelModificationListener {

	public PrerequisiteModificationListener(ISecurityContainer container) {
		super(container);
	}

	@Override
	public void addEntity() {
		Prerequisite prerequisite = (new SecurityPrerequisitesFactoryImpl()).createPrerequisite();
		NamedDescribedEntityCreatorDialog dialog = new NamedDescribedEntityCreatorDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), prerequisite);
		if (dialog.open() == Window.OK) {
			securityContainer.addPrerequisite(prerequisite);
		}
	}

	@Override
	public void deleteEntity(String id) {
		securityContainer.deletePrerequisite(id);
	}

}
