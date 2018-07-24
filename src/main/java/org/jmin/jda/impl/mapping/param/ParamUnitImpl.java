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
package org.jmin.jda.impl.mapping.param;

import org.jmin.jda.JdaTypePersister;
import org.jmin.jda.impl.statement.SqlFieldTypes;
import org.jmin.jda.mapping.ParamUnit;
import org.jmin.jda.mapping.ParamValueMode;
import org.jmin.jda.mapping.ResultMap;

/**
 * 对象中的参数属性的信息,一个属性单元只能出现一次
 *
 * @author Chris
 */

public class ParamUnitImpl implements ParamUnit{

	/**
	 *是否初始化过
	 */
	private boolean inited;

	/**
	 * 映射ID
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
	 * 列类型代码
	 */
	private int paramColumnTypeCode=SqlFieldTypes.NO_SET;
	
	/**
	 * 列类型名称
	 */
	private String paramColumnTypeName=null;
	
	/**
	 * 参数持久器
	 */
	private JdaTypePersister paramPersister=null;
	
	/**
	 * 参数模式
	 */
	private ParamValueMode paramMode=null;
	
	/**
	 * 过程调用游标返回映射
	 */
	private ResultMap cursorResultMap=null;
	

	/**
	* 构造函数
	*/
	public ParamUnitImpl(String propertyName){
	 this(propertyName,null);
	}

	/**
	* 构造函数
	*/
	public ParamUnitImpl(String propertyName,Class propertyType){
	 this.propertyName = propertyName;
	 this.propertyType = propertyType;
	 this.paramMode=ParamValueMode.IN;
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
	 * 映射owner
	 */
	public Object getMapOwner() {
		return mapOwner;
	}

	/**
	 * 映射owner
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
	public void setPropertyType(Class type){
		if(!this.isInited())
			this.propertyType = type;
	}

	/**
	 * 获得参数模式
	 */
	public ParamValueMode getParamValueMode(){
    return this.paramMode;
	}

	/**
	 * 设置参数模式
	 */
	public void setParamValueMode(ParamValueMode mode){
		if(!this.isInited())
		 this.paramMode = mode;
	}

	/**
	 * 列类型代码
	 */
	public int getParamColumnTypeCode() {
		return paramColumnTypeCode;
	}

	/**
	 * 列类型代码
	 */
	public void setParamColumnTypeCode(int code) {
		if(!this.isInited())
		  this.paramColumnTypeCode = code;
	}

	/**
	 * 返回映射列的类型
	 */
	public String getParamColumnTypeName(){
		return this.paramColumnTypeName;
	}

	/**
	 * 设置映射列的类型
	 */
	public void setParamColumnTypeName(String name){
		if(!this.isInited())
		  this.paramColumnTypeName = name;
	}
	
	/**
	 * 游标结果映射
	 */
	public ResultMap getCursorResultMap(){
		return cursorResultMap;
	}

	/**
	 * 游标结果映射
	 */
	public void setCursorResultMap(ResultMap cursorResultMap){
		this.cursorResultMap=cursorResultMap;
	}
	/**
	 * 获得参数转换
	 */
	public JdaTypePersister getJdbcTypePersister(){
		return this.paramPersister;
	}

	/**
	 * 设置参数的转换
	 */
	public void setJdbcTypePersister(JdaTypePersister persister){
		if(!this.isInited())
			this.paramPersister = persister;
	}
}
