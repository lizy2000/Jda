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
package org.jmin.jda.impl.execution.update;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jmin.jda.JdaTypePersister;
import org.jmin.jda.impl.JdaSessionImpl;
import org.jmin.jda.impl.exception.SqlExecutionException;
import org.jmin.jda.impl.execution.SqlRequest;
import org.jmin.jda.impl.execution.SqlRequestHandler;
import org.jmin.jda.impl.execution.worker.ParamObjectFactory;
import org.jmin.jda.impl.statement.SqlBaseStatement;
import org.jmin.jda.impl.util.CloseUtil;
import org.jmin.jda.impl.util.StringUtil;
import org.jmin.jda.impl.util.Symbols;
import org.jmin.jda.mapping.ParamValueMode;
import org.jmin.jda.statement.SqlPropertyIds;

/**
 * 批量更新操作执行器
 * 
 * @author Chris 
 */

public class BatchHandler {
	
	/**
	 * 批量更新Size
	 */
	private int batchUpdateSize= 10;
	
	/**
	 * 存放需要批量执行的请求
	 */
	private List batchRequestList = new LinkedList();

	/**
	 * 构造函数
	 */
	public BatchHandler(int batchUpdateSize){
		this.batchUpdateSize = batchUpdateSize;
	}

	/**
	 * 增加一个Item
	 */
	public void addBatchRequest(SqlRequest request){
	  this.batchRequestList.add(request);
	}
	
	/**
	 * 执行批量操作
	 */
	public int execute(Connection con,JdaSessionImpl session) throws SQLException {
		if(session.supportBatch())
	    return this.executeBatchUpdate(con,session);
	  else{
	    return this.executeGeneralUpdate(con,session);
	  }
	}

	/**
	 * 执行批量操作
	 */
	public int executeBatchUpdate(Connection con, JdaSessionImpl session) throws SQLException {
		int[] batchRows = null;
		int totalUpdatedRows = 0;
		int currentBatchCount = 0;
		String sqlId = null;
		String preSql = Symbols.Blank, curSql = Symbols.Blank;
		PreparedStatement batchStatement = null;
		List pendingClearCacheIdList = new ArrayList();

		SqlRequestHandler requestHandler = session.getSqlRequestHandler();
		ParamObjectFactory paramObjectFactory = session.getParamObjectFactory();

		try {
			Iterator itor = batchRequestList.iterator();
			while (itor.hasNext()) {
				SqlRequest curReqest = (SqlRequest) itor.next();
				sqlId = curReqest.getSqlId();
				curSql = curReqest.getSqlText();
				String[] paramNames = curReqest.getParamNames();
				Object[] paramValues = curReqest.getParamValues();
				int[] paramSqlTypeCodes = curReqest.getParamSqlTypeCodes();
				JdaTypePersister[] paramTypePersisters = curReqest.getParamTypePersisters();
				ParamValueMode[] paramValueModes = curReqest.getParamValueModes();
				
				//为清理二级缓存做准备
				SqlBaseStatement sqlStatement = session.getSqlStatement(sqlId);
				String flushCacheId = sqlStatement.getPropertyValue(SqlPropertyIds.FlushCacheId);
				if (session.isResultCacheOpen() && !StringUtil.isNull(flushCacheId)) {
					String[] subCacheIds = StringUtil.split(flushCacheId, ",");
					for (int i = 0; i < subCacheIds.length; i++) {
						if (!StringUtil.isNull(subCacheIds[i]) && pendingClearCacheIdList.contains(subCacheIds[i]))
							pendingClearCacheIdList.add(subCacheIds[i]);
					}
				}
				
				/**
				 * 满足批量更新的条件如下： 1:上一个Statement更新进去的数据已经达到Size,则需要执行前面Statement,
				 * 2:当前的SQL与前面执行SQL不同时候，则需要执行前面Statement,
				 * 3:到达数据列表的终点，不论1与2，都需要执行当前的Statement
				 */
				if (batchStatement == null)
					batchStatement = requestHandler.createGeneralStatement(con, curSql);

				// 批量更新Size已满或当前SQL与上一条SQL不一致
				if ((!StringUtil.isNull(preSql) && !curSql.equals(preSql)) || (currentBatchCount == batchUpdateSize)) {
					batchRows = batchStatement.executeBatch();
					totalUpdatedRows += getUpdateCount(batchRows);
					batchStatement.clearBatch();
					currentBatchCount = 0;

					// 当前SQL与上一条SQL不一致,必须关闭老的statement,创建新的statement
					if (!StringUtil.isNull(preSql) && !curSql.equals(preSql)) {
						CloseUtil.close(batchStatement);
						batchStatement = requestHandler.createGeneralStatement(con, curSql);
					}
				}

				paramObjectFactory.setParamValues(session, sqlId, batchStatement, paramNames, paramValues,
						paramSqlTypeCodes, paramTypePersisters, paramValueModes);
				batchStatement.addBatch();
				currentBatchCount++;
				preSql = curSql;
			}

			if (currentBatchCount > 0) {// 计算完毕后，依然还有剩余，将最后批次执行完毕即可
				batchRows = batchStatement.executeBatch();
				totalUpdatedRows += getUpdateCount(batchRows);
				batchStatement.clearBatch();
				currentBatchCount = 0;
				CloseUtil.close(batchStatement);
				batchStatement = null;
			}
			
			//清理二级别缓存
			if (session.isResultCacheOpen() && !pendingClearCacheIdList.isEmpty()){
				for(int i=0,n=pendingClearCacheIdList.size();i<n;i++){
					session.clearApplicationCache((String)pendingClearCacheIdList.get(i));
				}
			}
			
			return totalUpdatedRows;
		} catch (SQLException e) {
			throw new SqlExecutionException(sqlId, "Failed to execute sql:\n" + curSql + "\ncause:" + e.getMessage(),
					e);
		} finally {
			pendingClearCacheIdList.clear();
			if (batchStatement != null) {
				try {
					batchStatement.clearBatch();
				} catch (Exception e) {}

				CloseUtil.close(batchStatement);
				batchStatement = null;
			}
		}
	}
	
