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
 */
public class EventEmitter<T> {
	public final T emit;
	private final IListenerSet<T> listeners;
	private EventEmitter(T notifier, boolean weak, EventEmitterInvocationHandler<T> invokeHandler) {
		this.emit = notifier;
		
		if (weak) {
			listeners = new WeakListenerSet<T>();
		} else {
			listeners =	new ListenerSet<T>();
		}
		
		invokeHandler.listeners = listeners;
	}
	
	public void addListener(T listener) {
		listeners.add(listener);
	}
	
	public void removeListener(T listener) {
		listeners.remove(listener);
	}
	
	public void removeListeners() {
		listeners.clear();
	}
	
	public static <LT> EventEmitter<LT> create(Class<LT> listenerInterface, boolean weak) {
		EventEmitterInvocationHandler<LT> invokeHandler = new EventEmitterInvocationHandler<LT>();
		Object proxy = Proxy.newProxyInstance(
				EventEmitter.class.getClassLoader(),
				new Class [] {listenerInterface},
				invokeHandler);
		
		return new EventEmitter<LT>((LT)proxy, weak, invokeHandler);
	}
	
	private static class EventEmitterInvocationHandler<T> implements InvocationHandler {
		private volatile IListenerSet<T> listeners;
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			if (!method.isAccessible()) method.setAccessible(true); // TODO: security manager checks?
			for (T listener : listeners) {
				if (listener != null)
					method.invoke(listener, args);
			}
			
			return ObjectUtils.createNullInstanceOf(method.getReturnType());
		}
	}
}
