/*
 *   Copyright 2011 Tantaman LLC
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *  limitations under the License.
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
