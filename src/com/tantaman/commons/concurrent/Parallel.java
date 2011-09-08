package com.tantaman.commons.concurrent;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Parallel {
	private static final int NUM_CORES = Runtime.getRuntime().availableProcessors();
	public static <T> void For(final Iterable<T> pElements, final Operation<T> pOperation) {
		ExecutorService executor = Executors.newFixedThreadPool(NUM_CORES);
		List<Future<?>> futures = new LinkedList<Future<?>>();
		for (final T element : pElements) {
			Future<?> future = executor.submit(new Runnable() {
				@Override
				public void run() {
					pOperation.perform(element);
				}
			});
			
			futures.add(future);
		}
		
		for (Future<?> f : futures) {
			try {
				f.get();
			} catch (InterruptedException e) {
			} catch (ExecutionException e) {
			}
		}
		executor.shutdown();
	}
	
	public static interface Operation<T> {
		public void perform(T pParameter);
	}
}
