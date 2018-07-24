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
 *  Ms SQL2012 分页查询
 * 
 * @author Chris
 * @version 1.0
 */
public class MsSQL2012Dialect extends MsSQLBaseDialect {
	
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
	  return new StringBuffer(sql).append(" OFFSET ").append(startRow).append(" ROWS FETCH NEXT ").append(offset).append("ROWS ONLY").toString();
	}
}
