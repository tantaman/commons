package com.tantaman.commons.listeners;

public abstract class AbstractMultiEventSource implements IEventSource<Object> {
	protected EventEmitterMulti emitter;
	public AbstractMultiEventSource(boolean weak, Class ... listenerClasses) {
		emitter = EventEmitterMulti.create(weak, listenerClasses);
	}
	
	@Override
	public void addListener(Object listener) {
		emitter.addListener(listener);
	}
	
	public void removeListener(Object listener) {
		emitter.removeListener(listener);
	}
}

