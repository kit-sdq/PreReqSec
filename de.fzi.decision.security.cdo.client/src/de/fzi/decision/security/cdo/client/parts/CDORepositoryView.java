package de.fzi.decision.security.cdo.client.parts;

import java.io.File;
import java.util.EventObject;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

import de.fzi.decision.security.cdo.client.controller.SecurityRepoController;
import de.fzi.decision.security.cdo.client.util.SecurityFileHandler;
import de.fzi.decision.security.cdo.client.view.SecurityRepoView;
import de.fzi.decision.security.cdo.client.view.dialogs.RepoSettingsDialog;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ResourceSelectionDialog;
import org.eclipse.ui.ide.IDE;
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
	private Button btnCommit;
	private Button btnOpenClose;
	private Composite parent;

	@PostConstruct
	public void createPartControl(Composite parent) {
		this.parent = parent;
		parent.setLayout(new GridLayout(1, false));
		parent.addDisposeListener(initDisposeListener());
		
		lblHeading = new Label(parent, SWT.NONE);
		lblHeading.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblHeading.setText("No session active");
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		controller = new SecurityRepoController(this);
		
		btnOpenClose = new Button(composite, SWT.NONE);
		btnOpenClose.setText("Open Session");
		btnOpenClose.addSelectionListener(addOpenCloseSessionListener());
		
		btnCommit = new Button(composite, SWT.NONE);
		btnCommit.setText("Commit Changes");
		btnCommit.setEnabled(false);
		btnCommit.addSelectionListener(addCommitSelectionListener());
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
	public void openFileInEditor(String filePath) {
		File fileToOpen = new File(filePath);
		
		if (fileToOpen.exists() && fileToOpen.isFile()) {
		    IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
		    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		 
		    try {
		        IDE.openEditorOnFileStore(page, fileStore );
		    } catch (PartInitException e ) {
		        e.printStackTrace();
		    }
		} else {
		    //TODO Do something if the file does not exist
		}
	}
	
	@Override
	public void enableCommit() {
		//run the command on ui thread
		if (!parent.isDisposed()) {
			parent.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					btnCommit.setEnabled(true);
				}
			});
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
	
	private SelectionListener addCommitSelectionListener() {
		return new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.commitChanges();
				btnCommit.setEnabled(false);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
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
	
	private DisposeListener initDisposeListener() {
		return new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (controller.existLocalChanges()) {
					String title = "There are local changes in the model";
					String msg = "Do you want to commit your local changes to the repository?";
					boolean commitChanges = MessageDialog.openConfirm(parent.getShell(), title, msg);
					if (commitChanges) {
						controller.commitChanges();
					}
					SecurityFileHandler.deleteTemporaryProject();
				}
			}
		};
	}

	@Override
	public boolean showCDOConflictDialog() {
		String title = "The local changes are conflicting with the global repository state";
		String msg = "Do you want to commit your local changes and override the global changes?";
		return !(MessageDialog.openConfirm(parent.getShell(), title, msg));
	}
}
