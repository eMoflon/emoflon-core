package emfcodegenerator.inspectors.util

import emfcodegenerator.EcoreGenmodelParser
import emfcodegenerator.inspectors.InspectedObjectType
import java.util.ArrayList
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.emf.ecore.impl.EClassImpl
import org.eclipse.emf.ecore.impl.EClassifierImpl
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EAttribute
import emfcodegenerator.util.collections.SmartCollectionFactory

class AttributeInspector extends AbstractObjectFieldInspector{
	
	/**########################Attributes########################*/
	
	/**
	 * the EAttribute which shall be inspected
	 */
	var EAttribute e_attr

	var String getter_method_declaration_for_the_package_classes
	var boolean getter_method_declaration_for_the_package_classes_is_generated = false

	/**########################Constructor########################*/
	new(EStructuralFeature e_feature, EcoreGenmodelParser gen_model){
		super(e_feature, gen_model)
		init(e_feature)
	}

	new(EStructuralFeature e_feature, String super_package_name){
		super(e_feature, super_package_name)
		init(e_feature)
	}
	
	def private void init(EStructuralFeature e_feature){
		if(!(e_feature instanceof EAttribute))
			throw new IllegalArgumentException("Expected EAttribute, got " + e_feature.class.name)
		this.e_attr = e_feature as EAttribute
		
		//get import String for this Attribute
		var fq_module_name = e_attr.EAttributeType.instanceTypeName
		//if the Attribute is a custom EMF data type, instanceClass will be null
		if(e_attr.EAttributeType.instanceClass === null && !fq_module_name.nullOrEmpty)
			this.fq_import_name = fq_module_name
		if(!e_attr.EGenericType.ERawType.instanceClass.isPrimitive && !fq_module_name.nullOrEmpty)
			this.fq_import_name = fq_module_name
		this.fq_import_name = null
		this.add_import_as_String(this.fq_import_name)
		
		//set the default value for this EAttribute
		if(this.is_a_tuple()){
			this.default_value =  "new " +
				SmartCollectionFactory.get_elist_type_name(this.get_needed_elist_type_enum) +
				'''<«this.get_object_field_type_name»>()'''
		}
		else if(e_attr.defaultValue !== null){
			if(e_attr.defaultValue instanceof String){
				this.default_value =  '''"«e_attr.defaultValue»"'''.toString
			} else if((this.e_attr.EType as EClassifierImpl).instanceClassName.contains("float"))
				this.default_value =  '''«e_attr.defaultValue»F'''.toString
			this.default_value =  e_attr.defaultValue.toString
		}
		else if(e_attr.defaultValue === null){
			//unchangeable EAttributes do not get setters. If no setter is provided an
			//attribute can only be set via its getter if it is contained in an collection.
			//Thus if it isn't a tuple and not changeable or no default value was provided,
			//then the model is faulty.
			if(!this.is_changeable){
				println(
					(this.e_attr.eContainer as EClass).name + "." +
					this.e_attr.name +
					" is unchangeable and a either a literal or a non" +
					" String-object not contained in a Collection." + System.lineSeparator +
					"Please increase the UpperBound  in the meta-model or set a default value."
				)
				throw new IllegalArgumentException(
					"Unchangeable, non Collection EAttribute encountered. Model is invalid."
				)
			} else this.default_value = "null"
		}
		else this.default_value = e_attr.defaultValue.toString()
	}

	/**########################Methods########################*/

