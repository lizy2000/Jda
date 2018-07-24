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
package org.jmin.jda.impl.mapping.result;

import org.jmin.jda.JdaTypePersister;
import org.jmin.jda.mapping.ResultUnit;

/**
 * 对象中的结果映射属性
 * 
 * @author Chris
 */

public class ResultUnitImpl implements ResultUnit{
	
	/**
	 *是否初始化过
	 */
	private boolean inited;
	
	/**
	 * 所属于的映射map
	 */
  private Object mapOwner=null;
	
	/**
	 * 属性名
	 */
	private String propertyName=null;
	
	/**
	 * 属性类型
	 */
	private Class propertyType=null;
	
	/**
	* 映射的列名
	*/
	private String resultColumnName=null;
	
	/**
	 * 列类型名称
	 */
	private String resultColumnTypeName=null;
	
	/**
	 * 结果转换器
	 */
	private JdaTypePersister resultConverter=null;
	
	/**
	* 构造函数
	*/
	public ResultUnitImpl(String propertyName){
		this(propertyName,(Class)null);
	}
	
	/**
	* 构造函数
	*/
	public ResultUnitImpl(String propertyName,Class propertyType){
		this.propertyType = propertyType;
		if(propertyName!=null){
		 this.propertyName = propertyName.trim();
		 this.resultColumnName = this.propertyName.trim().toUpperCase();
		}
	}
	
	/**
	 *是否初始化过
	 */
	public boolean isInited() {
		return inited;
	}
	
	/**
	 *是否初始化过
	 */
	public void setInited(boolean inited) {
		this.inited = inited;
	}
	
	/**
	 * 所属于的映射map
	 */
	public Object getMapOwner() {
		return mapOwner;
	}

	/**
	 * 所属于的映射map
	 */
	public void setMapOwner(Object mapOwner) {
		this.mapOwner = mapOwner;
	}

	/**
	 * 获得属性名
	 */
	public String getPropertyName(){ 
		return this.propertyName;
	}
	
	/**
	 * 获得属性类别
	 */
	public Class getPropertyType(){ 
		return this.propertyType;
	}
	
	/**
	 * 设置属性类别
	 */
	public void setPropertyType(Class propertyType){ 
	  if(!this.isInited())
		  this.propertyType = propertyType;
	}
	
	/**
	 * 获得属性类别
	 */
	public String getResultColumnName(){ 
		return resultColumnName;
	} 
	
	/**
	 * 设置属性类别
	 */
	public void setResultColumnName(String fieldName){
	 if(!this.isInited() && fieldName!=null)
		this.resultColumnName=fieldName.trim().toUpperCase();
	} 
	
	/**
	 * 返回映射列的类型
	 */
	public String getResultColumnTypeName(){
		return this.resultColumnTypeName;
	}

	/**
	 * 设置映射列的类型
	 */
	public void setResultColumnTypeName(String name){
	 if(!this.isInited())
		 this.resultColumnTypeName = name;
	}

	/**
	 * 获得结果映射
	 */
	public JdaTypePersister getJdbcTypePersister(){
	  return this.resultConverter;
	}
	
	/**
	 * 设置结果映射
	 */
	public void setJdbcTypePersister(JdaTypePersister converter){
		if(!this.isInited())
			this.resultConverter = converter;
	}
}
