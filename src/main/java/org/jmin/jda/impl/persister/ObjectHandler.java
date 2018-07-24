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
package org.jmin.jda.impl.persister;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.jmin.jda.JdaResultRowMap;
import org.jmin.jda.JdaTypeConvertFactory;
import org.jmin.jda.JdaTypePersister;
import org.jmin.jda.impl.exception.SqlExecutionException;
import org.jmin.jda.impl.statement.SqlFieldTypes;

/**
 * Boolean Type
 * 
 * @author Chris
 */
public class ObjectHandler implements JdaTypePersister {
	
	/**
	 * 获得持久化类型
	 */
	public Class getPersisterType(){
		return Object.class;
	}

  /**
   * 读取结果对象
   */
  public Object get(ResultSet rs,int index,JdaTypeConvertFactory convertFactory,JdaResultRowMap rowMap)throws SQLException{
		try {
			if(rowMap.exists(index)) {
				return convertFactory.convert(rowMap.get(index),this.getPersisterType());
			}else{
				Object value = rs.getObject(index);
				rowMap.set(index, value);
				return convertFactory.convert(value,this.getPersisterType());
			}
		} catch (Throwable e) {
			throw new SqlExecutionException(null,"Failed to read result on index["+index+"]",e);
		}
  }
  
  /**
   * 读取结果对象
   */
  public Object get(ResultSet rs,String columnName,JdaTypeConvertFactory convertFactory,JdaResultRowMap rowMap)throws SQLException{
		try {
			if(rowMap.exists(columnName)){
				return convertFactory.convert(rowMap.get(columnName),this.getPersisterType());
			}else{
				Object value = rs.getObject(columnName);
				rowMap.set(columnName,value);
				return convertFactory.convert(value,this.getPersisterType());
			}
		} catch (Throwable e) {
			throw new SqlExecutionException(null,"Failed to read result on column name["+columnName+"]",e);
		}
  }
  
  /**
   * 读取结果对象
   */
  public Object get(CallableStatement cs,int index,JdaTypeConvertFactory convertFactory,JdaResultRowMap rowMap)throws SQLException{
  	try {
			if(rowMap.exists(index)) {
				return convertFactory.convert(rowMap.get(index),this.getPersisterType());
			}else{
				Object value = cs.getObject(index);
				rowMap.set(index, value);
				return convertFactory.convert(value,this.getPersisterType());
			}
		} catch (Throwable e) {
			throw new SqlExecutionException(null,"Failed to read result on index["+index+"]",e);
		}
  }
  
	 /**
   * 设置参数
   */
  public void set(PreparedStatement ps,int index,Object value,int typeCode,JdaTypeConvertFactory convertFactory)throws SQLException{
   try{
  	Object parameter = convertFactory.convert(value,this.getPersisterType());
  	if(parameter == null)
  		this.setNullParamValue(ps,index,typeCode);
  	else
  		ps.setObject(index,parameter);
  	} catch (Throwable e) {
			throw new SqlExecutionException(null,"Failed to set parameter on index["+index+"]",e);
		}
  }
 
	/**
	 * 存放默认的类到SQLMap的映射
	 */
	private Map classDefaultSqlTypeMap = new HashMap();
 
