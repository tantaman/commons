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


public class ObjectUtils {
	public static Class<?> [] getTypes(Object [] objects) {
		Class<?> [] types = new Class [objects.length];
		
		for (int i = 0; i < types.length; ++i) {
			types[i] = objects[i].getClass();
		}
		
		return types;
	}
	
	public static Class<?> [] getTypes(Object [] objects, Class<?> klass) {
		Class<?> [] types;
		if (klass != null) {
			types = new Class [objects.length + 1];
			types[objects.length] = klass;
		} else {
			types = new Class [objects.length];
		}
		
		
		for (int i = 0; i < objects.length; ++i) {
			types[i] = objects[i].getClass();
		}
		
		return types;
	}
	
	public static Object createNullInstanceOf(Class<?> klass) throws InstantiationException, IllegalAccessException {
		if (klass.isPrimitive()) {
			if (klass == void.class) {
				return null;
			} else if (klass == boolean.class) {
				return Boolean.FALSE;
			} else if (klass == char.class) {
				return Character.valueOf((char) 0);
			} else if (klass == long.class) {
				return Long.valueOf(0);
			} else if (klass == int.class) {
				return Integer.valueOf(0);
			} else if (klass == byte.class) {
				return Byte.valueOf((byte)0);
			} else if (klass == double.class) {
				return Double.valueOf(0);
			} else if (klass == float.class) {
				return Float.valueOf(0);
			} else if (klass == short.class) {
				return Short.valueOf((short)0);
			}
			
			return klass.newInstance();
		}
		
		return null;
	}
}
