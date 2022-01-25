package org.emoflon.smartemf

/*
 * @author Adrian Zwenger
 */
 
import java.util.HashMap
import java.util.HashSet
import org.eclipse.emf.codegen.ecore.genmodel.GenModel
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.ETypeParameter
import org.emoflon.smartemf.creators.templates.util.PackageInformation

/**
 * Wrapper for parsing Ecore- and GenModel-XMI files. All
 * {@link emfcodegenerator.EMFCodeGenerationClass EMFCodeGenerationClasses} have a static data-field
 * containing one instance of this class. As such all related classes to code generation can access
 * previously processed data from this wrapper without the need to have the XMI-files reprocessed.
 */
class EcoreGenmodelParser {
	/**
	 * maps all EClasses found in ecore-xmi to their URI-name.<br>
	 * used to verify the ecore and genmodel
	 * xmi files by comparing the uri's with each other.<br>
	 * The URI is not stored in the Ecore-XMI. However, it can be re-constructed by observing the
	 * package- and class-hierarchy.<br>
	 * Take a look at {@link #genclass_name_map genclass_name_map}.
	 */
	var HashMap<String, EClass> ecoreclass_name_map = new HashMap<String, EClass>()

	/**
	 * Reverse mapping of {@link #ecoreclass_name_map ecoreclass_name_map}.
	 */
	var HashMap<EClass, String> reverse_ecoreclass_name_map = new HashMap<EClass, String>()

	/**
	 * Maps the parsed {@link EPackage EPackages} to the
	 * {@link EClass EClasses} directly contained in said package.
	 */
	var HashMap<EPackage, HashSet<EClass>> epackage_and_contained_classes = new HashMap<EPackage, HashSet<EClass>>()

	/**
	 * Maps the parsed {@link EPackage EPackages} to the
	 * {@link EDataType EDataTypes} directly contained in said package.
	 */
	var HashMap<EPackage, HashSet<EDataType>> epackage_and_contained_edatatypes = new HashMap<EPackage, HashSet<EDataType>>()

	/**
	 * Maps the parsed {@link EPackage EPackages} to the
	 * {@link EEnum EEnums} directly contained in said package.
	 */
	var HashMap<EPackage, HashSet<EEnum>> epackage_and_contained_eenums = new HashMap<EPackage, HashSet<EEnum>>()
	
	val EPackage ePackage
	
	val GenModel genmodel

	/**
	 * maps found {@link EPackage EPackage} to its respective
	 * {@link emfcodegenerator.inspectors.util.PackageInspector PackageInspector}.
	 */
	var HashMap<EPackage, PackageInformation> packages_to_package_inspector_map = new HashMap<EPackage, PackageInformation>()

	/**
	 * stores all EClasses as key and a HashMap as value which stores all the EClasses
	 * ETypeParameters
	 * and their designated variable name for code generation.
	 * Needed by {@link emfcodegenerator.creators.util.EMFPackageSourceCreator
	 * EMFPackageSourceCreator}
	 */
	var HashMap<EClass, HashMap<ETypeParameter, String>> eclass_to_etypeparam_to_var_name_map = new HashMap<EClass, HashMap<ETypeParameter, String>>()

	/**########################Constructors########################*/
	/**
	 * constructs a new EcoreGenmodelParser
	 * @param ecore_path String path to the ecore-xmi
	 * @param genmodel_path String path to the genmodel-xmi
	 * @author Adrian Zwenger
	 */
	new(EPackage ePackage, GenModel genmodel, String generatedFileDir) {
		// store the path to GenModel-xmi. It is needed by parse_genmodel
		this.ePackage = ePackage
		this.genmodel = genmodel
		parse_ecore()
		val slashIdx = this.genmodel.modelDirectory.indexOf("/");
		val genFolder = this.genmodel.modelDirectory.substring(slashIdx, genmodel.modelDirectory.length)
		// create the PackageInspectors
		for (EPackage e_pak : this.get_epackage_and_contained_classes_map.keySet) {
			var e_pak_inspector = new PackageInformation(e_pak as EPackage, this, generatedFileDir+genFolder)
			this.packages_to_package_inspector_map.put(e_pak, e_pak_inspector)
		}
	}

