/*
*   Copyright 2010 Tantaman LLC
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

	public void invoke(PARAM_TYPE pParam) {
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
