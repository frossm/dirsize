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
import java.io.FileWriter;
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
	private static final String MAP_FILLED_CHAR = "*";
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
		int terminalWidth = 90;
		File exportFile = null;

		// Define the HashMaps the scanning results. The directory name will be the key
		HashMap<String, Long> mapSize = new HashMap<String, Long>();
		HashMap<String, Long> mapFiles = new HashMap<String, Long>();
		HashMap<String, String> mapFullPath = new HashMap<String, String>();

		// Variables to hold the overall grand total directories, sizes, and file counts
		long grandTotalSubdirs = 1L; // Starts with [root]
		long grandTotalSize = 0L;
		long grandTotalFiles = 0L;

		// Set the terminalWidth. jAnsi will get it for windows, but doesn't seem to work for Linux
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			// terminalWidth = org.fusesource.jansi.internal.WindowsSupport.getWindowsTerminalWidth() - 1;
			terminalWidth = 90;
		} else if (System.getProperty("os.name").toLowerCase().contains("linux")) {
			// TODO: determine how to handle this better. For now just set it to a reasonable amount
			terminalWidth = 90;
		} else {
			// Just set the terminalWidth to a fairly safe value
			terminalWidth = 90;
		}

		// getWindowsTerminalWidth() doesn't seem to work within Eclipse
		// This is a quick fix or ensure you use the -c switch as an argument
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

		// Process Command Line Options and set flags where needed
		Getopt optG = new Getopt("DirSize", args, "Dvx:s:rec:?h");
		while ((optionEntry = optG.getopt()) != -1) {
			switch (optionEntry) {
			// Debug Mode
			case 'D':
				Debug.enable();
				break;

			// Display Version and Exit
			case 'v':
				Output.printColorln(Ansi.Color.CYAN, "DirSize v" + VERSION);
				Output.printColorln(Ansi.Color.CYAN, COPYRIGHT);
				System.exit(0);
				break;

			// Export to CSV File
			case 'x':
				try {
					exportFile = new File(optG.getOptarg());
					if (exportFile.createNewFile() == false) {
						Output.fatalError("Could not create file: '" + exportFile + "'", 4);
					}
				} catch (IOException ex) {
					Output.fatalError("Could not create file: '" + exportFile + "'", 4);
				}
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
		Output.debugPrint("Columns set to: " + terminalWidth);
		Output.debugPrint("Root Directory: " + rootDir);
		Output.debugPrint("SortBy [s, f, d]: " + sortBy);
		Output.debugPrint("Surpress Error Display: " + errorDisplayFlag);
		try {
			Output.debugPrint("Export Filename:  " + exportFile.getName());
		} catch (NullPointerException ex) {
			// Guess we're not exporting - ignore
		}

		// Prime the hash maps that will store the results
		mapSize.put(ROOT_DIR_NAME, (long) 0);
		mapFiles.put(ROOT_DIR_NAME, (long) 0);
		mapFullPath.put(ROOT_DIR_NAME, rootDir);

		Output.printColor(Ansi.Color.WHITE, "Scanning " + rootDir + ": ");

		// Create the spinner
		SpinnerBouncyBall spinner = new SpinnerBouncyBall();
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
		Output.printColorln(Ansi.Color.WHITE, "[Complete]");

		// Determine number of columns based on the percentage constants
		int displayNameCol = (int) (terminalWidth * DISPLAY_PERCENT_NAME * .01);
		int displayFilesCol = (int) (terminalWidth * DISPLAY_PERCENT_NUMFILES * .01);
		int displaySizeCol = (int) (terminalWidth * DISPLAY_PERCENT_DIRSIZE * .01);
		int displayVisualMap = (int) (terminalWidth * DISPLAY_PERCENT_VISUALMAP * .01) - 5;

		Output.debugPrint("Column Widths:");
		Output.debugPrint("  - Terminal Width: " + terminalWidth);
		Output.debugPrint("  - Name:  " + DISPLAY_PERCENT_NAME + "% = " + displayNameCol + " Columns");
		Output.debugPrint("  - Files: " + DISPLAY_PERCENT_NUMFILES + "% = " + displayFilesCol + " Columns");
		Output.debugPrint("  - Size:  " + DISPLAY_PERCENT_DIRSIZE + "% = " + displaySizeCol + " Columns");
		Output.debugPrint("  - Map:   " + DISPLAY_PERCENT_VISUALMAP + "% = " + displayVisualMap + " Columns");

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

		Output.debugPrint("Slots in VisualMap: " + displayVisualMap);
		Output.debugPrint("Max Size found:       " + SizeMap.queryMax(mapSize, rootMembers));
		Output.debugPrint("Min Size found:       " + SizeMap.queryMin(mapSize, rootMembers));
		Output.debugPrint("Max Files found:       " + SizeMap.queryMax(mapFiles, rootMembers));
		Output.debugPrint("Min Files found:       " + SizeMap.queryMin(mapFiles, rootMembers));
		Output.debugPrint("Units Per slot:        " + unitsPerSlot);

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
				// DISPLAY DIRECTORY NAME
				// Truncate the directory name if it's too long and add a ">" to the end
				String dName = key;
				if (key.length() > displayNameCol) {
					dName = key.substring(0, Math.min(key.length(), displayNameCol - 3)) + "...";
				}

				String outString = String.format("%-" + displayNameCol + "s", dName);
				Output.printColor(fgColor, bgColor, outString);

				// DISPLAY SIZE
				outString = String.format("%" + displaySizeCol + "s", Format.humanReadableBytes(mapSize.get(key)));
				Output.printColor(fgColor, bgColor, outString);

				// DISPLAY FILES
				DecimalFormat df = new DecimalFormat("#,###");
				outString = String.format("%" + displayFilesCol + "s", df.format((double) mapFiles.get(key)));
				Output.printColor(fgColor, bgColor, outString);

				// DISPLAY SIZE OR FILES MAP
				int numAsterisk;
				try {
					if (sortBy == 'f') {
						numAsterisk = (int) (mapFiles.get(key) / unitsPerSlot);
					} else {
						numAsterisk = (int) (mapSize.get(key) / unitsPerSlot);
					}
				} catch (ArithmeticException ex) {
					// If there is an empty directory and no files, unitsPerSlot will be zero. Catch this.
					numAsterisk = 0;
				}

				// Quick safety check for an out of bounds value
				if (numAsterisk > displayVisualMap)
					numAsterisk = displayVisualMap;

				int numDashes = displayVisualMap - numAsterisk;
				Output.printColor(Ansi.Color.WHITE, "    [");
				Output.printColor(Ansi.Color.YELLOW, MAP_FILLED_CHAR.repeat(numAsterisk));
				Output.printColor(Ansi.Color.CYAN, MAP_EMPTY_CHAR.repeat(numDashes));
				Output.printColor(Ansi.Color.WHITE, "]");
				System.out.println();
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
		try {
			if (exportFile.canWrite()) {
				FileWriter exportFW;
				try {
					// Define the output file
					exportFW = new FileWriter(exportFile);

					// Output the header to the CSV file
					exportFW.append("\"" + "Directory" + "\",\"" + "Size" + "\",\"" + "Files" + "\"\n");

					// Loop through the results and export the output
					for (Map.Entry<String, Long> i : resultMap.entrySet()) {
						String key = i.getKey();
						exportFW.append("\"" + mapFullPath.get(key) + "\",");
						exportFW.append("\"" + mapSize.get(key) + "\",");
						exportFW.append("\"" + mapFiles.get(key) + "\"");
						exportFW.append("\n");
					}
					exportFW.flush();
					exportFW.close();

					Output.printColorln(Ansi.Color.CYAN, "\nExport Completed to file: " + exportFile.getAbsolutePath());
				} catch (IOException ex) {
					Output.printColorln(Ansi.Color.RED, "Error writing to export file: " + ex.getMessage());
				}
			}
		} catch (Exception ex) {
			// Looks like we are not exporting - ignore
		}

	} // END MAIN METHOD

} // END MAIN CLASS
