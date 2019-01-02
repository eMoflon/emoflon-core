package org.moflon.core.preferences.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.moflon.core.preferences.EMoflonPreferencesStorage;

/**
 * Unit tests for {@link ManifestFileUpdater}
 */
public class EMoflonPreferencesStorageTest {

	private static final String KEY = "k";
	private EMoflonPreferencesStorage storage;

	@Before
	public void setup() {
		storage = new EMoflonPreferencesStorage();
	}

	@Test
	public void test_get_missingEntry() throws Exception {
		assertNull(storage.get("missing"));
	}

	@Test
	public void test_getString() throws Exception {
		storage.put(KEY, "v");
		assertEquals("v", storage.get(KEY));
		assertEquals("v", storage.getString(KEY));
	}

	@Test(expected = RuntimeException.class)
	public void test_getString_wrongType() throws Exception {
		storage.put(KEY, 1);
		storage.getString(KEY);
	}

	@Test
	public void test_getInt_missingEntry() throws Exception {
		assertNull(storage.getInt("missing"));
	}

	@Test
	public void test_getInt() throws Exception {
		storage.put(KEY, 1);
		assertEquals(1, storage.get(KEY));
		assertEquals(new Integer(1), storage.getInt(KEY));
	}

	@Test(expected = RuntimeException.class)
	public void test_getInt_wrongType() throws Exception {
		storage.put(KEY, "v");
		storage.getInt(KEY);
	}

	@Test
	public void test_getDouble_missingEntry() throws Exception {
		assertNull(storage.getDouble("missing"));
	}

	@Test
	public void test_getDouble() throws Exception {
		storage.put(KEY, 1.0);
		assertEquals(1.0, storage.get(KEY));
		assertEquals(new Double(1.0), storage.getDouble(KEY));
	}

	@Test(expected = RuntimeException.class)
	public void test_getDouble_wrongType() throws Exception {
		storage.put(KEY, "v");
		storage.getDouble(KEY);
	}

	@Test
	public void test_getBoolean_missingEntry() throws Exception {
		assertNull(storage.getBoolean("missing"));
	}

	@Test
	public void test_getBoolean() throws Exception {
		storage.put(KEY, true);
		assertEquals(true, storage.get(KEY));
		assertEquals(true, storage.getBoolean(KEY));
	}

	@Test(expected = RuntimeException.class)
	public void test_getBoolean_wrongType() throws Exception {
		storage.put(KEY, "v");
		storage.getBoolean(KEY);
	}
}
