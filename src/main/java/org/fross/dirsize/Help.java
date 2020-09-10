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

import org.fross.library.Output;
import org.fusesource.jansi.Ansi;

/**
 * Help(): Display the help page when users enters 'h' command.
 * 
 * @author michael.d.fross
 *
 */
public class Help {
	/**
	 * Display(): Prints help in color using the JCDP library in the output module.
	 * 
	 */
	public static void Display() {
		Output.printColorln(Ansi.Color.CYAN, "\n+------------------------------------------------------------------------------+");
		Output.printColorln(Ansi.Color.CYAN, "DirSize v" + Main.VERSION + ".  " + Main.COPYRIGHT);
		Output.printColorln(Ansi.Color.CYAN, "+------------------------------------------------------------------------------+");
		Output.printColorln(Ansi.Color.WHITE, "DirSize is a tool to display directory size and file totals of subdirectories");
		Output.printColorln(Ansi.Color.CYAN, "                https://github.com/frossm/dirsize");

		Output.printColorln(Ansi.Color.YELLOW, "\nCommand Line Options:");
		Output.printColorln(Ansi.Color.WHITE, "Usage:    java -jar dirsize.jar [-D] [-x <filename>] [-ss|-sf|-sd] [-c <width>] [-v] [-h|?] [Directory]\n");
		Output.printColorln(Ansi.Color.WHITE, "   -D:   Debug Mode.  Displays extra debug output");
		Output.printColorln(Ansi.Color.WHITE, "   -e:   Suppress Error display.  Normally scanning errors are displayed");
		Output.printColorln(Ansi.Color.WHITE, "   -ss:  Sort output by directory size [Default]d");
		Output.printColorln(Ansi.Color.WHITE, "   -sf:  Sort output by file counts");
		Output.printColorln(Ansi.Color.WHITE, "   -sd:  Sort output by directory names");
		
		Output.printColorln(Ansi.Color.WHITE, "   -v:   Display the program version and exit");
		Output.printColorln(Ansi.Color.WHITE, "   -x filename: Export the results as a CSV to the file provided");
		Output.printColorln(Ansi.Color.WHITE, "   -c width:    Width of output in columns");
		Output.printColorln(Ansi.Color.WHITE, "   -h | -?:     Display this help information");

		Output.printColorln(Ansi.Color.YELLOW, "\nExample Usage:");
		Output.printColorln(Ansi.Color.WHITE, " java -jar dirsize.jar");
		Output.printColorln(Ansi.Color.CYAN, "    Display a size sorted report from the current directory\n");
		Output.printColorln(Ansi.Color.WHITE, " java -jar dirsize.jar -sd C:\\Apps");
		Output.printColorln(Ansi.Color.CYAN, "    Display a directoyr name sorted report from the C:\\Apps directory\n");
		Output.printColorln(Ansi.Color.WHITE, " java -jar dirsize.jar -c 80 -sf /home/jimbob");
		Output.printColorln(Ansi.Color.CYAN, "    Display a file number sorted report from the jimbob's home dir using 100 columns\n");
	}
}