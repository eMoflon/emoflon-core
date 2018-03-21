package org.moflon.emf.build;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.gervarro.eclipse.task.ITask;
import org.moflon.core.build.CrossReferenceResolver;
import org.moflon.core.utilities.MoflonConventions;
import org.moflon.core.utilities.WorkspaceHelper;
import org.moflon.core.utilities.eMoflonEMFUtil;
import org.moflon.emf.codegen.dependency.PackageRemappingDependency;

/**
 * Generic resource-loading process
 *
 * See {@link #run(IProgressMonitor)} for more details
 *
 * @author Gergely Varró - Initial implementation
 * @author Roland Kluge - Documentation, refactoring, increase reusability
 */
public class GenericMonitoredResourceLoader implements ITask {
	private final ResourceSet resourceSet;

	protected final IFile file;

	private Resource resource;

	private List<Resource> resources;

	/**
	 * Initializes this loader with the target {@link ResourceSet} and the file from
	 * where the {@link Resource} should be loaded
	 * 
	 * @param resourceSet
	 *            the target {@link ResourceSet}
	 * @param file
	 *            the file to be loaded
	 */
	public GenericMonitoredResourceLoader(final ResourceSet resourceSet, final IFile file) {
		this.resourceSet = resourceSet;
		this.file = file;
	}

	@Override
	public String getTaskName() {
		return "Resource loading";
	}

	/**
	 * Performs the following steps (i) preprocessing, (ii) load resource from
	 * {@link #getFile()}, (iii) postprocessing
	 * 
	 * @param monitor
	 *            the progress monitor
	 * @return the success status
	 */
	@Override
	public final IStatus run(final IProgressMonitor monitor) {
		final IProject project = this.getFile().getProject();
		final SubMonitor subMon = SubMonitor.convert(monitor, "Loading metamodel for project " + project.getName(), 20);

		final IStatus preprocessingStatus = preprocessResourceSet(subMon.split(5));
		if (preprocessingStatus.matches(IStatus.ERROR | IStatus.CANCEL)) {
			return preprocessingStatus;
		}

		if (isValidProject(project)) {
			final URI projectURI = URI.createPlatformResourceURI(project.getName() + "/", true);
			final URI uri = URI.createURI(getFile().getProjectRelativePath().toString()).resolve(projectURI);
			try {
				this.resource = this.getResourceSet().getResource(uri, true);
			} catch (final WrappedException e) {
				return new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(getClass()), IStatus.ERROR,
						e.getCause().getMessage(), e.getCause());
			}
			subMon.worked(5);
			if (subMon.isCanceled()) {
				return Status.CANCEL_STATUS;
			}

			final IStatus postprocessingStatus = postprocessResourceSet(subMon.split(5));
			if (postprocessingStatus.matches(IStatus.ERROR | IStatus.CANCEL)) {
				return postprocessingStatus;
			}

			return eMoflonEMFUtil.validateResourceSet(resourceSet, getTaskName(), subMon.split(5));
		} else {
			return new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(getClass()),
					String.format("Project %s is not accessible", project.getName()));
		}
	}

	/**
	 * The target resource set
	 * 
	 * @return the file
	 */
	public final ResourceSet getResourceSet() {
		return resourceSet;
	}

	/**
	 * The file containing the resource to be loaded
	 * 
	 * @return the file
	 */
	public final IFile getFile() {
		return file;
	}

	/**
	 * Returns the list of all resolved resources in the resource set after a
	 * successfull {@link #run(IProgressMonitor)}
	 * 
	 * @return all loaded resources
	 */
	public final List<Resource> getResources() {
		return resources;
	}

	/**
	 * Returns the resource that was loaded from {@link #getFile()} (if
	 * {@link #run(IProgressMonitor)} was successfull
	 * 
	 * @return the main resource
	 */
	public final Resource getMainResource() {
		return resource;
	}

	/**
	 * This method is invoked before trying to load the actual resource
	 * 
	 * @param monitor
	 *            the progress monitor
	 * @return the preprocessing status
	 */
	protected IStatus preprocessResourceSet(final IProgressMonitor monitor) {
		try {
			final SubMonitor subMon = SubMonitor.convert(monitor, "Preprocessing resource set", 15);
			// Prepare plugin to resource URI mapping
			eMoflonEMFUtil.createPluginToResourceMapping(resourceSet, subMon.split(5));
			if (subMon.isCanceled()) {
				return Status.CANCEL_STATUS;
			}

			// Create (unloaded) resources for all possibly dependent metamodels in
			// workspace projects
			createResourcesForWorkspaceProjects(subMon.split(10));
			if (subMon.isCanceled()) {
				return Status.CANCEL_STATUS;
			}

			return Status.OK_STATUS;
		} catch (final CoreException e) {
			return new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(getClass()), e.getMessage(), e);
		}
	}

	/**
	 * This method is invoked after loading the actual resource
	 * 
	 * @param monitor
	 *            the progress monitor
	 * @return the postprocessing status
	 */
	protected IStatus postprocessResourceSet(final IProgressMonitor monitor) {
		final SubMonitor subMon = SubMonitor.convert(monitor, "Postprocessing resource set", 5);
		// Resolve cross-references
		final CrossReferenceResolver crossReferenceResolver = new CrossReferenceResolver(resource);
		crossReferenceResolver.run(subMon.split(5));
		this.resources = crossReferenceResolver.getResources();

		// Remove unloaded resources from resource set
		final List<Resource> resources = getResourceSet().getResources();
		for (int i = 0; i < resources.size(); i++) {
			final Resource resource = resources.get(i);
			if (!resource.isLoaded()) {
				resources.remove(i--);
			}
		}
		return Status.OK_STATUS;
	}

	protected void createResourcesForWorkspaceProjects(final IProgressMonitor monitor) {
		final IProject[] workspaceProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		final SubMonitor subMon = SubMonitor.convert(monitor, "Loading workspace projects", workspaceProjects.length);
		for (final IProject workspaceProject : workspaceProjects) {
			if (isValidProject(workspaceProject)) {
				//TODO@aanjorin: The following statement does not work with emoflon-tool (TGG compilation fails)
				// final URI projectURI = eMoflonEMFUtil.lookupProjectURIAsPlatformResource(workspaceProject);
				final URI projectURI = eMoflonEMFUtil.lookupProjectURI(workspaceProject);
				final URI metamodelURI = MoflonConventions.getDefaultProjectRelativeEcoreFileURI(workspaceProject)
						.resolve(projectURI);
				new PackageRemappingDependency(metamodelURI, false, false).getResource(this.resourceSet, false, true);
			}
			subMon.worked(1);
		}
	}

	/**
	 * Returns true if the given project can be handled by the
	 * {@link GenericMonitoredResourceLoader}
	 * 
	 * @param project
	 *            the project to check
	 * @return true iff the project can be handled
	 */
	protected boolean isValidProject(final IProject project) {
		return project.isAccessible() && MoflonConventions.getDefaultEcoreFile(project).exists();
	}
}
