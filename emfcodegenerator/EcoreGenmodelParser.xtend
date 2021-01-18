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
import org.eclipse.emf.ecore.impl.EPackageImpl
import org.eclipse.emf.ecore.impl.EClassImpl;
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
import java.util.Set
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

class EcoreGenmodelParser {
	
	/**########################Attributes########################*/

	/**
	 * maps all GenClasses found in genmodel-xmi to their URI-name. used to verify the ecore and genmodel
	 * xmi files by comparing the uri's with each other
	 */
	var HashMap<String,GenClass> genclass_name_map = new HashMap<String,GenClass>()

	/**
	 * maps all EClasses found in ecore-xmi to their URI-name. used to verify the ecore and genmodel
	 * xmi files by comparing the uri's with each other
	 */
	var HashMap<String,EClassImpl> ecoreclass_name_map = new HashMap<String,EClassImpl>()

	/**
	 *  maps all EClasses found in ecore-xmi to their URI-name but reversed
	 */
	var HashMap<EClassImpl,String> reverse_ecoreclass_name_map = new HashMap<EClassImpl,String>()

	/**
	 * a mapping with EPackage as key and its containing EClasses as value
	 */
	var HashMap<EPackage,HashSet<EClassImpl>> epackage_and_contained_classes = new HashMap<EPackage,HashSet<EClassImpl>>()
	
	/**
	 * a mapping with EPackage as key and its containing EDataTypes as value
	 */
	var HashMap<EPackage,HashSet<EDataType>> epackage_and_contained_edatatypes = new HashMap<EPackage,HashSet<EDataType>>()
	
	var HashMap<EPackage,HashSet<EEnum>> epackage_and_contained_eenums =
		new HashMap<EPackage,HashSet<EEnum>>()

	/**
	 * the genmodel xmi can specify a toplayer package name which will be stored here or null
	 */
	var String super_package_name

	/**
	 * top-layer EPackage in hierarchy
	 */
	var EPackageImpl super_package

	/**
	 * stores the path to the genmodel-xmi as String
	 */
	var String genmodel_xmi_fq_path

	/**
	 * maps found EStructuralFeature to a newly created AbstractObjectFieldInspector
	 */
	var HashMap<EStructuralFeature,AbstractObjectFieldInspector> struct_features_to_inspector_map =
		new HashMap<EStructuralFeature,AbstractObjectFieldInspector>()

	/**
	 * maps found EPackage to a newly created PackageInspector
	 */
	var HashMap<EPackage, PackageInspector> packages_to_package_inspector_map =
		new HashMap<EPackage, PackageInspector>()

	/**
	 * stores all EClasses as key and a HashMap as well which stores all the EClasses ETypeParameters
	 * and their designated variable name
	 */
	var HashMap<EClass,HashMap<ETypeParameter,String>> eclass_to_etypeparam_to_var_name_map = 
		new HashMap<EClass,HashMap<ETypeParameter,String>>()

	/**########################Constructors########################*/

	/**
	 * constructs a new EcoreGenmodelParser
	 * @param String path to the ecore-xmi
	 * @param String path to the genmodel-xmi
	 */
	new(String ecore_path, String genmodel_path){
		genmodel_xmi_fq_path = genmodel_path
		parse_genmodel(genmodel_path)
		super_package = parse_ecore(ecore_path)
		//verify that ecore and genmodel contain the same classes
		if(!this.genclass_name_map.keySet().equals(ecoreclass_name_map.keySet())){
			println("1 " + genclass_name_map.keySet())
			println("2 " + ecoreclass_name_map.keySet())
			println(super_package_name)
			throw new UnsupportedOperationException("genmodel and ecore do not specify same classes")
		}
		for(EPackage e_pak : this.get_epackage_and_contained_classes_map.keySet){
			var e_pak_inspector = new PackageInspector(e_pak as EPackageImpl, this)
			this.packages_to_package_inspector_map.put(e_pak, e_pak_inspector)
		}
	}

