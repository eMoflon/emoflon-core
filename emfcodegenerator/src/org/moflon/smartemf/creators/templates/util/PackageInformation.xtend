package org.moflon.smartemf.creators.templates.util

import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EGenericType
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.ETypeParameter
import org.moflon.smartemf.EcoreGenmodelParser
import org.eclipse.emf.ecore.impl.EClassImpl
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EcorePackage
import java.util.Set
import java.util.LinkedHashSet

/**
 * Inspects EPackages
 */
class PackageInformation {

	/**########################Attributes########################*/
	/**
	 * stores the path to the folder representing this package as a String
	 */
	var String path_to_folder

	/**
	 * stores the package declaration which should be used by classes in this package
	 */
	var String package_declaration

	/**
	 * the EPackage to be inspected
	 */
	var EPackage e_pak
	
	public val EcoreGenmodelParser genmodel
	
	val String targetDirectory 
	
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
	
		/**
	 * a Set containing strings of modules which need to be imported
	 * by using a TreeSet, the imports are sorted alphabetically
	 *
	 * example: "java.util.Map"
	 */
	var protected Set<String> needed_imports = new LinkedHashSet<String>()

	/**
	 * HashSet storing the EClasses contained in the package
	 */
	var HashSet<EClass> e_classes = new HashSet<EClass>()

	/**
	 * HashSet storing the EDataTypes contained in the package
	 */
	var HashSet<EDataType> e_data_types = new HashSet<EDataType>()

	/**
	 * HashSet storing the EDataTypes contained in the package
	 */
	var HashSet<EEnum> e_enums = new HashSet<EEnum>()

	/**
	 * stores if the given package has sub package
	 */
	var boolean has_sub_package
	
	/**
	 * if a class inherits from another, that classes package is needed to create its contents
	 */
	var HashSet<EPackage> package_dependency_set = new HashSet<EPackage>()

	/**
	 * stores code snippets needed to to initialize this package in the EMFPackage class
	 * the key represents a variables name and the key is a ordered list of code concerning the
	 * variable.<br>
	 * example:<br>
	 * <b>key</b> =  <em>the_SubPackage</em><br>
	 * <b>value</b> = <em>
	 *      ["Object registered_SubPackage = EPackage.Registry.INSTANCE.getEPackage(SubPackage.eNS_URI);",
	 *      "SubPackageImpl the_SubPackage = (SubPackageImpl)
	 *      ((registered_SubPackage instanceof SubPackageImpl) ?
	 *      registered_SubPackage : SubPackage.eINSTANCE);"]</em><br>
	 */
	var HashMap<String, ArrayList<String>> init_snippet = new HashMap<String, ArrayList<String>>()

	/**
	 * stores all EClasses as key and a HashMap as well which stores all the EClasses
	 * ETypeParameters and their designated variable name
	 */
	var HashMap<EClass, HashMap<ETypeParameter, String>> eclass_to_etypeparam_to_var_name_map = new HashMap<EClass, HashMap<ETypeParameter, String>>()

	/**
	 * stores all classes contained in the package which have ETypeParameters
	 */
	var HashSet<EClass> eclasses_which_have_etypeparameters = new HashSet<EClass>()

	/**
	 * stores if the contents of this Inspector have been inited
	 */
	var boolean isInited = false

	/**
	 * stores the String which is used to declare the type-parameters for a class.<br>
	 * <b>Example:</b>
	 *  <xmp>TestClassImpl<a extends classB, b> --> stored String = "<a extends classB, b>"</xmp>
	 */
	var HashMap<EClass, String> eclass_to_type_arguments_declaration = new HashMap<EClass, String>()

	/**
	 * stores the needed imports for the eclass if type arguments are needed
	 */
	var HashMap<EClass, HashSet<String>> eclass_to_needed_imports_for_type_arguments_map = new HashMap<EClass, HashSet<String>>()

	/**
	 * stores a reduced String which is used to declare the type-parameters for a class.<br>
	 * <b>Example:</b>
	 *  <xmp>TestClassImpl<a extends classB, b> --> stored String = "<a, b>"<xmp>
	 */
	var HashMap<EClass, String> eclass_to_type_to_reduced_arguments_declaration = new HashMap<EClass, String>()