	/**
	 * 构造函数
	 */
	public ObjectHandler(){
		classDefaultSqlTypeMap.put(Boolean.class,Integer.valueOf(SqlFieldTypes.BIT));
		classDefaultSqlTypeMap.put(Byte.class,Integer.valueOf(SqlFieldTypes.TINYINT));
		classDefaultSqlTypeMap.put(Short.class,Integer.valueOf(SqlFieldTypes.SMALLINT));
		classDefaultSqlTypeMap.put(Integer.class,Integer.valueOf(SqlFieldTypes.INTEGER));
		classDefaultSqlTypeMap.put(Long.class,Integer.valueOf(SqlFieldTypes.BIGINT));
		classDefaultSqlTypeMap.put(Float.class,Integer.valueOf(SqlFieldTypes.REAL));
		classDefaultSqlTypeMap.put(Double.class,Integer.valueOf(SqlFieldTypes.DOUBLE));
		classDefaultSqlTypeMap.put(String.class,Integer.valueOf(SqlFieldTypes.VARCHAR));
		classDefaultSqlTypeMap.put(Character.class,Integer.valueOf(SqlFieldTypes.CHAR));
		
		classDefaultSqlTypeMap.put(Blob.class,Integer.valueOf(SqlFieldTypes.BLOB));
		classDefaultSqlTypeMap.put(Clob.class,Integer.valueOf(SqlFieldTypes.CLOB));
		classDefaultSqlTypeMap.put(Calendar.class,Integer.valueOf(SqlFieldTypes.DATE));
		classDefaultSqlTypeMap.put(Date.class,Integer.valueOf(SqlFieldTypes.DATE));
		classDefaultSqlTypeMap.put(Time.class,Integer.valueOf(SqlFieldTypes.TIME));
		classDefaultSqlTypeMap.put(Timestamp.class,Integer.valueOf(SqlFieldTypes.TIMESTAMP));
		classDefaultSqlTypeMap.put(java.util.Date.class,Integer.valueOf(SqlFieldTypes.DATE));
		classDefaultSqlTypeMap.put(BigDecimal.class ,Integer.valueOf(SqlFieldTypes.NUMERIC));
		classDefaultSqlTypeMap.put(BigInteger.class ,Integer.valueOf(SqlFieldTypes.BIGINT));
	}
	
	/**
	 * SQL的数据类型
	 */
	private int[]types= new int[]{
		 SqlFieldTypes.VARCHAR,SqlFieldTypes.CHAR,SqlFieldTypes.LONGVARCHAR, 	
	   SqlFieldTypes.BIT,SqlFieldTypes.TINYINT,SqlFieldTypes.SMALLINT,	
	   SqlFieldTypes.INTEGER,SqlFieldTypes.BIGINT,SqlFieldTypes.FLOAT, 		
	   SqlFieldTypes.REAL,SqlFieldTypes.DOUBLE,SqlFieldTypes.NUMERIC,SqlFieldTypes.DECIMAL,		
	   SqlFieldTypes.DATE,SqlFieldTypes.TIME,SqlFieldTypes.TIMESTAMP, 
	   SqlFieldTypes.BINARY,SqlFieldTypes.VARBINARY,SqlFieldTypes.LONGVARBINARY, 	
	   SqlFieldTypes.NULL,SqlFieldTypes.JAVA_OBJECT,        
	   SqlFieldTypes.DISTINCT,SqlFieldTypes.STRUCT,SqlFieldTypes.ARRAY,              
	   SqlFieldTypes.BLOB,SqlFieldTypes.CLOB,SqlFieldTypes.REF,                 
	   SqlFieldTypes.DATALINK,SqlFieldTypes.BOOLEAN,SqlFieldTypes.OTHER
	};

	/**
	 * 设置Null值调用方法
	 */
	protected void setNullParamValue(PreparedStatement statement,int index,int typeCode)throws SQLException{
		SQLException ee=null;
		boolean successful=false;
		if(typeCode == SqlFieldTypes.NO_SET){//如果当前的列类型没有设置，则需依据持久化类型选择一个SQL Type
			Integer sqlType =(Integer)classDefaultSqlTypeMap.get(this.getPersisterType());
			if(sqlType!=null)typeCode =sqlType.intValue();
			if(sqlType==null)typeCode =SqlFieldTypes.JAVA_OBJECT;
		}
		
		try{
			statement.setNull(index,typeCode);
			successful=true;
		}catch(SQLException e){
			ee=e;
			successful=false;
		}
 	
		if(!successful){
			for(int i=0,n=types.length;i<n;i++){
				try{
					statement.setNull(index,types[i]);
					successful=true;
					break;
				}catch(SQLException e){
					continue;
				}
			}
		}
		
		if(!successful&&ee!=null)
			throw new SQLException(ee.getMessage());
	}
}
