package org.moflon.core.xtext.scoping;

import java.util.HashMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.util.SimpleAttributeResolver;

public class MoflonEObjectDescription extends EObjectDescription
{
   public MoflonEObjectDescription(EObject element)
   {
      super(QualifiedName.create(getName(element)), element, new HashMap<>());
   }
   
   private static String getName(EObject element){
      String name = SimpleAttributeResolver.NAME_RESOLVER.apply(element);
      
      return name;
   }
   
   @Override
   public URI getEObjectURI()
   {
      URI uri = EcoreUtil2.getPlatformResourceOrNormalizedURI(this.getEObjectOrProxy());
      return uri;
   }

}
