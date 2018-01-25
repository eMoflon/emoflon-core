package org.moflon.core.propertycontainer;

import org.eclipse.emf.ecore.EDataType;
import org.moflon.core.propertycontainer.impl.PropertycontainerFactoryImpl;

/**
 * This factory override the EMF-generated factory for elements of the 
 * MoflonPropertyContainer to support the proper handling of method body handlers.  
 */
public class MoflonPropertyContainerCustomizedFactory extends PropertycontainerFactoryImpl
{

   @Override
   public SDMCodeGeneratorIds createSDMCodeGeneratorIdsFromString(final EDataType eDataType, final String initialValue)
   {
      final SDMCodeGeneratorIds result = SDMCodeGeneratorIds.getByName(initialValue);
      if (result == null)
         throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
      return result;
   }

   @Override
   public String convertSDMCodeGeneratorIdsToString(final EDataType eDataType, final Object instanceValue)
   {
      return instanceValue == null ? null : ((SDMCodeGeneratorIds) instanceValue).getName();
   }
}
