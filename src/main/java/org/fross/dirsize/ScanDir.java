/***********************************************************************
 * DirSize
 * 
 * Simple command line tool to recursively scan a directory and report
 * on the sizes and file counts contained within it.
 * 
 * See LICENSE file for permitted use
 * 
 ***********************************************************************/
package org.fross.dirsize;

import java.io.File;

import org.fross.library.Output;
import org.fusesource.jansi.Ansi;

/**
 * This class contains methods for recursively scanning and reporting the number
 * of files and sizes of a sent subdirectory.
 *
 * It is sent a File Object of the directory to scan and returns a Long array
 * Element [0] = Total Size Element [1] = Total Files
 *
 * @author michael.d.fross
 */
public class ScanDir {

	public long[] ScanDirectory(File DirToScan) {
		// Accumulating totals. Element [0]=Total Size. Element [1]=Total Files.
		long[] LocalTotals = { 0, 0 };

		// Holds the results of a recursive call
		long[] SubTotals;

		// List of files and directories of the provided dir
		File[] DirContents;

		try {
			DirContents = DirToScan.listFiles();

			// Loop through directories and files, counting size and numbers
			for (int i = 0; i < DirContents.length; i++) {
				if (DirContents[i].isDirectory() == true) {
					// Subdirectory Found. Scan it!
					SubTotals = new ScanDir().ScanDirectory(DirContents[i]);
					LocalTotals[0] += SubTotals[0];
					LocalTotals[1] += SubTotals[1];
				} else {
					// Add local files to Sizes and File Counts
					LocalTotals[0] += DirContents[i].length();
					LocalTotals[1]++;
				}
			}
		} catch (NullPointerException Ex) {
			Output.printColorln(Ansi.Color.RED, "ERROR: " + DirToScan.getName());

		} catch (Exception Ex) {
			Output.printColorln(Ansi.Color.RED, "ERROR Scanning " + DirToScan.toString() + "\n" + Ex.getMessage());
		}

		// Return back to the calling function an array with [0]Size totals and [1]File
		// totals
		return (LocalTotals);
	}
}
