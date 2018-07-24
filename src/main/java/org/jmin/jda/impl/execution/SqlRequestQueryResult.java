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
package org.jmin.jda.impl.execution;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * SQL更新处理结果
 */
public class SqlRequestQueryResult extends SqlRequestResult{
	
	/**
	 * 查询结果集合
	 */
	 private ResultSet resultSet;
	 
	 /**
	  * 查询宣言对象
	  */
	 private PreparedStatement statement;
	 
	/**
	 * 构造函数
	 */
	public SqlRequestQueryResult(SqlRequest request){
		super(request);
	}
	
	/**
	 * 查询结果集合
	 */
	public ResultSet getResultSet() {
		return resultSet;
	}
	
	/**
	 * 查询结果集合
	 */
	void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}
	
	 /**
	  * 查询宣言对象
	  */
	public PreparedStatement getStatement() {
		return statement;
	}
	
	 /**
	  * 查询宣言对象
	  */
	void setStatement(PreparedStatement statement) {
		this.statement = statement;
	}

}
