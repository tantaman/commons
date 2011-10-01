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
		
		// we'll might get 2 messages output because replace can't replace running tasks, it
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
