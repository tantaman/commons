package com.tantaman.commons.examples;

import com.tantaman.commons.gc.GCNotifier;

/**
 * The GCNotifier allows you to be notified when an object is garbage collected
 * but without using a finalizer.
 * @author tantaman
 *
 */
public class GCNotifierDemo {
	public static void main(String[] args) {
		GCNotifier notifier = new GCNotifier();
		
		Object someImportantObject = new Object();
		notifier.register(new GCNotifier.Listener() {
			@Override
			public void objectCollected() {
				// do something that is relevant to someImportantObject having been collected.
			}
		}, someImportantObject);
		
		//ReferenceQueue<T>
	}
}
