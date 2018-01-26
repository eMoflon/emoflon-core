package org.moflon.emf.injection.build;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moflon.emf.injection.build.MatchingParametersChecker;

/**
 * Unit tests for {@link MatchingParametersChecker}.
 */
public class MatchingParameterCheckerTest
{
   private static final String P1 = "param1";

   private static final String P2 = "param1";

   private EcoreFactory factory;

   private MatchingParametersChecker parameterChecker;

   private EOperation operation;

   @Before
   public void setUp() throws Exception
   {
      factory = EcoreFactory.eINSTANCE;
      parameterChecker = new MatchingParametersChecker();
      operation = factory.createEOperation();

   }

   @Test
   public void testSingleUnqualifiedString1() throws Exception
   {

      addToParameters(createParameter(P1, createClassifier("EString", "java.lang.String")));

      shouldMatch(operation, list(P1), list("String"));
   }

   @Test
   public void testSingleUnqualifiedString2() throws Exception
   {

      addToParameters(createParameter(P1, createClassifier("EString", "String")));

      shouldMatch(operation, list(P1), list("String"));
   }

   @Test
   public void testSingleUnqualifiedString3() throws Exception
   {
      // The eParameter contains an invalid Java instance type ('String')
      addToParameters(createParameter(P1, createClassifier("EString", "String")));

      shouldNotMatch(operation, list(P1), list("java.lang.String"));
   }

   @Test
   public void testSingleQualifiedString() throws Exception
   {
      addToParameters(createParameter(P1, createClassifier("EString", "java.lang.String")));

      shouldMatch(operation, list(P1), list("java.lang.String"));
   }

   @Test
   public void testMismatchWithoutTypeInformationForEString() throws Exception
   {
      addToParameters(createParameter(P1, createClassifier("EString", null)));

      shouldNotMatch(operation, list(P1), list("java.lang.String"));
   }
   
   @Test
   public void whenEOperationHasTwoParametersAndParametersHaveOneParameter_thenShouldNotMatch() throws Exception
   {
      addToParameters(createParameter(P1, createClassifier("EString", "java.lang.String")));
      addToParameters(createParameter(P2, createClassifier("EString", "java.lang.String")));

      shouldNotMatch(operation, list(P1), list("java.lang.String"));
   }

   @Test
   public void whenEOperationHasWrongParameterOrder_thenShouldNotMatch() throws Exception
   {
      addToParameters(createParameter(P1, createClassifier("String", "java.lang.String")));
      addToParameters(createParameter(P2, createClassifier("EInt", "int")));

      shouldNotMatch(operation, list(P1, P2), list("int", "java.lang.String"));
   }

   private void addToParameters(final EParameter parameter)
   {
      EList<EParameter> parameters = operation.getEParameters();
      parameters.add(parameter);
   }

   private EParameter createParameter(final String parameterName, final EClassifier parameterType)
   {
      EParameter parameter = factory.createEParameter();
      parameter.setName(parameterName);
      parameter.setEType(parameterType);
      return parameter;
   }

   private EClass createClassifier(final String className, final String instanceClassName)
   {
      EClass classifier = factory.createEClass();
      classifier.setName(className);
      classifier.setInstanceClassName(instanceClassName);
      return classifier;
   }

   private void shouldMatch(final EOperation operation, final List<String> parameterNames, final List<String> parameterTypes)
   {
      Assert.assertTrue(parameterChecker.haveMatchingParamters(operation, parameterTypes));
   }

   private void shouldNotMatch(final EOperation operation, final List<String> parameterNames, final List<String> parameterTypes)
   {
      Assert.assertFalse(parameterChecker.haveMatchingParamters(operation, parameterTypes));
   }

   private static List<String> list(final String... strings)
   {
      return Arrays.asList(strings);
   }
}
