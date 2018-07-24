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

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jmin.jda.JdaResultRowMap;
import org.jmin.jda.JdaTypeConvertFactory;
import org.jmin.jda.impl.JdaSessionImpl;
import org.jmin.jda.impl.exception.ResultMapException;
import org.jmin.jda.impl.exception.SqlExecutionException;
import org.jmin.jda.impl.execution.SqlRequest;
import org.jmin.jda.impl.execution.base.JdaResultRowMapImpl;
import org.jmin.jda.impl.execution.select.ObjectRelateFinder;
import org.jmin.jda.impl.mapping.result.RelationUnitImpl;
import org.jmin.jda.impl.mapping.result.ResultMapImpl;
import org.jmin.jda.impl.mapping.result.ResultUnitImpl;
import org.jmin.jda.impl.property.PropertyUtil;
import org.jmin.jda.impl.statement.SqlBaseStatement;
import org.jmin.jda.impl.statement.SqlOperationType;
import org.jmin.jda.impl.statement.SqlStaticStatement;
import org.jmin.jda.impl.util.ArrayUtil;
import org.jmin.jda.impl.util.BeanUtil;
import org.jmin.jda.impl.util.StringUtil;
import org.jmin.jda.mapping.ParamUnit;
import org.jmin.jda.mapping.RelationUnit;
import org.jmin.jda.mapping.ResultMap;
import org.jmin.jda.mapping.ResultUnit;

/**
 * 结果辅助类
 * 
 * @author chris 
 */

public class ResultObjectFactory {
	
	/**
	 * 设置属性值
	 */
	public void setPropertyValue(JdaSessionImpl session,Object sqlId,Object bean,int propertyIndex,String propertyName,Object value)throws SQLException{
		try {
			if(bean!=null){
				if(Collection.class.isInstance(bean)){
					if(List.class.isInstance(bean)){
						List listBean = (List)bean;
						listBean.set(propertyIndex,value);
					} 
				}else if(bean.getClass().isArray()){
					Class arrayType = ArrayUtil.getArrayType(bean.getClass());
					Object newValue = session.convert(value,arrayType);
					ArrayUtil.setObject(bean,propertyIndex,newValue);
				}else if(!StringUtil.isNull(propertyName)){
					PropertyUtil.setPropertyValue(bean,propertyName,value,session);
				}else{
					if(!StringUtil.isNull(propertyName))
					 throw new SqlExecutionException(sqlId,"Failed to set property value on name:"+propertyName);
					else
					 throw new SqlExecutionException(sqlId,"Failed to set property value on index:"+(propertyIndex+1));
				}
			}
		} catch (Throwable e) {
			throw new SqlExecutionException(sqlId,e);
		}
	}
	
	/**
	 * 依据映射读出结果
	 */
	public Object readResultObject(JdaSessionImpl session,Connection con,SqlBaseStatement statement,ResultSet resultSet,Object resultObject)throws SQLException{
		return this.readResultObject(session,con,statement,null,resultSet,resultObject);
	}
	
