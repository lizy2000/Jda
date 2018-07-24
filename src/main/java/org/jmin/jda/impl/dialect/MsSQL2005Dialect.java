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
package org.jmin.jda.impl.dialect;

import java.sql.SQLException;

import org.jmin.jda.statement.SqlPropertyTable;

/**
 * Ms SQL2005分页查询
 * 
 * @author Chris
 * @version 1.0
 */
public class MsSQL2005Dialect extends MsSQLBaseDialect {

	/**
	 * 是否需要移动到startRow
	 */
	public boolean pageQueryResultNeedMove()throws SQLException{
		return false;
	}
	
	/**
	 * 构造分页查询SQL
	 */
	public String getPageQuerySql(String sql,int startRow,int offset,SqlPropertyTable table)throws SQLException{
		int orderByIndex = sql.toLowerCase().lastIndexOf(" order by ");  
	  if(orderByIndex <= 0)
		  throw new SQLException("must specify 'order by' statement to support limit operation with offset in sql server 2005");  
	  
    String sqlOrderBy = sql.substring(orderByIndex + 10);  
	  String sqlRemoveOrderBy = sql.substring(0,orderByIndex);  
	  StringBuffer sqlBuffer = new StringBuffer();
	  sqlBuffer.append(" with pageTable as (select row_number() over(order by ").append(sqlOrderBy).append(")rowNumber, *");
	  sqlBuffer.append(" from (").append(sqlRemoveOrderBy).append(")tt) select * from pageTable where rowNumber between ").append(startRow).append(" and ").append((startRow+offset-1));
	  
	  return sqlBuffer.toString();
	}
}
