package emfcodegenerator.creators.util

import emfcodegenerator.EcoreGenmodelParser
import emfcodegenerator.inspectors.ObjectFieldInspector
import emfcodegenerator.inspectors.util.EOperationInspector
import emfcodegenerator.inspectors.util.PackageInspector
import java.io.File
import java.io.FileWriter
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.impl.EClassImpl
import org.eclipse.emf.ecore.EAttribute
import emfcodegenerator.inspectors.util.AttributeInspector
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EOperation
import java.util.Arrays
import org.eclipse.emf.ecore.EPackage
import emfcodegenerator.inspectors.util.ReferenceInspector
import emfcodegenerator.inspectors.util.AbstractObjectFieldInspector
import emfcodegenerator.inspectors.InspectedObjectType
import emfcodegenerator.util.collections.SmartCollectionFactory

/**
 * creates the source code for an EClass
 */
class SourceCodeCreator extends InterfaceCreator {

	/**########################Attributes########################*/

	/**
	 * HashMap containing the a method declaration as key and a method body as value
	 */
	var HashMap<String,String> methods

	/**
	 * String storing the classes declaration
	 */
	var String class_declaration

	/**
	 * HashSet containing ObjectFieldInspector's for unsettable data-fields
	 */
	var HashSet<ObjectFieldInspector> unsettable_data_fields

	/**
	 * ArrayList containing all data-field declarations as String
	 */
	var ArrayList<String> data_field_declarations

	/**
	 * stores the package name, where the corresponding Interface for given EClass is located
	 */
	var String emf_interface_package

	/**
	 * stores the super class from which this class inherits directly as inheritance in java is only
	 * possible from 1 class
	 */
	var EClassImpl the_super_class = null

	/**
	 * stores the classes from which the EClass inherits indirectly. As EMF allows for
	 * multi-inheritance direct and indirect inheritance must be differentiated.</br>
	 * In other words, attributes and references from the indirect inherited classes
	 * need to be implemented too.
	 */
	var ArrayList<EClass> other_super_classes = null

	/**
	 * stores if the super class is a custom emf-generated class
	 */
	var boolean super_class_is_model_defined = false

	/**
	 * map containing EPackage as key and its PackageInspector as value
	 */
	var HashMap<EPackage,PackageInspector> e_pak_map

	/**########################Constructors########################*/

	/**
	 * Constructs a new SourceCodeCreator
	 * @param eclass EClassImpl the EClass for which a file shall be created
	 * @param gen_model EcoreGenmodelParser the wrapper for the ecore-xmi and genmodel-xmi files
	 * @param e_data_fields HashSet<ObjectFieldInspector> the ObjectFieldInspector's for the
	 * contained Attributes/References
	 * @param e_operations HashSet<EOperationInspector> the EOperationInspector's for all
	 * the EOperations
	 * @param e_pak PackageInspector the Inspector for the package which contains the EClass
	 * @author Adrian Zwenger
	 */
	new(
		EClass eclass, 
		EcoreGenmodelParser gen_model,
		HashSet<AbstractObjectFieldInspector> e_data_fields,
		HashSet<EOperationInspector> e_operations,
		PackageInspector e_pak
	){
		super(eclass, gen_model, e_data_fields, e_operations, e_pak)
		this.emf_interface_package = new String(this.package_declaration)
		this.package_declaration += ".impl"
		this.e_pak = e_pak
		this.e_pak_map = SourceCodeCreator.emf_model.get_packages_to_package_inspector_map
		add_import_as_String(e_pak.get_package_declaration_name + "." +
							 e_pak.get_emf_package_class_name)
		this.init_super_types()
		this.add_import_as_String(
			this.e_pak.get_eclass_to_needed_imports_for_type_arguments_map.get(e_class)
		)
	}

