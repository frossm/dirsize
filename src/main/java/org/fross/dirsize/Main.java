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
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.fross.library.Debug;
import org.fross.library.Format;
import org.fross.library.Output;
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
	private static final int DISPLAY_PERCENT_SIZEMAP = 40;
	private static final String ROOT_DIR_NAME = "[RootDir]";

	// Class Variables
	protected static String VERSION;
	protected static String COPYRIGHT;

	/**
	 * Main(): Main program execution entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int optionEntry;
		String rootDir = "";
		File[] rootMembers = {};
		int maxColumns = org.fusesource.jansi.internal.WindowsSupport.getWindowsTerminalWidth() - 1;
		int terminalWidth = maxColumns;
		char sortBy = 's';	// Default is sortBy size. 'f' and 'd' are also allowed

		// Define the HashMaps the scanning results. The directory name will be the key
		HashMap<String, Long> mapSize = new HashMap<String, Long>();
		HashMap<String, Long> mapFiles = new HashMap<String, Long>();
		HashMap<String, String> mapFullPath = new HashMap<String, String>();

		// Variables to hold the overall grand total directories, sizes, and file counts
		long grandTotalSubdirs = 1L; // Starts with [root]
		long grandTotalSize = 0L;
		long grandTotalFiles = 0L;

		// getWindowsTerminalWidth() doens't work within Eclipse. This is a quick fix or ensure you use the
		// -c switch
		if (terminalWidth < 0) {
			Output.debugPrint("Seems to be running within Eclipse. Setting columns to 100");
			terminalWidth = 100;
		}

		// Process application level properties file and update properties from Maven at build time
		// https://stackoverflow.com/questions/3697449/retrieve-version-from-maven-pom-xml-in-code
		try {
			InputStream iStream = Main.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
			Properties prop = new Properties();
			prop.load(iStream);
			VERSION = prop.getProperty("Application.version");
			COPYRIGHT = "Copyright " + prop.getProperty("Application.inceptionYear") + "-" + org.fross.library.Date.getCurrentYear()
					+ " by Michael Fross.  All rights reserved";
		} catch (Exception ex) {
			Output.fatalError("Unable to read property file '" + PROPERTIES_FILE + "'", 2);
		}

		// Show program information
		Output.printColorln(Ansi.Color.CYAN, "DirSize v" + VERSION);
		Output.printColorln(Ansi.Color.CYAN, COPYRIGHT + "\n");

		// Process Command Line Options and set flags where needed
		Getopt optG = new Getopt("DirSize", args, "Dvx:s:c:?h");
		while ((optionEntry = optG.getopt()) != -1) {
			switch (optionEntry) {
			// Debug Mode
			case 'D':
				Debug.enable();
				break;

			// Display Version and Exit
			case 'v':
				Output.println("DirSize Version: " + VERSION);
				Output.println(COPYRIGHT);
				System.exit(0);
				break;

			// Export to CSV File
			case 'x':
				Output.printColorln(Ansi.Color.RED, "Not Yet Implemented...");
				break;

			// Sort output by...
			case 's':
				char sortOption = optG.getOptarg().toLowerCase().charAt(0);
				switch (sortOption) {
				case 's':	// Size
					sortBy = 's';
					break;
				case 'f':	// Files
					sortBy = 'f';
					break;
				case 'd':	// Directory
					sortBy = 'd';
					break;
				default:
					Output.fatalError("Sort by option (-s) not recognized: '" + sortBy + "' See help", 1);
					break;
				}
				break;

			// Sets the width in columns of the output
			case 'c':
				try {
					terminalWidth = Integer.parseInt(optG.getOptarg());
					if (terminalWidth > maxColumns) {
						terminalWidth = maxColumns;
					}
					Output.debugPrint("Number columns set to: " + terminalWidth);
				} catch (Exception Ex) {
					Output.fatalError("Invalid Option for -c (columns) switch: '" + optG.getOptarg() + "'", 1);
				}
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
		rootMembers = new File(rootDir).listFiles();

		// listFiles does return full paths. Build a HashMap with full paths
		for (int i = 0; i < rootMembers.length; i++) {
			mapFullPath.put(rootMembers[i].getName(), rootMembers[i].getAbsolutePath());
		}

		// Debug output: Show root members
		Output.debugPrint("Root Members to Process:");
		Output.debugPrint("  Directories:");
		for (int i = 0; i < rootMembers.length; i++) {
			if (rootMembers[i].isDirectory() == true)
				Output.debugPrint("     - " + rootMembers[i].toString());
		}
		Output.debugPrint("  Files:");
		for (int i = 0; i < rootMembers.length; i++) {
			if (rootMembers[i].isFile() == true)
				Output.debugPrint("     - " + rootMembers[i].toString());
		}

		// Display important values after options have been set
		Output.debugPrint("\nColumns set to: " + terminalWidth);
		Output.debugPrint("Root Directory: " + rootDir);
		Output.debugPrint("SortBy [s, f, d]: " + sortBy);

		// Prime the hash maps that will store the results
		mapSize.put(ROOT_DIR_NAME, (long) 0);
		mapFiles.put(ROOT_DIR_NAME, (long) 0);
		mapFullPath.put(ROOT_DIR_NAME, rootDir);

		Output.printColor(Ansi.Color.WHITE, "Scanning " + rootDir + ":  ");

		// Create the spinner
		Spinner spinner = new Spinner();
		// spinner.displaySpinner();
		spinner.start();

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
		spinner.interrupt();
		Output.printColorln(Ansi.Color.WHITE, " [Complete]");

		// Determine number of columns based on the percentage constants
		int displayNameCol = (int) (terminalWidth * DISPLAY_PERCENT_NAME * .01);
		int displayFilesCol = (int) (terminalWidth * DISPLAY_PERCENT_NUMFILES * .01);
		int displaySizeCol = (int) (terminalWidth * DISPLAY_PERCENT_DIRSIZE * .01);
		int displaySizeMap = (int) (terminalWidth * DISPLAY_PERCENT_SIZEMAP * .01) - 5;

		Output.debugPrint("Column Widths:");
		Output.debugPrint("  - Total Columns: " + terminalWidth);
		Output.debugPrint("  - Name:  " + DISPLAY_PERCENT_NAME + "% = " + displayNameCol + " Columns");
		Output.debugPrint("  - Files: " + DISPLAY_PERCENT_NUMFILES + "% = " + displayFilesCol + " Columns");
		Output.debugPrint("  - Size:  " + DISPLAY_PERCENT_DIRSIZE + "% = " + displaySizeCol + " Columns");
		Output.debugPrint("  - Map:   " + DISPLAY_PERCENT_SIZEMAP + "% = " + displaySizeMap + " Columns");

		// Determine the SizeMap, which is a relative difference graphic between directories
		// sizePerSlot is the file size per "asterisk"
		long sizePerSlot = (SizeMap.queryMax(mapSize, rootMembers) - SizeMap.queryMin(mapSize, rootMembers)) / displaySizeMap;

		Output.debugPrint("Slots in the map: " + displaySizeMap);
		Output.debugPrint("Max Size found: " + SizeMap.queryMax(mapSize, rootMembers));
		Output.debugPrint("Min Size found: " + SizeMap.queryMin(mapSize, rootMembers));
		Output.debugPrint("Size Per slot:  " + sizePerSlot);

		// Display the output header
		Output.printColorln(Ansi.Color.CYAN, "-".repeat(terminalWidth));
		Output.printColor(Ansi.Color.WHITE, "Directory" + " ".repeat(displayNameCol - 9));
		Output.printColor(Ansi.Color.WHITE, " ".repeat(displaySizeCol - 4) + "Size");
		Output.printColor(Ansi.Color.WHITE, " ".repeat(displayFilesCol - 5) + "Files");
		Output.printColor(Ansi.Color.WHITE, "    SizeMap [" + Format.humanReadableBytes(sizePerSlot) + " per slot]");
		Output.printColorln(Ansi.Color.CYAN, "\n" + "-".repeat(terminalWidth));

		// Get the sorted results based on the which column the user chose (-s option)
		Map<String, Long> resultMap = null;
		switch (sortBy) {
		case 's':
			resultMap = HashmapUtils.sortByValueDescending(mapSize);
			break;
		case 'f':
			resultMap = HashmapUtils.sortByValueDescending(mapFiles);
			break;
		case 'd':
			// TODO: Case insensitive ordering
			resultMap = new TreeMap<>(mapSize);
			break;
		default:
			Output.printColorln(Ansi.Color.RED, "ERROR: Could not detemine how to sort.  You should never see this...");
			break;
		}

		// Display the output
		for (Map.Entry<String, Long> i : resultMap.entrySet()) {
			String key = i.getKey();

			if (new File(mapFullPath.get(key)).isDirectory() == true) {
				// Name
				String outString = String.format("%-" + displayNameCol + "s", key);
				Output.printColor(Ansi.Color.WHITE, outString);

				// Size
				outString = String.format("%" + displaySizeCol + "s", Format.humanReadableBytes(mapSize.get(key)));
				Output.printColor(Ansi.Color.WHITE, outString);

				// Files
				DecimalFormat df = new DecimalFormat("#,###");
				outString = String.format("%" + displayFilesCol + "s", df.format((double) mapFiles.get(key)));
				Output.printColor(Ansi.Color.WHITE, outString);

				// Size Map
				int numAsterisk = (int) (mapSize.get(key) / sizePerSlot);
				int numDashes = displaySizeMap - numAsterisk;
				Output.printColor(Ansi.Color.WHITE, "    [");
				Output.printColor(Ansi.Color.YELLOW, "*".repeat(numAsterisk));
				Output.printColor(Ansi.Color.CYAN, "-".repeat(numDashes));
				Output.printColor(Ansi.Color.WHITE, "]");
				System.out.println();

			}
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

	} // END MAIN METHOD

} // END MAIN CLASS
