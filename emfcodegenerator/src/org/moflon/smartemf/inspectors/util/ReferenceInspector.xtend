package emfcodegenerator.inspectors.util

import emfcodegenerator.EcoreGenmodelParser
import emfcodegenerator.inspectors.util.AbstractObjectFieldInspector
import emfcodegenerator.inspectors.InspectedObjectType
import java.util.ArrayList
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.emf.ecore.impl.EReferenceImpl
import org.eclipse.emf.ecore.EClass
import emfcodegenerator.util.collections.SmartCollectionFactory

/**
 * inspects EReferences
 */
class ReferenceInspector extends AbstractObjectFieldInspector {

	/**########################Attributes########################*/
	
	/**
	 * the EAttribute which shall be inspected
	 */
	var EReferenceImpl e_ref

	/**
	 * stores the declaration for the EReference-getter-method needed in the EMF-package-class
	 */
	var String getter_method_declaration_for_the_package_classes
	
	/**
	 * stores if {@link #getter_method_declaration_for_the_package_classes
	 getter_method_declaration_for_the_package_classes} holds a value
	 */
	var boolean getter_method_declaration_for_the_package_classes_is_generated = false

	/**########################Constructor########################*/

	/**
	 * Constructs a new ReferenceInspector.
	 * @param e_feature EStructuralFeature
	 * @param gen_model EcoreGenmodelParser
	 * @author Adrian Zwenger
	 */
	new(EStructuralFeature e_feature, EcoreGenmodelParser gen_model) {
		super(e_feature, gen_model)
		init(e_feature)
	}

	/**
	 * Constructs a new ReferenceInspector.
	 * @param e_feature EStructuralFeature
	 * @param super_package_name String
	 * @author Adrian Zwenger
	 */
	new(EStructuralFeature e_feature, String super_package_name){
		super(e_feature, super_package_name)
		init(e_feature)
	}

	/**
	 * initializes this ReferenceInspector. This method is called by the constructor
	 * @param e_feature EStructuralFeature
	 * @author Adrian Zwenger
	 */
	def private void init(EStructuralFeature e_feature){
		if(!(e_feature instanceof EReferenceImpl))
			throw new IllegalArgumentException(
				"Expected an EReference, but got a " + e_feature.class.name
			)
		
		this.e_ref = e_feature as EReferenceImpl
		
		//unchangeable EReferences do not get setters. If no setter is provided a
		//reference can never be set. Thus indicating, that the model is faulty.
		if(!this.e_ref.changeable){
			println(
				(this.e_ref.eContainer as EClass).name + "." +
				this.e_ref.name + " is unchangeable."
			)
			throw new IllegalArgumentException(
				"Unchangeable EReference encountered. Model is invalid."
			)	
		}

		//get import String for this Reference
		this.fq_import_name = this.create_import_name_for_ereference_or_eclass(this.e_ref)
		
		//set a default value
		if(e_ref.defaultValue !== null){
			this.default_value = this.e_ref.defaultValue.toString
		}

		else if(this.is_a_tuple()){
			this.default_value = "new " +
				SmartCollectionFactory.get_elist_type_name(this.get_needed_elist_type_enum) +
				'''<«this.get_object_field_type_name»>()'''
		}
		else if(e_ref.defaultValue === null) this.default_value = "null"
		else this.default_value = e_ref.defaultValue.toString()

		//add package dependencies
		this.meta_model_package_dependencies.add(this.e_ref.EReferenceType.EPackage)
	}

	/**########################Methods########################*/

