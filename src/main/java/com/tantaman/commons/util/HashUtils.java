/*
*   Copyright 2010 Matthew Crinklaw-Vogt
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
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
