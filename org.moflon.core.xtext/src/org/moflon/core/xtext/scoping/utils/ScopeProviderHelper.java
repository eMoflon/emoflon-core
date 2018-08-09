package org.moflon.core.xtext.scoping.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import org.moflon.core.xtext.exceptions.CannotFindScopeException;
import org.moflon.core.xtext.utils.ResourceUtil;

public class ScopeProviderHelper <E extends EObject> {
	private Map<URI, E> existingScopingRoots;
	private Map<String, List<EObject>> oldCandidates;
	private ResourceSet resourceSet;

	public ScopeProviderHelper(ResourceSet resSet) {
		init();
		resourceSet = resSet;
	}

	public ScopeProviderHelper() {
		init();
		resourceSet = ResourceUtil.getResourceSet("ecore");
	}

	private void init(){
		existingScopingRoots = new HashMap<>();
		oldCandidates = new HashMap<>();
	}

	public ResourceSet getResourceSet(){
		return resourceSet;
	}

	private E getScopingObject(URI uri, Class<E> clazz) throws IOException{
		if(existingScopingRoots.containsKey(uri)){
			return existingScopingRoots.get(uri);
		}else{
			Resource res=resourceSet.getResource(uri, false);
			if(res == null){
			   res= resourceSet.createResource(uri);
			}
			res.load(Collections.EMPTY_MAP);
			E scopingRoot = clazz.cast(res.getContents().get(0));
			existingScopingRoots.put(uri, scopingRoot);
			return scopingRoot;
		}
	}

	public IScope createScope(List<URI> uris, Class<E> clazz, Class<? extends EObject> type) throws CannotFindScopeException{
	     return createScope(uris, clazz, type, null);
	}

	public <T extends EObject> IScope createScope(List<URI> uris, Class<E> clazz, Class<T> type, List<T> currentFound) throws CannotFindScopeException{
		try {
			List<EObject> candidates=null;
			if(oldCandidates.containsKey(type.toGenericString())){
				candidates=oldCandidates.get(type.toGenericString());
			}
			else {
				candidates = new ArrayList<>();

				for(URI uri : uris){
					E scopingObject=getScopingObject(uri, clazz);
					Iterator<EObject> candidateIterator = scopingObject.eAllContents();
					while(candidateIterator.hasNext()){
						EObject candidate = candidateIterator.next();
						if(type.isAssignableFrom(candidate.getClass()))
							candidates.add(candidate);
					}
				}
				oldCandidates.put(type.toGenericString(), candidates);
			}
			if (currentFound != null){
			   candidates.addAll(currentFound);
			}
			return Scopes.scopeFor(candidates);
		}catch (Exception ioobe){
			throw new CannotFindScopeException("Cannot find Resource");
		}
	}
}
