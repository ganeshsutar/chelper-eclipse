package chelpereclipse.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.osgi.framework.Bundle;

public class ProjectHelper {
	public static final String BUNDLE_NAME = "CHelperEclipse";
	
	public static IProject createProject(String name) throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(name);
		project.create(null);
		project.open(null);
		createDescription(project);
		return project;
	}

	private static void createDescription(IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] {JavaCore.NATURE_ID});
		project.setDescription(description, null);
	}
	
	public static IJavaProject convertProjectToJava(IProject project) {
		return JavaCore.create(project);
	}
	
	public static IFolder createFolder(String name, IProject project) throws CoreException {
		IFolder folder = project.getFolder(name);
		folder.create(true, true, null);
		return folder;
	}
	
	public static void addClasspathEntries(IJavaProject javaProject, IClasspathEntry[] extraEntries) throws JavaModelException {
		List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
		entries.addAll(Arrays.asList(extraEntries));

		IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
		LibraryLocation[] locations = JavaRuntime.getLibraryLocations(vmInstall);
		for(LibraryLocation location : locations) {
			entries.add(JavaCore.newLibraryEntry(location.getSystemLibraryPath(), null, null));
		}
		javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);
	}

	public static void createLaunchConfiguration(IProject project, String mainClass) throws CoreException {
		ILaunchManager mgr = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType javaLaunchType = mgr.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
		ILaunchConfigurationWorkingCopy wc = javaLaunchType.newInstance(project, "Run-" + project.getName());
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, project.getName());
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, mainClass);
		ILaunchConfiguration lc = wc.doSave();
		lc.launch(ILaunchManager.DEBUG_MODE, null);
	}
	
	public static String readFile(String filename) throws URISyntaxException, IOException {
		Bundle bundle = Platform.getBundle(BUNDLE_NAME);
		URL fileUrl = bundle.getEntry(filename);
		File file = new File(FileLocator.resolve(fileUrl).toURI());
		return String.join("\n", Files.readAllLines(file.toPath()));
	}
	
	public static void createMainFile(IJavaProject javaProject, String classFileName, String packageName, String templateFilename, IFolder srcFolder) throws JavaModelException, URISyntaxException, IOException {
		IPackageFragment pack  = javaProject.getPackageFragmentRoot(srcFolder).createPackageFragment(packageName, false, null);
		String content = readFile(templateFilename);
		pack.createCompilationUnit(classFileName, content, false, null);
	}

}
