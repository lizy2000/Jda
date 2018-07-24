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
package org.jmin.jda.impl.statement;

import java.util.Properties;

import org.jmin.jda.statement.SqlPropertyTable;

/**
 * SQL定义的一些属性映射
 * 
 * @author Chris Liao
 */
public class SqlPropertyCenter implements SqlPropertyTable{
	
	/**
	 * 是否初始过
	 */
	private boolean inited;
	
	/**
	 * 属性映射列表
	 */
	private Properties properties = null;
	
	/**
	 * 设置属性映射列表
	 */
	public SqlPropertyCenter(){
		this.properties = new Properties();
	}
	
	/**
	 * 设置属性映射列表
	 */
	public SqlPropertyCenter(Properties properties){
		this.properties = new Properties();
		if(properties !=null)
			this.properties.putAll(properties);
	}

	/**
	 * 设置已经初始化
	 */
	void setAsInited() {
		this.inited = true;
	}

	/**
	 * 设置属性值
	 */
	public void putValue(String name,String value){
		if(!inited && properties!=null)properties.put(name,value);
	}
	
	/**
	 * 设置属性值
	 */
	public void removeValue(String name){
		if(!inited && properties!=null)properties.remove(name);
	}
	
	/**
	 * 获得属性值
	 */
	public String getValue(String name){
		return (properties==null)?null:properties.getProperty(name);
	}
	
	/**
	 * 获得属性值名字
	 */
	public String[] getNames(){
		return (properties==null)?new String[0]:(String[])properties.keySet().toArray(new String[properties.size()]);
	}
}
