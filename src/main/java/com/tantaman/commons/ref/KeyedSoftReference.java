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

package com.tantaman.commons.ref;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

public class KeyedSoftReference<K, V> extends SoftReference<V> implements IKeyedReference<K, V> {
	private final K mKey;
	public KeyedSoftReference(K pKey, V referent, ReferenceQueue<? super V> q) {
		super(referent, q);
		mKey = pKey;
	}

	@Override
	public K getKey() {
		return mKey;
	}
}
