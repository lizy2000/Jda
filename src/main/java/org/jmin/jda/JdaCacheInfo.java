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
package org.jmin.jda;

/**
 * 缓存定义
 * 
 * @author Liao
 */
public final class JdaCacheInfo {
	
	/**
	 * 缓存大小
	 */
	private int size;
	
	/**
	 * 缓存类型
	 */
	private JdaCacheType type;
	
	/**
	 * 是否只读
	 */
	private boolean readOnly;
	
	/**
	 * 是否需要序列化
	 */
	private boolean serialize;
	
	/**
	 * 清理间隔时长
	 */
	private long flushInterval;
	
	/**
	 * 缓存自定义类型
	 */
	private Class cacheImplementClass;
	
	/**
	 * 构造函数
	 */
	public JdaCacheInfo(JdaCacheType type,int size){
		this.type =type;
		this.size =size;
		this.flushInterval=30*60*1000;//默认30分钟清理一次
	}
	
	/**
	 * 构造函数
	 */
	public JdaCacheInfo(Class cacheImplementClass,int size){
		this.size =size;
		this.cacheImplementClass =cacheImplementClass;
		this.flushInterval=60*60*1000;//默认一小时清理一次
	}
	
	/**
	 * 缓存大小
	 */
	public int getSize() {
		return size;
	}	
	
	/**
	 * 缓存类型
	 */
	public JdaCacheType getType() {
		return type;
	}
	
	/**
	 * 是否只读
	 */
	public boolean isReadOnly() {
		return readOnly;
	}
	
	/**
	 * 是否只读
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	/**
	 * 是否需要序列化
	 */
	public boolean isSerialize() {
		return serialize;
	}
	
	/**
	 * 是否需要序列化
	 */
	public void setSerialize(boolean serialize) {
		this.serialize = serialize;
	}

	/**
	 * 清理间隔时长
	 */
	public long getFlushInterval() {
		return flushInterval;
	}
	
	/**
	 * 清理间隔时长
	 */
	public void setFlushInterval(long flushInterval) {
		this.flushInterval = flushInterval;
	}
	
	/**
	 * 缓存自定义类型
	 */
	public Class getCacheImplementClass() {
		return cacheImplementClass;
	}
}