	/**
	 * 依据映射读出结果
	 */
	public Object readResultObject(JdaSessionImpl session,Connection con,SqlBaseStatement statement,ResultMapImpl resultMap,ResultSet resultSet,Object resultObject)throws SQLException{
		try {
			Class resultLazyClass = null;
			Object sqlId=statement.getSqlId();
			Class resultClass= statement.getResultClass();
			
			JdaTypeConvertFactory convertFactory = session.getTypeConvertFactory();
			ResultUnit[]resultUnits=this.getResultProperty(session,statement,resultMap,resultSet);
			RelationUnit[]relationUnits=this.getRelationUnits(session,statement,resultSet);
			
			if(resultMap==null)
			 resultMap =(ResultMapImpl)statement.getResultMap();
			resultLazyClass = resultMap.getLayLoadresultClass();
			
			if(session.supportsConversionType(resultClass)){//直接可映射结果类
				String columnName = resultUnits[0].getResultColumnName();
				if(StringUtil.isNull(columnName))
					columnName = resultSet.getMetaData().getColumnName(1);
				 return resultUnits[0].getJdbcTypePersister().get(resultSet,columnName,convertFactory,new JdaResultRowMapImpl());
			}else{
				JdaResultRowMap RowCache = new JdaResultRowMapImpl();
				if(resultObject==null)
					resultObject=BeanUtil.createInstance(resultLazyClass);
				
				//读出结果属性
				List fieldNameList = this.getResultFieldNames(resultSet);
				for(int i=0,n=resultUnits.length;i<n;i++) {
					ResultUnitImpl unit =(ResultUnitImpl)resultUnits[i];
          if(fieldNameList.contains(unit.getResultColumnName())){
					 Object propetyValue= unit.getJdbcTypePersister().get(resultSet,unit.getResultColumnName(),convertFactory,RowCache);
					 this.setPropertyValue(session,sqlId,resultObject,i,unit.getPropertyName(),propetyValue);
          }
				 }
				
				ObjectRelateFinder objectRelateFinder=session.getObjectRelateFinder();
				RelationObjectFactory relationObjectFactory=session.getRelationObjectFactory();
				
				
				//读出关联属性
				for(int i=0,n=(relationUnits==null)?0:relationUnits.length;i<n;i++) {
					RelationUnitImpl unit =(RelationUnitImpl)relationUnits[i];
					SqlBaseStatement subStatement=session.getSqlStatement(unit.getSqlId());	
					if(!(subStatement instanceof SqlStaticStatement))
						throw new SqlExecutionException(sqlId,"Relation unit["+i+":"+unit.getPropertyName()+"]sql("+unit.getSqlId()+")is not a static statement");
					if(!SqlOperationType.Select.equals(subStatement.getSqlType()))
						throw new ResultMapException(sqlId,"Relation unit["+i+":"+unit.getPropertyName()+"]sql("+unit.getSqlId()+")is not select statement");
					
					SqlStaticStatement relateStatement =(SqlStaticStatement)subStatement;
					String relationSqlId=relateStatement.getSqlId();
					Class relationParamType =relateStatement.getParamClass();
					ParamUnit[]paramUnits=relateStatement.getParamUnits();
					String[]relateColumns=relationUnits[i].getRelationColumnNames();
					
					int relateParamsLen =(paramUnits!=null)?paramUnits.length:0;
					int columnNamesLen =(relateColumns!=null)?relateColumns.length:0;
					if(relateParamsLen!=columnNamesLen)
						throw new SqlExecutionException(sqlId,"Relation unit["+i+":"+unit.getPropertyName()+"]column names length not match sql("+unit.getSqlId()+")parameter units");
				
					Object[] relateParamValues = readRelationValue(sqlId,RowCache,resultSet,relateColumns,session);
					SqlRequest request = new SqlRequest(session,relationSqlId,relationParamType,new Object());
					request.setDefinitionType(subStatement.getSqlType());
					request.setSqlText(relateStatement.getSql());
					
					
					//设置关联查询的属性
					request.setParamValues(relateParamValues);
					request.setParamNames(relationObjectFactory.getParamNames(paramUnits));
 				  request.setParamValueTypes(relationObjectFactory.getParamTypes(paramUnits,request.getParamValues()));
					request.setParamSqlTypeCodes(relationObjectFactory.getParamSQLTypes(paramUnits));
					request.setParamValueModes(relationObjectFactory.getParamValueMode(paramUnits));
					request.setParamTypePersisters(relationObjectFactory.getParamTypePersisters(session,paramUnits,relateParamValues));
					request.setRelationUnit(unit);
					//设置关联查询的属性
					
					if(!unit.isLazyLoad()){//不需要延迟加载属性
						request.setConnection(con);
						Object relatePropValue = objectRelateFinder.getRelationValue(request,false);
						this.setPropertyValue(session,sqlId,resultObject,i,unit.getPropertyName(),relatePropValue);
					}else{//需要延迟加载属性
					 	String lazyPropertyName=unit.getPropertyName() +"$SqlRequest";
					 	String methodName = PropertyUtil.getPropertySetMethodName(lazyPropertyName);
					 	Method method = resultLazyClass.getDeclaredMethod(methodName, new Class[]{org.jmin.jda.impl.execution.SqlRequest.class});
					 	BeanUtil.invokeMethod(resultObject,method,new Object[]{request});
					}	
				}
 
				return resultObject;
			}
		}catch(Throwable e){
			throw new SqlExecutionException(statement.getSqlId(),e.getMessage(),e);
		}
	}
	
	
	/**
	* 读取结果集，并将注入结果对象中
	*/
	private Object[] readRelationValue(Object sqlId,JdaResultRowMap rowCache,ResultSet resultSet,String[] relateColumnNames,JdaSessionImpl session)throws SQLException{
		Object[] relateParamValues = new Object[relateColumnNames==null?0:relateColumnNames.length];
		for(int i=0,n=relateParamValues.length;i<n;i++){
			if(rowCache!=null && rowCache.exists(relateColumnNames[i].trim().toLowerCase()))
				relateParamValues[i] = rowCache.get(relateColumnNames[i].trim().toLowerCase());
			else
				relateParamValues[i] = resultSet.getObject(relateColumnNames[i].trim().toLowerCase());
		}
		
		return relateParamValues;
	}

