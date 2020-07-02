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
	 */
	public static void Display() {
		Output.printColorln(Ansi.Color.YELLOW, "\n+----------------------------------------------------------------------+");
		Output.printColorln(Ansi.Color.YELLOW, "DirSize v" + Main.VERSION + ".  "+Main.COPYRIGHT);
		Output.printColorln(Ansi.Color.YELLOW, "+----------------------------------------------------------------------+");
		Output.printColorln(Ansi.Color.WHITE, "DirSize is a tool to display directory size and file totals of subdirectories");
		Output.printColorln(Ansi.Color.CYAN, "                https://github.com/frossm/dirsize\n");

		Output.printColorln(Ansi.Color.YELLOW, "Command Line Options:");
		Output.printColorln(Ansi.Color.WHITE, "Usage:   java -jar DirSize.jar [-D] [-h] [-?] [-b|-k|-m] [Starting Dir]\n");
		Output.printColorln(Ansi.Color.WHITE, "   -D:  Debug Mode.  Displays extra debug output.");
		Output.printColorln(Ansi.Color.WHITE, "        Totals will include them.");
		Output.printColorln(Ansi.Color.WHITE, "   -?:  Display this help information");
	}
}