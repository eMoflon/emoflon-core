package org.emoflon.smartemf.runtime;

import java.util.Collection;
import java.util.function.BiConsumer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

public interface SmartPackage extends EPackage {
	
	public EStructuralFeature insertNewFeature(final EClass eClass, final EStructuralFeature eFeature);
	
	public boolean isDynamicEStructuralFeature(final EClass eClass, final EStructuralFeature eFeature);
	
	public boolean hasDynamicEStructuralFeatures(final EClass eClass);
	
	public Collection<EStructuralFeature> getDynamicEStructuralFeatures(final EClass eClass);
	
	public void registerDynamicFeatureUpdateCallback(final EClass eClass, final BiConsumer<EClass, EStructuralFeature> callback);

}
