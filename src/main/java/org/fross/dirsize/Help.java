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

import org.fross.library.Format;
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
	 * Display(): Prints help information
	 */
	public static void Display() {
		final int HEADERWIDTH = 90;

		Output.printColorln(Ansi.Color.CYAN, "\n+" + "-".repeat(HEADERWIDTH - 2) + "+");
		Output.printColorln(Ansi.Color.CYAN, Format.CenterText(HEADERWIDTH, ("DirSize v" + Main.VERSION + ".  " + Main.COPYRIGHT)));
		Output.printColorln(Ansi.Color.CYAN, "+" + "-".repeat(HEADERWIDTH - 2) + "+");
		Output.printColorln(Ansi.Color.WHITE, Format.CenterText(HEADERWIDTH, "DirSize is a tool to display directory size and file totals of subdirectories"));
		Output.printColorln(Ansi.Color.CYAN, Format.CenterText(HEADERWIDTH, "https://github.com/frossm/dirsize"));

		Output.printColorln(Ansi.Color.YELLOW, "\nCommand Line Options:");
		Output.printColorln(Ansi.Color.CYAN, " java -jar dirsize.jar [-D] [-e] [-ss|-sf|-sd] [-r] [-x filename] [-c width] [-z] [-v] [-h|?] [Directory]");

		Output.printColorln(Ansi.Color.WHITE, "   -D:       Debug Mode.  Displays extra debug output");
		Output.printColorln(Ansi.Color.WHITE, "   -e:       Suppress Error display.  Normally scanning errors are displayed");
		Output.printColorln(Ansi.Color.WHITE, "   -ss:      Sort output by directory size [Default]");
		Output.printColorln(Ansi.Color.WHITE, "   -sf:      Sort output by file counts");
		Output.printColorln(Ansi.Color.WHITE, "   -sd:      Sort output by directory names");
		Output.printColorln(Ansi.Color.WHITE, "   -r:       Reverse the default sort order");
		Output.printColorln(Ansi.Color.WHITE, "   -x file:  Export the results as a CSV to the file provided");
		Output.printColorln(Ansi.Color.WHITE, "   -c width: Width of output in columns");
		Output.printColorln(Ansi.Color.WHITE, "   -z        Disable colorized output");
		Output.printColorln(Ansi.Color.WHITE, "   -v:       Display the program version as well as the latest release from GitHub");
		Output.printColorln(Ansi.Color.WHITE, "   -h | -?:  Display this help information");

		Output.printColorln(Ansi.Color.YELLOW, "\nExample Usage:");
		Output.printColorln(Ansi.Color.WHITE, " java -jar dirsize.jar");
		Output.printColorln(Ansi.Color.CYAN, "    Display a size sorted report from the current directory\n");
		Output.printColorln(Ansi.Color.WHITE, " java -jar dirsize.jar -sd C:\\Apps");
		Output.printColorln(Ansi.Color.CYAN, "    Display a directory name sorted report from the C:\\Apps directory\n");
		Output.printColorln(Ansi.Color.WHITE, " java -jar dirsize.jar -c 100 -sf /home/jimbob");
		Output.printColorln(Ansi.Color.CYAN, "    Display a file number sorted report from the jimbob's home dir using 100 columns");

		Output.printColorln(Ansi.Color.YELLOW, "\nSNAP permissions:");
		Output.printColorln(Ansi.Color.WHITE, " When installed via a snap, permissions must be given to read the filesystem");
		Output.printColorln(Ansi.Color.WHITE, " System-Backup allows dirsize to read directory contents and file sizes");
		Output.printColorln(Ansi.Color.WHITE, "   - Give permission:    sudo snap connect dirsize:system-backup");
		Output.printColorln(Ansi.Color.WHITE, "   - Remove permission:  sudo snap disconnect dirsize:system-backup");
		Output.printColorln(Ansi.Color.WHITE, "\n Home permission allows dirsize to import and output data to the filesystem");
		Output.printColorln(Ansi.Color.WHITE, "   - Give permission:    sudo snap connect dirsize:home");
		Output.printColorln(Ansi.Color.WHITE, "   - Remove permission:  sudo snap disconnect dirsize:home");
	}
}
