package org.moflon.emf.injection.unparsing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
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

   public MethodVisitor(final String codeForProject)
   {
      final ASTParser parser = ASTParser.newParser(AST.JLS8);
      parser.setSource(codeForProject.toCharArray());
      parser.setIgnoreMethodBodies(true);

      final CompilationUnit node = (CompilationUnit) parser.createAST(null);

      node.accept(this);
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
