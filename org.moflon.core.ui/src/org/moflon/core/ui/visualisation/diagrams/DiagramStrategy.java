/**
 * 
 */
package org.moflon.core.ui.visualisation.diagrams;

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * @author Johannes Brandt
 *
 */
@FunctionalInterface
public interface DiagramStrategy<T> extends UnaryOperator<T> {

	static <T> DiagramStrategy<T> identity() {
		return arg -> arg;
	}
	
	default DiagramStrategy<T> andThen(DiagramStrategy<T> after) {
		Objects.requireNonNull(after);
		return arg -> after.apply(this.apply(arg));
	}

	default DiagramStrategy<T> compose(DiagramStrategy<T> before) {
		Objects.requireNonNull(before);
		return arg -> this.apply(before.apply(arg));
	}
}