	/**
	 * is called by the constructor. It registers direct and indirect classes to inherit from and
	 * thus discerns which things (EAttributes, EReferences, EOperations, methods)
	 * need to be implemented and which not
	 * @auhtor Adrian Zwenger
	 */
	def private void init_super_types(){
		this.other_super_classes = new ArrayList<EClass>(this.e_class.ESuperTypes)
		if(!this.other_super_classes.isEmpty){
			var String import_string
			//EMF stores the SuperTypes according to their level in the hierarchy
			//the first element is thus the next class when traversing the hierarchy bottom up
			//it is the class from which is directly inherited of
			this.the_super_class = this.other_super_classes.get(0) as EClassImpl

			if(
				EMFPackageSourceCreator.emf_model.eclass_is_registered(this.the_super_class) &&
				!this.the_super_class.isInterface
			){
				//The direct super-class was registered when parsing the Ecore and Genmodel xmi
				//and is not an interface (thus an implementation of it exists)
				this.super_class_is_model_defined = true
				//remove the class from the super-classes list to prevent parsing it twice
				this.other_super_classes.remove(0)
				
				//create the import name
				import_string =
					this.create_import_name_for_ereference_or_eclass(this.the_super_class)
				var buffer = import_string.split("\\.")
				//to be a valid import string for EMF generated classes the import string must be
				//changed to point towards an implemented class instead of its interface
				import_string = String.join(
					".",
					Arrays.copyOfRange(buffer, 0, buffer.length - 1)
				)
				import_string += ".impl." + buffer.get(buffer.size - 1) + "Impl"
				//add the import
				this.add_import_as_String(import_string)
			} else {
				//the class was either specified in the xmi-files or it is an interface
				this.super_class_is_model_defined = false
				//it is not removed from the indirect inheritance list, as it needs to be processed
				//there is no direct, implemented class from which is inherited, thus 
				//setting the super class to null
				this.the_super_class = null
			}

			//process all the class from which this EClass inherits indirectly
			for(super_ecl : this.other_super_classes){
				if(
					EMFPackageSourceCreator.emf_model.eclass_is_registered(super_ecl)
				){
					//if the class is already registered, it does not need to be re-registered
					var the_package = this.e_pak_map.get(super_ecl.EPackage)
					//get the package inspector for this class as it holds all the other
					//needed inspectors
					if(!the_package.is_initialized) {
						//if the package was somehow not initialized do it now
						the_package.initialize()
						//update the global EcoreGenmodelParser instance with the newly
						//initialized package
						this.e_pak_map = SourceCodeCreator.emf_model.update_package_inspector(
								the_package.get_emf_e_package, the_package
							)
					}
					//generate the import String by concatenating the package declaration
					//with the class name
					import_string = the_package.get_package_declaration_name + "." + super_ecl.name
				} else {
					//if it was not registered it is most likely some EMF class with no proper info
					//they are not supported
					//as it is EMF, all EClasses have EAttributes and so on
					//thus it is treated as another custom class
					//because most EMF classes are specified in org.eclipse.emf.ecore
					//I assume, that its interface lives there
					import_string = "org.eclipse.emf.ecore." + super_ecl.EPackage.name
					for(EAttribute e_attr : super_ecl.EAllAttributes){
						//register the EAttributes and create an Inspector for them
						if(
							EMFPackageSourceCreator.emf_model.get_struct_features_to_inspector_map
															 .containsKey(e_attr)
						){
							this.e_data_fields.add(
								EMFPackageSourceCreator.emf_model
													   .get_struct_features_to_inspector_map
													   .get(e_attr)
							)
						} else {
							this.e_data_fields.add(
								new AttributeInspector(e_attr, EMFPackageSourceCreator.emf_model)
							)
						}
					}
					for(EReference e_ref : super_ecl.EAllReferences){
						//register the EReferences and create an Inspector for them
						if(
							EMFPackageSourceCreator.emf_model
												   .get_struct_features_to_inspector_map
												   .containsKey(e_ref)
						){
							this.e_data_fields.add(
								EMFPackageSourceCreator.emf_model
													   .get_struct_features_to_inspector_map
													   .get(e_ref)
							)
						} else {
							this.e_data_fields.add(
								new ReferenceInspector(e_ref, EMFPackageSourceCreator.emf_model)
							)
						}
					}
					for(EOperation e_op : super_ecl.EAllOperations)
						this.e_operations.add(
							new EOperationInspector(e_op, EMFPackageSourceCreator.emf_model)
						)
					//create an inspector for all the EOperations as well
				}
				//import the class
				this.add_import_as_String(import_string)
			}
		}

		if(this.super_class_is_model_defined){
			var the_package = this.e_pak_map.get(this.the_super_class.EPackage)
			if(!the_package.is_initialized) {
				//if the package was somehow not initialized do it now
				the_package.initialize()
				//update the global EcoreGenmodelParser instance with the newly
				//initialized package
				this.e_pak_map = SourceCodeCreator.emf_model.update_package_inspector(
						the_package.get_emf_e_package, the_package
					)
			}
			
			// if the super-class is defined in the ECore and is implemented
			// then this EClass can inherit from it directly and the inherited Features do not need
			// to be registered and processed. Thus they are removed
			this.e_data_fields.removeAll(
				the_package.get_all_object_field_inspectors_for_class(this.the_super_class)
			)
			//EOperations are inherited. Thus they need to be removed from the to-be-processed pool
			this.e_operations.removeAll(
				the_package.get_all_eoperation_inspector_for_class(this.the_super_class)
			)
		}
	}

