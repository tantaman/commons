package com.tantaman.commons.listeners;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ListenerSet<T> implements IListenerSet<T> {
	private final Set<T> listeners = new CopyOnWriteArraySet<T>();
	
	@Override
	public Iterator<T> iterator() {
		return listeners.iterator();
	}

	@Override
	public void add(T listener) {
		listeners.add(listener);
	}

	@Override
	public void remove(T listener) {
		listeners.remove(listener);
	}

	@Override
	public int size() {
		return listeners.size();
	}

	
	@Override
	public void clear() {
		listeners.clear();
	}
}
