package com.tantaman.commons.collections;

public class Pair<T1, T2> {
	private final T1 first;
	private final T2 second;
	
	public Pair(T1 pFirst, T2 pSecond) {
		first = pFirst;
		second = pSecond;
	}
	
	public T1 getFirst() {
		return first;
	}
	
	public T2 getSecond() {
		return second;
	}
}