	/**########################Getters########################*/
	
	/**
	 * @inerhitDoc
	 */
	override String get_name(){
		return e_class.name + "Impl"
	}

	/**
	 * Returns the classes declaration which is generated by
	 * {@link #create_class_declaration create_class_declaration()}
	 * </br>
	 * <b>example:</b> <xmp>public class MyClassImpl<T> extends SmartObject implements MyClass<T></xmp>
	 * <br>
	 * @return String representing the declaration of an class including their generics if existent.
	 * @auhtor Adrian Zwenger
	 */
	override get_declaration(){
		if(!this.is_initialized)
			throw new RuntimeException('''The «this.class» was not initialized.'''.toString)
		return class_declaration
	}

	/**########################Declaration-Generation########################* */

	/**
	 * This method generates and returns a String representing the class's declaration which can
	 * then be retrieved by calling {@link #get_declaration get_declaration()}
	 * @return String
	 * @author Adrian Zwenger
	 */
	def private String create_class_declaration() {
		//TODO move constants to some global instance instead of storing them locally
		var super_class_import = "emfcodegenerator.util.SmartObject"
		var super_class_name = "SmartObject"

		//update the super-class name if it is not the standard super-class for
		//all SmartEMF-EClasses
		if(this.the_super_class !== null && !this.the_super_class.isInterface) {
			super_class_name = (this.super_class_is_model_defined) ?
				this.the_super_class.name + "Impl" : this.the_super_class.name
		}
		//import the super-class
		add_import_as_String(super_class_import)
		//import the interface for this EClass
		add_import_as_String(this.emf_interface_package + "." + e_class.name)

		var declaration = ((e_class.isAbstract) ? "public abstract class " : "public class ")

		// add the type params and generics
		declaration += this.get_name + this.generic_type_declaration_string
		
		declaration += " extends "

		//set the direct super-class
		if(
		   this.the_super_class !== null &&
		   this.generic_super_types_map.containsKey(this.the_super_class)
	   	){
	   		//the super-class is model-defined
	   		//now get the full class name including type params
	   		//example: MyClass<Z>
			super_class_name = this.etype_param_declarationgetter(
					this.generic_super_types_map.get(this.the_super_class), ""
				)
			if(SourceCodeCreator.emf_model.eclass_is_registered(the_super_class)){
				//if an implementation is generated change the name to Implementation type name
				//MyClass<Z> --> MyClassImpl<Z>
				super_class_name = 
					super_class_name.replaceFirst(
						the_super_class.name,
						the_super_class.name + "Impl"
					)
			}
		}

		declaration += super_class_name + " implements " + e_class.name
		var iterator = this.e_class.ETypeParameters.iterator
		if(iterator.hasNext) {
			declaration += "<"
			while(iterator.hasNext){
				declaration += iterator.next.name
				if(iterator.hasNext) declaration += ","
			}
			declaration += ">"
		}
		return declaration
	}
	
	/**
	 * creates two constructors, one default one without parameters and one with a EClass type
	 * parameter for inheritance, as sub class will pass the type to the super class.
	 * @return HashSet<String,String> key is method name value is method body
	 * @author Adrian Zwenger
	 */
	def private HashMap<String,String> create_constructor(){
		if(!this.is_initialized)
			throw new RuntimeException('''The «this.class» was not initialized.'''.toString)
		/*
		 * Two constructors are needed. One regular one and one which passes an EClass bottom-up
		 * through the inheritance hierarchy.<br>
		 * The regular one is needed in the EMF-Package-Factories, where the runtime-classes
		 * are created. For this no argument is needed, as it just needs to pass its own
		 * meta-model-class to its super-class.<br>
		 * However, if the super-class is not the base-class for all SmartEMF-objects
		 * (take a look at{@link emfgenerator.util.SmartObject SmartObject}, a constructor needs to
		 * be implemented, which takes a meta-model-class as argument and just passes it to its
		 * super-class. Thus the model can propagate all the way to its top-most class in
		 * inheritance hierarchy. All SmartEMF classes should have these two constructors to make
		 * inheritance easier.
		 */
		this.add_import_as_String("org.eclipse.emf.ecore.EClass")
		var method = new HashMap<String,String>()
		var declaration = IDENTION + "protected " + this.get_name

		//regular constructor
		var body = 
'''«IDENTION»«IDENTION»super(«e_pak.get_emf_package_class_name».Literals.«emf_to_uppercase(super.get_name)»);//regular constructor'''.toString()

		method.put(declaration + "()", body)
		
		//constructor for inheritance
		declaration += "(EClass type)"
		body = '''«IDENTION»«IDENTION»super(type);//constructor for inheritance'''.toString

		method.put(declaration, body)
		return method
	}