	/**
	 * @inheritDoc
	 */
	override generate_init_code_for_package_class(EcoreGenmodelParser gen_model) {
		if(this.type_init_commands_are_generated === true) return;
		this.type_init_commands_are_generated = true
		ReferenceInspector.emf_model = gen_model
		var body = new ArrayList<String>()
		body.addAll(this.generate_needed_generic_types_for_init_code())
		
		/* initEReference(
			EReference r, EClassifier type, EReference otherEnd,
			String name, String defaultValue, int lowerBound, 
			int upperBound, Class<?> containerClass, boolean isTransient, 
			boolean isVolatile, boolean isChangeable, 
			boolean isContainment, boolean isResolveProxies, boolean isUnsettable,
			boolean isUnique, boolean isDerived, boolean isOrdered)*/
		
		var entry = new StringBuilder("initEReference(")
		//the EClass in which this reference which is inspected is contained

		var the_containing_eclass = this.e_ref.eContainer as EClass
		var eclass_name = the_containing_eclass.name
		entry.append("get" + eclass_name + "_")
		entry.append(this.get_unfiltered_name.substring(0,1).toUpperCase)
		entry.append(this.get_unfiltered_name.substring(1))
		entry.append("(), ")
		
		//add EClassifier
		if(!this.this_feautures_datatype_is_composed_of_multiple_generic_sub_types &&
		   this.e_ref.EGenericType.EClassifier !== null &&
		   this.e_ref.EGenericType.EClassifier.EPackage.nsURI.equals(EcorePackage.eNS_URI)
		){
			//all ecore-xmi specified attribute-types have an ECLassifier of null
			//it is an normal element without generics, but contained in the ecorePackage
			entry.append("ecorePackage.get")
			entry.append(this.e_ref.EGenericType.EClassifier.name)
			entry.append("(), ")
		} else if(this.this_feautures_datatype_is_composed_of_multiple_generic_sub_types) {
			//it is a generic thus only the top-level generic needs to be added
			entry.append(this.generic_feauturetype_classifier_var_name + ", ")
		} else if(
			ReferenceInspector.emf_model.get_epackage_and_contained_classes_map.keySet.contains(
				this.e_ref.EReferenceType.EPackage
			)
		){
			//the attribute-type is specified in a generated package
			var String package_name =
				(this.e_ref.EReferenceType.EPackage.equals(the_containing_eclass.EPackage)) ?
					"this" : "the" + this.e_ref.EReferenceType.EPackage.name.substring(0, 1).toUpperCase() 
					+ this.e_ref.EReferenceType.EPackage.name.substring(1) + "Package"

			entry.append('''«package_name».get«this.e_ref.EReferenceType.name»(), ''')
		} else if(this.e_ref.EGenericType.ETypeParameter !== null &&
				  the_containing_eclass.ETypeParameters.contains(
				  	this.e_ref.EGenericType.ETypeParameter
				  )
		){
			//can only be of a generic ETypeParameter of the EClass
			entry.append(the_containing_eclass.name.substring(0,1).toLowerCase())
			entry.append(the_containing_eclass.name.substring(1))
			entry.append("EClass_")
			entry.append(this.e_ref.EGenericType.ETypeParameter.name)
			entry.append(", ")
		} else {
			throw new RuntimeException("Encountered unknown EReference-Data-Type")
		}
		
		//add EReference otherEnd
		if(this.e_ref.EOpposite === null){
			entry.append("null, ")
		} else {
			var class_of_opposite_reference = this.e_ref.EOpposite.eContainer as EClass
			var package_of_opposite_reference = class_of_opposite_reference.EPackage
			if((this.e_ref.eContainer as EClass).EPackage.equals(package_of_opposite_reference))
				entry.append("this.get")
			else {
				entry.append("the")
				entry.append(package_of_opposite_reference.name.substring(0,1).toUpperCase)
				entry.append(package_of_opposite_reference.name.substring(1))
				entry.append("Package.get")
				this.meta_model_package_dependencies.add(package_of_opposite_reference)
			}
			entry.append(class_of_opposite_reference.name.substring(0, 1).toUpperCase())
			entry.append(class_of_opposite_reference.name.substring(1))
			entry.append("_")
			entry.append(this.e_ref.EOpposite.name.substring(0, 1).toUpperCase)
			entry.append(this.e_ref.EOpposite.name.substring(1))
			entry.append("(), ")
		}
		
		//add String name,
		entry.append('''"«this.get_name()»", ''')
		
		//add String defaultValue
		entry.append(
			(this.e_ref.defaultValueLiteral === null) ?
			"null" : '''"«this.e_ref.defaultValueLiteral»"'''
			)
		entry.append(", ")
		
		//add int lowerBound,
		entry.append(this.e_ref.lowerBound)
		entry.append(", ")
		
		//add int upperBound
		entry.append(this.e_ref.upperBound)
		entry.append(", ")
		
		//add Class<?> containerClass,
		entry.append((this.e_ref.eContainer as EClass).name)
		entry.append(".class, ")

		//boolean isTransient,
		entry.append((this.e_ref.isTransient) ? "IS_TRANSIENT" : "!IS_TRANSIENT")
		entry.append(", ")
		//boolean isVolatile,
		entry.append((this.e_ref.isVolatile) ? "IS_VOLATILE" : "!IS_VOLATILE")
		entry.append(", ")
		//boolean isChangeable,
		entry.append((this.e_ref.isChangeable) ? "IS_CHANGEABLE" : "!IS_CHANGEABLE")
		entry.append(", ") 
		//boolean isContainment,
		//TODO verify if isContainment() really gives the boolean for the isContainment parameter
		//as EMF uses the IS_COMPOSITE flag
		entry.append((this.e_ref.isContainment) ? "IS_COMPOSITE" : "!IS_COMPOSITE")
		entry.append(", ")
		//boolean isResolveProxies,
		entry.append((this.e_ref.isResolveProxies) ? "IS_RESOLVE_PROXIES" : "!IS_RESOLVE_PROXIES")
		entry.append(", ")
		//boolean isUnsettable,
		entry.append((this.e_ref.isUnsettable) ? "IS_UNSETTABLE" : "!IS_UNSETTABLE")
		entry.append(", ")
		//boolean isUnique,
		entry.append((this.e_ref.isUnique) ? "IS_UNIQUE" : "!IS_UNIQUE")
		entry.append(", ")
		///boolean isDerived,
		entry.append((this.e_ref.isDerived) ? "IS_DERIVED" : "!IS_DERIVED")
		entry.append(", ")
		//boolean isOrdered
		entry.append((this.e_ref.isOrdered) ? "IS_ORDERED" : "!IS_ORDERED")
		entry.append(");")

		body.add(entry.toString)
		this.type_init_commands = body
	}

