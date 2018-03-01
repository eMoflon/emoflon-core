package org.moflon.core.ui.autosetup.tests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.CoreException;
import org.junit.Assert;
import org.junit.Test;
import org.moflon.core.ui.autosetup.PsfFileUtils;

/**
 * Utility class for working with PSF files.
 */
public final class PsfFileUtilsTest {

	@Test(expected = IllegalArgumentException.class)
	public void testJoinPsfFiles_DisallowNull() throws Exception {
		PsfFileUtils.joinPsfFile(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testJoinPsfFiles_DisallowEmpty() throws Exception {
		PsfFileUtils.joinPsfFile(Arrays.asList());
	}

	@Test(expected = Exception.class)
	public void testJoinPsfFiles_NonExisting() throws Exception {
		PsfFileUtils.joinPsfFile(Arrays.asList(new File("foo/bar/bla.psf")));
	}

	@Test
	public void testJoinPsfFiles_SingleFile() throws IOException, CoreException, URISyntaxException {
		final File psfFile1 = getSampleFile1();

		Assert.assertTrue(psfFile1.exists());

		final String expectedContent = FileUtils.readFileToString(psfFile1);
		final String actualContent = PsfFileUtils.joinPsfFile(Arrays.asList(psfFile1));

		Assert.assertEquals(expectedContent, actualContent);
	}

	@Test
	public void testJoinPsfFiles_TwoFiles() throws IOException, CoreException, URISyntaxException {
		final File psfFile1 = getSampleFile1();
		final File psfFile2 = getSampleFile2();

		Assert.assertTrue(psfFile1.exists());
		Assert.assertTrue(psfFile2.exists());

		final String actualContent = PsfFileUtils.joinPsfFile(Arrays.asList(psfFile1, psfFile2));

		FileUtils.write(File.createTempFile("tmp.psf", ""), actualContent);

		Assert.assertTrue(actualContent
				.contains("1.0,https://github.com/eMoflon/emoflon-examples.git,master,demo/org.moflon.demo.testsuite"));
		Assert.assertTrue(actualContent
				.contains("1.0,https://github.com/eMoflon/emoflon-examples.git,master,demo/org.moflon.demo"));
	}

	private File getSampleFile1() throws IOException {
		return createTemporaryFile("DemoWorkspace_Part1.psf");
	}

	private File getSampleFile2() throws URISyntaxException, IOException {
		return createTemporaryFile("DemoWorkspace_Part2.psf");
	}

	private File createTemporaryFile(String classpathResource) throws IOException {
		final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(classpathResource);
		final File tmpFile = File.createTempFile(getClass().getName(), ".psf");
		FileUtils.copyInputStreamToFile(inputStream, tmpFile);
		return tmpFile;
	}

}
