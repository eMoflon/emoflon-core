package org.moflon.smartemf

import java.util.HashMap
import java.util.LinkedList
import javax.naming.OperationNotSupportedException
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EGenericType
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EOperation
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.ETypeParameter
import org.eclipse.emf.ecore.EcorePackage
import org.moflon.smartemf.inspectors.util.PackageInspector

/**
 * This helper class can be used to traverse EMF-GenericType-hierarchies.
 * This is needed to properly generate the generics for the meta-model, as they need to be
 * declared and created in proper order.<br>
 * make sure, that all classes which use this Processor access the package dependencies and pass
 * them for dependency resolution.
 */
class EGenericTypeProcessor extends EMFCodeGenerationClass {

	/**
	 * Maps a EGenericType to its designated variable name to be used in class created by
	 * {@link emfcodegenerator.creators.util.EMFPackageSourceCreator EMFPackageSourceCreator}.
	 */
	public var HashMap<EGenericType,String> generic_bound_to_var_name_map =
		new HashMap<EGenericType,String>()

	/**
	 * Maps all {@link EPackage EPackages}, which are needed as an dependency,
	 * to its {@link emfcodegenerator.inspectors.util.PackageInspector inspector}.
	 */
	protected var package_dependency_map =
		new HashMap<EPackage,PackageInspector>()

	/**
	 * The variable names are made of an String prefix and an unique
	 * {@link #generic_bound_var_index int}.
	 */
	var protected String generic_bound_var_name
	
	/**
	 * suffix for generated variable names. Take a look at
	 * {@link #generic_bound_var_name generic_bound_var_name}.
	 */
	var static int generic_bound_var_index = 0

	/**
	 * the package for which the EMF-code shall be created for. Thus importing objects from the
	 * same package can be prevented.<br>
	 * This field is also needed for
	 * {@link emfcodegenerator.creators.util.EMFPackageSourceCreator EMFPackageSourceCreator}, which
	 * which inherits from this class.
	 */
	var protected PackageInspector e_pak

	/**
	 * Maps all {@link EPackage EPackages}
	 * to its {@link emfcodegenerator.inspectors.util.PackageInspector inspector}.
	 */
	protected var HashMap<EPackage,PackageInspector> packages
	
	/**
	 * a map used for generic type traversal for ObjectFields.
	 * the key is an element and the value is its parent
	 */
	var element_to_parent_map = new HashMap<EObject,EObject>()
	
	/**########################Constructor########################*/
	
	/**
	 * Constructs a new EGenericTypeProcessor.<br>
	 * @param gen_model {@link emfcodegenerator.EcoreGenmodelParser EcoreGenmodelParser}
	 * instance to access parsed information and
	 * previously created {@link emfcodegenerator.inspectors.Inspector inspectors}.<br>
	 * @param generic_var_name String which shall be used as the prefix for variable names. Take a
	 * look at {@link #generic_bound_var_name generic_bound_var_name}.<br>
	 * @param package_inspector PackageInspector instance for which the code shall be generated.
	 * Stored in {@link #e_pak e_pak}.<br>
	 * @author Adrian Zwenger
	 */
	new(EcoreGenmodelParser gen_model, String generic_var_name, PackageInspector package_inspector){
		super(gen_model)
		this.generic_bound_var_name = generic_var_name
		this.packages = EGenericTypeProcessor.emf_model.get_packages_to_package_inspector_map()
		this.e_pak = package_inspector
	}

	/**########################Methods########################*/

