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
package org.jmin.jda;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Jdbc type Persister
 * 
 * @author Chris
 * @version 1.0
 */
public interface JdaTypePersister {
	
	/**
	 * 获得持久化类型
	 */
	public Class getPersisterType();
	
	
  /**
   * 读取结果对象
   */
  public Object get(ResultSet rs,int index,JdaTypeConvertFactory convertFactory,JdaResultRowMap rowMap)throws SQLException;
  
  /**
   * 读取结果对象
   */
  public Object get(ResultSet rs,String column,JdaTypeConvertFactory convertFactory,JdaResultRowMap rowMap)throws SQLException;
  
  /**
   * 读取结果对象
   */
  public Object get(CallableStatement cs,int index,JdaTypeConvertFactory convertFactory,JdaResultRowMap rowMap)throws SQLException;
  
	 /**
   * 设置参数
   */
  public void set(PreparedStatement ps,int index,Object value,int typeCode,JdaTypeConvertFactory convertFactory)throws SQLException;

}
