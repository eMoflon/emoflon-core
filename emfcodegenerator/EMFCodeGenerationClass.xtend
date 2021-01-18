package emfcodegenerator

import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.impl.EClassImpl
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EGenericType
import java.util.HashSet
import org.eclipse.emf.ecore.ETypeParameter
import java.util.Arrays
import java.util.Collection
import org.eclipse.emf.ecore.EEnum

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

	var String super_package_name = null
	
	var boolean initialised_with_emf_model = true

	/**########################Constructors########################*/

	/**
	 * Constructs a new GenerationClass. Most need access to functionality provided by the
	 * EcoreGenmodelParser, thus a valid instance is needed to construct a new instance
	 * @param  EcoreGenmodelParser gen_model
	 */
	new(EcoreGenmodelParser gen_model){
		//if(emf_model !== null) println(gen_model.equals(emf_model))
		if(gen_model !== null && !gen_model.equals(EMFCodeGenerationClass.emf_model))
			EMFCodeGenerationClass.emf_model = gen_model
		else if(gen_model === null && EMFCodeGenerationClass.emf_model === null)
			throw new IllegalArgumentException()
	}

	/**
	 * Constructs a new GenerationClass. Only use this constructor if you do not need any
	 * functionality provided by EcoreGenmodelParser
	 */
	new(){
		println("Warning: a EMFCodeGenerationClass instance was created without an EcoreGenmodelParser")
		this.initialised_with_emf_model = false
	}

	new(String super_package_name){
		this.initialised_with_emf_model = false
		this.super_package_name = super_package_name
	}

	/**########################Import registration########################*/


	/**
	 * adds an Entry to the needed_imports data-field containing Strings which point to needed
	 * imports.
	 * returns a HashSet with the import strings which have been added.
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
			if(e_cl.instanceClass.isPrimitive ||
			   e_cl.instanceTypeName.equals("org.eclipse.emf.common.util.EList")) return import_strings;
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
	 * adds an import as String to the needed_imports Set. Entry is only added, if it is not null or
	 * empty
	 * @param String import_string
	 */
	def protected void add_import_as_String(String import_string){
		if(!import_string.nullOrEmpty) this.needed_imports.add(import_string)
	}

	/**
	 * adds a whole collection containing strings to the needed_imports Set by calling 
	 * add_import_as_String(String import_string)
	 * @param Collection<String> import_strings
	 */
	def protected void add_import_as_String(Collection<String> import_strings){
		for(String import_string : import_strings){
			add_import_as_String(import_string)
		}
	}
	
	/**
	 * returns the data type of an EAttribute by recursively traversing the EGenericType's and
	 * registers the the needed imports. If run as top level give empty string input. <br>
	 * It does not search EBounds
	 * @param e_type EGenericType
	 * @param declaration String describing the declaration. on first call pass empty String
	 * @return complete type declaration as String
	 */
	def protected String register_full_object_field_type(EGenericType e_type, String declaration){
		//get generictype import string
		var String new_declaration = declaration
		//check if it is a generic type
		if(e_type.ETypeParameter !== null){
			var ETypeParameter generic_parameter = e_type.ETypeParameter
			new_declaration = declaration + generic_parameter.name
		} else if (e_type.EClassifier === null && e_type.ETypeParameter === null){
			//a proper type was not specified
			new_declaration += "?"
		} else if(e_type.ERawType.instanceTypeName !== null){
			var fq_import_string = e_type.ERawType.instanceTypeName
			//the EMF EList is to be replaced
			var buffer = fq_import_string.split("\\.")
			new_declaration = new_declaration + buffer.get(buffer.length -1)
			add_import(e_type.ERawType)
		} else {
			new_declaration = new_declaration + e_type.ERawType.name
			add_import(e_type.ERawType)
		}
		//return if current etype does not contain more generictypes
		if(e_type.ETypeArguments.isEmpty()) return new_declaration
		//object_field_is_nested_type = true
		//if it does recursively iterate over all register them too
		var sub_e_type_iterator = e_type.ETypeArguments.iterator
		new_declaration += "<"
		while(sub_e_type_iterator.hasNext){
			var sub_e_type = sub_e_type_iterator.next()
			new_declaration = register_full_object_field_type(sub_e_type, new_declaration)
			if(sub_e_type_iterator.hasNext) new_declaration += ","
		}
		new_declaration += ">"
		return new_declaration
	}

	/**########################EList logic########################*/

	def static String get_elist_type_name(EListTypeEnum e_list_type){
		switch(e_list_type){
			case EListTypeEnum.NONE: return "NO_E_LIST"
			case EListTypeEnum.SET: return "HashESet"
			case EListTypeEnum.LINKED_LIST: return "LinkedEList"
			case EListTypeEnum.LINKED_SET: return "LinkedESet"
			default: return "DefaultEList"
		}
	}
	
	/**
	 * returns the needed String to import a specific custom EList implementation
	 * @param EListTypeEnum e_list_type
	 */
	def static String get_elist_import_String(EListTypeEnum e_list_type){
		switch(e_list_type){
			case EListTypeEnum.NONE: return null
			case EListTypeEnum.SET: return "emfcodegenerator.util.HashESet"
			case EListTypeEnum.LINKED_LIST: return "emfcodegenerator.util.LinkedEList"
			case EListTypeEnum.LINKED_SET: return "emfcodegenerator.util.LinkedESet"
			default: return "emfcodegenerator.util.DefaultEList"
		}
	}

	/**
	 * discerns which type of EList is needed by given two booleans representing if the EList
	 * is supposed to be ordered and the entries unique
	 * @param boolean is_ordered true=entries are ordered
	 * @param boolean is_unique true=entries are unique
	 * @return EListTypeEnum
	 */
	def static EListTypeEnum get_needed_elist_type(boolean is_ordered, boolean is_unique){
		if(!is_ordered && !is_unique){
			return EListTypeEnum.DEFAULT
		} else if(!is_ordered && is_unique){
			return EListTypeEnum.SET
		} else if(is_ordered && !is_unique){
			return EListTypeEnum.LINKED_LIST
		} else if(is_ordered && is_unique){
			return EListTypeEnum.LINKED_SET
		} else {
			return EListTypeEnum.NONE
		}
	}

	/**########################Helper Methods########################*/

	/**
	 * Helper Method. converts a normal interface path into an implementation file path
	 * Example: org/my_emf/classes/Myclass.java
	 *			-->
	 * 			org/my_emf/classes/impl/MyclassImpl.java
	 * @param String file path and name
	 * @return converted path
	 */
	def protected static String convert_regular_file_name_path_to_implementation_type(String fqdn){
		var buffer = fqdn.split("/")
		return String.join("/", Arrays.copyOfRange(buffer, 0, buffer.size - 1)) +
		 	   "/impl/" + buffer.get(buffer.size - 1) + "Impl.java"
	}


	/**
	 * EClasses and EReferences do not store their data-types proper fq-import name.
	 * The full path can be created by accessing the classes package and then continue to get the
	 * super-package until top layer in the hierarchy has been reached.
	 * Returns a the fq-import name
	 * @param EReference which is to be examined
	 * @return String
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
			fqdn = (e_obj as EClassifier).EPackage.name + "." + (e_obj as EClassifier).name
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
	 * Returns the HashSet with the needed imports
	 */
	def HashSet<String> get_needed_imports(){
		return needed_imports
	}

	def protected static String emf_to_uppercase(String value){
		/*
		 * Thank your stackoverflow
		 * https://stackoverflow.com/questions/1591132/how-can-i-add-an-underscore-before-each-capital-letter-inside-a-java-string
		 */
		return value.replaceAll("(.)([A-Z])", "$1_$2").toUpperCase()
	}
	
	/**
	 * creates the generic parameter declaration for a given EGenericType. Example: the described
	 * EGenericType is an ArrayList<b extends ClassA<?>>,
	 * the output would be: "ArrayList<b extends ClassA<?>>" if called with empty String.
	 * Method is recursive
	 * @param EGenericType generic for which this method is called
	 * @param String declaration saves the recursion state up until then. on first call pass empty String
	 * @return String
	 */
	/*def protected String etype_param_declarationgetter(EGenericType generic, String declaration){
		var new_declaration = declaration
		//get name of dependency and import them if needed
		if(generic.EClassifier !== null){
			new_declaration += generic.EClassifier.name
			this.add_import(generic.EClassifier)
		} else if (generic.ETypeParameter !== null) new_declaration += generic.ETypeParameter.name
		else new_declaration += "?"
		if(!generic.ETypeArguments.isEmpty){
			new_declaration += "<"
			var generics_iterator = generic.ETypeArguments.iterator
			while(generics_iterator.hasNext){
				new_declaration += this.etype_param_declarationgetter(generics_iterator.next, "")
				if(generics_iterator.hasNext) new_declaration += ","
			}
			new_declaration += ">"
		}
		if(generic.EUpperBound !== null){
			new_declaration += " extends "
			new_declaration += this.etype_param_declarationgetter(generic.EUpperBound, "")
		} else if (generic.ELowerBound !== null) {
			new_declaration += " super "
			new_declaration += this.etype_param_declarationgetter(generic.EUpperBound, "")
		}
		return new_declaration
	}*/
}