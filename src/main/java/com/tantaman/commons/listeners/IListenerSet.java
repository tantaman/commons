package com.tantaman.commons.listeners;

public interface IListenerSet<T> extends  Iterable<T> {

	public abstract void add(T listener);
	public abstract void remove(T listener);
	public abstract void clear();

	public abstract int size();

}