	/**########################Object-Field Declaration########################*/

	/**
	 * creates the declaration for all data-fields the class needs for a given EstructuralFeature
	 * represented by its
	 * {@link emfcodegenerator.inspectors.ObjectFieldInspector ObjectfieldInspector} and returns
	 * them contained in an ArrayList
	 * @param obj_field ObjectFieldInspector for an EStructuralFeature of this class
	 * @return ArrayList<String> each entry is one LOC declaring the a data-field for the class.
	 * @author Adrian Zwenger
	 */
	def protected ArrayList<String> create_object_field_declaration(ObjectFieldInspector obj_field){
		var declarations = new ArrayList<String>()
		var elist_type =
			SmartCollectionFactory.get_elist_type_name(obj_field.get_needed_elist_type_enum)
		var obj_field_type = obj_field.get_object_field_type_name
		var data_type = (obj_field.is_a_tuple) ? 
			'''«elist_type»<«obj_field_type»>'''.toString : obj_field_type

		var default_value =
			(obj_field.get_inspected_object_type === InspectedObjectType.EREFERENCE) ?
				(obj_field as ReferenceInspector).get_default_value_if_contained_reference(
					this.get_estructural_feature_getter_for_objectfield_inspector(obj_field)
				) : 
				obj_field.get_default_value()

		declarations.add(
	'''«IDENTION»protected «data_type» «obj_field.get_name()» = «default_value»;'''
		)

		if(obj_field.is_unsettable){
			declarations.add(
				'''«IDENTION»protected boolean «obj_field.get_name»IsSet = false;'''.toString
			)
		}

		this.add_import_as_String(obj_field.get_needed_imports())
		return declarations
	}

	/**########################Method-Generation########################*/

	/**
	 * This method creates a HashMap where the key is the declaration and the value is the body
	 * of a getter method generated for a given
	 * {@link emfcodegenerator.inspectors.ObjectFieldInspector ObjectfieldInspector}
	 * @param obj_field ObjectFieldInspector
	 * @return HashMap<String,String>
	 * @author Adrian Zwenger
	 */
	def protected HashMap<String,String> create_getter_for_object_field(ObjectFieldInspector obj_field){
		var declaration = IDENTION + "public " + create_getter_method_stump((obj_field))
		//var body = IDENTION + IDENTION + '''return  this.«obj_field.get_name()»;'''.toString
		var body =
'''«IDENTION»«IDENTION»return «(obj_field.is_a_tuple) ? ''' (EList<«obj_field.get_object_field_type_name»>) ''' : " "» this.«obj_field.get_name()»;'''

		var map = new HashMap<String,String>()
		map.put(declaration, body)
		return map
	}

	/**
	 * This method creates a HashMap where the key is the declaration and the value is the body
	 * of a setter method generated for a given
	 * {@link emfcodegenerator.inspectors.ObjectFieldInspector ObjectfieldInspector}.
	 * TODO setter methods need to send Notifications.
	 * @param obj_field ObjectFieldInspector
	 * @return HashMap<String,String>
	 * @author Adrian Zwenger
	 */
	def protected HashMap<String,String> create_setter_for_object_field(ObjectFieldInspector obj_field){
		if(!this.is_initialized)
			throw new RuntimeException('''The «this.class» was not initialized.'''.toString)

		val boolean is_reference_and_contained =
			obj_field.get_inspected_object_type === InspectedObjectType.EREFERENCE &&
			(obj_field as ReferenceInspector).is_contained()
			
		var declaration = IDENTION + "public " + create_setter_method_stump((obj_field))
		var body = ""

		//if the passed feature is an EReference, then the containment of said reference must be
		//handled. In that case the old contained object needs its containment flag reset
		if(is_reference_and_contained)
			body =
'''
«IDENTION»«IDENTION»if(this.«obj_field.get_name» != null) ((emfcodegenerator.util.MinimalSObjectContainer) this.«obj_field.get_name»).reset_containment();
'''.toString()

		if(obj_field.is_a_tuple()){
			var elist_type =
				SmartCollectionFactory.get_elist_type_name(obj_field.get_needed_elist_type_enum)
			var obj_field_type = obj_field.get_object_field_type_name
			add_import_as_String(
				SmartCollectionFactory.get_elist_import_String(obj_field.get_needed_elist_type_enum)
			)

			body +=
'''«IDENTION»«IDENTION»if(value instanceof «elist_type»){
«IDENTION»«IDENTION»«IDENTION»this.«obj_field.get_name()» = («elist_type»<«obj_field_type»>) value;
«IDENTION»«IDENTION»} else {
«IDENTION»«IDENTION»«IDENTION»throw new IllegalArgumentException();
«IDENTION»«IDENTION»}
'''.toString()

		} else {
			body +=
'''
«IDENTION»«IDENTION»this.«obj_field.get_name()» = value;
'''.toString()
		}

		//if a new reference is set, then that containment needs to be set
		if(is_reference_and_contained){
			

			body +=
'''
«IDENTION»«IDENTION»((emfcodegenerator.util.MinimalSObjectContainer) this.«obj_field.get_name()»).set_containment(
«IDENTION»«IDENTION»«IDENTION»this,
«IDENTION»«IDENTION»«IDENTION»«this.get_estructural_feature_getter_for_objectfield_inspector(obj_field)»
«IDENTION»«IDENTION»);'''

	}
			
		//if the object field is not settable, then the flag must be reset to true
		if(obj_field.is_unsettable) 
			body += System.lineSeparator + IDENTION + IDENTION +
					"this." + obj_field.get_name + "IsSet = true;"
		var map = new HashMap<String,String>()
		map.put(declaration, body)
		return map
	}

