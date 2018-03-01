package org.moflon.core.ui.propertytester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;

/**
 * This tester evaluates whether the currently active perspective is the
 * expected one.
 * 
 * It replaces the formerly built-in method of querying the
 * _activeWindow.activePerspective_ property.
 * 
 * Original source:
 * http://jonathanjwright.wordpress.com/2014/07/15/eclipse-4-4-0-perspective-based-show-hide-of-tool-bar-items/
 *
 */
public class PerspectivePropertyTester extends PropertyTester {

	/**
	 * @param receiver
	 *            the currently active {@link MPerspective}
	 * @param property
	 *            the property to test, in this case 'elementId'
	 * @param args
	 *            additional arguments, in this case an empty array
	 * @param expectedValue
	 *            the expected value of {@link MPerspective#getElementId()}
	 */
	@Override
	public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {
		final MPerspective perspective = (MPerspective) receiver;
		return perspective.getElementId().equals(expectedValue);
	}

}
