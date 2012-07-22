package com.tantaman.commons.listeners;

public abstract class AbstractMultiEventSource implements IEventSource<Object> {
	protected final EventEmitterMulti emitter;
	public AbstractMultiEventSource(boolean weak, Class ... listenerClasses) {
		if (listenerClasses != null)
			emitter = EventEmitterMulti.create(weak, listenerClasses);
		else
			emitter = null;
	}
	
	@Override
	public void addListener(Object listener) {
		emitter.addListener(listener);
	}
	
	public void removeListener(Object listener) {
		emitter.removeListener(listener);
	}
}

