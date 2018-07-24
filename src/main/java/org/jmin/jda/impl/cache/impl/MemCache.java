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

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.jmin.jda.JdaCache;
import org.jmin.jda.JdaCacheRefType;

/**
 * 内存引用缓存
 * 
 * @author Chris Liao
 */

public class MemCache implements JdaCache  {
	
	/**
	 * store the keys and values
	 */
	private Map objectMap;
	
	/**
	 * read write Lock
	 */
	private ReadWriteLock locker;
	
	/**
	 * 内存引用类型
	 */
	private JdaCacheRefType refType;
	
	/**
	 * 构造函数
	 */
	public MemCache(JdaCacheRefType refType) {
		this.refType=refType;
		this.objectMap = new HashMap();
		this.locker = new ReentrantReadWriteLock();
	}

	/**
	 * 获得读写锁
	 */
	public ReadWriteLock getReadWriteLock(){
    return this.locker;
	}

	/**
	 * get element
	 */
	public Object get(Object key) {
		try{
			Object value=null;
			locker.readLock().lock();
	    Object ref = objectMap.get(key);
	    if (ref != null) {
	      if (ref instanceof StrongReference) {
	      	value = ((StrongReference)ref).get();
	      } else if (ref instanceof SoftReference) {
	      	value = ((SoftReference)ref).get();
	      } else if (ref instanceof WeakReference) {
	      	value = ((WeakReference)ref).get();
	      }
	      if(value==null)objectMap.remove(key);
	    }
	    return value;
		}finally{
			locker.readLock().unlock();
		}
	}
	
	/**
	 * put element
	 */
	public void put(Object key, Object value) {
		try {
			locker.writeLock().lock();
			if (JdaCacheRefType.WEAK.equals(refType)) {
				this.objectMap.put(key, new WeakReference(value));
			} else if (JdaCacheRefType.SOFT.equals(refType)) {
				this.objectMap.put(key, new SoftReference(value));
			} else if (JdaCacheRefType.STRONG.equals(refType)) {
				this.objectMap.put(key, new StrongReference(value));
			}
		} finally {
			locker.writeLock().unlock();
		} 
	}
	
	/**
	 * remove element
	 */
	public Object remove(Object key) {
		try{
			locker.writeLock().lock();
		    Object ref = objectMap.remove(key);
		    if (ref != null) {
		      if (ref instanceof StrongReference) {
		      	return ((StrongReference) ref).get();
		      } else if (ref instanceof SoftReference) {
		      	return((SoftReference) ref).get();
		      } else if (ref instanceof WeakReference) {
		      	return((WeakReference) ref).get();
		      }
		    }
		    return null;
		}finally{
			locker.writeLock().unlock();
		}	
	}
	
	
	/**
	 * clear
	 */
	public void clear() {
		try{
			locker.writeLock().lock();
			this.objectMap.clear();
		}finally{
			locker.writeLock().unlock();
		}	
	}

  private class StrongReference {
    private Object object;
    public StrongReference(Object object) {
      this.object = object;
    }
    public Object get() {
      return object;
    }
  }
}