	/**
	 * 获取影响函数
	 */
	private int getUpdateCount(int[]batchRows){
		int count=0;
		for(int i=0,n=batchRows.length;i<n;i++){
			if(batchRows[i]>=0)
				count +=batchRows[i];
		}
		return count;
	}
	
	/**
	 * 正常情况下，一条一条执行
	 */
	private int executeGeneralUpdate(Connection con, JdaSessionImpl session) throws SQLException {
		int updatedRows, totalUpdatedRows = 0;
		String preSqlText = Symbols.Blank, curSqlText = Symbols.Blank;
		PreparedStatement statement = null;
		SqlRequestHandler requestHandler = session.getSqlRequestHandler();
		ParamObjectFactory paramObjectFactory = session.getParamObjectFactory();
		List pendingClearCacheIdList = new ArrayList();
		
		try {
			Iterator itor = batchRequestList.iterator();
			while (itor.hasNext()) {
				SqlRequest curReqest = (SqlRequest) itor.next();

				String sqlId = curReqest.getSqlId();
				curSqlText = curReqest.getSqlText();
				Object paramObject = curReqest.getParamObject();
				String[] paramNames = curReqest.getParamNames();
				Object[] paramValues = curReqest.getParamValues();
				Class[] paramTypes = curReqest.getParamValueTypes();
				int[] paramSqlTypeCodes = curReqest.getParamSqlTypeCodes();
				JdaTypePersister[] paramTypePersisters = curReqest.getParamTypePersisters();
				ParamValueMode[] paramValueModes = curReqest.getParamValueModes();
				
				//为清理二级缓存做准备
				SqlBaseStatement sqlStatement = session.getSqlStatement(sqlId);
				String flushCacheId = sqlStatement.getPropertyValue(SqlPropertyIds.FlushCacheId);
				if (session.isResultCacheOpen() && !StringUtil.isNull(flushCacheId)) {
					String[] subCacheIds = StringUtil.split(flushCacheId, ",");
					for (int i = 0; i < subCacheIds.length; i++) {
						if (!StringUtil.isNull(subCacheIds[i]) && pendingClearCacheIdList.contains(subCacheIds[i]))
							pendingClearCacheIdList.add(subCacheIds[i]);
					}
				}
				
				
				if (statement == null)
					statement = requestHandler.createGeneralStatement(con, curSqlText);

				if (!curSqlText.equals(preSqlText)) {
					CloseUtil.close(statement);
					statement = null;
					statement = requestHandler.createGeneralStatement(con, curSqlText);

					paramObjectFactory.setParamValues(session, sqlId, statement, paramNames, paramValues,
							paramSqlTypeCodes, paramTypePersisters, paramValueModes);
					updatedRows = statement.executeUpdate();
					preSqlText = curSqlText;

					totalUpdatedRows = totalUpdatedRows + updatedRows;
				} else {
					paramObjectFactory.setParamValues(session, sqlId, statement, paramNames, paramValues,
							paramSqlTypeCodes, paramTypePersisters, paramValueModes);
					updatedRows = statement.executeUpdate();

					if (statement instanceof CallableStatement && curReqest.getParamObject() != null && !curReqest
							.getRequestSession().supportsPersisterType(curReqest.getParamObject().getClass()))// 当前为存储过程调用，则需要读取那些out类型的参数结果

						paramObjectFactory.readCallStatement(session, sqlId, (CallableStatement) statement, paramObject,
								paramNames, paramTypes, paramValueModes, paramTypePersisters, con);
					preSqlText = curSqlText;
					totalUpdatedRows = totalUpdatedRows + updatedRows;
				}
			}
			
			//清理二级别缓存
			if (session.isResultCacheOpen() && !pendingClearCacheIdList.isEmpty()){
				for(int i=0,n=pendingClearCacheIdList.size();i<n;i++){
					session.clearApplicationCache((String)pendingClearCacheIdList.get(i));
				}
			}
			
			return totalUpdatedRows;
		} finally {
			pendingClearCacheIdList.clear();
			CloseUtil.close(statement);
		}
	}

	/**
	 * 清理所有Item
	 */
	public void clear(){
	  this.batchRequestList.clear();
	}
}
