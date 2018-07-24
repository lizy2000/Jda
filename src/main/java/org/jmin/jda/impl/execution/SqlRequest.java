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
package org.jmin.jda.impl.execution;

import java.sql.Connection;
import java.sql.SQLException;

import org.jmin.jda.JdaDialect;
import org.jmin.jda.JdaTypePersister;
import org.jmin.jda.impl.JdaSessionImpl;
import org.jmin.jda.impl.mapping.result.RelationUnitImpl;
import org.jmin.jda.impl.statement.SqlOperationType;
import org.jmin.jda.mapping.ParamValueMode;
import org.jmin.jda.statement.SqlPropertyTable;

/**
 * SQL执行请求
 * 
 * @author Chris Liao
 */

public class SqlRequest {
	
	/**
	 * 定义ID
	 */
	private String sqlId=null;

	/**
	 * 是否为动态SQL
	 */
	private boolean dynSQL=false;
	
	/**
	 * 等待执行SQL
	 */
	private String sqlText=null;
	
	/**
	 * 参数类
	 */
	private Class paramClass=null;

	/**
	 * 传递进来的参数对象
	 */
	private Object paramObject=null;
	
	/**
	 * 参数名
	 */
	private String[] paramNames=null;
	
	/**
	 * 参数类型
	 */
	private Class[] paramValueTypes=null;

	/**
	 * 影射到SQL语句中的参数值
	 */
	private Object[] paramValues=null;
	
	/**
	 * 对应参数的数据库的SQL列类型
	 */
	private int[] paramSqlTypeCodes;
	
	/**
	 *参数类型持久器
	 */
	private JdaTypePersister[] paramTypePersisters;
	
	/**
	 * 参数模型
	 */
	private ParamValueMode[] paramValueModes;

	/**
	 * 请求执行SQL的session
	 */
	private JdaSessionImpl session=null;
	
	/**
	 * 已经执行过SQL的connection,执行之后需要关闭
	 */
	private Connection connection;
	
	
	/*************下面为分页查询时候需要的一些变量信息*********************/
	
	/**
	 * 分页查询滚动指定位置
	 */
	private int recordSkipPos=0;
	
	/**
	 * 最大读取多少行记录数
	 */
	private int recordMaxRows=0;
	
	/**
	 * SQL本身的操作类型
	 */
	private SqlOperationType definitionType;
 
	/**
	 * 当前sql为一个关联的查询时候,该值将不为空
	 */
	private RelationUnitImpl  relationUnit=null;
	
	
	/*************上面为分页查询时候需要的一些变量信息*********************/
	
	/**
	 * 构造函数
	 */
	public SqlRequest(JdaSessionImpl session,String sqlId,Class paramClass,Object paramObject){
		this.sqlId = sqlId;
		this.session = session;
		this.paramClass = paramClass;
		this.paramObject = paramObject;
	}
	
	/**
	 * 定义ID
	 */
	public String getSqlId() {
		return sqlId;
	}
	
	/**
	 * 是否为动态SQL
	 */
	public boolean isDynSQL() {
		return dynSQL;
	}
	
	/**
	 * 是否为动态SQL
	 */
	public void setDynSQL(boolean dynSQL) {
		this.dynSQL = dynSQL;
	}

	/**
	 * 等待执行SQL
	 */
	public String getSqlText() {
		return sqlText;
	}
	
	/**
	 * 等待执行SQL
	 */
	public void setSqlText(String sqlText) {
		this.sqlText = sqlText;
	}

	/**
	 * 参数类
	 */
	public Class getParamClass() {
		return paramClass;
	}
	
	/**
	 * 参数类
	 */
	public void setParamClass(Class paramClass) {
		this.paramClass=paramClass;
	}
	
	/**
	 * 传递进来的参数对象
	 */
	public Object getParamObject() {
		return paramObject;
	}
	
	/**
	 * 参数名
	 */
	public String[] getParamNames() {
		return paramNames;
	}
	
	/**
	 * 参数名
	 */
	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}

	/**
	 * 参数类型
	 */
	public Class[] getParamValueTypes() {
		return paramValueTypes;
	}
	
	/**
	 * 参数类型
	 */
	public void setParamValueTypes(Class[] paramTypes) {
		this.paramValueTypes = paramTypes;
	}

	/**
	 * 影射到SQL语句中的参数值
	 */
	public Object[] getParamValues() {
		return paramValues;
	}
	
	/**
	 * 影射到SQL语句中的参数值
	 */
	public void setParamValues(Object[] paramValues) {
		this.paramValues = paramValues;
	}
	
	/**
	 * 对应参数的数据库的SQL列类型
	 */
	public int[] getParamSqlTypeCodes() {
		return paramSqlTypeCodes;
	}
	
	/**
	 * 对应参数的数据库的SQL列类型
	 */
	public void setParamSqlTypeCodes(int[] paramSqlTypeCodes) {
		this.paramSqlTypeCodes = paramSqlTypeCodes;
	}
	
	/**
	 * 参数模型
	 */
	public ParamValueMode[] getParamValueModes() {
		return paramValueModes;
	}
	
	/**
	 * 参数模型
	 */
	public void setParamValueModes(ParamValueMode[] paramValueModes) {
		this.paramValueModes = paramValueModes;
	}
	
	/**
	 *参数类型持久器
	 */
	public JdaTypePersister[] getParamTypePersisters() {
		return paramTypePersisters;
	}
	
	/**
	 *参数类型持久器
	 */
	public void setParamTypePersisters(JdaTypePersister[] paramTypePersisters) {
		this.paramTypePersisters = paramTypePersisters;
	}
	
	/**
	 * 请求执行SQL的session
	 */
	public JdaSessionImpl getRequestSession() {
		return session;
	}
	
	/**
	 * 已经执行过SQL的connection,执行之后需要关闭
	 */
	public Connection getConnection() {
		return connection;
	}
	
	/**
	 * 已经执行过SQL的connection,执行之后需要关闭
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	/**
	 * 分页查询滚动指定位置
	 */
	public int getRecordSkipPos() {
		return recordSkipPos;
	}
	
	/**
	 * 分页查询滚动指定位置
	 */
	public void setRecordSkipPos(int skipRow) {
		this.recordSkipPos = skipRow;
	}
	
	/**
	 * 最大读取多少行记录数
	 */
	public int getRecordMaxRows() {
		return recordMaxRows;
	}
	
	/**
	 * 最大读取多少行记录数
	 */
	public void setRecordMaxRows(int maxRows) {
		this.recordMaxRows = maxRows;
	}
	
	/**
	 * SQL方言
	 */
	public JdaDialect getSqlDialect() {
		return this.session.getJdaDialect();
	}
	
	/**
	 * SQL本身的操作类型
	 */
	public SqlOperationType getDefinitionType() {
		return definitionType;
	}
	
	/**
	 * SQL本身的操作类型
	 */
	public void setDefinitionType(SqlOperationType definitionType) {
		this.definitionType = definitionType;
	}

	/**
	 * 当前sql为一个关联的查询时候,该值将不为空
	 */
	public RelationUnitImpl getRelationUnit() {
		return relationUnit;
	}
	
	/**
	 * 当前sql为一个关联的查询时候,该值将不为空
	 */
	public void setRelationUnit(RelationUnitImpl relationUnit) {
		this.relationUnit = relationUnit;
	}
	
	/**
	 * 获得SQL属性映射
	 */
	public SqlPropertyTable getSqlAttributeTable()throws SQLException {
		return session.getSqlStatement(sqlId).getSqlPropertyTable();
	}
}
