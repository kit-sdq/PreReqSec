package de.fzi.decision.security.cdo.client.parts;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

import de.fzi.decision.security.cdo.client.controller.SecurityRepoController;
import de.fzi.decision.security.cdo.client.util.Constants;
import de.fzi.decision.security.cdo.client.util.SecurityEditorInput;
import de.fzi.decision.security.cdo.client.view.SecurityRepoView;
import de.fzi.decision.security.cdo.client.view.dialogs.RepoSettingsDialog;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Label;

/**
 * @author matthias endlichhofer
 *
 */
public class CDORepositoryView implements SecurityRepoView{
	
	private SecurityRepoController controller;
	private Label lblHeading;
	private Button btnOpenClose;
	private Composite parent;
	private SecurityEditorInput editorInput;

	@PostConstruct
	public void createPartControl(Composite parent) {
		this.parent = parent;
		parent.setLayout(new GridLayout(1, false));
		
		lblHeading = new Label(parent, SWT.NONE);
		GridData gd_lblHeading = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_lblHeading.minimumWidth = 250;
		lblHeading.setLayoutData(gd_lblHeading);
		lblHeading.setText("No session active");
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		controller = new SecurityRepoController(this);
		
		btnOpenClose = new Button(composite, SWT.NONE);
		btnOpenClose.setText("Open Session");
		btnOpenClose.addSelectionListener(addOpenCloseSessionListener());
	}

		@Focus
	public void setFocus() {
		//TODO
	}	
	
	@Override
	public URI startModelSelection() {
		FileDialog fileDialog = new FileDialog(parent.getShell());
		fileDialog.setText("Choose the security model:");
		fileDialog.setFilterExtensions(new String[]{"*.security"});
		fileDialog.setFilterPath(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString());
		String filePath = fileDialog.open();
		if (filePath != null) {
			return URI.createFileURI(filePath);
		}
		return null;
	}

	@Override
	public void openResourceInEditor(SecurityEditorInput editorInput) {
		if (editorInput != null) {
			this.editorInput = editorInput;
		    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		    try {
		        page.openEditor(editorInput, Constants.SECURITY_EDITOR_ID);
		    } catch (PartInitException e ) {
		        e.printStackTrace();
		    }
		}
	}
	
	@Override
	public void closeSecurityEditorIfOpen() throws PartInitException {
		if (editorInput != null) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			List<IEditorReference> editorsToClose = new ArrayList<>();
			for (IEditorReference editorReference : page.getEditorReferences()) {
				if (editorReference.getEditorInput().equals(editorInput)) {
					editorsToClose.add(editorReference);
				}
			}
			page.closeEditors(editorsToClose.toArray(new IEditorReference[editorsToClose.size()]), true);
		}
	}
	
	private boolean connectToServer(String host, String repoName) {
		try {
			controller.connectToCDOServer(host, repoName);
		} catch (LifecycleException ex) {
			ErrorDialog.openError(parent.getShell(), 
					"Connection Error", "Connection to " + repoName + " at " + host + " is not possible",
						new Status(IStatus.ERROR, "de.fzi.decision.security.cdo.client", ex.getClass().getName()));
			return false;
		}
		return true;
	}
	
	private SelectionListener addOpenCloseSessionListener() {
		return new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!controller.isRepoLoaded()) {
					String repoName = showRepoLoadDialog();
					if (repoName != null) {
						lblHeading.setText("Session active: [" + repoName + "]");
						btnOpenClose.setText("Close Session");
					}
				} else {
					closeCurrentSession();
					lblHeading.setText("No session active");
					btnOpenClose.setText("Open Session");
				}
				
			}
			
			private void closeCurrentSession() {
				controller.closeSession();
			}

			private String showRepoLoadDialog() {
				RepoSettingsDialog settingsDialog = new RepoSettingsDialog(parent.getShell());
				settingsDialog.create();
				if (settingsDialog.open() == Window.OK) {
					String host = settingsDialog.getHost();
					String repoName = settingsDialog.getRepoName();
					boolean connected = connectToServer(host, repoName);
					if (connected) {
						return repoName;
					}
				}
				return null;
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	/*@Override
	public boolean showCDOConflictDialog() {
		String title = "The local changes are conflicting with the global repository state";
		String msg = "Do you want to commit your local changes and override the global changes?";
		return !(MessageDialog.openConfirm(parent.getShell(), title, msg));
	}*/

	@Override
	public String showContainerChooserDialogAndGetResult(List<String> containers) {
		String chosenName = null;
		ElementListSelectionDialog dialog =
			    new ElementListSelectionDialog(parent.getShell(), new LabelProvider());
			dialog.setElements(containers.toArray());
			dialog.setTitle("There are multiple security containers in the repository");
			dialog.setMessage("Choose one of the security containers");
			dialog.setMultipleSelection(false);
			// user pressed cancel
			if (dialog.open() == Window.OK) {
				chosenName = dialog.getFirstResult().toString();
			}
			return chosenName;
	}
	
	
}
