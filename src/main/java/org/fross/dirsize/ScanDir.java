/******************************************************************************
 * DirSize
 * 
 * DirSize is a simple command line based directory size reporting tool
 * 
 *  Copyright (c) 2019-2023 Michael Fross
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

import org.fross.library.Output;
import org.fusesource.jansi.Ansi;

/**
 * This class contains methods for recursively scanning and reporting the number of files and sizes
 * of a sent subdirectory.
 *
 * It is sent a File Object of the directory to scan and returns a Long array Element [0] = Total
 * Size Element [1] = Total Files [2] = Error Count
 *
 * @author michael.d.fross
 */
public class ScanDir {
	public long[] ScanDirectory(File DirToScan) {
		// Accumulating totals. Element [0]=Total Size. Element [1]=Total Files.
		long[] LocalTotals = { 0L, 0L };

		// Holds the results of a recursive call
		long[] SubTotals;

		// List of files and directories of the provided directory
		File[] DirContents;

		try {
			DirContents = DirToScan.listFiles();

			// Loop through directories and files, counting size and numbers
			for (int i = 0; i < DirContents.length; i++) {
				if (DirContents[i].isDirectory() == true) {
					// Subdirectory Found - Scan
					SubTotals = new ScanDir().ScanDirectory(DirContents[i]);
					LocalTotals[0] += SubTotals[0];
					LocalTotals[1] += SubTotals[1];
				} else {
					// Add local files to Sizes and File Counts
					LocalTotals[0] += DirContents[i].length();
					LocalTotals[1]++;
				}
			}
		} catch (NullPointerException ex) {
			Output.debugPrint("SCAN ERROR: '" + DirToScan.getAbsolutePath() + "'");
			Main.errorList.put(DirToScan.getAbsolutePath(), ex.getMessage());

		} catch (Exception Ex) {
			Output.printColorln(Ansi.Color.RED, "ERROR Scanning " + DirToScan.toString() + "\n" + Ex.getMessage());
		}

		// Return back to the calling function an array with Size & File totals
		return (LocalTotals);

	}

}