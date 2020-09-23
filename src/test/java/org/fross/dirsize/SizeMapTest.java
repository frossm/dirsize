package org.fross.dirsize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SizeMapTest {
	HashMap<String, Long> hm = new HashMap<String, Long>();
	static File[] f = new File[3];
	static String[] s = { "a.test", "b.test", "c.test" };

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		// Create Test Files
		try {
			new File(s[0]);
			f[0] = File.createTempFile("dirsize-small-", ".test");
			new File(s[1]);
			f[1] = File.createTempFile("dirsize-medium-", ".test");
			new File(s[2]);
			f[2] = File.createTempFile("dirsize-large-", ".test");

		} catch (IOException ex) {
			fail("Could not create temp files");
		}
	}

	@Test
	void test() {
		hm.put(f[0].getName(), 100L);
		hm.put(f[1].getName(), 200L);
		hm.put(f[2].getName(), 300L);

		long result = SizeMap.queryMax(hm, f);

		assertEquals(300L, result, "Didn't return the Max Value");
	}

}
