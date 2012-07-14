/*
 * Copyright 2010 Matt Crinklaw-Vogt
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

package com.tantaman.commons.lang;

public class DelegatingHashCodeAndEquals {
	private final Object mDelegate;
	
	public DelegatingHashCodeAndEquals(Object pDelegate) {
		mDelegate = pDelegate;
	}
	
	protected Object getDelegate() {
		return mDelegate;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DelegatingHashCodeAndEquals) {
			obj = ((DelegatingHashCodeAndEquals)obj).getDelegate();
		}
		
		return mDelegate.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return mDelegate.hashCode();
	}
	
	@Override
	public String toString() {
		return mDelegate.toString();
	}
}
