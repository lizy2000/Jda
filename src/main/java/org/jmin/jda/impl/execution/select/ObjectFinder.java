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

import org.jmin.jda.impl.JdaSessionImpl;
import org.jmin.jda.impl.exception.ResultMapException;
import org.jmin.jda.impl.exception.SqlExecutionException;
import org.jmin.jda.impl.execution.SqlRequest;
import org.jmin.jda.impl.execution.SqlRequestQueryResult;
import org.jmin.jda.impl.execution.worker.ResultObjectFactory;
import org.jmin.jda.impl.statement.SqlBaseStatement;
import org.jmin.jda.impl.statement.SqlOperationType;
import org.jmin.jda.impl.util.ClassUtil;
import org.jmin.jda.impl.util.CloseUtil;

/**
 * 单个对象查找
 * 
 * @author Chris Liao
 */
public class ObjectFinder extends BaseFinder{
	
	/**
	 * 查找单个对象，如果找到多个对象，将抛出异常,如果resultObject不为空，则需要在该对象注射列值
	 */
	public Object find(JdaSessionImpl session,SqlBaseStatement statement,Object paramObj,Object resultObject)throws SQLException {
		ResultSet resultSet = null;
		SqlRequest request = null;
		Object[] paramValues=null;
		SqlRequestQueryResult queryResult=null;
		
	   try{
		  Object sqlId= statement.getSqlId();
		  if(SqlOperationType.Select.equals(statement.getSqlType()))
		  this.checkResultObject(sqlId,statement.getResultClass(),resultObject);
		  request=session.getSqlRequestFactory().createSqlRequest(session,statement,paramObj,SqlOperationType.Select);
		  
		  String sqlText=request.getSqlText();
		  paramValues = request.getParamValues();
		  resultObject=this.getObjectFromCache(session,statement,sqlText,paramValues,null);//从缓存中获取
		  if(resultObject==null){
			 queryResult =session.getSqlRequestHandler().handleQueryRequest(request);
			 resultSet=queryResult.getResultSet();
			 if(resultSet!=null){
				 ResultObjectFactory resultObjectFactory=session.getResultObjectFactory();	 
				 if(resultSet.next())
					 resultObject = resultObjectFactory.readResultObject(session,request.getConnection(),statement,resultSet,resultObject);
				 if(resultSet.next())
					 throw new SqlExecutionException(sqlId,"Found multiple records");
			 }
		 	this.putObjectIntoCache(session,statement,sqlText,paramValues,null,resultObject);//将结果放入缓存
		 }
		 return resultObject;
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
	
	/**
	 * 检查结果对象是否正确
	 */
	public void checkResultObject(Object id,Class resultClass,Object resultObject)throws SQLException{
		if(resultClass==null){
			throw new ResultMapException(id,"Result class can't be null");
		}else if(resultObject!=null && !ClassUtil.isAcceptableInstance(resultClass,resultObject)){
			throw new ResultMapException(id,"Result object can't match result class["+resultClass+"]");
		}
	}
}
