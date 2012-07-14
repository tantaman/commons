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
