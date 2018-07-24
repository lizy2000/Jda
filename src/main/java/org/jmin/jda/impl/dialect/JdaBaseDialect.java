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

import org.jmin.jda.JdaDialect;
import org.jmin.jda.statement.SqlPropertyTable;

/**
 * 基础方言
 * 
 * @author Chris
 * @version 1.0
 */

public abstract class JdaBaseDialect implements JdaDialect{
	
	/**
	 * 获取记录条数的添加View别名
	 */
	public final static String Table_Jmin_Jda_Record_Size_View = "Jmin_Jda_Record_Size_View";
	
	/**
	 * 为动态分页添加的View别名
	 */
	public final static String Table_Jmin_Jda_Record_Page_View = "Jmin_Jda_Record_Page_View";
	
	/**
	 * 是否需要移动到startRow
	 */
	public boolean pageQueryResultNeedMove()throws SQLException{
		return false;
	}
	
	/**
	 * 改造查询SQL获得数目统计SQL
	 */
	public String getRecordCountSQL(String sqlText,SqlPropertyTable table)throws SQLException{
		return new StringBuffer("select count(*) as Record_Size from( ").append(sqlText).append(" )").append(JdaBaseDialect.Table_Jmin_Jda_Record_Size_View).toString();
	}
}