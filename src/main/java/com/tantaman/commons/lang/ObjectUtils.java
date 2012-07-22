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

import java.lang.reflect.Field;

import sun.misc.Unsafe;

import com.tantaman.commons.Fn;


public class ObjectUtils {
	
	private static volatile Unsafe unsafe;
	
	static {
		try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe)f.get(null);
		} catch (Exception e) { /* ... */ }
	}
	
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
	
	public static Field [] setFields(Object target, Object source, Fn<Boolean, Field> filter, Class<?> upperBound) {
		Class<?> klass = source.getClass();
		if (upperBound == null)
			upperBound = Object.class;
		while (klass != upperBound && klass != null) {
			setOwnFields(klass, target, source, filter, upperBound);
			klass = klass.getSuperclass();
		}
		
		return null;
	}
	
	public static Field [] setOwnFields(Class<?> klass, Object target, Object source, Fn<Boolean, Field> filter, Class<?> upperBound) {
		Field [] fields = klass.getDeclaredFields();
		for (Field field : fields) {
			// TODO: looks like we are tieing ourselves to gson here! O NOES!
			// well we are only tied to their expose annotation and nothing else at the moment.
			if (filter.fn(field)) {
				if (!field.isAccessible())
					field.setAccessible(true);
				
				Class fieldType = field.getType();
				if (fieldType.isPrimitive() || field.getType().getName().startsWith("java")) {
					try {
						// Java 1.7 re-enabled the setting of final fields via reflection so we don't have to use Unsafe.
//						if (Modifier.isFinal(field.getModifiers())) {
//							long offset = unsafe.objectFieldOffset(field);
//							if (fieldType == char.class) {
//								unsafe.putChar(target, offset, field.getChar(source));
//							} else if (fieldType == int.class) {
//								unsafe.putInt(target, offset, field.getInt(source));
//							} else if (fieldType == long.class) {
//								unsafe.putLong(target, offset, field.getLong(source));
//							} else if (fieldType == byte.class) {
//								unsafe.putByte(target, offset, field.getByte(source));
//							} else if (fieldType == float.class) {
//								unsafe.putFloat(target, offset, field.getFloat(source));
//							} else if (fieldType == double.class) {
//								unsafe.putDouble(target, offset, field.getDouble(source));
//							} else if (fieldType == boolean.class) {
//								unsafe.putBoolean(target, offset, field.getBoolean(source));
//							} else {
//								unsafe.putObject(target, offset, field.get(source));
//							}
//						} else {
							field.set(target, field.get(source));
//						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				} else {
					try {
						setFields(field.get(target), field.get(source), filter, upperBound);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return null;
	}
}
