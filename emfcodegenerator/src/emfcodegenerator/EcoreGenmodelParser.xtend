package emfcodegenerator

/*
 * @author Adrian Zwenger
 */

//java.util 
import java.util.HashMap;

//org.eclipse.emf.codegen.ecore_2.23.0.v20200701-0840.jar
import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;

//org.eclipse.emf.ecore_2.23.0.v20200630-0516.jar
import org.eclipse.emf.ecore.impl.EClassifierImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EObject

//org.eclipse.emf.ecore.xmi_2.16.0.v20190528-0725.jar
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;



//
import org.eclipse.emf.common.util.URI;
import java.util.Arrays
import java.util.HashSet
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.EAttribute
import emfcodegenerator.inspectors.util.AttributeInspector
import org.eclipse.emf.ecore.EReference
import emfcodegenerator.inspectors.util.ReferenceInspector
import emfcodegenerator.inspectors.util.PackageInspector
import emfcodegenerator.inspectors.util.AbstractObjectFieldInspector
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.ETypeParameter
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.emf.ecore.EClassifier

/**
 * Wrapper for parsing Ecore- and GenModel-XMI files. All
 * {@link emfcodegenerator.EMFCodeGenerationClass EMFCodeGenerationClasses} have a static data-field
 * containing one instance of this class. As such all related classes to code generation can access
 * previously processed data from this wrapper without the need to have the XMI-files reprocessed.
 */
class EcoreGenmodelParser {
	
	/**########################Attributes########################*/

	/**
	 * maps all GenClasses found in genmodel-xmi to their URI-name.<br>
	 * Used to verify the ecore and genmodel
	 * xmi files by comparing the uri's with each other.<br>
	 * Take a look as {@link #ecoreclass_name_map ecoreclass_name_map}.
	 */
	var HashMap<String,GenClass> genclass_name_map = new HashMap<String,GenClass>()

	/**
	 * maps all EClasses found in ecore-xmi to their URI-name.<br>
	 * used to verify the ecore and genmodel
	 * xmi files by comparing the uri's with each other.<br>
	 * The URI is not stored in the Ecore-XMI. However, it can be re-constructed by observing the
	 * package- and class-hierarchy.<br>
	 * Take a look at {@link #genclass_name_map genclass_name_map}.
	 */
	var HashMap<String,EClass> ecoreclass_name_map = new HashMap<String,EClass>()

	/**
	 * Reverse mapping of {@link #ecoreclass_name_map ecoreclass_name_map}.
	 */
	var HashMap<EClass,String> reverse_ecoreclass_name_map = new HashMap<EClass,String>()

	/**
	 * Maps the parsed {@link org.eclipse.emf.ecore.EPackage EPackages} to the
	 * {@link org.eclipse.emf.ecore.EClass EClasses} directly contained in said package.
	 */
	var HashMap<EPackage,HashSet<EClass>> epackage_and_contained_classes =
		new HashMap<EPackage,HashSet<EClass>>()
	
	/**
	 * Maps the parsed {@link org.eclipse.emf.ecore.EPackage EPackages} to the
	 * {@link org.eclipse.emf.ecore.EDataType EDataTypes} directly contained in said package.
	 */
	var HashMap<EPackage,HashSet<EDataType>> epackage_and_contained_edatatypes =
		new HashMap<EPackage,HashSet<EDataType>>()
	
	/**
	 * Maps the parsed {@link org.eclipse.emf.ecore.EPackage EPackages} to the
	 * {@link org.eclipse.emf.ecore.EEnum EEnums} directly contained in said package.
	 */
	var HashMap<EPackage,HashSet<EEnum>> epackage_and_contained_eenums =
		new HashMap<EPackage,HashSet<EEnum>>()

	/**
	 * the GenModel-XMI can specify a top-layer package (-hierarchy) name which will be stored here
	 * or null if none was specified
	 */
	var String super_package_name

	/**
	 * top-layer {@link org.eclipse.emf.ecore.EPackage EPackage} in hierarchy
	 */
	var EPackage super_package

	/**
	 * stores the path to the genmodel-xmi-file as String
	 */
	var String genmodel_xmi_fq_path

