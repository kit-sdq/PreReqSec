package de.fzi.decision.security.cdo.client.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

import de.fzi.decision.security.cdo.client.controller.SecurityRepoController;
import de.fzi.decision.security.cdo.client.util.Constants;
import de.fzi.decision.security.cdo.client.util.SecurityEditorInput;
import de.fzi.decision.security.cdo.client.view.dialogs.OpenSessionDialog;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;

/**
 * @author matthias endlichhofer
 *
 */
public class SecurityRepoView implements ISecurityRepoView{
	
	private SecurityRepoController controller;
	private Label lblHeading;
	private Button btnSession;
	private Composite parent;
	private TableViewer tableViewer;
	private Table table;
	private Composite controlComposite;
	private Button deleteButton;
	private Button openButton;
	private String selectedContainer;

	@PostConstruct
	public void createPartControl(Composite parent) {
		this.parent = parent;
		parent.setLayout(new GridLayout(1, false));
		
		createHeading();
		createTableViewer();
		createControlComposite();
		createSessionButton();
		setVisibilityOfContainerComposite(false);
		
		controller = new SecurityRepoController(this);
	}

	private void createHeading() {
		lblHeading = new Label(parent, SWT.NONE);
		GridData gd_lblHeading = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_lblHeading.minimumWidth = 250;
		lblHeading.setLayoutData(gd_lblHeading);
		lblHeading.setText("No session active");
	}

	private void createTableViewer() {
		tableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		createContainerNameColumn();
		
		table = tableViewer.getTable();
		table.setHeaderVisible(false);
		table.setLinesVisible(false);
		addTableSelectionListener();
		
		GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalSpan = 2;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        tableViewer.getControl().setLayoutData(gridData);
	}


