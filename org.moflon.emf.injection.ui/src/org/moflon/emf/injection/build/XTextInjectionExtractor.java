package org.moflon.emf.injection.build;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenOperation;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.moflon.core.utilities.EclipseResourceUtils;
import org.moflon.core.utilities.MoflonUtil;
import org.moflon.core.utilities.WorkspaceHelper;
import org.moflon.emf.injection.injectionLanguage.ClassDeclaration;
import org.moflon.emf.injection.injectionLanguage.ClassInjectionDeclaration;
import org.moflon.emf.injection.injectionLanguage.InjectionFile;
import org.moflon.emf.injection.injectionLanguage.MethodDeclaration;
import org.moflon.emf.injection.injectionLanguage.RegularImport;
import org.moflon.emf.injection.injectionLanguage.StaticImport;
import org.moflon.emf.injection.unparsing.InjectionConstants;
import org.moflon.emf.injection.validation.MissingEClassValidationMessage;
import org.moflon.emf.injection.validation.MissingEOperationValidationMessage;

/**
 * This {@link InjectionExtractor} builds on an Xtext grammar for injection files
 * @author Roland Kluge - Initial implementation
 */
public class XTextInjectionExtractor implements InjectionExtractor
{
   private final HashMap<EOperation, String> modelCode;

   private final HashMap<String, String> membersCode;

   private final HashMap<String, List<String>> imports;

   private final HashMap<String, GenClass> fqnToGenClassMap;

   private final ClassNameToPathConverter classNameToPathConverter;

   private final IFolder injectionRootFolder;

   private final GenModel genModel;

   private final XTextInjectionParser injectionParser;

   /**
    * Prepares this injection extractor to extract injections from the given {@link IFolder}.
    * The given {@link GenModel} is used to identify corresponding {@link GenClass}es and {@link GenOperation}s
    */
   public XTextInjectionExtractor(final IFolder injectionFolder, final GenModel genModel)
   {
      this.modelCode = new HashMap<EOperation, String>();
      this.membersCode = new HashMap<String, String>();
      this.imports = new HashMap<String, List<String>>();
      this.fqnToGenClassMap = new HashMap<String, GenClass>();
      this.injectionRootFolder = injectionFolder;
      this.classNameToPathConverter = new ClassNameToPathConverter(WorkspaceHelper.GEN_FOLDER);
      this.genModel = genModel;
      this.injectionParser = new XTextInjectionParser();
   }

