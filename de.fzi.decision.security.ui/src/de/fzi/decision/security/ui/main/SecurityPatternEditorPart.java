package de.fzi.decision.security.ui.main;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.util.EditUIUtil;
import org.eclipse.emf.edit.ui.view.ExtendedPropertySheetPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;
import de.fzi.decision.security.cdo.client.util.SecurityEditorInput;
import de.fzi.decision.security.ui.controllers.AppController;
import de.fzi.decision.security.ui.models.impl.SecurityContainer;
import de.fzi.decision.security.ui.views.impl.SecurityPatternView;

/**
 * EntryPoint of the SecurityPattern UI. Used to handle interaction with and into Eclipse.
 */
public class SecurityPatternEditorPart extends EditorPart {
	private PropertySheetPage propertySheetPage;
	private AdapterFactoryEditingDomain editingDomain;
	private ComposedAdapterFactory adapterFactory;
	private Collection<Resource> removedResources = new ArrayList<Resource>();
	private Collection<Resource> changedResources = new ArrayList<Resource>();
	private Collection<Resource> savedResources = new ArrayList<Resource>();
	
	private AppController controller;
	private CDOTransaction transaction;
	private URI uri;
	
	private IPartListener partListener = new PartAdapter() {
		@Override
		public void partActivated(IWorkbenchPart p) {
			if (p instanceof PropertySheet && p.equals(propertySheetPage) || p == SecurityPatternEditorPart.this) {
				handleActivate();
			}
		}
	};
	
