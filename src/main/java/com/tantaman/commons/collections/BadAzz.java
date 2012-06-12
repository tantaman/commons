package com.tantaman.commons.collections;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;

public class BadAzz {
	public static <T extends Collection<E>, E> E create(T collection, Class<E> elemInterface) {
		return (E)Proxy.newProxyInstance(BadAzz.class.getClassLoader(), 
				new Class [] {Collection.class, elemInterface}, new InvoHandler<E>(collection, elemInterface));
	}
	
	
	private static class InvoHandler<E> implements InvocationHandler {
		private final Class<E> elemInterface;
		private final Collection<E> delegate;
		
		public InvoHandler(Collection<E> delegate, Class<E> elemInterface) {
			this.elemInterface = elemInterface;
			this.delegate = delegate;
		}
		
		// TODO a way to return "this" isntead... prob not since it is supposed to conform to proxy interface...
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			// TODO: or isAssignable from?
			// What happens when both interfaces contain the same meth?
			// Maybe the proxy should not implement the collection's interface?
			Object lastResult = null;
			if (method.getDeclaringClass() == elemInterface) {
				for (E e : delegate) {
					lastResult = method.invoke(e, args);
				}
			} else {
				return method.invoke(delegate, args);
			}
			
			return lastResult;
		}
	}
}
