/*
 *   Copyright 2010 Matthew Crinklaw-Vogt
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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import com.tantaman.commons.concurrent.NamedThreadFactory;

public class FoldingExecutorTest {
	private static volatile int numInvocations;
	@Test
	public void testCallableFolding() {
		final Object coordinator = new Object();
		
		ExecutorService exec = 
			new FoldingExecutor(Executors.newFixedThreadPool(1, new NamedThreadFactory(this.getClass().getSimpleName())), true);

		numInvocations = 0;
		Callable<Integer> task = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				synchronized (coordinator) {
					return ++numInvocations;
				}
			}
		};

		List<Future<Integer>> futures = new LinkedList<Future<Integer>>();
		
		synchronized (coordinator) {
			for (int i = 0; i < 10; ++i) {
				Future<Integer> f = exec.submit(task);
				futures.add(f);
			}
		}

		int lastIdentity = 0;
		int lastGet = 0;

		try {
			for (Future<Integer> f : futures) {
				if (lastIdentity == 0) {
					lastIdentity = System.identityHashCode(f);
					lastGet = f.get();
				}

				Assert.assertEquals(lastIdentity, System.identityHashCode(f));
				lastIdentity = System.identityHashCode(f);

				Assert.assertEquals(lastGet, f.get().intValue());
				lastGet = f.get();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Assert.assertEquals(1, numInvocations);
		exec.shutdownNow();
	}
	
	public void testRunnableFolding() {
		final Object coordinator = new Object();
		
		ExecutorService exec = 
			new FoldingExecutor(Executors.newFixedThreadPool(1, new NamedThreadFactory(this.getClass().getSimpleName())), true);

		numInvocations = 0;
		Runnable task = new Runnable() {
			@Override
			public void run() {
				synchronized (coordinator) {
					++numInvocations;
				}
			}
		};
		
		synchronized (coordinator) {
			for (int i = 0; i < 10; ++i) {
				exec.submit(task);
			}	
		}
		
		exec.shutdown();
		try {
			exec.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertEquals(1, numInvocations);
	}
}