	/**
	 * recursively builds a LinkedList where the topmost element is the first iteration element.<br>
	 * The tree is searched top down and sub elements are stored in reverse order, thus retaining 
	 * the original tree-order of traversed structure when iterating backwards through list.<br>
	 * Tree is scanned top-down and the resulting list should be read bottom-up.
	 * This method can be used to traverse trees for the
	 * {@link ETypeParameter ETypeParameter's} 
	 * {@link EGenericType EBounds} of an
	 * {@link EClass EClass} (take a look
	 * {@link emfcodegenerator.creators.util.EMFPackageSourceCreator
	 #generate_e_package_init_package_contents__create_type_params___for_eclasses here}),
	 * {@link EDataType EDataTypes}
	 * (take a look
	 * {@link emfcodegenerator.creators.util.EMFPackageSourceCreator
	 #generate_e_package_init_package_contents__create_type_params___for_edatatypes here})
	 * or {@link EOperation EOperations} (take a look
	 * {@link emfcodegenerator.inspectors.util.EOperationInspector
	 #generate_init_code_for_package_class here}).<br>
	 * To be used in cojuncture with
	 * {@link #create_egeneric_type_bound_set_up_command(EGenericType, HashMap, ETypeParameter)
	 create_egeneric_type_bound_set_up_command()}.
	 * @author Adrian Zwenger
	 * @param type EGenericType the top-most element of tree to traverse
	 * @param bounds_list LinkedList<EGenericType> list storing the current recursion state. Pass
	 * empty list on first call
	 * @return LinkedList<EGenericType>
 	 * @author Adrian Zwenger
 	 */
	def LinkedList<EGenericType> traverse_generic_bounds(
		EGenericType type, LinkedList<EGenericType> bounds_list
	){
		var new_bounds_list = bounds_list
		if(type.ETypeParameter !== null){
			var iterator = (new LinkedList<EGenericType>(type.ETypeParameter.EBounds)).descendingIterator
			while(iterator.hasNext){
				var bound = iterator.next
				this.traverse_generic_bounds(bound, new_bounds_list)
			}
		}
		if(type.ETypeArguments !== null){
			var iterator = (new LinkedList<EGenericType>(type.ETypeArguments)).descendingIterator
			while(iterator.hasNext){
				var param = iterator.next
				this.traverse_generic_bounds(param, new_bounds_list)
			}
		}
		if(type.EUpperBound !== null) this.traverse_generic_bounds(type.EUpperBound, new_bounds_list)
		if(type.ELowerBound !== null) this.traverse_generic_bounds(type.ELowerBound, new_bounds_list)
		this.generic_bound_to_var_name_map.put(
			type,
			this.generic_bound_var_name + EGenericTypeProcessor.generic_bound_var_index++
		)
		new_bounds_list.addFirst(type)
		return new_bounds_list
	}

