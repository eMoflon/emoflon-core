package org.emoflon.smartemf.runtime;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
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
	private Map<EClass, Set<BiConsumer<EClass, EStructuralFeature>>> foreignSubClassCallbacks = new HashMap<>();

	public SmartPackageImpl(final String nsUri, final EFactory factory) {
		super(nsUri, factory);
	}

	@Override
	public EStructuralFeature insertNewFeature(final EClass eClass, final EStructuralFeature eFeature) {
		if (!eContents().parallelStream().filter(obj -> (obj instanceof EClass)).filter(ecls -> eClass.equals(ecls))
				.findAny().isPresent()) {
			throw new RuntimeException("EClass <" + eClass + "> is not present in the package <" + this + ">");
		}

		if (eFeature instanceof EReference) {
			EReferenceImpl createdERef = (EReferenceImpl) ecoreFactory.createEReference();
			createdERef.setFeatureID(eClass.getEStructuralFeatures().size());
			eClass.getEStructuralFeatures().add(createdERef);

			initEReference(createdERef, eFeature.getEType(), ((EReference) eFeature).getEOpposite(), eFeature.getName(),
					eFeature.getDefaultValueLiteral(), eFeature.getLowerBound(), eFeature.getUpperBound(),
					eClass.getInstanceClass(), eFeature.isTransient(), eFeature.isVolatile(), eFeature.isChangeable(),
					((EReference) eFeature).isContainment(), ((EReference) eFeature).isResolveProxies(),
					eFeature.isUnsettable(), eFeature.isUnique(), eFeature.isDerived(), eFeature.isOrdered());

			insertClass2Feature(eClass, createdERef);
			getSubClassesInPackage(eClass).forEach(subClass -> {
				insertClass2Feature(subClass, createdERef);
			});

			if (foreignSubClassCallbacks.containsKey(eClass)) {
				foreignSubClassCallbacks.get(eClass).forEach(callback -> callback.accept(eClass, createdERef));
			}

			return createdERef;
		} else if (eFeature instanceof EAttribute) {
			EAttributeImpl createdEAtr = (EAttributeImpl) ecoreFactory.createEAttribute();
			createdEAtr.setFeatureID(eClass.getEStructuralFeatures().size());
			eClass.getEStructuralFeatures().add(createdEAtr);

			initEAttribute(createdEAtr, ((EAttribute) eFeature).getEType(), eFeature.getName(),
					eFeature.getDefaultValueLiteral(), eFeature.getLowerBound(), eFeature.getUpperBound(),
					eClass.getInstanceClass(), createdEAtr.isTransient(), createdEAtr.isVolatile(),
					createdEAtr.isChangeable(), createdEAtr.isUnsettable(), createdEAtr.isID(), createdEAtr.isUnique(),
					createdEAtr.isDerived(), createdEAtr.isOrdered());

			insertClass2Feature(eClass, createdEAtr);
			getSubClassesInPackage(eClass).forEach(subClass -> {
				insertClass2Feature(subClass, createdEAtr);
			});

			if (foreignSubClassCallbacks.containsKey(eClass)) {
				foreignSubClassCallbacks.get(eClass).forEach(callback -> callback.accept(eClass, createdEAtr));
			}

			return createdEAtr;
		} else {
			throw new RuntimeException("Unsupported EStructuralFeature type: " + eFeature);
		}

	}

	@Override
	public boolean isDynamicEStructuralFeature(final EClass eClass, final EStructuralFeature eFeature) {
		if (!dynamicFeatures.containsKey(eClass))
			return false;

		return dynamicFeatures.get(eClass).contains(eFeature);

	}

	@Override
	public boolean hasDynamicEStructuralFeatures(final EClass eClass) {
		if (!dynamicFeatures.containsKey(eClass))
			return false;

		return true;
	}

	@Override
	public Collection<EStructuralFeature> getDynamicEStructuralFeatures(final EClass eClass) {
		if (!dynamicFeatures.containsKey(eClass))
			return new HashSet<>();

		return dynamicFeatures.get(eClass);
	}

	@Override
	public void registerDynamicFeatureUpdateCallback(final EClass eClass,
			final BiConsumer<EClass, EStructuralFeature> callback) {
		Set<BiConsumer<EClass, EStructuralFeature>> currentCallbacks = foreignSubClassCallbacks.get(eClass);
		if (currentCallbacks == null) {
			currentCallbacks = new HashSet<>();
			foreignSubClassCallbacks.put(eClass, currentCallbacks);
		}
		currentCallbacks.add(callback);
	}

	protected void injectExternalReferences() {
		// Insert own features
		Collection<EReference> externalRefs = getExternalUnidirectionalReferences();
		for (EReference ref : externalRefs) {
			if (!(ref.getEType().getEPackage() instanceof SmartPackage))
				continue;

			SmartPackage foreignPackage = (SmartPackage) ref.getEType().getEPackage();

			EReferenceImpl inverse = (EReferenceImpl) ecoreFactory.createEReference();
			inverse.setName(ref.getName() + "_inverseTo_" + getName());
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

	protected void injectDynamicOpposites() {
		// Insert own features
		Collection<EReference> internalRefs = getInternalUnidirectionalReferences();
		for (EReference ref : internalRefs) {

			EReferenceImpl inverse = (EReferenceImpl) ecoreFactory.createEReference();
			inverse.setName(ref.getName() + "_inverseTo_" + getName());
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

			EReference trueInverse = (EReference) insertNewFeature((EClass) ref.getEType(), inverse);
			ref.setEOpposite(trueInverse);
		}
	}

	protected void fetchDynamicEStructuralFeaturesOfSuperTypes() {
		// Update dynamic feature collection with dynamic features from super classes
		Set<EClass> ownClasses = eContents().parallelStream().filter(obj -> (obj instanceof EClass))
				.map(ecls -> (EClass) ecls).collect(Collectors.toSet());

		for (EClass ownClass : ownClasses) {
			for (EClass foreignSuperClass : ownClass.getEAllSuperTypes().stream()
					.filter(ecls -> (ecls instanceof EClass)).map(ecls -> (EClass) ecls)
					.filter(ecls -> ecls.eContainer() != this && (ecls.eContainer() instanceof SmartPackage))
					.collect(Collectors.toSet())) {
				SmartPackage otherPkg = (SmartPackage) foreignSuperClass.eContainer();
				// Register a callback -> super classes might be subject to change during
				// runtime
				otherPkg.registerDynamicFeatureUpdateCallback(foreignSuperClass, this::insertFeatureOfSuperType);

				if (!otherPkg.hasDynamicEStructuralFeatures(foreignSuperClass))
					continue;

				otherPkg.getDynamicEStructuralFeatures(foreignSuperClass)
						.forEach(feature -> insertClass2Feature(ownClass, feature));
			}
		}
	}

	protected Collection<EReference> getExternalUnidirectionalReferences() {
		Set<EClass> ownClasses = eContents().parallelStream().filter(obj -> (obj instanceof EClass))
				.map(ecls -> (EClass) ecls).collect(Collectors.toSet());
		// Find all references that point to EClasses not defined in this package.
		return eContents().parallelStream().filter(obj -> (obj instanceof EClass)).map(ecls -> (EClass) ecls)
				.flatMap(ecls -> ecls.getEAllStructuralFeatures().parallelStream())
				.filter(ref -> (ref instanceof EReference)).map(ref -> (EReference) ref)
				.filter(ref -> ref.getEOpposite() == null).filter(ref -> !ownClasses.contains(ref.getEType()))
				.filter(ref -> !ref.isContainment()).collect(Collectors.toSet());
	}

	protected Collection<EReference> getInternalUnidirectionalReferences() {
		Set<EClass> ownClasses = eContents().parallelStream().filter(obj -> (obj instanceof EClass))
				.map(ecls -> (EClass) ecls).collect(Collectors.toSet());
		// Find all references that point to EClasses defined in this package.
		return eContents().parallelStream().filter(obj -> (obj instanceof EClass)).map(ecls -> (EClass) ecls)
				.flatMap(ecls -> ecls.getEAllStructuralFeatures().parallelStream())
				.filter(ref -> (ref instanceof EReference)).map(ref -> (EReference) ref)
				.filter(ref -> ref.getEOpposite() == null).filter(ref -> ownClasses.contains(ref.getEType()))
				.filter(ref -> !ref.isContainment()).collect(Collectors.toSet());
	}

	private void insertClass2Feature(final EClass eClass, final EStructuralFeature eFeature) {
		Set<EStructuralFeature> currentFeatures = dynamicFeatures.get(eClass);
		if (currentFeatures == null) {
			currentFeatures = new HashSet<>();
			dynamicFeatures.put(eClass, currentFeatures);
		}
		currentFeatures.add(eFeature);
	}

	private void insertFeatureOfSuperType(final EClass superClass, final EStructuralFeature eFeature) {
		Set<EClass> subClasses = eContents().parallelStream().filter(obj -> (obj instanceof EClass))
				.map(ecls -> (EClass) ecls).filter(ecls -> ecls.getEAllSuperTypes().contains(superClass))
				.collect(Collectors.toSet());

		for (EClass subClass : subClasses) {
			insertClass2Feature(subClass, eFeature);
		}

	}

	protected Set<EClass> getSubClassesInPackage(final EClass eClass) {
		if (subClassesInPackage.containsKey(eClass))
			return subClassesInPackage.get(eClass);

		Set<EClass> subClasses = new HashSet<>();
		subClassesInPackage.put(eClass, subClasses);

		for (EClass someClass : eContents().stream().filter(obj -> (obj instanceof EClass)).map(ecls -> (EClass) ecls)
				.filter(ecls -> ecls != eClass).collect(Collectors.toSet())) {
			if (someClass.getEAllSuperTypes().contains(eClass)) {
				subClasses.add(someClass);
			}
		}
		return subClasses;
	}

}