	/**
	 * maps found {@link org.eclipse.emf.ecore.EStructuralFeature EStructuralFeature} to an
	 * {@link emfcodegenerator.inspectors.util.AbstractObjectFieldInspector
	 * AbstractObjectFieldInspector} for said feature.
	 */
	var HashMap<EStructuralFeature,AbstractObjectFieldInspector> struct_features_to_inspector_map =
		new HashMap<EStructuralFeature,AbstractObjectFieldInspector>()

	/**
	 * maps found {@link org.eclipse.emf.ecore.EPackage EPackage} to its respective
	 * {@link emfcodegenerator.inspectors.util.PackageInspector PackageInspector}.
	 */
	var HashMap<EPackage, PackageInspector> packages_to_package_inspector_map =
		new HashMap<EPackage, PackageInspector>()

	/**
	 * stores all EClasses as key and a HashMap as value which stores all the EClasses
	 * ETypeParameters
	 * and their designated variable name for code generation.
	 * Needed by {@link emfcodegenerator.creators.util.EMFPackageSourceCreator
	 * EMFPackageSourceCreator}
	 */
	var HashMap<EClass,HashMap<ETypeParameter,String>> eclass_to_etypeparam_to_var_name_map = 
		new HashMap<EClass,HashMap<ETypeParameter,String>>()

	/**########################Constructors########################*/

	/**
	 * constructs a new EcoreGenmodelParser
	 * @param ecore_path String path to the ecore-xmi
	 * @param genmodel_path String path to the genmodel-xmi
	 * @author Adrian Zwenger
	 */
	new(String ecore_path, String genmodel_path){
		//store the path to GenModel-xmi. It is needed by parse_genmodel
		this.genmodel_xmi_fq_path = genmodel_path
		this.parse_genmodel(genmodel_path)
		//this.super_package =
		parse_ecore(ecore_path)
		//verify that ecore and genmodel contain the same classes
		if(!this.genclass_name_map.keySet().equals(ecoreclass_name_map.keySet())){
			println("1 " + genclass_name_map.keySet())
			println("2 " + ecoreclass_name_map.keySet())
			println(super_package_name)
			throw new UnsupportedOperationException("genmodel and ecore do not specify same classes")
		}
		//create the PackageInspectors
		for(EPackage e_pak : this.get_epackage_and_contained_classes_map.keySet){
			var e_pak_inspector = new PackageInspector(e_pak as EPackage, this)
			this.packages_to_package_inspector_map.put(e_pak, e_pak_inspector)
		}
	}

	/**########################Parsers########################*/

