package org.moflon.core.build.tests;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProjectDescription;
import org.gervarro.eclipse.workspace.util.ProjectUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moflon.core.build.nature.MoflonBuilderUtils;

/**
 * Unit tests for {@link MoflonBuilderUtils}
 * 
 * @author Roland Kluge - Initial implementation
 *
 */
public class MoflonBuilderUtilsTest {
	private IProjectDescription projectDescription;

	private ICommand[] buildSpecification;

	@SuppressWarnings("restriction")
	@Before
	public void setUp() {
		projectDescription = new org.eclipse.core.internal.resources.ProjectDescription();
		buildSpecification = new ICommand[] {};
	}

	@Test
	public void testTwoBuildersOK() throws Exception {
		buildSpecification = MoflonBuilderUtils.appendIfMissing(buildSpecification, "A", projectDescription);
		buildSpecification = MoflonBuilderUtils.appendIfMissing(buildSpecification, "B", projectDescription);

		MoflonBuilderUtils.ensureBuilderOrder(buildSpecification, Arrays.asList("A", "B"));

		assertBuilderOrder(buildSpecification, Arrays.asList("A", "B"));
	}

	@Test
	public void testTwoBuildersChangeOrder() throws Exception {
		buildSpecification = MoflonBuilderUtils.appendIfMissing(buildSpecification, "A", projectDescription);
		buildSpecification = MoflonBuilderUtils.appendIfMissing(buildSpecification, "B", projectDescription);

		MoflonBuilderUtils.ensureBuilderOrder(buildSpecification, Arrays.asList("B", "A"));

		assertBuilderOrder(buildSpecification, Arrays.asList("B", "A"));
	}

	@Test
	public void testMissingBuilder() throws Exception {
		buildSpecification = MoflonBuilderUtils.appendIfMissing(buildSpecification, "A", projectDescription);
		buildSpecification = MoflonBuilderUtils.appendIfMissing(buildSpecification, "B", projectDescription);

		MoflonBuilderUtils.ensureBuilderOrder(buildSpecification, Arrays.asList("C", "A"));

		assertBuilderOrder(buildSpecification, Arrays.asList("A", "B"));
	}

	@Test
	public void testThreeBuilders() throws Exception {
		buildSpecification = MoflonBuilderUtils.appendIfMissing(buildSpecification, "A", projectDescription);
		buildSpecification = MoflonBuilderUtils.appendIfMissing(buildSpecification, "B", projectDescription);
		buildSpecification = MoflonBuilderUtils.appendIfMissing(buildSpecification, "C", projectDescription);

		MoflonBuilderUtils.ensureBuilderOrder(buildSpecification, Arrays.asList("C", "A"));

		assertBuilderOrder(buildSpecification, Arrays.asList("C", "A", "B"));
	}

	/**
	 * Asserts that the given builder ID is present in the given build
	 * specification.
	 */
	public static void assertBuilderIsPresent(final ICommand[] buildSpecification, final String builderID) {
		Assert.assertTrue(ProjectUtil.indexOf(buildSpecification, builderID) >= 0);
	}

	/**
	 * Asserts that the given builder ID is missing from the given build
	 * specification.
	 */
	public static void assertBuilderIsMissing(final ICommand[] buildSpecification, final String builderID) {
		Assert.assertTrue(ProjectUtil.indexOf(buildSpecification, builderID) < 0);
	}

	/**
	 * Asserts that the given build specification exactly reflects the given list of
	 * builder IDs.
	 */
	public static void assertBuilderOrder(final ICommand[] buildSpecification, final List<String> builderIDs) {
		for (int i = 0; i < buildSpecification.length; ++i) {
			Assert.assertEquals(i, ProjectUtil.indexOf(buildSpecification, builderIDs.get(i)));
		}
	}
}
