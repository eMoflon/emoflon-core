package org.moflon.core.propertycontainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.moflon.core.utilities.LogUtils;
import org.moflon.core.utilities.MoflonConventions;
import org.moflon.core.utilities.WorkspaceHelper;
import org.moflon.core.utilities.eMoflonEMFUtil;

public class MoflonPropertiesContainerHelper {
	/**
	 * This string is used as a placeholder for the correct metamodel name
	 */
	public static final String UNDEFINED_METAMODEL_NAME = "NO_META_MODEL_PROJECT_NAME_SET_YET";

	private static final Logger logger = Logger.getLogger(MoflonPropertiesContainerHelper.class);

	private IProject project;

	private IProgressMonitor monitor;

	private MoflonPropertiesContainer container;
	
	public MoflonPropertiesContainerHelper(final IProject project, final IProgressMonitor monitor) {
		this.project = project;
		this.monitor = monitor;
		load();
	}
	
	/**
	 * Loads the eMoflon properties of the given project. If a properties file does
	 * not exist yet, it will be created.
	 *
	 * @param project
	 * @param monitor
	 * @return the properties. Is never null.
	 */
	public MoflonPropertiesContainer load() {
		loadOrCreatePropertiesContainer(project, MoflonConventions.getDefaultMoflonPropertiesFile(project));

		save();
		return container;
	}

	/**
	 * Loads the eMoflon properties of the given project. If a properties file does
	 * not exist, the returned Optional will be empty.
	 * 
	 * @param project
	 *                    the project
	 * @return an Optional for the properties
	 */
	private Optional<MoflonPropertiesContainer> loadIfExists(final IProject project) {
		return Optional.of(MoflonConventions.getDefaultMoflonPropertiesFile(project)) //
				.filter(f -> f.exists()) //
				.map(f -> loadPropertiesContainer(f));
	}


	private void loadOrCreatePropertiesContainer(final IProject project,
			final IFile propertyFile) {
		if (propertyFile.exists()) {
			PropertycontainerFactory.eINSTANCE.getClass();
			container = loadPropertiesContainer(propertyFile);
		} else {
			container = createDefaultPropertiesContainer(project);
			save();
		}
	}

	private MoflonPropertiesContainer loadPropertiesContainer(final IFile propertyFile) {
		final Resource resource = eMoflonEMFUtil.getResourceFromFileIntoDefaultResourceSet(propertyFile);
		return (MoflonPropertiesContainer) resource.getContents().get(0);
	}

	private MoflonPropertiesContainer createDefaultPropertiesContainer(final IProject project) {
		container = PropertycontainerFactory.eINSTANCE.createMoflonPropertiesContainer();
		container.setCodeGenerator(PropertycontainerFactory.eINSTANCE.createCodeGenerator());
		container.getCodeGenerator().setGenerator(UsedCodeGen.SMART_EMF);
		container.setProjectName(project.getName());
		container.setReplaceGenModel(PropertycontainerFactory.eINSTANCE.createReplaceGenModel());
		return container;
	}

	/**
	 * Saves the Moflon properties at the default path (see
	 * {@link MoflonConventions#getDefaultMoflonPropertiesFile(IProject)}
	 * 
	 * @param properties
	 *                       the properties to save
	 * @param monitor
	 *                       the progress monitor to report to
	 */
	public void save() {
		try {
			final SubMonitor subMon = SubMonitor.convert(monitor, "Saving eMoflon properties", 1);
			if (project == null) {
				LogUtils.error(logger, "Unable to save property file '%s' for project '%s'.",
						MoflonConventions.MOFLON_CONFIG_FILE, project.getName());
			} else {
				final IFile projectFile = project.getFile(MoflonConventions.MOFLON_CONFIG_FILE);
				final ResourceSet set = eMoflonEMFUtil.createDefaultResourceSet();
				final URI fileURI = eMoflonEMFUtil.createFileURI(projectFile.getLocation().toString(), false);
				final Resource resource = set.createResource(fileURI);
				resource.getContents().add(container);

				final HashMap<String, String> saveOptions = new HashMap<String, String>();
				saveOptions.put(Resource.OPTION_LINE_DELIMITER, WorkspaceHelper.DEFAULT_RESOURCE_LINE_DELIMITER);
				resource.save(saveOptions);

				projectFile.refreshLocal(IResource.DEPTH_ZERO, subMon.split(1));
			}
		} catch (final Exception e) {
			LogUtils.error(logger, "Unable to save property file '%s' for project '%s':\n %s",
					MoflonConventions.MOFLON_CONFIG_FILE, project.getName(),
					WorkspaceHelper.printStacktraceToString(e));
		}

	}

	public MoflonPropertiesContainer createEmptyContainer() {
		return PropertycontainerFactory.eINSTANCE.createMoflonPropertiesContainer();
	}
	
	public Map<String, String> mappingsToMap() {
		final Map<String, String> map = new HashMap<String, String>();

		for (final PropertiesMapping mapping : container.getImportMappings())
			map.put(mapping.getKey(), mapping.getValue());

		return map;
	}
}