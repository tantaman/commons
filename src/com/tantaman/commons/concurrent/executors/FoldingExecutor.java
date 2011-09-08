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

package com.tantaman.commons.concurrent.executors;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An executor that folds duplicate task submissions.
 * 
 * If a task is submitted while an equivalent
 * task is waiting in the queue,
 * only one of those tasks will be run.
 * 
 * If the constructor parameter pThrowAway is set to true,
 * submitted tasks that are equivalent to an already
 * running or queued task will be thrown out.
 * 
 * If pThrowAway is false, submitted tasks
 * that are equivalent to a currently queued task 
 * will replace that queued task.
 * 
 * Example:
 * 
 * FoldingExecutor with pThrowAway set to FALSE
 * Running Tasks:
 * A1 B1 C1
 * Queued Tasks:
 * D1 E1 F1
 * 
 * New submission:
 * task D2
 * 
 * since D2 is equivalent to D1 (determined via hashCode and equals)
 * D1 will be removed from the queue and replace by D2.
 * 
 * The resulting queue will look like:
 * Queued Tasks:
 * D2 E1 F1
 * 
 * Note: running tasks can not be replaced.  So submitting
 * A2 while A1 is running will just add A2 to the queue.
 * 
 * FoldingExecutor with pThrowAway set to TRUE
 * If pThrowAway is true, new tasks are thrown away
 * if an equivalent task is queued or running.
 * 
 * 
 * @author Matt Crinklaw-Vogt
 *
 */
public class FoldingExecutor implements ExecutorService {
	private final ExecutorService mExecutor;
	private final Map<QueueEntry<?>, QueueEntry<?>> mTasks;
	private final boolean mThrowAway;
	
	/**
	 * 
	 * @param pExecutor Executor to use
	 * @param pThrowAway true : throw away duplicate task submissions.
	 * false : replace duplicate tasks with the latest task to be submitted.
	 */
	public FoldingExecutor(ExecutorService pExecutor, boolean pThrowAway) {
		mExecutor = pExecutor;
		mTasks = new HashMap<QueueEntry<?>, QueueEntry<?>>();
		mThrowAway = pThrowAway;
	}
	
	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		return mExecutor.awaitTermination(timeout, unit);
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
			throws InterruptedException {
		List<Future<T>> futures = new LinkedList<Future<T>>();
		boolean done = false;

		try {
            for (Callable<T> t : tasks) {
            	futures.add(submit(t));
            }
            for (Future<T> f : futures) {
                if (!f.isDone()) {
                    try {
                        f.get();
                    } catch (CancellationException ignore) {
                    } catch (ExecutionException ignore) {
                    }
                }
            }
            done = true;
            return futures;
        } finally {
            if (!done)
                for (Future<T> f : futures)
                    f.cancel(true);
        }
	}

	@Override
	public <T> List<Future<T>> invokeAll(
			Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException {
		return null;
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
			throws InterruptedException, ExecutionException {
		return null;
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
			long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		return null;
	}

	@Override
	public boolean isShutdown() {
		return mExecutor.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return mExecutor.isTerminated();
	}

	@Override
	public void shutdown() {
		mExecutor.shutdown();
	}

	@Override
	public List<Runnable> shutdownNow() {
		return mExecutor.shutdownNow();
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		QueueEntry<?> entry = mTasks.get(new BaseQueueEntry<T>(task));
		Future<T> toReturn = null;
		
		boolean replaced = false;
		if (entry != null) {
			replaced = entry.replaceTask(task);
		}
		
		if (replaced) {
			toReturn = (Future<T>)entry.getFuture();
		} else {
			synchronized (this) {
				QueueEntry<T> queueEntry = new QueueEntry<T>(task);
				toReturn = mExecutor.submit(queueEntry);
				queueEntry.setFuture(toReturn);
				
				mTasks.put(queueEntry, queueEntry);
			}
		}

		return toReturn;
	}

	@Override
	public Future<?> submit(Runnable task) {
		RunnableAsCallable<Void> callableTask = new RunnableAsCallable<Void>(task);
		return submit(callableTask);
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		RunnableAsCallable<T> callableTask = new RunnableAsCallable<T>(task);
		
		return submit(callableTask);
	}

	@Override
	public void execute(Runnable command) {
		RunnableAsCallable<Void> callableTask = new RunnableAsCallable<Void>(command);
		
		submit(callableTask);
	}
	
	
	// will need to wrap the runnables in another runnable that we can replace
	// the inside of.
	private static class BaseQueueEntry<T> {
		protected final AtomicReference<Callable<?>> mTask;
		
		public BaseQueueEntry(Callable<T> pTask) {
			mTask = new AtomicReference<Callable<?>>(pTask);
		}
		
		public Object getTask() {
			Object userObj = mTask.get();
			
			if (userObj instanceof RunnableAsCallable<?>) {
				userObj = ((RunnableAsCallable<?>)userObj).getRunnable();
			}
			
			return userObj;
		}
		
		@Override
		public int hashCode() {
			return mTask.get().hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof BaseQueueEntry<?>) {
				obj = ((BaseQueueEntry<?>)obj).getTask();
			}
			
			return mTask.get().equals(obj);
		}
		
		@Override
		public String toString() {
			return mTask.toString();
		}
	}
	
	private class QueueEntry<T> extends BaseQueueEntry<T> implements Callable<T> {
		private final AtomicBoolean mRunning;
		private final AtomicReference<Future<T>> mFuture;
		
		public QueueEntry(Callable<T> pTask) {
			super(pTask);
			mRunning = new AtomicBoolean(false);
			mFuture = new AtomicReference<Future<T>>();
		}
		
		public Future<T> getFuture() {
			return mFuture.get();
		}
		
		public void setFuture(Future<T> pFuture) {
			mFuture.set(pFuture);
		}

		@Override
		public T call() throws Exception {
			synchronized (mTask) {
				mRunning.set(true);
			}
			
			if (!mThrowAway)
				mTasks.remove(this);
			// Unsafe... but is there really a way around it if we want to support callable?
			T result = (T)mTask.get().call();
			
			if (mThrowAway)
				mTasks.remove(this);
			
			return result;
		}
		
		public boolean replaceTask(Callable<?> pTask) {
			if (mThrowAway) return true;
			if (mRunning.get()) return false;
			synchronized (mTask) {
				if (mRunning.get()) return false;
				mTask.set(pTask);
				return true;
			}
		}
	}
	
	private static class RunnableAsCallable<Void> implements Callable<Void> {
		private final Runnable mRunnable;
		public RunnableAsCallable(Runnable pRunnable) {
			mRunnable = pRunnable;
		}
		
		@Override
		public Void call() throws Exception {
			mRunnable.run();
			return null;
		}
		
		public Runnable getRunnable() {
			return mRunnable;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof RunnableAsCallable<?>) {
				obj = ((RunnableAsCallable<?>)obj).getRunnable();
			}
			
			return mRunnable.equals(obj);
		}
		
		@Override
		public int hashCode() {
			return mRunnable.hashCode();
		}
		
		@Override
		public String toString() {
			return mRunnable.toString();
		}
	}
}
