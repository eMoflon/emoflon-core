/**
 */
package org.moflon.core.propertycontainer.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

import org.moflon.core.propertycontainer.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.moflon.core.propertycontainer.PropertycontainerPackage
 * @generated
 */
public class PropertycontainerSwitch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static PropertycontainerPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PropertycontainerSwitch() {
		if (modelPackage == null) {
			modelPackage = PropertycontainerPackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
		case PropertycontainerPackage.DEPENDENCIES: {
			Dependencies dependencies = (Dependencies) theEObject;
			T result = caseDependencies(dependencies);
			if (result == null)
				result = casePropertiesValue(dependencies);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case PropertycontainerPackage.PROPERTIES_VALUE: {
			PropertiesValue propertiesValue = (PropertiesValue) theEObject;
			T result = casePropertiesValue(propertiesValue);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case PropertycontainerPackage.ADDITIONAL_USED_GEN_PACKAGES: {
			AdditionalUsedGenPackages additionalUsedGenPackages = (AdditionalUsedGenPackages) theEObject;
			T result = caseAdditionalUsedGenPackages(additionalUsedGenPackages);
			if (result == null)
				result = casePropertiesValue(additionalUsedGenPackages);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case PropertycontainerPackage.IMPORT_MAPPINGS: {
			ImportMappings importMappings = (ImportMappings) theEObject;
			T result = caseImportMappings(importMappings);
			if (result == null)
				result = casePropertiesMapping(importMappings);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case PropertycontainerPackage.TGG_BUILD_MODE: {
			TGGBuildMode tggBuildMode = (TGGBuildMode) theEObject;
			T result = caseTGGBuildMode(tggBuildMode);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case PropertycontainerPackage.FACTORY_MAPPINGS: {
			FactoryMappings factoryMappings = (FactoryMappings) theEObject;
			T result = caseFactoryMappings(factoryMappings);
			if (result == null)
				result = casePropertiesMapping(factoryMappings);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER: {
			MoflonPropertiesContainer moflonPropertiesContainer = (MoflonPropertiesContainer) theEObject;
			T result = caseMoflonPropertiesContainer(moflonPropertiesContainer);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case PropertycontainerPackage.REPLACE_GEN_MODEL: {
			ReplaceGenModel replaceGenModel = (ReplaceGenModel) theEObject;
			T result = caseReplaceGenModel(replaceGenModel);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case PropertycontainerPackage.PROPERTIES_MAPPING: {
			PropertiesMapping propertiesMapping = (PropertiesMapping) theEObject;
			T result = casePropertiesMapping(propertiesMapping);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case PropertycontainerPackage.ADDITIONAL_DEPENDENCIES: {
			AdditionalDependencies additionalDependencies = (AdditionalDependencies) theEObject;
			T result = caseAdditionalDependencies(additionalDependencies);
			if (result == null)
				result = casePropertiesValue(additionalDependencies);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case PropertycontainerPackage.META_MODEL_PROJECT: {
			MetaModelProject metaModelProject = (MetaModelProject) theEObject;
			T result = caseMetaModelProject(metaModelProject);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		default:
			return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Dependencies</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Dependencies</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDependencies(Dependencies object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Properties Value</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Properties Value</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePropertiesValue(PropertiesValue object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Additional Used Gen Packages</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Additional Used Gen Packages</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAdditionalUsedGenPackages(AdditionalUsedGenPackages object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Import Mappings</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Import Mappings</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseImportMappings(ImportMappings object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>TGG Build Mode</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>TGG Build Mode</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTGGBuildMode(TGGBuildMode object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Factory Mappings</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Factory Mappings</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFactoryMappings(FactoryMappings object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Moflon Properties Container</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Moflon Properties Container</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMoflonPropertiesContainer(MoflonPropertiesContainer object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Replace Gen Model</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Replace Gen Model</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseReplaceGenModel(ReplaceGenModel object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Properties Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Properties Mapping</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePropertiesMapping(PropertiesMapping object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Additional Dependencies</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Additional Dependencies</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAdditionalDependencies(AdditionalDependencies object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Meta Model Project</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Meta Model Project</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMetaModelProject(MetaModelProject object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(EObject object) {
		return null;
	}

} //PropertycontainerSwitch
