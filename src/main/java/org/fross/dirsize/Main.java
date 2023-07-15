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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.fross.library.Debug;
import org.fross.library.Format;
import org.fross.library.GitHub;
import org.fross.library.Output;
import org.fross.library.SpinnerBouncyBall;
import org.fusesource.jansi.Ansi;

import gnu.getopt.Getopt;

/**
 * Main Class for DirSize
 * 
 * @author Michael
 *
 */
public class Main {
	// Class Constants
	private static final String PROPERTIES_FILE = "app.properties";
	private static final int DISPLAY_PERCENT_NAME = 30;
	private static final int DISPLAY_PERCENT_DIRSIZE = 15;
	private static final int DISPLAY_PERCENT_NUMFILES = 15;
	private static final int DISPLAY_PERCENT_VISUALMAP = 40;
	private static final String ROOT_DIR_NAME = "[RootDir]";
	private static final int MIN_TERMINAL_WIDTH = 60;
	private static final String MAP_FILLED_CHAR = "o";
	private static final String MAP_EMPTY_CHAR = "-";

	// Class Variables
	protected static String VERSION;
	protected static String COPYRIGHT;
	protected static HashMap<String, String> errorList = new HashMap<String, String>();

	/**
	 * Main(): Main program execution entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int optionEntry;
		String rootDir = "";
		File[] rootMembers = {};
		char sortBy = 's';	// Default is sortBy size. 'f' and 'd' are also allowed
		boolean errorDisplayFlag = true;
		boolean reverseSort = false;
		boolean exportFlag = false;
		int terminalWidth = 90;
		Export exportFile = new Export();

		// Define the HashMaps for the scanning results. The directory name will be the key.
		HashMap<String, Long> mapSize = new HashMap<String, Long>();
		HashMap<String, Long> mapFiles = new HashMap<String, Long>();
		HashMap<String, String> mapFullPath = new HashMap<String, String>();

		// Variables to hold the overall grand total directories, sizes, and file counts
		long grandTotalSubdirs = 1L; // It's not zero as it starts with [RootDir]
		long grandTotalSize = 0L;
		long grandTotalFiles = 0L;

		// Set the default terminalWidth by OS. jAnsi used to work in windows with:
		// org.fusesource.jansi.internal.WindowsSupport.getWindowsTerminalWidth()
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			terminalWidth = 90;
		} else if (System.getProperty("os.name").toLowerCase().contains("linux")) {
			terminalWidth = 90;
		} else {
			terminalWidth = 90;
		}

		// getWindowsTerminalWidth() doesn't seem to work within Eclipse
		// This is a quick fix or ensure you use the -c switch as an argument
		if (terminalWidth < 0) {
			Output.debugPrintln("Seems to be running within Eclipse. Setting columns to 100");
			terminalWidth = 100;
		}

		// Process application level properties file and update properties from Maven at build time
		// https://stackoverflow.com/questions/3697449/retrieve-version-from-maven-pom-xml-in-code
		try {
			InputStream iStream = Main.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
			Properties prop = new Properties();
			prop.load(iStream);
			VERSION = prop.getProperty("Application.version");
			COPYRIGHT = "Copyright " + prop.getProperty("Application.inceptionYear") + "-" + org.fross.library.Date.getCurrentYear() + " by Michael Fross";
		} catch (Exception ex) {
			Output.fatalError("Unable to read property file '" + PROPERTIES_FILE + "'", 2);
		}

		// Process Command Line Options and set flags where needed
		Getopt optG = new Getopt("DirSize", args, "Dvx:s:rec:z?h");
		while ((optionEntry = optG.getopt()) != -1) {
			switch (optionEntry) {
			// Debug Mode
			case 'D':
				Debug.enable();
				break;

			// Display Version and Exit
			case 'v':
				Output.printColorln(Ansi.Color.WHITE, "DirSize Version: v" + VERSION);
				Output.printColorln(Ansi.Color.CYAN, COPYRIGHT);
				Output.printColorln(Ansi.Color.WHITE, "\nLatest Release on GitHub: " + GitHub.updateCheck("dirsize"));
				Output.printColorln(Ansi.Color.CYAN, "HomePage: https://github.com/frossm/dirsize");
				System.exit(0);
				break;

			// Export to CSV File
			case 'x':
				exportFlag = true;

				try {
					exportFile.setExportFilename(optG.getOptarg());
					if (exportFile.createNewFile() == false) {
						throw new IOException();
					}
				} catch (IOException ex) {
					Output.fatalError("Could not create file: '" + exportFile.getName() + "'", 4);
				}
				break;

			// Sort output by...
			case 's':
				char sortOption = optG.getOptarg().toLowerCase().charAt(0);
				switch (sortOption) {

				// Sort by Size
				case 's':
					sortBy = 's';
					break;

				// Sort by Files
				case 'f':
					sortBy = 'f';
					break;

				// Sort by Directory Name
				case 'd':
					sortBy = 'd';
					break;

				// Display error if unsupported sort switch provided
				default:
					Output.fatalError("SortBy option '" + sortBy + "' not recognized.  See help", 1);
					break;
				}

				break;

			// Reverse Sort - Display results in ascending order
			case 'r':
				reverseSort = true;
				break;

			// Error Display flag
			case 'e':
				errorDisplayFlag = false;
				break;

			// Sets the width in columns of the output
			case 'c':
				try {
					terminalWidth = Integer.parseInt(optG.getOptarg());
					if (terminalWidth < MIN_TERMINAL_WIDTH) {
						terminalWidth = MIN_TERMINAL_WIDTH;
					}
					Output.debugPrintln("Number columns set to: " + terminalWidth);
				} catch (Exception Ex) {
					Output.fatalError("Invalid Option for -c (columns) switch: '" + optG.getOptarg() + "'", 1);
				}
				break;

			// Disable colorized output
			case 'z':
				Output.enableColor(false);
				break;

			// Display Help and Exit
			case 'h':
			case '?':
				Help.Display();
				System.exit(0);
				break;

			default:
				Output.fatalError("Unknown Command Line Option: '" + (char) optionEntry + "'", 1);
				Help.Display();
				System.exit(0);
				break;
			}
		}

		// Show program information
		Output.printColorln(Ansi.Color.CYAN, "DirSize v" + VERSION);
		Output.printColorln(Ansi.Color.CYAN, COPYRIGHT + "\n");

		// Display some useful information about the environment if in Debug Mode
		Debug.displaySysInfo();

		// If a directory was entered on the command line, validate it and set it as root. If not use the
		// current directory as the default
		try {
			rootDir = args[optG.getOptind()];
			if (new File(rootDir).isDirectory()) {
				rootDir = new File(rootDir).getAbsolutePath();
			} else {
				Output.fatalError("'" + rootDir + "' is not a valid directory", 1);
			}
		} catch (ArrayIndexOutOfBoundsException Ex) {
			// Use the current directory as the default as none was provided
			rootDir = System.getProperty("user.dir");
		} catch (Exception Ex) {
			Output.fatalError("Could not process command line arguments:\n" + Ex.getMessage(), 1);
		}

		// Create a File array of each subdirectory under the root directory that will be our target
		try {
			rootMembers = new File(rootDir).listFiles();

			// listFiles does return full paths. Build a HashMap with full paths
			for (int i = 0; i < rootMembers.length; i++) {
				mapFullPath.put(rootMembers[i].getName(), rootMembers[i].getAbsolutePath());
			}
		} catch (NullPointerException ex) {
			Output.printColorln(Ansi.Color.RED, "Error scanning root directory files");
			Output.fatalError("If DirSize is running as a snap, ensure it's been given the system-backup privilege.  See help (-h)\n", 1);
		}

		// Debug output: Show root members
		Output.debugPrintln("Root Members to Process:");
		Output.debugPrintln("  Directories:");
		if (Debug.query() == true) {
			for (int i = 0; i < rootMembers.length; i++) {
				if (rootMembers[i].isDirectory() == true)
					Output.debugPrintln("     - " + rootMembers[i].toString());
			}

			Output.debugPrintln("  Files:");
			for (int i = 0; i < rootMembers.length; i++) {
				if (rootMembers[i].isFile() == true)
					Output.debugPrintln("     - " + rootMembers[i].toString());
			}
		}

		// Display important values after options have been set
		Output.debugPrintln("Columns set to: " + terminalWidth);
		Output.debugPrintln("Root Directory: " + rootDir);
		Output.debugPrintln("SortBy [s, f, d]: " + sortBy);
		Output.debugPrintln("Surpress Error Display: " + errorDisplayFlag);
		try {
			Output.debugPrintln("Export Filename:  " + exportFile.getName());
		} catch (NullPointerException ex) {
			// Guess we're not exporting - ignore
		}

		// Prime the hash maps that will store the results of the scan
		mapSize.put(ROOT_DIR_NAME, 0L);
		mapFiles.put(ROOT_DIR_NAME, 0L);
		mapFullPath.put(ROOT_DIR_NAME, rootDir);

		Output.printColor(Ansi.Color.WHITE, "Scanning " + rootDir + ": ");

		// Create the spinner if we have color enabled (ANSI is needed for cursor movement)
		SpinnerBouncyBall spinner = new SpinnerBouncyBall();
		if (Output.queryColorEnabled() == true) {
			spinner.start();
		} else {
			Output.println("");
		}

		// Enable the benchmark timer
		Benchmark benchmarkTimer = new Benchmark();

		// Main program loop. Step through each of the root members.
		// If it's a file, add it up. If it's a directory, recursively get the totals
		for (int i = 0; i < rootMembers.length; i++) {

			// Process Directories
			if (rootMembers[i].isDirectory() == true) {
				// ScanDir returns a long array with [0]=Size totals & [1]=Files totals [2]=Errors
				long[] subDirTotals = new ScanDir().ScanDirectory(rootMembers[i]);

				// Save the results to the hash maps
				mapSize.put(rootMembers[i].getName(), subDirTotals[0]);
				mapFiles.put(rootMembers[i].getName(), subDirTotals[1]);

				// Update overall totals
				grandTotalSubdirs++;
				grandTotalSize += subDirTotals[0];
				grandTotalFiles += subDirTotals[1];
			}

			// Process Files
			else {
				mapFiles.put(ROOT_DIR_NAME, mapFiles.get(ROOT_DIR_NAME) + 1);
				mapSize.put(ROOT_DIR_NAME, mapSize.get(ROOT_DIR_NAME) + rootMembers[i].length());

				// Update overall totals
				grandTotalFiles++;
			}
		}

		// Stop the spinner
		if (Output.queryColorEnabled() == true) {
			spinner.interrupt();
			Output.printColorln(Ansi.Color.WHITE, "[Complete]");
		}

		// Determine number of columns based on the percentage constants
		int displayNameCol = (int) (terminalWidth * DISPLAY_PERCENT_NAME * .01);
		int displayFilesCol = (int) (terminalWidth * DISPLAY_PERCENT_NUMFILES * .01);
		int displaySizeCol = (int) (terminalWidth * DISPLAY_PERCENT_DIRSIZE * .01);
		int displayVisualMap = (int) (terminalWidth * DISPLAY_PERCENT_VISUALMAP * .01) - 5;

		Output.debugPrintln("Column Widths:");
		Output.debugPrintln("  - Terminal Width: " + terminalWidth);
		Output.debugPrintln("  - Name:  " + DISPLAY_PERCENT_NAME + "% = " + displayNameCol + " Columns");
		Output.debugPrintln("  - Files: " + DISPLAY_PERCENT_NUMFILES + "% = " + displayFilesCol + " Columns");
		Output.debugPrintln("  - Size:  " + DISPLAY_PERCENT_DIRSIZE + "% = " + displaySizeCol + " Columns");
		Output.debugPrintln("  - Map:   " + DISPLAY_PERCENT_VISUALMAP + "% = " + displayVisualMap + " Columns");

		// Determine the size of the VisualMap, which is a relative difference graphic between directories
		// unitsPerSlot is the FileSize of FileNumber per asterisk
		long unitsPerSlot = 0;
		if (sortBy == 'f') {
			// FilesMap
			unitsPerSlot = (SizeMap.queryMax(mapFiles, rootMembers) - SizeMap.queryMin(mapFiles, rootMembers)) / displayVisualMap;
		} else {
			// SizeMap
			unitsPerSlot = (SizeMap.queryMax(mapSize, rootMembers) - SizeMap.queryMin(mapSize, rootMembers)) / displayVisualMap;
		}

		Output.debugPrintln("Slots in VisualMap: " + displayVisualMap);
		Output.debugPrintln("Max Size found:       " + SizeMap.queryMax(mapSize, rootMembers));
		Output.debugPrintln("Min Size found:       " + SizeMap.queryMin(mapSize, rootMembers));
		Output.debugPrintln("Max Files found:       " + SizeMap.queryMax(mapFiles, rootMembers));
		Output.debugPrintln("Min Files found:       " + SizeMap.queryMin(mapFiles, rootMembers));
		Output.debugPrintln("Units Per slot:        " + unitsPerSlot);

		// Display the output header
		Output.printColorln(Ansi.Color.CYAN, "-".repeat(terminalWidth));
		Output.printColor(Ansi.Color.WHITE, "Directory" + " ".repeat(displayNameCol - 9));
		Output.printColor(Ansi.Color.WHITE, " ".repeat(displaySizeCol - 4) + "Size");
		Output.printColor(Ansi.Color.WHITE, " ".repeat(displayFilesCol - 5) + "Files");
		if (sortBy == 'f') {
			Output.printColor(Ansi.Color.WHITE, "    Files Map [" + unitsPerSlot + " files/slot]");
		} else {
			Output.printColor(Ansi.Color.WHITE, "    Size Map [" + Format.humanReadableBytes(unitsPerSlot) + "/slot]");
		}
		Output.printColorln(Ansi.Color.CYAN, "\n" + "-".repeat(terminalWidth));

		// Get the sorted results based on the which column the user chose (-s option)
		// If reverse sorting is desired (-r) adjust accordingly
		Map<String, Long> resultMap = null;
		switch (sortBy) {
		case 's':
			if (reverseSort == false)
				resultMap = SizeMap.sortByValueDescending(mapSize);
			else
				resultMap = SizeMap.sortByValueAscending(mapSize);
			break;

		case 'f':
			if (reverseSort == false)
				resultMap = SizeMap.sortByValueDescending(mapFiles);
			else
				resultMap = SizeMap.sortByValueAscending(mapFiles);
			break;

		case 'd':
			if (reverseSort == false)
				resultMap = SizeMap.sortByKeyAscendingCI(mapFiles);
			else
				resultMap = SizeMap.sortByKeyDescendingCI(mapFiles);
			break;

		default:
			Output.printColorln(Ansi.Color.RED, "ERROR: Could not detemine how to sort.  Defaulting to Size. You should never see this...");
			// Default to size sorting
			resultMap = SizeMap.sortByValueDescending(mapSize);
			break;
		}

		// Display the output
		int colorCounter = 0;

		for (Map.Entry<String, Long> i : resultMap.entrySet()) {
			String key = i.getKey();

			// Color swapper to alternate the colors of each output line
			Ansi.Color bgColor, fgColor;
			fgColor = ((colorCounter % 2 == 0) ? Ansi.Color.WHITE : Ansi.Color.DEFAULT);
			bgColor = Ansi.Color.DEFAULT;

			// Set the background to another color for symbolic links
			// Currently works well in Linux, but not in Windows
			if (Files.isSymbolicLink(Paths.get(mapFullPath.get(key))) == true) {
				fgColor = Ansi.Color.WHITE;
				bgColor = Ansi.Color.MAGENTA;
			}

			if (new File(mapFullPath.get(key)).isDirectory() == true) {
				// Display the Directory Name
				String displayName = key;

				// Append [LINK] to the name for symbolic links (Symbolic Link detection doesn't work in Windows)
				if (Files.isSymbolicLink(Paths.get(mapFullPath.get(key))) == true) {
					displayName = displayName + " [LINK]";
				}

				// Truncate the directory name if it's too long and add a ">" to the end
				if (key.length() > displayNameCol) {
					displayName = key.substring(0, Math.min(key.length(), displayNameCol - 3)) + "...";
				}

				String outString = String.format("%-" + displayNameCol + "s", displayName);
				Output.printColor(fgColor, bgColor, outString);

				// DISPLAY SIZE
				outString = String.format("%" + displaySizeCol + "s", Format.humanReadableBytes(mapSize.get(key)));
				Output.printColor(fgColor, bgColor, outString);

				// DISPLAY FILES
				DecimalFormat df = new DecimalFormat("#,###");
				outString = String.format("%" + displayFilesCol + "s", df.format((double) mapFiles.get(key)));
				Output.printColor(fgColor, bgColor, outString);

				// DISPLAY SIZE OR FILES MAP
				int numFilledSlots;
				try {
					if (sortBy == 'f') {
						numFilledSlots = (int) (mapFiles.get(key) / unitsPerSlot);
					} else {
						numFilledSlots = (int) (mapSize.get(key) / unitsPerSlot);
					}
				} catch (ArithmeticException ex) {
					// If there is an empty directory and no files, unitsPerSlot will be zero. Catch this.
					numFilledSlots = 0;
				}

				// Quick safety check for an out of bounds value
				if (numFilledSlots > displayVisualMap)
					numFilledSlots = displayVisualMap;

				int numEmptySlots = displayVisualMap - numFilledSlots;
				Output.printColor(Ansi.Color.WHITE, "    [");
				Output.printColor(Ansi.Color.YELLOW, MAP_FILLED_CHAR.repeat(numFilledSlots));
				Output.printColor(Ansi.Color.CYAN, MAP_EMPTY_CHAR.repeat(numEmptySlots));
				Output.printColor(Ansi.Color.WHITE, "]");
				System.out.println();

				// Update the export objects
				exportFile.addExportLine(displayName, mapSize.get(key), mapFiles.get(key));

			}
			colorCounter++;
		}

		// Display the summary information
		Output.printColorln(Ansi.Color.CYAN, "-".repeat(terminalWidth));
		// Name
		String outString = String.format("Directories: %-" + (displayNameCol - 13) + "s", grandTotalSubdirs);
		Output.printColor(Ansi.Color.CYAN, outString);
		// Size
		outString = String.format("%" + displaySizeCol + "s", Format.humanReadableBytes(grandTotalSize));
		Output.printColor(Ansi.Color.WHITE, outString);
		// Files
		DecimalFormat df = new DecimalFormat("#,###");
		outString = String.format("%" + displayFilesCol + "s", df.format((double) grandTotalFiles));
		Output.printColor(Ansi.Color.WHITE, outString);

		// Gather and display benchmark data
		float timeDelta = benchmarkTimer.Stop();
		float filesPerMS = grandTotalFiles / timeDelta;
		outString = String.format("\nScanning Time: %,d ms (%,.3f files/ms)", (int) timeDelta, filesPerMS);
		Output.printColorln(Ansi.Color.CYAN, "\n" + outString);

		// If Error Display is enabled and we have some errors, show them
		if (errorDisplayFlag == true && errorList.isEmpty() != true) {
			// Display the output header
			Output.printColorln(Ansi.Color.RED, "\n" + "-".repeat(terminalWidth));
			Output.printColorln(Ansi.Color.RED, "Scanning Errors  [Use -e to suppress]");
			Output.printColorln(Ansi.Color.RED, "-".repeat(terminalWidth));

			// Display the contents of the error list
			for (Map.Entry<String, String> i : errorList.entrySet()) {
				Output.printColorln(Ansi.Color.RED, i.getKey());
			}
		}

		// Export the results to a CSV file if user requested an export
		if (exportFlag == true) {
			if (exportFile.writeToDisk() == false) {
				Output.printColorln(Ansi.Color.RED, "Error exporting to file: " + exportFile.getName());
			}
		}

	}

}