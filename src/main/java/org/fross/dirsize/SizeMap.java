package org.fross.dirsize;

import java.io.File;
import java.util.HashMap;

public class SizeMap {

	/**
	 * queryMax(): Returns the largest size found in the provided hashmap
	 * 
	 * @param inputMap
	 * @param members
	 * @return
	 */
	public static long queryMax(HashMap<String, Long> inputMap, File[] members) {
		Long sizeMapMax = 0L;

		for (int i = 0; i < members.length; i++) {
			// Ensure we only look at directories
			if (members[i].isDirectory()) {
				String key = members[i].getName();

				if (inputMap.get(key) > sizeMapMax)
					sizeMapMax = inputMap.get(key);
			}
		}

		return (sizeMapMax);
	}

	/**
	 * queryMin(): Returns the smallest size found in the provided hashmap
	 * 
	 * @param inputMap
	 * @param members
	 * @return
	 */
	public static long queryMin(HashMap<String, Long> inputMap, File[] members) {
		long sizeMapMin = 0x7fffffffffffffffL;  // Largest value a long can have

		for (int i = 0; i < members.length; i++) {
			// Ensure we only look at directories
			if (members[i].isDirectory()) {
				String key = members[i].getName();

				if (inputMap.get(key) < sizeMapMin)
					sizeMapMin = inputMap.get(key);
			}
		}

		return (sizeMapMin);
	}
}
