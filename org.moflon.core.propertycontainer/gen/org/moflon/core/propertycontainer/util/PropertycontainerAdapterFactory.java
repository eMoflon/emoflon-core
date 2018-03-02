/**
 */
package org.moflon.core.propertycontainer.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.moflon.core.propertycontainer.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.moflon.core.propertycontainer.PropertycontainerPackage
 * @generated
 */
public class PropertycontainerAdapterFactory extends AdapterFactoryImpl
{
   /**
    * The cached model package.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected static PropertycontainerPackage modelPackage;

   /**
    * Creates an instance of the adapter factory.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public PropertycontainerAdapterFactory()
   {
      if (modelPackage == null)
      {
         modelPackage = PropertycontainerPackage.eINSTANCE;
      }
   }

   /**
    * Returns whether this factory is applicable for the type of the object.
    * <!-- begin-user-doc -->
    * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
    * <!-- end-user-doc -->
    * @return whether this factory is applicable for the type of the object.
    * @generated
    */
   @Override
   public boolean isFactoryForType(Object object)
   {
      if (object == modelPackage)
      {
         return true;
      }
      if (object instanceof EObject)
      {
         return ((EObject) object).eClass().getEPackage() == modelPackage;
      }
      return false;
   }

   /**
    * The switch that delegates to the <code>createXXX</code> methods.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected PropertycontainerSwitch<Adapter> modelSwitch = new PropertycontainerSwitch<Adapter>() {
      @Override
      public Adapter caseDependencies(Dependencies object)
      {
         return createDependenciesAdapter();
      }

      @Override
      public Adapter casePropertiesValue(PropertiesValue object)
      {
         return createPropertiesValueAdapter();
      }

      @Override
      public Adapter caseAdditionalUsedGenPackages(AdditionalUsedGenPackages object)
      {
         return createAdditionalUsedGenPackagesAdapter();
      }

      @Override
      public Adapter caseImportMappings(ImportMappings object)
      {
         return createImportMappingsAdapter();
      }

      @Override
      public Adapter caseTGGBuildMode(TGGBuildMode object)
      {
         return createTGGBuildModeAdapter();
      }

      @Override
      public Adapter caseSdmCodegeneratorMethodBodyHandler(SdmCodegeneratorMethodBodyHandler object)
      {
         return createSdmCodegeneratorMethodBodyHandlerAdapter();
      }

      @Override
      public Adapter caseFactoryMappings(FactoryMappings object)
      {
         return createFactoryMappingsAdapter();
      }

      @Override
      public Adapter caseMoflonPropertiesContainer(MoflonPropertiesContainer object)
      {
         return createMoflonPropertiesContainerAdapter();
      }

      @Override
      public Adapter caseReplaceGenModel(ReplaceGenModel object)
      {
         return createReplaceGenModelAdapter();
      }

      @Override
      public Adapter casePropertiesMapping(PropertiesMapping object)
      {
         return createPropertiesMappingAdapter();
      }

      @Override
      public Adapter caseAdditionalDependencies(AdditionalDependencies object)
      {
         return createAdditionalDependenciesAdapter();
      }

      @Override
      public Adapter caseMetaModelProject(MetaModelProject object)
      {
         return createMetaModelProjectAdapter();
      }

      @Override
      public Adapter defaultCase(EObject object)
      {
         return createEObjectAdapter();
      }
   };

   /**
    * Creates an adapter for the <code>target</code>.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @param target the object to adapt.
    * @return the adapter for the <code>target</code>.
    * @generated
    */
   @Override
   public Adapter createAdapter(Notifier target)
   {
      return modelSwitch.doSwitch((EObject) target);
   }

   /**
    * Creates a new adapter for an object of class '{@link org.moflon.core.propertycontainer.Dependencies <em>Dependencies</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    * @return the new adapter.
    * @see org.moflon.core.propertycontainer.Dependencies
    * @generated
    */
   public Adapter createDependenciesAdapter()
   {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.moflon.core.propertycontainer.PropertiesValue <em>Properties Value</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    * @return the new adapter.
    * @see org.moflon.core.propertycontainer.PropertiesValue
    * @generated
    */
   public Adapter createPropertiesValueAdapter()
   {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.moflon.core.propertycontainer.AdditionalUsedGenPackages <em>Additional Used Gen Packages</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    * @return the new adapter.
    * @see org.moflon.core.propertycontainer.AdditionalUsedGenPackages
    * @generated
    */
   public Adapter createAdditionalUsedGenPackagesAdapter()
   {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.moflon.core.propertycontainer.ImportMappings <em>Import Mappings</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    * @return the new adapter.
    * @see org.moflon.core.propertycontainer.ImportMappings
    * @generated
    */
   public Adapter createImportMappingsAdapter()
   {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.moflon.core.propertycontainer.TGGBuildMode <em>TGG Build Mode</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    * @return the new adapter.
    * @see org.moflon.core.propertycontainer.TGGBuildMode
    * @generated
    */
   public Adapter createTGGBuildModeAdapter()
   {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.moflon.core.propertycontainer.SdmCodegeneratorMethodBodyHandler <em>Sdm Codegenerator Method Body Handler</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    * @return the new adapter.
    * @see org.moflon.core.propertycontainer.SdmCodegeneratorMethodBodyHandler
    * @generated
    */
   public Adapter createSdmCodegeneratorMethodBodyHandlerAdapter()
   {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.moflon.core.propertycontainer.FactoryMappings <em>Factory Mappings</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    * @return the new adapter.
    * @see org.moflon.core.propertycontainer.FactoryMappings
    * @generated
    */
   public Adapter createFactoryMappingsAdapter()
   {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer <em>Moflon Properties Container</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    * @return the new adapter.
    * @see org.moflon.core.propertycontainer.MoflonPropertiesContainer
    * @generated
    */
   public Adapter createMoflonPropertiesContainerAdapter()
   {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.moflon.core.propertycontainer.ReplaceGenModel <em>Replace Gen Model</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    * @return the new adapter.
    * @see org.moflon.core.propertycontainer.ReplaceGenModel
    * @generated
    */
   public Adapter createReplaceGenModelAdapter()
   {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.moflon.core.propertycontainer.PropertiesMapping <em>Properties Mapping</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    * @return the new adapter.
    * @see org.moflon.core.propertycontainer.PropertiesMapping
    * @generated
    */
   public Adapter createPropertiesMappingAdapter()
   {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.moflon.core.propertycontainer.AdditionalDependencies <em>Additional Dependencies</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    * @return the new adapter.
    * @see org.moflon.core.propertycontainer.AdditionalDependencies
    * @generated
    */
   public Adapter createAdditionalDependenciesAdapter()
   {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.moflon.core.propertycontainer.MetaModelProject <em>Meta Model Project</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    * @return the new adapter.
    * @see org.moflon.core.propertycontainer.MetaModelProject
    * @generated
    */
   public Adapter createMetaModelProjectAdapter()
   {
      return null;
   }

   /**
    * Creates a new adapter for the default case.
    * <!-- begin-user-doc -->
    * This default implementation returns null.
    * <!-- end-user-doc -->
    * @return the new adapter.
    * @generated
    */
   public Adapter createEObjectAdapter()
   {
      return null;
   }

} //PropertycontainerAdapterFactory
