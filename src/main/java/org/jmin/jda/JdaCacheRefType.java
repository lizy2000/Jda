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
 * 内存管理缓存引用类型
 * 
 * @author Liao
 */
public final class JdaCacheRefType {
		
	/**
	 * 类型名称
	 */
	private String typeName;

	/**
	 * 构造函数
	 */
	JdaCacheRefType(String typeName){
		this.typeName = typeName;
	}
	
	/**
	 * 类型名称
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * 重新方法
	 */
	public boolean equals(Object obj) {
		if (obj instanceof JdaCacheRefType) {
			JdaCacheRefType other = (JdaCacheRefType) obj;
			return this.typeName.equals(other.typeName);
		} else {
			return false;
		}
	}

 /**
  * hashCode
  */
  public int hashCode(){
  	return typeName.hashCode();
  }
		
	public static final JdaCacheRefType SOFT = new JdaCacheRefType("SOFT");
	
	public static final JdaCacheRefType WEAK = new JdaCacheRefType("WEAK");

	public static final JdaCacheRefType STRONG = new JdaCacheRefType("STRONG");

	/**
	 * 缓存类型
	 */
	public static JdaCacheRefType getJdaCacheRefType(String typeName){
		if(SOFT.typeName.equalsIgnoreCase(typeName)){
			return SOFT;
		}else if(WEAK.typeName.equalsIgnoreCase(typeName)){
			return WEAK;
		}else if(STRONG.typeName.equalsIgnoreCase(typeName)){
			return STRONG;
		}else{
			return null;
		}
	}
}
