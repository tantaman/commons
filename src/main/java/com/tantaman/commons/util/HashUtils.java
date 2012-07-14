/*
 * Copyright 2010 Matt Crinklaw-Vogt
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

package com.tantaman.commons.util;

import java.util.Arrays;

public class HashUtils {

	public static int createHash(Object [] pParams) {
		if (pParams == null) return 0;
		final int prime = 31;
		int result = 1;
		
		for (Object obj : pParams)
			result = prime * result + ((obj == null) ? 0 : obj.hashCode());
		
		return result;
	}
	
	public static int appendToHash(Object pObject, int pResult) {
		final int prime = 31;
		
		pResult = prime * pResult + ((pObject == null) ? 0 : pObject.hashCode());
		
		return pResult;
	}
	
	public static boolean equals(Object [] pFirstObjs, Object [] pSecondObjs) {
		return Arrays.equals(pFirstObjs, pSecondObjs);
	}
}