	/**
	 * Creates the command which gets the EClassifier for an EGenericType.<br>
	 * If the type was not previously registered by traversing its hierarchy,
	 * it will default to the command which will create the "?" EGenericType.
	 * @param generic_type EGenericType
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_eclassifier_getter_command_for_egenerictype(EGenericType generic_type){
		//create the command which gets the defining EClassifier for the type/generic
		if(generic_type.EClassifier !== null){
			var String package_var_name
			//type might be an EClass specified in genmodel/ecore
			if(EGenericTypeProcessor.emf_model.eclass_is_registered(generic_type.EClassifier)){
				//it is a user specified class
				var package_dependency = generic_type.EClassifier.EPackage
				package_var_name = "this"
				//add as an dependency if it is not the current package for
				//which all this code is generated for
				//println(this.packages)
				if(!this.packages.get(package_dependency).equals(this.e_pak)){
				//if(!EGenericTypeProcessor.emf_model.get_epackage_and_contained_classes_map.)
					this.package_dependency_map.put(package_dependency, packages.get(package_dependency))
					package_var_name = "the" 
					package_var_name += package_dependency.name.substring(0,1).toUpperCase
					package_var_name += package_dependency.name.substring(1)
					package_var_name += "Package"
				}
			} else if(EcorePackage.eINSTANCE.equals(generic_type.EClassifier.EPackage)){
				//it is an EMF specified class
				package_var_name = "ecorePackage"
			} else if(
				EGenericTypeProcessor.emf_model.get_packages_to_package_inspector_map
											   .containsKey(generic_type.EClassifier.EPackage)
			){
				//The package of the generic_type is user-specified, however it was not a class
				var package_inspector = 
					EGenericTypeProcessor.emf_model.get_packages_to_package_inspector_map
												   .get(generic_type.EClassifier.EPackage)
				//make sure the package was used before
				if(!package_inspector.is_initialized){
					package_inspector.initialize()
					this.packages = EGenericTypeProcessor.emf_model.update_package_inspector(
						generic_type.EClassifier.EPackage, package_inspector
					)
				}
				if(
					package_inspector.get_all_edata_types_in_package.contains(generic_type.EClassifier) ||
					package_inspector.get_all_eenums_in_package.contains(generic_type.EClassifier)
				){
					//the generic-type is actually an EDataType
					//add dependency
					package_var_name = "this"
					if(!package_inspector.equals(this.e_pak)){
						this.package_dependency_map.put(generic_type.EClassifier.EPackage, package_inspector)
						package_var_name = "the" 
						package_var_name += package_inspector.get_name.substring(0,1).toUpperCase
						package_var_name += package_inspector.get_name.substring(1)
						package_var_name += "Package"
					}
				} else throw new UnsupportedOperationException()
			} else {
				//println(generic_type.EClassifier.EPackage)
				//the class is unknown
				throw new RuntimeException(
				"Failed to locate ECLassifier for Type Parameter generation for following class: " +
				System.lineSeparator +
				generic_type.EClassifier.toString
				)
			}
			//create the classifier getter
			return '''«package_var_name».get«generic_type.EClassifier.name»()'''.toString
		}
		return ""
	}
	
	/**
	 * creates the command used to set up bounds for ETypeParameters.<br>
	 * If the passed EGenericType is:
	 * <ol>
	 *	<li> a ELowerBound to its parent, it will be added as such:<br> parent.setELowerBound(element)</li>
	 *	<li> a EUpperBound to its parent, it will be added as such:<br> parent.setEUpperBound(element)</li>
	 *	<li> a ETypeParameter to its parent, it will be added as such:<br> parent.setETypeParameter(element)</li>
	 *	<li> a ETypeArgument to its parent, it will be added as such:<br> parent.getETypeArguments().add(element)</li>
	 *	<li> a EBound to an ETypeParameter, it will be added as such:<br> parent.getEBounds().add(element)</li>
	 * </ol>
	 * To do this the method relies on the this.generic_bound_to_var_name_map data-field and the
	 * passed parameters to discern the elements parent, relation and their variable name.<br>
	 * @param generic_type EGenericType: The element for which the command shall be generated
	 * @param etype_to_var_name_map HashMap<ETypeParameter,String>: Map containing ETypeParameter and their variable name
	 * @param type_param ETypeParameter: The Parameter to which the EGenericType recursively belongs to
	 * @return String the generated command
	 * @author Adrian Zwenger
	 */
	def String create_egeneric_type_bound_set_up_command(
		EGenericType generic_type,
		HashMap<ETypeParameter,String> etype_to_var_name_map,
		ETypeParameter type_param
	){
		var entry = new StringBuilder()
		if(
			this.generic_bound_to_var_name_map.keySet.contains(generic_type.eContainer)
		){
			//get the container in which the generic type lives
			var generic_type_to_add_it_to = generic_type.eContainer as EGenericType
			
			var var_name = this.generic_bound_to_var_name_map.get(generic_type)
			
			//set the newly generated generic as what it is supposed to be
			if(
				generic_type_to_add_it_to.ELowerBound !== null &&
				generic_type_to_add_it_to.ELowerBound.equals(generic_type)
			){
				//the current generic type is a lower bound and needs to be processed as such
				entry = new StringBuilder(
					this.generic_bound_to_var_name_map.get(generic_type_to_add_it_to)
				)
				entry.append(".setELowerBound(")
				entry.append(var_name)
				entry.append(");")
				return entry.toString
			} else if(
				generic_type_to_add_it_to.EUpperBound !== null &&
				generic_type_to_add_it_to.EUpperBound.equals(generic_type)
			){
				//the current generic type is a upper bound and needs to be processed as such
				entry = new StringBuilder(
					this.generic_bound_to_var_name_map.get(generic_type_to_add_it_to)
				)
				entry.append(".setEUpperBound(")
				entry.append(var_name)
				entry.append(");")
				return entry.toString
			} else if (
				generic_type_to_add_it_to.ETypeParameter !== null &&
				generic_type_to_add_it_to.ETypeParameter.equals(generic_type)
			){
				//the current generic type is a type param and needs to be processed as such
				entry = new StringBuilder(
					this.generic_bound_to_var_name_map.get(generic_type_to_add_it_to)
				)
				entry.append(".setETypeParameter(")
				entry.append(var_name)
				entry.append(");")
				return entry.toString
			} else if(generic_type_to_add_it_to.ETypeArguments.contains(generic_type)){
				//the current generic type is a type argument and needs to be processed as such
				entry = new StringBuilder(
					this.generic_bound_to_var_name_map.get(generic_type_to_add_it_to)
				)
				entry.append(".getETypeArguments().add(")
				entry.append(var_name)
				entry.append(");")
				return entry.toString
			} else {
				throw new OperationNotSupportedException(
				"Unable to process bounds for: " + type_param.toString
				)
			}
		} else if(etype_to_var_name_map.keySet.contains(generic_type.eContainer)){
			//the current generic is a bound and needs to be added as such
			entry = new StringBuilder(
				etype_to_var_name_map.get(generic_type.eContainer)
				)
			entry.append(".getEBounds().add(")
			entry.append(this.generic_bound_to_var_name_map.get(generic_type))
			entry.append(");")
			return entry.toString
		}
		var error_message = new StringBuilder("Unable to process following object:")
		error_message.append(System.lineSeparator)
		error_message.append(generic_type.toString)
		error_message.append(System.lineSeparator)
		error_message.append("to the type parameter:")
		error_message.append(type_param.toString)
		throw new OperationNotSupportedException(error_message.toString)
	}

