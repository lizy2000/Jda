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
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.jmin.jda.JdaTypePersister;
import org.jmin.jda.impl.JdaSessionImpl;
import org.jmin.jda.impl.dynamic.DynSqlFactory;
import org.jmin.jda.impl.dynamic.DynSqlResult;
import org.jmin.jda.impl.exception.ParamMapException;
import org.jmin.jda.impl.exception.SqlIllegalAccessException;
import org.jmin.jda.impl.execution.worker.ParamObjectFactory;
import org.jmin.jda.impl.statement.SqlBaseStatement;
import org.jmin.jda.impl.statement.SqlDynStatement;
import org.jmin.jda.impl.statement.SqlOperationType;
import org.jmin.jda.impl.statement.SqlStaticStatement;
import org.jmin.jda.impl.util.ArrayUtil;
import org.jmin.jda.impl.util.ClassUtil;
import org.jmin.jda.mapping.ParamMap;
import org.jmin.jda.mapping.ParamUnit;
import org.jmin.jda.mapping.ParamValueMode;

/**
 * SQL请求构造工厂
 * 
 * @author Chris Liao
 */

public class SqlRequestFactory {
	
	/**
	 * 动态SQL工厂
	 */
	private DynSqlFactory dynSqlFactory = new DynSqlFactory();
	
	/**
	 * 构造一个SQL请求
	 */
	public SqlRequest createSqlRequest(JdaSessionImpl session,SqlBaseStatement statement,Object paramObj,SqlOperationType sqlOperationType)throws SQLException{
		String sqlId=statement.getSqlId();
		this.checkOperationType(sqlId,statement.getSqlType(),sqlOperationType);
		this.checkParamObject(session,sqlId,statement,statement.getParamClass(),paramObj);
		
		SqlRequest request=new SqlRequest(session,sqlId,statement.getParamClass(),paramObj);
		String targetSQL=null;
		ParamUnit[] paramUnits=null;
		
		if(statement instanceof SqlStaticStatement){
			SqlStaticStatement st = (SqlStaticStatement)statement;
			targetSQL = st.getSql();
			ParamMap paramMap = st.getParamMap();
			if(paramMap!=null)
				paramUnits = paramMap.getParamUnits();
		}else{
			SqlDynStatement st =(SqlDynStatement)statement;
			DynSqlResult result = dynSqlFactory.crate(sqlId,st.getDynTags(),paramObj,session);
			targetSQL = result.getSqlText();
			paramUnits=result.getParamUnits();
			request.setDynSQL(true);
		}

		request.setSqlText(targetSQL);
		initRequest(statement.getParamClass(),request,paramUnits);
		return request;
	}
	
	/**
	 * 初始Request,主要设置一些Request中的成员
	 */
  private void initRequest(Class paramClass,SqlRequest request,ParamUnit[] paramUnits)throws SQLException{
	  	Object sqlID = request.getSqlId();
	  	Object paramObject = request.getParamObject();
	    JdaSessionImpl session = request.getRequestSession();
	   	
		int paramLen=(paramUnits==null)?0:paramUnits.length;
		String[]paramNames = new String[paramLen];//参数属性名
		Object[]paramValues = new Object[paramLen];//设置参数属值
		Class[] paramTypes = new Class[paramLen];//设置参数属性类型
		int[] sqlTypes = new int[paramLen];//设置参数属性数据库的SQL类型代码
		ParamValueMode[] modes = new ParamValueMode[paramLen];//设置参数属性参数值模式
		JdaTypePersister[] persisters = new JdaTypePersister[paramLen];//设置参数持久器
			
		request.setParamNames(paramNames);
		request.setParamValues(paramValues);
		request.setParamValueTypes(paramTypes);
		request.setParamSqlTypeCodes(sqlTypes);
		request.setParamValueModes(modes);
		request.setParamTypePersisters(persisters);
		
		 if(paramLen >0){
			 if(paramObject.getClass().isArray()){//数组类型参数
				 if(ArrayUtil.getArraySize(paramObject) < paramLen)
 				throw new ParamMapException(sqlID,"Array parameter size is not enough,target size is("+paramLen+")");
			  for(int i = 0; i < paramLen; i++){
				  paramValues[i] = Array.get(paramObject,i);
				  this.setParameterMapping(i,paramUnits,paramNames,paramValues,paramTypes,sqlTypes,modes,persisters,session);
			  }
			 }else if(paramObject instanceof Collection){//Collection类型参数
				 int i = 0;
				 Collection col=(Collection)paramObject;
				 if(col.size() < paramLen)
	 			 throw new ParamMapException(sqlID,"List parameter size is not enough,target size is("+paramLen+")");
				 Iterator itor =col.iterator();
				 while(i < paramLen){
				  paramValues[i] = itor.next();
				  this.setParameterMapping(i++,paramUnits,paramNames,paramValues,paramTypes,sqlTypes,modes,persisters,session);
				 }
			}else if(paramObject instanceof Map){//Map类型参数
				Map map = (Map)paramObject;
				if(map.size() < paramLen)
		 		throw new ParamMapException(sqlID,"Map parameter size is not enough,target size is("+paramLen+")");
				for(int i = 0; i < paramLen; i++){
				 paramValues[i] = map.get(paramUnits[i].getPropertyName());
				 this.setParameterMapping(i,paramUnits,paramNames,paramValues,paramTypes,sqlTypes,modes,persisters,session);
				}
			}else if(paramLen ==1 && session.supportsPersisterType(paramObject.getClass())){//单个类型参数
				paramValues[0] = paramObject;
				this.setParameterMapping(0,paramUnits,paramNames,paramValues,paramTypes,sqlTypes,modes,persisters,session);
	  }else{//对象类型参数
	  	if(paramClass !=null && !ClassUtil.isAcceptableInstance(paramClass,paramObject))
	  	 throw new ParamMapException(sqlID,"Parameter object can't match paramter mapping class["+paramClass+"]");
	  	ParamObjectFactory paramObjectFactory =session.getParamObjectFactory();
				for(int i=0;i<paramLen;i++){
				  paramValues[i]= paramObjectFactory.getPropertyValue(session,sqlID,paramObject,paramUnits[i].getPropertyName());
				  this.setParameterMapping(i,paramUnits,paramNames,paramValues,paramTypes,sqlTypes,modes,persisters,session);
				}
		  }
		}
 }
  
