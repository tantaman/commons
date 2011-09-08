/*
 *   Copyright 2010 Matthew Crinklaw-Vogt, Khemara Chuon
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

package com.tantaman.commons.concurrent.throttler;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThrottleRunnable<PARAM_TYPE> implements Runnable {
	private LinkedList<PARAM_TYPE> mParameters;
	private final AccumulativeRunnable<PARAM_TYPE> mAccRunnable;
	private final long mDelayTime;
	private final ScheduledExecutorService mExecutor;
	private Future<?> mFuture;

	public ThrottleRunnable(AccumulativeRunnable<PARAM_TYPE> pAccRunnable,
			long pDelayTime, ScheduledExecutorService pExecutor) {
		mExecutor = pExecutor;
		mAccRunnable = pAccRunnable;
		mParameters = new LinkedList<PARAM_TYPE>();
		mDelayTime = pDelayTime;
		mFuture = null;
	}
	
	public void run() {
		do {
			LinkedList<PARAM_TYPE> parameters;
			synchronized (this) {
				parameters = swapParameters();
				if (parameters == null) {
					cancel(false);
					return;
				}
			}

			run(parameters);
		} while (mDelayTime <= 0);
	}
	
	synchronized void cancel(boolean mayInterruptIfRunning) {
		if (mFuture != null) {
			mFuture.cancel(mayInterruptIfRunning);
			mFuture = null;
		}
	}

	synchronized void add(PARAM_TYPE pParam) {
		mParameters.add(pParam);

		if (mFuture == null) {
			if (mDelayTime <= 0) {
				mFuture = mExecutor.submit(this);
			} else {
				mFuture = mExecutor.scheduleWithFixedDelay(this, 0, mDelayTime, TimeUnit.MILLISECONDS);
			}
		}
	}
	

	void shutdown() {
		mExecutor.shutdown();
		//cancel(false);
	}
	
	List<Runnable> shutdownNow() {
		List<Runnable> runnables = mExecutor.shutdownNow();
		//cancel(false);
		return runnables;
	}

	private LinkedList<PARAM_TYPE> swapParameters() {
		LinkedList<PARAM_TYPE> parameters;
		if (mParameters.isEmpty()) {
			parameters = null;
		} else {
			parameters = mParameters;
			mParameters = new LinkedList<PARAM_TYPE>();
		}

		return parameters;
	}

	private void run(LinkedList<PARAM_TYPE> pParameters) {
		mAccRunnable.run(pParameters);
	}

	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return mExecutor.awaitTermination(timeout, unit);
	}

	public boolean isShutdown() {
		return mExecutor.isShutdown();
	}

	public boolean isTerminated() {
		return mExecutor.isTerminated();
	}
}