	/**
	 * stores the specific imports the EMF-package-factory needs as well, which however,
	 * the EMF-package-class does not.
	 */
	var HashSet<String> needed_imports_for_package_factory = new HashSet<String>()

	/**########################Constructor########################*/
	/**
	 * construct a new PackagInspector
	 * @param e_package EPackag 
	 * @param gen_model EcoreGenmodelParser
	 * @author Adrian Zwenger
	 */
	new(EPackage e_package, EcoreGenmodelParser gen_model, String targetDirectory) {
		this.e_pak = e_package
		this.genmodel = gen_model
		this.targetDirectory = targetDirectory
		this.e_classes = new HashSet<EClass>()
		this.has_sub_package = !e_pak.ESubpackages.isEmpty()

	}

	/**
	 * initializes this PackageInspector.<br>
	 * <ol>
	 * <li>registers EClasses</li>
	 * <li>EAttributes and EReferences</li>
	 * <li>EOperations</li>
	 * <li>inherited things</li>
	 * <li>EDataTypes</li>
	 * </ol>
	 * @author Adrian Zwenger
	 */
	def void initialize() {
		this.isInited = true
		// register attributes, references, EOperations for the EClasses
		for (EClass e_class : genmodel.get_epackage_and_contained_classes_map.get(e_pak)) {
			this.e_classes.add(e_class)

			// register the type-parameter declaration strings
			this.create_arguments_declaration_for_eclass(e_class)

			// register Packages on which the EClass depends
			var dependant_packages = new HashSet<EPackage>()
			for (super_e_cl : e_class.ESuperTypes) {
				if (super_e_cl.EPackage !== null && !super_e_cl.EPackage.equals(this.e_pak))
					dependant_packages.add(super_e_cl.EPackage)
			}
			this.package_dependency_set.addAll(dependant_packages)

			// register generic type parameters for class
			var generic_type_params_map = genmodel.
				get_generic_type_to_var_name_map_for_eclass(e_class)
			this.eclass_to_etypeparam_to_var_name_map.put(e_class, generic_type_params_map)
			if (!generic_type_params_map.keySet.isEmpty)
				this.eclasses_which_have_etypeparameters.add(e_class)

		}

		this.package_dependency_set.addAll(this.e_pak.ESubpackages)
		set_path_to_folder_and_package_declaration()

		this.e_data_types = genmodel.get_epackage_and_contained_e_data_types_map.get(e_pak)

		this.e_enums = genmodel.get_epackage_and_contained_eenums_map.get(e_pak)

		// generate init for given this package snippet
		var snippets = new ArrayList<String>()
		var package_name = this.get_emf_package_class_name()

		snippets.add(
			'''Object registered_«package_name» = EPackage.Registry.INSTANCE.getEPackage(«package_name».eNS_URI);'''
		)

		snippets.add(
			'''«package_name»Impl the_«package_name» = («package_name»Impl) ((registered_«package_name» instanceof «package_name»Impl) ? registered_«package_name» : «package_name».eINSTANCE);'''.
				toString
		)

		this.init_snippet.put('''the_«package_name»'''.toString(), snippets)
	}

