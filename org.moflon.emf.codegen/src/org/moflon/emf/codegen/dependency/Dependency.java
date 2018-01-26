package org.moflon.emf.codegen.dependency;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

public interface Dependency {
   public Resource getResource(ResourceSet resourceSet, boolean loadContent);
}
