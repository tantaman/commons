/*
 * Copyright 2011 Matt Crinklaw-Vogt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.tantaman.commons.examples;

import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.tantaman.commons.concurrent.throttler.AccumulativeRunnable;
import com.tantaman.commons.concurrent.throttler.InvocationCombiner;

public class InvocationCombinerDemo {
	private static int counter = 0;
	public static void main(String[] args) {
		InvocationCombiner<String> combiner = new InvocationCombiner<String>(
				new AccumulativeRunnable<String>() {
					public void run(LinkedList<String> pParams) {
						System.out.println("RUNNING FOR THE " + (++counter) + " TIME!");
						for (String s : pParams) {
							System.out.println(s);
						}
					};
				}, 50, TimeUnit.MILLISECONDS, Executors.newScheduledThreadPool(1));
		
		String [] messages = "combining all invocations over a 50 millisecond period".split(" ");
		
		// we could get at most 2 runs b/c the first submission is dispatched immediately,
		// and then the 50 ms timer kicks in.
		// TODO: add an initial delay option.
		for (String message : messages) {
			combiner.invoke(message);
		}
	}
}
