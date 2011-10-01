package com.tantaman.commons.examples;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.tantaman.commons.concurrent.executors.ObservableFuture;
import com.tantaman.commons.concurrent.executors.ObservableFuture.Observer;
import com.tantaman.commons.concurrent.executors.ObservableFuturesThreadPool;

public class ObservableFuturesThreadPoolDemo {
	public static void main(String[] args) {
		ObservableFuturesThreadPool pool = 
		new ObservableFuturesThreadPool(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
		
		ObservableFuture<Integer> f = pool.submit(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return 5;
			}
		});
		
		f.addObserver(new Observer<Integer>() {
			@Override
			public void taskCompleted(Integer result) {
				System.out.println("Task completed with result: " + result);
			}
		});
	}
}
