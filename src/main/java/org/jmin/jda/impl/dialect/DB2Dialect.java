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
 * DB2区间查询
 * 
 * @author Chris
 * @version 1.0
 */
public class DB2Dialect extends JdaBaseDialect {
	
	/**
	 * 将一个普通SQL转换为分页查询的语句
	 */
	public String getPageQuerySql(String sqlText,int startRow,int offset,SqlPropertyTable table)throws SQLException{
		return new StringBuffer("SELECT * FROM ( ")
			.append("SELECT ROW_NUMBER() OVER() AS RN, PAGETEMP.* FROM ")
			.append("(").append(sqlText).append(")").append(JdaBaseDialect.Table_Jmin_Jda_Record_Page_View)
			.append(" WHERE RN >= ").append(startRow).append(" AND RN <=").append((startRow + offset-1)).toString();
	}
}
