package org.moflon.core.utilities.tests;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.moflon.core.utilities.MoflonUtil;

public class MoflonUtilTest {
	@Test
	public void testCorrectPathWithImportMappings() throws Exception {
		HashMap<String, String> importMappings = new HashMap<String, String>();
		importMappings.put("java.classifiers", "org.emftext.language.java.classifiers");
		importMappings.put("java.classifiers.foo", "longer.prefix");

		Assert.assertEquals("", MoflonUtil.transformPackageNameUsingImportMapping("", importMappings));
		Assert.assertEquals("notcontained",
				MoflonUtil.transformPackageNameUsingImportMapping("notcontained", importMappings));
		Assert.assertEquals("not.contained",
				MoflonUtil.transformPackageNameUsingImportMapping("not.contained", importMappings));
		Assert.assertEquals("java.partialmatch",
				MoflonUtil.transformPackageNameUsingImportMapping("java.partialmatch", importMappings));
		Assert.assertEquals("org.emftext.language.java.classifiers",
				MoflonUtil.transformPackageNameUsingImportMapping("java.classifiers", importMappings));

		Assert.assertEquals("org.emftext.language.java.classifiers.some-suffix",
				MoflonUtil.transformPackageNameUsingImportMapping("java.classifiers.some-suffix", importMappings));

		// We match the longest prefix
		Assert.assertEquals("longer.prefix",
				MoflonUtil.transformPackageNameUsingImportMapping("java.classifiers.foo", importMappings));
	}
}
