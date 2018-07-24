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
package org.jmin.jda.impl.transaction.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import org.jmin.jda.impl.transaction.Transaction;
import org.jmin.jda.impl.transaction.TransactionException;

/**
 * JDBC事务
 * 
 * @author Chris liao
 */

public class JdbcTransaction implements Transaction {
	
  /**
   * 数据连接
   */
  private Connection connection;
 
  /**
   * 构造函数
   */
  public JdbcTransaction(Connection connection,int isolationLevel)throws SQLException {
  	this.connection = connection;
  	this.connection.setAutoCommit(false);
  	this.connection.setTransactionIsolation(isolationLevel);
  }

  /**
   * 提交事物
   */
  public void commit() throws SQLException {
		if (connection != null) {
			connection.commit();
		 }else {
			 throw new TransactionException("Invalid operation,transaction has been committed or rollback");
		 }
  }
  
  /**
   * 回滚事物
   */
  public void rollback() throws SQLException {
		if (connection != null) {
			connection.rollback();
		}else {
		 throw new TransactionException("Invalid operation,transaction has been committed or rollback");
		}
  }
  
  /**
   * 获得事物的连接
   */
  public Connection getConnection() throws SQLException {
    return this.connection;
  }
  
  /**
   * 使事务死亡
   */
  public void clear()throws SQLException{
    this.connection.setAutoCommit(true);
    this.connection = null;
  }
}
