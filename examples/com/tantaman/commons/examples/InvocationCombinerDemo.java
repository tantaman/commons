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
