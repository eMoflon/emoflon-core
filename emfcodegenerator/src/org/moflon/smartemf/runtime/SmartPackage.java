package org.moflon.smartemf.runtime;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

public interface SmartPackage extends EPackage {
	
	public EStructuralFeature insertNewFeature(final EClass eClass, final EStructuralFeature eFeature);

}
