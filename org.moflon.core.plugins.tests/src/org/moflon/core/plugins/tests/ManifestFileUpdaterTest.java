package org.moflon.core.plugins.tests;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.moflon.core.plugins.manifest.ManifestFileUpdater;

/**
 * Unit tests for {@link ManifestFileUpdater}
 */
public class ManifestFileUpdaterTest {

	@Test
	public void testCalculateMissingDependencies1() throws Exception {
		List<String> missingDependencies = ManifestFileUpdater.calculateMissingDependencies(Arrays.asList("org.A"),
				Arrays.asList("org.B"));
		Assert.assertEquals(Arrays.asList("org.B"), missingDependencies);
	}

	@Test
	public void testCalculateMissingDependencies2() throws Exception {
		List<String> missingDependencies = ManifestFileUpdater
				.calculateMissingDependencies(Arrays.asList("org.A", "org.B"), Arrays.asList("org.B"));
		Assert.assertEquals(Arrays.asList(), missingDependencies);
	}

	@Test
	public void testCalculateMissingDependencies3() throws Exception {
		List<String> missingDependencies = ManifestFileUpdater.calculateMissingDependencies(
				Arrays.asList("org.A", "org.B;bundle-version=\"3.7.0\""), Arrays.asList("org.B"));
		Assert.assertEquals(Arrays.asList(), missingDependencies);
	}

	@Test
	public void testCalculateMissingDependencies4__RespectVersion() throws Exception {
		List<String> missingDependencies = ManifestFileUpdater.calculateMissingDependencies(Arrays.asList("org.A"),
				Arrays.asList("org.B;bundle-version=\"3.7.0\""));
		Assert.assertEquals(Arrays.asList("org.B;bundle-version=\"3.7.0\""), missingDependencies);
	}

	@Test
	public void testCalculateMissingDependencies5__RespectIgnoreFlag() throws Exception {
		List<String> missingDependencies = ManifestFileUpdater.calculateMissingDependencies(Arrays.asList("org.A"),
				Arrays.asList("org.C;bundle-version=\"1.0\"", "org.B;bundle-version=\"3.7.0\";__ignore__"));
		Assert.assertEquals(Arrays.asList("org.C;bundle-version=\"1.0\""), missingDependencies);
	}

	@Test
	public void testCalculateMissingDependencies6__IgnoreNewDependencyWithSamePluginIdButDifferentVersion()
			throws Exception {
		List<String> missingDependencies = ManifestFileUpdater.calculateMissingDependencies(
				Arrays.asList("org.A", "org.B;bundle-version=\"3.7.0\""),
				Arrays.asList("org.C;bundle-version=\"1.0\"", "org.B;bundle-version=\"3.7.0\""));
		Assert.assertEquals(Arrays.asList("org.C;bundle-version=\"1.0\""), missingDependencies);
	}
}
