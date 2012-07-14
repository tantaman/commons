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

package com.tantaman.commons.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SortedList<E> implements List<E> {
	private final ArrayList<E> mList;
	private final Comparator<E> mComparator;
	
	public SortedList(Comparator<E> pComparator) {
		mList = new ArrayList<E>();
		mComparator = pComparator;
	}
	
	@Override
	public boolean add(E e) {
		int index = Collections.binarySearch(mList, e, mComparator);
		
		if (index > 0) {
			mList.add(index, e);
		} else {
			mList.add(Math.abs(index) - 1, e);
		}
		
		return true;
	}

	@Override
	public void add(int index, E element) {
		throw new UnsupportedOperationException();
	}

	// TODO: optimize for when they are adding a sorted list to this list?
	@Override
	public boolean addAll(Collection<? extends E> c) {
		Iterator<? extends E> iter = c.iterator();
		
		while (iter.hasNext()) {
			add(iter.next());
		}
		
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		mList.clear();
	}

	@Override
	public boolean contains(Object o) {
		int index = Collections.binarySearch(mList, (E)o, mComparator);
		
		return index >= 0 ? true : false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		Iterator<?> iter = c.iterator();
		
		boolean contained = false;
		while (iter.hasNext()) {
			contained = contains(iter.next());
			if (!contained) return false;
		}
		
		return true;
	}

	@Override
	public E get(int index) {
		return mList.get(index);
	}

	@Override
	public int indexOf(Object o) {
		int index = Collections.binarySearch(mList, (E)o, mComparator);
		
		return index >= 0 ? index : -1;
	}

	@Override
	public boolean isEmpty() {
		return mList.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return mList.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		int index = indexOf(o);
		
		if (index == -1) return -1;
		
		int lastIndex = index;
		for (int i = index + 1; i < mList.size(); ++i) {
			if (mList.get(i).equals(o)) {
				++lastIndex;
			} else {
				break;
			}
		}
		
		return lastIndex;
	}

	@Override
	public ListIterator<E> listIterator() {
		return mList.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return mList.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
		int index = Collections.binarySearch(mList, (E)o, mComparator);
		
		if (index >= 0)
			mList.remove(index);
		else
			return false;
		
		return true;
	}

	@Override
	public E remove(int index) {
		return mList.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		Iterator<?> iter = c.iterator();
		
		boolean removed = true;
		while (iter.hasNext()) {
			removed = removed && mList.remove(iter.next());
		}
		
		return removed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return mList.retainAll(c);
	}

	@Override
	public E set(int index, E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return mList.size();
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return mList.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return mList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return mList.toArray(a);
	}

}
