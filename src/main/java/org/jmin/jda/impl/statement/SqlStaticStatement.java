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

import org.jmin.jda.mapping.ParamMap;
import org.jmin.jda.mapping.ParamUnit;
import org.jmin.jda.mapping.RelationUnit;
import org.jmin.jda.mapping.ResultMap;
import org.jmin.jda.mapping.ResultUnit;
import org.jmin.jda.statement.SqlPropertyTable;

/**
 * 静态SQL 定义
 * 
 * @author Chris
 */
public class SqlStaticStatement extends SqlBaseStatement {

	/**
	 * SQL定义
	 */
	private String sql;

	/**
	 * 参数影射
	 */
	private ParamMap paramMap = null;

	/**
	 * 构造函数
	 */
	public SqlStaticStatement(String id, String sql,SqlOperationType sqlType,ParamMap paramMap, ResultMap resultMap,SqlPropertyTable sqlPropertyTable) {
		super(id,sqlType,sqlPropertyTable);
		this.sql = sql;
		this.paramMap = paramMap;
		this.resultMap = resultMap;
	}
	
	/**
	 * SQL定义
	 */
	public String getSqlId() {
		return sqlId;
	}

	/**
	 * SQL定义
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * 参数影射
	 */
	public ParamMap getParamMap() {
		return paramMap;
	}
 
	/**
	 * 参数类
	 */
	public Class getParamClass() {
		return (paramMap == null)?null:paramMap.getParamClass();
	}
	
	/**
	 * 参数类
	 */
	public Class getResultClass() {
		return (resultMap == null)?null:resultMap.getResultClass();
	}
	
	/**
	 * 参数单元
	 */
	public ParamUnit[] getParamUnits() {
		return (paramMap == null)?null:paramMap.getParamUnits();
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
