package org.moflon.emf.codegen;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.moflon.core.propertycontainer.AdditionalDependencies;
import org.moflon.core.propertycontainer.AdditionalUsedGenPackages;
import org.moflon.core.propertycontainer.MoflonPropertiesContainer;
import org.moflon.core.utilities.MoflonConventions;
import org.moflon.core.utilities.eMoflonEMFUtil;
import org.moflon.emf.codegen.dependency.Dependency;
import org.moflon.emf.codegen.dependency.DependencyTypes;
import org.moflon.emf.codegen.dependency.SimpleDependency;

public class MoflonGenModelBuilder extends GenModelBuilder {
	protected static final Logger logger = Logger.getLogger(MoflonGenModelBuilder.class);

	// The model file used for code generation
	protected IFile ecoreFile;

	protected final URI ecoreURI;

	private final List<Resource> resources;

	// The directory containing model file
	private String modelDirectory;

	protected MoflonPropertiesContainer moflonProperties;

	private String basePackage;

	public MoflonGenModelBuilder(final ResourceSet resourceSet, final List<Resource> resources, final IFile ecoreFile,
			final String basePackage, final String modelDirectory, final MoflonPropertiesContainer moflonProperties) {
		super(resourceSet);
		this.resources = resources;
		this.ecoreFile = ecoreFile;
		this.basePackage = basePackage;
		this.modelDirectory = modelDirectory;
		this.moflonProperties = moflonProperties;

		final IProject project = ecoreFile.getProject();
		final URI projectURI = eMoflonEMFUtil.lookupProjectURIAsPlatformResource(project);
		this.ecoreURI = MoflonConventions.getDefaultProjectRelativeEcoreFileURI(project).resolve(projectURI);
	}

	@Override
	public boolean isNewGenModelRequired(final URI genModelURI) {
		return super.isNewGenModelRequired(genModelURI) || moflonProperties.getReplaceGenModel().isBool();
	}

	@Override
	public void loadDefaultGenModelContent(final GenModel genModel) {
		super.loadDefaultGenModelContent(genModel);
		genModel.getForeignModel().add(ecoreFile.getName());
		genModel.setModelDirectory(modelDirectory); // org.gervarro.democles.emoflon/src
		for (GenPackage genPackage : genModel.getGenPackages()) {
			genPackage.setBasePackage(basePackage);
		}

		// Enable generics
		genModel.setComplianceLevel(GenJDKLevel.JDK80_LITERAL);

		// Enable operation reflection
		genModel.setOperationReflection(true);

		// Set plugin id so conversion to plugin is available in editor
		genModel.setModelPluginID(ecoreFile.getProject().getName());
	}

	@Override
	public List<Dependency> getGenModelResourceDependencies() {
		BasicEList<Dependency> result = new BasicEList<Dependency>();

		// User-defined GenPackages
		for (final AdditionalUsedGenPackages usedDefinedGenPackage : moflonProperties.getAdditionalUsedGenPackages()) {
			result.add(new SimpleDependency(URI.createURI(usedDefinedGenPackage.getValue())));
		}

		for (final Resource resource : resources) {
			final URI uri = resource.getURI();
			if (!isAdditionalDependency(uri) && !uri.equals(ecoreURI)) {
				final int kind = DependencyTypes.getDependencyType(uri);
				if (kind == DependencyTypes.DEPLOYED_PLUGIN) {
					result.add(new SimpleDependency(lookupExtensionRegistry(uri)));
				} else if (kind == DependencyTypes.WORKSPACE_PLUGIN_PROJECT
						|| kind == DependencyTypes.WORKSPACE_PROJECT) {
					result.add(new SimpleDependency(calculateGenModelURI(uri)));
				}
			}
		}
		return result;
	}

	public static final URI calculateGenModelURI(final URI ecoreURI) {
		return ecoreURI.trimFileExtension().appendFileExtension(GENMODEL_FILE_EXTENSION);
	}

	private final boolean isAdditionalDependency(final URI uri) {
		for (AdditionalDependencies dependency : moflonProperties.getAdditionalDependencies()) {
			if (dependency.getValue().equals(uri.toString())) {
				return true;
			}
		}
		return false;
	}

	private final URI lookupExtensionRegistry(final URI uri) {
		assert uri.isPlatformPlugin() && uri.segmentCount() >= 2;
		final String uriString = uri.toString();
		final String pluginID = uri.segment(1);
		final IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
				.getExtensionPoint("org.eclipse.emf.ecore.generated_package");
		for (IExtension extension : extensionPoint.getExtensions()) {
			if (pluginID.equals(extension.getContributor().getName())) {
				for (IConfigurationElement config : extension.getConfigurationElements()) {
					if (uriString.equals(config.getAttribute("uri"))) {
						final String genModelAttribute = config.getAttribute("genModel");
						if (genModelAttribute != null) {
							final URI pluginURI = URI.createPlatformPluginURI(pluginID + "/", true);
							return URI.createURI(genModelAttribute).resolve(pluginURI);
						}
					}
				}
				extension.getConfigurationElements()[0].getAttribute("genModel");
			}
		}
		return calculateGenModelURI(uri);
	}
}
