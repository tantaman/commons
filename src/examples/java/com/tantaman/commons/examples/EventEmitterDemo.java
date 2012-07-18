package com.tantaman.commons.examples;

import java.util.LinkedList;
import java.util.List;

import com.tantaman.commons.listeners.AbstractEventSource;

public class EventEmitterDemo {
	public static void main(String[] args) {
		NamedList list = new NamedList();
		
		list.addListener(new Listener() {
			@Override
			public void valueAdded(int value, List<Integer> collection) {
				System.out.println("GOT A VALUE ADDED EVENT!!!");
			}
			
			@Override
			public void nameChanged(String newName) {
				System.out.println("GOT A NAME CHANGE EVENT!!!");
			}
		});
		
		list.setName("FijiWiji");
		list.addValue(1);
	}
	
	// Just some
	private static class NamedList extends AbstractEventSource<Listener> {
		private String name;
		private List<Integer> values = new LinkedList<Integer>();
		
		public NamedList() {
			super(Listener.class, false);
		}
		
		public void setName(String name) {
			this.name = name;
			emitter.emit.nameChanged(name);
		}
		
		public void addValue(int value) {
			values.add(value);
			emitter.emit.valueAdded(value, values);
		}
	}
	
	private static interface Listener {
		public void valueAdded(int value, List<Integer> collection);
		public void nameChanged(String newName);
	}
}
