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

import java.sql.SQLException;

import org.jmin.jda.statement.SqlPropertyTable;

/**
 * SQL方言
 * 
 * @author Chris
 */
public interface JdaDialect {
	
	/**
	 * 是否需要移动到startRow
	 */
	public boolean pageQueryResultNeedMove()throws SQLException;
	
	/**
	 * 获得数目统计SQL
	 */
	public String getRecordCountSQL(String sqlText,SqlPropertyTable table)throws SQLException;
	
	/**
	 * 将一个普通SQL转换为分页查询的语句
	 */
	public String getPageQuerySql(String sqlText,int startRow,int offset,SqlPropertyTable table)throws SQLException;
	
}
