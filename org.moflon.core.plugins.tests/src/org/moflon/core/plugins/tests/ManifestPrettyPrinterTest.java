package org.moflon.core.plugins.tests;

import org.junit.Assert;
import org.junit.Test;
import org.moflon.core.plugins.manifest.ManifestPrettyPrinter;

public class ManifestPrettyPrinterTest {

	@Test
	public void test_WhenStringIsAlreadyFormatted_ThenDoNothing() throws Exception {
		ManifestPrettyPrinter printer = new ManifestPrettyPrinter();
		String content = "Manifest-Version: 1.0\r\n"//
				+ "Bundle-ManifestVersion: 2\r\n"//
				+ "Export-Package: antlr,\r\n"//
				+ " antlr.ASdebug,\r\n"//
				+ " antlr.actions.cpp,\r\n"//
				+ " antlr.actions.csharp,\r\n"//
				+ " antlr.actions.java,\r\n"//
				+ " antlr.actions.python\r\n";
		String formattedContent = printer.print(content);
		Assert.assertEquals(content, formattedContent);
	}

	@Test
	public void test_WhenTwoPackagesAreInOneLine_ThenSplitTheLine() throws Exception {
		ManifestPrettyPrinter printer = new ManifestPrettyPrinter();
		String content = "Manifest-Version: 1.0\r\n"//
				+ "Bundle-ManifestVersion: 2\r\n"//
				+ "Export-Package: antlr,antlr.ASdebug\r\n";
		String expected = "Manifest-Version: 1.0\r\n"//
				+ "Bundle-ManifestVersion: 2\r\n"//
				+ "Export-Package: antlr,\r\n"//
				+ " antlr.ASdebug\r\n";
		String formattedContent = printer.print(content);
		Assert.assertEquals(expected, formattedContent);
	}

	@Test
	public void test_WhenThreeEntriesAreScatteredAcrossTwoLines_ThenReformat_NoSplitting() throws Exception {
		ManifestPrettyPrinter printer = new ManifestPrettyPrinter();
		String content = "Manifest-Version: 1.0\r\n"//
				+ "Bundle-ManifestVersion: 2\r\n"//
				+ "Export-Package: antlr,antlr.ASdebug,\r\n"//
				+ " antlr.Foo";
		String expected = "Manifest-Version: 1.0\r\n"//
				+ "Bundle-ManifestVersion: 2\r\n"//
				+ "Export-Package: antlr,\r\n"//
				+ " antlr.ASdebug,\r\n"//
				+ " antlr.Foo\r\n";
		String formattedContent = printer.print(content);
		Assert.assertEquals(expected, formattedContent);
	}

	@Test
	public void test_WhenTwoEntriesAreScatteredAcrossTwoLines_ThenReformat_WithSplitEntry() throws Exception {
		ManifestPrettyPrinter printer = new ManifestPrettyPrinter();
		String content = "Manifest-Version: 1.0\r\n"//
				+ "Bundle-ManifestVersion: 2\r\n"//
				+ "Export-Package: antlr,antlr.ASd\r\n"//
				+ " ebug\r\n";
		String expected = "Manifest-Version: 1.0\r\n"//
				+ "Bundle-ManifestVersion: 2\r\n"//
				+ "Export-Package: antlr,\r\n"//
				+ " antlr.ASdebug\r\n";
		String formattedContent = printer.print(content);
		Assert.assertEquals(expected, formattedContent);
	}

	@Test
	public void test_WhenASingleDependencyIsLongerThan72Characters_ThenSplitDependency() throws Exception {
		ManifestPrettyPrinter printer = new ManifestPrettyPrinter();
		String content = "Manifest-Version: 1.0\r\n"//
				+ "Bundle-ManifestVersion: 2\r\n"//
				+ "Export-Package: aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab";//
		String expected = "Manifest-Version: 1.0\r\n"//
				+ "Bundle-ManifestVersion: 2\r\n"//
				+ "Export-Package: aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\r\n"//
				+ " b\r\n";//
		String formattedContent = printer.print(content);
		Assert.assertEquals(expected, formattedContent);
	}

	@Test
	public void test_WhenASingleExportedPackageIsLongerThan140Characters_ThenSplitTwiceDependency() throws Exception {
		ManifestPrettyPrinter printer = new ManifestPrettyPrinter();
		String content = "Manifest-Version: 1.0\r\n"//
				+ "Bundle-ManifestVersion: 2\r\n"//
				+ "Export-Package: aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"//
				+ "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"//
				+ "c";//
		String expected = "Manifest-Version: 1.0\r\n"//
				+ "Bundle-ManifestVersion: 2\r\n"//
				+ "Export-Package: aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\r\n"//
				+ " bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb\r\n"//
				+ " c\r\n";//
		String formattedContent = printer.print(content);
		Assert.assertEquals(expected, formattedContent);
	}

	@Test
	public void test_WhenDependenciesIsAlreadyFormatted_ThenDoNothing() throws Exception {
		ManifestPrettyPrinter printer = new ManifestPrettyPrinter();
		String content = "Manifest-Version: 1.0\r\n"//
				+ "Bundle-ManifestVersion: 2\r\n"//
				+ "Requie-Bundle: antlr,\r\n"//
				+ " antlr.ASdebug,\r\n"//
				+ " antlr.actions.cpp,\r\n"//
				+ " antlr.actions.csharp,\r\n"//
				+ " antlr.actions.java,\r\n"//
				+ " antlr.actions.python\r\n";
		String formattedContent = printer.print(content);
		Assert.assertEquals(content, formattedContent);
	}

	@Test
	public void testMixedDependencies() throws Exception {
		ManifestPrettyPrinter printer = new ManifestPrettyPrinter();
		String content = "Manifest-Version: 1.0\r\n"//
				+ "Bundle-ManifestVersion: 2\r\n"//
				+ "Require-Bundle: antlr,antlr.ASd\r\n"//
				+ " ebug,antlr.Foo," + " aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab\r\n";
		String expected = "Manifest-Version: 1.0\r\n"//
				+ "Bundle-ManifestVersion: 2\r\n"//
				+ "Require-Bundle: antlr,\r\n"//
				+ " antlr.ASdebug,\r\n"//
				+ " antlr.Foo,\r\n"//
				+ " aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\r\n"//
				+ " b\r\n";
		String formattedContent = printer.print(content);
		Assert.assertEquals(expected, formattedContent);
	}

	@Test
	public void test_WhenASingleDependencyIsLongerThan140Characters_ThenSplitTwiceDependency() throws Exception {
		ManifestPrettyPrinter printer = new ManifestPrettyPrinter();
		String content = "Manifest-Version: 1.0\r\n"//
				+ "Bundle-ManifestVersion: 2\r\n"//
				+ "Export-Package: antlr,aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"//
				+ "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"//
				+ "c";//
		String expected = "Manifest-Version: 1.0\r\n"//
				+ "Bundle-ManifestVersion: 2\r\n"//
				+ "Export-Package: antlr,\r\n"//
				+ " aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\r\n"//
				+ " bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb\r\n"//
				+ " c\r\n";//
		String formattedContent = printer.print(content);
		Assert.assertEquals(expected, formattedContent);
	}
}
