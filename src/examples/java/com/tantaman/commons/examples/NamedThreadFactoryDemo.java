package com.tantaman.commons.examples;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tantaman.commons.concurrent.NamedThreadFactory;

public class NamedThreadFactoryDemo {
	public static void main(String[] args) {
		NamedThreadFactory threadFactory = new NamedThreadFactory("DemoThread");
		
		ExecutorService threadPool = Executors.newFixedThreadPool(5, threadFactory);
		
		Collection<Callable<Void>> callables = new LinkedList<Callable<Void>>();
		
		for (int i = 0; i < 10; ++i) {
			callables.add(new MyCallable());
		}
		
		try {
			threadPool.invokeAll(callables);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static class MyCallable implements Callable<Void> {
		public Void call() throws Exception {
			System.out.println(Thread.currentThread().getName());
			return null;
		};
	}
}
