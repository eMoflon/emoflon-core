package org.emoflon.smartemf.runtime.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class ReplacingIterator<S> implements Iterator<S> {

	final S[] copiedElements;
	int iteratorIndex;

	@SuppressWarnings("unchecked")
	ReplacingIterator(SmartCollection<S, ? extends Collection<S>> collection) {
		this.copiedElements = (S[]) collection.elements.toArray();
		this.iteratorIndex = 0;
	}

	@Override
	public boolean hasNext() {
		return iteratorIndex < copiedElements.length;
	}

	@Override
	public S next() {
		if (iteratorIndex >= copiedElements.length)
			throw new NoSuchElementException("There is no next element in this collection!");

		return copiedElements[iteratorIndex++];
	}

	public abstract void replace(S element);

}
