package org.moflon.smartemf.runtime;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EAttributeImpl;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EReferenceImpl;

public abstract class SmartPackageImpl extends EPackageImpl implements SmartPackage {
	
	private Map<EClass, Set<EStructuralFeature>> dynamicFeatures = new HashMap<>();
	private Map<EClass, Set<EClass>> subClassesInPackage = new HashMap<>();
	

	public SmartPackageImpl(final String nsUri, final EFactory factory) {
		super(nsUri, factory);
	}
	
	@Override
	public EStructuralFeature insertNewFeature(final EClass eClass, final EStructuralFeature eFeature) {
		if(!eContents().parallelStream().filter(obj -> (obj instanceof EClass)).filter(ecls -> eClass.equals(ecls)).findAny().isPresent()) {
			throw new RuntimeException("EClass <"+eClass+"> is not present in the package <"+this+">");
		}
		
		if(eFeature instanceof EReference) {
			EReferenceImpl createdERef = (EReferenceImpl)ecoreFactory.createEReference();
			createdERef.setFeatureID(eClass.getEStructuralFeatures().size());
			eClass.getEStructuralFeatures().add(createdERef);
			
			initEReference(createdERef, eFeature.getEType(), ((EReference) eFeature).getEOpposite(), eFeature.getName(), eFeature.getDefaultValueLiteral(), 
					eFeature.getLowerBound(), eFeature.getUpperBound(), eClass.getInstanceClass(), eFeature.isTransient(), eFeature.isVolatile(), eFeature.isChangeable(), 
					((EReference) eFeature).isContainment(), ((EReference) eFeature).isResolveProxies(), eFeature.isUnsettable(), eFeature.isUnique(), eFeature.isDerived(), 
					eFeature.isOrdered());
			
			insertClass2Feature(eClass, createdERef);
			getSubClassesInPackage(eClass).forEach(subClass -> {
				insertClass2Feature(subClass, createdERef);
			});
			
			return createdERef;
		} else if(eFeature instanceof EAttribute) {
			EAttributeImpl createdEAtr = (EAttributeImpl)ecoreFactory.createEAttribute();
			createdEAtr.setFeatureID(eClass.getEStructuralFeatures().size());
			eClass.getEStructuralFeatures().add(createdEAtr);
			
			initEAttribute(createdEAtr, ((EAttribute)eFeature).getEType(), eFeature.getName(), eFeature.getDefaultValueLiteral(), eFeature.getLowerBound(), eFeature.getUpperBound(),
					eClass.getInstanceClass(), createdEAtr.isTransient(), createdEAtr.isVolatile(), createdEAtr.isChangeable(), createdEAtr.isUnsettable(), createdEAtr.isID(), createdEAtr.isUnique(),
					createdEAtr.isDerived(), createdEAtr.isOrdered());
			
			insertClass2Feature(eClass, createdEAtr);
			getSubClassesInPackage(eClass).forEach(subClass -> {
				insertClass2Feature(subClass, createdEAtr);
			});
			
			return createdEAtr;
		} else {
			throw new RuntimeException("Unsupported EStructuralFeature type: "+eFeature);
		}

	}
	
	@Override
	public boolean isDynamicEStructuralFeature(final EClass eClass, final EStructuralFeature eFeature) {
		if(!dynamicFeatures.containsKey(eClass))
			return false;
		
		return dynamicFeatures.get(eClass).contains(eFeature);
		
	}
	
	@Override
	public boolean hasDynamicEStructuralFeatures(final EClass eClass) {
		if(!dynamicFeatures.containsKey(eClass))
			return false;
		
		return true;
	}
	
	@Override
	public Collection<EStructuralFeature> getDynamicEStructuralFeatures(final EClass eClass) {
		if(!dynamicFeatures.containsKey(eClass))
			return new HashSet<>();
		
		return dynamicFeatures.get(eClass);
	}
	
	protected void injectExternalReferences() {
		Collection<EReference> externalRefs = getExternalReferences();
		for(EReference ref : externalRefs) {
			if(! (ref.getEType().getEPackage() instanceof SmartPackage))
				continue;
			
			SmartPackage foreignPackage = (SmartPackage)ref.getEType().getEPackage();
			
			EReferenceImpl inverse = (EReferenceImpl)ecoreFactory.createEReference();
			inverse.setName(ref.getName()+"_inverseTo_"+getName());
			inverse.setEType(ref.getEContainingClass());
			inverse.setEOpposite(ref);
			inverse.setLowerBound(0);
			inverse.setUpperBound(-1);
			inverse.setTransient(ref.isTransient());
			inverse.setVolatile(ref.isVolatile());
			inverse.setChangeable(ref.isChangeable());
			inverse.setContainment(false);
			inverse.setResolveProxies(ref.isResolveProxies());
			inverse.setUnsettable(ref.isUnsettable());
			inverse.setUnique(ref.isUnique());
			inverse.setDerived(ref.isDerived());
			inverse.setOrdered(ref.isOrdered());
			
			EReference trueInverse = (EReference) foreignPackage.insertNewFeature((EClass) ref.getEType(), inverse);
			ref.setEOpposite(trueInverse);
		}
	}
	
	protected Collection<EReference> getExternalReferences() {
		Set<EClass> ownClasses = eContents().parallelStream().filter(obj->(obj instanceof EClass)).map(ecls -> (EClass)ecls).collect(Collectors.toSet());
		// Find all references that point to EClasses not defined in this package.
		return eContents().parallelStream()
			.filter(obj->(obj instanceof EClass))
			.map(ecls -> (EClass)ecls)
			.flatMap(ecls -> ecls.getEAllStructuralFeatures().parallelStream())
			.filter(ref -> (ref instanceof EReference))
			.map(ref -> (EReference) ref)
			.filter(ref -> !ownClasses.contains(ref.getEType()))
			.collect(Collectors.toSet());
	}
	
	private void insertClass2Feature(final EClass eClass, final EStructuralFeature eFeature) {
		Set<EStructuralFeature> currentFeatures = dynamicFeatures.get(eClass);
		if(currentFeatures == null) {
			currentFeatures = new HashSet<>();
			dynamicFeatures.put(eClass, currentFeatures);
		}
		currentFeatures.add(eFeature);
	}
	
	protected Set<EClass> getSubClassesInPackage(final EClass eClass) {
		if(subClassesInPackage.containsKey(eClass))
			return subClassesInPackage.get(eClass);
		
		Set<EClass> subClasses = new HashSet<>();
		subClassesInPackage.put(eClass, subClasses);
		
		for(EClass someClass : eContents().stream().filter(obj->(obj instanceof EClass)).map(ecls -> (EClass)ecls).filter(ecls -> ecls != eClass).collect(Collectors.toSet())) {
			if(someClass.getEAllSuperTypes().contains(eClass)) {
				subClasses.add(someClass);
			}
		}
		return subClasses;
	}

}
