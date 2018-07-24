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
package org.jmin.jda.impl.execution.worker;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jmin.jda.JdaTypeConvertFactory;
import org.jmin.jda.JdaTypePersister;
import org.jmin.jda.impl.JdaSessionImpl;
import org.jmin.jda.impl.exception.SqlExecutionException;
import org.jmin.jda.impl.execution.base.JdaResultRowMapImpl;
import org.jmin.jda.impl.mapping.result.ResultMapImpl;
import org.jmin.jda.impl.property.PropertyException;
import org.jmin.jda.impl.property.PropertyUtil;
import org.jmin.jda.impl.statement.SqlStaticStatement;
import org.jmin.jda.impl.util.ArrayUtil;
import org.jmin.jda.impl.util.BeanUtil;
import org.jmin.jda.impl.util.CloseUtil;
import org.jmin.jda.mapping.ParamUnit;
import org.jmin.jda.mapping.ParamValueMode;

/**
 * 参数辅助类
 * 
 * @author chris 
 */
public class ParamObjectFactory {
	
	/**
	 * 获得属性值
	 */
	public Object getPropertyValue(JdaSessionImpl session,Object sqlId,Object bean,String propertyName)throws SQLException{
		try {
			 if(propertyName!=null && propertyName.trim().length()>0){
				return PropertyUtil.getPropertyValue(bean,propertyName,session);
			}else{
				throw new PropertyException("Property info can't be null");
			}
		} catch (Throwable e) {
			throw new SqlExecutionException(sqlId,e);
		}
	}
	
	/**
	 * 设置参数值到PreparedStatement
	 */
	public void setParamValues(JdaSessionImpl session,Object sqlId,PreparedStatement statement,
			String[]paramNames,Object[]paramValues,int[] sqlTypes,JdaTypePersister[]persisters,
		  ParamValueMode[] paramValueModes)throws SQLException {
	
		JdaTypeConvertFactory convertFactory = session.getTypeConvertFactory();
		if(statement instanceof CallableStatement){//存储过程调用
			CallableStatement callStatement =(CallableStatement)statement;
			for(int i=0,n=(paramValues==null)?0:paramValues.length;i<n;i++) {
				try {
					int typeCode = sqlTypes[i];
					ParamValueMode paramMode = paramValueModes[i];
					
					if(!ParamValueMode.IN.equals(paramMode)) 
						callStatement.registerOutParameter(i+1,typeCode);
					if(!ParamValueMode.OUT.equals(paramMode))
						persisters[i].set(statement,i+1,paramValues[i],typeCode,convertFactory);
				}catch (Throwable e) {
					throw new SqlExecutionException(sqlId,"Failed to set callable statement parameter value on index:" +(i+1),e);
				}
			}
		}else{//普通SQL调用
			for(int i=0,n=(paramValues==null)?0:paramValues.length;i<n;i++) {
				try {
					persisters[i].set(statement,i+1,paramValues[i],sqlTypes[i],convertFactory);
				}catch (Throwable e) {
					throw new SqlExecutionException(sqlId,"Failed to set prepared statement parameter value on index:" +(i+1),e);
				}
			}
		}
  }
 
