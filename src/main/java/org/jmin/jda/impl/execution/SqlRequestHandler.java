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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jmin.jda.JdaDialect;
import org.jmin.jda.JdaTypePersister;
import org.jmin.jda.impl.JdaSessionImpl;
import org.jmin.jda.impl.exception.SqlExecutionException;
import org.jmin.jda.impl.execution.worker.ParamObjectFactory;
import org.jmin.jda.impl.statement.SqlBaseStatement;
import org.jmin.jda.impl.statement.SqlOperationType;
import org.jmin.jda.impl.util.CloseUtil;
import org.jmin.jda.impl.util.Symbols;
import org.jmin.jda.mapping.ParamValueMode;
import org.jmin.log.LogPrinter;
/**
 * SQL执行请求处理
 * 
 * @author Chris Liao
 */
public class SqlRequestHandler{
	
	/**
	 * message Printer
	 */
	private LogPrinter logger = LogPrinter.getLogPrinter(SqlRequestHandler.class);
	
	private final String Segment1="Success to execute update sql:\n";
	private final String Segment2="Failed to execute update sql:\n";
	
	private final String Segment3="Success to execute query sql:\n";
	private final String Segment4="Failed to execute query sql:\n";
	
	private final String Segment5= "effected rows:";
	private final String Segment6= "\ncause:";
	
	
	/**
	 * 执行更新操作
	 */
	public SqlRequestUpdateResult handleUpdateRequest(SqlRequest request)throws SQLException{
		Connection connection = null;
		PreparedStatement statement = null;
        long startTime = System.nanoTime();
		SqlRequestUpdateResult updateResult = new SqlRequestUpdateResult(request);
		
		String sqlId = request.getSqlId();
		String sqlText = request.getSqlText();
		Object paramObject = request.getParamObject();
		String[] paramNames = request.getParamNames();
		Class[] paramTypes = request.getParamValueTypes();
		Object[] paramValues = request.getParamValues();
		int[] paramSqlTypeCodes = request.getParamSqlTypeCodes();
		JdaTypePersister[] paramTypePersisters = request.getParamTypePersisters();
		ParamValueMode[] paramValueModes = request.getParamValueModes();
		JdaSessionImpl session = request.getRequestSession();
		boolean needShowSql = session.needShowSql();
		ParamObjectFactory paramObjectFactory=session.getParamObjectFactory();
 
		try {
			connection=getConneciton(request);
			statement=this.createGeneralStatement(connection,sqlText);
			paramObjectFactory.setParamValues(session,sqlId,statement,paramNames,paramValues,paramSqlTypeCodes,paramTypePersisters,paramValueModes);
			
			int effectRows=statement.executeUpdate();
			if(statement instanceof CallableStatement && paramObject!= null && !session.supportsPersisterType(request.getParamClass()))//当前为存储过程调用，则需要读取那些out类型的参数结果
				paramObjectFactory.readCallStatement(session,sqlId,(CallableStatement)statement,paramObject,paramNames,paramTypes,paramValueModes,paramTypePersisters,connection);
			
			if(needShowSql)
			 logger.info(sqlId,new StringBuffer(Segment1)
				   .append(this.getSqlPrintInfo(sqlText,paramNames,paramTypes,paramValues))
				   .append(Symbols.New_Line).append(Segment5).append(effectRows).append(Symbols.New_Line).toString());
			
			updateResult.setEffectedRows(effectRows); 
			updateResult.setExcuteTimeMis((System.nanoTime()-startTime)/1000000L);
		}catch (Throwable e) {
			updateResult.setFailCause(e);
			updateResult.setExcuteTimeMis((System.nanoTime()-startTime)/1000000L);
			String message = new StringBuffer(Segment2)
			    .append( this.getSqlPrintInfo(sqlText,paramNames,paramTypes,paramValues))
			    .append(Segment6).append(e.getMessage()).toString();
			
			if(needShowSql)
			 logger.info(sqlId,message);
			throw new SqlExecutionException(sqlId,message,e);
		}finally{
			CloseUtil.close(statement);
		}
		return updateResult;
	}
	
