/*
 * Copyright 2010 Matt Crinklaw-Vogt, Khemara Chuon
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
 * 
 * Based on the RunnableThrottler by Khemara Chuon
 */

package com.tantaman.commons.concurrent.throttler;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class InvocationCombiner<PARAM_TYPE> {
	private final ThrottleRunnable<PARAM_TYPE> mRunnable;

	public InvocationCombiner(AccumulativeRunnable<PARAM_TYPE> pRunnable,
			long pDelayTime, TimeUnit pTimeUnit, ScheduledExecutorService pExecutor) {
		mRunnable = new ThrottleRunnable<PARAM_TYPE>(
				pRunnable,
				TimeUnit.MILLISECONDS.convert(pDelayTime, pTimeUnit),
				pExecutor);
	}
	
	public InvocationCombiner(AccumulativeRunnable<PARAM_TYPE> pRunnable,
			ScheduledExecutorService pExecutor) {
		mRunnable = new ThrottleRunnable<PARAM_TYPE>(
				pRunnable, 0, pExecutor);
	}

	public void execute(PARAM_TYPE pParam) {
		mRunnable.add(pParam);
	}

	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		return mRunnable.awaitTermination(timeout, unit);
	}

	public boolean isShutdown() {
		return mRunnable.isShutdown();
	}

	public boolean isTerminated() {
		return mRunnable.isTerminated();
	}

	public void shutdown() {
		mRunnable.shutdown();
	}

	public List<Runnable> shutdownNow() {
		return mRunnable.shutdownNow();
	}
}
