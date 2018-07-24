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
import java.util.Map;

import org.jmin.jda.impl.JdaSessionImpl;
import org.jmin.jda.impl.exception.ResultMapException;
import org.jmin.jda.impl.exception.SqlExecutionException;
import org.jmin.jda.impl.execution.SqlRequest;
import org.jmin.jda.impl.execution.SqlRequestQueryResult;
import org.jmin.jda.impl.execution.worker.ParamObjectFactory;
import org.jmin.jda.impl.execution.worker.ResultObjectFactory;
import org.jmin.jda.impl.property.PropertyException;
import org.jmin.jda.impl.property.PropertyUtil;
import org.jmin.jda.impl.statement.SqlBaseStatement;
import org.jmin.jda.impl.statement.SqlOperationType;
import org.jmin.jda.impl.util.BeanUtil;
import org.jmin.jda.impl.util.CloseUtil;
import org.jmin.jda.impl.util.StringUtil;

/**
 * 查询，结果返回一个Map
 * 
 * @author Chris
 */

public class ObjectMapFinder extends BaseFinder{
		
	/**
	 * Map结果查询
	 */
	public Map find(JdaSessionImpl session,SqlBaseStatement statement,Object paramObject,String keyPropName,String valuePropName)throws SQLException {
	 return find(session,statement,paramObject,keyPropName,valuePropName,Map.class);
	}
	
	/**
	 * Map结果查询
	 */
	public Map find(JdaSessionImpl session,SqlBaseStatement statement,Object paramObject,String keyPropName,String valuePropName,Class mapClass)throws SQLException {
		Object[] paramValues=null;
		Map resultMapObj=null;
		ResultSet resultSet = null;
		SqlRequestQueryResult queryResult=null;
		
		Object sqlId=statement.getSqlId();
		Class resultClass=statement.getResultClass();
	  
		if(!Map.class.isAssignableFrom(mapClass))
			throw new ResultMapException(statement.getSqlId(),"Result class must be child of Map.class");
		
		SqlRequest request=session.getSqlRequestFactory().createSqlRequest(session,statement,paramObject,SqlOperationType.Select);
	
		try {
			 String sqlText =request.getSqlText();
			 paramValues = request.getParamValues();
			 resultMapObj=(Map)this.getObjectFromCache(session,statement,sqlText,paramValues,null);//从缓存中获取
			 if(resultMapObj ==null){//缓存中无法取到对象
				 if(!StringUtil.isNull(keyPropName) && !Map.class.isAssignableFrom(resultClass))
					try {
						PropertyUtil.getPropertyType(resultClass,keyPropName);
					} catch (PropertyException e) {
						throw new SqlExecutionException(sqlId,"Failed find map key property["+keyPropName+"]",e);
					} 
				
				if(!StringUtil.isNull(valuePropName) && !Map.class.isAssignableFrom(resultClass))
					try {
						 PropertyUtil.getPropertyType(resultClass,valuePropName);
					} catch (PropertyException e) {
						throw new SqlExecutionException(sqlId,"Failed find map value property["+valuePropName+"]",e);
					} 
				
				resultMapObj =(Map)BeanUtil.createInstance(mapClass);
				queryResult = session.getSqlRequestHandler().handleQueryRequest(request);
				resultSet=queryResult.getResultSet();
				
				ParamObjectFactory paramObjectFactory=session.getParamObjectFactory();
				ResultObjectFactory resultObjectFactory=session.getResultObjectFactory();
				if(resultSet!=null){
					while(resultSet.next()){
						Object resultObject = resultObjectFactory.readResultObject(session,request.getConnection(),statement,resultSet,null);
						Object keyValue = paramObjectFactory.getPropertyValue(session,sqlId,resultObject,keyPropName);
						if(!StringUtil.isNull(valuePropName))
						  resultObject = paramObjectFactory.getPropertyValue(session,sqlId,resultObject,valuePropName); 
						resultMapObj.put(keyValue,resultObject);
					}
					
					this.putObjectIntoCache(session,statement,sqlText,paramValues,null,resultMapObj);//将结果放入缓存
				}else{
					throw new SqlExecutionException(sqlId,"Map query resultSet is null");
				}
			}
		  return resultMapObj;
		}catch(InstantiationException e){
			throw new SqlExecutionException(sqlId,"Failed to create map instace",e);
		}catch(IllegalAccessException e){
			throw new SqlExecutionException(sqlId,"Failed to create map instace",e);
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
