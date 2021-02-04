# SmartEMF - emfcodegenerator v0.8

This package provides the capability to generate [EMF-similar](https://www.eclipse.org/modeling/emf/)
code given an Ecore and Genmodel file.
The generated code has not been tested and verified yet. This is just the first more or less working
version of the generator.

## Provided functionality
The generator is capable of generating code for following EObject types:
* EClass
	* type modifiers including:
		1. ETypeParameter
		2. EBound
		3. EGenericType (generic super types)
	* EAttribute
	* EReference
	* EOperation
		* following is supported:
			1. EGeneric Return Type
			2. EType Parameters
			3. EParameter
			4. EGeneric Exceptions
		* following is not supported:
			1. implementation generation
			2. eInvoke-method in EClass as usage of EOperation's is discouraged (by requirements for this project)
	* EEnum
	* EDataType

## Functionality in future releases
* Containment of EReference and EAttribute is not provided yet
* Replacements for EList and similar sub-types
* proper interfacing to call generator
* TBA

## Functionality which is not being supported (as of now)
* EAnnotation

## How to use?
There is no proper interface to communicate with the generator yet. For now the generator is started
by providing the paths to the Ecore-xmi and the Genmodel-xmi in the TestMain.xtend file and run the
main-method.

## How does it work?
Basically the Ecore- and Genmodel-files are parsed using the EMF framework. This is done by the
[EcoreGenmodelParser](./EcoreGenmodelParser.xtend) which then inspects those files and creates
[Inspectors](./inspectors/Inspector.xtend) for the [EAttributes](./inspectors/util/AttributeInspector.xtend)
[EReferences](./inspectors/util/ReferenceInspector.xtend) and [EPackages](./inspectors/util/PackageInspector.xtend).
These Inspectors inspect their respective EObject and gather all needed info for code generation.
Afterwards a [FileCreator](./creators/FileCreator.xtend) can use these Inspectors to generate the needed
code.

## TODO
* update README
* complete documentation
* complete JavaDoc
* test functionality of code
* generate emfcodegenerator.util classes instead of leaving them in generator package,
  as the generated code needs it and not the generator itself
* discern when EDataTypes are flagged as "IS_GENERATED_INSTANCE_CLASS" as I have not yet
  been able to produce a test meta-model where the flag is set to true
* the initEReference()-method uses an "IS_COMPOSITE" for the "isContainment"-parameter - verify
  if the EReference.isContainment() gives the correct info or not.
