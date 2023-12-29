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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.fross.library.Output;
import org.fusesource.jansi.Ansi;

public class Export {
	File exportFile = null;
	List<String> entryDirName = new ArrayList<>();
	List<Long> entryTotalSize = new ArrayList<>();
	List<Long> entryTotalFiles = new ArrayList<>();

	/**
	 * Constructor: Set export file via passed FILE
	 * 
	 * @param f
	 */
	public Export() {

	}

	public Export(String f) {
		setExportFilename(f);
	}

	/**
	 * setExportFilename(): Overwrite an existing output filename
	 * 
	 * @param fn
	 */
	public void setExportFilename(String fn) {
		exportFile = new File(fn);
		Output.debugPrintln("Output file set to: '" + exportFile.getName() + "'");
	}

	/**
	 * addExportLine(): Add a line to the output lists done after each directory scan
	 * 
	 * @param directory
	 * @param totalSize
	 * @param totalFiles
	 */
	public void addExportLine(String directory, long totalSize, long totalFiles) {
		entryDirName.add(directory);
		entryTotalSize.add(totalSize);
		entryTotalFiles.add(totalFiles);
	}

	/**
	 * writeToDisk(): Dump the contents of all directories to the export file
	 * 
	 */
	public boolean writeToDisk() {
		try {
			if (exportFile.canWrite()) {
				FileWriter exportFW;
				try {
					// Define the output file
					exportFW = new FileWriter(exportFile);

					// Output the header to the CSV file
					exportFW.append("\"" + "Directory" + "\",\"" + "Size in Bytes" + "\",\"" + "Files" + "\"\n");

					// Loop through the results and export the output
					for (int i = 0; i < entryDirName.size(); i++) {
						exportFW.append("\"" + entryDirName.get(i) + "\",");
						exportFW.append("\"" + entryTotalSize.get(i) + "\",");
						exportFW.append("\"" + entryTotalFiles.get(i) + "\"");
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
			return false;
		}

		return true;

	}

	/**
	 * createNewFile(): Creates a new file (duh)
	 * 
	 * @return
	 */
	public boolean createNewFile() {
		try {
			return exportFile.createNewFile();
		} catch (Exception ex) {
			Output.printColorln(Ansi.Color.RED, ex.getMessage());
			return false;
		}
	}

	/**
	 * getName(): Returns the name of the file
	 * 
	 * @return
	 */
	public String getName() {
		try {
			return exportFile.getName();
		} catch (NullPointerException ex) {
			return "";
		}
	}

}
