package org.moflon.core.ui.autosetup.tests;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
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

   private File getSampleFile1() throws URISyntaxException
   {
      final URL url1 = getClass().getClassLoader().getResource("DemoWorkspace_Part1.psf");
   	final File psfFile1 = new File(url1.toURI());
      return psfFile1;
   }

   private File getSampleFile2() throws URISyntaxException
   {
      final URL url2 = getClass().getClassLoader().getResource("DemoWorkspace_Part2.psf");
		final File psfFile2 = new File(url2.toURI());
      return psfFile2;
   }
}
