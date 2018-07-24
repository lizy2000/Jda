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
package org.jmin.jda.impl.persister.date;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.jmin.jda.JdaException;
import org.jmin.jda.JdaResultRowMap;
import org.jmin.jda.JdaTypeConvertFactory;
import org.jmin.jda.impl.exception.SqlExecutionException;
import org.jmin.jda.impl.persister.ObjectHandler;

/**
 * 日期
 *
 * @author Chris
 */
public class DateTimestampHandler extends ObjectHandler {
	
	/**
	 * 获得持久化类型
	 */
	public Class getPersisterType(){
		return Timestamp.class;
	}
	
  /**
   * 读取结果对象
   */
  public Object get(ResultSet rs,int index,JdaTypeConvertFactory convertFactory,JdaResultRowMap rowMap)throws SQLException{
		try {
			if(rowMap.exists(index)) {
				return convertFactory.convert(rowMap.get(index),this.getPersisterType());
			}else{
				Object value = rs.getTimestamp(index);
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
				Object value = rs.getTimestamp(columnName);
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
				Object value = cs.getTimestamp(index);
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
  	Timestamp parameter =(Timestamp)convertFactory.convert(value,this.getPersisterType());
  	if(parameter== null)
  	  this.setNullParamValue(ps,index,typeCode);
  	else
  		ps.setTimestamp(index,parameter);
   } catch (Throwable e) {
	 		throw new JdaException(null,e);
	 }	
  }
}