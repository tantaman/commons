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
}
