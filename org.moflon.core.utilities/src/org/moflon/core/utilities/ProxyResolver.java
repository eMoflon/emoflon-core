package org.moflon.core.utilities;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

public class ProxyResolver {
	
	private static Map<String, URI> pack2genMapEnv = EcorePlugin.getEPackageNsURIToGenModelLocationMap(false);
	private static Map<String, URI> pack2genMapTarget = EcorePlugin.getEPackageNsURIToGenModelLocationMap(true);
	
	private static Map<URI, EClassifier> uri2class = new HashMap<>();

	private static Map<URI, EPackage> uri2packages = new HashMap<>();

	public static void resolveReference(EReference ref) {
		ref.setEType(resolve(ref.getEType()));
	}
	
	public static EClassifier resolve(EClassifier obj) {
		if(obj == null)
			return null;
		
		if(!((InternalEObject) obj).eIsProxy())
			return obj;
		
		URI uri = ((InternalEObject) obj).eProxyURI();
		
		// has it already been registered?
		if(uri2class.containsKey(uri))
			return uri2class.get(uri);
		
		// resolve containing package from URI
		EPackage pkg = resolvePackage(uri);
		if(pkg == null) {
			if(uri.toString().contains("/resource/")) {
				URI pluginURI = URI.createURI(uri.toString().replace("/resource/", "/plugin/"));
				pkg = resolvePackage(pluginURI);
				
				// we register uri in case that we found URI as this is to be expected for other elements that are unresolved
				uri2packages.put(uri, pkg);
			}
		}
		else {
			uri2packages.put(uri, pkg);
		}
		
		// explore package and register uris of all eclassifiers
		registerEClassifier(uri, pkg);
		
		// has it already been registered?
		if(uri2class.containsKey(uri))
			return uri2class.get(uri);
		
		// nothing to be done. cannot resolve it. blame EMF
		return obj;
	}
	
	private static void registerEClassifier(URI uri, EPackage pkg) {
		String packageUri = uri.toString().substring(0, uri.toString().indexOf("#")) + "#//";
		for(EClassifier ec : pkg.getEClassifiers()) {
			uri2class.put(URI.createURI(packageUri + ec.getName()), ec);
		}
	}

	private static String getTypeFromURI(URI uri) {
		return uri.toString().substring(uri.toString().lastIndexOf("/") + 1);
	}
	
	public static void fixBrokenEReferences(EPackage epackage) {
		epackage.getEClassifiers().stream().filter(c -> c instanceof EClass).forEach(c -> ((EClass) c).getEReferences().forEach(ProxyResolver::resolveReference));;
	}
	
	private static EPackage resolvePackage(URI uri) {
		if(uri2packages.containsKey(uri)) {
			return uri2packages.get(uri);
		}
		
		try {
			ResourceSet rs = new ResourceSetImpl();
			Resource r = rs.createResource(uri);
			r.load(null);
			if(r.isLoaded()) {
				return (EPackage) r.getContents().get(0);
			}
		}
		catch(Exception e) {
		}
		return null;
	}
}
