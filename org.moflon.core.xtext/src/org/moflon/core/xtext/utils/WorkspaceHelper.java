package org.moflon.core.xtext.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

public class WorkspaceHelper {

	public final static WorkspaceHelper INSTANCE = new WorkspaceHelper();
	
	private final String BIN = "bin";
	private final String SRC = "src";
	private final String SRC_GEN = "src-gen";
	private WorkspaceHelper() {
		
	}
	
	/**
	 * Returns the list of all projects in the workspace
	 */
	public List<IProject> getAllProjectsInWorkspace() {
		return Arrays.asList(ResourcesPlugin.getWorkspace().getRoot().getProjects());
	}
	
	public boolean projectExist(String projectName) {
		return getProjectMonad(projectName).isPresent();
	}
	
	public IProject createEmptyProject(String projectName) throws CoreException {
		IProgressMonitor progressMonitor = new NullProgressMonitor();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		project.create(progressMonitor);
		project.open(progressMonitor);
		
		return project;
	}
	
	public IProject getProjectByName(String projectName) {
		Optional<IProject> monad = getProjectMonad(projectName);
		return monad.isPresent()? monad.get() : null;
	}
	
	public IResource getIResource(URI uri) throws CoreException {
		IProject project = getProjectByURI(uri);
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		IPath path = getPathByURI(project, uri);
		if(path.toFile().isDirectory())
			return project.getFolder(path);
		else	
			return project.getFile(path);			
	}
	
	public IPath getPathByURI(IResource iResource, URI uri) {
		String[] segments = uri.segments();
		IPath path = iResource.getLocation();
		for(int index = 2; index < segments.length; ++index) {
			String segment = segments[index];
			path = path.append(segment);			
		}
		return path;
	}
	
	public IProject getProjectByURI(URI uri) {
		String projectName = ResourceUtil.getInstance().getProjectNameFromURI(uri);
		return getProjectByName(projectName);
	}
	
	public IProject getProjectByResource(Resource resource) {
		return getProjectByURI(resource.getURI());
	}
	
	private Optional<IProject> getProjectMonad(String projectName){
		return getAllProjectsInWorkspace().parallelStream().filter(project -> projectName.equalsIgnoreCase(project.getName())).findFirst();
	}
	
	public void addNature(IProject project, String natureID) throws CoreException {
		IProjectDescription description = project.getDescription();

		String[] natures = description.getNatureIds();
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = natureID;

		// validate the natures
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IStatus status = workspace.validateNatureSet(newNatures);

		// only apply new nature, if the status is ok
		if (status.getCode() == IStatus.OK) {
		    description.setNatureIds(newNatures);
		    project.setDescription(description, null);
		}
	}
	
	public IFolder getSrcFolder(IProject project) {
		return project.getFolder(SRC);
	}
	
	public IFolder getBinFolder(IProject project) {
		return project.getFolder(BIN);
	}
	
	public IFolder getSrcGenFolder(IProject project) {
		return project.getFolder(SRC_GEN);
	}
	
	public IFolder getSubFolderFromQualifiedName(IFolder folder, String qualifiedName) {
		List<String> parts = Arrays.asList(qualifiedName.split("\\."));
		IFolder current = folder;
		IFolder parent;
		for(String part : parts) {
			parent = current;
			current = parent.getFolder(part);
			if(!current.exists()) {
				return parent;
			}
		}
		return current;
	}
	
}
