package org.moflon.core.utilities;

import java.io.IOException;
import java.util.List;

import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.diff.DefaultDiffEngine;
import org.eclipse.emf.compare.diff.DiffBuilder;
import org.eclipse.emf.compare.diff.FeatureFilter;
import org.eclipse.emf.compare.diff.IDiffEngine;
import org.eclipse.emf.compare.diff.IDiffProcessor;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Collection of useful methods for EMFCompare
 *
 * @author Anthony Anjorin
 */
public class EmfCompareUtil {

	/**
	 * Compares actual to expected and returns a list of differences, filtered
	 * according to the filter flags.
	 *
	 * @param actual
	 * @param expected
	 * @param ignoreReferenceOrder
	 *
	 * @return <b>Unmodifiable</b> list of filtered differences.
	 *
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static List<Diff> compareAndFilter(final EObject actual, final EObject expected,
			final boolean ignoreReferenceOrder) throws InterruptedException {
		IDiffProcessor diffProcessor = new DiffBuilder();
		IDiffEngine diffEngine = new DefaultDiffEngine(diffProcessor) {
			@Override
			protected FeatureFilter createFeatureFilter() {
				return new FeatureFilter() {
					@Override
					public boolean checkForOrderingChanges(final EStructuralFeature feature) {
						return !ignoreReferenceOrder;
					}
				};
			}
		};

		IComparisonScope scope = new DefaultComparisonScope(actual, expected, null);
		Comparison comparison = EMFCompare.builder().setDiffEngine(diffEngine).build().compare(scope);

		return comparison.getDifferences();
	}
}