	/**
	 * This method creates a HashMap where the key is the declaration and the value is the body
	 * of a IsSet method generated for a given
	 * {@link emfcodegenerator.inspectors.ObjectFieldInspector ObjectfieldInspector}.
	 * @param obj_field ObjectFieldInspector
	 * @return HashMap<String,String>
	 * @author Adrian Zwenger
	 */
	def protected HashMap<String,String> create_isset_for_object_field(ObjectFieldInspector obj_field){
		var declaration = IDENTION + "public " + create_isset_method_stump(obj_field)
		var body = IDENTION + IDENTION + '''return «obj_field.get_name()»IsSet;'''
		var map = new HashMap<String,String>()
		map.put(declaration, body)
		return map
	}

	/**
	 * This method creates a HashMap where the key is the declaration and the value is the body
	 * of a UnSet method generated for a given
	 * {@link emfcodegenerator.inspectors.ObjectFieldInspector ObjectfieldInspector}. 
	 * @param obj_field ObjectFieldInspector
	 * @return HashMap<String,String>
	 * @author Adrian Zwenger
	 * TODO: eNotification support
	 */
	def protected HashMap<String,String> create_unset_for_object_field(ObjectFieldInspector obj_field){
		var declaration = IDENTION + "public " + create_unset_method_stump(obj_field)
		var body = IDENTION + IDENTION +
				   '''this.«obj_field.get_name» = «obj_field.get_default_value»;'''.toString +
				   System.lineSeparator + IDENTION + IDENTION +
				   '''this.«obj_field.get_name»IsSet = false;'''.toString
		var map = new HashMap<String,String>()
		map.put(declaration, body)
		return map
	}

	/**
	 * All SmartEMF objects inherit from SmartObject.
	 * Thus following methods need to be overridden: eGet, eSet, eUnset, eIsSet, toString 
	 * @return HashMap<String,String> key is the declaration and value the implementation
	 * @author Adrian Zwenger
	 */
	def protected HashMap<String,String> create_inherited_methods(){
		var declarations = #[
			IDENTION + "@Override" + System.lineSeparator + IDENTION + 
				"public Object eGet(int feautureID, boolean resolve, boolean coreType)",
			IDENTION + "@Override" + System.lineSeparator + IDENTION +
				"public void eSet(int feautureID, Object newValue)",
			IDENTION + "@Override" + System.lineSeparator + IDENTION +
				"public void eUnset(int feautureID)",
			IDENTION + "@Override" + System.lineSeparator + IDENTION +
				"public boolean eIsSet(int feautureID)",
			IDENTION + "@Override" + System.lineSeparator + IDENTION +
				"public String toString()",
			IDENTION + "@Override" + System.lineSeparator + IDENTION + 
				"public Object eGet(EStructuralFeature eFeature)",
			IDENTION + "@Override" + System.lineSeparator + IDENTION +
				"public void eSet(EStructuralFeature eFeature, Object newValue)"
		]
		this.add_import_as_String("org.eclipse.emf.ecore.EStructuralFeature")
		var inherited_methods = new HashMap<String,String>()

		var e_get_method_body = new StringBuilder()
		var alternate_e_get_method_body = new StringBuilder()
		var e_set_method_body = new StringBuilder()
		var alternate_e_set_method_body = new StringBuilder()
		var e_unset_method_body = new StringBuilder()
		var e_isset_method_body = new StringBuilder()
		var to_string_method_body = new StringBuilder(
			IDENTION + IDENTION +
			'''StringBuilder result = new StringBuilder(super.toString() + '''.toString() +
			'''"(name: «e_class.name») ");'''.toString() +
			System.lineSeparator()
			)

