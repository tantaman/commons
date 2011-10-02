package com.tantaman.commons.concurrent.executors;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;

public abstract class AbstractObservableFuture<V> implements ObservableFuture<V> {
	private final Set<ObservableFuture.Observer<V>> observers = 
		new CopyOnWriteArraySet<ObservableFuture.Observer<V>>();

	@Override
	public void addObserver(ObservableFuture.Observer<V> listener) {
		observers.add(listener);
		if (isDone()) {
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
		for (ObservableFuture.Observer<V> o : observers) {
			try {
				o.taskCompleted(val);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
}
