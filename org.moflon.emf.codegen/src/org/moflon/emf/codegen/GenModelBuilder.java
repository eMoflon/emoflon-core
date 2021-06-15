package org.moflon.emf.codegen;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.importer.ecore.EcoreImporter;
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

	public GenModel buildGenModel(final URI genModelURI) throws Exception {
		if (isNewGenModelRequired(genModelURI)) {

			final URI ecoreURI = getEcoreURI(genModelURI);
			final Resource ecoreResource = resourceSet.getResource(ecoreURI, true);
			final Optional <EPackage> ePackageOpt = ecoreResource.getContents().stream()
				.filter(obj -> EcorePackage.eINSTANCE.getEPackage().isInstance(obj))
				.map(obj -> (EPackage)obj)
				.findFirst();
			
			if(!ePackageOpt.isPresent())
				throw new RuntimeException("No EPackage found at given URI: "+ecoreURI);
			
			final EPackage ePackage = ePackageOpt.get();
			
			Monitor monitor = BasicMonitor.toMonitor(new NullProgressMonitor());
			
			EcoreImporter importer = new EcoreImporter();
			importer.setModelLocation(ecoreURI.toString());
			importer.computeEPackages(monitor);
			importer.adjustEPackages(monitor);
			
			//Find the correct corresponding project name
			String projectName = Character.toUpperCase(ePackage.getName().charAt(0)) + ePackage.getName().substring(1, ePackage.getName().length());
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			for(IProject project : workspace.getRoot().getProjects()) {
				if(project.getName().equalsIgnoreCase(projectName) && !project.getName().equals(projectName)) {
					projectName = project.getName();
					break;
				}
				
				if(!project.getName().equalsIgnoreCase(projectName)) {
					String fileName = ePackage.eResource().getURI().lastSegment();
					IFile file = project.getFile("model/"+fileName);
					if(file.exists()) {
						projectName = project.getName();
						break;
					}
				}
			}
			
			
			for(EPackage ePack : importer.getEPackages()) {
				if(ePack.getName().equals(ePackage.getName())) {
					importer.getEPackageConvertInfo(ePack).setConvert(true);
				}else {
					importer.getEPackageConvertInfo(ePack).setConvert(false);
				}
			}			
			
			importer.setGenModelContainerPath(new Path(projectName).append("model"));
			importer.setGenModelFileName(importer.computeDefaultGenModelFileName());
			importer.prepareGenModelAndEPackages(monitor);

			GenModel genModel = importer.getGenModel();
			
			genModel.setModelDirectory(projectName + "/gen/");
			
		    Set<GenPackage> removals = genModel.getGenPackages().stream().filter(pkg -> !pkg.getEcorePackage().getName().equals(ePackage.getName())).collect(Collectors.toSet());
			removals.forEach(pkg -> genModel.getGenPackages().remove(pkg));
			
			
			Map<String, URI> pack2genMapEnv = EcorePlugin.getEPackageNsURIToGenModelLocationMap(false);
			Map<String, URI> pack2genMapTarget = EcorePlugin.getEPackageNsURIToGenModelLocationMap(true);

			Map<String, URI> uriName2PluginUri = new HashMap<>();
			for(URI uri : pack2genMapEnv.values()) {
				uriName2PluginUri.put(uri.toString(), uri);
			}
			Map<String, URI> uriName2ResourceUri = new HashMap<>();
			for(URI uri : pack2genMapTarget.values()) {
				uriName2ResourceUri.put(uri.toString(), uri);
			}
			
			// create dummy genmodels or else the genpackages can not be found and thus persisted
			for(GenPackage gp : removals) {
				// search first in environment in case that the genmodel is exported as plugin
				URI genURI = pack2genMapEnv.get(gp.getNSURI());
				if(genURI == null)
					genURI = pack2genMapTarget.get(gp.getNSURI());
				ResourceSet rs = new ResourceSetImpl();
				Resource createResource = rs.getResource(genURI, true);
				if(createResource.isLoaded()) {
					GenModel newGen = (GenModel) createResource.getContents().get(0);
					newGen.getGenPackages().clear();
					newGen.getGenPackages().add(gp);
					genModel.getUsedGenPackages().add(gp);
				}
				else {
					throw new RuntimeException("No GenPackage found at given URI: "+genURI);
				}
			}
			
			genModel.setGenerateSchema(true);
			genModel.setCanGenerate(true);
		    genModel.reconcile();
		    return genModel;
		} else {
			// Load GenModel
			final Resource genModelResource = resourceSet.getResource(genModelURI, true);
			GenModel genModel = (GenModel) genModelResource.getContents().get(0);
			genModel.reconcile();
			return genModel;
		}

		
	}


}
