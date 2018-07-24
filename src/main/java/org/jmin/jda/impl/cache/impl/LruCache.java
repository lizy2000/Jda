/*
 * Copyright (C) Chris Liao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jmin.jda.impl.cache.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.jmin.jda.JdaCache;

/**
 * 最少访问删除缓存
 * 
 * @author Chris Liao
 */

public class LruCache implements JdaCache {

	/**
	 * read write Lock
	 */
	private ReadWriteLock locker;
	
	/**
	 * store the keys and values
	 */
	private LinkedHashMap objectMap;

	/**
	 * 构造函数
	 */
	public LruCache(final int maxSize) {
		this.locker = new ReentrantReadWriteLock();
		this.objectMap = new LinkedHashMap(maxSize, 0.75F, true) {
			protected boolean removeEldestEntry(Map.Entry eldest) {
				return size() > maxSize;
			}
		};
	}

	/**
	 * 获得读写锁
	 */
	public ReadWriteLock getReadWriteLock() {
		return this.locker;
	}

	/**
	 * remove element
	 */
	public Object remove(Object key) {
		return this.objectMap.remove(key);
	}

	/**
	 * get element
	 */
	public Object get(Object key) {
		return this.objectMap.get(key);
	}

	/**
	 * put element
	 */
	public void put(Object key, Object value) {
		this.objectMap.put(key, value);
	}

	/**
	 * clear
	 */
	public void clear() {
		this.objectMap.clear();
	}
}
