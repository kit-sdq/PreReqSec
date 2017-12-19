package de.fzi.decision.security.ui.main;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

/**
 * No-Op implemention of IPartListener.
 */
public class PartAdapter implements IPartListener {

	@Override
	public void partActivated(IWorkbenchPart part) {
		// no-op
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		// no-op
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		// no-op
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		// no-op
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		// no-op
	}

}
