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
package org.jmin.jda.statement;

/**
 * 动态参数块SQL信息类
 * 
 * @author chris
 */
public final class DynParamUnit{
	/**
	 * 参数块SQL
	 */
	private String blockSQL;
	
	/**
	 * 参数内部
	 */
	private String blockContent;
	
	/**
	 * 构造函数
	 */
	public DynParamUnit(String blockSQL,String blockContent){
		this.blockSQL =blockSQL;
		this.blockContent =blockContent;
	}
	
	public String getBlockSQL() {
		return blockSQL;
	}

	public String getBlockContent() {
		return blockContent;
	}
	
	/**
	 * Override hashCode
	 */
	public int hashCode(){
		return this.blockSQL.hashCode();
	}
	
	public boolean equals(Object ojb){
		if(ojb instanceof DynParamUnit){
			DynParamUnit other =(DynParamUnit)ojb;
			return this.blockSQL.equals(other.blockSQL);
		}else{
			return false;
		}
	}
}