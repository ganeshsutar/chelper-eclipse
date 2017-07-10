package chelpereclipse.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

public class CreateProjectWizard extends Wizard implements INewWizard {
	private static final String WIZARD_NAME = "CHelper Eclipse Java Project";
	private static final String WIZARD_DESC = "CHelper Eclipse Java Project for the problem. A Main class with default package will be created with launch configuration";
	
	private WizardNewProjectCreationPage projectPage;
	
	public CreateProjectWizard() {
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public boolean performFinish() {
		try {
			getContainer().run(true, true, new ProjectCreator(projectPage.getProjectName()));
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	@Override
	public void addPages() {
		super.addPages();

		projectPage = new WizardNewProjectCreationPage(WIZARD_NAME);
		projectPage.setTitle(WIZARD_NAME);
		projectPage.setDescription(WIZARD_DESC);
		
		addPage(projectPage);
	}
}