	/**########################Parsers########################*/
	/**
	 * parses the defined classes from the ecore-xmi and populates following object attributes:<br>
	 * <ul> 
	 * 	<li>{@link #super_package super_package}</li>
	 * <li>{@link #ecoreclass_name_map ecoreclass_name_map}</li>
	 * <li>{@link #reverse_ecoreclass_name_map reverse_ecoreclass_name_map}</li>
	 * </ul>
	 * Calls {@link #get_ecore_classes get_ecore_classes()}.
	 * 
	 * @param ecore_path String path to ecore-xmi
	 * @author Adrian Zwenger
	 */
	def void parse_ecore() {
		// create the prefix for all class-names contained in the Ecore
//		var classname_prefix = ePackage.getName() + "/"
		ecoreclass_name_map = get_ecore_classes(ePackage, "")

		// register all classes with the proper prefix
		// by preserving the prefix a direct comparison of all class names contained in the 
		// GenModel-XMI and Ecore-XMI is sufficient to check if both have the same classes registered
		for (String key : ecoreclass_name_map.keySet) {
			this.reverse_ecoreclass_name_map.put(ecoreclass_name_map.get(key), key)
		}
	}

	/**
	 * recursively registers all classes found in ecore-xmi and creates a HashMap where the key
	 * is a string which represents the classes position in the package hierarchy and the 
	 * EClass as a value itself.<br>
	 * Called by {@link #parse_ecore(String) parse_ecore()}.
	 * @param epak EPackage the root package of the meta-model
	 * @param package_path String describes the path to the root-package of the EMF-meta-model.
	 * It can be empty or a string specified by the GenModel-XMI. It is needed for properly
	 * comparing the classes specified in GenModel, as all classes their have this String as an
	 * prefix and in the Ecore they do not.
	 * @return HashMap<String,EClass>
	 * @author Adrian Zwenger
	 */
	def private HashMap<String, EClass> get_ecore_classes(EPackage epak, String package_path) {
		var e_classes = new HashMap<String, EClass>()
		var e_data_types = new HashSet<EDataType>()
		var e_enums = new HashSet<EEnum>()

		// exit recursion as soon as a package has no content at all
		if (epak.eContents().isEmpty()) {
			epackage_and_contained_classes.put(epak, new HashSet<EClass>())
			epackage_and_contained_edatatypes.put(epak, e_data_types)
			return e_classes
		}

		// iterate over all objects
		for (EObject e_obj : epak.eContents()) {
			if (e_obj instanceof EClass) {
				var e_class = e_obj as EClass

				// register all classes in package
				e_classes.put(package_path + (e_class).getName(), e_class)

				var generic_type_params_map = new HashMap<ETypeParameter, String>()
				for (ETypeParameter e_param : e_class.ETypeParameters) {
					// register the type parameters for the e_class and create a var-name for it
					var generic_name = e_class.name.substring(0, 1).toLowerCase +
						e_class.name.substring(1) + "EClass_" + e_param.name
					generic_type_params_map.put(e_param, generic_name)
				}
				eclass_to_etypeparam_to_var_name_map.put(e_class, generic_type_params_map)
			} else if (e_obj instanceof EDataType && !(e_obj instanceof EEnum)) {
				// register EDataTypes
				e_data_types.add(e_obj as EDataType)
			} else if (e_obj instanceof EEnum) {
				// register EEnums
				e_enums.add(e_obj as EEnum)
			}
		}

		epackage_and_contained_eenums.put(epak, e_enums)

		epackage_and_contained_edatatypes.put(epak, e_data_types)

		epackage_and_contained_classes.put(epak, new HashSet<EClass>(e_classes.values))

		// check if there are subpackages to be scanned as well
		if(epak.getESubpackages().isEmpty()) return e_classes
		// exit recursion if package does not have any sub_packages
		for (EPackage sub_epak : epak.getESubpackages()) {
			// repeat process for all subpackages recursively and add all classes to register
			e_classes.putAll(get_ecore_classes(sub_epak, package_path + sub_epak.getName() + "/"))
		}
		return e_classes
	}

	/**
	 * getter for {@link #reverse_ecoreclass_name_map ecore-class registry}.<br>
	 * The returned map contains all EClasses which are specified by the XMI-files.<br>
	 * The key is the class and the value is a String representing the classes position in the
	 * specified package hierarchy.
	 * @return HashMap<EClass, String>
	 * @author Adrian Zwenger
	 */
	def HashMap<EClass, String> get_object_to_class_name_map() {
		return reverse_ecoreclass_name_map
	}

