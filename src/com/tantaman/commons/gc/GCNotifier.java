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
