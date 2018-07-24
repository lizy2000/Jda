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

import java.util.concurrent.locks.ReadWriteLock;

import org.jmin.jda.JdaCache;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
 * OS缓存
 * 
 * @author Chris Liao
 */

public class OSCache implements JdaCache  {
	
	/**
	 * 缓存ID
	 */
	private Object cacheId;
	
	/**
	 * 清理间隔时长
	 */
	private long flushInterval;
	
	/**
	 * OS Cache
	 */
	private GeneralCacheAdministrator osCache;
	
	/**
	 * 构造函数
	 */
	public OSCache(Object cacheId,long flushInterval){
		this.cacheId =cacheId;
		this.flushInterval =flushInterval;
		this.osCache = new GeneralCacheAdministrator();
	}

	/**
	 * 获得读写锁
	 */
	public ReadWriteLock getReadWriteLock(){
    return null;
	}
	
	/**
	 * get element
	 */
	public Object get(Object key) {
		String keyString = key.toString();
		try {
			int refreshPeriod = (int) (flushInterval);
			return osCache.getFromCache(keyString, refreshPeriod);
		} catch (NeedsRefreshException e) {
			osCache.cancelUpdate(keyString);
			return null;
		}
	}
	
	/**
	 * put element
	 */
	public void put(Object key, Object value) {
		String keyString = key.toString();
	  osCache.putInCache(keyString,value, new String[]{cacheId.toString()});
	}
	
	/**
	 * remove element
	 */
	public Object remove(Object key) {
		Object result=null;
    String keyString = key.toString();
    try {
      int refreshPeriod = (int)(flushInterval);
      Object value = osCache.getFromCache(keyString, refreshPeriod);
      if (value != null) {
        osCache.flushEntry(keyString);
      }
      result = value;
    } catch (NeedsRefreshException e) {
      try {
        osCache.flushEntry(keyString);
      } finally {
        osCache.cancelUpdate(keyString);
        result = null;
      }
    }
    return result;
	}
	
	/**
	 * clear
	 */
	public void clear() {
		osCache.flushAll();
	}
}
