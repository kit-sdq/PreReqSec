package de.fzi.decision.security.ui.views.impl.parts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;

/**
 * SashForm that is used to wrap the main content of the UI. Directly below another composite.
 */
public class ContentWrapper extends SashForm {
	
	/**
	 * Creates a new ContenWrapper SashForm. Expects the parent layout to be a FormLayout.
	 * 
	 * @param parent the parent composite
	 * @param top the composite that is rendered above the ContentWrapper
	 */
	public ContentWrapper(Composite parent, Composite top) {
		super(parent, SWT.VERTICAL);
		
		FormData formData = new FormData();
		formData.top = new FormAttachment(top);
		formData.left = new FormAttachment(0);
		formData.right = new FormAttachment(100);
		formData.bottom = new FormAttachment(100);
		this.setLayoutData(formData);
	}
}