	private IResourceChangeListener resourceChangeListener = new IResourceChangeListener() {
		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			IResourceDelta delta = event.getDelta();
			ResourceDeltaVisitor visitor = new ResourceDeltaVisitor(editingDomain.getResourceSet(),	savedResources);
			
			try {
				delta.accept(visitor);
			} catch (CoreException e) {
				e.printStackTrace();
			}

			if (visitor.hasRemovedResources()) {
				getSite().getShell().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						removedResources.addAll(visitor.getRemovedResources());
						if (!isDirty()) {
							getSite().getPage().closeEditor(SecurityPatternEditorPart.this, false);
						}
					}
				});
			}

			if (visitor.hasChangedResources()) {
				getSite().getShell().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						changedResources.addAll(visitor.getChangedResources());
						if (getSite().getPage().getActiveEditor() == SecurityPatternEditorPart.this) {
							handleActivate();
						}
					}
				});
			}
		}
	};
		
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInputWithNotify(input);
		setPartName(input.getName());
		site.getPage().addPartListener(partListener);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener, IResourceChangeEvent.POST_CHANGE);
		initializeEditingDomain(input);
		
	}
	
	

	@Override
	public void createPartControl(Composite parent) {
		SecurityPatternView view = new SecurityPatternView(editingDomain);
		SecurityContainer model = new SecurityContainer(editingDomain);
		controller = new AppController(view, model);
		
		DelegateSelectionProvider delegateSelectionProvider = new DelegateSelectionProvider();
		getSite().setSelectionProvider(delegateSelectionProvider);
		
		controller.init(parent, uri, delegateSelectionProvider);
	}

	@Override
	public void setFocus() {
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object getAdapter(Class key) {
		if (key.equals(IPropertySheetPage.class)) {
			return propertySheetPage;
		} else {
			return super.getAdapter(key);
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		final Map<Object, Object> saveOptions = new HashMap<Object, Object>();
		saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);
		saveOptions.put(Resource.OPTION_LINE_DELIMITER, Resource.OPTION_LINE_DELIMITER_UNSPECIFIED);

		WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
			@Override
			public void execute(IProgressMonitor monitor) {
				for (Resource resource : editingDomain.getResourceSet().getResources()) {
					boolean notEmpty = !resource.getContents().isEmpty() || isPersisted(resource);
					boolean writeAllowed = !editingDomain.isReadOnly(resource);

					if (notEmpty && writeAllowed) {
						try {
							long timeStamp = resource.getTimeStamp();
							//TODO catch LocalCommitConflictException
							resource.save(saveOptions);
							if (resource.getTimeStamp() != timeStamp) {
								savedResources.add(resource);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		};

		try {
			new ProgressMonitorDialog(getSite().getShell()).run(true, false, operation);
			((BasicCommandStack) editingDomain.getCommandStack()).saveIsDone();
			firePropertyChange(PROP_DIRTY);
			controller.runAnalysis();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			transaction.commit();
		} catch (CommitException e) {
			//TODO handle that
			e.printStackTrace();
		}
	}

	@Override
	public void doSaveAs() {
		// no save as allowed for now
	}

	@Override
	public boolean isDirty() {
		return ((BasicCommandStack) editingDomain.getCommandStack()).isSaveNeeded();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public boolean isSaveOnCloseNeeded() {
		return true;
	}
	
	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceChangeListener);
		adapterFactory.dispose();
		propertySheetPage.dispose();
		getSite().getPage().removePartListener(partListener);
		if (transaction != null) {
			transaction.close();
			transaction = null;
		}
		super.dispose();
	}
	
	private void initializeEditingDomain(IEditorInput editorInput) {
		adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
		
		BasicCommandStack commandStack = new BasicCommandStack();

		commandStack.addCommandStackListener(new CommandStackListener() {
			@Override
			public void commandStackChanged(final EventObject event) {
				getSite().getShell().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						firePropertyChange(PROP_DIRTY);
					}
				});
			}
		});

		editingDomain = new AdapterFactoryEditingDomain(adapterFactory, commandStack, new HashMap<Resource, Boolean>());
		if (checkIfCDOInput(editorInput)) {
			SecurityEditorInput securityInput = (SecurityEditorInput) editorInput;
			handleCDOInput(securityInput, editingDomain);
		} else {
			uri = EditUIUtil.getURI(getEditorInput(), editingDomain.getResourceSet().getURIConverter());
		}
		propertySheetPage = new ExtendedPropertySheetPage(editingDomain);
		propertySheetPage.setPropertySourceProvider(new AdapterFactoryContentProvider(adapterFactory));
	}
	
	private boolean checkIfCDOInput(IEditorInput input) {
		if (input instanceof SecurityEditorInput) {
			return true;
		} else {
			return false;
		}
	}
	
	private void handleCDOInput(SecurityEditorInput input, AdapterFactoryEditingDomain editingDomain) {
		ResourceSet set = editingDomain.getResourceSet();
		transaction = input.getTransaction(set);
		CDOResource resource = transaction.getResource(input.getResourcePath());
		uri = resource.getURI();
	}
	
	private void handleActivate() {
		if (!removedResources.isEmpty()) {
			if (handleDirtyConflict()) {
				getSite().getPage().closeEditor(SecurityPatternEditorPart.this, false);
			} else {
				removedResources.clear();
				changedResources.clear();
				savedResources.clear();
			}
		} else if (!changedResources.isEmpty()) {
			changedResources.removeAll(savedResources);
			handleChangedResources();
			changedResources.clear();
			savedResources.clear();
		}
	}
	
	private boolean handleDirtyConflict() {
		return MessageDialog.openQuestion(
			getSite().getShell(),
			"File Conflict",
			"There are unsaved changes that conflict with changes made outside the editor. " +
			"Do you wish to discard this editor's changes?"
		);
	}
	
	private void handleChangedResources() {
		if (!changedResources.isEmpty() && (!isDirty() || handleDirtyConflict())) {
			if (isDirty()) {
				changedResources.addAll(editingDomain.getResourceSet().getResources());
			}
			
			editingDomain.getCommandStack().flush();

			for (Resource resource : changedResources) {
				if (resource.isLoaded()) {
					resource.unload();
					
					try {
						resource.load(Collections.EMPTY_MAP);
						controller.reload(resource.getURI());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private boolean isPersisted(Resource resource) {
		boolean result = false;

		try {
			InputStream stream = editingDomain.getResourceSet().getURIConverter().createInputStream(resource.getURI());
			if (stream != null) {
				result = true;
				stream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}


}
