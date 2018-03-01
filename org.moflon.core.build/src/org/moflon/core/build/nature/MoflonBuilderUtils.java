package org.moflon.core.build.nature;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProjectDescription;
import org.gervarro.eclipse.workspace.util.ProjectUtil;
import org.moflon.core.utilities.UtilityClassNotInstantiableException;

/**
 * Utility class for manipulating project builders
 * 
 * @author Roland Kluge - Initial implementation
 */
public final class MoflonBuilderUtils {
	// Disabled constructor of utility class
	private MoflonBuilderUtils() {
		throw new UtilityClassNotInstantiableException();
	}

	/**
	 * Inserts a build specification with the given builderID into the given
	 * buildSpecification if the builder is not contained in the build specification
	 * yet.
	 * 
	 * @param buildSpecification
	 *            the build specification
	 * @param builderID
	 *            the ID of the new builder
	 * @param projectDescription
	 *            the {@link IProjectDescription} that shall be used to construct
	 *            the builder command
	 * @return the updated build specification
	 */
	public static ICommand[] appendIfMissing(final ICommand[] buildSpecification, final String builderID,
			final IProjectDescription projectDescription) {
		if (ProjectUtil.indexOf(buildSpecification, builderID) < 0) {
			final ICommand xtextBuilder = projectDescription.newCommand();
			xtextBuilder.setBuilderName(builderID);
			final ICommand[] newBuildSpecification = Arrays.copyOf(buildSpecification, buildSpecification.length + 1);
			newBuildSpecification[newBuildSpecification.length - 1] = xtextBuilder;
			return newBuildSpecification;
		} else {
			return buildSpecification;
		}
	}

	/**
	 * Returns a list of builder configurations that results from removing the
	 * builder with the given ID 'builderID' from the given list of IDs
	 * 'inputBuildSpecs'.
	 * 
	 * @param inputNatureIDs
	 * @param id
	 * @return the updated list of IDs
	 */
	public static ICommand[] removeBuilderID(final ICommand[] inputBuildSpecs, final String builderID) {
		final int builderPosition = ProjectUtil.indexOf(inputBuildSpecs, builderID);
		if (builderPosition >= 0) {
			final Object[] newBuildSpecificationTmp = ProjectUtil.remove(inputBuildSpecs, builderPosition);
			// Workaround for https://github.com/eMoflon/emoflon-tool/issues/177
			final ICommand[] newBuildSpecification = Arrays.copyOf(inputBuildSpecs, newBuildSpecificationTmp.length);
			System.arraycopy(newBuildSpecificationTmp, 0, newBuildSpecification, 0, newBuildSpecificationTmp.length);
			return newBuildSpecification;
		} else {
			return inputBuildSpecs;
		}
	}

	/**
	 * Reorders the given build specification to satisfy the given builder order.
	 * 
	 * The reordering is performed in-place. The reordering algorithm considers the
	 * builder IDs in buildOrder in the given sequence. For each builder that is out
	 * of order, the algorithm identifies the smallest index of all builders that
	 * appear later in the build order and moves the current builder to this
	 * position.
	 * 
	 * @param buildSpecification
	 *            the build specification to modify
	 * @param builderOrder
	 *            the desired builder order (as list of builder IDs)
	 */
	public static void ensureBuilderOrder(final ICommand[] buildSpecification, final List<String> builderOrder) {
		final List<String> buildersInBuildSpecification = builderOrder.stream()
				.filter(builderID -> ProjectUtil.indexOf(buildSpecification, builderID) >= 0)
				.collect(Collectors.toList());

		for (int i = 0; i < buildersInBuildSpecification.size(); ++i) {
			final String currentBuilderID = buildersInBuildSpecification.get(i);
			final int currentBuilderPosition = ProjectUtil.indexOf(buildSpecification, currentBuilderID);
			int newBuilderPosition = currentBuilderPosition;
			for (int j = i + 1; j < buildersInBuildSpecification.size(); ++j) {
				final String otherBuilderID = buildersInBuildSpecification.get(j);
				final int otherBuilderPosition = ProjectUtil.indexOf(buildSpecification, otherBuilderID);
				newBuilderPosition = Math.min(newBuilderPosition, otherBuilderPosition);
			}

			/*
			 * Found a builder that appears later in the builder order but has a lower
			 * position Therefore, we move the current builder just in front this other
			 * builder.
			 */
			if (newBuilderPosition != currentBuilderPosition) {
				final ICommand builder = buildSpecification[currentBuilderPosition];
				System.arraycopy(buildSpecification, newBuilderPosition, buildSpecification, newBuilderPosition + 1,
						currentBuilderPosition - newBuilderPosition);
				buildSpecification[newBuilderPosition] = builder;
			}
		}
	}
}
