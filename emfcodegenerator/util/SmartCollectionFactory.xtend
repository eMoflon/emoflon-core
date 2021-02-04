package emfcodegenerator.util

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
	
	def static <E> EList<E> automatically_determine_and_create_collection(EReference e_ref){
		var is_ordered = e_ref.ordered
		var is_unique = e_ref.unique

		if(!is_ordered && !is_unique){
			return createDefaultEList()
		} else if(!is_ordered && is_unique){
			return createHashESet()
		} else if(is_ordered && !is_unique){
			return createLinkedESet()
		} else if(is_ordered && is_unique){
			return createLinkedESet()
		}
		throw new UnsupportedOperationException()
	}
	
	def static <E> EList<E> createCollection(EObject eContainer, EReference containment_feature){
		var EList<E> e = automatically_determine_and_create_collection(containment_feature)
		(e as MinimalSObjectContainer).set_containment(eContainer, containment_feature)
		return e
	}
}