	/**
	 * Similar to {@link #traverse_generic_bounds traverse_generic_bounds}.<br>
	 * However, EAttributes and EReferences need to be traversed a bit differently. Use this method
	 * for them.
	 * @param type EGenericType representing the current node from which to explore the hierarchy
	 * @param bounds_list LinkedList<EGenericType> the current recursion/exploration state
	 * @param e_class EClass to which the EAttribute/EReference belongs to
	 * @return LinkedList<EGenericType>
	 * @author Adrian Zwenger
	 */
	def LinkedList<EGenericType> traverse_generic_bounds_for_object_fields(
		EGenericType type,
		LinkedList<EGenericType> bounds_list,
		EClass e_class
	){
		var etype_param_set = EGenericTypeProcessor.emf_model.get_generic_type_to_var_name_map_for_eclass(e_class).keySet
		var new_bounds_list = bounds_list
		var boolean process_the_generic_type = true
		if(type.ETypeParameter !== null && etype_param_set.contains(type.ETypeParameter)) {
			//it is an ETypeParameter belonging to the class. Init code does not need to be generated
			//new_bounds_list.addFirst(type)
			process_the_generic_type = false
			//return new_bounds_list	
		}
		if(type.ETypeParameter !== null && process_the_generic_type){
			//the passed EObject is an ETypeParameter
			var iterator = (new LinkedList<EGenericType>(type.ETypeParameter.EBounds)).descendingIterator
			while(iterator.hasNext){
				//bounds need to be added and thus traversed
				var bound = iterator.next
				this.traverse_generic_bounds_for_object_fields(bound, new_bounds_list, e_class)
				
				if(bound.ETypeParameter !== null && etype_param_set.contains(bound.ETypeArguments)){
					//bound is an ETypeParameter belonging to the EClass. The parent element must be
					//noted
					this.element_to_parent_map.put(bound, type)
				} else this.element_to_parent_map.put(bound, type)
			}
		}
		if(type.ETypeArguments !== null && process_the_generic_type){
			var iterator = (new LinkedList<EGenericType>(type.ETypeArguments)).iterator //.descendingIterator
			while(iterator.hasNext){
				var param = iterator.next
				this.traverse_generic_bounds_for_object_fields(param, new_bounds_list, e_class)

				if(param.ETypeParameter !== null && etype_param_set.contains(param.ETypeParameter)){
					//Parameter is an ETypeParameter belonging to the EClass. The parent element must be
					//noted
					this.element_to_parent_map.put(param, type)
				} else this.element_to_parent_map.put(param, type)
			}
		}
		if(type.EUpperBound !== null && process_the_generic_type) {
			var bound = type.EUpperBound
			this.traverse_generic_bounds_for_object_fields(bound, new_bounds_list, e_class)
			
			if(bound.ETypeParameter !== null && etype_param_set.contains(bound.ETypeParameter)){
				//bound is an ETypeParameter belonging to the EClass. The parent element must be
				//noted
				this.element_to_parent_map.put(bound, type)
			} else this.element_to_parent_map.put(bound, type)
		}
		if(type.ELowerBound !== null && process_the_generic_type) {
			var bound = type.ELowerBound			
			this.traverse_generic_bounds_for_object_fields(bound, new_bounds_list, e_class)
			
			if(bound.ETypeParameter !== null && etype_param_set.contains(bound.ETypeParameter)){
				//bound is an ETypeParameter belonging to the EClass. The parent element must be
				//noted
				this.element_to_parent_map.put(bound, type)
			} else this.element_to_parent_map.put(bound, type)
		}

		if(process_the_generic_type)
			this.generic_bound_to_var_name_map.put(
				type,
				this.generic_bound_var_name + EGenericTypeProcessor.generic_bound_var_index++
			)

		new_bounds_list.addFirst(type)
		return new_bounds_list
	}
	
