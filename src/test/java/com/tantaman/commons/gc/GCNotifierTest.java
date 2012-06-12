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
 *  limitations under the License.
 */


package com.tantaman.commons.gc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.junit.Test;

import com.tantaman.commons.gc.GCNotifier.Listener;

public class GCNotifierTest {

	// no fool proof way to test this
	@Test
	public void testRegister() {
		final int numObjects = 1000;
		final CountDownLatch collectionLatch = new CountDownLatch(numObjects);
		GCNotifier notif = new GCNotifier();
		// reads happening in junit thread, writes happening in GC notification thread
		// so we need a memory barrier
		final AtomicInteger numCollections = new AtomicInteger(0);
		
		for (int i = 0; i < numObjects; ++i) {
			notif.register(new Listener() {
				@Override
				public void objectCollected() {
					collectionLatch.countDown();
					numCollections.incrementAndGet();
				}
			}, new Object());
		}
		
		// allocate bytes and do stuff w/ them so the code isn't
		// optimized out
		int temp = 0;
		for (int i = 0; i < 1000; ++i) {
			byte [] forceCollection = new byte[1024];
			for (int j = 0; j < forceCollection.length; ++j) {
				forceCollection[j] = (byte)j;
			}
			
			for (byte b : forceCollection) {
				temp += b;
			}
		}
		
		// try to gc.. hopefully we already did earlier...
		System.gc();
		
		// wait a little while for the system to perform collections.
		try {
			collectionLatch.await(200, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Assert.assertEquals(numObjects, numCollections.get());
	}
}
