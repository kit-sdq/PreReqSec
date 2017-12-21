package de.fzi.decision.security.cdo.client.view.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class RepoSettingsDialog extends TitleAreaDialog {
	
	private Text hostText;
	private Text repoText;
	private String host;
	private String repo;

	public RepoSettingsDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
    public void create() {
        super.create();
        setTitle("Open Repository Session");
    }
	
	@Override
	protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);
        createHostInput(container);
        createRepoInput(container);
        return area;
    }
	
	private void createHostInput(Composite container) {
        Label lblHost = new Label(container, SWT.NONE);
        lblHost.setText("Host:");

        GridData dataHost = new GridData();
        dataHost.grabExcessHorizontalSpace = true;
        dataHost.horizontalAlignment = GridData.FILL;

        hostText = new Text(container, SWT.BORDER);
        hostText.setLayoutData(dataHost);
    }

    private void createRepoInput(Composite container) {
        Label lblRepo = new Label(container, SWT.NONE);
        lblRepo.setText("Repository name:");

        GridData dataRepo = new GridData();
        dataRepo.grabExcessHorizontalSpace = true;
        dataRepo.horizontalAlignment = GridData.FILL;
        repoText = new Text(container, SWT.BORDER);
        repoText.setLayoutData(dataRepo);
    }
    
    @Override
    protected boolean isResizable() {
        return true;
    }

    // save content of the Text fields because they get disposed
    // as soon as the Dialog closes
    private void saveInput() {
        host = hostText.getText();
        repo = repoText.getText();

    }

    @Override
    protected void okPressed() {
        saveInput();
        super.okPressed();
    }

    public String getHost() {
        return host;
    }

    public String getRepoName() {
        return repo;
    }

}