		//if the class contains data fields which are not directly inherited, then those need to be
		//checked before passing request to super class
		if(!this.e_data_fields.isEmpty){
			var switch_case_declaration = 
				'''«IDENTION + IDENTION»switch(feautureID) {«System.lineSeparator»'''.toString()
			e_get_method_body.append(switch_case_declaration)
			e_set_method_body.append(switch_case_declaration)
			e_unset_method_body.append(switch_case_declaration)
			e_isset_method_body.append(switch_case_declaration)
			to_string_method_body.append(
				IDENTION + IDENTION +
				'''result.append(" (");'''.toString() +
				System.lineSeparator()
				)
			
			var iterator = this.e_data_fields.iterator()

			while(iterator.hasNext){
				var attr = iterator.next
				var id_name = emf_to_uppercase(e_class.name) + "__" + emf_to_uppercase(attr.get_name())
				var package_class_name = e_pak.get_emf_package_class_name()
				var attr_name = attr.get_name_with_first_letter_capitalized()
				var attr_type = attr.get_object_field_type_name()

				var case_statement = IDENTION + IDENTION + IDENTION + 
					'''case «package_class_name».«id_name»:'''.toString() + System.lineSeparator() +
					IDENTION + IDENTION + IDENTION + IDENTION
				
				//create eGet case body
				e_get_method_body.append(
					case_statement + ''' return get«attr_name»();''' + System.lineSeparator()
					)
					
				alternate_e_get_method_body.append(
'''«IDENTION»«IDENTION»if («this.get_estructural_feature_getter_for_objectfield_inspector(attr)».equals(eFeature))
«IDENTION»«IDENTION»«IDENTION»return get«attr_name»();''' + System.lineSeparator()
				)
				
				//create eSet case body
				if(attr.is_changeable){
					var elist_type =
						SmartCollectionFactory.get_elist_type_name(attr.get_needed_elist_type_enum)
					var cast_type = (attr.is_a_tuple) ? 
						'''«elist_type»<«attr_type»>'''.toString : attr_type
					e_set_method_body.append(
						case_statement + '''set«attr_name»((«cast_type») newValue);'''.toString() +
						System.lineSeparator()
						)
					alternate_e_set_method_body.append(
						'''«IDENTION»«IDENTION»if («this.get_estructural_feature_getter_for_objectfield_inspector(attr)».equals(eFeature))
«IDENTION»«IDENTION»«IDENTION»{set«attr_name»((«cast_type») newValue); return;}''' + System.lineSeparator()
					)
				}

				//create eUnset case body
				if(attr.is_unsettable && attr.is_changeable)
				e_unset_method_body.append(
					case_statement + '''unset«attr_name»();'''.toString + System.lineSeparator() + 
					IDENTION + IDENTION + IDENTION + IDENTION +"return;" + System.lineSeparator()
					)

				//create eIsSet case body
				if(attr.is_unsettable)
				e_isset_method_body.append(
					case_statement + '''return isSet«attr_name»();'''.toString() +
					System.lineSeparator()
					)
				
				//create toString method body
				
				to_string_method_body.append(
					IDENTION + IDENTION + '''result.append("«attr.get_name»:");'''.toString() +
					System.lineSeparator()
				)
				var to_string_entry = '''result.append(«attr.get_name»'''.toString()
				if(attr instanceof AttributeInspector && !(attr as AttributeInspector).is_a_literal)
					to_string_entry += ".toString()"
				to_string_entry += ");"
				if(attr.is_unsettable){
					to_string_method_body.append(
						IDENTION + IDENTION + '''if(«attr.get_name»IsSet)'''.toString +
						to_string_entry +
						System.lineSeparator + IDENTION + IDENTION +
						'''else result.append("<unset>");'''.toString() + System.lineSeparator()
					)
				} else {
					to_string_method_body.append(
						IDENTION + IDENTION + 
						to_string_entry +
						System.lineSeparator()
						)
				}
				if(iterator.hasNext) to_string_method_body.append(
							IDENTION + IDENTION + '''result.append(", ");''' +
							System.lineSeparator
							)
			}

			var switch_case_closer = '''«IDENTION»«IDENTION»}«System.lineSeparator()»'''.toString

			e_get_method_body.append(switch_case_closer)
			//alternate_e_get_method_body.append(switch_case_closer)
			e_set_method_body.append(switch_case_closer)
			e_unset_method_body.append(switch_case_closer)
			e_isset_method_body.append(switch_case_closer)

			to_string_method_body.append(
				IDENTION + IDENTION + '''result.append(")");'''.toString() +
				System.lineSeparator()
			)
		}

