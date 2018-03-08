package de.fzi.decision.security.ui.models.impl;

import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

import de.fzi.decision.security.ui.models.ISecurityContainer;
import de.fzi.decision.security.ui.models.ModelModificationListener;
import de.fzi.decision.security.ui.views.impl.dialogs.NamedDescribedEntityCreatorDialog;
import security.securityThreats.Attack;
import security.securityThreats.impl.SecurityThreatsFactoryImpl;

public class AttackModificationListener extends ModelModificationListener {

	public AttackModificationListener(ISecurityContainer container) {
		super(container);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addEntity() {
		Attack attack = (new SecurityThreatsFactoryImpl()).createAttack();
		NamedDescribedEntityCreatorDialog dialog = new NamedDescribedEntityCreatorDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), attack);
		if (dialog.open() == Window.OK) {
			securityContainer.addAttack(attack);
		}
	}

	@Override
	public void deleteEntity(String id) {
		securityContainer.deleteAttack(id);
	}

}
