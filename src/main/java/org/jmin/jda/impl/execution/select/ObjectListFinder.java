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
package org.jmin.jda.impl.execution.select;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.jmin.jda.JdaDialect;
import org.jmin.jda.impl.JdaSessionImpl;
import org.jmin.jda.impl.exception.ResultMapException;
import org.jmin.jda.impl.exception.SqlExecutionException;
import org.jmin.jda.impl.execution.SqlRequest;
import org.jmin.jda.impl.execution.SqlRequestQueryResult;
import org.jmin.jda.impl.execution.worker.ResultObjectFactory;
import org.jmin.jda.impl.statement.SqlBaseStatement;
import org.jmin.jda.impl.statement.SqlOperationType;
import org.jmin.jda.impl.util.BeanUtil;
import org.jmin.jda.impl.util.CloseUtil;

/**
 * 查询，结果返回一个List
 * 
 * @author Chris
 */

public class ObjectListFinder extends BaseFinder{
		
	/**
	 * 多结果的查询，返回一个List
	 */
	public List find(JdaSessionImpl session,SqlBaseStatement statement,Object paramObject,int rowNo,int pageSize,JdaDialect dialect)throws SQLException {
		return (List)find(session,statement,paramObject,rowNo,pageSize,List.class,dialect);
	}
	
	/**
	 * 多结果的查询，返回一个List
	 */
	public Collection find(JdaSessionImpl session,SqlBaseStatement statement,Object paramObject,int rowNo,int pageSize,Class listClass,JdaDialect dialect)throws SQLException {
		Object[] paramValues=null;
		Collection collection=null;
		ResultSet resultSet=null;
		SqlRequestQueryResult queryResult=null;
		
		Object sqlId=statement.getSqlId();
		if(dialect==null)
		 dialect=session.getJdaDialect();
	
		if(!Collection.class.isAssignableFrom(listClass))
			throw new ResultMapException(sqlId,"Result class must be child of Collection.class");
	
		SqlOperationType sqlOperationType=statement.getSqlType();
		SqlRequest request=session.getSqlRequestFactory().createSqlRequest(session,statement,paramObject,SqlOperationType.Select);

		if(rowNo<=0 && pageSize >0)rowNo=1;
		request.setRecordSkipPos(rowNo);
		request.setRecordMaxRows(pageSize);
		request.setDefinitionType(statement.getSqlType());
		String sqlText =request.getSqlText();
		
		try {
			 paramValues = request.getParamValues();
			 ResultObjectFactory resultObjectFactory=session.getResultObjectFactory();
			 Object[] optionals = new Object[]{Integer.valueOf(rowNo),Integer.valueOf(pageSize)};
			 collection =(Collection)this.getObjectFromCache(session,statement,sqlText,paramValues,optionals);//从缓存中获取	
			 if(collection ==null){//缓存中无法取到对象
				 queryResult=session.getSqlRequestHandler().handleQueryRequest(request);
				 resultSet=queryResult.getResultSet();
				 
				 if(resultSet!=null){
					collection =(Collection)BeanUtil.createInstance(listClass);
					
					if(rowNo>=2)
					 this.moveToTargetRow(resultSet,rowNo-1,sqlOperationType,dialect,session);//将ResultSet游标移动到目标行前一行
					
					int readCount=0;
					while(resultSet.next()) {
						collection.add(resultObjectFactory.readResultObject(session,request.getConnection(),statement,resultSet,null));
						if(pageSize >0 && ++readCount == pageSize)
							break;
					}
					
					this.putObjectIntoCache(session,statement,sqlText,paramValues,optionals,collection);//将结果放入缓存
			 }else{
				throw new SqlExecutionException(sqlId,"List query resultSet is null");
			 }
			}
			 
		  return collection;
		}catch(InstantiationException e){
			throw new SqlExecutionException(sqlId,"Failed to create list instace",e);
		}catch(IllegalAccessException e){
			throw new SqlExecutionException(sqlId,"Failed to create list instace",e);
		}finally {
			if(queryResult!= null){
				CloseUtil.close(queryResult.getResultSet());
				CloseUtil.close(queryResult.getStatement());
			}
		
			if(request!=null && request.getConnection()!= null){
				session.releaseConnection(request.getConnection());
				request.setConnection(null);
			}
		}
	}
}