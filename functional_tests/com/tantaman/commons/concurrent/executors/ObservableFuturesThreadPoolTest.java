/*
*   Copyright 2011 Tantaman LLC
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/

package com.tantaman.commons.concurrent.executors;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Assert;
import org.junit.Test;

import com.tantaman.commons.concurrent.executors.ObservableFuture.Observer;

public class ObservableFuturesThreadPoolTest {
	/** 
	 * lock used for testing so we can ensure our
	 * test variables have been set when we go to check them
	 * and that the test case does not exit before our task executes.
	 * */
	private final ReentrantLock testLock = new ReentrantLock();
	private final Condition listenerNotified = testLock.newCondition();
	
	private ObservableFuturesThreadPool createPool() {
		 return
			new ObservableFuturesThreadPool(3, 3,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());
	}
	
	@Test
	public void testSubmitCallableOfT() {
		ObservableFuturesThreadPool pool = createPool();
		final Object expected = new Object();
		ObservableFuture<Object> future = pool.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				return expected;
			}
		});
		
		testConditions(future, expected);
	}

	@Test
	public void testSubmitRunnable() {
		ObservableFuturesThreadPool pool = createPool();
		ObservableFuture future = pool.submit(new Runnable() {
			@Override
			public void run() {
			}
		});
		
		testConditions(future, null);
	}

	@Test
	public void testSubmitRunnableT() {
		Object expected = new Object();
		ObservableFuturesThreadPool pool = createPool();
		ObservableFuture<Object> future = pool.submit(new Runnable() {
			@Override
			public void run() {
			}
		}, expected);
		
		testConditions(future, expected);
	}
	
	private void testConditions(ObservableFuture<Object> future, Object expected) {
		// lock used for testing so we can ensure our
		// test variables have been set when we go to check them
		// and that the test case does not exit before our task executes.
		testLock.lock();
		try {
			final AtomicReference<Object> listenerResult = new AtomicReference<Object>(new Object());
			future.addObserver(new Observer<Object>() {
				@Override
				public void taskCompleted(Object result) {
					// lock here so we can finish adding our listener first (for this test case)
					testLock.lock();
					try {
						listenerResult.set(result);
						listenerNotified.signalAll();
					} finally {
						testLock.unlock();
					}
				}
			});
			
			try {
				// wait until our listener has been notified, or 200 MS.
				listenerNotified.await(200, TimeUnit.MILLISECONDS);
				Assert.assertTrue(expected == listenerResult.get());
			} catch (InterruptedException e) {
				Assert.fail(e.getMessage());
			}
		} finally {
			testLock.unlock();
		}
	}
}
