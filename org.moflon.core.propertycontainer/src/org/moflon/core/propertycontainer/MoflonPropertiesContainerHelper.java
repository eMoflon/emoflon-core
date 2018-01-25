package org.moflon.core.propertycontainer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.moflon.core.utilities.LogUtils;
import org.moflon.core.utilities.WorkspaceHelper;
import org.moflon.core.utilities.eMoflonEMFUtil;
import org.moflon.util.plugins.xml.XMLUtils;
import org.w3c.dom.Document;

public class MoflonPropertiesContainerHelper
{
   public static final String MOFLON_CONFIG_FILE = "moflon.properties.xmi";

   /**
    * This string is used as a placeholder for the correct metamodel name
    */
   public static final String UNDEFINED_METAMODEL_NAME = "NO_META_MODEL_PROJECT_NAME_SET_YET";

   private static final Logger logger = Logger.getLogger(MoflonPropertiesContainerHelper.class);

   // This is the list of XML element tagnames that are no longer supported
   private static final List<String> OBSOLETE_TAGNAMES = Arrays.asList("buildFilter", "core", "debugMode", "genSdmRpCoverageInstrumentation",
         "genTracingInstrumentation", "injectionErrorHandling", "listShuffling", "skipValidation", "strictSDMConditionalBranching");