	private void createContainerNameColumn() {
		TableViewerColumn colContainerName = new TableViewerColumn(tableViewer, SWT.NONE);
		colContainerName.getColumn().setWidth(500);
		colContainerName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return element.toString();
			}
		});
	}
	
	private void addTableSelectionListener() {
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem item = (TableItem) e.item;
				if (item != null) {
					selectedContainer = (String) item.getData();
					setButtonsEnabled(true);
				} else {
					selectedContainer = null;
					setButtonsEnabled(false);
				}
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (selectedContainer != null) {
					controller.openResourceInEditor("/" + selectedContainer);
				}
			}
		});
	}
	
	private void setButtonsEnabled(boolean enabled) {
		openButton.setEnabled(enabled);
		deleteButton.setEnabled(enabled);
	}
	
	private void createControlComposite() {
		controlComposite = new Composite(parent, SWT.BORDER_SOLID);
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.fill = true;
		layout.marginBottom = 5;
		layout.marginTop = 5;
		controlComposite.setLayout(layout);
		createOpenEditorButton();
		createDeleteButton();
		createLoadButton();
	}
	
	private void createOpenEditorButton() {
		openButton = new Button(controlComposite, SWT.PUSH);
		
		openButton.setEnabled(false);
		openButton.setText("Open");
		openButton.computeSize(SWT.DEFAULT, table.getItemHeight());
		openButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selectedContainer != null) {
					String path = "/" + selectedContainer;
					controller.openResourceInEditor(path);
				}
			}
		});
	}
	
	private void createDeleteButton() {
		deleteButton = new Button(controlComposite, SWT.PUSH);
		deleteButton.setEnabled(false);
		deleteButton.setText("Delete");
		deleteButton.computeSize(SWT.DEFAULT, table.getItemHeight());
		deleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selectedContainer != null) {
					if (doubleCheckDeletionIntention(selectedContainer)) {
						String name = selectedContainer;
						deleteResource(name);
						closeSecurityEditorByModelName(name);
					}
				}
			}
		});
	}
	
	private boolean doubleCheckDeletionIntention(String name) {
		String title = "Deletion of " + name;
		String msg = "Do you really want to delete the container " + name + "?";
		return MessageDialog.openConfirm(parent.getShell(), title, msg);
	}
	
	private void deleteResource(String name) {
		try {
			controller.deleteResource(name);
		} catch (CommitException e) {
			e.printStackTrace();
			MessageDialog.openInformation(parent.getShell(), "Resource deletion could not be committed", e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			MessageDialog.openInformation(parent.getShell(), "Resource could not be deletetd", e.getMessage());
		}
	}
	
	private void closeSecurityEditorByModelName(String name) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		for (IEditorReference editorReference : page.getEditorReferences()) {
			try {
				if (editorReference.getEditorInput() instanceof SecurityEditorInput) {
					SecurityEditorInput input = (SecurityEditorInput) editorReference.getEditorInput();
					if (input.getName().equals(name)) {
						page.closeEditor(editorReference.getEditor(false), true);
					}
				}
			} catch (PartInitException e) {
				e.printStackTrace();
				MessageDialog.openInformation(parent.getShell(), 
						"EditorInput could not be found for: " + editorReference.getName(), e.getMessage());
			}
		}
	}
	
	private void createLoadButton() {
		Button loadButton = new Button(controlComposite, SWT.PUSH);
		loadButton.setEnabled(true);
		loadButton.setText("Load Security Model");
		loadButton.computeSize(SWT.DEFAULT, table.getItemHeight());
		loadButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					controller.doLoadModel();
				} catch (CommitException e1) {
					e1.printStackTrace();
					MessageDialog.openInformation(parent.getShell(), "Resource could not be committed", e1.getMessage());
				} catch (IllegalArgumentException e2) {
					MessageDialog.openInformation(parent.getShell(), "Resource could not be loaded", 
							"The selected container name already exists in the repository.");
				} catch (Exception e3) {
					MessageDialog.openInformation(parent.getShell(), "Resource could not be loaded", 
							e3.getMessage());
				}
			}
		});
	}
	
	private void createSessionButton() {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		btnSession = new Button(composite, SWT.NONE);
		btnSession.setText("Open Session");
		btnSession.addSelectionListener(addOpenCloseSessionListener());
	}

	private SelectionListener addOpenCloseSessionListener() {
		return new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!controller.isRepoLoaded()) {
					String repoName = showOpenSessionDialog();
					if (repoName != null) {
						lblHeading.setText("Session active: [" + repoName + "]");
						btnSession.setText("Close Session");
					}
				} else {
					closeCurrentSession();
					lblHeading.setText("No session active");
					btnSession.setText("Open Session");
				}
				
			}
			
			private void closeCurrentSession() {
				controller.closeSession();
			}
		};
	}
	
	private String showOpenSessionDialog() {
		OpenSessionDialog openSessionDialog = new OpenSessionDialog(parent.getShell());
		openSessionDialog.create();
		if (openSessionDialog.open() == Window.OK) {
			String host = openSessionDialog.getHost();
			String repoName = openSessionDialog.getRepoName();
			boolean connected = connectToServer(host, repoName);
			if (connected) {
				return repoName;
			}
		}
		return null;
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
	
	private void setVisibilityOfContainerComposite(boolean visible) {
		table.setVisible(visible);
		controlComposite.setVisible(visible);
	}

	@Focus
	public void setFocus() {
		//not important
	}	

	@Override
	public void openResourceInEditor(SecurityEditorInput editorInput) {
		if (editorInput != null) {
		    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		    try {
		        page.openEditor(editorInput, Constants.SECURITY_EDITOR_ID);
		    } catch (PartInitException e ) {
		        e.printStackTrace();
		    }
		}
	}
	
	@Override
	public URI openModelSelectionDialog() {
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
	public void onSessionClosed() {
		closeAllOpenSecurityEditors();
		selectedContainer = null;
		setButtonsEnabled(false);
		setVisibilityOfContainerComposite(false);
	}
	
	@Override
	public void setTableInput(String[] containerNames) {
		parent.getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				setVisibilityOfContainerComposite(true);
				table.clearAll();
				tableViewer.setInput(containerNames);
				tableViewer.refresh();
			}
		});
	}
	
	private void closeAllOpenSecurityEditors() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		List<IEditorReference> editorsToClose = new ArrayList<>();
		for (IEditorReference editorReference : page.getEditorReferences()) {
			try {
				if (editorReference.getEditorInput() instanceof SecurityEditorInput) {
					editorsToClose.add(editorReference);
				}
			} catch (PartInitException e) {
				e.printStackTrace();
				MessageDialog.openInformation(parent.getShell(),
						"EditorInput could not be found for: " + editorReference.getName(), e.getMessage());
			}
		}
		page.closeEditors(editorsToClose.toArray(new IEditorReference[editorsToClose.size()]), true);
	}
	
}
