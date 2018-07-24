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

import org.jmin.jda.mapping.RelationUnit;
import org.jmin.jda.mapping.ResultMap;
import org.jmin.jda.mapping.ResultUnit;
import org.jmin.jda.statement.DynTag;
import org.jmin.jda.statement.SqlPropertyTable;

/**
 * 动态SQL定义
 * 
 * @author Chris
 */
public class SqlDynStatement extends SqlBaseStatement{
	
	/**
	 * 合成标记
	 */
	private DynTag[]tags;
	
	/**
	 * 参数类
	 */
	private Class paramClass;
 
	/**
	 * 构造函数
	 */
	public SqlDynStatement(String id,SqlOperationType sqlType,DynTag[]tags,Class paramClass,SqlPropertyTable sqlPropertyTable){
		 super(id,sqlType,sqlPropertyTable);
		 this.tags=tags;
		 this.paramClass = paramClass;
	}
	
	/**
	 * 构造函数
	 */
	public SqlDynStatement(String id,SqlOperationType sqlType,DynTag[]tags,Class paramClass,ResultMap resultMap,SqlPropertyTable sqlPropertyTable){
		 super(id,sqlType,sqlPropertyTable);
		 this.tags=tags;
		 this.paramClass = paramClass;
		 this.resultMap = resultMap;
	}

	/**
	 * 合成标记
	 */
	public DynTag[] getDynTags(){
		return this.tags;
	}
	
	/**
	 * 参数类
	 */
	public Class getParamClass() {
		return paramClass;
	}

	/**
	 * 结果影射列表
	 */
	public ResultMap getResultMap() {
		return resultMap;
	}
	
	/**
	 * 参数类
	 */
	public Class getResultClass() {
		return (resultMap == null)?null:resultMap.getResultClass();
	}
	
	/**
	 * 结果单元
	 */
	public ResultUnit[] getResultUnits() {
		return (resultMap == null)?null:resultMap.getResultUnits();
	}
	
	/**
	 * 关联单元
	 */
	public RelationUnit[] getRelationUnits() {
		return (resultMap == null)?null:resultMap.getRelationUnits();
	}
}