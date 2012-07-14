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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.tantaman.commons.concurrent.executors.FoldingExecutor;

public class FoldingExecutorDemo {
	private static class Task implements Runnable {
		private final String message;
		public Task(String message) {
			this.message = message;
		}
		
		// hash code and equals that identify
		// all tasks of this Class as being duplicate tasks.
		@Override
		public int hashCode() {
			return getClass().hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			return obj.getClass() == getClass();
		}
		
		@Override
		public void run() {
			System.out.println(message);
			try {
				// sleep for demonstration
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		// exec that replaces queued tasks with duplicate submitted tasks
		ExecutorService exec = new FoldingExecutor(Executors.newFixedThreadPool(1), false);
		
		String [] messages = new String [] { "we", "only", "care", "about", "the", "last", "of", "us" };
		
		// we might get 2 messages output because replace can't replace running tasks, it
		// can only replace queued tasks
		System.out.println("REPLACE");
		for (String message : messages) {
			exec.execute(new Task(message));
		}
		
		exec.shutdown();
		exec.awaitTermination(200, TimeUnit.MILLISECONDS);
		
		// exec that throws away duplicate tasks
		exec = new FoldingExecutor(Executors.newFixedThreadPool(1), true);
		messages = new String [] { "we", "only", "care", "about", "the", "first", "of", "us" };
		
		System.out.println("THROW AWAY");
		for (String message : messages) {
			exec.execute(new Task(message));
		}
		
		exec.shutdown();
		exec.awaitTermination(200, TimeUnit.MILLISECONDS);
	}
}
