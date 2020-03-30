package de.fzi.decision.security.ui.analysis.dialog.controller;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.FileDialog;
import org.palladiosimulator.pcm.system.util.SystemResourceFactoryImpl;

import de.fzi.decision.security.ui.analysis.dialog.views.CCTabItem;
import de.fzi.decision.security.ui.analysis.dialog.views.SecurityAnalysisWindow;

/**
 * An action class that is responsible for opening a new system file and
 * changing the UI accordingly.
 * 
 * @author Robert Hochweiss
 */
public class FileOpenAction extends Action {

	private String startPath;
	private SecurityAnalysisWindow source;

	/**
	 * Creates a new FileOpenAction.
	 * 
	 * @param source    The source UI-Window, from which the Action is called and
	 *                  which is to be modified.
	 * @param startPath The file path where the file dialog should start with its
	 *                  search.
	 */
	public FileOpenAction(SecurityAnalysisWindow source, String startPath) {
		super("&Open");
		this.source = source;
		this.startPath = startPath;
		setToolTipText("Open a new system that is to be analyzied regarding security concerns");
	}

	@Override
	public void run() {
		FileDialog fd = new FileDialog(source.getShell(), SWT.OPEN);
		fd.setText("Open");
		fd.setFilterPath(startPath);
		String[] filterExt = { "*.system" };
		fd.setFilterExtensions(filterExt);
		String selected = fd.open();
		if (new File(selected).exists()) {
			ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
					new EcoreResourceFactoryImpl());
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("system",
					new SystemResourceFactoryImpl());
			Resource systemModel = resourceSet.getResource(URI.createFileURI(selected), true);
			EObject root = systemModel.getContents().get(0);
			source.getTabFolder().setSelection(createFileTab(source.getTabFolder(), root));
		}
	}

	private CTabItem createFileTab(CTabFolder parent, EObject root) {
		source.setCurTabItem(new CCTabItem(parent, root));
		return source.getCurTabItem();
	}
}