	/**
	 * parses the defined classes from the ecore-xmi and populates following object attributes:<br>
	 * <ul> 
	 * 	<li>{@link #super_package super_package}</li>
	 *	<li>{@link #ecoreclass_name_map ecoreclass_name_map}</li>
	 *	<li>{@link #reverse_ecoreclass_name_map reverse_ecoreclass_name_map}</li>
	 * </ul>
	 * Calls {@link #get_ecore_classes get_ecore_classes()}.
	 *
	 * @param ecore_path String path to ecore-xmi
	 * @author Adrian Zwenger
	 */
	def void parse_ecore(String ecore_path){
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
				.put("ecore", new XMIResourceFactoryImpl());
		//register "ecore" as valid file extension
		this.super_package =
			(new ResourceSetImpl()).getResource(
				URI.createFileURI(ecore_path), true
			).getContents().get(0) as EPackage

		//create the prefix for all class-names contained in the Ecore
	  	var classname_prefix = this.super_package.getName() + "/"
	  	classname_prefix = (super_package_name === null || super_package_name.isEmpty) ?
	  					classname_prefix : super_package_name + "/" + classname_prefix
		this.ecoreclass_name_map = this.get_ecore_classes(this.super_package, classname_prefix)

		//register all classes with the proper prefix
		//by preserving the prefix a direct comparison of all class names contained in the 
		//GenModel-XMI and Ecore-XMI is sufficient to check if both have the same classes registered

		for(String key : ecoreclass_name_map.keySet){
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
	def private HashMap<String,EClass> get_ecore_classes(EPackage epak, String package_path){
		var e_classes = new HashMap<String,EClass>()
		var e_data_types = new HashSet<EDataType>()
		var e_enums = new HashSet<EEnum>()

		//exit recursion as soon as a package has no content at all
		if(epak.eContents().isEmpty()){
			epackage_and_contained_classes.put(epak, new HashSet<EClass>())
			epackage_and_contained_edatatypes.put(epak, e_data_types)
			return e_classes	
		}

		//iterate over all objects
		for(EObject e_obj: epak.eContents()){
			if(e_obj instanceof EClass){
				var e_class = e_obj as EClass

				//register all classes in package
				e_classes.put(package_path + (e_class).getName(), e_class)
				
				for(EStructuralFeature feature : e_class.EAllStructuralFeatures){
					//register all structural features
					if(!struct_features_to_inspector_map.containsKey(feature)){
						//prevent double creation of Inspectors
						if(feature instanceof EAttribute){
							struct_features_to_inspector_map.put(
								feature as EAttribute,
								new AttributeInspector(
									feature as EAttribute, this.super_package_name
								)
							)
						} else if(feature instanceof EReference){
							/*
							 * EReferences to non SmartEMF class is not permitted.
							 * Thus validity of the EReference has to be checked
							 */
							var reference = feature as EReference
							if(reference.EGenericType === null)
								throw new IllegalArgumentException('''
ERROR! The target of EReference "«reference.name»" contained in class "«package_path.replace("/", ".") + (e_class).getName()»" has not been specified.'''
								)

							if(
								reference.EGenericType.EClassifier === null &&
								reference.EGenericType.ETypeParameter !== null
							){
								/* generics are permitted. However, the user must make sure that all
								 * runtime instances inherit from the SmartObject class.
								 * In other words it needs to be a SmartEMF object
								 */

								println(
'''Warning!! Target of EReference "«reference.name»" contained in class "«package_path.replace("/", ".") + (e_class).getName()»" is a generic type-parameter.
«"\t"»Please do take care, that the runtime instance inherits from emfcodegenerator.util.SmartObject.'''
								)

							} else if(reference.EGenericType.EClassifier !== null) {
								//the EReference points to a specific class
								var the_classifier = reference.EGenericType.EClassifier
								if(the_classifier.EPackage.equals(EcorePackage.eINSTANCE)){
									//the reference type is an EMF-class which are not supported
									throw new IllegalArgumentException('''
ERROR! The target of EReference "«reference.name»" contained in class "«package_path.replace("/", ".") + (e_class).getName()»"is not supported as it is en Eclipse-EMF class.'''
								)
								} else if(
									!the_classifier.EPackage.equals(EcorePackage.eINSTANCE) &&
									!this.super_package.eAllContents.contains(the_classifier)
								){
									/*
									 * the classifier is not contained in Eclipse-EMF or the
									 * given XMI-s.
									 * In this case the user must make sure, that the type which is
									 * being referenced implements
									 * {@link emfcodegenerator.util.MinimalSObjectContainer
									 * MinimalSObjectContainer} or
									 * inherit from {{@link emfcodegenerator.util.SmartObject 
									 * SmartObject}
									 */
									println('''
Warning!! Target of EReference "«reference.name»" contained in class "«package_path.replace("/", ".") + (e_class).getName()»" is not an Eclipse-EMF and was not registered in the given XMI-files.
«"\t"»Please do take care, that the runtime instances inherit from emfcodegenerator.util.SmartObject.
''')
								}
							}
							struct_features_to_inspector_map.put(
								feature as EReference,
								new ReferenceInspector(feature as EReference, this.super_package_name)
							)
						}
					}
				}

				var generic_type_params_map = new HashMap<ETypeParameter, String>()
				for(ETypeParameter e_param : e_class.ETypeParameters){
					//register the type parameters for the e_class and create a var-name for it
					var generic_name = e_class.name.substring(0,1).toLowerCase +
								   	   e_class.name.substring(1) + "EClass_" + e_param.name
			   	    generic_type_params_map.put(e_param, generic_name)
				}
				eclass_to_etypeparam_to_var_name_map.put(e_class, generic_type_params_map)
			}
			else if(e_obj instanceof EDataType && !(e_obj instanceof EEnum)) {
				//register EDataTypes
				e_data_types.add(e_obj as EDataType)
			} else if(e_obj instanceof EEnum){
				//register EEnums
				e_enums.add(e_obj as EEnum)
			}
		}
		
		epackage_and_contained_eenums.put(epak, e_enums)
		
		epackage_and_contained_edatatypes.put(epak, e_data_types)
		
		epackage_and_contained_classes.put(epak, new HashSet<EClass>(e_classes.values))
		
		//check if there are subpackages to be scanned as well
		if(epak.getESubpackages().isEmpty()) return e_classes
		//exit recursion if package does not have any sub_packages
		for(EPackage sub_epak : epak.getESubpackages()){
			//repeat process for all subpackages recursively and add all classes to register
			e_classes.putAll(get_ecore_classes(sub_epak, package_path +  sub_epak.getName() + "/"))
		}
		return e_classes
	}

	/**
	 * parses the defined classes from the genmodel-xmi and populates following object attributes:<br>
	 * <ul> 
	 * 	<li>{@link #super_package_name super_package_name}</li>
	 *	<li>{@link #genclass_name_map genclass_name_map}</li>
	 * </ul>
	 * Calls {@link #get_genmodel_classes(GenPackage) get_genmodel_classes()}.<br>
	 * Make sure, that this method is called before calling
	 * {@link #parse_ecore(String) parse_ecore()}.
	 * @param genmodel_path String path to genmodel-xmi
	 * @author Adrian Zwenger
	 */
	def void parse_genmodel(String genmodel_path){
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
				.put("genmodel", new XMIResourceFactoryImpl())

		var res_impl = new ResourceSetImpl()

		//add genmodel-file-suffix to registry
		res_impl.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("genmodel", new EcoreResourceFactoryImpl())

		//add the package to the registry
		res_impl.getPackageRegistry().put(GenModelPackage.eNS_URI, GenModelPackage.eINSTANCE)

		//get a package instance
		var gen_model =
			res_impl.getResource(
				URI.createFileURI(genmodel_path), true
			).getContents().get(0) as GenModel

		this.super_package_name = gen_model.getGenPackages().get(0).basePackage
		//register all classes found in the genmodel-xmi
		this.genclass_name_map = get_genmodel_classes(gen_model.getGenPackages().get(0))
	}

	/**
	 * recursively registers all classes found in genmodel-xmi and creates a HashMap where the key
	 * is a string which represents the classes position in the package hierarchy and the 
	 * GenClass as a value itself.<br>
	 * Called by {@link #parse_genmodel(String) parse_genmodel()}.
	 * @param gp GenPackage top-level/root GenPackage specified by GenModel-XMI
	 * @return HashMap<String,GenClass>
	 * @author Adrian Zwenger
	 */
	def private HashMap<String,GenClass> get_genmodel_classes(GenPackage gp){
		var gn_path_array = URI.createFileURI(genmodel_xmi_fq_path).toString().split("/")
		var genmodel_folder = String.join("/", Arrays.copyOfRange(gn_path_array, 0 , gn_path_array.length -1)) + "/"
		var gen_classes = new HashMap<String,GenClass>()
		if(gp.eContents().isEmpty()) return gen_classes
		//exit if package is empty
		for(GenClass gc : gp.getGenClasses()){
			var eproxy_uri = (gc.getEcoreClassifier() as EClassifierImpl).eProxyURI()
			var String fq_classname
			if(!eproxy_uri.isFile()){
				 fq_classname = eproxy_uri.toString().replaceAll(".ecore#//", "/")
			} else {
				//if the genmodel file is not in working directory, the whole path is added in front
				//of package hierarchy. It needs to be stripped away to be able to use it
				fq_classname = eproxy_uri.toString().replace(genmodel_folder, "")
				 									.replaceAll(".ecore#//", "/")
			}
			fq_classname = (super_package_name === null || super_package_name.isEmpty) ?
						   fq_classname : super_package_name + "/" + fq_classname
			gen_classes.put(fq_classname, gc)
			//register all genclasses with their full path
		}
		if(gp.getSubGenPackages().isEmpty()) return gen_classes
		//exit if there are no subpackages
		for(GenPackage gp_sub : gp.getSubGenPackages()){
			gen_classes.putAll(get_genmodel_classes(gp_sub))
			//repeat process for all subpackages
		}
		return gen_classes
	}

	/**########################Getters########################*/

	/**
	 * The GenModel-XMI can specify a package in which the contained meta-model is contained.<br>
	 * This getter returns the fqdn-package name stored in
	 * {@link #super_package_name super_package_name}
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_super_package_name() {
		return super_package_name
	}

	/**
	 * getter for {@link #reverse_ecoreclass_name_map ecore-class registry}.<br>
	 * The returned map contains all EClasses which are specified by the XMI-files.<br>
	 * The key is the class and the value is a String representing the classes position in the
	 * specified package hierarchy.
	 * @return HashMap<EClass, String>
	 * @author Adrian Zwenger
	 */
	def HashMap<EClass, String> get_object_to_class_name_map(){
		return reverse_ecoreclass_name_map
	}

	/**
	 * Returns true if the the passed value is contained in the 
	 * {@link #reverse_ecoreclass_name_map ecore-class registry}.<br>
	 * @param e_class EClassifier the class to check
	 * @return boolean
	 * @author Adrian Zwenger
	 */
	def boolean eclass_is_registered(EClassifier e_class){
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
	def HashMap<EPackage,HashSet<EClass>> get_epackage_and_contained_classes_map(){
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
	def HashMap<EPackage,HashSet<EDataType>> get_epackage_and_contained_e_data_types_map(){
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
	def HashMap<EPackage,HashSet<EEnum>> get_epackage_and_contained_eenums_map(){
		return epackage_and_contained_eenums
	}

	/**
	 * Returns a HashMap with ESructuralFeatures specified by Ecore and GenModel XMI-files
	 * as key and a corresponding
	 * AbstractObjectFieldInspector as value.<br>
	 * Returns the value of
	 * {@link #struct_features_to_inspector_map struct_features_to_inspector_map}.
	 * @return HashMap<EStructuralFeature,AbstractObjectFieldInspector>
	 * @author Adrian Zwenger
	 */
	def get_struct_features_to_inspector_map(){
		return this.struct_features_to_inspector_map
	}

	/**
	 * Returns a mapping of EPackages to their corresponding PackageInspector.<br>
	 * Returns value of
	 * {@link #packages_to_package_inspector_map packages_to_package_inspector_map}.
	 * @return HashMap<EPackage, PackageInspector>
	 * @author Adrian Zwenger 
	 */
	def get_packages_to_package_inspector_map(){
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
	def get_generic_type_to_var_name_map_for_eclass(EClass e_class){
		return this.eclass_to_etypeparam_to_var_name_map.get(e_class)
	}

	/**########################Overridden methods########################*/

	/**
	 * Returns the Hash-Code of this object
	 * @return int
	 * @author Adrian Zwenger
	 */
	override int hashCode(){
		return this.packages_to_package_inspector_map.hashCode
	}
	
	/**
	 * checks given object for equality
	 * @param o Object
	 * @return boolean
	 * @author Adrian Zwenger
	 */
	override boolean equals(Object o){
		if(!(o instanceof EcoreGenmodelParser)) return false
		return (o as EcoreGenmodelParser).packages_to_package_inspector_map.equals(this.packages_to_package_inspector_map)
	}

	/**########################Updater methods########################*/

	/**
	 * Always call this method when making changes to a PackageInspector which was gotten from
	 * this ECoreGenmodelParser. If not done so other class will not profit from those changes.
	 * @param e_pak EPackage the EPackage which the given e_pak_inspector is inspecting
	 * @param e_pak_inspector PackageInspector
	 * @author Adrian Zwenger
	 */
	def update_package_inspector(EPackage e_pak, PackageInspector e_pak_inspector){
		if(!e_pak_inspector.equals(e_pak))
			throw new IllegalArgumentException(
				"The given Inspector does not inspect the given EPackage"
			)

		if(this.packages_to_package_inspector_map.containsKey(e_pak))
			this.packages_to_package_inspector_map.put(e_pak, e_pak_inspector)
		return this.packages_to_package_inspector_map
	}
}
