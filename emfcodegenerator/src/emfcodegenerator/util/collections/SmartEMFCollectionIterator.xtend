package emfcodegenerator.util.collections

import java.util.Iterator

class SmartEMFCollectionIterator<E, B extends MinimalSObjectContainerCollection<E>> implements Iterator<E> {

	var Iterator<E> base_itr
	var B the_collection
	var E current_object = null

	new(Iterator<E> base_itr, B collection){
		this.base_itr = base_itr
		this.the_collection = collection
	}

	override hasNext() {
		this.base_itr.hasNext
	}
	
	override next() {
		this.current_object = this.base_itr.next()
		return current_object
	}

	override remove(){
		this.base_itr.remove()
		if(this.the_collection.isContainmentObject)
			this.the_collection.remove_containment_to_passed_object(this.current_object)
	}
}
