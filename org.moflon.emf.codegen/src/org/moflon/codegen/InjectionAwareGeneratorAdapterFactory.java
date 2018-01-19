package org.moflon.codegen;
import org.eclipse.emf.common.notify.Adapter;
import org.moflon.emf.injection.build.InjectionManager;

public class InjectionAwareGeneratorAdapterFactory extends GeneratorAdapterFactory
      implements org.eclipse.emf.codegen.ecore.generator.GeneratorAdapterFactory.Descriptor
{
   public InjectionAwareGeneratorAdapterFactory(final InjectionManager injectionManager)
   {
      this.injectionManager = injectionManager;
   }

   public Adapter createGenClassAdapter()
   {
      if (genClassGeneratorAdapter == null)
      {
         genClassGeneratorAdapter = new InjectionAwareClassGeneratorAdapter(this);
      }
      return genClassGeneratorAdapter;
   }

   @Override
   public InjectionAwareGeneratorAdapterFactory createAdapterFactory()
   {
      return this;
   }
}
