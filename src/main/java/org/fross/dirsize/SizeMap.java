/******************************************************************************
 * DirSize
 * 
 * DirSize is a simple command line based directory size reporting tool
 * 
 *  Copyright (c) 2011-2024 Michael Fross
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *           
 ******************************************************************************/
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
		Long sizeMapMax = Long.MIN_VALUE;

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
		long sizeMapMin = Long.MAX_VALUE;  // Largest value a long can have

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
	 * sortByKeyDescending(): Return a HashMap with case insensitive key sorting
	 * 
	 * @param hm
	 * @return
	 */
	public static HashMap<String, Long> sortByKeyDescendingCI(HashMap<String, Long> hm) {
		// Create a list from elements of HashMap
		List<Map.Entry<String, Long>> list = new LinkedList<Map.Entry<String, Long>>(hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {
			public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
				return (o1.getKey().toLowerCase()).compareTo(o2.getKey().toLowerCase());
			}
		}.reversed());

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