	/**
	 * creates the generic parameter declaration for a given EGenericType. Example: the described
	 * EGenericType is an ArrayList<b extends ClassA<?>>,
	 * the output would be: "ArrayList<b extends ClassA<?>>" if called with empty String.
	 * registers needed imports too and registers them for the PackageFactory creators as well.
	 * Method is recursive
	 * @param generic EGenericType for which this method is called
	 * @param declaration String saves the recursion state up until then.
	 * Pass empty String on first call.
	 * @param container_eclass EClass which needs to be passed to check if a type parameter is an
	 * existing parameter established by the class by which the passed EGenericType is contained.
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String etype_param_declarationgetter(
		EGenericType generic,
		String declaration,
		EClass container_eclass
	) {
		var new_declaration = declaration
		// get name of dependency and import them if needed
		if (generic.EClassifier !== null) {
			new_declaration += generic.EClassifier.name
			var imports = this.add_import(generic.EClassifier)
			this.needed_imports_for_package_factory.addAll(imports)
			this.eclass_to_needed_imports_for_type_arguments_map.get(container_eclass).addAll(imports)

		} else if(generic.ETypeParameter !== null) new_declaration +=
			generic.ETypeParameter.name else new_declaration += "?"

		if (!generic.ETypeArguments.isEmpty) {
			new_declaration += "<"
			var generics_iterator = generic.ETypeArguments.iterator
			while (generics_iterator.hasNext) {
				new_declaration += this.etype_param_declarationgetter(
					generics_iterator.next,
					"",
					container_eclass
				)
				if(generics_iterator.hasNext) new_declaration += ","
			}
			new_declaration += ">"
		}

		if (generic.EUpperBound !== null) {
			new_declaration += " extends "
			new_declaration += this.etype_param_declarationgetter(
				generic.EUpperBound,
				"",
				container_eclass
			)
		} else if (generic.ELowerBound !== null) {
			new_declaration += " super "
			new_declaration += this.etype_param_declarationgetter(
				generic.EUpperBound,
				"",
				container_eclass
			)
		}
		return new_declaration
	}

	/**
	 * creates the generic parameters declaration for a class. Example: "<a,b>"
	 * @param e_class EClass
	 * @return String
	 * @author Adrian Zwenger
	 */
	def private void create_arguments_declaration_for_eclass(EClass e_class) {
		this.eclass_to_needed_imports_for_type_arguments_map.put(e_class, new HashSet<String>())
		var imports = new ArrayList<String>()
		var generic_type_declaration_string = ""
		var reduced_generic_type_declaration_string = ""

		if (!e_class.ETypeParameters.isEmpty) {
			generic_type_declaration_string += "<"
			reduced_generic_type_declaration_string += "<"
			var iterator = e_class.ETypeParameters.iterator
			while (iterator.hasNext) {
				var entry = iterator.next
				generic_type_declaration_string += entry.name
				reduced_generic_type_declaration_string += entry.name
				if (!entry.EBounds.isEmpty) {
					generic_type_declaration_string += " extends "
					var bound_iterator = entry.EBounds.iterator
					while (bound_iterator.hasNext) {
						var the_bound = bound_iterator.next
						// import the bound if it is importable
						if (the_bound.EClassifier !== null)
							imports.addAll(this.add_import(the_bound.EClassifier))
						generic_type_declaration_string += this.etype_param_declarationgetter(the_bound, "", e_class)
						if(bound_iterator.hasNext) generic_type_declaration_string += " & "
					}
				}
				if (iterator.hasNext) {
					generic_type_declaration_string += ","
					reduced_generic_type_declaration_string += ","
				}
			}
			generic_type_declaration_string += ">"
			reduced_generic_type_declaration_string += ">"
		}

		this.eclass_to_type_arguments_declaration.put(e_class, generic_type_declaration_string)
		this.eclass_to_type_to_reduced_arguments_declaration.put(
			e_class,
			reduced_generic_type_declaration_string
		)
		this.eclass_to_needed_imports_for_type_arguments_map.get(e_class).addAll(imports)
	}

	/**
	 * returns true if this inspector has been initialized
	 * @return boolean
	 * @author Adrian Zwenger
	 */
	def boolean is_initialized() {
		return this.isInited
	}

	/**
	 * returns the String which is used to declare the type-parameters for a class.<br>
	 * <b>Example:</b>
	 *  <xmp>TestClassImpl<a extends classB, b> --> stored String = "<a extends classB, b>"</xmp>
	 * @param e_class EClass
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_type_arguments_declaration_for_eclass(EClass e_class) {
		return this.eclass_to_type_arguments_declaration.get(e_class)
	}

	/**
	 * returns the reduced String which is used to declare the type-parameters for a class.<br>
	 * <b>Example:</b>
	 *  <xmp>TestClassImpl<a extends classB, b> --> stored String = "<a, b>"</xmp>
	 * @param e_class EClass
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_reduced_type_arguments_declaration_for_eclass(EClass e_class) {
		return this.eclass_to_type_to_reduced_arguments_declaration.get(e_class)
	}

	/**
	 * returns a HashMap where the key is an EClass of this package and the key its generic
	 * type-arguments. Take a look at {@link #eclass_to_needed_imports_for_type_arguments_map
	 *  eclass_to_needed_imports_for_type_arguments_map } 
	 * @return HashMap<EClass, HashSet<String>>
	 * @author Adrian Zwenger
	 */
	def HashMap<EClass, HashSet<String>> get_eclass_to_needed_imports_for_type_arguments_map() {
		return eclass_to_needed_imports_for_type_arguments_map
	}