   /**
    * Loads the eMoflon properties of the given project.
    *
    * @param project
    * @param monitor
    * @return the properties. Is never null.
    */
   public static MoflonPropertiesContainer load(final IProject project, final IProgressMonitor monitor)
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "Load properties.", 1);

      removeObsoleteTags(project);

      final MoflonPropertiesContainer container = loadOrCreatePropertiesContainer(project, project.getFile(MOFLON_CONFIG_FILE));
      final String projectName = project.getName();
      checkForMissingDefaults(container);

      // The TGG build mode is currently set during checkForMissingDefaults, where we cannot distinguish between TGG and
      // SDM projects
      //      if (!IntegrationNature.isIntegrationProjectNoThrow(project))
      //      {
      //         container.setTGGBuildMode(null);
      //      }

      if (!projectName.equals(container.getProjectName()))
      {
         LogUtils.warn(logger, "Project name in Moflon properties file ('%s') does not match Project. Setting correct project name to '%s'.",
               container.getProjectName(), projectName);
         container.setProjectName(projectName);
      }

      MoflonPropertiesContainerHelper.save(container, subMon.split(1));
      return container;
   }

   private static void removeObsoleteTags(final IProject project)
   {
      IFile propertiesFile = project.getFile(MOFLON_CONFIG_FILE);
      try
      {
         final InputStream inputStream = propertiesFile.getContents();
         final String content = IOUtils.toString(inputStream);
         IOUtils.closeQuietly(inputStream);
         // Make sure that we need to do anything at all
         if (OBSOLETE_TAGNAMES.stream().anyMatch(tagname -> {
            return content.contains(tagname.toString());
         }))
         {
            final Document doc = XMLUtils.parseXmlDocument(content);

            for (final String obsoleteTagname : OBSOLETE_TAGNAMES)
            {
               removeAllChildrenByTagname(doc, obsoleteTagname);
            }

            String newContent = XMLUtils.formatXmlString(doc, new NullProgressMonitor());
            if (!newContent.equals(content))
            {
               propertiesFile.setContents(new ByteArrayInputStream(newContent.getBytes()), true, true, new NullProgressMonitor());
            }
         }
      } catch (IOException | CoreException e)
      {
         LogUtils.error(logger, "Failed to remove obsolete tags from %s. Reason: %s", propertiesFile, WorkspaceHelper.printStacktraceToString(e));
      }
   }

   private static void removeAllChildrenByTagname(final Document doc, final String tagname)
   {
      for (int i = 0; i < doc.getElementsByTagName(tagname).getLength(); ++i)
         doc.getDocumentElement().removeChild(doc.getElementsByTagName(tagname).item(i));
   }

   private static MoflonPropertiesContainer loadOrCreatePropertiesContainer(final IProject project, final IFile propertyFile)
   {
      MoflonPropertiesContainer moflonPropertiesCont;
      if (propertyFile.exists())
      {
         PropertycontainerFactory.eINSTANCE.getClass();
         moflonPropertiesCont = (MoflonPropertiesContainer) eMoflonEMFUtil.getResourceFromFileIntoDefaultResourceSet(propertyFile).getContents().get(0);

      } else
      {
         LogUtils.error(logger,
               "Moflon property file '%s' not found in project ''. Generating default properties file. Unable to set MetamodelProject. Has to be fixed manually",
               propertyFile, project.getName());
         moflonPropertiesCont = PropertycontainerFactory.eINSTANCE.createMoflonPropertiesContainer();

      }
      return moflonPropertiesCont;
   }

   public static MoflonPropertiesContainer createDefaultPropertiesContainer(final String projectName, final String metaModelProjectName)
   {
      MoflonPropertiesContainer container = PropertycontainerFactory.eINSTANCE.createMoflonPropertiesContainer();
      container.setProjectName(projectName);

      updateMetamodelProjectName(container, metaModelProjectName);
      checkForMissingDefaults(container);

      return container;
   }

   public static void save(final MoflonPropertiesContainer properties, final IProgressMonitor monitor)
   {
      try
      {
         final SubMonitor subMon = SubMonitor.convert(monitor, "Saving eMoflon properties", 1);
         final IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
         final IProject project = workspace.getProject(properties.getProjectName());
         if (project == null)
         {
            LogUtils.error(logger, "Unable to save property file '%s' for project '%s'.", MOFLON_CONFIG_FILE, properties.getProjectName());
         } else
         {
            final IFile projectFile = project.getFile(MOFLON_CONFIG_FILE);
            final ResourceSet set = eMoflonEMFUtil.createDefaultResourceSet();
            final URI fileURI = eMoflonEMFUtil.createFileURI(projectFile.getLocation().toString(), false);
            final Resource resource = set.createResource(fileURI);
            resource.getContents().add(normalize(properties));

            final HashMap<String, String> saveOptions = new HashMap<String, String>();
            saveOptions.put(Resource.OPTION_LINE_DELIMITER, WorkspaceHelper.DEFAULT_RESOURCE_LINE_DELIMITER);
            resource.save(saveOptions);

            projectFile.refreshLocal(IResource.DEPTH_ZERO, subMon.split(1));
         }
      } catch (final Exception e)
      {
         LogUtils.error(logger, "Unable to save property file '%s' for project '%s':\n %s", MOFLON_CONFIG_FILE, properties.getProjectName(),
               WorkspaceHelper.printStacktraceToString(e));
      }

   }

   /**
    * This method sets the {@link MetaModelProject} of the given {@link MoflonPropertiesContainer} to the given value
    */
   public static void updateMetamodelProjectName(final MoflonPropertiesContainer propertiesContainer, final String metamodelProjectName)
   {
      MetaModelProject metamodelProject = propertiesContainer.getMetaModelProject();
      if (metamodelProject == null)
      {
         metamodelProject = PropertycontainerFactory.eINSTANCE.createMetaModelProject();
         propertiesContainer.setMetaModelProject(metamodelProject);
         metamodelProject.setMetaModelProjectName(metamodelProjectName);
      }

      metamodelProject.setMetaModelProjectName(metamodelProjectName);
   }

   /**
    * Adds the minimal set of properties to a {@link MoflonPropertiesContainer}
    */
   private static void checkForMissingDefaults(final MoflonPropertiesContainer propertiesContainer)
   {
      final PropertycontainerFactory factory = PropertycontainerFactory.eINSTANCE;
      if (propertiesContainer.getReplaceGenModel() == null)
      {
         propertiesContainer.setReplaceGenModel(factory.createReplaceGenModel());
      }

      if (propertiesContainer.getSdmCodegeneratorHandlerId() == null)
      {
         propertiesContainer.setSdmCodegeneratorHandlerId(factory.createSdmCodegeneratorMethodBodyHandler());
      }

      if (propertiesContainer.getTGGBuildMode() == null)
      {
         propertiesContainer.setTGGBuildMode(factory.createTGGBuildMode());
      }

      if (propertiesContainer.getMetaModelProject() == null) {
         final MetaModelProject metaModelProject = factory.createMetaModelProject();
         propertiesContainer.setMetaModelProject(metaModelProject);
         metaModelProject.setMetaModelProjectName(UNDEFINED_METAMODEL_NAME);
      }
   }

   private static EObject normalize(final MoflonPropertiesContainer properties)
   {
      // Normalize properties to avoid unnecessary nondeterminism
      List<Dependencies> sortedDependencies = new ArrayList<>(properties.getDependencies());
      sortedDependencies.sort((d1, d2) -> d1.getValue().compareTo(d2.getValue()));
      properties.getDependencies().clear();
      properties.getDependencies().addAll(sortedDependencies);

      return properties;
   }

   public static Map<String, String> mappingsToMap(final List<? extends PropertiesMapping> mappings)
   {
      Map<String, String> map = new HashMap<String, String>();

      for (PropertiesMapping mapping : mappings)
         map.put(mapping.getKey(), mapping.getValue());

      return map;
   }

   public static Collection<String> mapToValues(final Collection<? extends PropertiesValue> values)
   {
      List<String> list = new LinkedList<String>();
      for (PropertiesValue value : values)
         list.add(value.getValue());
      return list;
   }

   public static MoflonPropertiesContainer createEmptyContainer()
   {
      return PropertycontainerFactory.eINSTANCE.createMoflonPropertiesContainer();
   }

   /**
    * Returns the code generator configured in moflon.properties.xmi
    */
   public static final String getMethodBodyHandler(final MoflonPropertiesContainer moflonProperties)
   {
      SDMCodeGeneratorIds handlerId = moflonProperties.getSdmCodegeneratorHandlerId().getValue();
      return handlerId.getLiteral();
   }

}