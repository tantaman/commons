package com.tantaman.commons.listeners;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.tantaman.commons.lang.ObjectUtils;

/**
 * EventEmitter allows you to register listeners of type T and then call them
 * by doing:
 * 
 * <code>
 * eventEmitterInstance.emit.listenerMethod();
 * </code>
 * @author tantaman
 *
 * @param <T>
 * 
 *  // TODO: inherit EventEmitter and EventEmitterMulti from a common base
 */
public class EventEmitterMulti {
	public final Object emit;
	private final IListenerSet<Object> listeners;
	private EventEmitterMulti(Object notifier, boolean weak, EventEmitterInvocationHandler invokeHandler) {
		this.emit = notifier;
		
		if (weak) {
			listeners = new WeakListenerSet<Object>();
		} else {
			listeners =	new ListenerSet<Object>();
		}
		
		invokeHandler.listeners = listeners;
	}
	
	public void addListener(Object listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Object listener) {
		listeners.remove(listener);
	}
	
	public void removeListeners() {
		listeners.clear();
	}
	
	public static EventEmitterMulti create(boolean weak, Class ... listenerInterfaces) {
		EventEmitterInvocationHandler invokeHandler = new EventEmitterInvocationHandler();
		Object proxy = Proxy.newProxyInstance(
				EventEmitter.class.getClassLoader(),
				listenerInterfaces,
				invokeHandler);
		
		return new EventEmitterMulti(proxy, weak, invokeHandler);
	}
	
	private static class EventEmitterInvocationHandler implements InvocationHandler {
		private volatile IListenerSet<Object> listeners;
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			if (!method.isAccessible()) method.setAccessible(true); // TODO: security manager checks?
			for (Object listener : listeners) {
				if (listener != null) {
					if (method.getDeclaringClass().isAssignableFrom(listener.getClass())) {
						method.invoke(listener, args);
					}
				}
			}
			
			return ObjectUtils.createNullInstanceOf(method.getReturnType());
		}
	}
}