	/**
	 * Tweaked version of {@link #traverse_generic_bounds traverse_generic_bounds} for
	 * EOperations.<br>
	 * Note, that during ETypeParameter generation for EOperation's everything needs
	 * to be traversed. Even the ETypeParameters of the containing EClass.
	 * However, this is not needed for the EParameters ETypeParameters.<br>
	 * Set the ignore_eclass_parameters to true if they are to be skipped.
	 * @param type EGenericType representing the current node from which to explore the hierarchy
	 * @param bounds_list LinkedList<EGenericType> the current recursion/exploration state
	 * @param e_op EOperation for which this method is called
	 * @param ignore_eclass_parameters boolean. Pass TRUE when calling for the EParameters of an 
	 * EOperation.
	 * @return LinkedList<EGenericType>
	 * @author Adrian Zwenger
	 */
	def LinkedList<EGenericType> traverse_generic_bounds_for_eoperations(
		EGenericType type,
		LinkedList<EGenericType> bounds_list,
		EOperation e_op,
		boolean ignore_eclass_parameters
	){
		var etype_param_set = e_op.ETypeParameters
		var new_bounds_list = bounds_list
		var boolean process_the_generic_type = true
		if(type.ETypeParameter !== null && etype_param_set.contains(type.ETypeParameter)) {
			//it is an ETypeParameter belonging to the e_op. Init code does not need to be generated
			process_the_generic_type = false
		}
		if(ignore_eclass_parameters && type.ETypeParameter !== null && (e_op.eContainer as EClass).ETypeParameters.contains(type.ETypeParameter)) {
			process_the_generic_type = false
		}
		if(type.ETypeParameter !== null && process_the_generic_type){
			//the passed EObject is an ETypeParameter
			var iterator = (new LinkedList<EGenericType>(type.ETypeParameter.EBounds)).descendingIterator
			while(iterator.hasNext){
				//bounds need to be added and thus traversed
				var bound = iterator.next
				this.traverse_generic_bounds_for_eoperations(bound, new_bounds_list, e_op, ignore_eclass_parameters)
				
				if(bound.ETypeParameter !== null && etype_param_set.contains(bound.ETypeArguments)){
					//bound is an ETypeParameter belonging to the EClass. The parent element must be
					//noted
					this.element_to_parent_map.put(bound, type)
				} else this.element_to_parent_map.put(bound, type)
			}
		}
		if(type.ETypeArguments !== null && process_the_generic_type){
			var iterator = (new LinkedList<EGenericType>(type.ETypeArguments)).iterator //.descendingIterator
			while(iterator.hasNext){
				var param = iterator.next
				this.traverse_generic_bounds_for_eoperations(param, new_bounds_list, e_op, ignore_eclass_parameters)

				if(param.ETypeParameter !== null && etype_param_set.contains(param.ETypeParameter)){
					//Parameter is an ETypeParameter belonging to the EClass. The parent element must be
					//noted
					this.element_to_parent_map.put(param, type)
				} else this.element_to_parent_map.put(param, type)
			}
		}
		if(type.EUpperBound !== null && process_the_generic_type) {
			var bound = type.EUpperBound
			this.traverse_generic_bounds_for_eoperations(bound, new_bounds_list, e_op, ignore_eclass_parameters)
			
			if(bound.ETypeParameter !== null && etype_param_set.contains(bound.ETypeParameter)){
				//bound is an ETypeParameter belonging to the EClass. The parent element must be
				//noted
				this.element_to_parent_map.put(bound, type)
			} else this.element_to_parent_map.put(bound, type)
		}
		if(type.ELowerBound !== null && process_the_generic_type) {
			var bound = type.ELowerBound			
			this.traverse_generic_bounds_for_eoperations(bound, new_bounds_list, e_op, ignore_eclass_parameters)
			
			if(bound.ETypeParameter !== null && etype_param_set.contains(bound.ETypeParameter)){
				//bound is an ETypeParameter belonging to the EClass. The parent element must be
				//noted
				this.element_to_parent_map.put(bound, type)
			} else this.element_to_parent_map.put(bound, type)
		}

		if(process_the_generic_type)
			this.generic_bound_to_var_name_map.put(
				type,
				this.get_next_generated_var_name()
			)

		new_bounds_list.addFirst(type)
		return new_bounds_list
	}

