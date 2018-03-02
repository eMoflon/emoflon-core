/**
 */
package org.moflon.core.propertycontainer.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.moflon.core.propertycontainer.PropertycontainerPackage;
import org.moflon.core.propertycontainer.SDMCodeGeneratorIds;
import org.moflon.core.propertycontainer.SdmCodegeneratorMethodBodyHandler;
// <-- [user defined imports]
// [user defined imports] -->

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Sdm Codegenerator Method Body Handler</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.moflon.core.propertycontainer.impl.SdmCodegeneratorMethodBodyHandlerImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.impl.SdmCodegeneratorMethodBodyHandlerImpl#getValue <em>Value</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SdmCodegeneratorMethodBodyHandlerImpl extends EObjectImpl implements SdmCodegeneratorMethodBodyHandler
{
   /**
    * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see #getDescription()
    * @generated
    * @ordered
    */
   protected static final String DESCRIPTION_EDEFAULT = "[Value determines the MethodBodyHandler that invokes the code generator for SDMs.]";

   /**
    * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see #getDescription()
    * @generated
    * @ordered
    */
   protected String description = DESCRIPTION_EDEFAULT;

   /**
    * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see #getValue()
    * @generated
    * @ordered
    */
   protected static final SDMCodeGeneratorIds VALUE_EDEFAULT = SDMCodeGeneratorIds.DEMOCLES;

   /**
    * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see #getValue()
    * @generated
    * @ordered
    */
   protected SDMCodeGeneratorIds value = VALUE_EDEFAULT;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected SdmCodegeneratorMethodBodyHandlerImpl()
   {
      super();
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   protected EClass eStaticClass()
   {
      return PropertycontainerPackage.Literals.SDM_CODEGENERATOR_METHOD_BODY_HANDLER;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public void setDescription(String newDescription)
   {
      String oldDescription = description;
      description = newDescription;
      if (eNotificationRequired())
         eNotify(new ENotificationImpl(this, Notification.SET, PropertycontainerPackage.SDM_CODEGENERATOR_METHOD_BODY_HANDLER__DESCRIPTION, oldDescription,
               description));
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public SDMCodeGeneratorIds getValue()
   {
      return value;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public void setValue(SDMCodeGeneratorIds newValue)
   {
      SDMCodeGeneratorIds oldValue = value;
      value = newValue == null ? VALUE_EDEFAULT : newValue;
      if (eNotificationRequired())
         eNotify(new ENotificationImpl(this, Notification.SET, PropertycontainerPackage.SDM_CODEGENERATOR_METHOD_BODY_HANDLER__VALUE, oldValue, value));
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public Object eGet(int featureID, boolean resolve, boolean coreType)
   {
      switch (featureID)
      {
      case PropertycontainerPackage.SDM_CODEGENERATOR_METHOD_BODY_HANDLER__DESCRIPTION:
         return getDescription();
      case PropertycontainerPackage.SDM_CODEGENERATOR_METHOD_BODY_HANDLER__VALUE:
         return getValue();
      }
      return super.eGet(featureID, resolve, coreType);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public void eSet(int featureID, Object newValue)
   {
      switch (featureID)
      {
      case PropertycontainerPackage.SDM_CODEGENERATOR_METHOD_BODY_HANDLER__DESCRIPTION:
         setDescription((String) newValue);
         return;
      case PropertycontainerPackage.SDM_CODEGENERATOR_METHOD_BODY_HANDLER__VALUE:
         setValue((SDMCodeGeneratorIds) newValue);
         return;
      }
      super.eSet(featureID, newValue);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public void eUnset(int featureID)
   {
      switch (featureID)
      {
      case PropertycontainerPackage.SDM_CODEGENERATOR_METHOD_BODY_HANDLER__DESCRIPTION:
         setDescription(DESCRIPTION_EDEFAULT);
         return;
      case PropertycontainerPackage.SDM_CODEGENERATOR_METHOD_BODY_HANDLER__VALUE:
         setValue(VALUE_EDEFAULT);
         return;
      }
      super.eUnset(featureID);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public boolean eIsSet(int featureID)
   {
      switch (featureID)
      {
      case PropertycontainerPackage.SDM_CODEGENERATOR_METHOD_BODY_HANDLER__DESCRIPTION:
         return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
      case PropertycontainerPackage.SDM_CODEGENERATOR_METHOD_BODY_HANDLER__VALUE:
         return value != VALUE_EDEFAULT;
      }
      return super.eIsSet(featureID);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public String toString()
   {
      if (eIsProxy())
         return super.toString();

      StringBuffer result = new StringBuffer(super.toString());
      result.append(" (description: ");
      result.append(description);
      result.append(", value: ");
      result.append(value);
      result.append(')');
      return result.toString();
   }
   // <-- [user code injected with eMoflon]

   // [user code injected with eMoflon] -->
} //SdmCodegeneratorMethodBodyHandlerImpl
