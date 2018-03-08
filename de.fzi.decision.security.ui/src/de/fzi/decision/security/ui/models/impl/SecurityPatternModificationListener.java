package de.fzi.decision.security.ui.models.impl;

import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

import de.fzi.decision.security.ui.models.ISecurityContainer;
import de.fzi.decision.security.ui.models.ModelModificationListener;
import de.fzi.decision.security.ui.views.impl.dialogs.NamedDescribedEntityCreatorDialog;
import security.securityPatterns.SecurityPattern;
import security.securityPatterns.impl.SecurityPatternsFactoryImpl;

public class SecurityPatternModificationListener extends ModelModificationListener {

	public SecurityPatternModificationListener(ISecurityContainer model) {
		super(model);
	}

	@Override
	public void addEntity() {
		SecurityPattern pattern = (new SecurityPatternsFactoryImpl()).createSecurityPattern();
		NamedDescribedEntityCreatorDialog dialog = new NamedDescribedEntityCreatorDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), pattern);
		if (dialog.open() == Window.OK) {
			securityContainer.addSecurityPattern(pattern);
		}
	}

	@Override
	public void deleteEntity(String id) {
		securityContainer.deleteSecurityPattern(id);
	}
}