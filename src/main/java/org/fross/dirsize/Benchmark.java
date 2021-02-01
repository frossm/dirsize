/******************************************************************************
 * DirSize
 * 
 * DirSize is a simple command line based directory size reporting tool
 * 
 *  Copyright (c) 2019-2021 Michael Fross
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
	 * Stop the timer by setting an end time and then calculating the delta in milliseconds.
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
