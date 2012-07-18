package com.tantaman.commons.listeners;

public interface IEventSource<T> {
	public void addListener(T listener);
	public void removeListener(T listener);
}