	/**########################Parsers########################*/

	/**
	 * parses the defined classes from the ecore-xmi and populates object attributes
	 * @param String path to ecore-xmi
	 */
	def EPackageImpl parse_ecore(String ecore_path){
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
				.put("ecore", new XMIResourceFactoryImpl());
		//register "ecore" as valid file extension
		var epak = (new ResourceSetImpl()).getResource(URI.createFileURI(ecore_path), true)
										  .getContents().get(0) as EPackageImpl
	  	//get super EPackage from ecore-xmi
	  	var proxy_uri_extension = "/"
	  	// + ".ecore#//")//exchange if the full ProxyUri is used with genmodel
	  	var fq_classname = epak.getName() + proxy_uri_extension
	  	fq_classname = (super_package_name === null || super_package_name.isEmpty) ?
	  					fq_classname : super_package_name + "/" + fq_classname
		this.ecoreclass_name_map = get_ecore_classes(epak, fq_classname)
		//register all classes with fqdn
		for(String key : ecoreclass_name_map.keySet){
			this.reverse_ecoreclass_name_map.put(ecoreclass_name_map.get(key), key)
		}
		return epak
	}

	/**
	 * recursively registers all classes found in ecore-xmi and creates a HashMap where the key
	 * is a string which represents the classes position in the package hierarchy and the 
	 * EClass as a value itself
	 * @param epak toplevel EPackage
	 * @param package_path String giving the toplevel package path/name
	 */
	def private HashMap<String,EClassImpl> get_ecore_classes(EPackage epak, String package_path){
		var e_classes = new HashMap<String,EClassImpl>()
		var e_data_types = new HashSet<EDataType>()
		var e_enums = new HashSet<EEnum>()
		//exit recursion as soon as a package has no content at all
		if(epak.eContents().isEmpty()){
			epackage_and_contained_classes.put(epak, new HashSet<EClassImpl>())
			epackage_and_contained_edatatypes.put(epak, e_data_types)
			return e_classes	
		}

		//iterate over all objects
		for(EObject e_obj: epak.eContents()){
			if(e_obj instanceof EClassImpl){
				var e_class = e_obj as EClassImpl
				//register all classes in package
				e_classes.put(package_path + (e_class).getName(),
							  e_obj as EClassImpl)
				
				for(EStructuralFeature feature : e_class.EAllStructuralFeatures){
					if(!struct_features_to_inspector_map.containsKey(feature)){
						if(feature instanceof EAttribute){
							struct_features_to_inspector_map.put(
								feature as EAttribute,
								new AttributeInspector(feature as EAttribute, this.super_package_name)
							)
						} else if(feature instanceof EReference){
							struct_features_to_inspector_map.put(
								feature as EReference,
								new ReferenceInspector(feature as EReference, this.super_package_name)
							)
						}
					}
				}
				var generic_type_params_map = new HashMap<ETypeParameter, String>()
				for(ETypeParameter e_param : e_class.ETypeParameters){
					var generic_name = e_class.name.substring(0,1).toLowerCase +
								   	   e_class.name.substring(1) + "EClass_" + e_param.name
			   	    generic_type_params_map.put(e_param, generic_name)
				}
				eclass_to_etypeparam_to_var_name_map.put(e_class, generic_type_params_map)
			}
			else if(e_obj instanceof EDataType && !(e_obj instanceof EEnum)) {
				e_data_types.add(e_obj as EDataType)
				//println(e_obj instanceof EEnum)
			} else if(e_obj instanceof EEnum){
				e_enums.add(e_obj as EEnum)
			}
		}
		
		epackage_and_contained_eenums.put(epak, e_enums)
		
		epackage_and_contained_edatatypes.put(epak, e_data_types)
		
		epackage_and_contained_classes.put(epak, new HashSet<EClassImpl>(e_classes.values))
		
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
	 * parses the defined classes from the genmodel-xmi and populates object attributes
	 * @param String path to genmodel-xmi
	 */
	def void parse_genmodel(String genmodel_path){
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
				.put("genmodel", new XMIResourceFactoryImpl())
		// register *.genmodel xmi
		var res_impl = new ResourceSetImpl()
		res_impl.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("genmodel", new EcoreResourceFactoryImpl())
		// teach resource how to read *.genmodel
		res_impl.getPackageRegistry().put(GenModelPackage.eNS_URI, GenModelPackage.eINSTANCE)
		// get the genmodel
		var gen_model = res_impl.getResource(URI.createFileURI(genmodel_path), true)
								.getContents().get(0) as GenModel
		//gen_model.
		this.super_package_name = gen_model.getGenPackages().get(0).basePackage
		this.genclass_name_map = get_genmodel_classes(gen_model.getGenPackages().get(0))
		//register all classes found in the genmodel-xmi
	}

	/**
	 * recursively registers all classes found in genmodel-xmi and creates a HashMap where the key
	 * is a string which represents the classes position in the package hierarchy and the 
	 * GenClass as a value itself
	 * @param gp toplevel GenPackage
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
				//of package hierarchy. needs to be stripped away
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
	 * return the genmodel-specified superpackage name. Null if non specified
	 */
	def String get_super_package_name() {
		return super_package_name
	}

	/**
	 * getter for genclass registry
	 */
	def Set<String> get_class_names(){
		return genclass_name_map.keySet()
	}

	/**
	 * getter for ecoreclass registry
	 */
	def HashMap<String,EClassImpl> get_class_name_to_object_map(){
		return ecoreclass_name_map
	}

	/**
	 * getter for ecoreclass registry
	 */
	def HashMap<EClassImpl, String> get_object_to_class_name_map(){
		return reverse_ecoreclass_name_map
	}

	/**
	 * returns a HashMap containing an EPackage as key and a HashSet of Eclasses contained in said
	 * package as a HashSet
	 * @return HashMap<EPackage,HashSet<EClassImpl>>
	 */
	def HashMap<EPackage,HashSet<EClassImpl>> get_epackage_and_contained_classes_map(){
		return epackage_and_contained_classes
	}

	/**
	 * returns the top-most EPackage in the package hierarchy
	 */
	def EPackageImpl get_top_layer_epackage(){
		return super_package
	}

	def HashMap<EPackage,HashSet<EDataType>> get_epackage_and_contained_e_data_types_map(){
		return epackage_and_contained_edatatypes
	}


	def HashMap<EPackage,HashSet<EEnum>> get_epackage_and_contained_eenums_map(){
		return epackage_and_contained_eenums
	}

	def get_struct_features_to_inspector_map(){
		return struct_features_to_inspector_map
	}

	def get_packages_to_package_inspector_map(){
		return packages_to_package_inspector_map
	}
	
	/**
	 * returns a HashMap<ETypeParameter,String> which contains the ETypeParameters of the passed
	 * class as value and the designated variable name to be used in the Package-class 
	 * "public void initializePackageContents()" method
	 */
	def get_generic_type_to_var_name_map_for_eclass(EClass e_class){
		return this.eclass_to_etypeparam_to_var_name_map.get(e_class)
	}
	
	override int hashCode(){
		return this.packages_to_package_inspector_map.hashCode
	}
	
	override boolean equals(Object o){
		if(!(o instanceof EcoreGenmodelParser)) return false
		return (o as EcoreGenmodelParser).packages_to_package_inspector_map.equals(this.packages_to_package_inspector_map)
	}
	
	def update_package_inspector(EPackage e_pak, PackageInspector e_pak_inspector){
		if(this.packages_to_package_inspector_map.containsKey(e_pak))
			this.packages_to_package_inspector_map.put(e_pak, e_pak_inspector)
		return this.packages_to_package_inspector_map
	}
}