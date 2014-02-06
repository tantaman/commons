/*
 * Copyright 2011 Matt Crinklaw-Vogt
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

package com.tantaman.commons.concurrent.executors;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public abstract class AbstractObservableFuture<V> implements ObservableFuture<V> {
	private final Set<ObservableFuture.Observer<V>> observers = 
		new LinkedHashSet<ObservableFuture.Observer<V>>();
	private final Object completeLock = new Object();

	@Override
	public void addObserver(ObservableFuture.Observer<V> listener) {
		boolean notify = false;
		synchronized (completeLock) {
			if (isDone())
				notify = true;
			else
				observers.add(listener);
		}
	   
		if (notify) {
			try {
				listener.taskCompleted(get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void removeObserver(ObservableFuture.Observer<V> listener) {
		observers.remove(listener);
	}
	
	protected void notifyObservers(V val) {
		List<ObservableFuture.Observer<V>> observers = null;
		synchronized (completeLock) {
			observers = new LinkedList<ObservableFuture.Observer<V>>(this.observers);
			this.observers.clear();
		}
	   
		for (ObservableFuture.Observer<V> o : observers) {
			try {
				o.taskCompleted(val);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
}
