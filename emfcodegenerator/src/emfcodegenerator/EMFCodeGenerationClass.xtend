package emfcodegenerator

import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.impl.EClassImpl
import org.eclipse.emf.ecore.EDataType
import java.util.HashSet
import java.util.Collection
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EcorePackage

/**
 * class containing useful methods which all inheriting classes can have in common
 */
class EMFCodeGenerationClass {
	
	/**########################Attributes########################*/

	/**
	 * The genmodel/ecore -xmi parser
	 */
	var static protected EcoreGenmodelParser emf_model
	
	/**
	 * The sub-directory relative to the working directory where the
	 * generated files shall be saved at
	 */
	val static protected String GENERATED_FILE_DIR = "./src-gen/"
	
	/**
	 * a HashSet containing strings of modules which need to be imported
	 * example: "java.util.Map"
	 */
	var protected HashSet<String> needed_imports = new HashSet<String>()

	/**
	 * stores the GenModel-XMI specified super package for the EMF code.
	 */
	var String super_package_name = null

	/**
	 * stores if this object was created with an String or an EcoreGenmodelParser instance and is
	 * used to determine if the String is used as the name for the super-package, or if it should be
	 * inquired from the parser.
	 */
	var boolean initialised_with_emf_model = true

	/**########################Constructors########################*/

	/**
	 * Constructs a new EMFCodeGenerationClass.
	 * @param gen_model EcoreGenmodelParser
	 * @author Adrian Zwenger
	 */
	new(EcoreGenmodelParser gen_model){
		if(gen_model !== null && !gen_model.equals(EMFCodeGenerationClass.emf_model))
			EMFCodeGenerationClass.emf_model = gen_model
		else if(gen_model === null && EMFCodeGenerationClass.emf_model === null)
			throw new IllegalArgumentException()
	}

	/**
	 * Constructs an EMFCodeGenerationClass with a given GenModel-XMI specified super-package name 
	 * String.<br> Take note, that all functionality which depends on an
	 * {@link #emf_model EcoreGenmodelParser} cannot be used until it is set.
	 * @param super_package_name String
	 * @author Adrian Zwenger
	 */
	new(String super_package_name){
		this.initialised_with_emf_model = false
		this.super_package_name = super_package_name
	}

	/**########################Import registration########################*/


	/**
	 * Takes an EClassifier and adds its import Strings (multiple possible) to the needed
	 * {@link #needed_imports needed imports}. Finally it returns all the imports which are needed
	 * for the given classifier contained in an HashSet.
	 * @param e_cl EClassifier
	 * @return HashSet<String>
	 * @author Adrian Zwenger
	 */
	def protected HashSet<String> add_import(EClassifier e_cl){
		var HashSet<String> import_strings = new HashSet<String>();
		if(e_cl instanceof EClassImpl){
			//if the classifier is of type EClass, the found module path from the
			//EcoreGenmodelParser
			//object can be used it just needs to be transformed to a proper one first
			var import_string = create_import_name_for_ereference_or_eclass(e_cl as EClassImpl)
			this.add_import_as_String(import_string)
			import_strings.add(import_string)
		} else if (e_cl instanceof EDataType){
			//if the instance class is null -> it is an EMF-model specified "custom"-data-type
			if(e_cl instanceof EEnum){
				var import_string = create_import_name_for_ereference_or_eclass(e_cl)
				this.add_import_as_String(import_string)
				import_strings.add(import_string)
			} else if(e_cl.instanceClass === null){
				var e_dt = e_cl as EDataType
				var import_string = e_dt.instanceTypeName
				this.add_import_as_String(import_string)
				import_strings.add(import_string)
			}
			else if(!e_cl.instanceClass.isPrimitive) {
				var import_string = e_cl.instanceTypeName
				needed_imports.add(import_string)
				import_strings.add(import_string)
			}
		} else {
			if(
				e_cl.instanceClass.isPrimitive ||
				e_cl.instanceTypeName.equals("org.eclipse.emf.common.util.EList")
			  ) return import_strings;
			if(e_cl.instanceTypeName.equals("java.util.Map")){
				this.add_import_as_String(e_cl.instanceTypeName)
				import_strings.add(e_cl.instanceTypeName)
				this.add_import_as_String("java.util.HashMap")
				import_strings.add("java.util.HashMap")
			}
		}
		return import_strings
	}
	
