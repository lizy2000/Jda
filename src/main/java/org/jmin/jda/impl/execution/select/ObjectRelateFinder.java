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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

import org.jmin.jda.impl.JdaSessionImpl;
import org.jmin.jda.impl.exception.SqlExecutionException;
import org.jmin.jda.impl.execution.SqlRequest;
import org.jmin.jda.impl.execution.SqlRequestQueryResult;
import org.jmin.jda.impl.execution.worker.ParamObjectFactory;
import org.jmin.jda.impl.execution.worker.ResultObjectFactory;
import org.jmin.jda.impl.mapping.result.RelationUnitImpl;
import org.jmin.jda.impl.property.PropertyException;
import org.jmin.jda.impl.property.PropertyUtil;
import org.jmin.jda.impl.statement.SqlStaticStatement;
import org.jmin.jda.impl.util.BeanUtil;
import org.jmin.jda.impl.util.CloseUtil;
import org.jmin.jda.impl.util.StringUtil;
import org.jmin.jda.mapping.RelationUnit;

/**
 * 关联查询
 * 
 * @author Chris Liao
 */

public class ObjectRelateFinder extends BaseFinder{

	/**
	 * 为延迟加载准备
	 */
	public Object getRelationValue(SqlRequest request,boolean needReleaseCon)throws SQLException{
		ResultSet resultSet = null;
		SqlRequestQueryResult queryResult=null;
		Object resultValue = null;
		RelationUnitImpl relateUnit =null;
		
		try{
			relateUnit = request.getRelationUnit();
			Class relationType= relateUnit.getPropertyType();
			JdaSessionImpl session = request.getRequestSession();
			
			queryResult=session.getSqlRequestHandler().handleQueryRequest(request);
			resultSet = queryResult.getResultSet();
			if(resultSet!=null){
				Connection connection = request.getConnection();
				SqlStaticStatement statement =(SqlStaticStatement)session.getSqlStatement(request.getSqlId());
				
				if(ListIterator.class.isAssignableFrom(relationType)){
					List list = (List)readCollection(session,connection,resultSet,statement,List.class,relateUnit);
					return list.listIterator();
				}else if(Iterator.class.isAssignableFrom(relationType)){
					List list = (List)readCollection(session,connection,resultSet,statement,List.class,relateUnit);
					return list.iterator();
				}else if(Enumeration.class.isAssignableFrom(relationType)){	
					Vector vector = (Vector)readCollection(session,connection,resultSet,statement,List.class,relateUnit);
					return vector.elements();
				}else if(Collection.class.isAssignableFrom(relationType)){
					resultValue = readCollection(session,connection,resultSet,statement,relationType,relateUnit);
				}else if(Map.class.isAssignableFrom(relationType)){
					resultValue = readMap(session,connection,resultSet,statement,relateUnit);
				}else if(Object[].class.isAssignableFrom(relationType)){
					Collection col = readCollection(session,connection,resultSet,statement,relationType,relateUnit);
					return col.toArray(new Object[col.size()]);
				}else{
					resultValue = readObject(session,connection,resultSet,statement,relateUnit);
				}
				return resultValue;
			}else{
				throw new SqlExecutionException(request.getSqlId(),"Relation query resultSet is null");
			}
		}finally {
			if(queryResult!= null){
				CloseUtil.close(queryResult.getResultSet());
				CloseUtil.close(queryResult.getStatement());
			}
			
			if(needReleaseCon && request!=null && request.getConnection()!= null){
				request.getRequestSession().releaseConnection(request.getConnection());
				request.setConnection(null);
			}
		}
	}
	
	/**
	 * 读出一个对象
	 */
	private Object readObject(JdaSessionImpl session,Connection con,ResultSet resultSet,SqlStaticStatement statement,RelationUnit relationUnit)throws SQLException{
		try {
			Object resultObject=null;
			Object sqlId = statement.getSqlId();
			
			if(resultSet.next()){
				ResultObjectFactory resultObjectFactory=session.getResultObjectFactory();
				resultObject =resultObjectFactory.readResultObject(session,con,statement,resultSet,null);
			}else{
				throw new SqlExecutionException(sqlId,"No any records existed");
			}
			
			if(resultSet.next())
				throw new SqlExecutionException(sqlId,"Multiple row records existed");
	
			return resultObject;
		}catch(Throwable e){
			throw new SqlExecutionException(statement.getSql(),"Failed to execute sinle relation for propery name:"+relationUnit.getSqlId(),e);
		}
	}
	
	/**
	 * 读出一个普通的collection
	 */
	private Collection readCollection(JdaSessionImpl session,Connection con,ResultSet resultSet,SqlStaticStatement statement,Class collectionClass,RelationUnit relationUnit)throws SQLException{
		try {
			ResultObjectFactory resultObjectFactory=session.getResultObjectFactory();
			Collection collection =(Collection)BeanUtil.createInstance(collectionClass);
			while(resultSet.next()) 
			 collection.add(resultObjectFactory.readResultObject(session,con,statement,resultSet,null));
			return collection;
		}catch(Throwable e){
			throw new SqlExecutionException(statement.getSql(),"Failed to execute list relation for propery name:"+relationUnit.getSqlId(),e);
		}
	}
	
	/**
	 * 读出一个普通的Map
	 */
	private Map readMap(JdaSessionImpl session,Connection con,ResultSet resultSet,SqlStaticStatement statement,RelationUnit relationUnit)throws SQLException{
		try {
			Object sqlId = statement.getSqlId();
			Class beanClass = statement.getResultClass();
			Class relaitonType =relationUnit.getPropertyType();
		
			String keyPropertyname = relationUnit.getMapKeyPropertyName();
			String ValuePropertyName = relationUnit.getMapValuePropertyName();
			
			Map map =(Map)BeanUtil.createInstance(relaitonType);
			
			if(!StringUtil.isNull(keyPropertyname) && !Map.class.isAssignableFrom(beanClass))
				try {
					PropertyUtil.getPropertyType(beanClass,keyPropertyname);
				} catch (PropertyException e) {
					throw new SqlExecutionException(sqlId,"Failed find map key property["+keyPropertyname+"]",e);
				}
				
			if(!StringUtil.isNull(ValuePropertyName) && !Map.class.isAssignableFrom(beanClass))
				try {
					PropertyUtil.getPropertyType(beanClass,ValuePropertyName);
				} catch (PropertyException e) {
					throw new SqlExecutionException(sqlId,"Failed find map value property["+ValuePropertyName+"]",e);
				}
				
			 ParamObjectFactory paramObjectFactory=session.getParamObjectFactory();
			 ResultObjectFactory resultObjectFactory=session.getResultObjectFactory();			 
			 while(resultSet.next()) {
				Object resultObj = resultObjectFactory.readResultObject(session,con,statement,resultSet,null);
				Object keyValue = paramObjectFactory.getPropertyValue(session,sqlId,resultObj,keyPropertyname);
				if(!StringUtil.isNull(ValuePropertyName))
				  resultObj = paramObjectFactory.getPropertyValue(session,sqlId,resultObj,ValuePropertyName); 
				map.put(keyValue,resultObj);
			}
			return map;
		}catch(Throwable e){
			throw new SqlExecutionException(statement.getSql(),"Failed to execute map relation for propery name:"+relationUnit.getSqlId(),e);
		}
	}
}
