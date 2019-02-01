package org.moflon.emf.codegen;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.moflon.core.preferences.PlatformUriType;
import org.moflon.core.propertycontainer.MoflonPropertiesContainer;
import org.moflon.core.utilities.ExtensionsUtil;
import org.moflon.core.utilities.MoflonConventions;
import org.moflon.core.utilities.eMoflonEMFUtil;

public class MoflonGenModelBuilder extends GenModelBuilder {
	protected static final Logger logger = Logger.getLogger(MoflonGenModelBuilder.class);

	private static final String URI_PREF_EXTENSION_ID = "org.moflon.emf.codegen.URIPreferenceExtension";

	private static Collection<URIPreferenceExtension> uriPreferenceExtension = ExtensionsUtil
			.collectExtensions(URI_PREF_EXTENSION_ID, "class", URIPreferenceExtension.class);

	// The model file used for code generation
	protected IFile ecoreFile;

	protected final URI ecoreURI;

	// The directory containing model file
	private final String modelDirectory;

	protected MoflonPropertiesContainer moflonProperties;

	private final String basePackage;

	public MoflonGenModelBuilder(final ResourceSet resourceSet, final List<Resource> resources, final IFile ecoreFile,
			final String basePackage, final String modelDirectory, final MoflonPropertiesContainer moflonProperties) {
		super(resourceSet);
		this.ecoreFile = ecoreFile;
		this.basePackage = basePackage;
		this.modelDirectory = modelDirectory;
		this.moflonProperties = moflonProperties;

		final IProject project = ecoreFile.getProject();

		final URI projectURI = determineProjectUriBasedOnPreferences(project);
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
		for (final GenPackage genPackage : genModel.getGenPackages()) {
			genPackage.setBasePackage(basePackage);
		}

		// Enable generics
		genModel.setComplianceLevel(GenJDKLevel.JDK80_LITERAL);

		// Enable operation reflection
		genModel.setOperationReflection(true);

		// Set plugin id so conversion to plugin is available in editor
		genModel.setModelPluginID(ecoreFile.getProject().getName());
	}

	/**
	 * Returns a platform:/ {@link URI} for the given project based on the visible
	 * {@link URIPreferenceExtension}s
	 * 
	 * If multiple extensions exist, the preference of one extension is taken
	 * nondeterministically.
	 * 
	 * @param project
	 *                    the project
	 * @return the corresponding {@link URI}
	 */
	public static URI determineProjectUriBasedOnPreferences(final IProject project) {
		final PlatformUriType preferredGenModelPlatformUriType = uriPreferenceExtension.stream()//
				.map(URIPreferenceExtension::getPlatformURIType)//
				.findAny()//
				.orElse(PlatformUriType.DEFAULT);
		final URI projectURI = determineProjectUriBasedOnPlatformUriType(project, preferredGenModelPlatformUriType);
		return projectURI;
	}

	/**
	 * Returns a platform:/ {@link URI} for the given project based on the given
	 * {@link PlatformUriType}
	 * 
	 * @param project
	 *                            the project
	 * @param platformUriType
	 *                            the {@link PlatformUriType}
	 * @return the corresponding {@link URI}
	 */
	private static URI determineProjectUriBasedOnPlatformUriType(final IProject project,
			final PlatformUriType platformUriType) {
		final URI projectURI;
		switch (platformUriType) {
		case RESOURCE:
			projectURI = eMoflonEMFUtil.lookupProjectURIAsPlatformResource(project);
			break;
		case PLUGIN:
		default:
			projectURI = eMoflonEMFUtil.lookupProjectURI(project);
			break;
		}
		return projectURI;
	}

	public static final URI calculateGenModelURI(final URI ecoreURI) {
		return ecoreURI.trimFileExtension().appendFileExtension(GENMODEL_FILE_EXTENSION);
	}
}
