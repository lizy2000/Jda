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
package org.jmin.jda.impl.dynamic;

import org.jmin.jda.mapping.ParamUnit;

/**
 * 动态SQL结果
 * 
 * @author Chris liao
 */

public class DynSqlResult {
	
	/**
	 * sql id
	 */
	private Object sqlId;
	
	/**
	 * sql本身文本
	 */
	private String sqlText;
	
	/**
	 * 参数对象
	 */
	private Object parmaObject;
	
	/**
	 * 执行的参数映射
	 */
	private ParamUnit[] paramUnits;
	
	/**
	 * 构造函数
	 */
	public DynSqlResult(Object sqlId,Object parmaObject){
		this.sqlId =sqlId;
		this.parmaObject=parmaObject;
	}
	
	/**
	 * sql id
	 */
	public Object getSqlId() {
		return sqlId;
	}
	
	/**
	 * 参数对象
	 */
	public Object getParmaObject() {
		return parmaObject;
	}
	
	/**
	 * sql本身文本
	 */
	public String getSqlText() {
		return sqlText;
	}
	
	public void setSqlText(String sqlText) {
		this.sqlText = sqlText;
	}
	
	/**
	 * 执行的参数映射
	 */
	public ParamUnit[] getParamUnits() {
		return paramUnits;
	}
	
	/**
	 * 执行的参数映射
	 */
	public void setParamUnits(ParamUnit[] paramUnits) {
		this.paramUnits = paramUnits;
	}
}
