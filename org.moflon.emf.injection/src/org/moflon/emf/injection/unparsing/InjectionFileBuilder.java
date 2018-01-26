package org.moflon.emf.injection.unparsing;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.moflon.core.utilities.EclipseResourceUtils;
import org.moflon.core.utilities.MoflonUtil;
import org.moflon.core.utilities.WorkspaceHelper;
import org.moflon.emf.injection.injectionLanguage.ClassDeclaration;
import org.moflon.emf.injection.injectionLanguage.ClassInjectionDeclaration;
import org.moflon.emf.injection.injectionLanguage.InjectionFile;
import org.moflon.emf.injection.injectionLanguage.InjectionLanguageFactory;
import org.moflon.emf.injection.injectionLanguage.ParameterDeclaration;
import org.moflon.emf.injection.injectionLanguage.RegularImport;
import org.moflon.emf.injection.injectionLanguage.StaticImport;

/**
 * This class is able to construct an {@link InjectionFile} from the contents of a Java file.
 * @author Roland Kluge - Initial implementation
 *
 */
public class InjectionFileBuilder
{
   public static InjectionFile createInjectionFile(final IFile javaFile, final MultiStatus status)
   {
      try
      {

         final InjectionLanguageFactory injectionFactory = InjectionLanguageFactory.eINSTANCE;
         final InjectionFile injectionFile = injectionFactory.createInjectionFile();

         final String className = EclipseResourceUtils.getBasename(javaFile);
         final ClassDeclaration classDeclaration = injectionFactory.createClassDeclaration();
         injectionFile.setClassDeclaration(classDeclaration);
         classDeclaration.setClassName(className);

         final String javaFileContent = readFileContents(javaFile);

         injectionFile.getImports().addAll(extractUserImports(javaFileContent));

         final ClassInjectionDeclaration classInjectionDeclaration = injectionFactory.createClassInjectionDeclaration();
         classDeclaration.setClassInjectionDeclaration(classInjectionDeclaration);
         final String membersCode = extractUserMembers(javaFileContent);
         if (membersCode != null)
         {
            classInjectionDeclaration.setBody(String.format("%s%s%s", InjectionConstants.CODE_BEGIN_TOKEN, membersCode, InjectionConstants.CODE_END_TOKEN));
         }

         classDeclaration.getMethodDeclarations().addAll(extractModelMethods(javaFileContent));

         return injectionFile;
      } catch (final CoreException e)
      {
         status.add(new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(InjectionFileBuilder.class),
               String.format("Error reading file: '%s'. Reason: %s", javaFile, e.getMessage()), e));
         return null;
      }
   }

   private static String readFileContents(final IFile javaFile) throws CoreException
   {
      InputStream javaContentStream = null;
      try
      {
         javaContentStream = javaFile.getContents();
         final Scanner scanner = new Scanner(javaContentStream);
         scanner.useDelimiter("\\A");
         final String javaFileContent = scanner.hasNext() ? scanner.next() : "";
         scanner.close();
         return javaFileContent;
      } finally
      {
         IOUtils.closeQuietly(javaContentStream);
      }
   }

   /**
   * Extracts the custom (static) imports from given file contents
   */
   private static Collection<EObject> extractUserImports(final String fileContent)
   {
      final InjectionLanguageFactory injectionFactory = InjectionLanguageFactory.eINSTANCE;
      final List<EObject> importDeclarations = new ArrayList<>();
      final int startIndex = fileContent.indexOf(InjectionConstants.USER_IMPORTS_BEGIN);
      final int endIndex = fileContent.indexOf(InjectionConstants.USER_IMPORTS_END);
      final int startIndexAfterToken = startIndex + InjectionConstants.USER_IMPORTS_BEGIN.length();

      if (startIndexAfterToken >= 0 && endIndex >= 0)
      {
         final String extractedImports = fileContent.substring(startIndexAfterToken, endIndex);

         if (!extractedImports.trim().equals(""))
         {
            final List<String> importLines = Arrays.asList(extractedImports.split(InjectionConstants.NL));
            importDeclarations.addAll(importLines.stream()//
                  .filter(importLine -> !importLine.trim().isEmpty())//
                  .map(importLine -> importLine.replaceAll("^import\\s+", ""))//
                  .map(importLine -> {
               if (importLine.startsWith("static "))
               {
                  final StaticImport importStatement = injectionFactory.createStaticImport();
                  importStatement.setNamespace(importLine.replaceFirst("static\\s+", ""));
                  return importStatement;
               } else
               {
                  final RegularImport importStatement = injectionFactory.createRegularImport();
                  importStatement.setNamespace(importLine);
                  return importStatement;
               }
            }).collect(Collectors.toList()));
         }
      }
      return importDeclarations;
   }

   /**
    * Extracts the per-class injection code from the given file content
    */
   private static String extractUserMembers(final String fileContent)
   {
      int startIndex, endIndex;
      startIndex = fileContent.indexOf(InjectionConstants.MEMBERS_BEGIN) + InjectionConstants.MEMBERS_BEGIN.length();
      endIndex = fileContent.indexOf(InjectionConstants.MEMBERS_END);

      final String membersCode;
      if (startIndex >= 0 && endIndex >= 0)
      {
         final String extractedMembers = fileContent.substring(startIndex, endIndex);

         membersCode = extractedMembers.trim().equals("") ? null : extractedMembers;
      } else
      {
         membersCode = null;
      }
      return membersCode;
   }

   private static Collection<org.moflon.emf.injection.injectionLanguage.MethodDeclaration> extractModelMethods(final String javaFileContent)
   {
      final InjectionLanguageFactory injectionFactory = InjectionLanguageFactory.eINSTANCE;
      final List<org.moflon.emf.injection.injectionLanguage.MethodDeclaration> methodDeclarations = new ArrayList<>();
      final MethodVisitor methodVisitor = new MethodVisitor(javaFileContent);
      final List<MethodDeclaration> methods = methodVisitor.getMethods();
      
      for (final MethodDeclaration method : methods)
      {
         final Block bodyBlock = method.getBody();
         if (bodyBlock != null)
         {
            final String body = getBlockBody(bodyBlock, javaFileContent);
            if (isInjectedModel(body) && !isDefaultBody(body))
            {
               org.moflon.emf.injection.injectionLanguage.MethodDeclaration methodDeclaration = injectionFactory.createMethodDeclaration();
               methodDeclarations.add(methodDeclaration);
               methodDeclaration.setMethodName(method.getName().toString());
               
               final String bodyWithoutComment = removeModelComment(body);
               methodDeclaration.setBody(String.format("%s%s%s", InjectionConstants.CODE_BEGIN_TOKEN, bodyWithoutComment, InjectionConstants.CODE_END_TOKEN));
               
               @SuppressWarnings("unchecked") // Generic type is only documented in Javadoc
               final List<SingleVariableDeclaration> parameters = (List<SingleVariableDeclaration>) method.parameters();
               parameters.stream()//
                     .forEach(parameter -> {
                        final ParameterDeclaration parameterDeclaration = injectionFactory.createParameterDeclaration();
                        methodDeclaration.getParameters().add(parameterDeclaration);
                        parameterDeclaration.setParameterName(parameter.getName().toString());
                        parameterDeclaration.setParameterType(parameter.getType().toString());
                     });
            }
         }
      }
      return methodDeclarations;
   }

   /**
    * Checks if a method body belongs to an injected model method
    * 
    * @param methodBody
    *           Body of the method
    * @return True, if the method is an injected model method
    */
   private static boolean isInjectedModel(final String methodBody)
   {
      return methodBody.trim().startsWith(MoflonUtil.EOPERATION_MODEL_COMMENT);
   }

   /**
    * Checks if a body matches the default content of a generated method
    */
   private static boolean isDefaultBody(final String body)
   {
      final String bodyWithoutTabs = normalizeForDefaultBodyCheck(body);
      return bodyWithoutTabs.equals(getNormalizedDefaultInjectedBody());
   }

   private static String getNormalizedDefaultInjectedBody()
   {
      return normalizeForDefaultBodyCheck(MoflonUtil.DEFAULT_METHOD_BODY);
   }

   private static String normalizeForDefaultBodyCheck(final String body)
   {
      String normalizedBody = body;
      normalizedBody = body.replaceAll("//\\s*TODO:[^\\n]*", "");
      normalizedBody = normalizedBody.replaceAll("\\s+", " ");
      normalizedBody = normalizedBody.trim();
      return normalizedBody;
   }

   /**
    * Extracts the given block of code from the contentString and returns it as a String
    * @param javaFileContent 
    */
   private static String getBlockBody(final Block block, final String javaFileContent)
   {
      return javaFileContent.substring(block.getStartPosition() + 1, block.getLength() + block.getStartPosition() - 1).trim();
   }

   /**
    * Removes the the leading comment of the model method
    * 
    * @param body
    *           Body of the method
    * @return Body without the comment
    */
   private static String removeModelComment(final String body)
   {
      if (body.equals(MoflonUtil.EOPERATION_MODEL_COMMENT))
         return "";
      else
         return body.substring(MoflonUtil.EOPERATION_MODEL_COMMENT.length() + 1);
   }
}
