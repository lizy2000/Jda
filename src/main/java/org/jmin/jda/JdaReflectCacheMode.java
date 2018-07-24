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
 * 反射缓存模式
 * 
 * @author Liao
 */
public final class JdaReflectCacheMode {

	/**
	 * 模式名称
	 */
	private String modeName;

	/**
	 * 构造函数
	 */
	JdaReflectCacheMode(String modeName){
		this.modeName = modeName;
	}
	
	/**
	 * 模式名称
	 */
	public String getModeName() {
		return modeName;
	}
	
	/**
	 * 重新方法
	 */
	public boolean equals(Object obj) {
		if (obj instanceof JdaReflectCacheMode) {
			JdaReflectCacheMode other = (JdaReflectCacheMode) obj;
			return this.modeName.equals(other.modeName);
		} else {
			return false;
		}
	}

 /**
  * hashCode
  */
  public int hashCode(){
  	return modeName.hashCode();
  }
	
	public static final JdaReflectCacheMode SHARE_MODE = new JdaReflectCacheMode("SHARE");
	
	public static final JdaReflectCacheMode PRIVATE_MODE = new JdaReflectCacheMode("PRIVATE");

	/**
	 * 缓存类型
	 */
	public static JdaReflectCacheMode getJdaReflectCacheMode(String modeName){
		if(SHARE_MODE.modeName.equalsIgnoreCase(modeName)){
			return SHARE_MODE;
		}else if(PRIVATE_MODE.modeName.equalsIgnoreCase(modeName)){
			return PRIVATE_MODE;
		}else{
			return null;
		}
	}
}
