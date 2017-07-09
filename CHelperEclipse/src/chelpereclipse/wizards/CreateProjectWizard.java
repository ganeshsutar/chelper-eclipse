package chelpereclipse.wizards;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import chelpereclipse.util.ProjectHelper;

public class CreateProjectWizard extends Wizard implements INewWizard {
	private static final String WIZARD_NAME = "CHelper Eclipse Java Project";
	private static final String WIZARD_DESC = "CHelper Eclipse Java Project for the problem. A Main class with default package will be created with launch configuration";
	
	private WizardNewProjectCreationPage projectPage;
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public boolean performFinish() {
		try {
			prepareProject();
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
	
	private void prepareProject() throws CoreException, URISyntaxException, IOException {
		// Create Project
		IProject project = ProjectHelper.createProject(projectPage.getProjectName());
		IJavaProject javaProject = ProjectHelper.convertProjectToJava(project);

		// Create bin folder 
		IFolder binFolder = ProjectHelper.createFolder("bin", project);
		javaProject.setOutputLocation(binFolder.getFullPath(), null);

		// Create src folder and add classpath
		IFolder srcFolder = ProjectHelper.createFolder("src", project);
		IPackageFragmentRoot packageRoot = javaProject.getPackageFragmentRoot(srcFolder);
		IClasspathEntry srcClasspath = JavaCore.newSourceEntry(packageRoot.getPath());
		ProjectHelper.addClasspathEntries(javaProject, new IClasspathEntry[] {srcClasspath});

		// Create Main class
		createMainFile(javaProject, srcFolder);
		
		// Create Run Configuration
		ProjectHelper.createLaunchConfiguration(project, "Main");
	}
	
	private void createMainFile(IJavaProject javaProject, IFolder srcFolder) throws JavaModelException {
		IPackageFragment pack  = javaProject.getPackageFragmentRoot(srcFolder).createPackageFragment("", false, null);
		StringBuffer buffer = new StringBuffer();
		buffer.append("public class Main {\n");
		buffer.append("\tpublic static void main(String[] args) {\n");
		buffer.append("\t\tSystem.out.println(\"Hello World !!!\");\n");
		buffer.append("\t}\n");
		buffer.append("}\n");
		pack.createCompilationUnit("Main.java", buffer.toString(), false, null);
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