	/**
	 * creates the command used to set up bounds for ETypeParameters belonging to EOperations,
	 * EAttributes and EReferences.<br>
	 * If the passed EGenericType is:
	 * <ol>
	 *	<li> a ELowerBound to its parent, it will be added as such:<br>
	 	parent.setELowerBound(element)</li>
	 *	<li> a EUpperBound to its parent, it will be added as such:<br>
	 	parent.setEUpperBound(element)</li>
	 *	<li> a ETypeParameter to its parent, it will be added as such:<br>
	 	parent.setETypeParameter(element)</li>
	 *	<li> a ETypeArgument to its parent, it will be added as such:<br>
	 	parent.getETypeArguments().add(element)</li>
	 *	<li> a EBound to an ETypeParameter, it will be added as such:<br>
	 	parent.getEBounds().add(element)</li>
	 * </ol>
	 * To do this the method relies on the this.generic_bound_to_var_name_map data-field and the
	 * passed parameters to discern the elements parent, relation and their variable name.<br>
	 * @param generic_type EGenericType: The element for which the command shall be generated
	 * @param etype_to_var_name_map HashMap<ETypeParameter,String>:
	 * Map containing ETypeParameter and their variable name
	 * @return String the generated command
	 * @author Adrian Zwenger
	 */
	def String create_egeneric_type_bound_set_up_command_for_object_fields(
		EGenericType e_obj,
		HashMap<ETypeParameter,String> etype_to_var_name_map
	){
		var generic_type = e_obj as EGenericType
		var entry = new StringBuilder()
		//etype_to_var_name_map.contains((e_obj as EGenericType).ETypeParameter)
		if(
			this.generic_bound_to_var_name_map.keySet.contains(generic_type.eContainer)
		){
			//get the container in which the generic type lives
			var generic_type_to_add_it_to = generic_type.eContainer as EGenericType
			
			var var_name = this.generic_bound_to_var_name_map.get(generic_type)
			if(
				generic_type.ETypeParameter !== null &&
				etype_to_var_name_map.containsKey(generic_type.ETypeParameter)
			){
				var_name = "createEGenericType(" +
						   etype_to_var_name_map.get(generic_type.ETypeParameter) + ")"
			}

			//set the newly generated generic as what it is supposed to be
			if(
				generic_type_to_add_it_to.ELowerBound !== null &&
				generic_type_to_add_it_to.ELowerBound.equals(generic_type)
			){
				//the current generic type is a lower bound and needs to be processed as such
				entry = new StringBuilder(
					this.generic_bound_to_var_name_map.get(generic_type_to_add_it_to)
				)
				entry.append(".setELowerBound(")
				entry.append(var_name)
				entry.append(");")
				return entry.toString
			} else if(
				generic_type_to_add_it_to.EUpperBound !== null &&
				generic_type_to_add_it_to.EUpperBound.equals(generic_type)
			){
				//the current generic type is a upper bound and needs to be processed as such
				entry = new StringBuilder(
					this.generic_bound_to_var_name_map.get(generic_type_to_add_it_to)
				)
				entry.append(".setEUpperBound(")
				entry.append(var_name)
				entry.append(");")
				return entry.toString
			} else if (
				generic_type_to_add_it_to.ETypeParameter !== null &&
				generic_type_to_add_it_to.ETypeParameter.equals(generic_type)
			){
				//the current generic type is a type param and needs to be processed as such
				entry = new StringBuilder(
					this.generic_bound_to_var_name_map.get(generic_type_to_add_it_to)
				)
				entry.append(".setETypeParameter(")
				entry.append(var_name)
				entry.append(");")
				return entry.toString
			} else if(generic_type_to_add_it_to.ETypeArguments.contains(generic_type)){
				//the current generic type is a type argument and needs to be processed as such
				entry = new StringBuilder(
					this.generic_bound_to_var_name_map.get(generic_type_to_add_it_to)
				)
				entry.append(".getETypeArguments().add(")
				entry.append(var_name)
				entry.append(");")
				return entry.toString
			} else {
				throw new OperationNotSupportedException(
				"Unable to process: " + e_obj.toString
				)
			}
		} else if(etype_to_var_name_map.keySet.contains(generic_type.eContainer)){
			//the current generic is a bound and needs to be added as such
			entry = new StringBuilder(
				etype_to_var_name_map.get(generic_type.eContainer)
				)
			entry.append(".getEBounds().add(")
			entry.append(this.generic_bound_to_var_name_map.get(generic_type))
			entry.append(");")
			return entry.toString
		}

		var error_message = new StringBuilder("Unable to process following object:")
		error_message.append(System.lineSeparator)
		error_message.append(e_obj.toString)
		error_message.append(System.lineSeparator)
		error_message.append("for bound set up")
		throw new OperationNotSupportedException(error_message.toString)
	}

	/**
	 * Helper method, which generates the next variable name to be used.<br>
	 * After call {@link #generic_bound_var_index generic_bound_var_index} will be incremented by
	 * one.
	 * @return String
	 * @author Adrian Zwenger
	 */
	private def String get_next_generated_var_name(){
		return this.generic_bound_var_name + EGenericTypeProcessor.generic_bound_var_index++
	}
	
	/**
	 * Returns a map with all EPackages and their inspectors which were needed as an dependency.<br>
	 * Returns the following data-field: {@link #package_dependency_map package_dependency_map}. 
	 * @return HashMap<EPackage, PackageInspector>
	 * @author Adrian Zwenger
	 */
	def HashMap<EPackage,PackageInspector> get_package_dependencies(){
		return this.package_dependency_map
	}
}
