package org.moflon.emf.injection.tests;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moflon.emf.injection.InjectionLanguageStandaloneSetupGenerated;
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
   public void setUp()
   {
      parser = new InjectionLanguageTestParser();
   }

   @Test
   public void testEmptyClassInjection() throws Exception
   {
      final String classpathFolderName = "/org/moflon/emf/injection/tests/samples/small/TestEmptyClass.inject";
      final URL url = getClass().getResource(classpathFolderName);
      final URI uri = URI.createURI(url.toURI().toString());

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
      final String classpathFolderName = "/org/moflon/emf/injection/tests/samples/small/TestVariable.inject";
      final URL url = getClass().getResource(classpathFolderName);
      final URI uri = URI.createURI(url.toURI().toString());

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
      final String classpathFolderName = "/org/moflon/emf/injection/tests/samples/dev";
      final List<String> filenames = Arrays.asList("AbstractClassNodeRuleImpl.inject", //
            "AddedEdgeForAllNewRuleImpl.inject", //
            "AddedEdgeForExistingNodesRuleImpl.inject", //
            "AttributeConstraintBlackAndNacPatternTransformerImpl.inject", //
            "AttributeConstraintBlackPatternInvocationBuilderImpl.inject", //
            "AttributeConstraintGreenPatternTransformerImpl.inject", //
            "AttributeConstraintLibraryImpl.inject", //
            "AttributeConstraintLibUtilImpl.inject", //
            "AttributeConstraintsExporterImpl.inject", //
            "AttributeDeltaForCreationRuleImpl.inject", //
            "AttributeDeltaForExistingRuleImpl.inject", //
            "AttributeVariableImpl.inject", //
            "BindingAndBlackPatternBuilderImpl.inject", //
            "BindingFeatureHelperImpl.inject", //
            "BlackAndNacPatternTransformerImpl.inject", //
            "BlackPatternBuilderImpl.inject", //
            "BoxImpl.inject", //
            "CatImpl.inject", //
            "CCHelperImpl.inject", //
            "CCMatch.inject", //
            "CCMatchImpl.inject", //
            "CodeAdapterImpl.inject", //
            "ConstantTransformerImpl.inject", //
            "ContentContainerImpl.inject", //
            "ControlFlowBuilderImpl.inject", //
            "CoordinateImpl.inject", //
            "CspCodeGeneratorHelperImpl.inject", //
            "CspCodeGeneratorImpl.inject", //
            "CSPImpl.inject", //
            "CSPPrecompilerHelperImpl.inject", //
            "CSPSearchPlanAdapterImpl.inject", //
            "DatabaseValidationImpl.inject", //
            "DECNACAnalysisImpl.inject", //
            "DefaultExpressionTransformerImpl.inject", //
            "DeltaAxiomImpl.inject", //
            "DepthFirstSearchImpl.inject", //
            "DirectedTriangleTopologyControlAlgorithmImpl.inject", //
            "Ecore2MocaXMIConverterAdapterImpl.inject", //
            "EdgeImpl.inject", //
            "EdgeStateBasedConnectivityConstraintImpl.inject", //
            "EMoflonEdgeImpl.inject", //
            "ExistingInheritanceRuleImpl.inject", //
            "ExporterImpl.inject", //
            "ExportUtilImpl.inject", //
            "ExpressionExporterImpl.inject", //
            "ExpressionTransformerImpl.inject", //
            "ExtendsToNodeRuleImpl.inject", //
            "GraphElementImpl.inject", //
            "GraphImpl.inject", //
            "HelperImpl.inject", //
            "IdentifierAllocatorImpl.inject", //
            "IdentifyerHelperImpl.inject", //
            "InjectionHelperImpl.inject", //
            "LinkImpl.inject", //
            "LinkVariablePostProcessingHelperImpl.inject", //
            "Match.inject", //
            "MatchImpl.inject", //
            "MemberImpl.inject", //
            "MemoryBoxUtilImpl.inject", //
            "MocaCompareImpl.inject", //
            "ModelgeneratorRuleResultImpl.inject", //
            "ModelGenUtilImpl.inject", //
            "MoDiscoTGGPreprocessingImpl.inject", //
            "NacPatternBuilderImpl.inject", //
            "Node.inject", //
            "NodeImpl.inject", //
            "OperationSpecificationGroupImpl.inject", //
            "OSMLanguageUtilImpl.inject", //
            "PackageAxiomImpl.inject", //
            "ParserImpl.inject", //
            "PatternInvocationBuilderImpl.inject", //
            "PatternMatcherImpl.inject", //
            "PatternVariableHandlerImpl.inject", //
            "PrecompileLogImpl.inject", //
            "PrecompilerHelperImpl.inject", //
            "PrecompilerImpl.inject", //
            "ProblemImpl.inject", //
            "RegularPatternInvocationBuilderImpl.inject", //
            "RuleRefinementPrecompilerImpl.inject", //
            "ScopeValidatorImpl.inject", //
            "StoryPatternHelperImpl.inject", //
            "StringValueImpl.inject", //
            "TAbstractTypeImpl.inject", //
            "TClassImpl.inject", //
            "TemplateUnparserImpl.inject", //
            "TestEmptyClass.inject", //
            "TestVariable.inject", //
            "TFieldDefinitionImpl.inject", //
            "TFieldSignatureImpl.inject", //
            "TGGCompiler.inject", //
            "TGGCompilerImpl.inject", //
            "TGGConstraintImpl.inject", //
            "TggExporter.inject", //
            "TggExporterImpl.inject", //
            "TGGGrammarDirectedGraphAxiomImpl.inject", //
            "TGGRuleMorphism.inject", //
            "TGGRuleMorphismImpl.inject", //
            "TMethodDefinitionImpl.inject", //
            "TMethodSignatureImpl.inject", //
            "TransformerImpl.inject", //
            "TreeElementImpl.inject", //
            "UnparserImpl.inject", //
            "ValidationReportImpl.inject", //
            "ValidatorImpl.inject", //
            "Variable.inject", //
            "VariableImpl.inject", //
            "VariableTypeManagerImpl.inject", //
            "XMLParserImpl.inject", //
            "XMLUnparserImpl.inject");
      parseAllInjectFiles(filenames.stream().map(filename -> classpathFolderName + "/" + filename).collect(Collectors.toList()));
   }

   @Test
   public void testMoflonTestInjections() throws Exception
   {
      final String classpathFolderName = "/org/moflon/emf/injection/tests/samples/test";
      final List<String> filenames = Arrays.asList("BoxImpl.inject", //
            "CatImpl.inject", //
            "ContentContainerImpl.inject", //
            "CoordinateImpl.inject", //
            "DatabaseValidationImpl.inject", //
            "DirectedTriangleTopologyControlAlgorithmImpl.inject", //
            "EdgeImpl.inject", //
            "EdgeStateBasedConnectivityConstraintImpl.inject", //
            "ExtendsToNodeRuleImpl.inject", //
            "GraphElementImpl.inject", //
            "GraphImpl.inject", //
            "HelperImpl.inject", //
            "LinkImpl.inject", //
            "MemberImpl.inject", //
            "MemoryBoxUtilImpl.inject", //
            "MoDiscoTGGPreprocessingImpl.inject", //
            "NodeImpl.inject", //
            "OSMLanguageUtilImpl.inject", //
            "TAbstractTypeImpl.inject", //
            "TClassImpl.inject", //
            "TFieldDefinitionImpl.inject", //
            "TFieldSignatureImpl.inject", //
            "TMethodDefinitionImpl.inject", //
            "TMethodSignatureImpl.inject");
      parseAllInjectFiles(filenames.stream().map(filename -> classpathFolderName + "/" + filename).collect(Collectors.toList()));
   }

   private List<InjectionFile> parseAllInjectFiles(final List<String> injectFilenames) throws IOException, URISyntaxException
   {
      final List<InjectionFile> injectionFiles = new ArrayList<>();
      for (final String filename : injectFilenames)
      {
         URL url = getClass().getResource(filename);
         URI uri = URI.createURI(url.toURI().toString());
         final InjectionFile injectionFile = InjectionFile.class.cast(parser.parse(uri));
         injectionFiles.add(injectionFile);
         Assert.assertNotNull("Problem with file: " + filename, injectionFile.getClassDeclaration());
      }
      return injectionFiles;
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
