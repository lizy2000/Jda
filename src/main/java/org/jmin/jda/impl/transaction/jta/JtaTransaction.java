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
package org.jmin.jda.impl.transaction.jta;

import java.sql.Connection;
import java.sql.SQLException;

import javax.transaction.UserTransaction;

import org.jmin.jda.impl.transaction.Transaction;
import org.jmin.jda.impl.transaction.TransactionException;

/**
 * JTA事物
 * 
 * @author Chris
 */
public class JtaTransaction implements Transaction {
	
  /**
   * 连接
   */
  private Connection connection;
  
  /**
   * JTA事物实现
   */
	private UserTransaction userTransaction;
	
  /**
   * 构造函数
   */
  public JtaTransaction(UserTransaction transaction,Connection connection,int isolationLevel) throws SQLException {
  	this.userTransaction = transaction;
  	this.connection = connection;
  	this.connection.setAutoCommit(false);
  	this.connection.setTransactionIsolation(isolationLevel);
  
  	try {
			this.userTransaction.begin();
		} catch (Throwable e) {
			throw new TransactionException("Failed to begin a JTA transaction",e);
		}
  }

  /**
   * 提交事物
   */
  public void commit() throws SQLException {
  	try {
  		if(userTransaction!= null && connection != null) {
				userTransaction.commit();
			  connection.setAutoCommit(true);
			 }else {
				 throw new TransactionException("Invalid operation,transaction has been committed or rollback");
			 }
  	}catch(SQLException e){
  		throw e;
  	}catch(Exception e){
  		throw new TransactionException(e);
		}
  }
  
  /**
   * 回滚事物
   */
  public void rollback() throws SQLException {
  	try {
			if(userTransaction!= null && connection != null) {
				userTransaction.rollback();
			  connection.setAutoCommit(true);
			 }else {
				 throw new TransactionException("Invalid operation,transaction has been committed or rollback");
			 }
  	}catch(SQLException e){
  		throw e;
  	}catch(Exception e){
  		throw new TransactionException(e);
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
  public void clear()throws SQLException,TransactionException{
    this.connection.setAutoCommit(true);
    this.connection = null;
    this.userTransaction = null;
  }
}
