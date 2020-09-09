package org.fross.dirsize;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

	/**
	 * sortByKeyAscending(): Return a HashMap with case insensitive key sorting
	 * 
	 * @param hm
	 * @return
	 */
	public static HashMap<String, Long> sortByKeyAscendingCI(HashMap<String, Long> hm) {
		// Create a list from elements of HashMap
		List<Map.Entry<String, Long>> list = new LinkedList<Map.Entry<String, Long>>(hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {
			public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
				return (o1.getKey().toLowerCase()).compareTo(o2.getKey().toLowerCase());
			}
		});

		// put data from sorted list to hash map
		HashMap<String, Long> temp = new LinkedHashMap<String, Long>();
		for (Map.Entry<String, Long> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}

	/**
	 * sortByValueAscending(): Return a Map sorted by values
	 * 
	 * @param hm
	 * @return
	 */
	public static HashMap<String, Long> sortByValueAscending(HashMap<String, Long> hm) {
		// Create a list from elements of HashMap
		List<Map.Entry<String, Long>> list = new LinkedList<Map.Entry<String, Long>>(hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {
			public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// put data from sorted list to hashmap
		HashMap<String, Long> temp = new LinkedHashMap<String, Long>();
		for (Map.Entry<String, Long> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}

	/**
	 * sortByValueDescending(): Return a Map sorted by values
	 * 
	 * @param hm
	 * @return
	 */
	public static HashMap<String, Long> sortByValueDescending(HashMap<String, Long> hm) {
		// Create a list from elements of HashMap
		List<Map.Entry<String, Long>> list = new LinkedList<Map.Entry<String, Long>>(hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {
			public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		}.reversed());

		// put data from sorted list to hashmap
		HashMap<String, Long> temp = new LinkedHashMap<String, Long>();
		for (Map.Entry<String, Long> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}

	/**
	 * main(): This is for testing as it's not intended to be run standalone
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		HashMap<String, Long> hm = new HashMap<String, Long>();

		// Add test data into the HashMap
		hm.put("Math", 98L);
		hm.put("Data Structure", 85L);
		hm.put("Database", 91L);
		hm.put("Java", 95L);
		hm.put("Operating System", 79L);
		hm.put("Networking", 80L);

		// Display Ascending Sort
		System.out.println("Ascending:");
		Map<String, Long> hm1 = sortByValueAscending(hm);
		for (Map.Entry<String, Long> en : hm1.entrySet()) {
			System.out.println("Key = " + en.getKey() + ", Value = " + en.getValue());
		}

		// Display Descending Sort
		System.out.println("\n\nDescending:");
		Map<String, Long> hm2 = sortByValueDescending(hm);
		for (Map.Entry<String, Long> en : hm2.entrySet()) {
			System.out.println("Key = " + en.getKey() + ", Value = " + en.getValue());
		}
	}
}
