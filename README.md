tantaman.commons
================

*Useful Java libraries mostly pertaining to concurrency.*

building: "mvn package"

## A Tour of tantaman.commons

### tantaman.commons.collections
* **[BadAzz](https://github.com/tantaman/commons/wiki/Tour-of-BadAzz)** - Wraps a collection up in a user defined interface.  Whenever a method is called on the wrapped collection that method is applied to all members of the collection.  Similar to how jQuery functions. [More...](https://github.com/tantaman/commons/wiki/Tour-of-BadAzz)

### tantaman.commons.concurrent
* **[NamedThreadFactory](https://github.com/tantaman/commons/wiki/Tour-of-NamedThreadFactory)** - Creates and names threads with a specified name.  [More...](https://github.com/tantaman/commons/wiki/Tour-of-NamedThreadFactory)
* **[Parallel](https://github.com/tantaman/commons/wiki/Parallel-Tour)** - Provides an implementation of [C#'s Parallel.ForEach](http://msdn.microsoft.com/en-us/library/dd460720.aspx).  [More...](https://github.com/tantaman/commons/wiki/Parallel-Tour)

### tantaman.commons.concurrent.executors
* **[FoldingExecutor](https://github.com/tantaman/commons/wiki/FoldingExecutor-Tour)** - An executor that can fold or discard duplicate task submissions.  [More...](https://github.com/tantaman/commons/wiki/FoldingExecutor-Tour)
* **[ObservableFuturesThreadPool](https://github.com/tantaman/commons/wiki/ObservableFuturesThreadPool--Tour)** - A thread pool that returns futures that can be observed for completion.  [More...](https://github.com/tantaman/commons/wiki/ObservableFuturesThreadPool--Tour)

### tantaman.commons.concurrent.throttler
* **[InvocationCombiner](https://github.com/tantaman/commons/wiki/InvocationCombiner-Tour)** - A service that combines all invocations that occur within a given time threshold.  [More...](https://github.com/tantaman/commons/wiki/InvocationCombiner-Tour)
* TODO: Debouncer...

### tantaman.commons.gc
* **[GCNotifier](https://github.com/tantaman/commons/wiki/GCNotifier-Tour)** - Allows one to be notified when specific objects have been garbage collected, without using finalizers.  [More...](https://github.com/tantaman/commons/wiki/GCNotifier-Tour)

### tantaman.commons.lang
* **DelegatingHashCodeAndEquals** - Delegates hashCode and equals calls to a specified delegate.  More...
* **[ObjectUtils](https://github.com/tantaman/commons/wiki/ObjectUtils-Tour)** - Various methods for working with and creating objects.  [More...](https://github.com/tantaman/commons/wiki/ObjectUtils-Tour)

### tantaman.commons.listeners
* **[WeakListenerSet](https://github.com/tantaman/commons/wiki/WeakListenerSet-Tour)** - A set that holds listeners weakly.  [More...](https://github.com/tantaman/commons/wiki/WeakListenerSet-Tour)
* **[EventEmitter](https://github.com/tantaman/commons/wiki/EventEmitter-Tour)** - Simplified listener and event management that still retains data types.  [More...](https://github.com/tantaman/commons/wiki/EventEmitter-Tour)

### tantaman.commons.ref
* **KeyedWeakReference** - A weak reference that maintains a key.  Useful in maps.  More...
* **KeyedSoftReference** - A soft reference that maintains a key.  Useful in maps.  More...