	/**
	 * @inheritDoc
	 */
	override get_inspected_object_type() {
		return InspectedObjectType.EREFERENCE
	}

	/**
	 * @inheritDoc
	 */
	override get_getter_method_declaration_for_the_package_classes() {
		if(this.getter_method_declaration_for_the_package_classes_is_generated)
			return "EReference " + this.getter_method_declaration_for_the_package_classes
		var entry = this.get_getter_method_declaration_for_the_package_classes__stump_only()
		return "EReference " + entry.toString()
	}

	/**
	 * @inheritDoc
	 */
	override get_getter_method_declaration_for_the_package_classes__stump_only(){
		if(this.getter_method_declaration_for_the_package_classes_is_generated)
			return this.getter_method_declaration_for_the_package_classes
		var entry = new StringBuilder("get")
		entry.append((this.e_ref.eContainer as EClass).name)
		entry.append("_")
		entry.append(this.get_name_with_first_letter_capitalized())
		entry.append("()")
		this.getter_method_declaration_for_the_package_classes = entry.toString
		this.getter_method_declaration_for_the_package_classes_is_generated = true
		return entry.toString()
	}

	/**
	 * @inheritDoc
	 */
	override get_literals_entry_for_package_classes() {
		var entry = new StringBuilder("EReference ")
		entry.append(this.get_emf_package_literals_interface_var_name())
		entry.append(" = eINSTANCE.")
		entry.append(this.get_getter_method_declaration_for_the_package_classes__stump_only())
		entry.append(";")
		return entry.toString()
	}

	/**
	 * returns true if the inspected EReference is in an containment-relationship
	 * @return boolean
	 * @author Adrian Zwenger
	 */
	def boolean is_contained(){
		return this.e_ref.isContainment()
	}

	/**
	 * if a default value was specified and the EReference is in an containment-relationship,
	 * and a tuple a fitting Collection is created and set up.
	 * @param ereference_instance String the EReference instance
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_default_value_if_contained_reference(String ereference_instance){
		if(!this.is_contained || !this.is_a_tuple) return this.get_default_value
		this.default_value =
			"new " +
			SmartCollectionFactory.get_elist_type_name(this.get_needed_elist_type_enum) +
			'''<«this.get_object_field_type_name»>(this, «ereference_instance»)'''
		return this.default_value
	}
}