	/**
	 * 从存储过程调用中获取Out,InOut参数的结果值
	 */
	public void readCallStatement(JdaSessionImpl session,String sqlId,CallableStatement statement,Object paramObject,
			String[]paramNames,Class[]paramTypes,ParamValueMode[] paramValueModes,JdaTypePersister[]persisters,Connection connection)throws SQLException{
	
	 JdaResultRowMapImpl recordRow = new JdaResultRowMapImpl();
		JdaTypeConvertFactory convertFactory=session.getTypeConvertFactory();
		ResultObjectFactory resultObjectFactory=session.getResultObjectFactory();
		
		if(paramObject!=null){
			SqlStaticStatement sqlStaticStatement =(SqlStaticStatement)session.getSqlStatement(sqlId);
			ParamUnit[]paramUnits=sqlStaticStatement.getParamUnits();
			
			for(int i=0,n=(paramNames==null)?0:paramNames.length;i<n;i++) {
				try{
					 if(!ParamValueMode.IN.equals(paramValueModes[i])){
						 Object resultValue = persisters[i].get(statement,i+1,convertFactory,recordRow);
						 if(resultValue!=null && ResultSet.class.isInstance(resultValue)){//目标读出属性是ResultSet游标
							 ResultSet cursorResultSet=(ResultSet)resultValue;
							 ResultMapImpl cursorResultMap =(ResultMapImpl)paramUnits[i].getCursorResultMap();		
							 
							 try{
								  if(paramTypes[i]!=null && cursorResultMap!=null){//目标映射不为空
								  	if(ArrayUtil.isArray(paramTypes[i])){//数组类型
									    List dataList = new ArrayList();
									    while(cursorResultSet.next()){
									    	dataList.add(resultObjectFactory.readResultObject(session,connection,sqlStaticStatement,cursorResultMap,cursorResultSet,null));
									  	}
									    
									    Class arrayType = ArrayUtil.getArrayType(paramTypes[i]);
									    resultValue = ArrayUtil.createArray(arrayType,dataList.size());
									    for(int j=0;j<dataList.size();j++){
									    	ArrayUtil.setObject(resultValue,j,session.convert(dataList.get(j),arrayType));
									    }
								  	}else if(Collection.class.isAssignableFrom(paramTypes[i])){//collection类型
									  	Collection col = (Collection)BeanUtil.createInstance(paramTypes[i]);
									  	while(cursorResultSet.next()){
									  		col.add(resultObjectFactory.readResultObject(session,connection,sqlStaticStatement,cursorResultMap,cursorResultSet,null));
									  	}
									  	resultValue =col;
									  }else{//Map与对象类型(单个）
									  	resultValue = BeanUtil.createInstance(paramTypes[i]);
									  	if(!cursorResultMap.getResultClass().isInstance(resultValue))
									  		throw new SqlExecutionException(sqlId,"Result object("+resultValue+")is not an instance of class("+cursorResultMap.getResultClass()+")for cursor out parameter at index:"+i);
									  	
									  	if(cursorResultSet.next())
									     resultObjectFactory.readResultObject(session,connection,sqlStaticStatement,cursorResultMap,cursorResultSet,resultValue);
									  	if(cursorResultSet.next())
											 throw new SqlExecutionException(sqlId,"Found multiple records for cursor out parameter at index:"+i);
									  }
								}else if(paramTypes[i]==null && cursorResultMap!=null){//目标映射单元为空
									  resultValue=null;
								  	if(Map.class.isInstance(paramObject)){//Map parameter Bean 只能读出一个对象
								  		if(cursorResultSet.next())
									  	 resultObjectFactory.readResultObject(session,connection,sqlStaticStatement,cursorResultMap,cursorResultSet,null);
								  		if(cursorResultSet.next())
											 throw new SqlExecutionException(sqlId,"Found multiple records for map bean on cursor out parameter at index:"+i);
								  	}else if(Collection.class.isInstance(paramObject)){//Collection parameter Bean 只能读出一个对象
								  		if(List.class.isInstance(paramObject) && cursorResultSet.next())
								  		 resultValue=resultObjectFactory.readResultObject(session,connection,sqlStaticStatement,cursorResultMap,cursorResultSet,null);
								  		if(List.class.isInstance(paramObject) && cursorResultSet.next())
											 throw new SqlExecutionException(sqlId,"Found multiple records for list bean on cursor out parameter at index:"+i);
								  	}else if(ArrayUtil.isArray(paramObject)){//Array parameter Bean 只能读出一个对象
								  		if(cursorResultSet.next())
								  		 resultValue=resultObjectFactory.readResultObject(session,connection,sqlStaticStatement,cursorResultMap,cursorResultSet,null);
								  		if(cursorResultSet.next())
											 throw new SqlExecutionException(sqlId,"Found multiple records for array bean on cursor out parameter at index:"+i);
								  	}else{//普通对象类型Bean
								  		Class paramType=PropertyUtil.getPropertyType(paramObject.getClass(),paramNames[i]);//依据参数名字获取参数类型
								  		if(paramType!=null){
										  	if(Collection.class.isAssignableFrom(paramType)){//collection类型
											  	Collection col = (Collection)BeanUtil.createInstance(paramType);
											  	while(cursorResultSet.next()){
											  		col.add(resultObjectFactory.readResultObject(session,connection,sqlStaticStatement,cursorResultMap,cursorResultSet,null));
											  	}
											  	resultValue =col;
											  }else if(paramType.isArray()){//数组类型
											    List dataList = new ArrayList();
											    while(cursorResultSet.next()){
											    	dataList.add(resultObjectFactory.readResultObject(session,connection,sqlStaticStatement,cursorResultMap,cursorResultSet,null));
											  	}
											    
											    Class arrayType = ArrayUtil.getArrayType(paramType);
											    resultValue = ArrayUtil.createArray(arrayType,dataList.size());
											    for(int j=0;j<dataList.size();j++){
											    	ArrayUtil.setObject(resultValue,j,session.convert(dataList.get(j),arrayType));
											    }
											  }else{//Map与对象类型(单个)
											  	resultValue = BeanUtil.createInstance(paramType);
											  	if(!cursorResultMap.getResultClass().isInstance(resultValue))
											  		throw new SqlExecutionException(sqlId,"Result object("+resultValue+")is not an instance of class("+cursorResultMap.getResultClass()+")for cursor out parameter at index:"+i);
											
											    resultObjectFactory.readResultObject(session,connection,sqlStaticStatement,cursorResultMap,cursorResultSet,resultValue);
											  	if(cursorResultSet.next())
														throw new SqlExecutionException(sqlId,"Found multiple records for cursor out parameter ");
											  }
								  		}
								  	}
								  }
								}finally{
									CloseUtil.close(cursorResultSet);
							  }
							}
							   
						 if(resultValue!=null)
					    resultObjectFactory.setPropertyValue(session,sqlId,paramObject,i,paramNames[i],resultValue);
						}
				 } catch (Throwable e) {
					throw new SqlExecutionException(sqlId,"Failed to read call statement out value on index:"+i,e);
				}
			}
		}
	}
}
