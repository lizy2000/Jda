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
package org.jmin.jda.impl.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.jmin.jda.UserTransactionInfo;
import org.jmin.jda.impl.transaction.jdbc.JdbcTransaction;
import org.jmin.jda.impl.transaction.jta.JtaTransaction;

/**
 * 事务管理接口
 * 
 * @author Chris Liao
 */
public class TransactionManager {
	
	/**
	 * 是否为JTA事务
	 */
	private UserTransactionInfo jtaTransactionInfo;
	
	/**
	 * 构造方法
	 */
	public TransactionManager(UserTransactionInfo jtaTransactionInfo){
		this.jtaTransactionInfo = jtaTransactionInfo;
	}

	/**
	 * 开始事务
	 */
	public Transaction begin(Connection con, int isolation)throws SQLException,TransactionException{
		if(jtaTransactionInfo !=null){
			con.setAutoCommit(false);
			UserTransaction userTransaction = this.getUserTranscation(jtaTransactionInfo);
			return new JtaTransaction(userTransaction,con,isolation);
		}else{
			con.setAutoCommit(false);
			return new JdbcTransaction(con,isolation);
		}
	}
	
	/**
	 * 获得JTA Trasaction
	 */
	private UserTransaction getUserTranscation(UserTransactionInfo info)throws SQLException{
		try {
			if(info!= null){
	      Properties prop = new Properties();
	      prop.put(Context.PROVIDER_URL, info.getProvider());
	      prop.put(Context.INITIAL_CONTEXT_FACTORY, info.getFactory());
	      prop.put(Context.SECURITY_PRINCIPAL, info.getPrincipal());
	      prop.put(Context.SECURITY_CREDENTIALS, info.getCredentials());
	      InitialContext ctx = new InitialContext(prop);
	      return (UserTransaction) ctx.lookup(info.getJndi());
			}else {
				return null;
			}
    } catch (NamingException e) {
      throw new SQLException("Failedl to lookup JTA transaction,caused by "+ e);
    }
	}
}