	/**
	 * 读出结果映射属性
	 */
	public ResultUnit[] getResultProperty(JdaSessionImpl session,SqlBaseStatement sqlStatement,ResultMap resultMap,ResultSet resultSet)throws SQLException{
		if(resultMap==null)
		 resultMap = sqlStatement.getResultMap();
		ResultUnit[] resultUnits = resultMap.getResultUnits();
		if(resultUnits== null || resultUnits.length ==0){
			readResultUnitFromResult(session,sqlStatement,resultMap,resultSet);
		}
		return resultMap.getResultUnits();
	}
	
	/**
	 *从结果集中，读出结果映射属性,需要保持同步
	 */
	private void readResultUnitFromResult(JdaSessionImpl session,SqlBaseStatement sqlStatement,ResultMap resultMap,ResultSet resultSet)throws SQLException{
		if(resultMap==null)
		 resultMap = sqlStatement.getResultMap();
		ResultUnit[] resultUnits = resultMap.getResultUnits();
		if(resultUnits== null || resultUnits.length ==0){
			resultUnits = createResultProperty(session,resultMap.getResultClass(),resultSet);
			((ResultMapImpl)resultMap).setResultUnits(resultUnits);
		}
	}
	
	/**
	 * 获得关联属性,保持同步
	 */
	private RelationUnit[] getRelationUnits(JdaSessionImpl session,SqlBaseStatement sqlStatement,ResultSet resultSet)throws SQLException{
		if(sqlStatement instanceof SqlStaticStatement){
			SqlStaticStatement staticSQL = (SqlStaticStatement)sqlStatement;
			return staticSQL.getRelationUnits();
		}else{
			return null;
		}
	}
	
	
	/**
	 * 当前查询结果不存在一个结果映射时候，则调用该方法获取结果映射属性
	 */
	private ResultUnit[] createResultProperty(JdaSessionImpl session,Class resultClass,ResultSet resultSet)throws SQLException{
		ResultSetMetaData meta = resultSet.getMetaData();
		int resultFieldCount = meta.getColumnCount();
		                       
		if(Map.class.isAssignableFrom(resultClass)){//如果结果类为Map,则结果集合中所有字段都接受
			ResultUnit[]properties = new ResultUnit[resultFieldCount];
			for(int i=0;i<resultFieldCount;i++){
				properties[i]= new ResultUnitImpl(meta.getColumnName(i+1),Object.class);
				properties[i].setResultColumnName(meta.getColumnName(i+1));
				((ResultUnitImpl)properties[i]).setJdbcTypePersister(session.getTypePersister(Object.class));
			}
			return properties;
		}else{
			if(session.supportsConversionType(resultClass)){//结果类为直接可映射类
				ResultUnit[]properties = new ResultUnit[1];
				properties[0]= new ResultUnitImpl(null,resultClass);
				properties[0].setResultColumnName(meta.getColumnName(1));
				((ResultUnitImpl)properties[0]).setJdbcTypePersister(session.getTypePersister(resultClass));
				return properties;
			}else{//结果类为不可直接可映射类
				List resultPropertyList = new ArrayList();
				Method[] methods = resultClass.getMethods();
				String methodName = null;
				Class[] paramTypes = null;
				for(int i=0;i<resultFieldCount;i++){
					for(int j=0,n=methods.length;j<n;j++){
					  methodName = methods[j].getName();  
					  paramTypes = methods[j].getParameterTypes();
					 
					 if(methodName.equalsIgnoreCase("set"+meta.getColumnName(i+1))&& paramTypes.length==1 && session.supportsConversionType(paramTypes[0])){
						  String propertyName = methodName.substring(3,4).toLowerCase() + methodName.substring(4);
							ResultUnit unit = new ResultUnitImpl(propertyName,paramTypes[0]);
							unit.setResultColumnName(meta.getColumnName(i+1));
							((ResultUnitImpl)unit).setJdbcTypePersister(session.getTypePersister(unit.getPropertyType()));
							resultPropertyList.add(unit);
				  }
				}
			}
			return (ResultUnit[])resultPropertyList.toArray(new ResultUnit[resultPropertyList.size()]);
		}
	 }
  }
	
  /**
	 * 查找出结果列表中的字段名
	 */
  private List getResultFieldNames(ResultSet resultSet)throws SQLException{
	 ResultSetMetaData meta = resultSet.getMetaData();
	 int fieldSize = meta.getColumnCount();
	 List fieldNameList = new ArrayList(fieldSize);
	 for(int i=1;i<=fieldSize;i++){
		String name=meta.getColumnLabel(i);
		if(StringUtil.isNull(name))
		 name= meta.getColumnName(i);
		if(!StringUtil.isNull(name))
		 fieldNameList.add(name.toUpperCase());
	 }
	 return fieldNameList;
	}
}