	/**########################Helper Methods########################*/
	/**
	 * traverses package hierarchy to set the folder path and the equivalent java representation
	 * <b>example:</b> <xmp>"./src-gen/org/util/mypackage" and "org.util.mypackage"</xmp>
	 * @author Adrian Zwenger
	 */
	def private void set_path_to_folder_and_package_declaration() {
		var path = e_pak.name
		var declaration = e_pak.name
		var e_package = e_pak
		while (e_package.ESuperPackage !== null) {
			e_package = e_package.ESuperPackage
			path = e_package.name + "/" + path
			declaration = e_package.name + "." + declaration
		}
		var super_package = genmodel.get_super_package_name
		if (super_package === null) {
			package_declaration = declaration
			path_to_folder = targetDirectory + path
			return
		}
		var buffer = genmodel.get_super_package_name.split("\\.").join("/")
		path_to_folder = targetDirectory + buffer + "/" + path
		package_declaration = super_package + "." + declaration
	}

	/**########################Getters########################*/
	/**
	 * returns the path to the folder where all Classes in package are located
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_path_to_folder() {
		if (!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
			
		return path_to_folder
	}
	
	/**
	 * Returns a HashSet containing all EClasses in this package
	 * @return HashSet<EClass>
	 * @author Adrian Zwenger
	 */
	def HashSet<EClass> get_all_eclasses_in_package() {
		if (!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return e_classes
	}

	/**
	 * Returns all custom EMF datatypes contained in this package in an HashSet
	 * @return HashSet<EDataType>
	 * @author Adrian Zwenger
	 */
	def HashSet<EDataType> get_all_edata_types_in_package() {
		if (!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return this.e_data_types
	}

	/**
	 * Returns all custom EMF datatypes contained in this package in an HashSet
	 * @return HashSet<EEnum>
	 * @author Adrian Zwenger
	 */
	def HashSet<EEnum> get_all_eenums_in_package() {
		if (!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return this.e_enums
	}

	/**
	 * returns true if this package contains sub-packages
	 * @return boolean
	 * @author Adrian Zwenger
	 */
	def boolean has_sub_packages() {
		if (!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return has_sub_package
	}

	/**
	 * Returns the package declaration as String.<br>
	 * <b>example:</b> <xmp>or.util.fake.package</xmp>
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_package_declaration_name() {
		if (!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return package_declaration
	}

	/**
	 * Returns the package declaration for the implementations as String
	 * <b>example:</b> <xmp>or.util.fake.package.impl</xmp>
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_package_declaration_name_for_implementations() {
		if (!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return package_declaration + ".impl"
	}

	/**
	 * All EMF generated packages have a class which is used to initialise the model. The name of
	 * said class is returned here
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_emf_package_class_name() {
		if (!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return this.get_name_with_first_letter_capitalized + "Package"
	}

	/**
	 * All EMF generated packages have a class which is used to create parts of the model.
	 * The name of said class is returned here
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_emf_package_factory_class_name() {
		if (!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return this.get_name_with_first_letter_capitalized + "Factory"
	}

	/**
	 * returns the regular name of this package
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_name() {
		if (!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return e_pak.name
	}

	/**
	 * returns the NS-URI of the inspected EPackage
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_ens_uri() {
		if (!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return e_pak.nsURI
	}

	/**
	 * returns the NS-prefix of the inspected EPackage
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_ens_prefix() {
		if (!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return e_pak.nsPrefix
	}

	/**
	 * returns the packages name with a capitalised first letter
	 * @return String
	 * @author Adrian Zwenger
	 */
	def get_name_with_first_letter_capitalized() {
		if (!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return this.e_pak.name.substring(0, 1).toUpperCase() + this.e_pak.name.substring(1);
	}

	/**
	 * Returns the ESuperPackage of the EPackage which is being inspected.
	 * @return EPackage
	 * @author Adrian Zwenger
	 */
	def EPackage get_super_epackage() {
		if (!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return this.e_pak.ESuperPackage
	}

	/**
	 * returns a HashSet<Epackage> with all sub-packages of zhe inspected Package
	 * @return HashSet<Epackage>
	 * @author Adrian Zwenger
	 */
	def HashSet<EPackage> get_sub_packages() {
		if (!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return new HashSet<EPackage>(this.e_pak.ESubpackages)
	}

	/**
	 * returns the code snippet to obtain or create and register an EPackage instance for this
	 * EPackage as an HashMap. the key is the variables name in the snippet and the ArrayList
	 * contains the needed code-lines as entry
	 * @return HashMap<String,ArrayList<String>>
	 * @author Adrian Zwenger
	 */
	def HashMap<String, ArrayList<String>> get_init_code_snippet() {
		if (!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return new HashMap<String, ArrayList<String>>(this.init_snippet)
	}

	/**
	 * returns the EPackage which this PackageInspector is inspecting
	 * @return EPackage
	 * @author Adrian Zwenger
	 */
	def EPackage get_emf_e_package() {
		if (!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return this.e_pak
	}

	/**
	 * checks PackageInspectors for equality
	 */
	override boolean equals(Object other) {
		if(!(other instanceof PackageInformation)) return false
		return this.e_pak.equals((other as PackageInformation).e_pak)
	}

	/**
	 * returns true if the given EPackage is the inspected one
	 */
	def boolean equals(EPackage e_pak) {
		return this.e_pak.equals(e_pak)
	}

	/**
	 * returns the hash-code for this Inspector
	 */
	override int hashCode() {
		return this.e_pak.hashCode()
	}

	/**
	 * returns a HashSet<EPackage> on which the inspected Package depends. Does not contain all
	 * dependencies contained in EGenericTypes or similar, but takes super-packages and class
	 * inheritance related dependencies into account
	 * @return HashSet<EPackage>
	 * @author Adrian Zwenger
	 */
	def HashSet<EPackage> get_e_package_dependencies() {
		if (!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return this.package_dependency_set
	}

	/**
	 * takes EClass and returns a HashMap<ETypeParameter,String>, where the key is an ETypeParameter
	 * of the EClass and the designated variable name as value. Returns null if EClass is not
	 * registered.
	 * @param EClass e_class
	 * @return HashMap<ETypeParameter,String>
	 * @author Adrian Zwenger
	 */
	def get_generic_type_to_var_name_map_for_eclass(EClass e_class) {
		if (!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return this.eclass_to_etypeparam_to_var_name_map.get(e_class)
	}

	/**
	 * returns a HashSet with all EClasses contained in the Package
	 * @return HashSet<EClass>
	 * @author Adrian Zwenger
	 */
	def HashSet<EClass> get_eclasses_which_have_etypeparameters() {
		if (!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return this.eclasses_which_have_etypeparameters
	}

	/**
	 * Returns the needed imports for the EMF-package-factory class.
	 * @return HashSet<String>
	 * @author Adrian Zwenger
	 */
	def HashSet<String> get_needed_imports_for_package_factory() {
		return this.needed_imports_for_package_factory
	}
	
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
			add_import_as_String(import_string)
			import_strings.add(import_string)
		} else if (e_cl instanceof EDataType){
			//if the instance class is null -> it is an EMF-model specified "custom"-data-type
			if(e_cl instanceof EEnum){
				var import_string = create_import_name_for_ereference_or_eclass(e_cl)
				add_import_as_String(import_string)
				import_strings.add(import_string)
			} else if(e_cl.instanceClass === null){
				var e_dt = e_cl as EDataType
				var import_string = e_dt.instanceTypeName
				add_import_as_String(import_string)
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
				add_import_as_String(e_cl.instanceTypeName)
				import_strings.add(e_cl.instanceTypeName)
				add_import_as_String("java.util.HashMap")
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
		if(!import_string.nullOrEmpty) needed_imports.add(import_string)
	}
	
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
		var package_prefix = (initialised_with_emf_model) ? genmodel.get_super_package_name : this.super_package_name
		return (package_prefix === null ||
				package_prefix.isEmpty) ? 
				fqdn : package_prefix + "." + fqdn
	}
}
