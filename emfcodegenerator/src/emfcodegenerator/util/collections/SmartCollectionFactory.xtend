package emfcodegenerator.util.collections

import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EObject

class SmartCollectionFactory {

	def static <E> DefaultEList<E> createDefaultEList(){
		return new DefaultEList<E>()
	}

	def static <E> HashESet<E> createHashESet(){
		return new HashESet<E>()
	}

	def static <E> LinkedESet<E> createLinkedESet(){
		return new LinkedESet<E>()
	}
	
	def static <E> LinkedEList<E> createLinkedEList(){
		return new LinkedEList<E>()
	}
	
	def static <E> HashEMultiset<E> createHashEMultiset() {
		return new HashEMultiset<E>()
	}
	
	def static <E> EList<E> automatically_determine_and_create_collection(EReference e_ref){
		var is_ordered = e_ref.ordered
		var is_unique = e_ref.unique

		switch(get_needed_elist_type(is_ordered, is_unique)){
			case EListTypeEnum.NONE: throw new UnsupportedOperationException()
			case EListTypeEnum.SET: return createHashESet()
			case EListTypeEnum.LINKED_LIST: return createLinkedEList()
			case EListTypeEnum.LINKED_SET: return createLinkedESet()
			case EListTypeEnum.MULTISET: return createHashEMultiset()
			default: return createDefaultEList()
		}
	}
	
	def static <E> EList<E> createCollection(EObject eContainer, EReference containment_feature){
		var EList<E> e = automatically_determine_and_create_collection(containment_feature)
		(e as MinimalSObjectContainerCollection<E>).setContainment(eContainer, containment_feature)
		return e
	}
	
	def static String get_elist_import_String(EListTypeEnum type){
		switch(type){
			case EListTypeEnum.NONE: return null
			case EListTypeEnum.SET: return "emfcodegenerator.util.collections.HashESet"
			case EListTypeEnum.LINKED_LIST: return "emfcodegenerator.util.collections.LinkedEList"
			case EListTypeEnum.LINKED_SET: return "emfcodegenerator.util.collections.LinkedESet"
			case EListTypeEnum.MULTISET: return "emfcodegenerator.util.collections.HashEMultiset"
			default: return "emfcodegenerator.util.collections.DefaultEList"
		}
	}
	
	def static EListTypeEnum get_needed_elist_type(boolean is_ordered, boolean is_unique){
		switch (is_ordered -> is_unique) {
			case false -> false: return EListTypeEnum.MULTISET
			case false -> true:  return EListTypeEnum.SET
			case true  -> false: return EListTypeEnum.LINKED_LIST
			case true  -> true:  return EListTypeEnum.LINKED_SET
		}
	}
	
	def static String get_elist_type_name(EListTypeEnum e_list_type){
		switch(e_list_type){
			case EListTypeEnum.NONE: return "NO_E_LIST"
			case EListTypeEnum.SET: return "HashESet"
			case EListTypeEnum.LINKED_LIST: return "LinkedEList"
			case EListTypeEnum.LINKED_SET: return "LinkedESet"
			case EListTypeEnum.MULTISET: return "HashEMultiset"
			default: return "DefaultEList"
		}
	}
}