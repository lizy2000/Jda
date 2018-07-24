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

public class InterBaseDialect extends JdaBaseDialect {
	
	/**
	 * 将一个普通的查询SQL语句转换为方言可以执行的SQL
	 * QUERY_SQL row ? to ?
	 */
	public String getPageQuerySql(String sqlText,int startRow,int offset,SqlPropertyTable table)throws SQLException{ 
	  return new StringBuffer(sqlText).append(" row ").append(+startRow).append(" to ").append(startRow+offset-1).toString();
	}
}