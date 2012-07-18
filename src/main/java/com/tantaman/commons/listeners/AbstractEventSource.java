package com.tantaman.commons.listeners;

public abstract class AbstractEventSource<T> implements IEventSource<T> {
	protected EventEmitter<T> emitter;
	public AbstractEventSource(Class<T> listenerClass, boolean weak) {
		emitter = EventEmitter.create(listenerClass, weak);
	}
	
	@Override
	public void addListener(T listener) {
		emitter.addListener(listener);
	}
	
	public void removeListener(T listener) {
		emitter.removeListener(listener);
	}
}