	override generate_init_code_for_package_class(EcoreGenmodelParser gen_model){
		if(this.type_init_commands_are_generated === true) return;
		this.type_init_commands_are_generated = true
		AttributeInspector.emf_model = gen_model

		var body = new ArrayList<String>()
		body.addAll(this.generate_needed_generic_types_for_init_code())

		//create the Attribute init command
		/*initEAttribute(
		 * EAttribute a, EClassifier type, String name, String defaultValue, 
		 * int lowerBound, int upperBound, Class<?> containerClass,
		 * boolean isTransient, boolean isVolatile, boolean isChangeable,
		 * boolean isUnsettable, boolean isID, boolean isUnique, boolean isDerived, boolean isOrdered)
		 * */
		var entry = new StringBuilder("initEAttribute(")
		//the eclass in which this attribute which is inspected is contained
		var the_containing_eclass = this.e_attr.eContainer as EClassImpl
		var eclass_name = the_containing_eclass.name
		entry.append("get" + eclass_name + "_")

		entry.append(this.get_name_with_first_letter_capitalized)
		entry.append("(), ")
		
		//add EClassifier
		if(!this.this_feautures_datatype_is_composed_of_multiple_generic_sub_types &&
		   this.e_attr.EGenericType.EClassifier !== null &&
		   this.e_attr.EGenericType.EClassifier.EPackage.nsURI.equals(EcorePackage.eNS_URI)
		){
			//all ecore-xmi specified attribute-types have an ECLassifier of null
			//it is an normal element without generics, but contained in the ecorePackage
			entry.append("ecorePackage.get")
			entry.append(this.e_attr.EGenericType.EClassifier.name)
			entry.append("(), ")
		} else if(this.this_feautures_datatype_is_composed_of_multiple_generic_sub_types) {
			//it is a generic thus only the top-level generic needs to be added
			entry.append(this.generic_feauturetype_classifier_var_name)
			entry.append(", ")
		} else if(AttributeInspector.emf_model.get_epackage_and_contained_classes_map.keySet
				  	  .contains(this.e_attr.EAttributeType.EPackage)
		){
			//the attribute-type is specified in a generated package
			var String package_name = 
				(this.e_attr.EAttributeType.EPackage.equals(
					the_containing_eclass.EPackage
				)) ? "this" : "the" + this.e_attr.EAttributeType.EPackage.name + "Package"
			entry.append(package_name)
			entry.append(".get")
			entry.append(this.e_attr.EAttributeType.name)
			entry.append("(), ")
		} else if(this.e_attr.EGenericType.ETypeParameter !== null &&
				  the_containing_eclass.ETypeParameters.contains(this.e_attr.EGenericType.ETypeParameter)
		){
			//can only be of a generic ETypeParameter of the EClass
			entry.append(the_containing_eclass.name.substring(0,1).toLowerCase())
			entry.append(the_containing_eclass.name.substring(1))
			entry.append("EClass_")
			entry.append(this.e_attr.EGenericType.ETypeParameter.name)
			entry.append(", ")
		} else {
			throw new RuntimeException("Encountered unknown EAttribute-Data-Type")
		}
		//add name
		entry.append('''"«this.get_name»", '''.toString)
		//String defaultValue
		entry.append((this.e_attr.defaultValueLiteral === null) ?
			"null" : '''"«this.e_attr.defaultValueLiteral»"'''.toString)
		entry.append(", ")
		//int lowerBound,
		entry.append(this.e_attr.lowerBound)
		entry.append(", ")
		//int upperBound,
		entry.append(this.e_attr.upperBound)
		entry.append(", ")
		//Class<?> containerClass,
		entry.append(the_containing_eclass.name)
		entry.append(".class, ")
		//boolean isTransient,
		entry.append((this.e_attr.transient) ? "IS_TRANSIENT" : "!IS_TRANSIENT")
		entry.append(", ")
		//boolean isVolatile,
		entry.append((this.e_attr.isVolatile) ? "IS_VOLATILE" : "!IS_VOLATILE")
		entry.append(", ") 
		//boolean isChangeable,
		entry.append((this.e_attr.isChangeable) ? "IS_CHANGEABLE" : "!IS_CHANGEABLE")
		entry.append(", ")
		//boolean isUnsettable,
		entry.append((this.e_attr.isUnsettable) ? "IS_UNSETTABLE" : "!IS_UNSETTABLE")
		entry.append(", ")
		//boolean isID,
		entry.append((this.e_attr.isID) ? "IS_ID" : "!IS_ID")
		entry.append(", ")
		//boolean isUnique,
		entry.append((this.e_attr.isUnique) ? "IS_UNIQUE" : "!IS_UNIQUE")
		entry.append(", ")
		//boolean isDerived,
		entry.append((this.e_attr.isDerived) ? "IS_DERIVED" : "!IS_DERIVED")
		entry.append(", ")
		//boolean isOrdered)
		entry.append((this.e_attr.isOrdered) ? "IS_ORDERED" : "!IS_ORDERED")
		entry.append(");")
		body.add(entry.toString)
		this.type_init_commands = body
	}

	override get_inspected_object_type() {
		return InspectedObjectType.EATTRIBUTE
	}

	def boolean is_a_literal(){
		return (this.e_attr.EType as EClassifierImpl).instanceClass.isPrimitive
	}

	override get_getter_method_declaration_for_the_package_classes() {
		if(this.getter_method_declaration_for_the_package_classes_is_generated)
			return "EAttribute " + this.getter_method_declaration_for_the_package_classes
		var entry = this.get_getter_method_declaration_for_the_package_classes__stump_only()
		return "EAttribute " + entry.toString()
	}
	
	override get_getter_method_declaration_for_the_package_classes__stump_only(){
		if(this.getter_method_declaration_for_the_package_classes_is_generated)
			return this.getter_method_declaration_for_the_package_classes
		var entry = new StringBuilder("get")
		entry.append((this.e_attr.eContainer as EClass).name)
		entry.append("_")
		entry.append(this.get_name_with_first_letter_capitalized())
		entry.append("()")
		this.getter_method_declaration_for_the_package_classes = entry.toString
		this.getter_method_declaration_for_the_package_classes_is_generated = true
		return entry.toString()
	}
	
	override get_literals_entry_for_package_classes() {
		var entry = new StringBuilder("EAttribute ")
		entry.append(emf_to_uppercase((this.e_attr.eContainer as EClass).name))
		entry.append("_")
		entry.append(emf_to_uppercase(this.get_name))
		entry.append(" = eINSTANCE.")
		entry.append(this.get_getter_method_declaration_for_the_package_classes__stump_only())
		entry.append(";")
		return entry.toString()
	}
	
}
