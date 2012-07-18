/*
 * Copyright 2012 Tantaman LLC
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

package com.tantaman.commons.listeners;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.tantaman.commons.gc.GCNotifier;

public class WeakListenerSetTest {
	public static byte [] tryToForceGC;
	@Test
	public void testRegistration() {
		IListenerSet<TestListener> listeners = new WeakListenerSet<TestListener>("SomeName");
		TestListener someListener = new TestListener();
		listeners.add(someListener);
		
		assertEquals(1, listeners.size());
		
		listeners.add(someListener);
		
		assertEquals(1, listeners.size());
		
		TestListener someListener2 = new TestListener();
		
		listeners.add(someListener2);
		
		assertEquals(2, listeners.size());
	}
	
	@Test
	public void testNotification() {
		IListenerSet<TestListener> listeners = new WeakListenerSet<TestListener>();
		
		TestListener someListener = new TestListener();
		TestListener someListener2 = new TestListener();
		
		listeners.add(someListener2);
		listeners.add(someListener);
		
		for (TestListener listener : listeners) {
			listener.change();
			assertEquals(1, listener.count);
		}
	}
	
	/**
	 * There is no real way to test this since
	 * the system will garbage collect whenever it feels like it...
	 */
	@Test
	public void testCleanup() {
		IListenerSet<TestListener> listeners = new WeakListenerSet<TestListener>();
		
		listeners.add(new TestListener());
		
		attemptGC();
		
		TestListener listener = new TestListener();
		listeners.add(listener);
		
		assertEquals("If this failed it probably isn't a true failure.  This testing is assuming that a garbage collection is being performed which may not be the case.", 1, listeners.size());
		
		listener = null;
		
		attemptGC();
		
		assertEquals(1, listeners.size());
	}
	
	private static void attemptGC() {
		System.gc();
		tryToForceGC = new byte[2000];
		System.gc();
		System.gc();
	}
	
	@Test
	public void testGCListenerCleanup() {
		IListenerSet<TestListener> listeners = new WeakListenerSet<TestListener>(new GCNotifier());
		
		listeners.add(new TestListener());
		
		attemptGC();
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("Again, a failure here isn't necesarilly a real failure since we are making assumptions about how the garbage collector will behave.", 0, listeners.size());
	}
	
	private static class TestListener {
		public int count = 0;
		public void change() {
			count++;
		}
	}
}
