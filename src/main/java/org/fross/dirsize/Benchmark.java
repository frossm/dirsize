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

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import org.fross.library.Output;

/**
 * Benchmark simply starts a timer when it is created
 *
 * @author michael.d.fross
 */
public class Benchmark {

	LocalTime StartTime;
	LocalTime EndTime;
	long Delta;

	/**
	 * Constructor sets the start time.
	 */
	public Benchmark() {
		StartTime = LocalTime.now();
		Output.debugPrint("Benchmark StartTime = " + StartTime.toString());
	}

	/**
	 * Stop the timer by setting an end time and then calculating the delta in
	 * milliseconds.
	 *
	 * @return long
	 */
	public long Stop() {
		EndTime = LocalTime.now();

		Delta = ChronoUnit.MILLIS.between(StartTime, EndTime);
		
		Output.debugPrint("Benchmark EndTime   = " + EndTime.toString());
		Output.debugPrint("Milliseconds Delta = " + Delta);

		return (Delta);
	}

	/**
	 * Return the start time of the timer
	 *
	 * @return long
	 */
	public LocalTime queryStartTime() {
		return (StartTime);
	}

	/**
	 * Return the end time of the timer
	 *
	 * @return long
	 */
	public LocalTime queryEndTime() {
		return (EndTime);
	}

}
