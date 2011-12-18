package com.tantaman.commons.collections;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public class BadAzzTest {

	private int invocationCount;
	
	@Test
	public void testInvoke() {
		invocationCount = 0;
		List<TestInterface> list = new LinkedList<TestInterface>();
		
		list.add(new TestObject());
		list.add(new TestObject());
		list.add(new TestObject());
		list.add(new SubTestObject());
		
		TestInterface badazzList = BadAzz.create(list, TestInterface.class);
		
		badazzList.doStuff();
		
		Assert.assertEquals(5, invocationCount);
	}
	
	private static interface TestInterface {
		public void doStuff();
	}
	
	private class TestObject implements TestInterface {
		public TestObject() {
		}
		
		@Override
		public void doStuff() {
			++invocationCount;
		}
	}
	
	private class SubTestObject extends TestObject {
		@Override
		public void doStuff() {
			invocationCount += 2;
		}
	}

}
