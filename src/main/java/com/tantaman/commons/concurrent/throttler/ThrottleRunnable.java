/*
 * Copyright 2010 Matt Crinklaw-Vogt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
