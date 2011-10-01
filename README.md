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
