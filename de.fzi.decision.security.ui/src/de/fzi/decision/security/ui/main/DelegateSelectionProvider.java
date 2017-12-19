package de.fzi.decision.security.ui.main;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * A SelectionProvider that is used for the integration into the Eclipse SelectionListenerService.
 * It delegates the SelectionProvider to the currently selected Viewer.
 */
public class DelegateSelectionProvider implements ISelectionProvider {
	private ISelectionProvider delegate;
	private final ListenerList<ISelectionChangedListener> selectionListeners = new ListenerList<ISelectionChangedListener>();
	
	private ISelectionChangedListener selectionListener = new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
			if (event.getSelectionProvider() == delegate) {
				fireSelectionChanged(event.getSelection());
			}
		}
	};
	
	/**
	 * @param newDelegate the SelectionProvider that shall currently be used for the Eclipse SelectionListenerService
	 */
	public void setSelectionProviderDelegate(ISelectionProvider newDelegate) {
		if (delegate == newDelegate) {
			return;
		}
		
		if (delegate != null) {
			delegate.removeSelectionChangedListener(selectionListener);
		}
		
		delegate = newDelegate;
		
		if (newDelegate != null) {
			newDelegate.addSelectionChangedListener(selectionListener);

			fireSelectionChanged(newDelegate.getSelection());
		}
	}
	
	private void fireSelectionChanged(ISelection selection) {
		SelectionChangedEvent event = new SelectionChangedEvent(delegate, selection);
		for (ISelectionChangedListener listener : selectionListeners) {
			listener.selectionChanged(event);
		}
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.add(listener);				
	}

	@Override
	public ISelection getSelection() {
		return delegate == null ? null : delegate.getSelection();
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.remove(listener);				
	}

	@Override
	public void setSelection(ISelection selection) {
		if (delegate != null) {
			delegate.setSelection(selection);
		}				
	}

}