		e_get_method_body.append(
			IDENTION + IDENTION + "return super.eGet(feautureID, resolve, coreType);"
			)
		alternate_e_get_method_body.append(
			IDENTION + IDENTION + "return super.eGet(eFeature);"
			)
		e_set_method_body.append(
			IDENTION + IDENTION + "super.eSet(feautureID, newValue);"
			)
		alternate_e_set_method_body.append(
			IDENTION + IDENTION + "super.eSet(eFeature, newValue);"
			)
		e_unset_method_body.append(
			IDENTION + IDENTION + "super.eUnset(feautureID);"
			)
		e_isset_method_body.append(
			IDENTION + IDENTION + "return super.eIsSet(feautureID);"
			)
		to_string_method_body.append(
			IDENTION + IDENTION + "return result.toString();"
			)

		inherited_methods.put(declarations.get(0), e_get_method_body.toString)
		inherited_methods.put(declarations.get(1), e_set_method_body.toString)
		inherited_methods.put(declarations.get(2), e_unset_method_body.toString)
		inherited_methods.put(declarations.get(3), e_isset_method_body.toString)
		inherited_methods.put(declarations.get(4), to_string_method_body.toString)
		inherited_methods.put(declarations.get(5), alternate_e_get_method_body.toString)
		inherited_methods.put(declarations.get(6), alternate_e_set_method_body.toString)

