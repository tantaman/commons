tantaman.commons
================

*Useful Java libraries mostly pertaining to concurrency.*

Some highlights:
----------------

**FoldingExecutor**

This executor is designed to ease the handling of duplicate task submissions.

There are two modes of operation:

1. If a task is submitted that is equivalent to an already queued task, then the queued
task is replaced by the new submission.
2. If a task is submitted that is equivalent to an already queued OR running task, then the
new task is ignored.

*Duplicate tasks are determined by evaluating the hashCode and equals methods on the 
callables or runnables submitted to the executor.*


A good use case for this is handling user generated events that cause a costly update to 
a GUI.  E.g., a user spamming a "update image" button.  Only the last update is relevant
and you don't want to keep the user waiting while an entire queue of events is processed.

**ObservableFuturesThreadPool**

A thread pool that returns futures which can be observed for completion.  No more calling
get and blocking or making custom runnables to notify something.



**InvocationCombiner**

Pretty similar to FoldingExecutor but there are some important differences.  The InvocationCombiner takes an optional duration over which invocations are combined.  The InvocationCombiner combines values, not runnables/callables.  The combined values are passed as a list to the actual runnable that is processing tasks.


Examples
--------

**FoldingExecutor**


    package com.tantaman.commons.examples;

    import java.util.concurrent.ExecutorService;
    import java.util.concurrent.Executors;
    import java.util.concurrent.TimeUnit;

    import com.tantaman.commons.concurrent.executors.FoldingExecutor;

    public class FoldingExecutorDemo {
	private static class Task implements Runnable {
		private final String message;
		public Task(String message) {
			this.message = message;
		}
		
		// hash code and equals that identify
		// all tasks of this Class as being duplicate tasks.
		@Override
		public int hashCode() {
			return getClass().hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			return obj.getClass() == getClass();
		}
		
		@Override
		public void run() {
			System.out.println(message);
			try {
				// sleep for demonstration
				// ensures that tasks stay running so a queue will be
				// built up and operated on by the folding executor
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		// exec that replaces queued tasks with duplicate submitted tasks
		ExecutorService exec = new FoldingExecutor(Executors.newFixedThreadPool(1), false);
		
		String [] messages = new String [] { "we", "only", "care", "about", "the", "last", "of", "us" };
		
		// we might get 2 messages output because replace can't replace running tasks, it
		// can only replace queued tasks
		System.out.println("REPLACE");
		for (String message : messages) {
			exec.execute(new Task(message));
		}
		
		exec.shutdown();
		exec.awaitTermination(200, TimeUnit.MILLISECONDS);
		
		// exec that throws away duplicate tasks
		exec = new FoldingExecutor(Executors.newFixedThreadPool(1), true);
		messages = new String [] { "we", "only", "care", "about", "the", "first", "of", "us" };
		
		System.out.println("THROW AWAY");
                // we'll only see the first message output "we"
		for (String message : messages) {
			exec.execute(new Task(message));
		}
		
		exec.shutdown();
		exec.awaitTermination(200, TimeUnit.MILLISECONDS);
	}
  }
  
**Output**
    REPLACE
    **whichever string got to the running state before being replaced, could be none of them**
    us
    THROW AWAY
    we

**ObservableFuturesThreadPool**
    ObservableFuturesThreadPool pool = 
		new ObservableFuturesThreadPool(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
		
		ObservableFuture<Integer> f = pool.submit(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return 5;
			}
		});
		
		f.addObserver(new Observer<Integer>() {
			@Override
			public void taskCompleted(Integer result) {
				System.out.println("Task completed with result: " + result);
			}
		});

**Output**
    Task completed with result: 5

**InvocationCombiner**

    package com.tantaman.commons.examples;

    import java.util.LinkedList;
    import java.util.concurrent.Executors;
    import java.util.concurrent.TimeUnit;

    import com.tantaman.commons.concurrent.throttler.AccumulativeRunnable;
    import com.tantaman.commons.concurrent.throttler.InvocationCombiner;

    public class InvocationCombinerDemo {
	private static int counter = 0;
	public static void main(String[] args) {
		InvocationCombiner<String> combiner = new InvocationCombiner<String>(
				new AccumulativeRunnable<String>() {
					public void run(LinkedList<String> pParams) {
						System.out.println("RUNNING FOR THE " + (++counter) + " TIME!");
						for (String s : pParams) {
							System.out.println(s);
						}
					};
				}, 50, TimeUnit.MILLISECONDS, Executors.newScheduledThreadPool(1));
		
		String [] messages = "combining all invocations over a 50 millisecond period".split(" ");
		
		// we could get at most 2 runs b/c the first submission is dispatched immediately,
		// and then the 50 ms timer kicks in.
		// TODO: add an initial delay option.
		for (String message : messages) {
			combiner.invoke(message);
		}
	}
}

**Output:**
    RUNNING FOR THE 1 TIME!
    combining
    all
    invocations
    over
    a
    50
    millisecond
    period
    
*could get 2 calls to the runnable as there is no initial delay time, only a subsequent delay time.  See comment 
above `combiner.invoke`*