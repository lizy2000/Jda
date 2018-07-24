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
 * 缓存类型
 * 
 * @author Liao
 */
public final class JdaCacheType {
		
	/**
	 * 类型名称
	 */
	private String typeName;
	
	/**
	 * 引用类型
	 */
	private JdaCacheRefType refenceType;

	/**
	 * 构造函数
	 */
	JdaCacheType(String typeName){
		this.typeName = typeName;
	}
	
	/**
	 * 类型名称
	 */
	public String getTypeName() {
		return typeName;
	}
	
	/**
	 * 引用类型
	 */
	public void setRefenceType(JdaCacheRefType refenceType) {
		this.refenceType = refenceType;
	}

	/**
	 * 引用类型
	 */
	public JdaCacheRefType getRefenceType() {
		return refenceType;
	}
	
 /**
  * hashCode
  */
  public int hashCode(){
  	return typeName.hashCode();
  }
	
	/**
	 * 重新方法
	 */
	public boolean equals(Object obj) {
		if (obj instanceof JdaCacheType) {
			JdaCacheType other = (JdaCacheType) obj;
			return this.typeName.equals(other.typeName);
		} else {
			return false;
		}
	}
	
	public static final JdaCacheType LRU = new JdaCacheType("LRU");
	
	public static final JdaCacheType FIFO = new JdaCacheType("FIFO");
	
	public static final JdaCacheType MEMORY = new JdaCacheType("MEMORY");
	
	public static final JdaCacheType OSCACHE = new JdaCacheType("OSCACHE");

	/**
	 * 缓存类型
	 */
	public static JdaCacheType getJdaCacheType(String typeName){
		if(LRU.typeName.equalsIgnoreCase(typeName)){
			return LRU;
		}else if(FIFO.typeName.equalsIgnoreCase(typeName)){
			return FIFO;
		}else if(MEMORY.typeName.equalsIgnoreCase(typeName)){
			return MEMORY;
		}else if(OSCACHE.typeName.equalsIgnoreCase(typeName)){
			return OSCACHE;
		}else{
			return null;
		}
	}
}