		return inherited_methods
	}

	/**
	 * Generates all class-methods and populates the methods HashMap and method_declarations
	 * ArrayList.
	 * @author Adrian Zwenger
	 */
	def private void generate_members() {
		if(!this.is_initialized)
			throw new RuntimeException('''The «this.class» was not initialized.'''.toString)

		for(entry : this.create_constructor.keySet){
			this.method_declarations.add(entry)
			this.methods.putAll(this.create_constructor)
		}
		for(ObjectFieldInspector obj_field : e_data_fields){
			//println(obj_field.get_needed_imports())
			add_import_as_String(obj_field.get_needed_imports())
			//add data field declarations
			data_field_declarations.addAll(this.create_object_field_declaration(obj_field))

			var method = create_getter_for_object_field(obj_field)
			//add getters. all Attributes get getters
			for(key : method.keySet){
				method_declarations.add(key)
				methods.put(key, method.get(key))
			}
			//add setters. only changeables get setters
			if(obj_field.is_changeable){
				method = create_setter_for_object_field(obj_field)
				for(key : method.keySet){
					method_declarations.add(key)
					methods.put(key, method.get(key))
				}
			} else {
				//add "basic-setter" needed. All unchangeable data-fields need a basic-setter
				//which the user can use the set the value initially
				//this method needs to support eNotification support as well
				//TODO: Absprache NotificationChain Design und eNotifications Integration
			}
			//add isSet methods for unsettable data fields
			if(obj_field.is_unsettable){
				this.unsettable_data_fields.add(obj_field)
				method = create_isset_for_object_field(obj_field)
				for(key : method.keySet){
					method_declarations.add(key)
					methods.put(key, method.get(key))
				}
			}
			//add unset for changeable and unsettable data fields
			if(obj_field.is_unsettable && obj_field.is_changeable){
				this.unsettable_data_fields.add(obj_field)
				method = create_unset_for_object_field(obj_field)
				for(key : method.keySet){
					method_declarations.add(key)
					methods.put(key, method.get(key))
				}
			}
		}
		//add EOperations
		for(EOperationInspector e_op : e_operations){

			add_import_as_String(e_op.get_needed_imports())
			var e_op_map = e_op.get_method_implementation(IDENTION)
			method_declarations.addAll(e_op_map.keySet)
			methods.putAll(e_op_map)
		}
		//add inherited methods
		var inherited_methods = create_inherited_methods()
		method_declarations.addAll(inherited_methods.keySet)
		methods.putAll(inherited_methods)
	}

	/**
	 * maps an ObjecFieldInspector to the getter command which gets the EStructuralFeature from the
	 * package class. used by
	 * {@link #get_estructural_feature_getter_for_objectfield_inspector(ObjectFieldInspector)
	 * get_estructural_feature_getter_for_objectfield_inspector}.
	 * Static field used in case another class uses the same reference type and command does not
	 * need to be determined again.
	 */
	var static HashMap<ObjectFieldInspector,String> obj_field_to_e_feature_getter_map = null

	/**
	 * When EReferences are set up as a containment feature, the containment must be set. Thus the
	 * container-class and the containing feature (meta-model EReference) must be provided. As the
	 * container class is always the current class for which the code is generated , only the
	 * EReference needs to be accessed. This can be don by calling the defined instances in the
	 * Literals interface of the package. The needed command to generate that call is determined
	 * here.<br>
	 * As that command will most likely be used twice (once to initialize the List if the reference
	 * is a tuple and once when the containment is reset) it will be stored in a
	 * {@link #obj_field_to_e_feature_getter_map Map}
	 * @param obj_field ObjectFieldInspector
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_estructural_feature_getter_for_objectfield_inspector(ObjectFieldInspector obj_field){
		if(obj_field_to_e_feature_getter_map === null)
			obj_field_to_e_feature_getter_map = new HashMap<ObjectFieldInspector,String>()
		//return result immediately if already determined beforehand
		if(obj_field_to_e_feature_getter_map.containsKey(obj_field))
			return obj_field_to_e_feature_getter_map.get(obj_field)

		var package_of_reference_type = this.e_pak_map.get(
			(obj_field.get_inspected_object.eContainer as EClass).EPackage
		)
		//get the package inspector for this class as it holds all the other
		//needed inspectors
		if(!package_of_reference_type.is_initialized) {
			//if the package was somehow not initialized do it now
			package_of_reference_type.initialize()
			//update the global EcoreGenmodelParser instance with the newly
			//initialized package
			this.e_pak_map = SourceCodeCreator.emf_model.update_package_inspector(
					package_of_reference_type.get_emf_e_package, package_of_reference_type
				)
		}
		var command =
'''«package_of_reference_type.get_package_declaration_name».«package_of_reference_type.get_emf_package_class_name».Literals.«obj_field.get_emf_package_literals_interface_var_name()»'''.toString
		obj_field_to_e_feature_getter_map.put(
			obj_field,
			command
		)
		return command
	}
	/**########################Control Flow########################*/

	/**
	 * Initializes this creator and prepares and processes all needed information to generate
	 * a class and write it to a file. This method should always be called first.<br>
	 * @param fq_file_path String fully qualified path to the file which shall be written to
	 * @param IDENTION String represents the String which shall be used to indent code
	 * @author Adrian Zwenger
	 */
	override initialize_creator(String fq_file_path, String IDENTION){
		this.is_initialized = true
		this.fq_file_path = fq_file_path
		this.IDENTION = IDENTION
		this.method_declarations = new ArrayList<String>()
		this.methods = new HashMap<String,String>()
		this.unsettable_data_fields = new HashSet<ObjectFieldInspector>()
		this.data_field_declarations = new ArrayList<String>()
		this.class_declaration = create_class_declaration()
		generate_members()
	}

	/**
	 * starts the writing process writes the interface/class source-code contents to a file and
	 * resets the Creators status back to uninitialised.<br>
	 * Call {@link #initialize_creator(String, String) initialize_creator} first.
	 * @Author Adrian Zwenger
	 */
	override write_to_file(){
		if(!this.is_initialized)
			throw new RuntimeException('''The «this.class» was not initialized.'''.toString)
		var class_file = new File(this.fq_file_path)
		class_file.getParentFile().mkdirs()
		var class_fw = new FileWriter(class_file , false)
		
		//declare the package
		class_fw.write("package " + this.get_package_declaration + ";")
		class_fw.write(System.lineSeparator + System.lineSeparator)
		
		//import needed modules
		for(needed_import : needed_imports){
			class_fw.write('''import «needed_import»;'''.toString)
			class_fw.write(System.lineSeparator)
		}
		class_fw.write(System.lineSeparator)
		
		//declare the class
		class_fw.write(this.get_declaration() + " {")
		class_fw.write(System.lineSeparator + System.lineSeparator)
		
		//declare all data fields
		for(data_field : this.data_field_declarations){
			class_fw.write(data_field + System.lineSeparator)
		}
		class_fw.write(System.lineSeparator)

		//implement the methods
		for(method : this.method_declarations){
			if(!methods.keySet.contains(method))println(method)
			class_fw.write(method + "{" + System.lineSeparator)
			class_fw.write(methods.get(method))
			class_fw.write(System.lineSeparator + IDENTION +  "}" +
						   System.lineSeparator + System.lineSeparator)
		}
		
		//close class and add final newline
		class_fw.write("}" + System.lineSeparator + System.lineSeparator)
		
		//close file-handle
		class_fw.close()
		
		//set Creator status back to uninitialised
		this.is_initialized = false
	}

	override boolean equals(Object other){
		if(!(other instanceof SourceCodeCreator)) return false
		return this.e_class.equals((other as SourceCodeCreator).e_class)
	}
	
	override int hashCode(){
		return this.e_class.hashCode()
	}

}
