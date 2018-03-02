/**
 */
package org.moflon.core.propertycontainer.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.moflon.core.propertycontainer.AdditionalDependencies;
import org.moflon.core.propertycontainer.AdditionalUsedGenPackages;
import org.moflon.core.propertycontainer.Dependencies;
import org.moflon.core.propertycontainer.FactoryMappings;
import org.moflon.core.propertycontainer.ImportMappings;
import org.moflon.core.propertycontainer.MetaModelProject;
import org.moflon.core.propertycontainer.MoflonPropertiesContainer;
import org.moflon.core.propertycontainer.PropertycontainerPackage;
import org.moflon.core.propertycontainer.ReplaceGenModel;
import org.moflon.core.propertycontainer.SdmCodegeneratorMethodBodyHandler;
import org.moflon.core.propertycontainer.TGGBuildMode;
// <-- [user defined imports]
// [user defined imports] -->

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Moflon
 * Properties Container</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.moflon.core.propertycontainer.impl.MoflonPropertiesContainerImpl#getDependencies
 * <em>Dependencies</em>}</li>
 * <li>{@link org.moflon.core.propertycontainer.impl.MoflonPropertiesContainerImpl#getFactoryMappings
 * <em>Factory Mappings</em>}</li>
 * <li>{@link org.moflon.core.propertycontainer.impl.MoflonPropertiesContainerImpl#getAdditionalDependencies
 * <em>Additional Dependencies</em>}</li>
 * <li>{@link org.moflon.core.propertycontainer.impl.MoflonPropertiesContainerImpl#getMetaModelProject
 * <em>Meta Model Project</em>}</li>
 * <li>{@link org.moflon.core.propertycontainer.impl.MoflonPropertiesContainerImpl#getReplaceGenModel
 * <em>Replace Gen Model</em>}</li>
 * <li>{@link org.moflon.core.propertycontainer.impl.MoflonPropertiesContainerImpl#getSdmCodegeneratorHandlerId
 * <em>Sdm Codegenerator Handler Id</em>}</li>
 * <li>{@link org.moflon.core.propertycontainer.impl.MoflonPropertiesContainerImpl#getTGGBuildMode
 * <em>TGG Build Mode</em>}</li>
 * <li>{@link org.moflon.core.propertycontainer.impl.MoflonPropertiesContainerImpl#getImportMappings
 * <em>Import Mappings</em>}</li>
 * <li>{@link org.moflon.core.propertycontainer.impl.MoflonPropertiesContainerImpl#getAdditionalUsedGenPackages
 * <em>Additional Used Gen Packages</em>}</li>
 * <li>{@link org.moflon.core.propertycontainer.impl.MoflonPropertiesContainerImpl#getDescription
 * <em>Description</em>}</li>
 * <li>{@link org.moflon.core.propertycontainer.impl.MoflonPropertiesContainerImpl#getProjectName
 * <em>Project Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MoflonPropertiesContainerImpl extends EObjectImpl implements MoflonPropertiesContainer {
	/**
	 * The cached value of the '{@link #getDependencies() <em>Dependencies</em>}'
	 * containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getDependencies()
	 * @generated
	 * @ordered
	 */
	protected EList<Dependencies> dependencies;

	/**
	 * The cached value of the '{@link #getFactoryMappings() <em>Factory
	 * Mappings</em>}' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getFactoryMappings()
	 * @generated
	 * @ordered
	 */
	protected EList<FactoryMappings> factoryMappings;

	/**
	 * The cached value of the '{@link #getAdditionalDependencies() <em>Additional
	 * Dependencies</em>}' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getAdditionalDependencies()
	 * @generated
	 * @ordered
	 */
	protected EList<AdditionalDependencies> additionalDependencies;

	/**
	 * The cached value of the '{@link #getMetaModelProject() <em>Meta Model
	 * Project</em>}' containment reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getMetaModelProject()
	 * @generated
	 * @ordered
	 */
	protected MetaModelProject metaModelProject;

	/**
	 * The cached value of the '{@link #getReplaceGenModel() <em>Replace Gen
	 * Model</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getReplaceGenModel()
	 * @generated
	 * @ordered
	 */
	protected ReplaceGenModel replaceGenModel;

	/**
	 * The cached value of the '{@link #getSdmCodegeneratorHandlerId() <em>Sdm
	 * Codegenerator Handler Id</em>}' containment reference. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @see #getSdmCodegeneratorHandlerId()
	 * @generated
	 * @ordered
	 */
	protected SdmCodegeneratorMethodBodyHandler sdmCodegeneratorHandlerId;

	/**
	 * The cached value of the '{@link #getTGGBuildMode() <em>TGG Build Mode</em>}'
	 * containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getTGGBuildMode()
	 * @generated
	 * @ordered
	 */
	protected TGGBuildMode tGGBuildMode;

	/**
	 * The cached value of the '{@link #getImportMappings() <em>Import
	 * Mappings</em>}' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getImportMappings()
	 * @generated
	 * @ordered
	 */
	protected EList<ImportMappings> importMappings;

	/**
	 * The cached value of the '{@link #getAdditionalUsedGenPackages()
	 * <em>Additional Used Gen Packages</em>}' containment reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getAdditionalUsedGenPackages()
	 * @generated
	 * @ordered
	 */
	protected EList<AdditionalUsedGenPackages> additionalUsedGenPackages;

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = "[Properties to configure code generation]";

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

	/**
	 * The default value of the '{@link #getProjectName() <em>Project Name</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getProjectName()
	 * @generated
	 * @ordered
	 */
	protected static final String PROJECT_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getProjectName() <em>Project Name</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getProjectName()
	 * @generated
	 * @ordered
	 */
	protected String projectName = PROJECT_NAME_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected MoflonPropertiesContainerImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PropertycontainerPackage.Literals.MOFLON_PROPERTIES_CONTAINER;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public EList<Dependencies> getDependencies() {
		if (dependencies == null) {
			dependencies = new EObjectContainmentEList<Dependencies>(Dependencies.class, this,
					PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__DEPENDENCIES);
		}
		return dependencies;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public EList<FactoryMappings> getFactoryMappings() {
		if (factoryMappings == null) {
			factoryMappings = new EObjectContainmentEList<FactoryMappings>(FactoryMappings.class, this,
					PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__FACTORY_MAPPINGS);
		}
		return factoryMappings;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public EList<AdditionalDependencies> getAdditionalDependencies() {
		if (additionalDependencies == null) {
			additionalDependencies = new EObjectContainmentEList<AdditionalDependencies>(AdditionalDependencies.class,
					this, PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__ADDITIONAL_DEPENDENCIES);
		}
		return additionalDependencies;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public MetaModelProject getMetaModelProject() {
		return metaModelProject;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetMetaModelProject(MetaModelProject newMetaModelProject, NotificationChain msgs) {
		MetaModelProject oldMetaModelProject = metaModelProject;
		metaModelProject = newMetaModelProject;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
					PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__META_MODEL_PROJECT, oldMetaModelProject,
					newMetaModelProject);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setMetaModelProject(MetaModelProject newMetaModelProject) {
		if (newMetaModelProject != metaModelProject) {
			NotificationChain msgs = null;
			if (metaModelProject != null)
				msgs = ((InternalEObject) metaModelProject).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
						- PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__META_MODEL_PROJECT, null, msgs);
			if (newMetaModelProject != null)
				msgs = ((InternalEObject) newMetaModelProject).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
						- PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__META_MODEL_PROJECT, null, msgs);
			msgs = basicSetMetaModelProject(newMetaModelProject, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__META_MODEL_PROJECT, newMetaModelProject,
					newMetaModelProject));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ReplaceGenModel getReplaceGenModel() {
		return replaceGenModel;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetReplaceGenModel(ReplaceGenModel newReplaceGenModel, NotificationChain msgs) {
		ReplaceGenModel oldReplaceGenModel = replaceGenModel;
		replaceGenModel = newReplaceGenModel;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
					PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__REPLACE_GEN_MODEL, oldReplaceGenModel,
					newReplaceGenModel);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setReplaceGenModel(ReplaceGenModel newReplaceGenModel) {
		if (newReplaceGenModel != replaceGenModel) {
			NotificationChain msgs = null;
			if (replaceGenModel != null)
				msgs = ((InternalEObject) replaceGenModel).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
						- PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__REPLACE_GEN_MODEL, null, msgs);
			if (newReplaceGenModel != null)
				msgs = ((InternalEObject) newReplaceGenModel).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
						- PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__REPLACE_GEN_MODEL, null, msgs);
			msgs = basicSetReplaceGenModel(newReplaceGenModel, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__REPLACE_GEN_MODEL, newReplaceGenModel,
					newReplaceGenModel));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public SdmCodegeneratorMethodBodyHandler getSdmCodegeneratorHandlerId() {
		return sdmCodegeneratorHandlerId;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetSdmCodegeneratorHandlerId(
			SdmCodegeneratorMethodBodyHandler newSdmCodegeneratorHandlerId, NotificationChain msgs) {
		SdmCodegeneratorMethodBodyHandler oldSdmCodegeneratorHandlerId = sdmCodegeneratorHandlerId;
		sdmCodegeneratorHandlerId = newSdmCodegeneratorHandlerId;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
					PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__SDM_CODEGENERATOR_HANDLER_ID,
					oldSdmCodegeneratorHandlerId, newSdmCodegeneratorHandlerId);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setSdmCodegeneratorHandlerId(SdmCodegeneratorMethodBodyHandler newSdmCodegeneratorHandlerId) {
		if (newSdmCodegeneratorHandlerId != sdmCodegeneratorHandlerId) {
			NotificationChain msgs = null;
			if (sdmCodegeneratorHandlerId != null)
				msgs = ((InternalEObject) sdmCodegeneratorHandlerId).eInverseRemove(this,
						EOPPOSITE_FEATURE_BASE
								- PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__SDM_CODEGENERATOR_HANDLER_ID,
						null, msgs);
			if (newSdmCodegeneratorHandlerId != null)
				msgs = ((InternalEObject) newSdmCodegeneratorHandlerId).eInverseAdd(this,
						EOPPOSITE_FEATURE_BASE
								- PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__SDM_CODEGENERATOR_HANDLER_ID,
						null, msgs);
			msgs = basicSetSdmCodegeneratorHandlerId(newSdmCodegeneratorHandlerId, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__SDM_CODEGENERATOR_HANDLER_ID,
					newSdmCodegeneratorHandlerId, newSdmCodegeneratorHandlerId));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public TGGBuildMode getTGGBuildMode() {
		return tGGBuildMode;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetTGGBuildMode(TGGBuildMode newTGGBuildMode, NotificationChain msgs) {
		TGGBuildMode oldTGGBuildMode = tGGBuildMode;
		tGGBuildMode = newTGGBuildMode;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
					PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__TGG_BUILD_MODE, oldTGGBuildMode,
					newTGGBuildMode);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setTGGBuildMode(TGGBuildMode newTGGBuildMode) {
		if (newTGGBuildMode != tGGBuildMode) {
			NotificationChain msgs = null;
			if (tGGBuildMode != null)
				msgs = ((InternalEObject) tGGBuildMode).eInverseRemove(this,
						EOPPOSITE_FEATURE_BASE - PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__TGG_BUILD_MODE,
						null, msgs);
			if (newTGGBuildMode != null)
				msgs = ((InternalEObject) newTGGBuildMode).eInverseAdd(this,
						EOPPOSITE_FEATURE_BASE - PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__TGG_BUILD_MODE,
						null, msgs);
			msgs = basicSetTGGBuildMode(newTGGBuildMode, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__TGG_BUILD_MODE, newTGGBuildMode,
					newTGGBuildMode));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public EList<ImportMappings> getImportMappings() {
		if (importMappings == null) {
			importMappings = new EObjectContainmentEList<ImportMappings>(ImportMappings.class, this,
					PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__IMPORT_MAPPINGS);
		}
		return importMappings;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public EList<AdditionalUsedGenPackages> getAdditionalUsedGenPackages() {
		if (additionalUsedGenPackages == null) {
			additionalUsedGenPackages = new EObjectContainmentEList<AdditionalUsedGenPackages>(
					AdditionalUsedGenPackages.class, this,
					PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__ADDITIONAL_USED_GEN_PACKAGES);
		}
		return additionalUsedGenPackages;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setProjectName(String newProjectName) {
		String oldProjectName = projectName;
		projectName = newProjectName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__PROJECT_NAME, oldProjectName, projectName));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__DEPENDENCIES:
			return ((InternalEList<?>) getDependencies()).basicRemove(otherEnd, msgs);
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__FACTORY_MAPPINGS:
			return ((InternalEList<?>) getFactoryMappings()).basicRemove(otherEnd, msgs);
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__ADDITIONAL_DEPENDENCIES:
			return ((InternalEList<?>) getAdditionalDependencies()).basicRemove(otherEnd, msgs);
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__META_MODEL_PROJECT:
			return basicSetMetaModelProject(null, msgs);
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__REPLACE_GEN_MODEL:
			return basicSetReplaceGenModel(null, msgs);
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__SDM_CODEGENERATOR_HANDLER_ID:
			return basicSetSdmCodegeneratorHandlerId(null, msgs);
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__TGG_BUILD_MODE:
			return basicSetTGGBuildMode(null, msgs);
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__IMPORT_MAPPINGS:
			return ((InternalEList<?>) getImportMappings()).basicRemove(otherEnd, msgs);
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__ADDITIONAL_USED_GEN_PACKAGES:
			return ((InternalEList<?>) getAdditionalUsedGenPackages()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__DEPENDENCIES:
			return getDependencies();
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__FACTORY_MAPPINGS:
			return getFactoryMappings();
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__ADDITIONAL_DEPENDENCIES:
			return getAdditionalDependencies();
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__META_MODEL_PROJECT:
			return getMetaModelProject();
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__REPLACE_GEN_MODEL:
			return getReplaceGenModel();
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__SDM_CODEGENERATOR_HANDLER_ID:
			return getSdmCodegeneratorHandlerId();
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__TGG_BUILD_MODE:
			return getTGGBuildMode();
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__IMPORT_MAPPINGS:
			return getImportMappings();
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__ADDITIONAL_USED_GEN_PACKAGES:
			return getAdditionalUsedGenPackages();
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__DESCRIPTION:
			return getDescription();
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__PROJECT_NAME:
			return getProjectName();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__DEPENDENCIES:
			getDependencies().clear();
			getDependencies().addAll((Collection<? extends Dependencies>) newValue);
			return;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__FACTORY_MAPPINGS:
			getFactoryMappings().clear();
			getFactoryMappings().addAll((Collection<? extends FactoryMappings>) newValue);
			return;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__ADDITIONAL_DEPENDENCIES:
			getAdditionalDependencies().clear();
			getAdditionalDependencies().addAll((Collection<? extends AdditionalDependencies>) newValue);
			return;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__META_MODEL_PROJECT:
			setMetaModelProject((MetaModelProject) newValue);
			return;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__REPLACE_GEN_MODEL:
			setReplaceGenModel((ReplaceGenModel) newValue);
			return;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__SDM_CODEGENERATOR_HANDLER_ID:
			setSdmCodegeneratorHandlerId((SdmCodegeneratorMethodBodyHandler) newValue);
			return;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__TGG_BUILD_MODE:
			setTGGBuildMode((TGGBuildMode) newValue);
			return;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__IMPORT_MAPPINGS:
			getImportMappings().clear();
			getImportMappings().addAll((Collection<? extends ImportMappings>) newValue);
			return;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__ADDITIONAL_USED_GEN_PACKAGES:
			getAdditionalUsedGenPackages().clear();
			getAdditionalUsedGenPackages().addAll((Collection<? extends AdditionalUsedGenPackages>) newValue);
			return;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__DESCRIPTION:
			setDescription((String) newValue);
			return;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__PROJECT_NAME:
			setProjectName((String) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__DEPENDENCIES:
			getDependencies().clear();
			return;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__FACTORY_MAPPINGS:
			getFactoryMappings().clear();
			return;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__ADDITIONAL_DEPENDENCIES:
			getAdditionalDependencies().clear();
			return;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__META_MODEL_PROJECT:
			setMetaModelProject((MetaModelProject) null);
			return;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__REPLACE_GEN_MODEL:
			setReplaceGenModel((ReplaceGenModel) null);
			return;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__SDM_CODEGENERATOR_HANDLER_ID:
			setSdmCodegeneratorHandlerId((SdmCodegeneratorMethodBodyHandler) null);
			return;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__TGG_BUILD_MODE:
			setTGGBuildMode((TGGBuildMode) null);
			return;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__IMPORT_MAPPINGS:
			getImportMappings().clear();
			return;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__ADDITIONAL_USED_GEN_PACKAGES:
			getAdditionalUsedGenPackages().clear();
			return;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__DESCRIPTION:
			setDescription(DESCRIPTION_EDEFAULT);
			return;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__PROJECT_NAME:
			setProjectName(PROJECT_NAME_EDEFAULT);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__DEPENDENCIES:
			return dependencies != null && !dependencies.isEmpty();
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__FACTORY_MAPPINGS:
			return factoryMappings != null && !factoryMappings.isEmpty();
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__ADDITIONAL_DEPENDENCIES:
			return additionalDependencies != null && !additionalDependencies.isEmpty();
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__META_MODEL_PROJECT:
			return metaModelProject != null;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__REPLACE_GEN_MODEL:
			return replaceGenModel != null;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__SDM_CODEGENERATOR_HANDLER_ID:
			return sdmCodegeneratorHandlerId != null;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__TGG_BUILD_MODE:
			return tGGBuildMode != null;
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__IMPORT_MAPPINGS:
			return importMappings != null && !importMappings.isEmpty();
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__ADDITIONAL_USED_GEN_PACKAGES:
			return additionalUsedGenPackages != null && !additionalUsedGenPackages.isEmpty();
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__DESCRIPTION:
			return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER__PROJECT_NAME:
			return PROJECT_NAME_EDEFAULT == null ? projectName != null : !PROJECT_NAME_EDEFAULT.equals(projectName);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (description: ");
		result.append(description);
		result.append(", projectName: ");
		result.append(projectName);
		result.append(')');
		return result.toString();
	}
	// <-- [user code injected with eMoflon]

	// [user code injected with eMoflon] -->
} // MoflonPropertiesContainerImpl
