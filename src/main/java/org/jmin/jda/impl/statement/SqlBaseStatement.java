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

import org.jmin.jda.mapping.ResultMap;
import org.jmin.jda.statement.SqlPropertyTable;

/**
 * SQL定义超类
 * 
 * @author Chris
 */
public abstract class SqlBaseStatement {
	
	/**
	 * 定义ID
	 */
	protected String sqlId;
		
	/**
	 * SQL操作类型
	 */
	private SqlOperationType sqlType;
	
	/**
	 * 结果影射
	 */
	protected ResultMap resultMap = null;
	
	/**
	 * SQL定义的一些属性映射
	 */
	private SqlPropertyTable sqlPropertyTable = null;
	
	/**
	 * 构造函数
	 */
	public SqlBaseStatement(String id,SqlOperationType sqlType,SqlPropertyTable sqlPropertyTable){
		this.sqlId=id;
		this.sqlType=sqlType;
		this.sqlPropertyTable=sqlPropertyTable;
		if(this.sqlPropertyTable!=null)((SqlPropertyCenter)this.sqlPropertyTable).setAsInited();
	}
	
	/**
	 * 定义ID
	 */
	public String getSqlId() {
		return sqlId;
	}
	
	/**
	 * SQL操作类型
	 */
	public SqlOperationType getSqlType() {
		return sqlType;
	}
	
	/**
	 * 结果影射
	 */
	public ResultMap getResultMap() {
		return resultMap;
	}
	
	/**
	 * SQL定义的一些属性映射
	 */
	public SqlPropertyTable getSqlPropertyTable() {
		return this.sqlPropertyTable;
	}
	
	/**
	 * 获得属性值名字
	 */
	public String[] getPropertyNames(){
		return (sqlPropertyTable==null)?new String[0]:sqlPropertyTable.getNames();
	}
	
	/**
	 * 获得属性值
	 */
	public String getPropertyValue(String name){
		return (sqlPropertyTable==null)?null:sqlPropertyTable.getValue(name);
	}
	
	/**
	 * 参数类
	 */
	public abstract Class getParamClass();
	 
	/**
	 * 结果类
	 */
	public abstract Class getResultClass();
	
}
