/*
 * Copyright 2012 Matt Crinklaw-Vogt
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

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import com.tantaman.commons.gc.GCNotifier;

/**
 * A thread safe and weakly held set of listeners that provides notifications
 * when it grows beyond a certain size.
 * 
 * Unlike a traditional set, additions and removals are O(N).
 * The O(N) performance is due to the set members being held weakly.
 * 
 * By default the weak references with null referents are only removed from the set up when a listener
 * is added from the set or when the set is iterated.
 * 
 * To actively clean out weak references when their referents are removed, supply a
 * GCNotifier.
 * 
 * @author tantaman
 *
 */
public class WeakListenerSet<T> implements Iterable<T> {
	private final List<WeakReference<T>> listeners;
	private final GCNotifier gcNotifier;
	private final GCListener gcListener;
	private final int softMaxSize;
	private final String name;
	
	public WeakListenerSet() {
		this(null, 10, null);
	}
	
	public WeakListenerSet(int softMaxSize) {
		this(null, softMaxSize, null);		
	}
	
	public WeakListenerSet(String name) {
		this(name, 10, null);
	}
	
	public WeakListenerSet(GCNotifier notifier) {
		this(null, 10, notifier);
	}
	
	public WeakListenerSet(String name, int softMaxSize) {
		this(name, softMaxSize, null);
	}
	
	public WeakListenerSet(String name, int softMaxSize, GCNotifier notifier) {
		this.name = name;
		gcNotifier = notifier;
		this.softMaxSize = softMaxSize;
		if (notifier != null) {
			gcListener = new GCListener();
		} else {
			gcListener = null;
		}
		
		listeners = new CopyOnWriteArrayList<WeakReference<T>>();
	}
	
	public void add(T listener) {
		WeakReference<T> ref = new WeakReference<T>(listener);
		
		List<WeakReference<T>> toRemove = new LinkedList<WeakReference<T>>();
		for (WeakReference<T> listenerRef : listeners) {
			T theListener = listenerRef.get();
			if (theListener != null) {
				if (listener == theListener)
					return; // already present in the set
			} else {
				toRemove.add(listenerRef);
			}
		}
		
		listeners.removeAll(toRemove);

		// We don't have to add to this list before registering with the GC notifier
		// since listener is strongly held until this method returns.
		listeners.add(ref);
		if (gcNotifier != null) {
			gcNotifier.register(gcListener, listener);
		}
		
		if (listeners.size() > softMaxSize && softMaxSize > 0) {
			// TODO: allow logger configuration.
			System.out.println(name + ": Number of listeners exceeded max size.");
		}
	}
	
	public int size() {
		return listeners.size();
	}
	
	/* No need for a remove since listeners are held weakly.
	public void remove(T listener) {
	}
	*/
	
	@Override
	public Iterator<T> iterator() {
		return new ListenerSetIterator();
	}
	
	private class ListenerSetIterator implements Iterator<T> {
		private final ListIterator<WeakReference<T>> iter;
		public ListenerSetIterator() {
			iter = listeners.listIterator();
		}
		
		@Override
		public boolean hasNext() {
			// Doing the below operations is pointless
			// since a listener could be collected between a hasNext and a next call.
			// Users of this class will need to know to check what is returned from next
			// for null.
			
//			boolean result = false;
//			int cnt = 0;
//			while (iter.hasNext()) {
//				WeakReference<T> next = iter.next();
//				++cnt;
//				if (next.get() != null) {
//					result = true;
//					break;
//				}
//			}
//			
//			if (result) {
//				for (; cnt > -1; --cnt) {
//					iter.previous();
//				}
//				return true;
//			} else
//				return false;
			
			return iter.hasNext();
		}

		@Override
		public T next() {
			System.out.println("NEXT");
			WeakReference<T> next = iter.next();
			T theListener = next.get();
			
			if (theListener == null) {
				System.out.println("NULL");
				listeners.remove(next);
				System.out.println(listeners.size());
			}
			
			return theListener;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	private class GCListener implements GCNotifier.Listener {
		@Override
		public void objectCollected() {
			System.out.println("COLLECTED");
			Iterator<T> iter = WeakListenerSet.this.iterator();
			while (iter.hasNext())
				iter.next(); // next will automatically clean out null entries.
		}
	}
}
