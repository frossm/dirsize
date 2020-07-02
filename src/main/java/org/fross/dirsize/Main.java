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
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.fross.library.Debug;
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

	// Class Variables
	protected static String VERSION;
	protected static String COPYRIGHT;
	protected static int maxThreads = 8;
	protected static int columns = 100;

	/**
	 * Main(): Main program execution entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int optionEntry;
		String rootDir = System.getProperty("user.dir");
		File[] rootMembers;

		// Process application level properties file
		// Update properties from Maven at build time:
		// https://stackoverflow.com/questions/3697449/retrieve-version-from-maven-pom-xml-in-code
		try {
			InputStream iStream = Main.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
			Properties prop = new Properties();
			prop.load(iStream);
			VERSION = prop.getProperty("Application.version");
			COPYRIGHT = "Copyright " + prop.getProperty("Application.inceptionYear") + "-" + org.fross.library.Date.getCurrentYear()
					+ " by Michael Fross.  All rights reserved";
		} catch (IOException ex) {
			Output.fatalError("Unable to read property file '" + PROPERTIES_FILE + "'", 3);
		}

		// Process Command Line Options and set flags where needed
		Getopt optG = new Getopt("DirSize", args, "Dvo:s:c:t:h?");
		while ((optionEntry = optG.getopt()) != -1) {
			switch (optionEntry) {
			case 'D': // Debug Mode
				Debug.enable();
				break;

			case 'v': // Display Version and Exit
				Output.println("DirSize Version: " + VERSION);
				Output.println(COPYRIGHT);
				System.exit(0);
				break;

			case 'o':  // Output size tree
				// ToDo
				break;

			case 's': // Sort via size or directory
				// ToDo
				break;

			case 'c': // Set number of columns for output
				try {
					columns = Integer.parseInt(optG.getOptarg());
				} catch (Exception Ex) {
					Output.fatalError("Invalid Option for -c (columns) switch: '" + optG.getOptarg() + "'", 1);
				}
				break;

			case 't': // Set the max number of threads to execute
				try {
					maxThreads = Integer.parseInt(optG.getOptarg());
				} catch (Exception Ex) {
					Output.fatalError("Invalid Option for -t (max threads) switch: '" + optG.getOptarg() + "'", 1);
				}
				break;

			default:
				Output.fatalError("Unknown Command Line Option: '" + (char) optionEntry + "'", 1);
				Help.Display();
				System.exit(0);
				break;
			}
		}

		// If a directory was entered on the command line, validate it and set it as root. If not use the
		// current directory default
		try {
			rootDir = args[optG.getOptind()];
			if (new File(rootDir).isDirectory()) {
				rootDir = new File(rootDir).getAbsolutePath();
			} else {
				Output.fatalError("'" + rootDir + "' is not a valid directory", 1);
			}
		} catch (ArrayIndexOutOfBoundsException Ex) {
			// Skip because there was no directory entered
		} catch (Exception Ex) {
			Output.fatalError("Could not process command line arguments:\n" + Ex.getMessage(), 1);
		}

		// Display important values after options have been set
		Output.debugPrint("Columns set to: " + columns);
		Output.debugPrint("Max Threads set to: " + maxThreads);
		Output.debugPrint("Root Directory: " + rootDir);

		// Create a file array of each subdirectory under the root directory
		rootMembers = new File(rootDir).listFiles();

		// Debug output: Show root members
//		Output.debugPrint("Root Members to Process:");
//		for (int i = 0; i < rootMembers.length; i++) {
//			Output.debugPrint("   " + i + ": [" + (rootMembers[i].isDirectory() == true ? "Dir ] " : "File] ") + rootMembers[i].toString());
//		}

		// Display header text
		Output.printColorln(Ansi.Color.CYAN, "DirSize v" + VERSION + "\n");
		Output.printColorln(Ansi.Color.CYAN, "Root Directory: " + rootDir);
		Output.printColorln(Ansi.Color.CYAN, "-".repeat(columns));

		// Testing threads
		DirThread t1 = new DirThread("one");
		DirThread t2 = new DirThread("two");
		t1.start();
		t2.start();

	} // END MAIN METHOD

} // END MAIN CLASS