   @Override
   public IStatus extractInjections()
   {
      final MultiStatus resultStatus = new MultiStatus(WorkspaceHelper.getPluginId(getClass()), 0, "Problems during injection extraction", null);
      try
      {
         processGenModel();

         this.injectionRootFolder.accept(new IResourceVisitor() {

            @Override
            public boolean visit(final IResource resource) throws CoreException
            {
               final IFile injectionFile = resource.getAdapter(IFile.class);
               if (shallExtractInjectionsFromFile(injectionFile))
               {
                  extractInjectionFromFile(injectionFile, resultStatus);
               }
               return true;
            }
         });
      } catch (final CoreException e)
      {
         resultStatus.add(new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(getClass()), "Exception during injection extraction", e));
      }
      return resultStatus.matches(IStatus.WARNING) ? resultStatus : Status.OK_STATUS;
   }

   @Override
   public Collection<String> getImportsPaths()
   {
      return this.imports.keySet();
   }

   @Override
   public List<String> getImports(final String fullyQualifiedName)
   {
      return imports.getOrDefault(fullyQualifiedName, new ArrayList<>());
   }

   @Override
   public List<String> getAllImports()
   {
      final List<String> allRawImports = new ArrayList<>();
      for (final String file : this.imports.keySet())
      {
         allRawImports.addAll(this.imports.get(file));
      }
      return allRawImports;
   }

   @Override
   public boolean hasModelCode(final EOperation eOperation)
   {
      return modelCode.containsKey(eOperation);
   }

   @Override
   public String getModelCode(final EOperation eOperation)
   {
      return modelCode.get(eOperation);
   }

   @Override
   public Set<String> getMembersPaths()
   {
      return membersCode.keySet();
   }

   @Override
   public String getMembersCode(final String fullyQualifiedName)
   {
      return membersCode.get(fullyQualifiedName);
   }

   @Override
   public String getMembersCodeByClassName(final String className)
   {
      final String path = this.classNameToPathConverter.toPath(className);
      return this.getMembersCode(path);
   }

   private boolean shallExtractInjectionsFromFile(final IFile injectionFile)
   {
      return injectionFile != null && isInjectionFile(injectionFile) && !shallIgnoreFile(injectionFile);
   }

   private boolean isInjectionFile(final IFile injectionFile)
   {
      return WorkspaceHelper.INJECTION_FILE_EXTENSION.equals(injectionFile.getFileExtension());
   }

   /**
    * Returns true of the given file's name starts with {@link InjectionConstants#IGNORE_FILE_PREFIX}
    */
   private boolean shallIgnoreFile(final IFile file)
   {
      return file.getName().startsWith(InjectionConstants.IGNORE_FILE_PREFIX);
   }

   /**
    * Extracts all injection information from the given file.
    *
    * We may assume that {@link #shallExtractInjectionsFromFile(IFile)} is true for the given {@link IFile}
    */
   private void extractInjectionFromFile(final IFile injectionFile, final MultiStatus resultStatus)
   {
      final String fullyQualifiedClassName = buildInjectionPathDescription(injectionFile, ".");
      final String filePath = buildInjectionPathDescription(injectionFile, "/").concat(".").concat(WorkspaceHelper.INJECTION_FILE_EXTENSION);
      final GenClass genClass = fqnToGenClassMap.get(fullyQualifiedClassName);
      if (genClass != null)
      {
         final URI uri = URI
               .createPlatformResourceURI(injectionFile.getProject().getName() + "/" + injectionRootFolder.getProjectRelativePath() + "/" + filePath, false);
         try
         {
            final InjectionFile parsedFile = (InjectionFile) injectionParser.parse(uri);
            validateConnsistentClassName(injectionFile, parsedFile, resultStatus);
            processInjectionFile(parsedFile, genClass, fullyQualifiedClassName, injectionFile, resultStatus);
         } catch (final IOException e)
         {
            resultStatus.add(new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(getClass()), "Exception during injection extraction", e));
         }
      } else
      {
         reportMissingEClass(fullyQualifiedClassName, injectionFile, resultStatus);
      }
   }

   /**
    * Adds an error to the {@link MultiStatus} if the specified class names in both files mismatch
    */
   private void validateConnsistentClassName(final IFile injectionFile, final InjectionFile parsedFile, final MultiStatus resultStatus)
   {
      final String classDeclarationName = parsedFile.getClassDeclaration().getClassName();
      if (!classDeclarationName.equals(EclipseResourceUtils.getBasename(injectionFile)))
      {
         resultStatus.add(new Status(IStatus.WARNING, WorkspaceHelper.getPluginId(getClass()),
               String.format("Basename of injection file '%s' differs from class name '%s' declared inside the file.", injectionFile, classDeclarationName)));
      }
   }

   /**
    * Extracts all relevant information from the {@link GenModel}
    */
   private void processGenModel()
   {
      for (final GenPackage genPackage : this.genModel.getGenPackages())
      {
         processGenPackage(genPackage);
      }
   }

   /**
    * Extracts the fully-qualified name of each {@link GenClass} in the given {@link GenPackage} and adds it to {@link #fqnToGenClassMap}
    * @param genPackage the {@link GenPackage} to process
    */
   private final void processGenPackage(final GenPackage genPackage)
   {
      for (final GenClass genClass : genPackage.getGenClasses())
      {
         this.fqnToGenClassMap.put(CodeInjectionPlugin.getInterfaceName(genClass), genClass);
         this.fqnToGenClassMap.put(CodeInjectionPlugin.getClassName(genClass), genClass);
      }
      for (final GenPackage subPackage : genPackage.getSubGenPackages())
      {
         processGenPackage(subPackage);
      }
   }

   private void reportMissingEClass(final String fullyQualifiedClassName, final IFile injectionFile, final MultiStatus resultStatus)
   {
      final String fullPath = buildInjectionPathDescription(injectionFile, "/").concat(".").concat(WorkspaceHelper.INJECTION_FILE_EXTENSION);
      MissingEClassValidationMessage message = new MissingEClassValidationMessage(fullyQualifiedClassName, fullPath);
      resultStatus.add(message.convertToStatus());
   }

   /**
    * Stores
    * @param parsedFile
    * @param genClass
    * @param fullyQualifiedClassName
    */
   private void processInjectionFile(final InjectionFile parsedFile, final GenClass genClass, final String fullyQualifiedClassName, final IFile injectionFile,
         final MultiStatus resultStatus)
   {
      final List<String> perClassImports = new ArrayList<>();
      for (final EObject anImport : parsedFile.getImports())
      {
         if (anImport instanceof StaticImport)
         {
            perClassImports.add("static " + StaticImport.class.cast(anImport).getNamespace());
         } else if (anImport instanceof RegularImport)
         {
            perClassImports.add(RegularImport.class.cast(anImport).getNamespace());
         }
      }
      this.imports.put(fullyQualifiedClassName, perClassImports);

      final ClassDeclaration classDeclaration = parsedFile.getClassDeclaration();
      final ClassInjectionDeclaration classInjectionDeclaration = classDeclaration.getClassInjectionDeclaration();
      if (classInjectionDeclaration != null)
      {
         this.membersCode.put(fullyQualifiedClassName, normalizeCodeBody(classInjectionDeclaration.getBody()));
      }

      for (final MethodDeclaration methodDeclaration : classDeclaration.getMethodDeclarations())
      {
         final EOperation correspondingEOperation = getCorrespondingEOperation(genClass, methodDeclaration, resultStatus);

         if (correspondingEOperation == null)
         {
            reportMissingEOperation(genClass, methodDeclaration, injectionFile, resultStatus);
         } else
         {
            final String methodBody = normalizeCodeBody(methodDeclaration.getBody());
            final StringBuilder code = new StringBuilder();
            code.append("\n");
            code.append(MoflonUtil.EOPERATION_MODEL_COMMENT);
            code.append("\n");
            code.append(methodBody);
            this.modelCode.put(correspondingEOperation, code.toString());
         }
      }
   }

   private String normalizeCodeBody(final String body)
   {
      final int indexOfBeginToken = body.indexOf(InjectionConstants.CODE_BEGIN_TOKEN);
      final int indexOfEndToken = body.indexOf(InjectionConstants.CODE_END_TOKEN);
      if (indexOfBeginToken < 0)
         throw new IllegalArgumentException("Expected begin token " + InjectionConstants.CODE_BEGIN_TOKEN);
      if (indexOfEndToken < 0)
         throw new IllegalArgumentException("Expected end token " + InjectionConstants.CODE_END_TOKEN);
      return body.substring(0, indexOfEndToken).substring(indexOfBeginToken + InjectionConstants.CODE_BEGIN_TOKEN.length());
   }

   /**
    * Searches for the EOperation in given EClass, that matches the given name and parameters
    *
    * @param surroundingClass
    *           The EClass to search in.
    * @param methodName
    *           The name of the searched method.
    * @param paramTypes
    *           The types of the parameters in the same order as the names.
    * @return The EOperation with the given name and parameters. Guaranteed to be non-null.
    * @throws CoreException
    */
   private EOperation getCorrespondingEOperation(final GenClass surroundingClass, final MethodDeclaration methodDeclaration, final MultiStatus resultStatus)
   {
      final String methodName = methodDeclaration.getMethodName();
      final List<String> paramTypes = extractParameterTypes(methodDeclaration);
      EOperation result = null;

      final List<EOperation> eOperationsWithMatchingName = getEOperationsByName(surroundingClass, methodName);

      for (final EOperation eOperation : eOperationsWithMatchingName)
      {
         if (hasMatchingParameters(eOperation, paramTypes))
         {
            result = eOperation;
            break;
         }
      }

      return result;
   }

   private List<String> extractParameterTypes(final MethodDeclaration methodDeclaration)
   {
      return methodDeclaration.getParameters().stream().map(declaration -> declaration.getParameterType()).collect(Collectors.toList());
   }

   /**
    * Searches in a List of EOperations for all those with the given name.
    *
    * @param eClass
    *           The List to search in.
    * @param name
    *           The name of the searched EOperations.
    * @return All EOperations in the List with the given name.
    */
   private List<EOperation> getEOperationsByName(final GenClass eClass, final String name)
   {
      final ArrayList<EOperation> result = new ArrayList<EOperation>();
      for (final EOperation eOperation : eClass.getEcoreClass().getEOperations())
      {
         if (eOperation.getName().equals(name))
            result.add(eOperation);
      }
      return result;
   }

   /**
    * Checks if the parameters of given EOperation match the given method parameter.
    *
    * @param eOperationToCheck
    *           The EOperation to check.
    * @param parameterNames
    *           The names of the parameters.
    * @param paramTypes
    *           The types of the parameters.
    * @return True if the parameters matches the given lists, else false;
    */
   private boolean hasMatchingParameters(final EOperation eOperationToCheck, final List<String> paramTypes)
   {
      final MatchingParametersChecker parametersChecker = new MatchingParametersChecker();
      return parametersChecker.haveMatchingParamters(eOperationToCheck, paramTypes);
   }

   private void reportMissingEOperation(final GenClass surroundingClass, final MethodDeclaration methodDeclaration, final IFile injectionFile,
         final MultiStatus resultStatus)
   {
      final String fullPath = buildInjectionPathDescription(injectionFile, "/").concat(".").concat(WorkspaceHelper.INJECTION_FILE_EXTENSION);
      final MissingEOperationValidationMessage message = new MissingEOperationValidationMessage(methodDeclaration.getMethodName(),
            extractParameterTypes(methodDeclaration), surroundingClass.getName(), fullPath);
      resultStatus.add(message.convertToStatus());
   }

   private String buildInjectionPathDescription(final IFile injectionFile, final String separator)
   {
      final LinkedList<String> pathElements = new LinkedList<>();
      IContainer parentContainer = injectionFile.getParent();
      while (!parentContainer.equals(this.injectionRootFolder))
      {
         pathElements.addFirst(parentContainer.getName());
         parentContainer = parentContainer.getParent();
      }
      pathElements.add(EclipseResourceUtils.getBasename(injectionFile));
      return StringUtils.join(pathElements, separator);
   }
}