	/**
	 * Adds an import as String to the {@link #needed_imports needed imports}. Entry is only added,
	 * if it is not null or empty.
	 * @param import_string String
	 * @param Adrian Zwenger
	 */
	def protected void add_import_as_String(String import_string){
		if(!import_string.nullOrEmpty) this.needed_imports.add(import_string)
	}

	/**
	 * adds a whole collection containing strings to the {@link #needed_imports needed imports} 
	 * by calling 
	 * {@link #add_import_as_String(String) add_import_as_String(String)}.
	 * @param import_strings Collection<String>
	 * @author Adrian Zwenger
	 */
	def protected void add_import_as_String(Collection<String> import_strings){
		for(String import_string : import_strings){
			add_import_as_String(import_string)
		}
	}
	
	/**
	 * Adds {@link emfcodegenerator.notification.SmartEMFNotification} to the imports. <br/>
	 * Call this method when generating code that should create notifications.
	 * @return {@code true} if SmartEMFNotification was not already imported, {@code false} if it was
	 */
	def protected addNotificationImport() {
		needed_imports.add("emfcodegenerator.notification.SmartEMFNotification")
	}

	/**########################Helper Methods########################*/

	/**
	 * EClasses and EReferences do not store their data-types proper fq-import name.
	 * The full path can be created by accessing the classes package and then continue to get the
	 * super-package until top layer in the hierarchy has been reached.<br>
	 * Returns a the fq-import name.
	 * @param e_obj E EReference or EClass
	 * @return String
	 * @author Adrian Zwenger
	 */
	def protected <E> create_import_name_for_ereference_or_eclass(E e_obj){
		var String fqdn
		var EPackage super_package
		if(e_obj instanceof EReference) {
			// check if input is an EReference
			fqdn = (e_obj as EReference).EType.EPackage.name + "." +
				   (e_obj as EReference).EType.name
		    // get reference type and its package
			super_package = (e_obj as EReference).EType.EPackage.ESuperPackage
			// initialise the super package
			}
		else if(e_obj instanceof EClassifier){
			// same for EClasses
			var classifier = e_obj as EClassifier
			if(classifier.EPackage.equals(EcorePackage.eINSTANCE)){
				if(!classifier.instanceTypeName.nullOrEmpty) return classifier.instanceTypeName
				else throw new RuntimeException("unsupported EMF-classifier. please add support")
			}
			fqdn = classifier.EPackage.name + "." + (e_obj as EClassifier).name
			super_package = (e_obj as EClassifier).EPackage.ESuperPackage
		} else {
			throw new IllegalArgumentException("expected EReference or EClass. Got: " + e_obj.class)
		}
		while(super_package !== null){
			// EMF sets the ESuperPackage attribute to null if there is no super-package
			// traverse package hierarchy until top-layer is reached
			fqdn = super_package.name + "." + fqdn
			super_package = super_package.ESuperPackage
		}
		// The super-layer package specified in the genmodel-xmi is not stored in the ECLass structure
		// thus needs to be added manually
		//var super_package_name_string = (emf_model.get_super_package_name === null) emf_model.get_super_package_name ? this.
		var package_prefix = (this.initialised_with_emf_model) ? emf_model.get_super_package_name : this.super_package_name
		return (package_prefix === null ||
				package_prefix.isEmpty) ? 
				fqdn : package_prefix + "." + fqdn
	}

	/**########################Regular Getter/Setters########################*/

	/**
	 * Returns the HashSet with the {@link #needed_imports needed imports} represented as Strings.
	 * @return HashSet<String>
	 * @author Adrian Zwenger
	 */
	def HashSet<String> get_needed_imports(){
		return needed_imports
	}

	/**
	 * Takes String as input and returns it in uppercase with an underscore before each capital
	 * latter of the original String. EMF uses this naming scheme for the Literals in the
	 * Package-class
	 * @param value String
	 * @return String
	 * @auhtor StackOverflow
	 */
	def protected static String emf_to_uppercase(String value){
		/*
		 * Thank your stackoverflow
		 * https://stackoverflow.com/questions/1591132/how-can-i-add-an-underscore-before-each-capital-letter-inside-a-java-string
		 */
		return value.replaceAll("(.)([A-Z])", "$1_$2").toUpperCase()
	}

}