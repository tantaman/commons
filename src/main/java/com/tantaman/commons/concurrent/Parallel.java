/*
 * Copyright 2011 Matt Crinklaw-Vogt
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

package com.tantaman.commons.concurrent;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parallel {

  private static final int NUM_CORES = Runtime.getRuntime().availableProcessors();

  private static final ForkJoinPool fjPool = new ForkJoinPool(NUM_CORES, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);

  public static <T> void blockingFor(
      final Iterable<? extends T> elements,
      final Operation<T> operation) {
    blockingFor(2 * NUM_CORES, elements, operation);
  }

  public static <T> void blockingFor(
      int numThreads,
      final Iterable<? extends T> elements,
      final Operation<T> operation) {
    For(numThreads, new NamedThreadFactory("Parallel.For"), elements, operation,
        Integer.MAX_VALUE, TimeUnit.DAYS);
  }

  public static <T> void For(
      final Iterable<? extends T> elements,
      final Operation<T> operation) {
    For(2 * NUM_CORES, elements, operation);
  }
  
  public static <T> void For(
      int numThreads,
      final Iterable<? extends T> elements,
      final Operation<T> operation) {
    For(numThreads, new NamedThreadFactory("Parallel.For"), elements, operation, null, null);
  }
  
  public static <S extends T, T> void For(
      int numThreads,
      NamedThreadFactory threadFactory,
      final Iterable<S> elements,
      final Operation<T> operation,
      Integer wait,
      TimeUnit waitUnit) {

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(numThreads, numThreads,
        0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    final ThreadSafeIterator<S> itr = new ThreadSafeIterator<S>(elements.iterator());

    for (int i = 0; i < threadPoolExecutor.getMaximumPoolSize(); i++){
      threadPoolExecutor.submit(new Callable<Void>() {
        @Override
        public Void call() {
          T element;
          while ((element = itr.next()) != null) {
            try {
              operation.perform(element);
            } catch (Exception e) {
              Logger.getLogger(Parallel.class.getName())
                  .log(Level.SEVERE, "Exception during execution of parallel task", e);
            }
          }
          return null;
        }
      });
    }

    threadPoolExecutor.shutdown();

    if (wait != null) {
      try {
        threadPoolExecutor.awaitTermination(wait, waitUnit);
      } catch (InterruptedException e) {
        throw new IllegalStateException(e);
      }
    }
  }

  private static class ThreadSafeIterator<T> {
    
    private final Iterator<T> itr;
    
    public ThreadSafeIterator(Iterator<T> itr) {
      this.itr = itr;
    }
    
    public synchronized T next() {
      return itr.hasNext() ? itr.next() : null;
    }
  }

  public static <T> void ForFJ(final Iterable<T> elements, final Operation<T> operation) {
    // TODO: is this really utilizing any fork-join capabilities since it is just an invokeAll?
    // I assume work stealing is at least going on since this is sumbitted to a fork-join pool?
    // but performance tests don't show a different between this and the old way.
   	fjPool.invokeAll(createCallables(elements, operation));
  }
    
  public static <T> Collection<Callable<Void>> createCallables(final Iterable<T> elements, final Operation<T> operation) {
    List<Callable<Void>> callables = new LinkedList<Callable<Void>>();
    for (final T elem : elements) {
      callables.add(new Callable<Void>() {

        @Override
        public Void call() {
          operation.perform(elem);
          return null;
	    }
	  });
    }
    
    return callables;
  }

  public static interface Operation<T> {
    public void perform(T pParameter);
  }

}
