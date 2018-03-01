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
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.gervarro.eclipse.workspace.util.WorkspaceTask;

public class ExportedPackagesInManifestUpdater extends WorkspaceTask {

	public static final String EXPORT_PACKAGE_KEY = "Export-Package";

	public static final Name EXPORT_PACKAGE = new Attributes.Name(EXPORT_PACKAGE_KEY);

	private IProject project;

	private GenModel genModel;

	private ExportedPackagesInManifestUpdater(final IProject project, final GenModel genModel) {
		this.project = project;
		this.genModel = genModel;
	}

	public static final void updateExportedPackageInManifest(final IProject project, final GenModel genModel)
			throws CoreException {
		final ExportedPackagesInManifestUpdater manifestUpdater = new ExportedPackagesInManifestUpdater(project,
				genModel);
		WorkspaceTask.executeInCurrentThread(manifestUpdater, IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
	}

	@Override
	public void run(final IProgressMonitor monitor) throws CoreException {
		final SubMonitor subMon = SubMonitor.convert(monitor, "Update exported packages extension", 1);
		new ManifestFileUpdater().processManifest(project, manifest -> {
			return updateExportedPackages(manifest);
		});
		subMon.worked(1);
	}

	private boolean updateExportedPackages(final Manifest manifest) {
		String exportedPackageString = (String) manifest.getMainAttributes().get(EXPORT_PACKAGE);
		List<String> exportedPackages = new ArrayList<>();
		if (exportedPackageString != null && !exportedPackageString.isEmpty()) {
			exportedPackages.addAll(Arrays.asList(exportedPackageString.split(",")));
		}
		Set<String> newPackages = new HashSet<>(getExportPackage());
		newPackages.removeAll(exportedPackages);
		if (newPackages.isEmpty()) {
			// No update necessary
			return false;
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
