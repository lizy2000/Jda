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
package org.jmin.jda.impl.transaction;

import org.jmin.jda.impl.cache.CacheKey;

/**
 * 事务处理过程中,二级缓存操作描述
 * 
 * @author Chris Liao
 */

public class TransactionCaheOperation {

	/**
	 * 是否缓存清理
	 */
	private boolean clearInd;

	/**
	 * 清理缓存ID
	 */
	private String flushCacheId;

	/**
	 * 缓存ID
	 */
	private String cacheId;

	/**
	 * 缓存Key
	 */
	private CacheKey cacheKey;

	/**
	 * 缓存值
	 */
	private Object cacheValue;

	/**
	 * 构造函数
	 */
	public TransactionCaheOperation(String flushCacheId) {
		this.clearInd = true;
		this.flushCacheId = flushCacheId;
	}

	/**
	 * 构造函数
	 */
	public TransactionCaheOperation(String cacheId, CacheKey cacheKey, Object cacheValue) {
		this.clearInd = false;
		this.cacheId = cacheId;
		this.cacheKey = cacheKey;
		this.cacheValue = cacheValue;
	}

	/**
	 * 是否缓存清理
	 */
	public boolean isClearInd() {
		return clearInd;
	}

	/**
	 * 清理缓存ID
	 */
	public String getFlushCacheId() {
		return flushCacheId;
	}

	/**
	 * 缓存ID
	 */
	public String getCacheId() {
		return cacheId;
	}

	/**
	 * 缓存Key
	 */
	public CacheKey getCacheKey() {
		return cacheKey;
	}

	/**
	 * 缓存值
	 */
	public Object getCacheValue() {
		return cacheValue;
	}

	/**
	 * 重写HashCode
	 */
	public int hashCode() {
		if (clearInd) {
			return this.flushCacheId.hashCode();
		} else {
			return (cacheId.hashCode() * cacheKey.hashCode());
		}
	}

	/**
	 * 重写equals
	 */
	public boolean equals(Object obj) {
		if (obj instanceof TransactionCaheOperation) {
			TransactionCaheOperation other = (TransactionCaheOperation) obj;
			if (this.isClearInd() == other.isClearInd()) {
				if (clearInd) {
					return this.flushCacheId.equals(other.flushCacheId);
				} else {
					return this.cacheId.equals(other.cacheId) && this.cacheKey.equals(other.cacheKey);
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
