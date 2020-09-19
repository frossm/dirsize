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

import static org.fusesource.jansi.Ansi.ansi;

import java.util.concurrent.TimeUnit;

import org.fross.library.Output;
import org.fusesource.jansi.Ansi;

public class Spinner extends Thread {
	protected final int SPINNER_DELAY = 120;

	String[] spinnerSymbols = { "|", "/", "-", "\\" };
	int currentSpinner = 0;

	/**
	 * run(): Overrides Thread run() method interface and is the main thread execution loop
	 */
	public void run() {
		// Keep calling the update spinner until the thread is interrupted
		while (Thread.currentThread().isInterrupted() == false) {
			// Spin the spinner
			spinSpinner();

			// Delay before next thread symbol is displayed
			try {
				TimeUnit.MILLISECONDS.sleep(SPINNER_DELAY);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}

		// Erase the last spinner symbol character
		System.out.println(" ");
	}

	/**
	 * spinSpinner(): Show the spinner symbol and advance to the next index
	 * 
	 */
	public void spinSpinner() {
		// Display the Spinner
		Output.printColor(Ansi.Color.YELLOW, spinnerSymbols[currentSpinner]);

		// Move cursor back one spot
		System.out.print(ansi().cursorLeft(1));

		// Advance the spinner to the next symbol
		currentSpinner++;

		// Loop it back around when we hit the end
		if (currentSpinner >= spinnerSymbols.length) {
			currentSpinner = 0;
		}
	}

} // END CLASS
