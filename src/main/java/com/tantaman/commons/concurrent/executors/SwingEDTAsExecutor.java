package com.tantaman.commons.concurrent.executors;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.SwingUtilities;

public class SwingEDTAsExecutor implements ExecutorService {
	public static SwingEDTAsExecutor instance = new SwingEDTAsExecutor();
	@Override
	public void execute(Runnable runnable) {
		if (SwingUtilities.isEventDispatchThread()) {
			runnable.run();
		} else {
			SwingUtilities.invokeLater(runnable);
		}
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		throw new UnsupportedOperationException("The EDT won't terminate");
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
			throws InterruptedException {
		for (final Callable<T> c : tasks) {
			if (SwingUtilities.isEventDispatchThread()) {
				try {
					c.call();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							try {
								c.call();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}

	@Override
	public <T> List<Future<T>> invokeAll(
			Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
			throws InterruptedException, ExecutionException {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
			long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	@Override
	public boolean isShutdown() {
		return false;
	}

	@Override
	public boolean isTerminated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void shutdown() {
		throw new UnsupportedOperationException("Can't shut down the EDT.");
	}

	@Override
	public List<Runnable> shutdownNow() {
		throw new UnsupportedOperationException("Can't shut down the EDT.");
	}

	@Override
	public <T> Future<T> submit(final Callable<T> task) {
		if (SwingUtilities.isEventDispatchThread()) {
			try {
				task.call();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					try {
						task.call();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		
		return null;
	}

	@Override
	public Future<?> submit(final Runnable task) {
		if (SwingUtilities.isEventDispatchThread()) {
			task.run();
		} else {
			SwingUtilities.invokeLater(task);
		}
		
		return null;
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		submit(task);
		return null;
	}

}
