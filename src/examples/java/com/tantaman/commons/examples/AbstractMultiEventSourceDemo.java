package com.tantaman.commons.examples;

import java.awt.Color;

import com.tantaman.commons.listeners.AbstractMultiEventSource;

public class AbstractMultiEventSourceDemo {
	public static void main(String[] args) {
		SomeModel m = new SomeModel();
		
		m.addListener(new StructureListener() {
			
			@Override
			public void widthChanged(int w) {
				System.out.println("Got width change: " + w);
			}
			
			@Override
			public void heightChanged(int h) {
				System.out.println("Got height change: " + h);
			}
		});
		
		m.addListener(new StyleListener() {
			
			@Override
			public void colorChanged(Color c) {
				System.out.println("Got color change: " + c);
			}
			
			@Override
			public void backgroundChanged(Color c) {
				System.out.println("Got bg change: " + c);
			}
		});
		
		m.setWidth(20);
		m.setHeight(30);
		m.setBackground(Color.black);
		m.setColor(Color.white);
	}
	
	private static class SomeModel extends AbstractMultiEventSource {
		public SomeModel() {
			super(true, StructureListener.class, StyleListener.class);
		}
		
		public void setWidth(int w) {
			((StructureListener)emitter.emit).widthChanged(w);
		}
		
		public void setHeight(int h) {
			((StructureListener)emitter.emit).heightChanged(h);
		}
		
		public void setColor(Color c) {
			((StyleListener)emitter.emit).colorChanged(c);
		}
		
		public void setBackground(Color c) {
			((StyleListener)emitter.emit).backgroundChanged(c);
		}
	}
	
	private static interface StructureListener {
		public void widthChanged(int w);
		public void heightChanged(int h);
	}
	
	private static interface StyleListener {
		public void colorChanged(Color c);
		public void backgroundChanged(Color c);
	}
}
