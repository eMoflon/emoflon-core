package org.moflon.core.build;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.moflon.core.propertycontainer.AdditionalDependencies;
import org.moflon.core.propertycontainer.MoflonPropertiesContainer;
import org.moflon.core.utilities.WorkspaceHelper;
import org.moflon.emf.dependency.PackageRemappingDependency;

public class MonitoredMetamodelLoader extends GenericMonitoredResourceLoader
{
   private static final String TASK_NAME = "Metamodel loading";

   private final MoflonPropertiesContainer moflonProperties;

   public MonitoredMetamodelLoader(final ResourceSet resourceSet, final IFile ecoreFile, final MoflonPropertiesContainer moflonProperties)
   {
      super(resourceSet, ecoreFile);
      this.moflonProperties = moflonProperties;
   }

   @Override
   public String getTaskName()
   {
      return TASK_NAME;
   }

   protected IStatus preprocessResourceSet(final IProgressMonitor monitor)
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "Preprocessing resource set", 40);
      final IStatus preprocessingStatus = super.preprocessResourceSet(subMon.split(15));
      if (preprocessingStatus.matches(IStatus.ERROR | IStatus.CANCEL))
      {
         return preprocessingStatus;
      }

      // Always load Ecore metamodel
      PackageRemappingDependency ecoreMetamodelDependency = new PackageRemappingDependency(
            URI.createURI("platform:/plugin/org.eclipse.emf.ecore/model/Ecore.ecore"), true, true);
      ecoreMetamodelDependency.getResource(resourceSet, true);
      subMon.worked(5);
      if (subMon.isCanceled())
      {
         return Status.CANCEL_STATUS;
      }

      // Create resources for the user-defined dependent metamodels
      final List<Resource> resourcesToLoad = createResourcesForUserDefinedMetamodels(subMon.split(10));
      if (subMon.isCanceled())
      {
         return Status.CANCEL_STATUS;
      }

      // Load the user-defined dependent metamodels
      final IStatus userDefinedMetamodelLoaderStatus = loadUserDefinedMetamodels(resourcesToLoad, subMon.split(10));
      if (!userDefinedMetamodelLoaderStatus.isOK())
      {
         return userDefinedMetamodelLoaderStatus;
      }
      if (subMon.isCanceled())
      {
         return Status.CANCEL_STATUS;
      }
      return preprocessingStatus;
   }

   private final List<Resource> createResourcesForUserDefinedMetamodels(final IProgressMonitor monitor)
   {
      final List<Resource> resourcesToLoad = new LinkedList<Resource>();
      final List<AdditionalDependencies> additionalDependencies = moflonProperties.getAdditionalDependencies();

      final SubMonitor subMon = SubMonitor.convert(monitor, "Creating resources for user-defined metamodels", additionalDependencies.size());
      for (final AdditionalDependencies userDefinedMetamodel : additionalDependencies)
      {
         final URI uri = URI.createURI(userDefinedMetamodel.getValue());
         final PackageRemappingDependency dependency = new PackageRemappingDependency(uri, true, false);
         resourcesToLoad.add(dependency.getResource(resourceSet, false));
         subMon.split(1);
      }
      return resourcesToLoad;
   }

   private final IStatus loadUserDefinedMetamodels(final List<Resource> resourcesToLoad, final IProgressMonitor monitor)
   {
      final MultiStatus resourceLoadingStatus = new MultiStatus(WorkspaceHelper.getPluginId(getClass()), IStatus.OK, "Resource loading status", null);
      final SubMonitor subMon = SubMonitor.convert(monitor, "Loading user-defined metamodels", resourcesToLoad.size());
      for (Resource resource : resourcesToLoad)
      {
         try
         {
            resource.load(null);
         } catch (final IOException e)
         {
            resourceLoadingStatus.add(new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(getClass()), IStatus.ERROR, e.getMessage(), e));
         }
         subMon.worked(1);
      }
      return resourceLoadingStatus;
   }
}
