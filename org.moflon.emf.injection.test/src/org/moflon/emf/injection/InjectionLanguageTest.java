package org.moflon.emf.injection;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moflon.emf.injection.injectionLanguage.ClassDeclaration;
import org.moflon.emf.injection.injectionLanguage.ClassInjectionDeclaration;
import org.moflon.emf.injection.injectionLanguage.InjectionFile;
import org.moflon.emf.injection.injectionLanguage.RegularImport;
import org.moflon.emf.injection.injectionLanguage.StaticImport;

import com.google.inject.Injector;

public class InjectionLanguageTest
{
   private InjectionLanguageTestParser parser;

   @Before
   public void setUp() {
      parser = new InjectionLanguageTestParser();
   }
   
   @Test
   public void testEmptyClassInjection() throws Exception
   {
      final URI uri = URI.createFileURI("test/org/moflon/emf/injection/TestEmptyClass.inject");
      
      final InjectionFile injectionFile = InjectionFile.class.cast(parser.parse(uri));
      Assert.assertEquals(0, injectionFile.getImports().size());
      
      final ClassDeclaration classDecl = injectionFile.getClassDeclaration();
      Assert.assertEquals("TestEmptyClass", classDecl.getClassName());
      Assert.assertNull(classDecl.getClassInjectionDeclaration());
      Assert.assertEquals(0, classDecl.getMethodDeclarations().size());
   }

   @Test
   public void testVariableInjection() throws Exception
   {
      final URI uri = URI.createFileURI("test/org/moflon/emf/injection/TestVariable.inject");

      final InjectionFile injectionFile = InjectionFile.class.cast(parser.parse(uri));
      Assert.assertEquals(3, injectionFile.getImports().size());
      Assert.assertTrue(injectionFile.getImports().get(0) instanceof RegularImport);
      Assert.assertTrue(injectionFile.getImports().get(1) instanceof RegularImport);
      Assert.assertTrue(injectionFile.getImports().get(2) instanceof StaticImport);

      final ClassDeclaration classDecl = injectionFile.getClassDeclaration();
      Assert.assertEquals("TestVariable", classDecl.getClassName());
      ClassInjectionDeclaration classInjectionDeclaration = classDecl.getClassInjectionDeclaration();
      classInjectionDeclaration.getBody();
      Assert.assertEquals(0, classDecl.getMethodDeclarations().size());
   }
   
   @Test
   public void testMoflonDevInjections() throws Exception
   {
      final String folder = "test/org/moflon/emf/injection/samples/dev";
      final String[] devSampleFiles = new File(folder).list((dir, name) -> name.endsWith(".inject"));
      for (final String devSampleFile : devSampleFiles)
      {
         final String fullPath = folder + "/" + devSampleFile;
         final URI uri = URI.createFileURI(fullPath);
         final InjectionFile injectionFile = InjectionFile.class.cast(parser.parse(uri));
         Assert.assertNotNull("Problem with file: " + fullPath, injectionFile.getClassDeclaration());
      }
   }

   @Test
   public void testMoflonTestInjections() throws Exception
   {
      final String folder = "test/org/moflon/emf/injection/samples/test";
      final String[] devSampleFiles = new File(folder).list((dir, name) -> name.endsWith(".inject"));
      for (final String devSampleFile : devSampleFiles)
      {
         final String fullPath = folder + "/" + devSampleFile;
         final URI uri = URI.createFileURI(fullPath);
         final InjectionFile injectionFile = InjectionFile.class.cast(parser.parse(uri));
         Assert.assertNotNull("Problem with file: " + fullPath, injectionFile.getClassDeclaration());
      }
   }

   // Source: http://www.davehofmann.de/?p=101
   private class InjectionLanguageTestParser
   {

      private XtextResourceSet resourceSet;

      public InjectionLanguageTestParser()
      {
         setupParser();
      }

      private void setupParser()
      {
         // See also: https://wiki.eclipse.org/Xtext/FAQ#How_do_I_load_my_model_in_a_standalone_Java_application.C2.A0.3F
         final Injector injector = new InjectionLanguageStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
         resourceSet = injector.getInstance(XtextResourceSet.class);
         resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
      }

      /**
       * Parses a resource specified by an URI and returns the resulting object tree root element.
       * @param uri URI of resource to be parsed
       * @return Root model object
       * @throws IOException 
       */
      public EObject parse(URI uri) throws IOException
      {
         Resource resource = resourceSet.createResource(uri);
         resource.load(null);
         return resource.getContents().get(0);
      }
   }
}
