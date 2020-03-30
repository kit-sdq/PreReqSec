package de.fzi.decision.security.ui.analysis.dialog.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import de.fzi.decision.security.ui.analysis.dialog.views.CCTabItem;
import de.fzi.decision.security.ui.analysis.dialog.views.SecurityAnalysisWindow;

/**
 * An action class that is responsible for saving the results of the security
 * analysis for system model.
 * 
 * @author Robert Hochweiss
 */
public class FileSaveAction extends Action {

	private String startPath;
	private SecurityAnalysisWindow source;

	/**
	 * Creates a new FileSaveAction.
	 * 
	 * @param source    The source UI-Window, from which the Action is called.
	 * @param startPath The file path where the file dialog should start with its
	 *                  search.
	 */
	public FileSaveAction(SecurityAnalysisWindow source, String startPath) {
		super("&Save");
		this.source = source;
		this.startPath = startPath;
		setToolTipText("Save the current security analysis results for this system model");
	}

	@Override
	public void run() {
		FileDialog fd = new FileDialog(source.getShell(), SWT.SAVE);
		fd.setText("Save");
		fd.setFilterPath(startPath);
		String[] filterExt = { "*.txt", "*.*" };
		fd.setFilterExtensions(filterExt);
		String selected = fd.open();
		try {
			final Path path = Paths.get(selected);
			String[] results = ((CCTabItem) source.getCurTabItem()).getAnalysisResult().saveResults().split("\\r?\\n",
					-1);
			Files.write(path, Arrays.asList(results), StandardCharsets.UTF_8,
					Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
