package org.moflon.core.utilities;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class ProxyResolver {
	
	private static Map<String, URI> pack2genMapEnv = EcorePlugin.getEPackageNsURIToGenModelLocationMap(false);
	private static Map<String, URI> pack2genMapTarget = EcorePlugin.getEPackageNsURIToGenModelLocationMap(true);
	
	private static Map<URI, EClassifier> uri2class = new HashMap<>();

	private static Map<URI, EPackage> uri2packages = new HashMap<>();
	
	// one resource set, which loads all ecores to resolve all proxies
	private static ResourceSet rs = new ResourceSetImpl();


	public static void resolveReference(EReference ref) {
		ref.setEType(resolve(ref.getEType()));
	}
	
	public static EClassifier resolve(EClassifier obj) {
		if(obj == null)
			return null;
		
		if(!((InternalEObject) obj).eIsProxy())
			return obj;
		
		URI classURI = ((InternalEObject) obj).eProxyURI();
		URI uri = ((InternalEObject) obj).eProxyURI().trimFragment();
		
		// has it already been registered?
		if(uri2class.containsKey(classURI))
			return uri2class.get(classURI);
		
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
		if(uri2class.containsKey(classURI))
			return uri2class.get(classURI);
		
		// nothing to be done. cannot resolve it. blame EMF
		return obj;
	}
	
	private static void registerEClassifier(URI uri, EPackage pkg) {
		String packageUri = uri.toString() + "#//";
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
	
	public static EPackage resolvePackage(URI uri) {
		URI otherURI = null;
		
		if(uri.toString().contains("/resource/")) {
			otherURI = URI.createURI(uri.toString().replace("/resource/", "/plugin/"));
		}
		if(uri.toString().contains("/plugin/")) {
			otherURI = URI.createURI(uri.toString().replace("/plugin/", "/resource/"));
		}
		
		// first execute with original uri than with the other
		EPackage epkg = resolvePackageInternal(uri);
		if(epkg == null) {
			epkg = resolvePackageInternal(otherURI);
			if(epkg != null) {
				rs.getURIConverter().getURIMap().put(uri, otherURI);
			}
		}
		return epkg;
	}

	public static EPackage resolvePackageInternal(URI uri) {
		if(uri2packages.containsKey(uri)) {
			return uri2packages.get(uri);
		}
		
		EPackage epkg = Registry.INSTANCE.getEPackage(uri.toString());
		if(epkg == null) {
			try {
				Resource r = rs.createResource(uri);
				r.load(null);
				if(r.isLoaded()) {
					epkg = (EPackage) r.getContents().get(0);
					uri2packages.put(uri, epkg);
				}
			}
			catch(Exception e) {
			}
		}
		if(epkg != null)
			resolveProxies(epkg);

		return epkg;
	}

	private static void resolveProxies(EPackage epkg) {
		EObject proxy = null;
		do {
			EObject newProxy = findProxy(epkg);
			EcoreUtil.resolveAll(epkg);
			if(newProxy == null)
				return;
			
			if(newProxy.equals(proxy)) 
				throw new RuntimeException("Could not resolve proxy for " + newProxy);
			
			if(newProxy instanceof EClass) {
				EObject obj = ProxyResolver.resolve((EClass) newProxy);
			}
			else {
				throw new RuntimeException("Could not resolve " + newProxy);
			}
			EcoreUtil.resolveAll(epkg);
			proxy = newProxy;
		} while(proxy.eIsProxy());
	}
	
	private static EObject findProxy(EPackage epkg) {
		if(epkg.eIsProxy())
			return epkg;
		
		for(EPackage subPkg : epkg.getESubpackages()) {
			EObject proxy = findProxy(subPkg);
			if(proxy != null)
				return proxy;
		}
		
		// check if type is a proxy or any subtype, references or attribute types
		for(EClassifier classifier : epkg.getEClassifiers()) {
			if(classifier instanceof EClass eClass) {
				if(eClass.eIsProxy()) 
					return eClass;
				
				for(EClass subClass : eClass.getEAllSuperTypes()) {
					if(subClass.eIsProxy()) 
						return subClass;
				}
				
				for(EReference ref : eClass.getEAllReferences()) {
					if(ref.getEType().eIsProxy())
						return ref.getEType();
				}
				
				for(EAttribute attr : eClass.getEAllAttributes()) {
					if(attr.getEType().eIsProxy())
						return attr.getEType();
				}
			}
		}
		return null;
	}
}

