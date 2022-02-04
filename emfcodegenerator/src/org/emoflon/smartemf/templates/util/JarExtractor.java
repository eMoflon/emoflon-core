package org.emoflon.smartemf.templates.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class JarExtractor {
	
	private Map<String, File> name2genModelFiles = new HashMap<>();
    private Map<String, File> name2ecoreModelFiles = new HashMap<>();

    private String tempPath;
    private String path;
    
    public JarExtractor(String tempPath, String path) {
    	this.tempPath = tempPath;
    	this.path = path;
    	
    	GenModelPackage.eINSTANCE.getName();
        EcorePackage.eINSTANCE.getName();
    }

	public Collection<GenModel> extractGenModels() throws IOException {
		Collection<GenModel> genModels = new LinkedList<>();
		
		// read jar file and extract files
		readJarFile(path);
		
		// add all genmodels
		for(String fileName : name2genModelFiles.keySet()) {
			GenModel gen = getGenModel(name2genModelFiles.get(fileName));	
			genModels.add(gen);
		}
		
		// clean up all created files
		name2genModelFiles.values().forEach(File::delete);
		name2ecoreModelFiles.values().forEach(File::delete);
		
		return genModels;
	}
	
	public String readJarFile(String jarFilePath) {
	    try {
	        ZipFile zipFile = new ZipFile(jarFilePath);
	        Enumeration<? extends ZipEntry> e = zipFile.entries();
	        
	        while (e.hasMoreElements()) {
	            ZipEntry entry = (ZipEntry) e.nextElement();
	            // if the entry is not directory and matches relative file then extract it
	            if (!entry.isDirectory()) {
	                BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
	                
	                // Read the file
	                String entryName = entry.getName();
	                if(entryName.endsWith(".genmodel") || entryName.endsWith(".ecore")) {
	                	File jarFile = new File(entryName);
	                	
	                	String fullFileName = jarFile.getName();
	                	String[] fileNameSplit = fullFileName.split("[.]");
	                	String fileName = fileNameSplit[0];
	                	String extensionName = fileNameSplit[1];
	                	
	                	File newFile = new File(tempPath+File.separator+fullFileName);
	                	newFile.createNewFile();
	                	copyInputStreamToFile(bis, newFile);
	                	
	                	if(extensionName.equals("genmodel")) {
	                		name2genModelFiles.put(fileName, newFile);
	                	}
	                	if(extensionName.equals("ecore")) {
	                		name2ecoreModelFiles.put(fileName, newFile);
	                	}
	                }
	                bis.close();
	            } else {
	                continue;
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	public static GenModel getGenModel(File genModelFile) throws IOException {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("genmodel", new XMIResourceFactoryImpl());
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new XMIResourceFactoryImpl());
    	Resource createResource = rs.createResource(URI.createFileURI(genModelFile.getAbsolutePath()));
    	createResource.load(null);
    	if(createResource.getContents().get(0) instanceof GenModel gen) {
    		// tricking emf into loading the ecore
    		GenPackage genPackage = gen.getGenPackages().get(0);
    		genPackage.getEcorePackage();
    		return gen;
    	}
    	throw new RuntimeException("Genmodel not found in " + genModelFile.getAbsolutePath());
	}
	
	public static void copyInputStreamToFile(InputStream input, File file) {  
	    try (OutputStream output = new FileOutputStream(file)) {
	        input.transferTo(output);
	    } catch (IOException ioException) {
	        ioException.printStackTrace();
	    }
	}
}
