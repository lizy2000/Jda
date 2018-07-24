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
package org.jmin.jda.impl.cache;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReadWriteLock;

import org.jmin.jda.JdaCache;
import org.jmin.jda.JdaCacheInfo;
import org.jmin.jda.JdaCacheRefType;
import org.jmin.jda.JdaCacheType;
import org.jmin.jda.impl.cache.impl.FifoCache;
import org.jmin.jda.impl.cache.impl.LruCache;
import org.jmin.jda.impl.cache.impl.MemCache;
import org.jmin.jda.impl.cache.impl.OSCache;
import org.jmin.jda.impl.exception.ObjectCacheException;
import org.jmin.jda.impl.util.BeanUtil;

/**
 * 缓存中心
 * 
 * @author Chris
 */
public class CacheManager {

	/**
	 * 缓存
	 */
	private Map cacheMap;

	/**
	 * 清理任务
	 */
	private Map flushTaskMap;

	/**
	 * 清理定时器
	 */
	private Timer flushTimer;

	/**
	 * 虚拟机退出执行
	 */
	private SystemExitHook vmExitHook;

	/**
	 * 构造函数
	 */
	public CacheManager() {
		this.cacheMap = new HashMap();
		this.flushTaskMap = new HashMap();
		this.flushTimer = new Timer(true);
		this.vmExitHook = new SystemExitHook(this);
		Runtime.getRuntime().addShutdownHook(this.vmExitHook);
	}

	/**
	 * 注册Cache
	 */
	public void registerCache(Object cacheId, JdaCacheInfo cacheInfo) throws SQLException {
		if (!this.cacheMap.containsKey(cacheId)) {
			JdaCache cache = null;
			if (cacheInfo.getCacheImplementClass() != null) {
				cache = createCacheByClass(cacheInfo.getCacheImplementClass());
			} else if (JdaCacheType.LRU.equals(cacheInfo.getType())) {
				cache = new LruCache(cacheInfo.getSize());
			} else if (JdaCacheType.FIFO.equals(cacheInfo.getType())) {
				cache = new FifoCache(cacheInfo.getSize());
			} else if (JdaCacheType.MEMORY.equals(cacheInfo.getType())) {
				JdaCacheType cacheType = cacheInfo.getType();
				JdaCacheRefType refType = cacheType.getRefenceType();
				cache = new MemCache(refType);
			} else if (JdaCacheType.OSCACHE.equals(cacheInfo.getType())) {
				cache = new OSCache(cacheId, cacheInfo.getFlushInterval());
			}

			if (cache != null) {
				ClearTask task = new ClearTask(cache);
				this.cacheMap.put(cacheId, cache);
				this.flushTaskMap.put(cacheId, task);
				this.flushTimer.schedule(task, 1000, cacheInfo.getFlushInterval());
			}
		}
	}

	/**
	 * 创建缓存
	 */
	private JdaCache createCacheByClass(Class cacheClass) throws SQLException {
		try {
			return (JdaCache) BeanUtil.createInstance(cacheClass);
		} catch (Throwable e) {
			throw new ObjectCacheException("Can't create cache instance by class:" + cacheClass.getName(), e);
		}
	}

	/**
	 * 注销Cache
	 */
	public void deregisterCache(Object id) {
		cacheMap.remove(id);
		TimerTask task = (TimerTask) flushTaskMap.remove(id);
		if (task != null)
			task.cancel();
	}

	/**
	 * 清理Cache
	 */
	public void clearCache(Object id) {
		JdaCache cache = (JdaCache) cacheMap.get(id);
		if (cache != null) {
			cache.clear();
		}
	}

	/**
	 * 清理所有Cache
	 */
	public void clearAllCache() {
		Iterator itor = cacheMap.values().iterator();
		while (itor.hasNext()) {
			JdaCache cache = (JdaCache) itor.next();
			cache.clear();
		}
	}

	/**
	 * 破坏当前所有缓存
	 */
	public void destroy() {
		this.destroy2();
		Runtime.getRuntime().removeShutdownHook(this.vmExitHook);
	}

	/**
	 * 破坏当前所有缓存
	 */
	private void destroy2() {
		this.cacheMap.clear();
		this.flushTaskMap.clear();
		this.flushTimer.cancel();
	}

	/**
	 * 获得缓存对象
	 */
	public Object getObject(Object cacheId, Object key) {
		JdaCache cache = (JdaCache) cacheMap.get(cacheId);
		if (cache != null) {
			ReadWriteLock locker = cache.getReadWriteLock();
			try {
				if (locker != null)
					locker.readLock().lock();
				return cache.get(key);
			} finally {
				if (locker != null)
					locker.readLock().unlock();
			}
		} else {
			return null;
		}
	}

	/**
	 * 删除缓存对象
	 */
	public Object removeObject(Object cacheId, Object key) {
		JdaCache cache = (JdaCache) cacheMap.get(cacheId);
		if (cache != null) {
			ReadWriteLock locker = cache.getReadWriteLock();
			try {
				if (locker != null)
					locker.writeLock().lock();
				return cache.remove(key);
			} finally {
				if (locker != null)
					locker.writeLock().unlock();
			}
		} else {
			return null;
		}
	}

	/**
	 * 设置缓存对象
	 */
	public void putObject(Object cacheId, Object key, Object value) {
		JdaCache cache = (JdaCache) cacheMap.get(cacheId);
		if (cache != null) {
			ReadWriteLock locker = cache.getReadWriteLock();
			try {
				if (locker != null)
					locker.writeLock().lock();
				cache.put(key, value);
			} finally {
				if (locker != null)
					locker.writeLock().unlock();
			}
		}
	}

	/**
	 * 清理任务
	 */
	private class ClearTask extends TimerTask {

		/**
		 * 缓存中心
		 */
		private JdaCache cache;

		/**
		 * 构造函数
		 */
		public ClearTask(JdaCache cache) {
			this.cache = cache;
		}

		/**
		 * 执行清理
		 */
		public void run() {
			this.cache.clear();
		}
	}

	/**
	 * 虚拟机时执行钩子线程
	 */
	private class SystemExitHook extends Thread {

		/**
		 * 缓存管理
		 */
		private CacheManager manager;

		/**
		 * 构造函数
		 */
		public SystemExitHook(CacheManager manager) {
			this.manager = manager;
		}

		/**
		 * 线程方法
		 */
		public void run() {
			manager.destroy2();
		}
	}
}
