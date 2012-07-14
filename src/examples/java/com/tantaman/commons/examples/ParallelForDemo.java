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

package com.tantaman.commons.examples;

import java.util.Collection;
import java.util.LinkedList;

import org.debian.alioth.shootout.u32.nbody.NBodySystem;

import com.tantaman.commons.concurrent.Parallel;

public class ParallelForDemo {
	public static void main(String[] args) {
		Collection<Integer> elems = new LinkedList<Integer>();
		for (int i = 0; i < 40; ++i) {
			elems.add(i*55000 + 100);
		}
		
		Parallel.For(elems, new Parallel.Operation<Integer>() {
			public void perform(Integer pParameter) {
				// do something with the parameter
			};
		});
		
		Parallel.Operation<Integer> bodiesOp = new Parallel.Operation<Integer>() {
			@Override
			public void perform(Integer pParameter) {
				NBodySystem bodies = new NBodySystem();
				for (int i = 0; i < pParameter; ++i)
					bodies.advance(0.01);
			}
		};
		
		System.out.println("RUNNING THE Parallel.For vs Parallel.ForFJ performance comparison");
		System.out.println("This could take a while.");
		// warm up.. it really does have a large impact.
		Parallel.ForFJ(elems, bodiesOp);
		
		long start = System.currentTimeMillis();
		Parallel.For(elems, bodiesOp);
		long stop = System.currentTimeMillis();
		
		System.out.println("DELTA TIME VIA NORMAL PARALLEL FOR: " + (stop - start));
		
		start = System.currentTimeMillis();
		Parallel.ForFJ(elems, bodiesOp);
		stop = System.currentTimeMillis();
		
		System.out.println("DELTA TIME VIA FOR FORK JOIN: " + (stop - start));
		
		System.out.println("Finished");
	}
}

