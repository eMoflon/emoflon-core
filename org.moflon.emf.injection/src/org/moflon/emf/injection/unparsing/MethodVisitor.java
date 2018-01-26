package org.moflon.emf.injection.unparsing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.moflon.core.utilities.MoflonUtil;

/**
 * A helper class for extracting specified methods from the AST created by parsing the generated code from CodeGen2.
 * Parsing is performed by reusing {@link org.eclipse.jdt.core.dom.ASTParser}.
 *
 * @author Anthony Anjorin
 */
public class MethodVisitor extends ASTVisitor
{
   private final List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();

   private final String codeForProject;

   public MethodVisitor(final String codeForProject)
   {
      this.codeForProject = codeForProject;

      final ASTParser parser = ASTParser.newParser(AST.JLS8);
      parser.setSource(codeForProject.toCharArray());
      parser.setIgnoreMethodBodies(true);

      final CompilationUnit node = (CompilationUnit) parser.createAST(null);

      node.accept(this);
   }

   /**
    * @deprecated Not used anymore? (rkluge, 2017-12-18)
    */
   @Deprecated
   public String extractCodeForMethod(final EOperation eOperation, final IProject project)
   {
      for (final MethodDeclaration method : methods)
      {
         // Method has the same name as eOperation
         if (method.getName().toString().trim().equals(eOperation.getName().trim()))
         {
            // Check class name
            if (((TypeDeclaration) method.getParent()).getName().toString().trim().equals(eOperation.getEContainingClass().getName().trim() + "Impl"))
            {
               // Check arguments (number of args, matching names and types)
               if (method.parameters().size() == eOperation.getEParameters().size())
               {
                  // Same number of arguments, check names and types
                  boolean argumentsMatch = true;
                  for (int i = 0; i < method.parameters().size() && argumentsMatch; i++)
                  {
                     final SingleVariableDeclaration parameter = (SingleVariableDeclaration) method.parameters().get(i);

                     final String paramName = parameter.getName().toString().trim();
                     final String paramType = parameter.getType().toString().trim();
                     final EParameter eParam = eOperation.getEParameters().get(i);

                     argumentsMatch = paramName.equals(eParam.getName().trim()) && compareToClassifier(paramType, eParam.getEType(), project);
                  }

                  if (argumentsMatch && method.getBody() != null)
                  {
                     // Extract code
                     final String block = codeForProject
                           .substring(method.getBody().getStartPosition() + 1, method.getBody().getLength() + method.getBody().getStartPosition() - 1).trim();

                     // Only return if code was found, if not continue search
                     if (!block.isEmpty())
                        return block;
                  }
               }
            }
         }
      }

      return MoflonUtil.DEFAULT_METHOD_BODY;
   }

   private boolean compareToClassifier(final String paramType, final EClassifier eType, final IProject project)
   {
      final String nameOfEType = getNameOfClassifier(eType).trim();

      // Types are identical
      if (paramType.equals(nameOfEType))
         return true;

      // Check if eType has prefix which can be removed
      if (nameOfEType.lastIndexOf(".") != -1 && !nameOfEType.endsWith("."))
      {
         return paramType.equals(nameOfEType.substring(nameOfEType.lastIndexOf(".") + 1));
      }

      // Commented due to binary dependency to MoflonPropertiesContainer (rkluge, 2017-12-18)
      // Last try: Check if eType should be fully qualified.  Entry is first corrected with user mappings!
      //      final MoflonPropertiesContainer properties =
      //    		  MoflonPropertiesContainerHelper.load(project, new NullProgressMonitor());
      //
      //      final Map<String, String> importMappings = MoflonPropertiesContainerHelper.mappingsToMap(properties.getImportMappings());
      //      String fqn = MoflonUtil.getFQN(eType.getEPackage());
      //      if(importMappings.containsKey(fqn))
      //         fqn = importMappings.get(fqn);
      //
      //      return paramType.equals(fqn + "." + eType.getName());
      return false;
   }

   @Override
   public boolean visit(final MethodDeclaration node)
   {
      methods.add(node);
      return super.visit(node);
   }

   public List<MethodDeclaration> getMethods()
   {
      return methods;
   }

   public static String signatureFor(final EOperation eOperation)
   {
      String signature = MoflonUtil.getFQN(eOperation.getEContainingClass()) + "_" + eOperation.getName();
      for (final EParameter param : eOperation.getEParameters())
      {
         signature += "_" + param.getName() + "_" + getNameOfClassifier(param.getEType());
      }

      return signature;
   }

   public static String getNameOfClassifier(final EClassifier type)
   {
      return type.getInstanceTypeName() != null ? type.getInstanceTypeName() : type.getName();
   }
}
