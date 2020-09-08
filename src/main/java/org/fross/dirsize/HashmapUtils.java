/***********************************************************************
 * DirSize
 * 
 * Simple command line tool to recursively scan a directory and report
 * on the sizes and file counts contained within it.
 * 
 * See LICENSE file for permitted use
 * 
 * This code adapted from GeeksForGeeks
 * https://www.geeksforgeeks.org/sorting-a-hashmap-according-to-values/#:~:text=Solution%3A%20The%20idea%20is%20to,is%20sorted%20according%20to%20values.
 * 
 ***********************************************************************/

package org.fross.dirsize;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HashmapUtils {

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
