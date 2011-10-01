/*
*   Copyright 2011 Tantaman LLC
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

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.tantaman.commons.concurrent.NamedThreadFactory;

/**
 * {@link ThreadPoolExecutor} that returns futures that
 * can be observed for completion. 
 * @author tantaman
 *
 */
public class ObservableFuturesThreadPool extends ThreadPoolExecutor {
	private final ExecutorService notificationService = 
		Executors
			.newFixedThreadPool(1,
					new NamedThreadFactory(
							ObservableFuturesThreadPool.class.getSimpleName() + "-NotificationService", true));
	
	public ObservableFuturesThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	public ObservableFuturesThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
	}

	public ObservableFuturesThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory);
	}

	public ObservableFuturesThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
			RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory, handler);
	}
	
	
	
	@Override
	public <T> ObservableFuture<T> submit(Callable<T> task) {
		ObservableFutureImpl<T> observableFuture = new ObservableFutureImpl<T>();
		Callable<T> wrappedCallable = new CallableWrapper<T>(task, observableFuture);
		Future<T> future = super.submit(wrappedCallable);
		
		observableFuture.setDelegate(future);
		return observableFuture;
	}
	
	@Override
	public ObservableFuture<?> submit(Runnable task) {
		ObservableFutureImpl observableFuture = new ObservableFutureImpl();
		Runnable wrappedRunnable = new RunnableWrapper(task, observableFuture);
		Future<?> future = super.submit(wrappedRunnable);
		
		observableFuture.setDelegate(future);
		return observableFuture;
	}
	
	public <T extends Object> ObservableFuture<T> submit(Runnable task, T result) {
		ObservableFutureImpl<T> observableFuture = new ObservableFutureImpl<T>();
		Runnable wrappedRunnable = new RunnableWrapper(task, observableFuture);
		Future<T> future = super.submit(wrappedRunnable, result);
		
		observableFuture.setDelegate(future);
		return observableFuture;
	}
	
	private class CallableWrapper<V> implements Callable<V> {
		private final Callable<V> delegate;
		private final ObservableFutureImpl<V> future;
		
		public CallableWrapper(Callable<V> delegate, ObservableFutureImpl<V> future) {
			this.delegate = delegate;
			this.future = future;
		}
		
		@Override
		public V call() throws Exception {
			final V val = delegate.call();
			// Has to be put on a new thread since executionCompleted will need to call get
			// on the actual future which won't complete until this method exits.
			notificationService.execute(new Runnable() {
				@Override
				public void run() {
					future.executionCompleted();
				}
			});
			return val;
		}
	}
	
	private class RunnableWrapper implements Runnable {
		private final Runnable delegate;
		private final ObservableFutureImpl<?> future;
		
		public RunnableWrapper(Runnable delegate, ObservableFutureImpl<?> future) {
			this.delegate = delegate;
			this.future = future;
		}
		
		@Override
		public void run() {
			delegate.run();
			// Has to be put on a new thread since executionCompleted will need to call get
			// on the actual future which won't complete until this method exits.
			notificationService.execute(new Runnable() {
				@Override
				public void run() {
					future.executionCompleted();
				}
			});
		}
	}
	
	private static class ObservableFutureImpl<V> implements ObservableFuture<V> {
		private volatile Future<V> delegate;
		private final Set<ObservableFuture.Observer<V>> listeners;
		
		public ObservableFutureImpl() {
			listeners = new CopyOnWriteArraySet<ObservableFuture.Observer<V>>();
		}
		
		public void setDelegate(Future<V> delegate) {
			this.delegate = delegate;
		}
		
		public void executionCompleted() {
			Exception ex = null;
			V val = null;
			try {
				val = delegate.get();
			} catch (InterruptedException e) {
				ex = e;
				e.printStackTrace();
			} catch (ExecutionException e) {
				ex = e;
				e.printStackTrace();
			}
			
			notifyListeners(val, ex);
		}
		
		@Override
		public void addObserver(ObservableFuture.Observer<V> listener) {
			if (delegate.isDone()) {
				Exception ex = null;
				V val = null;
				try {
					val = delegate.get();
				} catch (InterruptedException e) {
					ex = e;
					e.printStackTrace();
				} catch (ExecutionException e) {
					ex = e;
					e.printStackTrace();
				}
				notifyListeners(val, ex); // TODO: check and make sure we always do this on notif thread??
			}
			
			listeners.add(listener);
		}
		
		@Override
		public void removeObserver(ObservableFuture.Observer<V> listener) {	
			listeners.remove(listener);
		}
		
		private void notifyListeners(V val, Exception e) {
			for (ObservableFuture.Observer<V> listener : listeners) {
				listener.taskCompleted(val);
			}
		}
		
		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return delegate.cancel(mayInterruptIfRunning);
		}
		
		@Override
		public V get() throws InterruptedException, ExecutionException {
			return delegate.get();
		}
		
		@Override
		public V get(long timeout, TimeUnit unit) throws InterruptedException,
				ExecutionException, TimeoutException {
			return delegate.get(timeout, unit);
		}
		
		@Override
		public boolean isCancelled() {
			return delegate.isCancelled();
		}
		
		@Override
		public boolean isDone() {
			return delegate.isDone();
		}
	}
}
