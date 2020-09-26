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

public class Spinner2 extends Thread {
	protected final int SPINNER_DELAY = 70;
	protected final int NUM_BALL_SLOTS = 6;
	protected final String LEFT_WALL = "[";
	protected final String RIGHT_WALL = "]";
	protected final String BALL = "o";

	// Position of the ball in it's journey
	int ballPosition = 0;

	// Direction the ball is heading. Positive is to the right
	int ballDirection = 1;

	/**
	 * run(): Overrides Thread run() method interface and is the main thread execution loop
	 */
	public void run() {
		// Keep calling the update spinner until the thread is interrupted
		while (Thread.currentThread().isInterrupted() == false) {
			// Bounce the ball
			bounceBall();

			// Delay before next thread symbol is displayed
			try {
				TimeUnit.MILLISECONDS.sleep(SPINNER_DELAY);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * bounceBall(): Show the spinner symbol and advance to the next index
	 * 
	 */
	public void bounceBall() {
		// Display the bouncy ball and walls
		Output.printColor(Ansi.Color.WHITE, LEFT_WALL);
		System.out.print(" ".repeat(ballPosition));
		Output.printColor(Ansi.Color.YELLOW, BALL);
		System.out.print(" ".repeat(NUM_BALL_SLOTS - ballPosition));
		Output.printColor(Ansi.Color.WHITE, RIGHT_WALL);

		// Move cursor back
		System.out.print(ansi().cursorLeft(NUM_BALL_SLOTS + 3));

		// Determine next ball location
		if (ballDirection > 0) {
			// Ball moving to the left (positive direction)
			ballPosition++;
			if (ballPosition >= NUM_BALL_SLOTS)
				ballDirection *= -1;
		} else {
			// Ball moving to the left
			ballPosition--;
			if (ballPosition <= 0)
				ballDirection *= -1;
		}
	}

} // END CLASS
