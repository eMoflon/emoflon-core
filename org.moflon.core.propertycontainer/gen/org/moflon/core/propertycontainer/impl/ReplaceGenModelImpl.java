/**
 */
package org.moflon.core.propertycontainer.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.moflon.core.propertycontainer.PropertycontainerPackage;
import org.moflon.core.propertycontainer.ReplaceGenModel;
// <-- [user defined imports]
// [user defined imports] -->

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Replace Gen Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.moflon.core.propertycontainer.impl.ReplaceGenModelImpl#isBool <em>Bool</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.impl.ReplaceGenModelImpl#getDescription <em>Description</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ReplaceGenModelImpl extends EObjectImpl implements ReplaceGenModel
{
   /**
    * The default value of the '{@link #isBool() <em>Bool</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see #isBool()
    * @generated
    * @ordered
    */
   protected static final boolean BOOL_EDEFAULT = true;

   /**
    * The cached value of the '{@link #isBool() <em>Bool</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see #isBool()
    * @generated
    * @ordered
    */
   protected boolean bool = BOOL_EDEFAULT;

   /**
    * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see #getDescription()
    * @generated
    * @ordered
    */
   protected static final String DESCRIPTION_EDEFAULT = "[Set to false if you wish to maintain the GenModel in the project yourself]";

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
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected ReplaceGenModelImpl()
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
      return PropertycontainerPackage.Literals.REPLACE_GEN_MODEL;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public boolean isBool()
   {
      return bool;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public void setBool(boolean newBool)
   {
      boolean oldBool = bool;
      bool = newBool;
      if (eNotificationRequired())
         eNotify(new ENotificationImpl(this, Notification.SET, PropertycontainerPackage.REPLACE_GEN_MODEL__BOOL, oldBool, bool));
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
         eNotify(new ENotificationImpl(this, Notification.SET, PropertycontainerPackage.REPLACE_GEN_MODEL__DESCRIPTION, oldDescription, description));
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
      case PropertycontainerPackage.REPLACE_GEN_MODEL__BOOL:
         return isBool();
      case PropertycontainerPackage.REPLACE_GEN_MODEL__DESCRIPTION:
         return getDescription();
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
      case PropertycontainerPackage.REPLACE_GEN_MODEL__BOOL:
         setBool((Boolean) newValue);
         return;
      case PropertycontainerPackage.REPLACE_GEN_MODEL__DESCRIPTION:
         setDescription((String) newValue);
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
      case PropertycontainerPackage.REPLACE_GEN_MODEL__BOOL:
         setBool(BOOL_EDEFAULT);
         return;
      case PropertycontainerPackage.REPLACE_GEN_MODEL__DESCRIPTION:
         setDescription(DESCRIPTION_EDEFAULT);
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
      case PropertycontainerPackage.REPLACE_GEN_MODEL__BOOL:
         return bool != BOOL_EDEFAULT;
      case PropertycontainerPackage.REPLACE_GEN_MODEL__DESCRIPTION:
         return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
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
      result.append(" (bool: ");
      result.append(bool);
      result.append(", description: ");
      result.append(description);
      result.append(')');
      return result.toString();
   }
   // <-- [user code injected with eMoflon]

   // [user code injected with eMoflon] -->
} //ReplaceGenModelImpl
