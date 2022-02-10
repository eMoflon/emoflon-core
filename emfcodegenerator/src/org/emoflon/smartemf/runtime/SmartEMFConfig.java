package org.emoflon.smartemf.runtime;

public class SmartEMFConfig {

	private static volatile boolean simpleStringRepresentations = false;

	public static void setSimpleStringRepresentations(boolean enabled) {
		simpleStringRepresentations = enabled;
	}

	public static boolean simpleStringRepresentations() {
		return simpleStringRepresentations;
	}

}
