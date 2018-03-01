package org.moflon.emf.codegen;

import java.util.Collections;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.moflon.core.utilities.MoflonUtil;
import org.moflon.emf.codegen.dependency.Dependency;
import org.moflon.emf.codegen.resource.GenModelResource;
import org.moflon.emf.codegen.resource.GenModelResourceFactory;

public class GenModelBuilder {
	public static final String ARCHIVE_SCHEME = "archive";

	public static final String SEGMENT_SEPARATOR = "/";

	public static final String ARCHIVE_SEPARATOR = "!" + SEGMENT_SEPARATOR;

	public static final String ECORE_FILE_EXTENSION = "ecore";

	public static final String GENMODEL_FILE_EXTENSION = "genmodel";

	protected static final Logger logger = Logger.getLogger(GenModelBuilder.class);

	// EMF specific part
	protected final ResourceSet resourceSet;

	public GenModelBuilder(final ResourceSet resourceSet) {
		this.resourceSet = resourceSet;
	}

	public boolean isNewGenModelRequired(final URI genModelURI) {
		return !resourceSet.getURIConverter().exists(genModelURI, null);
	}

	public void loadDefaultGenModelContent(final GenModel genModel) {
		genModel.setComplianceLevel(GenJDKLevel.JDK60_LITERAL);
		genModel.setModelName(genModel.eResource().getURI().trimFileExtension().lastSegment());
		genModel.setImporterID("org.eclipse.emf.importer.ecore");
		genModel.setCodeFormatting(true);
		genModel.setOperationReflection(true);
		genModel.setUpdateClasspath(false);
	}

	public Iterable<Dependency> getGenModelResourceDependencies() {
		return Collections.emptyList();
	}

	public void loadDefaultSettings() {
		resourceSet.getPackageRegistry().put("http://www.eclipse.org/emf/2002/GenModel",
				new StandalonePackageDescriptor("org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage"));

		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(GENMODEL_FILE_EXTENSION,
				new GenModelResourceFactory());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
	}

	protected URI getEcoreURI(final URI genModelURI) {
		return genModelURI.trimFileExtension().appendFileExtension("ecore");
	}

	public GenModel buildGenModel(final URI genModelURI) {
		GenModel genModel = null;

		if (isNewGenModelRequired(genModelURI)) {
			// Create new GenModel
			GenModelResource genModelResource = (GenModelResource) resourceSet.createResource(genModelURI);
			genModel = GenModelPackage.eINSTANCE.getGenModelFactory().createGenModel();
			genModelResource.getContents().add(genModel);

			adjustRegistry(genModel);

			loadDefaultGenModelContent(genModel);

			// Handle GenModel dependencies
			for (Dependency dependency : getGenModelResourceDependencies()) {
				Resource dependentGenModelResource = dependency.getResource(resourceSet, true);
				GenModel dependentGenModel = (GenModel) dependentGenModelResource.getContents().get(0);
				genModel.getUsedGenPackages().addAll(dependentGenModel.getGenPackages());
			}

			// Use Ecore model to create new GenModel
			URI ecoreURI = getEcoreURI(genModelURI);
			Resource ecoreResource = resourceSet.getResource(ecoreURI, true);

			// Add GenModel content
			LinkedList<EPackage> ePackages = new LinkedList<EPackage>();
			for (EObject eObject : ecoreResource.getContents()) {
				if (EcorePackage.eINSTANCE.getEPackage().isInstance(eObject)) {
					ePackages.add((EPackage) eObject);
				}
			}
			genModel.initialize(ePackages);

			for (GenPackage genPackage : genModel.getGenPackages()) {
				setDefaultPackagePrefixes(genPackage);
			}
		} else {
			// Handle GenModel dependencies
			for (Dependency dependency : getGenModelResourceDependencies()) {
				dependency.getResource(resourceSet, false);
			}
			// Load GenModel
			Resource genModelResource = resourceSet.getResource(genModelURI, true);
			genModel = (GenModel) genModelResource.getContents().get(0);
			genModel.reconcile();
		}

		return genModel;
	}

	private void setDefaultPackagePrefixes(final GenPackage genPackage) {
		genPackage.setPrefix(MoflonUtil.lastCapitalizedSegmentOf(genPackage.getPrefix()));
		for (GenPackage subPackage : genPackage.getSubGenPackages()) {
			setDefaultPackagePrefixes(subPackage);
		}
	}

	protected void adjustRegistry(final GenModel genModel) {
		// Ugly hack added by gervarro: GenModel has to be screwed
		EPackage.Registry registry = resourceSet.getPackageRegistry();
		resourceSet.setPackageRegistry(new EPackageRegistryImpl(registry));
		genModel.getExtendedMetaData();
		resourceSet.setPackageRegistry(registry);
	}

	public static final URI createArchiveURI(final String archiveFileURI) {
		StringBuilder builder = new StringBuilder(ARCHIVE_SCHEME);
		builder.append(':');
		builder.append(archiveFileURI);
		builder.append(ARCHIVE_SEPARATOR);
		return URI.createURI(builder.toString());
	}

	public static final URI createArchiveURI(final URI archiveFileURI) {
		return createArchiveURI(archiveFileURI.toString());
	}
}