	/**
	 * Returns true if the the passed value is contained in the 
	 * {@link #reverse_ecoreclass_name_map ecore-class registry}.<br>
	 * @param e_class EClassifier the class to check
	 * @return boolean
	 * @author Adrian Zwenger
	 */
	def boolean eclass_is_registered(EClassifier e_class) {
		return this.ecoreclass_name_map.containsValue(e_class)
	}

	/**
	 * Returns a HashMap with EPackages specified by the Ecore and GenModel XMI-files and the
	 * classes which the package contains as value.<br>
	 * The value of {@link #epackage_and_contained_classes epackage_and_contained_classes} is
	 * returned
	 * @return HashMap<EPackage,HashSet<EClass>>
	 * @author Adrian Zwenger
	 */
	def HashMap<EPackage, HashSet<EClass>> get_epackage_and_contained_classes_map() {
		return epackage_and_contained_classes
	}

	/**
	 * Returns a HashMap with EPackages specified by the Ecore and GenModel XMI-files and the
	 * EDataType which the package contains as value.<br>
	 * The value of {@link #epackage_and_contained_edatatypes epackage_and_contained_edatatypes} is
	 * returned
	 * @return HashMap<EPackage,HashSet<EDataType>>
	 * @author Adrian Zwenger
	 */
	def HashMap<EPackage, HashSet<EDataType>> get_epackage_and_contained_e_data_types_map() {
		return epackage_and_contained_edatatypes
	}

	/**
	 * Returns a HashMap with EPackages specified by the Ecore and GenModel XMI-files and the
	 * EEnums which the package contains as value.<br>
	 * The value of {@link #epackage_and_contained_eenums epackage_and_contained_eenums} is
	 * returned
	 * @return HashMap<EPackage,HashSet<EEnum>>
	 * @author Adrian Zwenger
	 */
	def HashMap<EPackage, HashSet<EEnum>> get_epackage_and_contained_eenums_map() {
		return epackage_and_contained_eenums
	}

	/**
	 * Returns a mapping of EPackages to their corresponding PackageInspector.<br>
	 * Returns value of
	 * {@link #packages_to_package_inspector_map packages_to_package_inspector_map}.
	 * @return HashMap<EPackage, PackageInspector>
	 * @author Adrian Zwenger 
	 */
	def get_packages_to_package_inspector_map() {
		return packages_to_package_inspector_map
	}

	/**
	 * returns a HashMap<ETypeParameter,String> which contains the ETypeParameters of the passed
	 * class as value and the designated variable name to be used in the Package-class 
	 * "public void initializePackageContents()" method. <br>
	 * Take a look at
	 * {@link #eclass_to_etypeparam_to_var_name_map eclass_to_etypeparam_to_var_name_map}.
	 * @param e_class EClass for which the generic-type to var-name map shall be gotten
	 * @return HashMap<ETypeParameter, String>
	 * @author Adrian Zwenger
	 */
	def get_generic_type_to_var_name_map_for_eclass(EClass e_class) {
		return this.eclass_to_etypeparam_to_var_name_map.get(e_class)
	}

	/**########################Overridden methods########################*/
	/**
	 * Returns the Hash-Code of this object
	 * @return int
	 * @author Adrian Zwenger
	 */
	override int hashCode() {
		return this.packages_to_package_inspector_map.hashCode
	}

	/**
	 * checks given object for equality
	 * @param o Object
	 * @return boolean
	 * @author Adrian Zwenger
	 */
	override boolean equals(Object o) {
		if(!(o instanceof EcoreGenmodelParser)) return false
		return (o as EcoreGenmodelParser).packages_to_package_inspector_map.equals(
			this.packages_to_package_inspector_map)
	}

	/**########################Updater methods########################*/
	/**
	 * Always call this method when making changes to a PackageInspector which was gotten from
	 * this ECoreGenmodelParser. If not done so other class will not profit from those changes.
	 * @param e_pak EPackage the EPackage which the given e_pak_inspector is inspecting
	 * @param e_pak_inspector PackageInspector
	 * @author Adrian Zwenger
	 */
	def update_package_inspector(EPackage e_pak, PackageInformation e_pak_inspector) {
		if (!e_pak_inspector.equals(e_pak))
			throw new IllegalArgumentException(
				"The given Inspector does not inspect the given EPackage"
			)

		if (this.packages_to_package_inspector_map.containsKey(e_pak))
			this.packages_to_package_inspector_map.put(e_pak, e_pak_inspector)
		return this.packages_to_package_inspector_map
	}
}
