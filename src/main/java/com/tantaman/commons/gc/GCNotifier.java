/*
 * Copyright 2011 Tantaman LLC
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

package com.tantaman.commons.gc;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tantaman.commons.concurrent.NamedThreadFactory;
import com.tantaman.commons.ref.KeyedWeakReference;

public class GCNotifier {
	private final ReferenceQueue<Object> refQueue;
	private final ExecutorService notificationExec;
	// have to hold the weak refs so they don't get GC'ed.
	// if the weak refs are GC'ed, then we don't get notified
	// about the GC of whatever they are referencing.
	private final Map<Reference<?>, Object> refs;
	private static final Object NOTHING = new Object();
	
	public GCNotifier() {
		refQueue = new ReferenceQueue<Object>();
		notificationExec = Executors.newFixedThreadPool(1, new NamedThreadFactory(GCNotifier.class.getSimpleName()));
		refs = new ConcurrentHashMap<Reference<?>, Object>();
		
		notificationExec.execute(new Runnable() {
			@Override
			public void run() {
				while (!Thread.interrupted()) {
					try {
						KeyedWeakReference<Listener, Object> ref = (KeyedWeakReference<Listener, Object>)refQueue.remove();
						refs.remove(ref);
						ref.getKey().objectCollected();
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		});
	}
	
	public void register(Listener listener, Object object) {
		KeyedWeakReference<Listener, Object> ref = new KeyedWeakReference<Listener, Object>(listener, object, refQueue);
		refs.put(ref, NOTHING);
	}
	
	public static interface Listener {
		public void objectCollected();
	}
}
