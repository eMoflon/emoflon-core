package org.moflon.core.build;

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
import org.moflon.core.utilities.WorkspaceHelper;
import org.moflon.core.utilities.eMoflonEMFUtil;
import org.moflon.emf.dependency.PackageRemappingDependency;

public class GenericMonitoredResourceLoader implements ITask
{
   private static final String TASK_NAME = "Resource loading";

   protected final ResourceSet resourceSet;

   protected final IFile file;

   private Resource resource;

   private List<Resource> resources;

   public GenericMonitoredResourceLoader(final ResourceSet resourceSet, final IFile file)
   {
      this.resourceSet = resourceSet;
      this.file = file;
   }

   @Override
   public final IStatus run(final IProgressMonitor monitor)
   {
      final IProject project = file.getProject();
      final SubMonitor subMon = SubMonitor.convert(monitor, "Loading metamodel for project " + project.getName(), 20);

      // Preprocess resource set
      final IStatus preprocessingStatus = preprocessResourceSet(subMon.split(5));
      if (preprocessingStatus.matches(IStatus.ERROR | IStatus.CANCEL))
      {
         return preprocessingStatus;
      }

      if (isValidProject(project))
      {
         // Load the file
         URI projectURI = URI.createPlatformResourceURI(project.getName() + "/", true);
         URI uri = URI.createURI(file.getProjectRelativePath().toString()).resolve(projectURI);
         try {
             resource = resourceSet.getResource(uri, true);
         } catch (final WrappedException e) {
             return new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(getClass()), IStatus.ERROR, e.getCause().getMessage(), e.getCause());
         }
         subMon.worked(5);
         if (subMon.isCanceled())
         {
            return Status.CANCEL_STATUS;
         }

         // Postprocess resource set
         final IStatus postprocessingStatus = postprocessResourceSet(subMon.split(5));
         if (postprocessingStatus.matches(IStatus.ERROR | IStatus.CANCEL))
         {
            return postprocessingStatus;
         }

         return eMoflonEMFUtil.validateResourceSet(resourceSet, TASK_NAME, subMon.split(5));
      } else
      {
         return new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(getClass()), "Project " + project.getName() + " is not accessible");
      }
   }

   public final List<Resource> getResources()
   {
      return resources;
   }

   public final Resource getMainResource()
   {
      return resource;
   }

   @Override
   public String getTaskName()
   {
      return TASK_NAME;
   }

   protected IStatus preprocessResourceSet(final IProgressMonitor monitor)
   {
      try
      {
         final SubMonitor subMon = SubMonitor.convert(monitor, "Preprocessing resource set", 15);
         // Prepare plugin to resource URI mapping
         eMoflonEMFUtil.createPluginToResourceMapping(resourceSet, subMon.split(5));
         if (subMon.isCanceled())
         {
            return Status.CANCEL_STATUS;
         }

         // Create (unloaded) resources for all possibly dependent metamodels in workspace projects
         createResourcesForWorkspaceProjects(subMon.split(10));
         if (subMon.isCanceled())
         {
            return Status.CANCEL_STATUS;
         }
         return Status.OK_STATUS;
      } catch (final CoreException e)
      {
         return new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(getClass()), e.getMessage(), e);
      }
   }

   protected IStatus postprocessResourceSet(final IProgressMonitor monitor)
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "Postprocessing resource set", 5);
      // Resolve cross-references
      final CrossReferenceResolver crossReferenceResolver = new CrossReferenceResolver(resource);
      crossReferenceResolver.run(subMon.split(5));
      resources = crossReferenceResolver.getResources();

      // Remove unloaded resources from resource set
      final List<Resource> resources = resourceSet.getResources();
      for (int i = 0; i < resources.size(); i++)
      {
         final Resource resource = resources.get(i);
         if (!resource.isLoaded())
         {
            resources.remove(i--);
         }
      }
      return Status.OK_STATUS;
   }

   protected void createResourcesForWorkspaceProjects(final IProgressMonitor monitor)
   {
      final IProject[] workspaceProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
      final SubMonitor subMon = SubMonitor.convert(monitor, "Loading workspace projects", workspaceProjects.length);
      for (final IProject workspaceProject : workspaceProjects)
      {
         if (isValidProject(workspaceProject))
         {
            final URI projectURI = eMoflonEMFUtil.lookupProjectURI(workspaceProject);
            final URI metamodelURI = eMoflonEMFUtil.getDefaultProjectRelativeEcoreFileURI(workspaceProject).resolve(projectURI);
            new PackageRemappingDependency(metamodelURI, false, false).getResource(resourceSet, false, true);
         }
         subMon.worked(1);
      }
   }

   /**
    * Returns true if the given project can be handled by the {@link GenericMonitoredResourceLoader}
    * @param project
    * @return
    */
   protected boolean isValidProject(final IProject project)
   {
      try
      {
         return project.isAccessible() && WorkspaceHelper.isMoflonProject(project);
      } catch (final CoreException e)
      {
         return false;
      }
   }
}
