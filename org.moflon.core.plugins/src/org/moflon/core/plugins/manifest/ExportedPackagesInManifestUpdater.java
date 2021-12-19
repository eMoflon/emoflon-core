package org.moflon.core.plugins.manifest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.gervarro.eclipse.workspace.util.WorkspaceTask;
import org.moflon.core.plugins.PluginProperties;
import org.moflon.core.plugins.manifest.ManifestFileUpdater.AttributeUpdatePolicy;
import org.moflon.core.utilities.WorkspaceHelper;

public class ExportedPackagesInManifestUpdater extends WorkspaceTask {

	public static final String EXPORT_PACKAGE_KEY = "Export-Package";

	public static final Name EXPORT_PACKAGE = new Attributes.Name(EXPORT_PACKAGE_KEY);

	private IProject project;

	private GenModel genModel;
	
	/**
	 * flag that shows if emf builder is smart emf or regular emf. If smart emf is built, then there are extra dependencies and less packages to export
	 */
	private boolean isSmartEMF;

	private ExportedPackagesInManifestUpdater(final IProject project, final GenModel genModel) {
		this.project = project;
		this.genModel = genModel;
		this.isSmartEMF = true;
	}

	public static final void updateExportedPackageInManifest(final IProject project, final GenModel genModel)
			throws CoreException {
		final ExportedPackagesInManifestUpdater manifestUpdater = new ExportedPackagesInManifestUpdater(project,
				genModel);
		WorkspaceTask.executeInCurrentThread(manifestUpdater, IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
		
		manifestUpdater.updateManifestForSmartEMF();
	}
	
	@Override
	public void run(final IProgressMonitor monitor) throws CoreException {
		final SubMonitor subMon = SubMonitor.convert(monitor, "Update exported packages extension", 1);
		new ManifestFileUpdater().processManifest(project, manifest -> {
			return updateExportedPackages(manifest);
		});
		subMon.worked(1);
	}
	/**
	 * changes MANIFEST.MF for smartemf code generation module
	 */
	public void updateManifestForSmartEMF() {
		
		if(isSmartEMF) {		
			//change Manifest
			ManifestFileUpdater manifestFileBuilder = new ManifestFileUpdater();
			
			try {
				manifestFileBuilder.processManifest(project, manifest -> {
					boolean changed = false;
					//if model generator is smart emf, then extra dependencies(emfcodegenerator) are necessary
	
					changed |= ManifestFileUpdater.updateDependencies(manifest, Arrays
							.asList(new String[] {"emfcodegenerator"}));
					//exported packages are only "model" and "model".impl; so the .util package needs to be removed
					String atr = (String) manifest.getMainAttributes().get(PluginManifestConstants.EXPORT_PACKAGE);
					List<String> exportsList = ManifestFileUpdater
							.extractDependencies(atr);
					exportsList.removeIf(s -> s.endsWith(".util"));
					changed |= ManifestFileUpdater.changeExports(manifest, exportsList);
					return changed;
				});
			} catch (CoreException e) {
				//TODO: Exception handling
			}
		
		}		
	}
	
	private boolean updateExportedPackages(final Manifest manifest) {
		// Check and update basic settings
		boolean changed = ManifestFileUpdater.setBasicProperties(manifest, project.getName());
		
		String exportedPackageString = (String) manifest.getMainAttributes().get(EXPORT_PACKAGE);
		List<String> exportedPackages = new ArrayList<>();
		if (exportedPackageString != null && !exportedPackageString.isEmpty()) {
			exportedPackages.addAll(Arrays.asList(exportedPackageString.split(",")));
		}
		Set<String> newPackages = new HashSet<>(getExportPackage());
		newPackages.removeAll(exportedPackages);
		if (newPackages.isEmpty() && !changed) {
			// No update necessary
			return false;
		} else if(newPackages.isEmpty() && changed) {
			return true;
		}

		exportedPackages.addAll(newPackages);

		String exportedPackagesString = exportedPackages.stream().collect(Collectors.joining(","));
		if (!exportedPackagesString.isEmpty()) {
			manifest.getMainAttributes().put(EXPORT_PACKAGE, exportedPackagesString);
		} else {
			manifest.getMainAttributes().remove(EXPORT_PACKAGE);
		}
		
		return true;
	}

	private List<String> getExportPackage() {
		final List<String> exportedPackages = new ArrayList<>();
		genModel.getAllGenPackagesWithClassifiers().forEach(genPackage -> {
			String interfacePackageName = genPackage.getInterfacePackageName();
			String utilitiesPackageName = genPackage.getUtilitiesPackageName();
			String classPackageName = genPackage.getClassPackageName();

			// Fixes strange behavior that these names are sometimes null
			if (interfacePackageName != null && !interfacePackageName.startsWith("null."))
				exportedPackages.add(interfacePackageName);
			if (utilitiesPackageName != null && !utilitiesPackageName.startsWith("null."))
				exportedPackages.add(utilitiesPackageName);
			if (classPackageName != null && !classPackageName.startsWith("null."))
				exportedPackages.add(classPackageName);
		});
		return exportedPackages;
	}

	@Override
	public String getTaskName() {
		return "Manifest file export package updater";
	}

	@Override
	public final ISchedulingRule getRule() {
		final IFile manifestFile = ManifestFileUpdater.getManifestFile(project);
		return manifestFile.exists() ? manifestFile : manifestFile.getParent();
	}
}
