package com.tantaman.commons.examples;

import java.util.LinkedList;
import java.util.List;

import com.tantaman.commons.concurrent.Parallel;

public class ParallelForDemo {
	public static void main(String[] args) {
		List<Integer> elems = new LinkedList<Integer>();
		for (int i = 0; i < 20; ++i) {
			elems.add(i);
		}
		
		Parallel.For(elems, new Parallel.Operation<Integer>() {
			public void perform(Integer pParameter) {
				System.out.println(pParameter);
			};
		});
	}
}

