/*
*   Copyright 2010 Matthew Crinklaw-Vogt
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
*   limitations under the License.
*/

package com.tantaman.commons.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class NamedThreadFactory implements ThreadFactory {
	private static final AtomicLong THREAD_POOL_NUM = new AtomicLong(0);
	private final AtomicLong mThreadNum = new AtomicLong(0);
	private final String mPrefix;
	private final boolean mIsDaemon;
	
	public NamedThreadFactory(String pPrefix) {
		this(pPrefix, true);
		THREAD_POOL_NUM.incrementAndGet();
	}
	
	public NamedThreadFactory(String pPrefix, boolean pIsDaemon) {
		mIsDaemon = pIsDaemon;
		mPrefix = pPrefix;
	}
	
	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r, mPrefix + "-" + THREAD_POOL_NUM.get() + "-Thread-" + mThreadNum.incrementAndGet());
		if (t.isDaemon() != mIsDaemon)
			t.setDaemon(mIsDaemon);
		
		return t;
	}
}