	/**
	 * 执行查询操作
	 */
	public SqlRequestQueryResult handleQueryRequest(SqlRequest request)throws SQLException{
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement statement = null;
		long startTime = System.nanoTime();
		SqlRequestQueryResult queryResult = new SqlRequestQueryResult(request);
		
		String sqlId = request.getSqlId();
		String sqlText = request.getSqlText();
		Object paramObject = request.getParamObject();
		String[] paramNames = request.getParamNames();
		Class[] paramTypes = request.getParamValueTypes();
		Object[] paramValues = request.getParamValues();

		int[] paramSqlTypeCodes = request.getParamSqlTypeCodes();
		JdaTypePersister[] paramTypePersisters = request.getParamTypePersisters();
		ParamValueMode[] paramValueModes = request.getParamValueModes();

		int startPos = request.getRecordSkipPos();
		int offset = request.getRecordMaxRows();
		JdaDialect dialect = request.getSqlDialect();
		JdaSessionImpl session = request.getRequestSession();
		boolean needShowSql = session.needShowSql();
		SqlOperationType definitionType = request.getDefinitionType();
		SqlBaseStatement sqlStatement = session.getSqlStatement(sqlId);
		ParamObjectFactory paramObjectFactory=session.getParamObjectFactory();
	 
		try {
			connection = getConneciton(request);
			if (startPos > 0 && offset > 0 && dialect != null && SqlOperationType.Select.equals(definitionType)) {// 当前SQL是分页查询，需要重新改造Request
				this.rebuildRequestForPageQuery(request, startPos, offset);// 重新设置了查询语句
				sqlText = request.getSqlText();
			}

			statement = this.createGeneralStatement(connection, request.getSqlText());
			this.setResultSetFetchSize(statement, session);// 设置查询结果的FetchSize
			paramObjectFactory.setParamValues(session, sqlId, statement, paramNames, paramValues, paramSqlTypeCodes,paramTypePersisters, paramValueModes);

			if (statement instanceof CallableStatement) {
				CallableStatement callStatement = (CallableStatement) statement;
				callStatement.execute();
				if (paramObject != null && !session.supportsPersisterType(paramObject.getClass()))// 当前为存储过程调用，则需要读取那些out类型的参数结果
					paramObjectFactory.readCallStatement(session, sqlId, (CallableStatement) statement, paramObject,
							paramNames, paramTypes, paramValueModes, paramTypePersisters, connection);
				if (sqlStatement.getResultMap() != null)
					try {
						resultSet = callStatement.getResultSet();
					} catch (Exception e) {}
			} else {
				resultSet = statement.executeQuery();
			}

			if (needShowSql)
				logger.info(sqlId,
						new StringBuffer(Segment3)
								.append(this.getSqlPrintInfo(sqlText, paramNames, paramTypes, paramValues))
								.append(Symbols.New_Line).toString());

			queryResult.setResultSet(resultSet);
			queryResult.setStatement(statement);
			queryResult.setExcuteTimeMis((System.nanoTime()-startTime)/1000000L);
		} catch (Throwable e) {
			queryResult.setFailCause(e);
			queryResult.setExcuteTimeMis((System.nanoTime()-startTime)/1000000L);
			if(resultSet!=null)CloseUtil.close(resultSet);
			if(statement!=null)CloseUtil.close(statement);
			
			String message = new StringBuffer(Segment4)
					.append(this.getSqlPrintInfo(sqlText, paramNames, paramTypes, paramValues)).append(Segment6)
					.append(e.getMessage()).toString();
			if (needShowSql)
				logger.info(sqlId, message);
			throw new SqlExecutionException(sqlId, message, e);
		}
		
		return queryResult;
	}

	/**
	 * 获取连接
	 */
	private Connection getConneciton(SqlRequest request)throws SQLException{
		Connection connection =null;
		if(request.getConnection()==null){
			JdaSessionImpl session= request.getRequestSession();
			connection= session.getConnection();
			request.setConnection(connection);
	   }else{
	  	connection=request.getConnection();
	   }
		
		return connection;
	}

	/**
	 * 构造一个普通PreparedStatement
	 */
	public PreparedStatement createGeneralStatement(Connection con,String sql) throws SQLException {
		if(sql.trim().startsWith(Symbols.Left_Braces))// 判断SQL文本是否为存储过程调用
			return con.prepareCall(sql);
		else
			return con.prepareStatement(sql);
	}
	
	/**
	 * 设置结果Fectch size
	 */
	private void setResultSetFetchSize(PreparedStatement ps,JdaSessionImpl session)throws SQLException {
		if(session.getResultSetFetchSize() > 0){
			if(session.supportFetchChecked()){//已经检查过是否支持ResultFetch
				if(session.supportFetch()){
					ps.setFetchSize(session.getResultSetFetchSize());
					ps.setFetchDirection(ResultSet.FETCH_FORWARD);
				}
			}else{//还没检查过是否支持ResultFetch
				try{
					ps.setFetchSize(session.getResultSetFetchSize());
					ps.setFetchDirection(ResultSet.FETCH_FORWARD);
					session.setSupportFetch(true);
				}catch(Throwable e){
					logger.warn("Current driver not support result fetch");
					session.setSupportFetch(false);
				}finally{
					session.setSupportFetchChecked(true);
				}
			}
		} 
	}

	/**
	 * 改造SQL为分页查询
	 */
	public void rebuildRequestForPageQuery(SqlRequest request,int rowNo,int offset)throws SQLException {
		JdaDialect dialect=request.getSqlDialect();
		String pageSql = dialect.getPageQuerySql(request.getSqlText(),rowNo,offset,request.getSqlAttributeTable());//构造出分页查询的SQL
		if(!pageSql.equals(request.getSqlText())){
			request.setSqlText(pageSql);
	   }
	}
	
	/**
	 * 获得SQL执行参数值
	 */
	private StringBuffer getSqlPrintInfo(String sql,String[]paramName,Class[]paramTypes,Object[] paramValues)throws SQLException {
		StringBuffer buffer = new StringBuffer();
		buffer.append(sql);
		if(paramValues!=null && paramValues.length >0)
			buffer.append(Symbols.New_Line);
		for(int i=0,n=(paramValues==null)?0:paramValues.length; i<n;i++){
			buffer.append(Symbols.Left_Square_Brackets).append((i+1)).append(Symbols.Right_Square_Brackets).append(Symbols.Equal).append(String.valueOf(paramValues[i]));
			buffer.append(Symbols.Space).append(Symbols.Left_Parentheses).append(paramTypes[i].getName()).append(Symbols.Space).append(Symbols.Colon).append(Symbols.Space).append(paramName[i]).append(Symbols.Right_Parentheses);
			if(i<paramValues.length-1)
				buffer.append(Symbols.New_Line);
		}
		return buffer;
	}
}