  /**
	 * 设置参数对象
	 */
	private void setParameterMapping(int i,ParamUnit[] paramUnits,
			String[]paramNames,Object[]paramValues,Class[] paramTypes,int[] sqlTypes,
			ParamValueMode[] modes,JdaTypePersister[] persisters,JdaSessionImpl session)throws SQLException{
		
		 ParamUnit paramUnit=paramUnits[i];
		 paramNames[i]=paramUnit.getPropertyName();
		 sqlTypes[i] = paramUnit.getParamColumnTypeCode();
		 modes[i]=paramUnit.getParamValueMode();
		 if(paramUnit.getPropertyType()!=null)
			 paramTypes[i]= paramUnit.getPropertyType();
		 else
			 paramTypes[i]= (paramValues[i]==null)?Object.class:paramValues[i].getClass();
		
		 if(paramUnit.getJdbcTypePersister()!=null){
			 persisters[i]=paramUnit.getJdbcTypePersister();
	 	}else{
		  persisters[i]= session.getTypePersister(paramTypes[i],paramUnit.getParamColumnTypeName());
		 }
	}
  
	/**
	 * 检查操作类型
	 */
	private void checkOperationType(Object id,SqlOperationType defineType,SqlOperationType requestType)throws SQLException{
		if(SqlOperationType.Insert.equals(requestType) && !SqlOperationType.Insert.equals(defineType)){//请求类型为: Insert
			throw new SqlIllegalAccessException(id,"Target definition is not a insert sql");
		}else if(SqlOperationType.Delete.equals(requestType)&& !SqlOperationType.Delete.equals(defineType)){//请求类型为: delete
		 throw new SqlIllegalAccessException(id,"Target definition is not a delete sql");
		}else if(SqlOperationType.Update.equals(requestType)&& !SqlOperationType.Update.equals(defineType) && !SqlOperationType.Procedure.equals(defineType) && !SqlOperationType.Unknown.equals(defineType)){//请求类型为: update
			throw new SqlIllegalAccessException(id,"Target definition is not a update sql");
		}else if(SqlOperationType.Select.equals(requestType) && !SqlOperationType.Select.equals(defineType) && !SqlOperationType.Procedure.equals(defineType)){
			throw new SqlIllegalAccessException(id,"Target definition is not a select sql");
		}
	}
	
	/**
	* 检查参数对象是否正确
	*/
	private void checkParamObject(JdaSessionImpl session,Object id,SqlBaseStatement statement,Class paramClass,Object paramObject)throws SQLException{
	 //针对静态定义的可以允许有数组参数
	 if(statement instanceof SqlStaticStatement){
		 SqlStaticStatement Statement = (SqlStaticStatement)statement;
		 ParamUnit[] untis = Statement.getParamUnits();
		 int unitLen =(untis==null)?0:untis.length;
		 if(unitLen > 0 && paramObject==null)
			throw new ParamMapException(id,"Parameter object can't be null");
		}else {
			if(paramObject==null)
			  throw new ParamMapException(id,"Parameter object can't be null");
			if(paramObject.getClass().isArray())
				throw new ParamMapException(id,"Dynamic sql don't support array type parameter");
		  if(paramObject instanceof Collection)
				throw new ParamMapException(id,"Dynamic sql don't support collection type parameter");
		  if(paramObject instanceof Iterator)
				throw new ParamMapException(id,"Dynamic sql don't support iterator type parameter");
			if(!ClassUtil.isAcceptableInstance(paramClass,paramObject)){
				throw new ParamMapException(id,"Parameter object can't match paramter class["+paramClass+"]");
			}
		}
	}
}
