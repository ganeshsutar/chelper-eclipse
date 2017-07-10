package chelpereclipse.wizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.operation.IRunnableWithProgress;

import chelpereclipse.util.ProjectHelper;

public class ProjectCreator implements IRunnableWithProgress {
	private String projectName;
	
	public ProjectCreator(String projectName) {
		this.projectName = projectName;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		try {
			prepareProject(monitor);
		} catch (CoreException | URISyntaxException | IOException ex) {
			ex.printStackTrace();
		}
	}

	private void prepareProject(IProgressMonitor monitor) throws CoreException, URISyntaxException, IOException {
		// Create Project
		monitor.beginTask("Creating project", 6);
		monitor.internalWorked(1);
		IProject project = ProjectHelper.createProject(projectName);
		IJavaProject javaProject = ProjectHelper.convertProjectToJava(project);

		// Create bin folder 
		monitor.internalWorked(1);
		IFolder binFolder = ProjectHelper.createFolder("bin", project);
		javaProject.setOutputLocation(binFolder.getFullPath(), null);

		// Create src folder and add classpath
		monitor.internalWorked(1);
		IFolder srcFolder = ProjectHelper.createFolder("src", project);
		IPackageFragmentRoot packageRoot = javaProject.getPackageFragmentRoot(srcFolder);
		IClasspathEntry srcClasspath = JavaCore.newSourceEntry(packageRoot.getPath());
		ProjectHelper.addClasspathEntries(javaProject, new IClasspathEntry[] {srcClasspath});

		// Create Main class
		monitor.internalWorked(1);
		String content = ProjectHelper.readFile("resources/Main.template");
		ICompilationUnit mainClass =  ProjectHelper.createFile(javaProject, srcFolder, "Main.java", "", content);
		ProjectHelper.openCompilationUnit(mainClass);
		
		// Create Run Configuration
		monitor.internalWorked(1);
		ProjectHelper.createLaunchConfiguration(project, "Main");
		
		monitor.internalWorked(1);
	}

}
