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
 * Oracle区间查询
 * 
 * @author Chris
 * @version 1.0
 */
public class OracleDialect extends JdaBaseDialect {
	
	/**
	 * 将一个普通的查询SQL语句转换为方言可以执行的SQL
	 * 	
	 * select * from (select a.*,rownum no from (SQL)a where rownum <=10) b where b.no >= 1
	 */
	public String getPageQuerySql(String sql,int startRow,int offset,SqlPropertyTable table)throws SQLException{
		StringBuffer buff = new StringBuffer(100);
	  buff.append("select * from (select Jmin_Jda_Record_Page_View_A.*,rownum no from (");
	  buff.append(sql);
	  buff.append(")Jmin_Jda_Record_Page_View_A where rownum <=").append((startRow+offset-1)).append(")Jmin_Jda_Record_Page_View_B where Jmin_Jda_Record_Page_View_B.no >=").append(startRow);   
	  return buff.toString();  
	}
}
